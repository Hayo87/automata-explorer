import { useMemo } from "react";
import { BuildResponse} from '../api/RequestResponse';

/**
 * @file useTransform.ts
 * 
 * Custom react hook that maps backend data into frontend ready format. 
 * @param backendData the rad backend data
 * @returns Cytoscape compatible nodes and edges
 */

const useTransformGraph = (backendData: BuildResponse | null) => {
  return useMemo(() => {
    if (!backendData) return { nodes: [], edges: [] };
    const { build: graphData } = backendData;

    let nodes = graphData.nodes.map((node) => {
      return {
        group: "nodes",
        data: {
          id: node.id,
          label: node.attributes?.label,
          NodeType:typeof node.attributes?.diffkind === "string"
          ? node.attributes.diffkind.toLowerCase()
          : "",
        },
        selectable: true,
        grabbable: true,
        locked: false,
        pannable: false,
        classes: typeof node.attributes?.diffkind === "string"
        ? node.attributes.diffkind.toLowerCase()
        : ""
      };
    });

    let edges = graphData.edges.map((edge) => {
      return {
        group: "edges",
        data: {
          id: edge.id,
          source: edge.source,
          target: edge.target,
          label: edge.attributes?.labeltext,
          edgeType: typeof edge.attributes?.diffkind === "string"
          ? edge.attributes.diffkind.toLowerCase()
          : "",
        },
        pannable: true,
        classes: typeof edge.attributes?.diffkind === "string"
        ? edge.attributes.diffkind.toLowerCase()
        : "",
      };
    });
    
    return { nodes, edges };
  }, [backendData]);
};

export default useTransformGraph;