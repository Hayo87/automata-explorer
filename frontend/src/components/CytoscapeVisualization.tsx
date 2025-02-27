import React, { useEffect, useRef } from "react";
import cytoscape from "cytoscape";
import dagre from "cytoscape-dagre";
import useTransformGraph from "../hooks/useTransformGraph";
import { GraphData } from "../hooks/useTransformGraph";

cytoscape.use(dagre);

interface CytoscapeVisualizationProps {
  data: GraphData;
}

const CytoscapeVisualization: React.FC<CytoscapeVisualizationProps> = ({ data }) => {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const transformedData = useTransformGraph(data);

  // Convert states to nodes
  const nodes = transformedData.nodes.map(node => ({
    group: "nodes",
    data: {
      id: node.data.id,
      label: node.data.label,
      parent: undefined,
      color: node.style.backgroundColor,
      height: node.style.height,
      width: node.style.width,
      shape: node.style.shape
    },
    position: node.position,
    selectable: true,
    grabbable: true,
    locked: false,
    pannable: false,
    classes:  [],
  }));

  // Convert transitions to edges
  const edges = transformedData.edges.map((edge, index) => ({
    group: "edges",
    data: {
      id: `edge${index}`,
      source: edge.data.source,
      target: edge.data.target,
      label: edge.data.label,
      color: edge.style.lineColor
    },
    pannable: true,
  }));

  const elements = [...nodes, ...edges];

  useEffect(() => {
    if (!containerRef.current) return;

    const cy = cytoscape({
      container: containerRef.current,
      elements,
      layout: {
        name: "preset",
      },
      style: [
        {
          selector: "node",
          style: {
            "label": "data(label)",
            "text-valign": "center",
            "color": "white",
            "background-color": "data(color)",
            "font-size": "12px",
            "width": "data(width)",
            "height": "data(height)",
            "border-width" : 2,
            "border-style": "solid",
            "border-color": "black"
          },
        },
        {
          selector: "edge",
          style: {
            "width": 2,
            "line-color": "data(color)",
            "target-arrow-color": "data(color)",
            "target-arrow-shape": "triangle",
            "curve-style": "bezier",
            "label": "data(label)",
            "font-size": "10px",
            "text-rotation": "autorotate",
            "text-margin-y": -10,
          },
        },
      ],
      zoom: 1,
      pan: { x: 0, y: 0 },
      minZoom: 1e-50,
      maxZoom: 1e50,
      zoomingEnabled: true,
      userZoomingEnabled: true,
      panningEnabled: true,
      userPanningEnabled: true,
      boxSelectionEnabled: true,
      selectionType: "single",
      touchTapThreshold: 8,
      desktopTapThreshold: 4,
      autolock: false,
      autoungrabify: false,
      autounselectify: false,
      multiClickDebounceTime: 250,
      headless: false,
      styleEnabled: true,
      hideEdgesOnViewport: false,
      textureOnViewport: false,
      motionBlur: false,
      motionBlurOpacity: 0.2,
      pixelRatio: "auto",
    });

    return () => {
      cy.destroy();
    };
  }, [elements]);

  return <div ref={containerRef} className="cytoscape-container"></div>;
};

export default CytoscapeVisualization;