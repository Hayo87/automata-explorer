// expandCollapseUtil.ts
import cytoscape from 'cytoscape';

export function attachExpandCollapse(cyInstance: cytoscape.Core) {
    const defaultOptions = {
      animate: false,
      animationDuration: 500,
      undoable: false,
      cueEnabled: false,
    };
  
    return cyInstance.expandCollapse(defaultOptions);
  }