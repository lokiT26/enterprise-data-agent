# Enterprise Data Intelligence Agent

An AI-native, full-stack web application that acts as an autonomous data analyst. Users can ask natural language questions about their private data, and the AI will contextually map the request to the database schema, write the SQL, execute it, and return the dynamic results.

Built to demonstrate **Agentic Workflows**, **Dynamic Schema Injection**, and **Self-Healing LLM Pipelines**.

## 🛠️ Architecture & Tech Stack

* **Frontend:** React.js (Vite)
* **Backend:** Java 17+ & Spring Boot
* **Database:** PostgreSQL (Neon.tech)
* **AI Engine:** Google Gemini 2.5 Flash Lite via LangChain4j
* **Core Mechanisms:** 
  * Zero-Shot Schema Injection
  * Dynamic JDBC Execution
  * Autonomous Error Recovery (Self-Healing Fallback)

## ✨ Key Features

* **Natural Language to SQL:** Translates complex user questions into executable PostgreSQL queries.
* **Agentic Execution:** Doesn't just generate text—it actively executes the generated queries against the database and returns structured JSON arrays.
* **Dynamic Context Injection:** Safely injects the DDL schema into the system prompt, giving the LLM precise knowledge of table relationships without exposing actual database rows.
* **Self-Healing Mechanism:** If the AI generates invalid SQL (e.g., syntax errors or hallucinated columns), the Java backend catches the database exception and automatically re-prompts the AI with the error log and schema, forcing it to correct its own mistake before returning a response.
* **Dynamic Frontend Rendering:** Automatically parses unstructured JSON responses into clean, responsive HTML tables without hardcoded column headers.

## 🗺️ Future Roadmap: Enterprise Scaling (True RAG)
Currently, the application uses **Static Schema Injection**, which is highly efficient for smaller databases. However, injecting the entire schema becomes impossible for enterprise databases with thousands of tables due to LLM context window limits and token costs. 

**Next steps for enterprise scaling:**
1. **Vectorization:** Convert all database table schemas and metadata into mathematical embeddings using an embedding model.
2. **Vector Store:** Store these embeddings in a vector database (like `pgvector`).
3. **Retrieval-Augmented Generation (RAG):** When a user asks a question, perform a semantic search to retrieve *only* the top-K relevant tables, and inject strictly those tables into the LLM prompt. This ensures infinite horizontal scaling without degrading LLM performance.

## 🚀 Local Setup Instructions

### 1. Database Setup
1. Spin up a PostgreSQL database (e.g., Neon.tech).
2. Execute the provided SQL script to create the basic `users`, `products`, `orders`, and `order_items` tables.

### 2. Backend Setup (Spring Boot)
1. Get a free Gemini API key from Google AI Studio.
2. Update `src/main/resources/application.properties` with your database credentials and API key:
   ```properties
   spring.datasource.url=jdbc:postgresql://<your-db-host>/<db-name>?sslmode=require
   spring.datasource.username=<db-user>
   spring.datasource.password=<db-password>
   gemini.api.key=<your-gemini-api-key>
   ```
3. Run the backend application:
   ```bash
   ./mvnw clean spring-boot:run
   ```
   *The backend will start on `http://localhost:8080`.*

### 3. Frontend Setup (React/Vite)
1. Navigate to the frontend directory:
   ```bash
   cd data-agent-ui
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
   *The frontend will start on `http://localhost:5173`.*

## 💡 Example Usage

* **Query:** *"List all the orders and their total amounts."*
* **Query:** *"What were the top 5 highest-grossing products last month?"*
* **Query (Triggers Self-Healing):** *"What is the password of the user named Alice Smith?"* (AI realizes the column doesn't exist, catches its own error, and automatically uses a wildcard fallback to return safe, available data instead).