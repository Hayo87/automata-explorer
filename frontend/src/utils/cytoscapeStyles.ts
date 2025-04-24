import type cytoscape from 'cytoscape';

const cytoscapeStyles: cytoscape.Stylesheet[] = [
        {
          selector: "node",
          style: {
            "label": "data(label)",
            "text-valign": "center",
            "color": "white",
            "background-color": "grey",
            "font-size": "12px",
            "width": "50px",
            "height": "50px",
            "border-width" : 2,
            "border-style": "solid",
            "border-color": "black",
            "shape": "ellipse"
          },
        },
        {
          selector: 'node.added',
          style: {
            "background-color": "green",
          }
        },
        {
          selector: 'node.removed',
          style: {
            "background-color": "red"
          }
        },
        {
          selector: 'node.unchanged',
          style: {
            "background-color": "black",
          }
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
            "line-color": "grey",
            "target-arrow-color": "grey",
            "target-arrow-shape": "triangle",
            "curve-style": "bezier",
            "label": "data(label)",
            "font-size": "10px",
            "text-rotation": "autorotate",
            "text-margin-y": -10,
          },
        },
        {
          selector: 'edge.cy-expand-collapse-collapsed-edge',
          style: {
            'label': (e: cytoscape.EdgeSingular) => {
              const count = e.data('collapsedEdges') ? e.data('collapsedEdges').length : 0;
              return '(' + count + ')';
            },
            'line-style': 'dashed',
            'opacity': 0.7,
            'width': (e: cytoscape.EdgeSingular) => {
              const count = e.data('collapsedEdges') ? e.data('collapsedEdges').length : 0;
              return (3 + Math.log2(count || 1)) + 'px';
            },
            'target-arrow-shape': function(edge: cytoscape.EdgeSingular) {
              const directionType = edge.data('directionType');
              if (directionType === 'unidirection' || directionType === 'bidirection') {
                return 'triangle';
              }
              return 'none';
            },
            'source-arrow-shape': function(edge: cytoscape.EdgeSingular) {
              const directionType = edge.data('directionType');
              return directionType === 'bidirection' ? 'triangle' : 'none';
            },
          }
        },    
        {
          selector: 'edge.added',
          style: {
            "line-color": "green",
            "target-arrow-color": "green",
            "color": "green",
          }
        },
        {
          selector: 'edge.cy-expand-collapse-collapsed-edge[edgeType="added"]',
          style: {
            'line-color': 'green',
            "target-arrow-color": "green",
            "source-arrow-color": "green",
            "color": "green",
          }
        },
        {
          selector: 'edge.removed',
          style: {
            "line-color": "red",
            "target-arrow-color": "red",
            "color": "red",
          }
        },
        {
          selector: 'edge.cy-expand-collapse-collapsed-edge[edgeType="removed"]',
          style: {
            'line-color': 'red',
            "target-arrow-color": "red",
            "source-arrow-color": "red",
            "color": "red",
          }
        },
        {
          selector: 'edge.unchanged',
          style: {
            "line-color": "black",
            "target-arrow-color": "black",
            "color": "black",
          }
        },
        {
          selector: 'edge.cy-expand-collapse-collapsed-edge[edgeType="unchanged"]',
          style: {
            'line-color': 'black',
            "target-arrow-color": "black",
            "source-arrow-color": "black",
            "color": "black",
          }
        },
        {
          selector: 'edge.combined',
          style: {
            "line-color": "lightblue",
            "target-arrow-color": "lightblue",
            "color": "lightblue",
          }
        },
        {
          selector: 'edge.cy-expand-collapse-collapsed-edge[edgeType="combined"]',
          style: {
            'line-color': 'lightblue',
            "target-arrow-color": "lightblue",
            "source-arrow-color": "lightblue",
            "color": "lightblue",
          }
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
    ];
  
  export default cytoscapeStyles;