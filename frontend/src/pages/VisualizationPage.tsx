import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import CytoscapeVisualization from '../components/CytoscapeVisualization';
import { useSession} from '../hooks/useSession';
import { useNavigate } from "react-router-dom";
import '../index.css';

const VisualizationPage: React.FC = () => {
  const { sessionId } = useParams<{ sessionId: string }>();
  const { data, loadSessionData, loading } = useSession();
  const [currentLayout, setCurrentLayout] = useState("preset");

  const navigate = useNavigate();
  const { closeSession } = useSession();

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
            <p>Loading visualization...</p>
          ) : data ? (
            <CytoscapeVisualization data={data} layout={currentLayout}/>
          ) : (
            <p>No data available</p>
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
          <button className="sidebar-button" title = "Set matrix layout" onClick={() => setCurrentLayout("dagre")}> 
            <span className="material-icons">article</span> 
          </button>

          <hr></hr>
          <button className="sidebar-button" title= "Filter"> 
            <span className="material-icons">filter_alt</span> 
          </button>
          <hr></hr>
          <button className="sidebar-button" title= "Get input information"> <span className="material-icons">info</span> </button>
        </aside>
      </div>
    </div>
  );
};

export default VisualizationPage;