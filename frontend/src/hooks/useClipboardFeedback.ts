import { useCallback, useEffect, useRef, useState } from 'react';

export function useClipboardFeedback() {
  const [feedback, setFeedback] = useState('');
  const attempt = useRef(0);
  const timer = useRef<ReturnType<typeof setTimeout> | null>(null);
  const invalidate = useCallback(() => {
    attempt.current += 1;
    if (timer.current !== null) {
      clearTimeout(timer.current);
    }
    timer.current = null;
  }, []);
  const reset = useCallback(() => {
    invalidate();
    setFeedback('');
  }, [invalidate]);
  const copy = useCallback(async (result: string) => {
    reset();
    const current = attempt.current;
    try {
      if (!navigator.clipboard?.writeText) {
        throw new Error('Clipboard unavailable');
      }
      await navigator.clipboard.writeText(result);
      if (current !== attempt.current) {
        return;
      }
      setFeedback('Copied');
      timer.current = setTimeout(() => {
        if (current === attempt.current) {
          setFeedback('');
          timer.current = null;
        }
      }, 1200);
    } catch {
      if (current === attempt.current) {
        setFeedback('Could not copy. Select the result to copy it manually.');
      }
    }
  }, [reset]);
  useEffect(() => invalidate, [invalidate]);
  return { feedback, reset, copy };
}
