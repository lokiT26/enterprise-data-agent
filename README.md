# Enterprise Data Intelligence Agent

An AI-native, full-stack web application that acts as an autonomous data analyst. Users can ask natural language questions about their private data, and the AI will contextually map the request to the database schema, write the SQL, execute it, and return the dynamic results.

Built to demonstrate **Agentic Workflows**, **RAG (Retrieval-Augmented Generation)**, and **Self-Healing LLM Pipelines**.

## 🛠️ Architecture & Tech Stack

* **Frontend:** React.js (Vite)
* **Backend:** Java 17+ & Spring Boot
* **Database:** PostgreSQL (Neon.tech)
* **AI Engine:** Google Gemini 2.5 Flash Lite via LangChain4j
* **Core Mechanisms:** 
  * Strict System Prompting (Zero-shot SQL generation)
  * Dynamic JDBC Execution
  * Autonomous Error Recovery (Self-Healing Fallback)

## ✨ Key Features

* **Natural Language to SQL:** Translates complex user questions into executable PostgreSQL queries.
* **Agentic Execution:** Doesn't just generate text—it actively executes the generated queries against the database and returns structured JSON arrays.
* **Self-Healing Mechanism:** If the AI generates invalid SQL (e.g., hallucinating a column or syntax error), the Java backend catches the database exception and automatically re-prompts the AI with the error log and schema, forcing it to correct its own mistake before returning a response to the user.
* **Dynamic Frontend Rendering:** Automatically parses unstructured JSON responses into clean, responsive HTML tables without hardcoded column headers.

## 🚀 Local Setup Instructions

### 1. Database Setup
1. Spin up a PostgreSQL database (e.g., Neon.tech).
2. Execute the provided SQL script (see `schema.sql` or create basic `users`, `products`, `orders`, and `order_items` tables).

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
* **Query (Triggers Self-Healing):** *"What is the password of the user named Alice?"* (AI realizes the column doesn't exist, catches its own error, and returns safe available data instead).