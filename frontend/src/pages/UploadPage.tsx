import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import DragAndDrop from "../components/DragAndDrop";
import { useSession } from "../hooks/useSession"; 
import "../index.css"; // Import global styles

const UploadPage: React.FC = () => {
  const { startSession, loading } = useSession(); 
  const [file1, setFile1] = useState<File | null>(null);
  const [file2, setFile2] = useState<File | null>(null);
  const navigate = useNavigate();

  const handleContinue = async () => {
    if (!file1 || !file2) return;

    try {
      const sessionId = await startSession(file1, file2);
      if (sessionId) {
        navigate(`/visualization/${sessionId}`);
      }
    } catch (error) {
      console.error("Error starting session:", error);
      alert("Failed to process files.");
    }
  };

  return (
    <div className="container">
      <img src="/logo.svg" alt="Logo" className="logo" />
      <h1 className="h1">Upload Your Finite State Machines</h1>

      <div className="upload-container">
        <DragAndDrop setFile={setFile1} label="File 1" />
        <DragAndDrop setFile={setFile2} label="File 2" />
      </div>

      {file1 && file2 && (
        <button onClick={handleContinue} className="button" disabled={loading}>
          {loading ? "Processing..." : "Continue"}
        </button>
      )}
    </div>
  );
};

export default UploadPage;
