import { useMemo } from "react";

export interface NodeData {
  id: string;
  label: string;
  position: string;
  style: {
    shape: string;
    color: string;
    width: number;
    height: number;
  };
}

export interface EdgeData {
  id: string;
  source: string;
  target: string;
  label: string;
  style: {
    "line-color": string;
    "curve-style": string;
  };
}

export interface GraphData {
  nodes: NodeData[];
  edges: EdgeData[];
}

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
    curveStyle: string;
  };
}

const useTransformGraph = (backendData: GraphData | null) => {
  return useMemo(() => {
    if (!backendData) return { nodes: [], edges: [] };

    const nodes: TransformedNode[] = backendData.nodes.map((node) => {
      const [x, y] = node.position.split(",").map(parseFloat);
      return {
        data: {
          id: node.id,
          label: node.label,
        },
        position: { x, y },
        style: {
          shape: node.style.shape,
          backgroundColor: node.style.color,
          width: node.style.width * 50, 
          height: node.style.height * 50,
        },
      };
    });

    const edges: TransformedEdge[] = backendData.edges.map((edge) => ({
      data: {
        id: edge.id,
        source: edge.source,
        target: edge.target,
        label: edge.label,
      },
      style: {
        lineColor: edge.style["line-color"],
        curveStyle: edge.style["curve-style"],
      },
    }));

    return { nodes, edges };
  }, [backendData]);
};

export default useTransformGraph;
