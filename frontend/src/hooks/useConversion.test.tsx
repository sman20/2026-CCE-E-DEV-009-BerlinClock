import { StrictMode, type PropsWithChildren } from 'react';
import { act, renderHook, waitFor } from '@testing-library/react';
import { expect, it, vi } from 'vitest';
import { useConversion } from './useConversion';
import { deferred } from '../test/deferred';
it('owns pending, success and failure transitions', async () => {
  const request = deferred<string>();
  const convert = vi
    .fn()
    .mockReturnValueOnce(request.promise)
    .mockRejectedValueOnce(new Error('Invalid time'));
  const { result } = renderHook(() => useConversion(convert));
  expect(result.current.state.status).toBe('idle');
  act(() => {
    void result.current.run('13:17:01');
  });
  expect(result.current.state).toEqual({ status: 'pending' });
  await act(async () => {
    request.resolve('reading');
  });
  expect(result.current.state).toEqual({
    status: 'success',
    result: 'reading',
  });
  await act(async () => {
    await result.current.run('24:00:00');
  });
  expect(result.current.state).toEqual({
    status: 'error',
    message: 'Invalid time',
  });
});
it('aborts replacement and ignores late success even if a fake ignores cancellation', async () => {
  const first = deferred<string>();
  const second = deferred<string>();
  const convert = vi
    .fn()
    .mockReturnValueOnce(first.promise)
    .mockReturnValueOnce(second.promise);
  const { result } = renderHook(() => useConversion(convert));
  act(() => {
    void result.current.run('first');
  });
  const oldSignal = convert.mock.calls[0][1] as AbortSignal;
  act(() => {
    void result.current.run('second');
  });
  expect(oldSignal.aborted).toBe(true);
  await act(async () => {
    first.resolve('stale');
  });
  expect(result.current.state.status).toBe('pending');
  await act(async () => {
    second.resolve('current');
  });
  expect(result.current.state).toEqual({
    status: 'success',
    result: 'current',
  });
});
it('invalidation clears output and suppresses late rejection', async () => {
  const pending = deferred<string>();
  const convert = vi.fn().mockReturnValue(pending.promise);
  const { result } = renderHook(() => useConversion(convert));
  act(() => {
    void result.current.run('old');
    result.current.invalidate();
  });
  expect(convert.mock.calls[0][1].aborted).toBe(true);
  await act(async () => {
    pending.reject(new Error('late'));
  });
  expect(result.current.state).toEqual({ status: 'idle' });
});
it('cleans up under StrictMode and accepts a replacement request', async () => {
  const requests: ReturnType<typeof deferred<string>>[] = [];
  const signals: AbortSignal[] = [];
  const convert = vi.fn((_input: string, signal: AbortSignal) => {
    signals.push(signal);
    const next = deferred<string>();
    requests.push(next);
    return next.promise;
  });
  function wrapper({ children }: PropsWithChildren) {
    return <StrictMode>{children}</StrictMode>;
  }
  const { result, unmount } = renderHook(() => useConversion(convert), {
    wrapper,
  });
  act(() => {
    void result.current.run('current');
  });
  await act(async () => {
    requests.at(-1)!.resolve('current');
  });
  await waitFor(() => expect(result.current.state.status).toBe('success'));
  act(() => {
    void result.current.run('unmount');
  });
  unmount();
  expect(signals.at(-1)!.aborted).toBe(true);
  await act(async () => {
    requests.at(-1)!.resolve('late');
  });
});
