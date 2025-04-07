import { useEffect, useRef, useImperativeHandle, forwardRef } from "react";
import cytoscape from "cytoscape";
import NodeSingular from "cytoscape"
import Core from 'cytoscape';

//hooks
import useTransformGraph from "../hooks/useTransform";
import { BuildResponse } from "../types/BuildResponse";

// Layout extentions
import dagre from "cytoscape-dagre";
import coseBilkent from 'cytoscape-cose-bilkent';
import avsdf from 'cytoscape-avsdf';
import cxtmenu from 'cytoscape-cxtmenu';
import popper from 'cytoscape-popper';
import tippy, { Props, Instance, sticky } from 'tippy.js';
import 'tippy.js/dist/tippy.css';
import type { VirtualElement } from '@popperjs/core';
import expandCollapse from "cytoscape-expand-collapse";

// Utils 
import { attachSynonymTooltips } from '../utils/attachSynonymTooltips';
import { attachCytoscapeMenus } from "../utils/attachCytoscapeMenus";
import cytoscapeStyles from '../utils/cytoscapeStyles';
import { attachExpandCollapse } from '../utils/attachContextCollapse';

// Register extensions 
cytoscape.use( coseBilkent );
cytoscape.use( avsdf );
cytoscape.use( dagre );
cytoscape.use( cxtmenu );
cytoscape.use(expandCollapse);
cytoscape.use(popper(tippyFactory));

function tippyFactory(ref: VirtualElement, content: HTMLElement): Instance<Props> {
  const dummyDomEle = document.createElement('div');

  const tip = tippy(dummyDomEle, {
    getReferenceClientRect: ref.getBoundingClientRect,
    trigger: 'manual',
    content, 
    arrow: true,
    placement: 'bottom',
    hideOnClick: false,
    interactive: true,
    appendTo: document.body,
    plugins: [sticky],
    sticky: true       
  });

  return tip;
}

interface CytoscapeVisualizationProps {
  data: BuildResponse;
  layout: string;
  openModal: (modalContent: any) => void;
  onCy?: (cy: Core) => void;
}

export interface CytoscapeVisualizationRef {
  exportPNG: () => string;
}

const CytoscapeVisualization = forwardRef<CytoscapeVisualizationRef, CytoscapeVisualizationProps>(
  ({ data, layout, openModal, onCy }, ref) => {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const transformedData = useTransformGraph(data);
  const cyRef = useRef<cytoscape.Core | null>(null);
  const initialPositionsRef = useRef<Record<string, { x: number; y: number }>>({});

  // Convert states to nodes

  useEffect(() => {
    if (!containerRef.current) return;

    const cyInstance = cytoscape({
      container: containerRef.current,
      elements: [],
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

    // Load elements in batch
    cyInstance.batch(() => {
      cyInstance.add(transformedData);
    });

    // Fit layout
    cyInstance.fit();

    // Store initial positions in a batch
    cyInstance.batch(() => {
      cyInstance.nodes().forEach((node: NodeSingular) => {
        const id = node.id();
        const pos = node.position();
        if (!initialPositionsRef.current[id]) {
          initialPositionsRef.current[id] = { x: pos.x, y: pos.y };
        }
      });
    });

    // Attach synonym tooltips
    attachSynonymTooltips(cyInstance, data?.filters || []);

    // Apply the ctx-menus
    attachCytoscapeMenus(cyInstance, openModal);

    // Attach expand and collapse functionality
    attachExpandCollapse(cyInstance);


    if (onCy) {
      onCy(cyInstance);
    }

    cyRef.current = cyInstance;

    return () => {
      cyInstance.destroy();
      cyRef.current = null;
    };
  }, [transformedData, onCy]);

  useEffect(() => {
    if (!cyRef.current) return;
    const cyInstance = cyRef.current;

    switch (layout) {

    case "preset":
      cyInstance.batch(() => {
        cyInstance.nodes().forEach((node: NodeSingular) => {
          const id = node.id();
          if (initialPositionsRef.current[id]) {
            node.position(initialPositionsRef.current[id]);
          }
        });
      });
      cyInstance.layout({ name: "preset", fit:true }).run();
      break;

    case "avsdf":
      {
        const numNodes = cyInstance.nodes().length;
        const spreadfactor = Math.max(100, numNodes * 5);
        cyInstance.layout({ name: 'avsdf', nodeSeparation: spreadfactor},).run();
      }
      break;
     
    case "cose-bilkent":
      console.log("Case bilkent gekozen");
      const numNodes = cyInstance.nodes().length;
      const spreadfactor = Math.max(9000, numNodes * 50);
      cyInstance.layout({ name: "cose-bilkent", fit: true, randomize: false, nodeRepulsion: spreadfactor, idealEdgeLength: 100}).run();
      break;  

    default:
      cyInstance.layout({ name: layout, fit:true }).run();
      break;
  }
  }, [layout]);

  useImperativeHandle(ref, () => ({
    exportPNG: (): string => {
      if (!cyRef.current) return "";
      return cyRef.current.png({ full: true, bg: "white" });
    }
  }));

  return <div ref={containerRef} className="graph-area"></div>;
});

export default CytoscapeVisualization;