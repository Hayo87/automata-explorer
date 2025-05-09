import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import DragAndDrop from "../components/DragAndDrop";
import { useSession } from "../hooks/useSession"; 
import "../index.css";

const UploadPage: React.FC = () => {
  const { startSession, loading } = useSession(); 
  const [file1, setFile1] = useState<File | null>(null);
  const [file2, setFile2] = useState<File | null>(null);
  const [parseAsMealy, setParseAsMealy] = useState(false);
  const navigate = useNavigate();

  const handleContinue = async () => {
    if (!file1 || !file2) return;

    const selectedType: "STRING" | "MEALY" = parseAsMealy ? "MEALY" : "STRING";

    try {
      const {sessionId, processingOptions } = await startSession(file1, file2,selectedType);
      if (sessionId) {
        navigate(`/visualization/${sessionId}`, {
          state: {
            reference: file1?.name,
            subject: file2?.name,
            options: processingOptions,
          }
        });
      }
    } catch (error) {
    }
  };

  return (
    <div className="upload-page-container">
      <img src="/logo.svg" alt="Logo" className="logo" />
      <h1 className="h1">Upload your Finite State Machines</h1>

      <div className="upload-container">
        <DragAndDrop setFile={setFile1} label="Reference" />
        <DragAndDrop setFile={setFile2} label="Subject" />
      </div>
      <label className="toggle-label">
        <span><strong>Process as Mealy</strong></span>
        <input
          type="checkbox"
          checked={parseAsMealy}
          onChange={(e) => setParseAsMealy(e.target.checked)}
          className="toggle-input"
        />
        <span className="toggle-slider" />
      </label>

      {file1 && file2 && (
        <button onClick={handleContinue} className="button" disabled={loading}>
          {loading ? "Processing..." : "Continue"}
        </button>
      )}
    </div>
  );
};

export default UploadPage;
