import { useMemo } from "react";
import DOMPurify from "dompurify";

export interface NodeData {
  _gvid: string,
  name: string,
  fillcolor:  string,
  fontcolor:  string,
  height: number,
  label:  string,
  pos: string,
  shape: string,
  style: string,
  width: number, 
}

export interface EdgeData {
  _gvid: string,
  tail: string,
  head: string,
  color: string,
  id: string,
  label: string,
  lp: string,
  pos: string,
  };

export interface GraphData {
  name: string, 
  objects: NodeData[],
  edges: EdgeData[],
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

const useTransformGraph = (backendData: GraphData | null) => {
  return useMemo(() => {
    if (!backendData) return { nodes: [], edges: [] };

    const nodes: TransformedNode[] = backendData.objects.map((node) => {
      const [x, y] = node.pos.split(",").map(parseFloat);
      return {
        data: {
          id: node._gvid,
          label: node.label,
        },
        position: { x, y },
        style: {
          shape: node.shape,
          backgroundColor: node.fillcolor,
          fontColor: node.fontcolor,
          width: node.width * 50, 
          height: node.height * 50,
        },
      };
    });

    const edges: TransformedEdge[] = backendData.edges.map((edge) => ({
      data: {
        id: edge.id,
        source: edge.tail,
        target: edge.head,
        label: DOMPurify.sanitize(edge.label, { ALLOWED_TAGS: [] })
      },
      style: {
        lineColor: edge.color,
        labelColor: edge.color
      },
    }));

    return { nodes, edges };
  }, [backendData]);
};

export default useTransformGraph;
