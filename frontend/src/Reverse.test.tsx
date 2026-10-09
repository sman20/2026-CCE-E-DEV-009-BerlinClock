import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { expect, it, vi } from 'vitest';
import App from './App';
import { deferred } from './test/deferred';
const reading = 'ORROORRROYYROOOOOOOOYYOO';
const json = (body: unknown, status = 200, contentType = 'application/json') =>
  new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': contentType },
  });
function server() {
  const mock = vi.fn((url: string) =>
    Promise.resolve(
      url.startsWith('/api/digital-time')
        ? json({ digitalTime: '13:17:01' })
        : json({ berlinClockTime: reading }),
    ),
  );
  vi.stubGlobal('fetch', mock);
  return mock;
}

it('loads both samples independently and explains representative seconds', async () => {
  const mock = server();
  render(<App />);
  expect(
    await screen.findByText('13:17:01', { selector: 'output' }),
  ).toBeInTheDocument();
  expect(screen.getByLabelText('Berlin Clock time')).toHaveValue(reading);
  expect(
    screen.getByText(/Returned seconds are representative 00 or 01/),
  ).toBeInTheDocument();
  expect(screen.getByLabelText('To Digital time')).toHaveAccessibleDescription(
    /preserves only seconds parity/,
  );
  expect(mock).toHaveBeenCalledWith(
    `/api/digital-time?berlinClockTime=${reading}`,
    { signal: expect.any(AbortSignal) },
  );
  expect(mock).toHaveBeenCalledWith(
    '/api/berlin-clock-time?digitalTime=13%3A17%3A01',
    { signal: expect.any(AbortSignal) },
  );
});

it('keeps the forward card usable while reverse is pending and sends all permitted text unchanged', async () => {
  const user = userEvent.setup();
  const next = deferred<Response>();
  const mock = server();
  render(<App />);
  const reverse = await screen.findByRole('form', {
    name: 'Berlin Clock time to Digital time',
  });
  const reverseInput = within(reverse).getByLabelText('Berlin Clock time');
  await waitFor(() => expect(reverseInput).toBeEnabled());
  await user.clear(reverseInput);
  await user.type(reverseInput, 'Y'.repeat(25));
  mock.mockReturnValueOnce(next.promise);
  await user.click(
    within(reverse).getByRole('button', { name: 'Convert to Digital time' }),
  );
  expect(mock).toHaveBeenLastCalledWith(
    `/api/digital-time?berlinClockTime=${'Y'.repeat(25)}`,
    { signal: expect.any(AbortSignal) },
  );
  expect(reverseInput).toBeDisabled();
  expect(within(reverse).getByLabelText('To Digital time')).toHaveTextContent(
    '—',
  );
  expect(screen.getByLabelText('Digital time')).toBeEnabled();
  await user.click(
    screen.getByRole('button', { name: 'Convert to Berlin Clock time' }),
  );
  await waitFor(() =>
    expect(
      screen.getByRole('button', { name: 'Copy Berlin Clock time' }),
    ).toBeEnabled(),
  );
  await act(async () => {
    next.resolve(
      json(
        {
          type: 'urn:berlin-clock:invalid-input',
          title: 'Bad Request',
          status: 400,
          detail: 'Exactly 24 lamps required.',
        },
        400,
        'application/problem+json',
      ),
    );
  });
  expect(within(reverse).getByRole('alert')).toHaveTextContent(
    'Exactly 24 lamps required.',
  );
  expect(reverseInput).toBeEnabled();
});

it('retains lowercase and digit zero, and displays complementary character validation', async () => {
  const user = userEvent.setup();
  server();
  render(<App />);
  const input = screen.getByLabelText('Berlin Clock time');
  await waitFor(() => expect(input).toBeEnabled());
  await user.clear(input);
  await user.type(input, 'yr0');
  expect(input).toHaveValue('yr0');
  expect(input).toHaveAttribute('aria-invalid', 'true');
  expect(
    screen.getByRole('button', { name: 'Convert to Digital time' }),
  ).toBeDisabled();
  expect(screen.getByRole('alert')).toHaveTextContent(
    'Use only uppercase Y, R, and O',
  );
  expect(screen.getByLabelText('To Digital time')).toHaveTextContent('—');
});
