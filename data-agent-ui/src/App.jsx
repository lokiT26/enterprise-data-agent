import { useState } from 'react'
import './App.css' // We will keep the default CSS file for basic styling

function App() {
  const[question, setQuestion] = useState("");
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState(null);

  const askAi = async (e) => {
    e.preventDefault();
    if (!question) return;

    setLoading(true);
    setResponse(null); // Clear previous results

    try {
      // Calling your Spring Boot Backend!
      const res = await fetch("http://localhost:8080/api/query", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ question })
      });

      const data = await res.json();
      setResponse(data);
    } catch (error) {
      console.error("Error connecting to backend:", error);
      setResponse({ status: "error", error_message: "Could not connect to the backend." });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: "2rem", maxWidth: "800px", margin: "0 auto", fontFamily: "sans-serif" }}>
      <h1>🤖 Enterprise Data Intelligence Agent</h1>
      <p>Ask a question about your database in plain English.</p>

      {/* SEARCH FORM */}
      <form onSubmit={askAi} style={{ display: "flex", gap: "10px", marginBottom: "2rem" }}>
        <input 
          type="text" 
          value={question} 
          onChange={(e) => setQuestion(e.target.value)} 
          placeholder="e.g., List all the orders" 
          style={{ flex: 1, padding: "10px", fontSize: "16px" }}
        />
        <button type="submit" disabled={loading} style={{ padding: "10px 20px", fontSize: "16px" }}>
          {loading ? "Thinking..." : "Ask"}
        </button>
      </form>

      {/* ERROR HANDLING */}
      {response?.status === "error" && (
        <div style={{ color: "red", backgroundColor: "#ffe6e6", padding: "1rem", borderRadius: "5px" }}>
          <strong>Error:</strong> {response.error_message}
        </div>
      )}

      {/* RESULTS DISPLAY */}
      {response?.status === "success" && (
        <div>
          {/* Show the AI's SQL */}
          <div style={{ backgroundColor: "#f4f4f4", padding: "1rem", borderRadius: "5px", marginBottom: "1rem" }}>
            <strong>Generated SQL:</strong>
            <pre style={{ margin: "0.5rem 0 0 0", whiteSpace: "pre-wrap" }}>{response.generated_sql}</pre>
          </div>

          {/* Show the Dynamic Data Table */}
          <h3>Results:</h3>
          {response.data && response.data.length > 0 ? (
            <table border="1" cellPadding="10" style={{ width: "100%", borderCollapse: "collapse", textAlign: "left" }}>
              <thead>
                <tr>
                  {/* Dynamically generate table headers based on the keys of the first row */}
                  {Object.keys(response.data[0]).map((key) => (
                    <th key={key}>{key}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {/* Dynamically generate table rows */}
                {response.data.map((row, index) => (
                  <tr key={index}>
                    {Object.values(row).map((val, i) => (
                      <td key={i}>{val !== null ? val.toString() : "NULL"}</td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <p>No data found for this query.</p>
          )}
        </div>
      )}
    </div>
  )
}

export default App