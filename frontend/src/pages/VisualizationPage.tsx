import React from "react";
import D3Visualization from "../components/D3Visualization";
import "../index.css"; // Import global styles

const VisualizationPage: React.FC = () => {
  return (
    <div className="container">
      <img src="/logo.svg" alt="Logo" className="logo" />
      <h1 className="h1">Visualization</h1>
      <D3Visualization />
    </div>
  );
};

export default VisualizationPage;
