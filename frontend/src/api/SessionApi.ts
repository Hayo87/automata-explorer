const API_BASE_URL = import.meta.env.VITE_API_BASE_URL; 

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
      throw new Error("Failed to create session.");
    }

    const { sessionId } = await response.json();
    return sessionId;
  } catch (error) {
    console.error("Upload error:", error);
    throw error;
  }
};
