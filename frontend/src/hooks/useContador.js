<<<<<<< HEAD
import { useState, useEffect } from 'react';
import api from '../services/api';

export function useContador(apiPath) {
  const [count, setCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let cancelled = false;
    const fetchCount = async () => {
      try {
        setLoading(true);
        const response = await api.get(apiPath);
        if (cancelled) return;
        setCount(typeof response.data === 'number' ? response.data : (response.data?.count ?? response.data?.total ?? 0));
      } catch (e) {
        if (!cancelled) setError(e);
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    fetchCount();
    return () => { cancelled = true; };
  }, [apiPath]);

  return { count, loading, error };
=======
import { useState, useEffect } from 'react'
import api from '../services/api'

export function useContador(apiPath) {
  const [count, setCount] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    let cancelled = false
    const fetchCount = async () => {
      try {
        setLoading(true)
        const response = await api.get(apiPath)
        if (cancelled) return
        setCount(typeof response.data === 'number' ? response.data : (response.data?.count ?? response.data?.total ?? 0))
      } catch (e) {
        if (!cancelled) setError(e)
      } finally {
        if (!cancelled) setLoading(false)
      }
    }

    fetchCount()
    return () => { cancelled = true }
  }, [apiPath])

  return { count, loading, error }
>>>>>>> develop
}