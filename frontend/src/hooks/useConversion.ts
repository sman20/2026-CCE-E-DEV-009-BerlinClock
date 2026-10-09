import { useCallback, useEffect, useRef, useState } from 'react';
import type { Conversion } from '../api';

export type ConversionState =
    | { status: 'idle' }
    | { status: 'pending' }
    | { status: 'success'; result: string }
    | { status: 'error'; message: string };

export function useConversion(convert: Conversion) {
  const [state, setState] = useState<ConversionState>({ status: 'idle' });
  const controller = useRef<AbortController | null>(null);
  const invalidate = useCallback(() => {
    controller.current?.abort();
    setState({ status: 'idle' });
  }, []);
  const run = useCallback(async (input: string) => {
    controller.current?.abort();
    const request = new AbortController();
    controller.current = request;
    setState({ status: 'pending' });
    try {
      const result = await convert(input, request.signal);
      if (!request.signal.aborted) {
        setState({ status: 'success', result });
      }
    } catch (cause) {
      if (!request.signal.aborted) {
        setState({ status: 'error', message: cause instanceof Error
              ? cause.message : 'Conversion failed. Please try again.' });
      }
    }
  }, [convert]);
  useEffect(() => () => { controller.current?.abort(); }, []);
  return { state, run, invalidate };
}
