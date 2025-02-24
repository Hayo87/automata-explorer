import React, { useEffect, useRef } from 'react';
import cytoscape from 'cytoscape';
import dagre from 'cytoscape-dagre';
cytoscape.use(dagre);

interface Transition {
  from: number;
  to: number;
  label: string;
  diffKind: string;
}

interface State {
  id: number;
  initial: boolean;
  diffKind: string;
}

interface GraphData {
  transitions: Transition[];
  states: State[];
}

interface CytoscapeVisualizationProps {
  data: GraphData;
}

const getColor = (diffKind: string): string => {
  switch (diffKind) {
    case "REMOVED":
      return "#00cc00"; // green
    case "ADDED":
      return "#ff4040"; // red
    default:
      return "#000000"; // black
  }
};

const CytoscapeVisualization: React.FC<CytoscapeVisualizationProps> = ({ data }) => {
  const containerRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (!data || !containerRef.current) return;

    // Convert states to nodes
    const nodes = data.states.map(state => ({
      data: {
        id: state.id.toString(),
        label: `State ${state.id}`,
        color: getColor(state.diffKind)
      }
    }));

    // Convert transitions to edges
    const edges = data.transitions.map((transition, index) => ({
      data: {
        id: `edge${index}`,
        source: transition.from.toString(),
        target: transition.to.toString(),
        label: transition.label,
        color: getColor(transition.diffKind)
      }
    }));

    const elements = [...nodes, ...edges];

    const cy = cytoscape({
      container: containerRef.current,
      elements: elements,
      style: [
        {
          selector: 'node',
          style: {
            'background-color': 'data(color)',
            'label': 'data(label)',
            'text-valign': 'center',
            'color': '#fff',
            'font-size': '12px',
            'width': '40px',
            'height': '40px'
          }
        },
        {
          selector: 'edge',
          style: {
            'width': 2,
            'line-color': 'data(color)',
            'target-arrow-color': 'data(color)',
            'target-arrow-shape': 'triangle',
            'curve-style': 'bezier',
            'label': 'data(label)',
            'font-size': '10px',
            'text-rotation': 'autorotate',
            'text-margin-y': -10
          }
        }
      ],
      layout: {
        name: 'dagre',
        padding: 30
      }
    });

    return () => {
      cy.destroy();
    };
  }, [data]);

  return <div ref={containerRef} className="cytoscape-container"></div>;
  
};

export default CytoscapeVisualization;
