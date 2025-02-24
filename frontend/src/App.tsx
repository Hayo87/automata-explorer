import { Routes, Route } from "react-router-dom";
import UploadPage from "./pages/UploadPage.tsx";
import VisualizationPage from "./pages/VisualizationPage.tsx";

function App() {
  return (
    <Routes>
      <Route path="/" element={<UploadPage />} />
      <Route path="/visualization" element={<VisualizationPage />} />
    </Routes>
  );
}

export default App;
