import { useState } from "react";
import { uploadFiles, postBuild, closeSession } from "../api/SessionApi";
import { ProcessAction, ProcessOption } from '../types/RequestResponse';

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
      const response = await uploadFiles(file1, file2, type);
      setSessionId(response.sessionId);
      setProcessOptions(response.processingOptions);
      return {
        sessionId: response.sessionId,
        options: response.processingOptions
      }
    } catch (err) {
      setError((err as Error).message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // Build session data for visualization
  const buildSession = async (sessionId: string, actions: ProcessAction[]) => {
    try {
      setLoading(true);
      setError(null);
      const sessionData = await postBuild(sessionId, actions);
      setData(sessionData);
    } catch (err) {
      setError((err as Error).message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // Close session using the API's closeSession function.
  const terminateSession = async (sessionId: string) => {
    try {
      await closeSession(sessionId);
    } catch (err) {
      setError((err as Error).message);
      throw err;
    }
  };

  return { sessionId, processOptions, data, loading, error, startSession, buildSession, terminateSession };
};
