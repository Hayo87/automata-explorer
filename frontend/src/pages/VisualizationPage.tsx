import React, { useEffect, useState, useRef } from 'react';
import { useParams } from 'react-router-dom';
import CytoscapeVisualization, { CytoscapeVisualizationRef } from "../components/CytoscapeVisualization";
import { useSession} from '../hooks/useSession';
import { useNavigate, useLocation  } from "react-router-dom";
import  InfoModal from '../components/InfoModal';
import AboutContent from '../components/AboutContent';
import FilterInfo from '../components/FilterInfo';
import '../index.css';
import { Filter } from '../types/BuildResponse';


const VisualizationPage: React.FC = () => {
  const { sessionId} = useParams<{ sessionId: string }>();
  const location = useLocation();
  const { reference, subject } = location.state as { reference: string; subject: string };
  const cyVizRef = useRef<CytoscapeVisualizationRef>(null);
  const [activeFilters, setActiveFilters] = useState<Filter[]>([]);

  const { data, buildSession, loading } = useSession();
  const [currentLayout, setCurrentLayout] = useState("preset");

  const navigate = useNavigate();
  const { terminateSession } = useSession();

  // Modal state for InfoModal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalContent, setModalContent] = useState<any>(null);

  // openModal and closeModal functions
  const openModal = (modalContent: any) => {
    setModalContent(modalContent);
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setModalContent(null);
  };

  const openFilterModal = () => {
    openModal(
      <FilterInfo
        initialFilters={activeFilters}
        onProcess={async (updatedFilters) => {
          setActiveFilters(updatedFilters); 
          closeModal();
          await buildSession(sessionId!, updatedFilters);
        }}
      />
    );
  };

  const handleGroupSynonym = () => {
    //dummy method
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
        await terminateSession(sessionId);
      }
    } catch (error) {
      console.error("Failed to close session:", error);
    } finally {
      navigate("/");
    }
  };

  // When sessionId is available, trigger a build with the current activeFilters.
  useEffect(() => {
    if (sessionId) {
      buildSession(sessionId, activeFilters);
    }
  }, [sessionId]);

  // When a build response comes back, update activeFilters from data.filters.
  useEffect(() => {
    if (data && data.filters) {
      setActiveFilters(data.filters);
    }
  }, [data]);

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
        
          {/* Layout Section */}
          <p className="sidebar-label">Layouts</p>
          <button className={`sidebar-button ${currentLayout === "preset" ? "active" : ""}`} title="Dot" onClick={() => setCurrentLayout("preset")}>
            <span className="material-icons">more_horiz</span>
          </button>
          <button className={`sidebar-button ${currentLayout === "avsdf" ? "active" : ""}`} title="Circular" onClick={() => setCurrentLayout("avsdf")}>
            <span className="material-icons">radio_button_unchecked</span>
          </button>
          <button className={`sidebar-button ${currentLayout === "grid" ? "active" : ""}`} title="Grid" onClick={() => setCurrentLayout("grid")}>
            <span className="material-icons">grid_view</span>
          </button>
          <button className={`sidebar-button ${currentLayout === "dagre" ? "active" : ""}`} title="Dagre" onClick={() => setCurrentLayout("dagre")}>
            <span className="material-icons">swap_horiz</span>
          </button>
          <button className={`sidebar-button ${currentLayout === "breadthfirst" ? "active" : ""}`} title="Breadthfirst" onClick={() => setCurrentLayout("breadthfirst")}>
            <span className="material-icons">park</span>
          </button>
          <button className={`sidebar-button ${currentLayout === "cose-bilkent" ? "active" : ""}`} title="coseBilkent" onClick={() => setCurrentLayout("cose-bilkent")}>
            <span className="material-icons">park</span>
          </button>


          {/* Tools Section */}
          <p className="sidebar-label">Tools</p>
          <button className="sidebar-button" title="Open filters" onClick={openFilterModal}>
            <span className="material-icons">filter_alt</span>
          </button>
          <button className="sidebar-button" title="Create synonym" onClick={handleGroupSynonym} >
            <span className="material-icons">track_changes</span>
          </button>


          {/* Export Section */}
          <p className="sidebar-label">Export</p>
          <button className="sidebar-button" title="Export as PNG" onClick={handleExport}>
            <span className="material-icons">image</span>
          </button>
          <button className="sidebar-button" title="Export as PDF">
            <span className="material-icons">picture_as_pdf</span>
          </button>

          {/* About and Exit section */}
          <div style={{ marginTop: 'auto' }}>
          <p className="sidebar-label">About</p>
            <button className="sidebar-button" title="About this app" onClick={() => openModal(<AboutContent/>)}>
              <span className="material-icons">info</span>
            </button>
            <p className="sidebar-label">Exit</p>
            <button className="sidebar-button" title="Exit this visualization" onClick={handleExit}>
              <span className="material-icons">exit_to_app</span>
            </button>
          </div>

          </aside>

      </div>
      <div className="bottom-left-info">
      <p> Reference: <span className="reference-file-name">{reference}</span> </p>
      <p> Subject: <span className="subject-file-name">{subject}</span> </p>
      </div>
      <InfoModal isOpen={isModalOpen} onClose={closeModal} content={modalContent} />
    </div>
  );
}



export default VisualizationPage;