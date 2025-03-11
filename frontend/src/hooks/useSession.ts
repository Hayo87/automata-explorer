import { useState } from "react";
import { uploadFiles, fetchSessionData, closeSession } from "../api/SessionApi";

export const useSession = () => {
  const [sessionId, setSessionId] = useState<string | null>(null);
  const [data, setData] = useState<any>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  //  Upload files and get sessionId
  const startSession = async (file1: File, file2: File) => {
    try {
      setLoading(true);
      setError(null);
      const newSessionId = await uploadFiles(file1, file2);
      setSessionId(newSessionId);
      return newSessionId;
    } catch (err) {
      setError((err as Error).message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // Fetch session data for visualization
  const loadSessionData = async (sessionId: string) => {
    try {
      setLoading(true);
      setError(null);
      const sessionData = await fetchSessionData(sessionId);
      setData(sessionData);
    } catch (err) {
      setError((err as Error).message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // Close session using the API's closeSession function.
  const closeSessionHook = async (sessionId: string) => {
    try {
      await closeSession(sessionId);
    } catch (err) {
      setError((err as Error).message);
      throw err;
    }
  };

  return { sessionId, data, loading, error, startSession, loadSessionData, closeSession: closeSessionHook };
};
