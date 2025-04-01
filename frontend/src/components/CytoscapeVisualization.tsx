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
import ElementInfo from '../components/ElementInfo';

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
          selector: 'node.start',
          style: {
            'underlay-color': 'blue',
            'underlay-padding': '4px',
            'underlay-opacity': 0.5,
            'underlay-shape' : 'ellipse'
          }
        },
        {
          selector: 'node.checked',
          style: {
            'opacity': 0.3,
          }
        },
        {
          selector: 'node.starred',
          style: {
            'background-image': 'url(data:image/svg+xml;utf8,' + encodeURIComponent(`<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE svg><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path fill="yellow" d="M12 .587l3.668 7.431 8.2 1.192-5.934 5.787 1.402 8.168L12 18.897l-7.336 3.864 1.402-8.168L.132 9.21l8.2-1.192z"/></svg>`) + ')',
            'background-image-containment': 'over',
            'background-position-x': '140%',
            'background-position-y': '-10%',
            'background-height': '50%',
            'background-width': '50%',
            'background-width-relative-to': 'inner', 
            'background-clip': 'none',
            'bounds-expansion': 20,
            'background-repeat': 'no-repeat'
          }
        },
        {
          selector: 'node.pie',
          style: {
            "width": "60px",
            "height": "60px",
            'pie-size': '90%',                           
            'pie-1-background-color': 'data(slice1Color)',
            'pie-1-background-size': 'data(slice1Size)',
            'pie-2-background-color': 'data(slice2Color)',
            'pie-2-background-size': 'data(slice2Size)',
            'pie-3-background-color': 'data(slice3Color)',
            'pie-3-background-size': 'data(slice3Size)',
            'background-color': '#ccc'

          }
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
        {
          selector: 'edge.checked',
          style: {
            'opacity': 0.3,
          }
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

    // Process synonyms on edges
    cyInstance.edges().forEach((edge: cytoscape.EdgeSingular) => {
      const label = edge.data('label');
      const [input, output] = label?.split('/')?.map((s: string) => s.trim()) ?? [];

      const hasInputSynonym = input && synonyms.has(input);
      const hasOutputSynonym = output && synonyms.has(output);

      if (hasInputSynonym || hasOutputSynonym) {
        const decoratedInput = hasInputSynonym ? `{${input}}` : input;
        const decoratedOutput = hasOutputSynonym ? `{${output}}` : output;
        const decoratedLabel = `${decoratedInput}/${decoratedOutput}`;

        edge.data('label', decoratedLabel);
        edge.addClass('synonym');

        if (hasInputSynonym) {
          edge.data('synonymInput', input);
        }
        if (hasOutputSynonym) {
          edge.data('synonymOutput', output);
        }
      }
    });


    // Show tooltip on mouseover for synonyms
    cyInstance.on('mouseover', 'edge.synonym', (event: cytoscape.EventObject) => {
      const edge = event.target;
    
      const inputTerm = edge.data('synonymInput');
      const outputTerm = edge.data('synonymOutput');
    
      const inputValues = inputTerm ? synonyms.get(inputTerm) : null;
      const outputValues = outputTerm ? synonyms.get(outputTerm) : null;
    
      if (!inputValues && !outputValues) return;
    
      const content = document.createElement('div');
      content.innerHTML = `
        ${inputValues ? `${inputTerm} → {${inputValues.join(', ')}}<br/>` : ''}
        ${outputValues ? `${outputTerm} → {${outputValues.join(', ')}}` : ''}
      `;
    
      const tip = edge.popper({
        content: () => content,
      });
    
      tip.show();
    
      edge.once('mouseout', () => tip.destroy());
    });
    

    // Add the context menu's 
    cyInstance.cxtmenu({
      selector: "node",
      commands: [
        {
          content: '<span class="fa fa-check-circle-o fa-2x"></span>',
          select: (node: cytoscape.NodeSingular) => {
          node.toggleClass('checked');
          },
        },
        {
          content: '<span class="fa fa-star fa-2x"></span>',
          select: (node: cytoscape.NodeSingular) => {
            node.toggleClass('starred');
          },
        },
        {
          content: '<span class="fa fa-info-circle fa-2x"></span>',
          select: (node: cytoscape.NodeSingular ) => {
          openModal(<ElementInfo element={node} />);
          }
        },

        {
          content: '<span class="fa fa-pie-chart fa-2x"></span>',
          select: (ele: cytoscape.NodeSingular) => {

            // Combine incoming and outgoing edges.
            const edges = ele.incomers('edge').union(ele.outgoers('edge'));
            const total = edges.length;

            // Tally edge colors.
            const counts: { [color: string]: number } = {};
            edges.forEach((edge: cytoscape.EdgeSingular)  => {
              const edgeColor = edge.style('line-color');
              counts[edgeColor] = (counts[edgeColor] || 0) + 1;
            });

            const computedSlices = Object.entries(counts)
              .map(([color, count]) => ({ color, size: (count / total) * 100 }))
              .sort((a, b) => b.size - a.size)
              .slice(0, 3);

              // Initialize slices with defaults
              let slices = [
                { color: 'gray', size: 0 },
                { color: 'gray', size: 0 },
                { color: 'gray', size: 0 },
              ];

              // Compute 
              computedSlices.forEach((slice, i) => {
                slices[i] = slice;
              });

              // Only present pie if at least 2 colors
              const nonDefaultColors = computedSlices.filter(slice => slice.color !== 'gray');
              if (nonDefaultColors.length < 2) {
                return;
              }

              ele.data({
                slice1Color: slices[0].color,
                slice1Size: slices[0].size,
                slice2Color: slices[1].color,
                slice2Size: slices[1].size,
                slice3Color: slices[2].color,
                slice3Size: slices[2].size,
              });
              
            ele.toggleClass('pie');
          },
        },

      ],
      fillColor: "rgba(0, 0, 0, 0.75)",
      activeFillColor: "rgba(0, 0, 0, 1)",
      activePadding: 10,
      indicatorSize: 24,
      separatorWidth: 3,
      spotlightPadding: 4,
      minSpotlightRadius: 24,
      maxSpotlightRadius: 38,
    });

    cyInstance.cxtmenu({
      selector: "edge",
      commands: [
        {
          content: '<span class="fa fa-check-circle-o fa-2x"></span>',
          select: (edge: cytoscape.EdgeSingular) => {
          edge.toggleClass('checked');
          },
        },
        {
          content: '<span class="fa fa-info-circle fa-2x"></span>',
          select: (edge: cytoscape.EdgeSingular) => {
          openModal(<ElementInfo element={edge} />);
          }
        },
        {
          content: '<span class="fa fa-cogs fa-2x"></span>',
          select: (edge: cytoscape.EdgeSingular) => {
            if (edge.tippyInstance) {
              edge.tippyInstance.destroy();
              delete edge.tippyInstance;
            } else {
              const tip = edge.popper({
                content: () => {
                  const content  = document.createElement('div');
                  content .innerHTML = `Dummy Operation`;
                  return content ;
                },
              });
        
              tip.show();
              edge.tippyInstance = tip;
            }
          }
        },
      ],
      fillColor: "rgba(0, 0, 0, 0.75)",
      activeFillColor: "rgba(0, 0, 0, 1)",
      activePadding: 10,
      indicatorSize: 24,
      separatorWidth: 3,
      spotlightPadding: 4,
      minSpotlightRadius: 24,
      maxSpotlightRadius: 38,
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