import { useEffect, useRef, useImperativeHandle, forwardRef } from "react";
import cytoscape from "cytoscape";
import NodeSingular from "cytoscape"
import dagre from "cytoscape-dagre";
import useTransformGraph from "../hooks/useTransformGraph";
import { GraphResponse } from "../hooks/useTransformGraph";
import coseBilkent from 'cytoscape-cose-bilkent';
import avsdf from 'cytoscape-avsdf';
import cxtmenu from 'cytoscape-cxtmenu';
import popper from 'cytoscape-popper';
import tippy, { Props, Instance } from 'tippy.js';
import 'tippy.js/dist/tippy.css'; // optional: tippy styling
import type { VirtualElement } from '@popperjs/core';
import { attachSynonymTooltips } from './utils/attachSynonymTooltips';
import { processSynonymLabels  } from './utils/processSynonymLabels';
import { attachCytoscapeMenus } from "./utils/attachCytoscapeMenus";
import cytoscapeStyles from '../style/cytoscapeStyles';


// Register extensions 
cytoscape.use( coseBilkent)
cytoscape.use( avsdf)
cytoscape.use( dagre)
cytoscape.use(cxtmenu);


function tippyFactory(ref: VirtualElement, content: HTMLElement): Instance<Props> {
  const dummyDomEle = document.createElement('div');

  const tip = tippy(dummyDomEle, {
    getReferenceClientRect: ref.getBoundingClientRect,
    trigger: 'manual',
    content, 
    arrow: true,
    placement: 'bottom',
    hideOnClick: false,
    sticky: 'reference',
    interactive: true,
    appendTo: document.body,
  });

  return tip;
}

cytoscape.use(popper(tippyFactory));

interface CytoscapeVisualizationProps {
  data: GraphResponse;
  layout: string;
  openModal: (modalContent: any) => void;
  synonyms: Map<string, string[]>;
}

export interface CytoscapeVisualizationRef {
  exportPNG: () => string;
}


const CytoscapeVisualization = forwardRef<CytoscapeVisualizationRef, CytoscapeVisualizationProps>(
  ({ data, layout, openModal, synonyms }, ref) => {
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
    classes:  node.style.shape === "doublecircle" ? "start" : ""
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
      style: cytoscapeStyles ,
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

    // Process synonyms on edges
    processSynonymLabels(cyInstance, synonyms);

    // Show tooltip on mouseover for synonyms
    attachSynonymTooltips(cyInstance, synonyms);
    
    // Apply the ctx-menus
    attachCytoscapeMenus(cyInstance, openModal);
       
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