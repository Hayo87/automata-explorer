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
import BuildInfo from '../components/BuildInfo';


const VisualizationPage: React.FC = () => {
  const { sessionId} = useParams<{ sessionId: string }>();
  const location = useLocation();
  const { reference, subject } = location.state as { reference: string; subject: string };
  const cyVizRef = useRef<CytoscapeVisualizationRef>(null);

  const navigate = useNavigate();
  const { terminateSession } = useSession();
  const { data, buildSession, loading } = useSession();

  // States
  const [currentLayout, setCurrentLayout] = useState("dagre");
  const [activeFilters, setActiveFilters] = useState<Filter[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalContent, setModalContent] = useState<any>(null);
  const [showCloseButton, setShowCloseButton] = useState(true);

  const [isCollapsed, setIsCollapsed] = useState(false);
  const [loopsHidden, setLoopsHidden] = useState(false);
  const [refHidden, setRefHidden] = useState(false);
  const [subjHidden, setSubjHidden] = useState(false);
   

  // openModal and closeModal functions
  const openModal = (modalContent: any, showCloseButton: boolean = true) => {
    setModalContent(modalContent);
    setIsModalOpen(true);
    setShowCloseButton(showCloseButton);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setModalContent(null);
  };
;
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

  const handleCollapse = () => {
    if (isCollapsed) {
      cyVizRef.current?.unCollapseEdges();
    } else {
      cyVizRef.current?.collapseEdges();
    }
    setIsCollapsed(prev => !prev);
  };

  const handleLoop = () => {
    if (loopsHidden) {
      cyVizRef.current?.unHideLoops();
    } else {
      cyVizRef.current?.hideLoops();
    }
    setLoopsHidden(prev => !prev);
  };

  const handleRef = () => {
    const newState = !refHidden;
    setRefHidden(newState);
    if (newState) {
      cyVizRef.current?.hideRef();   
    } else {
      cyVizRef.current?.showRef(); 
    }
  };
  
  const handleSubj = () => {
    const newState = !subjHidden;
    setSubjHidden(newState);
    if (newState) {
      cyVizRef.current?.hideSub();   
    } else {
      cyVizRef.current?.showSub(); 
    }
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

  // Trigger build
  useEffect(() => {
    if (sessionId) {
      openModal("Loading visualization...", false);

      buildSession(sessionId, activeFilters).then(() => {
        if(cyVizRef.current){
        openModal(<BuildInfo reference={reference} subject={subject} stats={cyVizRef.current.getStats()}/>);
    }})
      .catch((error) => {
        openModal(`Build failed: ${error.message}`);
    })}
  }, [sessionId]);

  // Update activeFilters after build response 
  useEffect(() => {
    if (data && data.filters) {
      setActiveFilters(data.filters);
    }
  }, [data]);

  return (
    <div className="page-container">
      <img src="/logo_nofill.svg" alt="Logo" className="small-logo" />
      
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
          <p className="sidebar-label">Layout</p>
          <button className={`sidebar-button ${currentLayout === "dagre" ? "active" : ""}`} title="Dagre" onClick={() => setCurrentLayout("dagre")}>
            <span className="material-icons">swap_horiz</span>
          </button> 
          <button className={`sidebar-button ${currentLayout === "avsdf" ? "active" : ""}`} title="Circular" onClick={() => setCurrentLayout("avsdf")}>
            <span className="material-icons">radio_button_unchecked</span>
          </button>
          <button className={`sidebar-button ${currentLayout === "grid" ? "active" : ""}`} title="Grid" onClick={() => setCurrentLayout("grid")}>
            <span className="material-icons">grid_view</span>
          </button>
          <button className={`sidebar-button ${currentLayout === "breadthfirst" ? "active" : ""}`} title="Breadthfirst" onClick={() => setCurrentLayout("breadthfirst")}>
            <span className="material-icons">park</span>
          </button>
          <button className={`sidebar-button ${currentLayout === "cose-bilkent" ? "active" : ""}`} title="coseBilkent" onClick={() => setCurrentLayout("cose-bilkent")}>
            <span className="material-icons">park</span>
          </button>

          {/* Filter Section */}
          <p className="sidebar-label">Filter</p>
          <button className={`sidebar-button ${isCollapsed ? "active" : ""}`} title="Toggle collapse filter" onClick={handleCollapse}>
            <span className="material-icons"> {isCollapsed ? "unfold_more" : "unfold_less"} </span>
          </button>
          <button className={`sidebar-button ${loopsHidden ? "active" : ""}`} title="Toggle Loop filter" onClick={handleLoop}>
            <span className="material-icons"> {loopsHidden ? "sync_disabled" : "sync"} </span>
          </button>
          <button className={`sidebar-button ${!refHidden ? "" : "active"}`} title="Reference on/off" onClick={handleRef} >
            {refHidden ? <s>REF</s> : "REF"}
          </button>
          <button className={`sidebar-button ${!subjHidden ? "" : "active"}`} title="Subject on/off" onClick={handleSubj} >
            {subjHidden ? <s>SUB</s> : "SUB"}
          </button>

          {/* Modify Section */}
          <p className="sidebar-label">Modify</p>
          <button className="sidebar-button" title="Open filters" onClick={openFilterModal}>
            <span className="material-icons">edit</span>
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
      <InfoModal isOpen={isModalOpen} onClose={closeModal} content={modalContent} showCloseButton={showCloseButton} />
    </div>
  );
}

export default VisualizationPage;