import cytoscape from 'cytoscape';

export function attachExpandCollapse(cyInstance: cytoscape.Core) {
    const defaultOptions = {
      animate: true,
      animationDuration: 500,
      undoable: true,
      cueEnabled: true,
    };
  
    return cyInstance.expandCollapse(defaultOptions);
  }