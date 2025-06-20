/**
 * @file RequestResponse.ts
 * 
 * Defines the interfaces used for request and response messages with the backend.
 */

// Interfaces for sessions
export interface SessionRequest{
  type: "MEALY" | "STRING";
  reference: string;
  subject: string;
}

export interface SessionResponse {
  sessionId: string;
  processingOptions: ProcessOption[];
}

export interface ProcessOption {
  stage:"PRE" | "POST"
  type: string,
  subtypes: string[]
}

// Interfaces for build settings and responses
export interface BuildRequest{
  actions?: ProcessAction[];
}
export interface BuildResponse  {
  type: string;
  build: Build;
  analysis: Analysis;
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

export interface Analysis {
  twins: TwinAnalysis[];
  groupedTwins: GroupedTwinAnalysis[]
}

export interface TwinAnalysis {
  left: number,
  right: number,
  causes: Edge[]
}

export interface GroupedTwinAnalysis{
  members: number[],
  causes: Edge[]
}

export interface ProcessAction {
    stage: string,
    type: string; 
    subtype?: string;
    order: number;
    name?: string;  
    values?: string[];
}