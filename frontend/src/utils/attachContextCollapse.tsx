import cytoscape from 'cytoscape';

export function attachExpandCollapse(cyInstance: cytoscape.Core) {
    const defaultOptions = { animate: true,
    animationDuration: 500,
    undoable: true,
    cueEnabled: true,
    expandCollapseCuePosition: 'top-left',
    expandCollapseCueSize: 12,
    expandCollapseCueLineSize: 2,
    groupEdgesOfSameTypeOnCollapse: true
  }
  
    return cyInstance.expandCollapse(defaultOptions);
  }