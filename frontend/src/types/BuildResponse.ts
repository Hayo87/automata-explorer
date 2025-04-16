export interface BuildResponse  {
  action: string;
  status: string;
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

export interface Attributes {
  [key: string]: string | number | boolean | { html: string };
}

export interface Filter {
    order: number,
    type: string; 
    subtype?: string;
    name: string;  
    values: string[];
    decoratedName: string;
}
  