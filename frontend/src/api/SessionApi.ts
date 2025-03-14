const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

console.log(" Using API Base URL in Frontend:", API_BASE_URL); // Debugging

// Function to read a .dot file as plain text
const readDotFileAsText = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = () => reject(new Error("Failed to read .dot file"));
    reader.readAsText(file);
  });
};

export const uploadFiles = async (file1: File, file2: File): Promise<string> => {
  try {
    // Read .dot files as text
    const reference = await readDotFileAsText(file1);
    const subject = await readDotFileAsText(file2);

    // Prepare JSON payload
    const payload = { reference, subject };

    const response = await fetch(`${API_BASE_URL}/session`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      const errorText = await response.text(); // Read error message if any
      throw new Error(`Failed to create session. Status: ${response.status}, Message: ${errorText}`);
    }

    try {
      const jsonResponse = await response.json();
      if (!jsonResponse.sessionId) {
        throw new Error("Invalid response: Missing sessionId");
      }
      return jsonResponse.sessionId;
    } catch (error) {
      throw new Error("Failed to parse JSON response.");
    }
  } catch (error) {
    console.error("Upload error:", error);
    throw error;
  }
};

// Fetch visualization data for a session
export const fetchSessionData = async (sessionId: string): Promise<any> => {
  try {
    const response = await fetch(`${API_BASE_URL}/session/${sessionId}/build`);

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Failed to fetch visualization data. Status: ${response.status}, Message: ${errorText}`);
    }

    return await response.json();
  } catch (error) {
    console.error("Fetch session data error:", error);
    throw error;
  }
};

// Close session
export const closeSession = async (sessionId: string): Promise<void> => {
  const response = await fetch(`${API_BASE_URL}/session/${sessionId}`, {
    method: "DELETE",
  });
  if (!response.ok) {
    throw new Error(`Error: ${response.status} ${response.statusText}`);
  }
};
