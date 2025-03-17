import { useEffect, useRef, useImperativeHandle, forwardRef } from "react";
import cytoscape from "cytoscape";
import NodeSingular from "cytoscape"
import dagre from "cytoscape-dagre";
import useTransformGraph from "../hooks/useTransformGraph";
import { GraphResponse } from "../hooks/useTransformGraph";
import coseBilkent from 'cytoscape-cose-bilkent';
import avsdf from 'cytoscape-avsdf';

cytoscape.use( coseBilkent)
cytoscape.use( avsdf)
cytoscape.use( dagre)
interface CytoscapeVisualizationProps {
  data: GraphResponse;
  layout: string;
}

export interface CytoscapeVisualizationRef {
  exportPNG: () => string;
}

const CytoscapeVisualization = forwardRef<CytoscapeVisualizationRef, CytoscapeVisualizationProps>(
  ({ data, layout }, ref) => {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const transformedData = useTransformGraph(data);
  const cyRef = useRef<cytoscape.Core | null>(null);
  const initialPositionsRef = useRef<Record<string, { x: number; y: number }>>({});

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

    const cyInstance = cytoscape({
      container: containerRef.current,
      elements,
      layout: {name: layout},
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
        // Selection styles
        {
          selector: "node:selected",
          style: {
            "underlay-color": "#FFC107",
            "underlay-padding": "4px",
            "underlay-opacity": 0.5
          }
        },
        {
          selector: "edge:selected",
          style: {
            "underlay-color": "#FFC107",
            "underlay-padding": "4px",
            "underlay-opacity": 0.5
          }
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
      selectionType: "additive",
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

    // Store initial postions
    cyInstance.nodes().forEach((node: NodeSingular) => {
      const id = node.id();
      const pos = node.position();
      // Only store if not already stored.
      if (!initialPositionsRef.current[id]) {
        initialPositionsRef.current[id] = { x: pos.x, y: pos.y };
      }
    });

    cyRef.current = cyInstance;


    return () => {
      cyInstance.destroy();
      cyRef.current = null;
    };
  }, [elements]);

  useEffect(() => {
    if (!cyRef.current) return;
    const cyInstance = cyRef.current;

    switch (layout) {

    case "preset":
      // Restore positions
      cyInstance.nodes().forEach((node: NodeSingular) => {
        const id = node.id();
        if (initialPositionsRef.current[id]) {
          node.position(initialPositionsRef.current[id]);
        }
      });
      cyInstance.layout({ name: "preset" }).run();
      break;

    case "avsdf":
      {
        const numNodes = cyInstance.nodes().length;
        const spreadfactor = Math.max(100, numNodes * 20);
        cyInstance.layout({ name: 'avsdf', nodeSeparation: spreadfactor},).run();
      }
      break;

    case "dagre":
      cyInstance.layout({ name: "dagre", fit: true }).run();
      break;

    case "grid":
      cyInstance.layout({ name: "grid", fit: true}).run();
      break;

    default:
      cyInstance.layout({ name: layout }).run();
      break;
  }
}, [layout]);

  useImperativeHandle(ref, () => ({
    exportPNG: (): string => {
      if (!cyRef.current) return "";
      return cyRef.current.png({ full: true, bg: "white" });
    }
  }));

  return <div ref={containerRef} className="cytoscape-container"></div>;
});

export default CytoscapeVisualization;