import cytoscape from 'cytoscape';

export function attachExpandCollapse(cyInstance: cytoscape.Core) {
    const defaultOptions = { animate: true,
    animationDuration: 500,
    undoable: true,
    cueEnabled: true,
    expandCollapseCuePosition: 'top-left',
    expandCollapseCueSize: 12,
    expandCollapseCueLineSize: 8,
    groupEdgesOfSameTypeOnCollapse: true,
    edgeTypeInfo: "edgeType",
    zIndex: 100000
  }
  
    return cyInstance.expandCollapse(defaultOptions);
  }