export interface BuildResponse  {
  type: string;
  message: string;
  build: GraphData;
  filters: Filter[];
}

export interface GraphData {
  name: string, 
  nodes: NodeData[],
  edges: EdgeData[],
}

export interface NodeData {
  name: string;
  attributes?: Attributes;
}

export interface EdgeData {
  id: string;
  tail: string | number;
  head: string | number;
  attributes?: Attributes;
}

export interface LabelEntry {
  type: 'input' | 'output';
  value: string;
  diffkind?: 'UNCHANGED' | 'ADDED' | 'REMOVED'; 
}

export interface Attributes {
  [key: string]: string | number | boolean | LabelEntry[];
}

export interface Filter {
    order: number,
    type: string; 
    subtype?: string;
    name: string;  
    values: string[];
    decoratedName: string;
}

export interface Stats {
    totalEdges: number;
    totalNodes: number;
    unchangedEdges: number;
    combinedEdges: number;
}
  