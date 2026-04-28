package com.loki.dataagent;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class QueryController {

    private final ChatLanguageModel aiModel;
    private final JdbcTemplate jdbcTemplate;

    public QueryController(ChatLanguageModel aiModel, JdbcTemplate jdbcTemplate) {
        this.aiModel = aiModel;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/query")
    public Map<String, Object> handleUserQuery(@RequestBody Map<String, String> request) {
        String userQuestion = request.get("question");

        String databaseSchema = """
                Table: users (user_id INT, name VARCHAR, email VARCHAR, signup_date DATE)
                Table: products (product_id INT, name VARCHAR, category VARCHAR, price DECIMAL)
                Table: orders (order_id INT, user_id INT, order_date DATE, total_amount DECIMAL)
                Table: order_items (order_item_id INT, order_id INT, product_id INT, quantity INT)
                """;

        String prompt = String.format("""
                You are a strict data analyst AI. 
                Below is the PostgreSQL database schema:
                %s
                
                The user asked: "%s"
                
                Write a valid PostgreSQL query to answer this question.
                Return ONLY the raw SQL query. Do not include markdown, backticks, or any explanations.
                """, databaseSchema, userQuestion);

        String generatedSql = aiModel.generate(prompt);
        String cleanSql = cleanSql(generatedSql);

        Map<String, Object> response = new HashMap<>();
        response.put("question", userQuestion);

        // ATTEMPT 1: Execute the AI's first guess
        try {
            List<Map<String, Object>> queryResults = jdbcTemplate.queryForList(cleanSql);
            response.put("generated_sql", cleanSql);
            response.put("data", queryResults);
            response.put("status", "success");
            response.put("retry_used", false);
            
        } catch (Exception e) {
            System.out.println("⚠️ SQL Error Encountered! Initiating Self-Healing...");
            
            // ATTEMPT 2: THE SELF-HEALING AGENT
            String errorPrompt = String.format("""
                    You are a strict data analyst AI.
                    Here is the PostgreSQL database schema again:
                    %s
                    
                    The user originally asked: "%s"
                    
                    You previously wrote this SQL query: %s
                    
                    It failed in PostgreSQL with this error: %s
                    
                    Fix the query so it executes successfully. 
                    If the user asked for a column that DOES NOT EXIST in the schema, change the query to select the closest relevant columns, or just use the SQL wildcard * (without quotes) to select all available columns from the relevant table.
                    Return ONLY the corrected raw SQL query without markdown or explanations.
                    """, databaseSchema, userQuestion, cleanSql, e.getMessage());

            String healedSql = cleanSql(aiModel.generate(errorPrompt));
            
            try {
                List<Map<String, Object>> healedResults = jdbcTemplate.queryForList(healedSql);
                response.put("generated_sql", healedSql);
                response.put("data", healedResults);
                response.put("status", "success");
                response.put("retry_used", true); // Let the frontend know the AI fixed itself!
                System.out.println("✅ Self-Healing Successful!");
            } catch (Exception finalError) {
                response.put("status", "error");
                response.put("error_message", "AI failed to recover. Final error: " + finalError.getMessage());
            }
        }

        return response;
    }

    // Helper method to keep code clean
    private String cleanSql(String rawAiOutput) {
        return rawAiOutput.replaceAll("```sql", "")
                          .replaceAll("```", "")
                          .replaceAll(";", "") // Sometimes trailing semicolons cause JDBC issues
                          .trim();
    }
}