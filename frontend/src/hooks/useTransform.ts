import { useMemo } from "react";
import DOMPurify from "dompurify";
import { BuildResponse} from '../types/BuildResponse';

interface TransformedNode {
  data: {
    id: string;
    label: string;
  };
  position: {
    x: number;
    y: number;
  };
  style: {
    shape: string;
    backgroundColor: string;
    fontColor: string,
    width: number;
    height: number;
  };
}

interface TransformedEdge {
  data: {
    id: string;
    source: string;
    target: string;
    label: string;
  };
  style: {
    lineColor: string;
    labelColor: string;

  };
}

const useTransformGraph = (backendData: BuildResponse | null) => {
  return useMemo(() => {
    if (!backendData) return { nodes: [], edges: [] };
    const { build: graphData } = backendData;


    let nodes: TransformedNode[] = graphData.objects.map((node) => {
      const [x, y] = node.pos.split(",").map(parseFloat);
      return {
        data: {
          id: node._gvid,
          label: node.label,
        },
        position: { 
          x: x, 
          y: y * -1 },
        style: {
          shape: node.shape,
          backgroundColor: node.fillcolor || "#000000",
          fontColor: node.fontcolor,
          width: node.width * 50, 
          height: node.height * 50,
        },
      };
    });

    let edges: TransformedEdge[] = graphData.edges.map((edge) => ({
      data: {
        id: edge.id,
        source: edge.tail,
        target: edge.head,
        label: DOMPurify.sanitize(edge.label, { ALLOWED_TAGS: [] })
      },
      style: {
        lineColor: edge.color || "#000000",
        labelColor: edge.color || "#000000"
      },
    }));

    // Post processing start state detection
    const emptyEdge = edges.find(edge => edge.data.label.trim() === "");
    if (emptyEdge) {
      const sourceId = emptyEdge.data.source;
      const targetId = emptyEdge.data.target;

    const connectedEdges = edges.filter(
      e => e.data.source === sourceId || e.data.target === sourceId
    );

    if (connectedEdges.length === 1) {
      nodes = nodes.filter(node => node.data.id !== sourceId);
      edges = edges.filter(edge => edge.data.id !== emptyEdge.data.id);
    }
    const targetNode = nodes.find(node => node.data.id === targetId);
        if (targetNode) {
          targetNode.style.shape = "doublecircle";
        }
    }
    


    return { nodes, edges };
  }, [backendData]);
};

export default useTransformGraph;
