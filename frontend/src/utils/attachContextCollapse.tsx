import cytoscape from 'cytoscape';

/**
 * @function attachExpandCollapse
 * 
 * Enables expand/collapse functionality on a Cytoscape instance using the cytoscape-expand-collapse extension.
 * Applies default options including animation and edge grouping behavior.
 * 
 * @param cyInstance - cyInstance to enhance.
 * @returns The initialized expandCollapse API for the given Cytoscape instance.
 */

export function attachExpandCollapse(cyInstance: cytoscape.Core) {
    const defaultOptions = { animate: true,
    animationDuration: 500,
    undoable: false,
    cueEnabled: true,
    expandCollapseCuePosition: 'top-left',
    expandCollapseCueSize: 12,
    expandCollapseCueLineSize: 8,
    groupEdgesOfSameTypeOnCollapse: true,
    edgeTypeInfo: "edgeType",
    zIndex: 999,
  }
  cyInstance.expandCollapse(defaultOptions);

  // Attach event handler to recompute edges after parentnode event  
  cyInstance.on('expandcollapse.aftercollapse expandcollapse.afterexpand', (event: any) => {
    const node = event.target;

    cyInstance.batch(() => {
      // Remove all summary edges  
      node.connectedEdges('[summary]').remove();

      // Show all remaining edges
      node.descendants('node').connectedEdges().forEach((edge:any) => {
          edge.show();
        });
    });

  updateProjection(cyInstance, node);
});
}

export function updateProjection(cy: cytoscape.Core, node: cytoscape.NodeSingular) {
  const seen = new Set<string>();

  cy.batch(() => {
    const neighbors = node.neighborhood('node');

    neighbors.forEach((neighbor: cytoscape.NodeSingular) => {
      const edges = node.edgesWith(neighbor).filter((e:any) => !e.data('summary'));
    
      // Group by source target and label
      const groups = new Map<string, cytoscape.EdgeSingular[]>();

      // collect all edges
      edges.forEach((edge:any) => {
        const label = edge.data('label');
        const src = edge.source().id();
        const tgt = edge.target().id();
        const key = `${src}->${tgt}:${label}`;

        if (!groups.has(key)) groups.set(key, []);
        groups.get(key)!.push(edge);
      });

      // For each group, merge if more than one
      groups.forEach((group, key) => {
        if (group.length < 2 || seen.has(key)) return;
        seen.add(key);

        // Hide original edges
        group.forEach(e => e.hide());

        // Determine class (added, removed, unchanged)
        const classList = group.map(e =>
          ['added', 'removed', 'unchanged'].find(cls => e.hasClass(cls))!
        );
        const summaryClass = classList.every(cls => cls === classList[0])
          ? classList[0]
          : 'unchanged';

        const [src, rest] = key.split('->');
        const [tgt, label] = rest.split(':');

        cy.add({
          group: 'edges',
          data: {
            id: `summary-${src}-${tgt}-${label}-${Date.now()}`,
            source: src,
            target: tgt,
            label,
            summary: true,
            count: group.length
          },
          classes: `summary ${summaryClass}`
        });
      });
    });
  });
};
