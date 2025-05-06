export interface BuildResponse  {
  type: string;
  message: string;
  build: Build;
  actions: ProcessAction[];
}

export interface Build {
  name: string; 
  nodes: Node[];
  edges: Edge[];
}

export interface Node {
  id: number;
  attributes?: NodeAttributes;
}

export interface NodeAttributes{
  label: string;
  isInitial: boolean;
  diffkind: string;
}

export interface Edge {
  id: string;
  source: number;
  target: number;
  attributes?: EdgeAttributes;
}

export interface EdgeAttributes{
  diffkind: string;
  labeltext: string;
  label: LabelEntry[];
}
export interface LabelEntry {
  type: 'input' | 'output' | 'label';
  value: string;
  diffkind?: 'UNCHANGED' | 'ADDED' | 'REMOVED'; 
}


export interface ProcessAction {
    stage: String,
    type: string; 
    subtype?: string;
    order: number;
    name?: string;  
    values?: string[];
}

export interface Stats {
    totalEdges: number;
    totalNodes: number;
    unchangedEdges: number;
    combinedEdges: number;
}