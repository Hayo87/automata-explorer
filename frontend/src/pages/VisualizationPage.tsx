import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import CytoscapeVisualization from '../components/CytoscapeVisualization';
import { useSession } from '../hooks/useSession';
import '../index.css';

const VisualizationPage: React.FC = () => {
  const { sessionId } = useParams<{ sessionId: string }>();
  const { data, loadSessionData, loading } = useSession();
  const [currentLayout, setCurrentLayout] = useState("dot");

  useEffect(() => {
    if (sessionId) {
      loadSessionData(sessionId);
    }
  }, [sessionId]);

  return (
    <div className="page-container">
      <img src="/logo.svg" alt="Logo" className="small-logo" />

      <div className="content-container">
        <main className="graph-area">
          {loading ? (
            <p>Loading visualization...</p>
          ) : data ? (
            <CytoscapeVisualization data={data} />
          ) : (
            <p>No data available</p>
          )}
        </main>

        <aside className="sidebar">
          <button className="sidebar-button"  title="Exit this visualization"> <span className="material-icons">exit_to_app</span> </button>
          <hr></hr>
          <button className="sidebar-button" title= "Set dot layout"> <span className="material-icons">more_horiz</span> </button>
          <button className="sidebar-button" title = "Set circular layout"> <span className="material-icons">radio_button_unchecked</span> </button>
          <button className="sidebar-button" title = "Set grid layout"> <span className="material-icons">grid_view</span> </button>
          <hr></hr>
          <button className="sidebar-button" title= "Filter"> <span className="material-icons">filter_alt</span> </button>
          <hr></hr>
          <button className="sidebar-button" title= "Get input information"> <span className="material-icons">info</span> </button>
        </aside>


      </div>
    </div>
  );
};

export default VisualizationPage;