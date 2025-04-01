import cytoscape from "cytoscape";
import ElementInfo from '../components/ElementInfo';

export function attachCytoscapeMenus(cyInstance: cytoscape.Core, openModal: (el: any) => void) {
      if (!cyInstance) return;
   
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
}
