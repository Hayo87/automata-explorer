import React from 'react';
import { Routes, Route } from 'react-router-dom';
import UploadPage from './pages/UploadPage';
import VisualizationPage from './pages/VisualizationPage';

const App = () => {
  return (
    <Routes>
      <Route path="/" element={<UploadPage />} />
      <Route path="/visualization/:sessionId" element={<VisualizationPage />} />
    </Routes>
  );
};

export default App;
