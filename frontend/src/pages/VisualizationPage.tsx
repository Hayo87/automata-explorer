import React, { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import CytoscapeVisualization from '../components/CytoscapeVisualization';
import { useSession } from '../hooks/useSession';
import '../index.css';

const VisualizationPage: React.FC = () => {
  const { sessionId } = useParams<{ sessionId: string }>();
  const { data, loadSessionData, loading } = useSession();

  useEffect(() => {
    if (sessionId) {
      loadSessionData(sessionId);
    }
  }, [sessionId]);

  return (
    <div className="vis-container">
      <img src="/logo.svg" alt="Logo" className="small-logo" />
      {loading ? (
        <p>Loading visualization...</p>
      ) : data ? (
        <CytoscapeVisualization data={data} />
      ) : (
        <p>No data available</p>
      )}
    </div>
  );
};

export default VisualizationPage;
