import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import DragAndDrop from "../components/DragAndDrop";
import "../index.css"; // Import global styles

const UploadPage: React.FC = () => {
  const [file1, setFile1] = useState<File | null>(null);
  const [file2, setFile2] = useState<File | null>(null);
  const navigate = useNavigate();

  const handleVisualize = () => {
    if (file1 && file2) {
      navigate("/visualization");
    }
  };

  return (
    <div className="container">
      <img src="/logo.svg" alt="Logo" className="logo" />
      <h1 className="h1">Upload Your Finite State Machines</h1>

      <div className="upload-container">
        <DragAndDrop setFile={setFile1} label="File1" />
        <DragAndDrop setFile={setFile2} label="File2" />
      </div>

      {file1 && file2 && (
        <button onClick={handleVisualize} className="button">
          Continue
        </button>
      )}
    </div>
  );
};

export default UploadPage;
