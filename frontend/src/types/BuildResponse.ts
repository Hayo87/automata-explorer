export interface BuildResponse  {
  action: string;
  status: string;
  message: string;
  build: GraphData;
  filters: Filter[];
}

export interface GraphData {
  name: string, 
  objects: NodeData[],
  edges: EdgeData[],
}

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
}

export interface Filter {
    order: number,
    type: string; 
    subtype?: string;
    name: string;  
    values: string[];
    decoratedName: string;
}
  