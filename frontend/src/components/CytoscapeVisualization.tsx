import { useEffect, useRef, useImperativeHandle, forwardRef } from "react";
import cytoscape from "cytoscape";
import Core from 'cytoscape';

//hooks
import useTransformGraph from "../hooks/useTransform";
import { BuildResponse } from "../api/RequestResponse";

// Layout extentions
import dagre from "cytoscape-dagre";
import elk from 'cytoscape-elk';
import avsdf from 'cytoscape-avsdf';
import cxtmenu from 'cytoscape-cxtmenu';
import expandCollapse from "cytoscape-expand-collapse";
import popper from 'cytoscape-popper';
import tippy, { Props, Instance, sticky } from 'tippy.js';
import 'tippy.js/dist/tippy.css';
import type { VirtualElement } from '@popperjs/core';

// Utils 
import { attachCytoscapeMenus } from "../utils/attachCytoscapeMenus";
import cytoscapeStyles from '../utils/cytoscapeStyles';
import { attachExpandCollapse } from '../utils/attachContextCollapse';
import {exportPDF} from '../utils/exportPdf';

// Register extensions 
cytoscape.use( elk );
cytoscape.use( avsdf );
cytoscape.use( dagre );
cytoscape.use( cxtmenu );
cytoscape.use(expandCollapse);
cytoscape.use(popper(popperFactory));

function popperFactory(ref: VirtualElement, content: HTMLElement): Instance<Props> {
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
    sticky: true,
    zIndex: 10,       
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
  exportPDF: (reference?: string, subject?:string) => void;
  collapseEdges: () => void;
  unCollapseEdges: () => void;
  hideLoops: () => void;
  unHideLoops: () => void;
  showRef: () => void;
  hideRef: () => void;
  showSub: () => void;
  hideSub: () => void;
  getStats: () => Stats
}

// Interface for build stats 
export interface Stats {
  totalEdges: number;
  totalNodes: number;
  unchangedEdges: number;
  combinedEdges: number;
}

