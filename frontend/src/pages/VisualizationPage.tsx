import React, { useEffect, useState, useRef, useContext} from 'react';
import { useNavigate, useLocation, useParams  } from "react-router-dom";
import { ModalContext } from '../App';
import CytoscapeVisualization, { CytoscapeVisualizationRef } from "../components/CytoscapeCanvas";
import { useSession} from '../hooks/useSession';
import AboutContent from '../components/AboutContent';
import ActionModal from '../components/ActionContent';
import BuildInfo from '../components/BuildContent';
import { ProcessAction, ProcessOption} from '../api/RequestResponse';

/**
 * @file VisualizationPage.tsx
 * 
 * Page component that allows users to explore the difference machine visualization. 
 */

const VisualizationPage: React.FC = () => {
  // Pull sessionId from URL parameter
  const { sessionId} = useParams<{ sessionId: string }>();
  
  // Get the state from navigate parameters
  const { reference, subject, options } = (useLocation().state ?? {}) as {
    reference: string;
    subject: string;
    options: ProcessOption[];
  };
  
  // Pull values from the useSession hook
  const { data, buildSession, loading, terminateSession} = useSession();

  // Route 
  const navigate = useNavigate()

  // Create a mutable reference to the visualization component
  const cyVizRef = useRef<CytoscapeVisualizationRef>(null);

  // Get modal functions
  const { openModal, closeModal } = useContext(ModalContext);

  // States
  const [currentLayout, setCurrentLayout] = useState("dagre");
  const [activeActions, setActiveActions] = useState<ProcessAction[]>([]);

  const [isCollapsed, setIsCollapsed] = useState(false);
  const [loopsHidden, setLoopsHidden] = useState(false);
  const [refHidden, setRefHidden] = useState(false);
  const [subjHidden, setSubjHidden] = useState(false);
  const [helperActive, setHelperActive] = useState(false);
   

  const waitForVizRef = async (): Promise<CytoscapeVisualizationRef> => {
    return new Promise((resolve) => {
      const check = () => {
        if (cyVizRef.current) {
          resolve(cyVizRef.current);
        } else {
          setTimeout(check, 50);
        }
      };
      check();
    });
  };

  const openActionModal = () => {
    if (!options) return; 
    openModal(
      <ActionModal
        appliedActions={activeActions}
        options={options}
        onProcess={async (updatedActions) => {
          setActiveActions(updatedActions); 
          closeModal();
          await buildSession(sessionId!, updatedActions);
        }}
      />
    );
  };

  const handleCollapse = () => {
    if (isCollapsed) {
      cyVizRef.current?.unCollapseEdges();
    } else {
      if(helperActive){
        handleHelper()
      }
      cyVizRef.current?.collapseEdges();
    }
    setIsCollapsed(prev => !prev);
    if (loopsHidden){
      cyVizRef.current?.hideLoops();
    } else{
      cyVizRef.current?.unHideLoops();
    }
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

  const handleHelper = () => {
    if(isCollapsed) {
      handleCollapse()
    }
    cyVizRef.current?.toggleHelper();
    setHelperActive(prev => !prev);
  };
    
  const handlePNGExport = () => {
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

  const handlePDFExport = async () => {
    if (!cyVizRef.current) return;
    const restore = isCollapsed;
    if (restore) {
      cyVizRef.current.unCollapseEdges();
    }
    cyVizRef.current.exportPDF(reference, subject);

    if (restore) {
      cyVizRef.current.collapseEdges();
    }
  };

  const handleExit = async () => {
    if (!cyVizRef.current) return;
    cyVizRef.current.clearVisualHelpers();

    if(sessionId){
      await terminateSession(sessionId);
    }
    navigate("/");
  };

  // Trigger build including Modals
  useEffect(() => {
    if (sessionId) {
      openModal("Loading visualization...", false);

      buildSession(sessionId, activeActions).then(async () => {
        const viz = await waitForVizRef();
        closeModal();
        openModal(<BuildInfo reference={reference} subject={subject} stats={viz.getStats()}/>);
    })}
  }, [sessionId]);

  // Update activeActions and filterbuttons after build response 
  useEffect(() => {
    if (data && data.filters) {
      setActiveActions(data.filters);
    }
    // reset button status
    setIsCollapsed(false);
    setLoopsHidden(false);
    setRefHidden(false);
    setSubjHidden(false);
    setHelperActive(false);
  }, [data]);

  // Determine what to render bases on loading state and available data. 
  let content: React.ReactNode;
  if(loading) {
    content = <p style={{ textAlign: "center" }} />;
  } else if (data) {
    content = (<CytoscapeVisualization ref={cyVizRef} data={data} layout={currentLayout} openModal={openModal} />
    );
  } else {
    content = <p style={{ textAlign: "center" }}>No data available</p>
  }

  return (
    <div className="page-container">
      <img src="/logo_nofill.svg" alt="Logo" className="small-logo" />
      
      <div className="content-container">
        <main className="graph-area">
          {content}
        </main>

        <aside className="sidebar">     
          {/* Layout Section */}
          <p className="sidebar-label">Layout</p>
          <button className={`button button--sidebar ${currentLayout === "dagre" ? "active" : ""}`} title="Dagre" onClick={() => setCurrentLayout("dagre")}>
            <span className="material-icons">swap_horiz</span>
          </button> 
          <button className={`button button--sidebar ${currentLayout === "avsdf" ? "active" : ""}`} title="Circular" onClick={() => setCurrentLayout("avsdf")}>
            <span className="material-icons">radio_button_unchecked</span>
          </button>
          <button className={`button button--sidebar ${currentLayout === "grid" ? "active" : ""}`} title="Grid" onClick={() => setCurrentLayout("grid")}>
            <span className="material-icons">grid_view</span>
          </button>
          <button className={`button button--sidebar ${currentLayout === "breadthfirst" ? "active" : ""}`} title="Breadthfirst" onClick={() => setCurrentLayout("breadthfirst")}>
            <span className="material-icons">park</span>
          </button>
          <button className={`button button--sidebar ${currentLayout === "cose-bilkent" ? "active" : ""}`} title="Elk Layered" onClick={() => setCurrentLayout("elk")}>
            <span className="material-icons">layers</span>
          </button>

          {/* Filter Section */}
          <p className="sidebar-label">Filter</p>
          <button className={`button button--sidebar ${isCollapsed ? "active" : ""}`} title="Toggle edge collapse " onClick={handleCollapse}>
            <span className="material-icons"> {isCollapsed ? "unfold_more" : "unfold_less"} </span>
          </button>
          <button className={`button button--sidebar ${loopsHidden ? "active" : ""}`} title="Show/hide loops" onClick={handleLoop}>
            <span className="material-icons"> {loopsHidden ? "sync_disabled" : "sync"} </span>
          </button>
          <button className={`button button--sidebar ${!refHidden ? "" : "active"}`} title="Show/hide reference" onClick={handleRef} >
            {refHidden ? <s>REF</s> : "REF"}
          </button>
          <button className={`button button--sidebar ${!subjHidden ? "" : "active"}`} title="Show/hide subject" onClick={handleSubj} >
            {subjHidden ? <s>SUB</s> : "SUB"}
          </button>
          <button className={`button button--sidebar ${helperActive ? "active" : ""}`} title="Toggle clustering & causes" onClick={handleHelper}>
            <span className="material-icons">explore</span>
          </button>

          {/* Modify Section */}
          <p className="sidebar-label">Modify</p>
          <button className="button button--sidebar" title="Open actions" onClick={openActionModal}>
            <span className="material-icons">edit</span>
          </button>

          {/* Export Section */}
          <p className="sidebar-label">Export</p>
          <button className="button button--sidebar" title="Export as PNG image" onClick={handlePNGExport}>
            <span className="material-icons">image</span>
          </button>
          <button className="button button--sidebar" title="Export as PDF report" onClick = {handlePDFExport}>
            <span className="material-icons">picture_as_pdf</span>
          </button>

          {/* About and Exit section */}
          <div style={{ marginTop: 'auto' }}>
          <p className="sidebar-label">About</p>
            <button className="button button--sidebar" title="About this app" onClick={() => openModal(<AboutContent/>)}>
              <span className="material-icons">info</span>
            </button>
            <p className="sidebar-label">Exit</p>
            <button className="button button--sidebar" title="Exit visualization" onClick={handleExit}>
              <span className="material-icons">exit_to_app</span>
            </button>
          </div>

          </aside>
      </div>

      {/* Input filename section */}
      <div className="bottom-left-info">
      <p> Reference: <span className="reference-file-name">{reference}</span> </p>
      <p> Subject: <span className="subject-file-name">{subject}</span> </p>
      </div>
    </div>
  );
}

export default VisualizationPage;