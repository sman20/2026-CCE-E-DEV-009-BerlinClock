import { describe, expect, it, vi } from 'vitest';
import { toBerlinClockTime, toDigitalTime } from './api';
const signal = () => new AbortController().signal;
const fallback = 'Conversion failed. Please try again.';
function reply(body: unknown, status = 200, contentType = 'application/json') {
  vi.stubGlobal(
    'fetch',
    vi.fn().mockResolvedValue(
      new Response(JSON.stringify(body), {
        status,
        headers: { 'Content-Type': contentType },
      }),
    ),
  );
}
describe('forward API client', () => {
  it('encodes the unchanged input and passes its signal', async () => {
    reply({ berlinClockTime: 'YOOOOOOOOOOOOOOOOOOOOOOO' });
    const ownSignal = signal();
    expect(await toBerlinClockTime(' 12:32:00+\n', ownSignal)).toBe(
      'YOOOOOOOOOOOOOOOOOOOOOOO',
    );
    expect(fetch).toHaveBeenCalledWith(
      '/api/berlin-clock-time?digitalTime=+12%3A32%3A00%2B%0A',
      { signal: ownSignal },
    );
  });
  it.each([
    null,
    [],
    {},
    { berlinClockTime: 24 },
    { berlinClockTime: '' },
    { berlinClockTime: '   ' },
    { digitalTime: '12:32:00' },
  ])('rejects an invalid successful body %j', async (body) => {
    reply(body);
    await expect(toBerlinClockTime('12:32:00', signal())).rejects.toThrow(
      fallback,
    );
  });
  it('shows useful detail from a valid 400 ProblemDetail', async () => {
    reply(
      {
        type: 'urn:berlin-clock:invalid-input',
        title: 'Bad Request',
        status: 400,
        detail: 'Hours must be 00–23.',
      },
      400,
      'application/problem+json',
    );
    await expect(toBerlinClockTime('24:00:00', signal())).rejects.toThrow(
      'Hours must be 00–23.',
    );
  });
  it.each([
    [
      500,
      'application/problem+json',
      {
        type: 'about:blank',
        title: 'Error',
        status: 500,
        detail: 'Secret server detail',
      },
    ],
    [
      400,
      'application/json',
      {
        type: 'about:blank',
        title: 'Bad Request',
        status: 400,
        detail: 'Untrusted detail',
      },
    ],
    [400, 'application/problem+json', { detail: 'Incomplete problem' }],
    [
      400,
      'application/problem+json',
      {
        type: 'about:blank',
        title: 'Bad Request',
        status: 500,
        detail: 'Wrong status',
      },
    ],
    [
      400,
      'application/problem+json',
      { type: 'about:blank', title: 'Bad Request', status: 400, detail: '' },
    ],
    [200, 'text/html', { berlinClockTime: 'unexpected' }],
  ])(
    'uses safe fallback for unexpected response %i %s',
    async (status, contentType, body) => {
      reply(body, status, contentType);
      await expect(toBerlinClockTime('12:32:00', signal())).rejects.toThrow(
        fallback,
      );
    },
  );
  it('handles unreadable JSON', async () => {
    vi.stubGlobal(
        'fetch',
        vi.fn().mockResolvedValue(
            new Response('<html lang="en">', {
              headers: { 'Content-Type': 'application/json' },
            }),
        ),
    );
    await expect(toBerlinClockTime('12:32:00', signal())).rejects.toThrow(
      fallback,
    );
  });
  it('distinguishes a network failure', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockRejectedValue(new TypeError('Failed to fetch')),
    );
    await expect(toBerlinClockTime('12:32:00', signal())).rejects.toThrow(
      'Unable to reach the converter. Please try again.',
    );
  });
  it('preserves cancellation without a network message', async () => {
    const controller = new AbortController();
    controller.abort();
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(controller.signal.reason));
    await expect(
      toBerlinClockTime('12:32:00', controller.signal),
    ).rejects.toMatchObject({ name: 'AbortError' });
  });
});

it('reverse API encodes exact supplied text and checks its own response field', async () => {
  const mock = vi.fn().mockResolvedValue(new Response(
      JSON.stringify({ digitalTime: '13:17:01' }),
      { headers: { 'Content-Type': 'application/json' } },
  ));
  vi.stubGlobal('fetch', mock);
  const signal = new AbortController().signal;
  expect(await toDigitalTime(' YRO+\n', signal)).toBe('13:17:01');
  expect(mock).toHaveBeenCalledWith(
      '/api/digital-time?berlinClockTime=+YRO%2B%0A',
      { signal },
  );
  mock.mockResolvedValueOnce(new Response(JSON.stringify({ berlinClockTime: 'ORROORRROYYROOOOOOOOYYOO' }),
      { headers: { 'Content-Type': 'application/json' } }));
  await expect(toDigitalTime('ORROORRROYYROOOOOOOOYYOO', signal)).rejects.toThrow(
      'Conversion failed. Please try again.',
  );
});
