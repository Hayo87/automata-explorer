import { useState } from "react";
import { requestSession, requestBuild, requestSessionClose } from "../api/SessionApi";
import { ProcessAction, ProcessOption } from '../api/RequestResponse';

export const useSession = () => {
  const [sessionId, setSessionId] = useState<string | null>(null);
  const [data, setData] = useState<any>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [processOptions, setProcessOptions] = useState<ProcessOption[]>([]);

  //  Upload files and get sessionId and processing options
  const startSession = async (file1: File, file2: File, type: "STRING" | "MEALY") => {
    try {
      setLoading(true);
      setError(null);
      const response = await requestSession(file1, file2, type);
      setSessionId(response.sessionId);
      setProcessOptions(response.processingOptions);
      return response;   
    } finally {
      setLoading(false);
    }
  };

  // Build session data for visualization
  const buildDiff = async (sessionId: string, actions: ProcessAction[]) => {
    try {
      setLoading(true);
      setError(null);
      const sessionData = await requestBuild(sessionId, actions);
      setData(sessionData);
    } finally {
      setLoading(false);
    }
  };

  // Close session using the API's closeSession function.
  const endSession = async (sessionId: string) => {
    await requestSessionClose(sessionId);
  };

  return { sessionId, processOptions, data, loading, error, startSession, buildSession: buildDiff, terminateSession: endSession };
};
