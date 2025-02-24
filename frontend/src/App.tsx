import { Routes, Route, BrowserRouter } from "react-router-dom";
import UploadPage from "./pages/UploadPage.tsx";
import VisualizationPage from "./pages/VisualizationPage.tsx";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<UploadPage />} />
        {/* Add a route with the session token as a parameter */}
        <Route path="/visualization/:sessionId" element={<VisualizationPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
