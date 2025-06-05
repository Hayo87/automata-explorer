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