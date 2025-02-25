import React, { useState } from "react";
import "../index.css"; 

interface DragAndDropProps {
  setFile: (file: File | null) => void;
  label: string;
}

const DragAndDrop: React.FC<DragAndDropProps> = ({ setFile, label }) => {
  const [dragOver, setDragOver] = useState(false);
  const [uploadedFile, setUploadedFile] = useState<File | null>(null);

  const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setDragOver(true);
  };

  const handleDragLeave = () => {
    setDragOver(false);
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setDragOver(false);
    const file = e.dataTransfer.files[0];
    if (file) {
      setUploadedFile(file);
      setFile(file);
    }
  };

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    if (file) {
      setUploadedFile(file);
      setFile(file);
    }
  };

  return (
    <div className="drag-drop-container">
      <h3 className="drag-drop-header">{label}</h3>
      <div
        className={`drop-zone ${dragOver ? "drag-over" : ""}`}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
      >
        <p className="drop-text">
          {uploadedFile ? uploadedFile.name : "Drag and drop a file here, or click to select one"}
        </p>
        <input type="file" accept=".dot" onChange={handleFileSelect} className="hidden-input" id={label} />
        <label htmlFor={label} className="file-label">
          Select a File
        </label>
      </div>
    </div>
  );
};

export default DragAndDrop;
