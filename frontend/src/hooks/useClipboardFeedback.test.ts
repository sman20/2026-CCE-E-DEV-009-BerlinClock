import { act, renderHook } from '@testing-library/react';
import { expect, it, vi } from 'vitest';
import { useClipboardFeedback } from './useClipboardFeedback';
import { deferred } from '../test/deferred';
function clipboard(writeText = vi.fn().mockResolvedValue(undefined)) {
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText },
  });
  return writeText;
}
it('copies exact text, awaits success and expires feedback after 1.2 seconds', async () => {
  vi.useFakeTimers();
  const write = deferred<void>();
  const writeText = clipboard(vi.fn().mockReturnValue(write.promise));
  const { result } = renderHook(useClipboardFeedback);
  act(() => {
    void result.current.copy('ORROORRROYYROOOOOOOOYYOO');
  });
  expect(writeText).toHaveBeenCalledWith('ORROORRROYYROOOOOOOOYYOO');
  expect(result.current.feedback).toBe('');
  await act(async () => {
    write.resolve();
  });
  expect(result.current.feedback).toBe('Copied');
  act(() => vi.advanceTimersByTime(1199));
  expect(result.current.feedback).toBe('Copied');
  act(() => vi.advanceTimersByTime(1));
  expect(result.current.feedback).toBe('');
});
it.each(['unavailable', 'rejected'])(
    'reports truthful %s clipboard failure',
    async (mode) => {
      if (mode === 'unavailable')
        Object.defineProperty(navigator, 'clipboard', {
          configurable: true,
          value: undefined,
        });
      else clipboard(vi.fn().mockRejectedValue(new Error('denied')));
      const { result } = renderHook(useClipboardFeedback);
      await act(async () => {
        await result.current.copy('reading');
      });
      expect(result.current.feedback).toBe(
          'Could not copy. Select the result to copy it manually.',
      );
    },
);
it('invalidates a pending write and timer when the input or conversion changes', async () => {
  vi.useFakeTimers();
  const write = deferred<void>();
  clipboard(
      vi.fn().mockReturnValueOnce(write.promise).mockResolvedValue(undefined),
  );
  const { result } = renderHook(useClipboardFeedback);
  act(() => {
    void result.current.copy('old');
    result.current.reset();
  });
  await act(async () => {
    write.resolve();
  });
  expect(result.current.feedback).toBe('');
  await act(async () => {
    await result.current.copy('new');
  });
  expect(result.current.feedback).toBe('Copied');
  act(() => result.current.reset());
  expect(result.current.feedback).toBe('');
  expect(vi.getTimerCount()).toBe(0);
});
it.each(['success', 'failure'])(
    'a new copy attempt ignores older late %s', async (completion) => {
      const old = deferred<void>();
      const current = deferred<void>();
      clipboard(
          vi
              .fn()
              .mockReturnValueOnce(old.promise)
              .mockReturnValueOnce(current.promise),
      );
      const { result } = renderHook(useClipboardFeedback);
      act(() => {
        void result.current.copy('same');
        void result.current.copy('same');
      });
      await act(async () => {
        current.reject(new Error('denied'));
      });
      const failure = result.current.feedback;
      await act(async () => {
        if (completion === 'success') old.resolve();
        else old.reject(new Error('older failure'));
      });
      expect(result.current.feedback).toBe(failure);
      expect(failure).toContain('Could not copy');
    });
it('clears timers and ignores pending writes on unmount', async () => {
  vi.useFakeTimers();
  const late = deferred<void>();
  clipboard(
      vi.fn().mockResolvedValueOnce(undefined).mockReturnValueOnce(late.promise),
  );
  const { result, unmount } = renderHook(useClipboardFeedback);
  await act(async () => {
    await result.current.copy('first');
  });
  expect(vi.getTimerCount()).toBe(1);
  act(() => {
    void result.current.copy('late');
  });
  expect(vi.getTimerCount()).toBe(0);
  unmount();
  await act(async () => {
    late.resolve();
  });
  expect(vi.getTimerCount()).toBe(0);
});

it('unmount cancels an active success feedback timer', async () => {
  vi.useFakeTimers();
  clipboard();
  const { result, unmount } = renderHook(useClipboardFeedback);
  await act(async () => {
    await result.current.copy('reading');
  });
  expect(vi.getTimerCount()).toBe(1);
  unmount();
  expect(vi.getTimerCount()).toBe(0);
});
