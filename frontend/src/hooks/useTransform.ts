import { useMemo } from "react";
import { BuildResponse, LabelEntry} from '../types/BuildResponse';

const useTransformGraph = (backendData: BuildResponse | null) => {
  return useMemo(() => {
    if (!backendData) return { nodes: [], edges: [] };
    const { build: graphData } = backendData;

    let nodes = graphData.nodes.map((node) => {
      return {
        group: "nodes",
        data: {
          id: node.name,
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
      const labelEntries = Array.isArray(edge.attributes?.label)
        ? edge.attributes.label as LabelEntry[]
        : [];

      const inputs = labelEntries
      .filter((entry: LabelEntry) => entry.type === 'input')
      .map((entry: LabelEntry) => `${entry.value} [${entry.diffkind}]`)
      .join(', ');
  
    const outputs = labelEntries
      .filter((entry: LabelEntry) => entry.type === 'output')
      .map((entry: LabelEntry) => `${entry.value} [${entry.diffkind}]`)
      .join(', ');
  
    const label = `${inputs} / ${outputs}`;


      return {
        group: "edges",
        data: {
          id: edge.id,
          source: edge.tail,
          target: edge.head,
          label: label,
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