const CytoscapeVisualization = forwardRef<CytoscapeVisualizationRef, CytoscapeVisualizationProps>(
  ({ data, layout, openModal, onCy }, ref) => {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const transformedData = useTransformGraph(data);
  const cyRef = useRef<cytoscape.Core | null>(null);

  const loopsHidden = useRef(false);
  const refHidden = useRef(false);
  const subHidden = useRef(false);
  const collapsed = useRef(false);

  useEffect(() => {
    if (!containerRef.current) return;

    const cyInstance = cytoscape({
      container: containerRef.current,
      elements: [],
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
    cyInstance.layout({ name: layout, fit: true }).run();

    // Apply the ctx-menus
    attachCytoscapeMenus(cyInstance, openModal);

    // Attach expand and collapse functionality
    attachExpandCollapse(cyInstance);

    // Style start nodes
    const startNodes = cyInstance.nodes().roots();
    startNodes.forEach((n: cytoscape.NodeSingular) => 
      n.addClass('start'));

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

    case "dagre":
      {
        cyInstance.layout({ name: 'dagre', 
                            fit:true, 
                            nodeSep:60,
                            edgeSep: 20,
                            rankSep: 100,
                            rankdir: 'TB',
                            ranker: 'tight-tree',
                            padding: 20,
                            spacingFactor: 1.1,
                            nodeDimensionsIncludeLabels: true,         
                          }).run();
      }
      break;  

    case "avsdf":
      {
        const numNodes = cyInstance.nodes().length;
        const spreadfactor = Math.max(100, numNodes * 5);
        cyInstance.layout({ name: 'avsdf', nodeSeparation: spreadfactor},).run();
      }
      break;
     
    case "elk":   
      cyInstance.layout({
        name: "elk", 
        fit: true,
        elk: {
          algorithm: 'org.eclipse.elk.layered', 
          edgeRouting: 'SPLINES',
          spacing: 50, 
          direction: 'RIGHT',
          nodePlacementStrategy: 'SIMPLE'
        }
      }).run();
      break;  

    case "breadthfirst": 
      {
        cyInstance.layout({ 
          name: 'breadthfirst', 
          roots: '.start',
          directed: true,
        }).run();
      }
      break;

    default:
      cyInstance.layout({ name: layout, fit:true }).run();
      break;
  }
  }, [layout]);


  useImperativeHandle(ref, () => ({
    exportPNG: () => cyRef.current?.png({ full: true, bg: "white" }) || "",
  
    exportPDF: async (reference?: string, subject?: string) => {
       exportPDF(cyRef.current, reference, subject);  
    },

    collapseEdges: () => {
      const cy = cyRef.current;
      if (!cy) return;
      const api = (cy as any).expandCollapse('get');
      api.collapseAllEdges();
      collapsed.current = true;

      if(refHidden.current) {
        cy.elements('edge.cy-expand-collapse-collapsed-edge[edgeType="removed"]').forEach((ele: cytoscape.SingularElementReturnValue) => ele.hide())
      }
      if(subHidden.current) {
        cy.elements('edge.cy-expand-collapse-collapsed-edge[edgeType="added"]').forEach((ele: cytoscape.SingularElementReturnValue) => ele.hide())
      }
    },
  
    unCollapseEdges: () => {
      const cy = cyRef.current;
      if (!cy) return;
      const api = (cy as any).expandCollapse('get');
      api.expandAllEdges();
      collapsed.current = false;

      if(refHidden.current) {
        cy.elements('.removed').forEach((ele: cytoscape.SingularElementReturnValue) => ele.hide())
      }
      if(subHidden.current) {
        cy.elements('.added').forEach((ele: cytoscape.SingularElementReturnValue) => ele.hide())
      }
    },
  
    hideLoops: () => {
      const cy = cyRef.current;
      if (!cy) return;
      loopsHidden.current = true;
      const loops = cy.edges().filter((e: cytoscape.EdgeSingular) => e.source().id() === e.target().id());
      loops.forEach((e: cytoscape.EdgeSingular) => e.hide());
    },
  
    unHideLoops: () => {
      const cy = cyRef.current;
      if (!cy) return;
      loopsHidden.current = false;
    
      const loops = cy.edges().filter((e: cytoscape.EdgeSingular) =>
        e.source().id() === e.target().id()
      );
    
      loops.forEach((e: cytoscape.EdgeSingular) => {
        if (e.hasClass("removed") && refHidden.current) return;
        if (e.hasClass("added") && subHidden.current) return;
        e.show();
      });
    },
    
    hideRef: () => {
      const cy = cyRef.current;
      if (!cy) return;
      refHidden.current = true;
      cy.elements('.removed, edge.cy-expand-collapse-collapsed-edge[edgeType="removed"]').forEach((ele: cytoscape.SingularElementReturnValue) => ele.hide());
    },

    showRef: () => {
      const cy = cyRef.current;
      if (!cy) return;
      refHidden.current = false;
      cy.elements('.removed, edge.cy-expand-collapse-collapsed-edge[edgeType="removed"]').forEach((ele: cytoscape.SingularElementReturnValue) => {
        const isLoop = ele.isEdge() && ele.source().id() === ele.target().id();
        if (!loopsHidden.current || !isLoop) ele.show();
      });
    },
    
    hideSub: () => {
      const cy = cyRef.current;
      if (!cy) return;
      subHidden.current = true;
      cy.elements('.added, edge.cy-expand-collapse-collapsed-edge[edgeType="added"]').forEach((ele: cytoscape.SingularElementReturnValue) => ele.hide());
    },

    showSub: () => {
      const cy = cyRef.current;
      if (!cy) return;
      subHidden.current = false;
      cy.elements('.added, edge.cy-expand-collapse-collapsed-edge[edgeType="added"]').forEach((ele: cytoscape.SingularElementReturnValue) => {
        const isLoop = ele.isEdge() && ele.source().id() === ele.target().id();
        if (!loopsHidden.current || !isLoop) ele.show();
      });
    },

    getStats: () => {
      const cy = cyRef.current;
      const totalNodes = cy.nodes().length;
      const totalEdges = cy.edges().length;
      const unchangedEdges = cy.edges('.unchanged').length;
      const combinedEdges = cy.edges('.combined').length;

      const stats = {
        totalNodes,
        totalEdges,
        unchangedEdges,
        combinedEdges,
      };
      return stats;
    }

  }));
  
  return <div ref={containerRef} className="graph-area"></div>;
});

export default CytoscapeVisualization;