import cytoscape from "cytoscape";
import ElementInfo from '../components/ElementContent';

/**
 * @function attachCytoscapeMenus
 * 
 * Attaches the context menu to the cyInstance using the ctxmenu extension. Enables custom actions when 
 * right clicking nodes or edges.  
 */

export function attachCytoscapeMenus(cyInstance: cytoscape.Core, openModal: (el: any) => void) {
      if (!cyInstance) return;
   
    cyInstance.cxtmenu({
            selector: "node:childless",
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

            {
                content: '<span class="fa fa-link fa-2x"></span>',
                select: (node: cytoscape.NodeSingular) => {
                  cyInstance.elements().unselect();
                  node.select();
                  node.neighborhood().select();
                },  
            },
            {
                content: '<span class="fa fa-tag fa-2x"></span>',
                select: (node: cytoscape.NodeSingular) => {
                  const key = '__tippy';
                  const tip = node.scratch('__tippy');
                  const text = node.data('tooltip');
              
                  if (!text) {
                    const content = document.createElement('div');
                    content.textContent = 'Edit label';
                    content.contentEditable = 'true';
                    content.style.outline = 'none';
                    content.style.minWidth = '100px';
                    content.style.padding = '4px';
                    content.style.cursor = 'text';
              
                    // Save on blur
                    content.onblur = () => {
                      const updated = content.textContent?.trim();
                      if (updated) node.data('tooltip', updated);
                    };
              
                    const instance = node.popper({ content: () => content });
                    instance.show();
                    node.data('tooltip', 'Edit me label');
                    node.scratch(key, instance);
                  } else {
                    node.removeData('tooltip');
                    tip?.destroy?.();
                    node.removeScratch(key);
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
                content: '<span class="fa fa-link fa-2x"></span>',
                select: (edge: cytoscape.EdgeSingular ) => {
                  cyInstance.elements().unselect();
                  edge.select();
                  edge.connectedNodes().select();
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
}
