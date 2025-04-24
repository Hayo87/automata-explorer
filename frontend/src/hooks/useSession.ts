import { useState } from "react";
import { uploadFiles, postBuild, closeSession } from "../api/SessionApi";
import { Filter } from '../types/BuildResponse';

export const useSession = () => {
  const [sessionId, setSessionId] = useState<string | null>(null);
  const [data, setData] = useState<any>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  //  Upload files and get sessionId
  const startSession = async (file1: File, file2: File, type: "STRING" | "MEALY") => {
    try {
      setLoading(true);
      setError(null);
      const newSessionId = await uploadFiles(file1, file2, type);
      setSessionId(newSessionId);
      return newSessionId;
    } catch (err) {
      setError((err as Error).message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // Build session data for visualization
  const buildSession = async (sessionId: string, filters: Filter[]) => {
    try {
      setLoading(true);
      setError(null);
      const sessionData = await postBuild(sessionId, filters);
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

  return { sessionId, data, loading, error, startSession, buildSession, terminateSession };
};
