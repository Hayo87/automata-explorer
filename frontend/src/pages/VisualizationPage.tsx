import React, { useEffect, useState, useRef } from 'react';
import { useParams } from 'react-router-dom';
import CytoscapeVisualization, { CytoscapeVisualizationRef } from "../components/CytoscapeVisualization";
import { useSession} from '../hooks/useSession';
import { useNavigate, useLocation  } from "react-router-dom";
import  InfoModal from '../components/InfoModal';


import '../index.css';

const VisualizationPage: React.FC = () => {
  const { sessionId} = useParams<{ sessionId: string }>();
  const location = useLocation();
  const { reference, subject } = location.state as { reference: string; subject: string };
  const cyVizRef = useRef<CytoscapeVisualizationRef>(null);

  const { data, loadSessionData, loading } = useSession();
  const [currentLayout, setCurrentLayout] = useState("preset");

  const navigate = useNavigate();
  const { closeSession } = useSession();

  // Modal state for InfoModal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalNodeData, setModalNodeData] = useState<any>(null);

  // openModal and closeModal functions
  const openModal = (nodeData: any) => {
    setModalNodeData(nodeData);
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setModalNodeData(null);
  };

  const handleExport = () => {
    if (!cyVizRef.current) return;
    const pngDataUrl = cyVizRef.current.exportPNG();

    // Create a temporary link 
    const link = document.createElement("a");
    link.href = pngDataUrl;
    // Set filename and download
    link.download = `DiffMachine_${reference}_${subject}.png`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };


  const handleExit = async () => {
    try {
      if(sessionId){
        await closeSession(sessionId);
      }
    } catch (error) {
      console.error("Failed to close session:", error);
    } finally {
      navigate("/");
    }
  };

  useEffect(() => {
    if (sessionId) {
      loadSessionData(sessionId);
    }
  }, [sessionId]);

  return (
    <div className="page-container">
      <img src="/logo_nofill.svg" alt="Logo" className="small-logo" style={{ backgroundColor: "transparent" }} />
      
      <div className="content-container">
        <main className="graph-area">
          {loading ? (
            <p style={{ textAlign: "center" }}>Loading visualization...</p>
          ) : data ? (
            <CytoscapeVisualization ref={cyVizRef} data={data} layout={currentLayout} openModal={openModal} />
          ) : (
            <p style={{ textAlign: "center" }}>No data available</p>

          )}
        </main>

        <aside className="sidebar">
          <button className="sidebar-button" title="Exit this visualization" onClick={handleExit}> <span className="material-icons">exit_to_app</span> </button>
          <hr></hr>
          <button className="sidebar-button" title= "Set dot layout" onClick={() => setCurrentLayout("preset")}> 
            <span className="material-icons">more_horiz</span> 
          </button>
          <button className="sidebar-button" title="Set circular layout" onClick={() => setCurrentLayout("avsdf")}>
            <span className="material-icons">radio_button_unchecked</span>
          </button>
          <button className="sidebar-button" title = "Set grid layout" onClick={() => setCurrentLayout("grid")}> 
            <span className="material-icons">grid_view</span> 
          </button>
          <button className="sidebar-button" title = "Set dagre layout" onClick={() => setCurrentLayout("dagre")}> 
            <span className="material-icons">swap_horiz</span> 
          </button>
          <button className="sidebar-button" title = "Set breadthfirst layout" onClick={() => setCurrentLayout("breadthfirst")}> 
            <span className="material-icons">park</span> 
          </button>

          <hr></hr>
          <button className="sidebar-button" title= "Filter"> 
            <span className="material-icons">filter_alt</span> 
          </button>
          <button className="sidebar-button" title= "Filter"> 
            <span className="material-icons">filter_alt</span> 
          </button>
          <button className="sidebar-button" title= "Filter"> 
            <span className="material-icons">filter_alt</span> 
          </button>
          <hr></hr>
          <button className="sidebar-button" title= "Filter"> 
            <span className="material-icons">track_changes</span> 
          </button>
          <hr></hr>
          <button className="sidebar-button" title= "Export as PNG" onClick={handleExport}> 
            <span className="material-icons">image</span> 
          </button>
          <button className="sidebar-button" title= "Export as PDF"> 
            <span className="material-icons">picture_as_pdf</span> 
          </button>
          <hr></hr>
          <button className="sidebar-button" title= "Get input information"> <span className="material-icons">info</span> </button>
        </aside>
      </div>
      <div className="bottom-left-info">
      <p> Reference: <span className="reference-file-name">{reference}</span> </p>
      <p> Subject: <span className="subject-file-name">{subject}</span> </p>
      </div>
      <InfoModal isOpen={isModalOpen} onClose={closeModal} node={modalNodeData} />
    </div>
  );
};

export default VisualizationPage;