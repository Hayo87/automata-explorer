import { useMemo } from "react";
import DOMPurify from "dompurify";
import { BuildResponse} from '../types/BuildResponse';

const useTransformGraph = (backendData: BuildResponse | null) => {
  return useMemo(() => {
    if (!backendData) return { nodes: [], edges: [] };
    const { build: graphData } = backendData;

    let nodes = graphData.objects.map((node) => {
      const [x, y] = node.pos.split(",").map(parseFloat);

      let typeClass = "";
      if (node.fillcolor === "#00cc00") {
        typeClass = "added";
      } else if (node.fillcolor === "#ff4040") {
        typeClass = "removed";
      } else {
        typeClass = "common";
      }

      return {
        group: "nodes",
        data: {
          id: node._gvid,
          label: node.label,
          parent: undefined,
          NodeType:typeClass
        },
        position: { x, y: y * -1 },
        selectable: true,
        grabbable: true,
        locked: false,
        pannable: false,
        classes: typeClass, 
      };
    });

    let edges = graphData.edges.map((edge) => {
      let typeClass = "";
      if (edge.color === "#00cc00") {
        typeClass = "added";
      } else if (edge.color === "#ff4040") {
        typeClass = "removed";
      } else {
        typeClass = "common";
      }
      return {
        group: "edges",
        data: {
          id: edge.id,
          source: edge.tail,
          target: edge.head,
          label: DOMPurify.sanitize(edge.label, { ALLOWED_TAGS: [] }),
          edgeType: typeClass,
        },
        pannable: true,
        classes: typeClass,
      };
    });
    
    return { nodes, edges };
  }, [backendData]);
};

export default useTransformGraph;