import { DependencyList, useCallback, useEffect, useState } from 'react';
import { msgErro } from './api';

export function useLoad<T>(fn: () => Promise<T>, deps: DependencyList) {
  const [data, setData] = useState<T | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [tick, setTick] = useState(0);

  useEffect(() => {
    let vivo = true;
    setLoading(true);
    fn()
      .then((d) => {
        if (vivo) {
          setData(d);
          setError(null);
        }
      })
      .catch((e) => vivo && setError(msgErro(e)))
      .finally(() => vivo && setLoading(false));
    return () => {
      vivo = false;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...deps, tick]);

  const reload = useCallback(() => setTick((t) => t + 1), []);
  return { data, error, loading, reload };
}
