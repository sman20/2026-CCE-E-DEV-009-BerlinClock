import { render, screen } from '@testing-library/react';
import { expect, it, vi } from 'vitest';
import App from './App';
it('loads the forward example from the API with accessible controls and heading artwork', async () => {
  vi.stubGlobal(
    'fetch',
    vi
      .fn()
      .mockResolvedValue(
        new Response(
          JSON.stringify({ berlinClockTime: 'ORROORRROYYROOOOOOOOYYOO' }),
          { headers: { 'Content-Type': 'application/json' } },
        ),
      ),
  );
  render(<App />);
  expect(
    screen.getByRole('heading', { name: 'Berlin Clock Time converter' }),
  ).toBeInTheDocument();
  expect(document.querySelector('header img')).toHaveAttribute(
    'src',
    '/berlin-clock.jpg',
  );
  expect(document.querySelector('header img')).toHaveAttribute('alt', 'Berlin Clock');
  expect(
    await screen.findByText('ORROORRROYYROOOOOOOOYYOO'),
  ).toBeInTheDocument();
  expect(fetch).toHaveBeenCalledWith(
    '/api/berlin-clock-time?digitalTime=13%3A17%3A01',
    { signal: expect.any(AbortSignal) },
  );
});
