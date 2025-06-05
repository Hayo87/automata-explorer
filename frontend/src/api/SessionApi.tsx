import { SessionRequest, SessionResponse, ProcessAction, BuildRequest, BuildResponse } from './RequestResponse';
import axios from "axios";

/**
 * @file SessionApi.tsx
 * 
 * Provides functions to communicate with the backend API. Includes utilities for starting/closing a session,
 * sending files, receiving responses and error handling. 
 */
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api";
const api = axios.create({ baseURL: API_BASE_URL });

// Set API error handling (400,500) and error message format
export const setGlobalErrorHandler = (onError: (msg: React.ReactNode) => void) => {
  api.interceptors.response.use(
    res => res,
    err => {
      if(!err.response){
        onError(
          <>
            <h2><span>❌</span>Error</h2>
            <hr />
            <p>{err.response?.data?.message ?? "Could not reach backend service!"}</p>
          </>

        )
      }
      else if (err.response) {
        onError( 
          <>
            <h2><span>❌</span>Error</h2>
            <hr />
            <p>{err.response.data?.message || `Unexpected error (${err.response.status})`}</p>
          </>
        );
      }
      return Promise.reject(err instanceof Error ? err: new Error(String(err)));
    }
  );
};

// Function to read a .dot file as plain text
const readDotFileAsText = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = () => reject(new Error("Failed to read .dot file"));
    reader.readAsText(file);
  });
};

// Post input files and initiate a session
export const requestSession = async (file1: File, file2: File, type:SessionRequest["type"]): Promise<SessionResponse> => {
  try {
    // Read .dot files as text
    const reference = await readDotFileAsText(file1);
    const subject = await readDotFileAsText(file2);

    // Prepare JSON payload
    const payload: SessionRequest= { type, reference, subject };

    const response = await api.post<SessionResponse>("/session", payload);

    return response.data;

  } catch (error) { 
    throw new Error(`Failed to start session: ${String(error)}`);
  }
};

// Post build action and get build data for a session
export const requestBuild = async (sessionId: string, actions:ProcessAction[]): Promise<BuildResponse> => {
  const payload: BuildRequest = { actions };
  const response = await api.post<BuildResponse>(`/session/${sessionId}/build`, payload);
  
  return response.data;
};

// Close session
export const requestSessionClose = async (sessionId: string): Promise<SessionResponse> => {
  const response = await api.delete<SessionResponse>(`/session/${sessionId}`);
  
  return response.data;
};
