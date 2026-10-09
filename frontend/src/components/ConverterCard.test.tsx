import { StrictMode } from 'react';
import {
  act,
  fireEvent,
  render,
  screen,
  waitFor,
} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { expect, it, vi } from 'vitest';
import ConverterCard, { type ConverterCardProps } from './ConverterCard';
import { deferred } from '../test/deferred';
const reading = 'ORROORRROYYROOOOOOOOYYOO';
const props: ConverterCardProps = {
  inputLabel: 'Digital time',
  resultLabel: 'To Berlin Clock time',
  resultName: 'Berlin Clock time',
  initialInput: '13:17:01',
  hint: '24-hour, e.g. 13:17:01',
  allowedCharacters: /^[0-9:]+$/,
  characterHint: 'Use only ASCII digits (0–9) and colons (:).',
  convert: vi.fn(),
};
const convertButton = () =>
  screen.getByRole('button', { name: 'Convert to Berlin Clock time' });
const copyButton = () =>
  screen.getByRole('button', { name: 'Copy Berlin Clock time' });
const input = () => screen.getByLabelText('Digital time');
const output = () => screen.getByLabelText('To Berlin Clock time');
it('requests its sample and disables controls until a result arrives', async () => {
  const next = deferred<string>();
  const convert = vi.fn().mockReturnValue(next.promise);
  render(<ConverterCard {...props} convert={convert} />);
  expect(convert).toHaveBeenCalledWith('13:17:01', expect.any(AbortSignal));
  expect(input()).toBeDisabled();
  expect(convertButton()).toBeDisabled();
  expect(copyButton()).toBeDisabled();
  expect(output()).toHaveTextContent('—');
  await act(async () => {
    next.resolve(reading);
  });
  expect(input()).toBeEnabled();
  expect(copyButton()).toBeEnabled();
  expect(output()).toHaveTextContent(reading);
  expect(input()).toHaveAccessibleDescription('24-hour, e.g. 13:17:01');
  expect(output()).toHaveAttribute('tabindex', '0');
});
it('Enter and the button use the same handler, sending incomplete permitted input unchanged', async () => {
  const user = userEvent.setup();
  const next = deferred<string>();
  const convert = vi
    .fn()
    .mockResolvedValueOnce(reading)
    .mockReturnValueOnce(next.promise)
    .mockResolvedValueOnce('new result');
  render(<ConverterCard {...props} convert={convert} />);
  await waitFor(() => expect(input()).toBeEnabled());
  await user.clear(input());
  await user.type(input(), '12:32');
  expect(output()).toHaveTextContent('—');
  await user.keyboard('{Enter}');
  expect(convert).toHaveBeenLastCalledWith('12:32', expect.any(AbortSignal));
  expect(output()).toHaveTextContent('—');
  fireEvent.submit(screen.getByRole('form'));
  expect(convert).toHaveBeenCalledTimes(2);
  await act(async () => {
    next.resolve('second result');
  });
  await user.click(convertButton());
  expect(convert).toHaveBeenCalledTimes(3);
});
it.each(['', ' 12:32:00', '１２:３２:００', '12:32:00x'])(
  'keeps invalid characters %j and links the error',
  async (value) => {
    const user = userEvent.setup();
    const convert = vi.fn().mockResolvedValue(reading);
    render(<ConverterCard {...props} convert={convert} />);
    await waitFor(() => expect(input()).toBeEnabled());
    await user.clear(input());
    if (value) await user.type(input(), value);
    expect(input()).toHaveValue(value);
    expect(input()).toHaveAttribute('aria-invalid', 'true');
    expect(convertButton()).toBeDisabled();
    expect(copyButton()).toBeDisabled();
    expect(screen.getByRole('alert')).toHaveTextContent(
      value ? props.characterHint : 'Enter digital time.',
    );
    expect(input()).not.toHaveAttribute('maxlength');
  },
);
it('clears failures on editing and permits retry after errors', async () => {
  const user = userEvent.setup();
  const convert = vi
    .fn()
    .mockRejectedValueOnce(new Error('Time must be valid'))
    .mockResolvedValueOnce(reading);
  render(<ConverterCard {...props} convert={convert} />);
  await screen.findByText('Time must be valid');
  expect(convertButton()).toBeEnabled();
  expect(copyButton()).toBeDisabled();
  await user.clear(input());
  await user.type(input(), '13:17:01');
  expect(screen.queryByText('Time must be valid')).not.toBeInTheDocument();
  await user.click(convertButton());
  await waitFor(() => expect(output()).toHaveTextContent(reading));
});
it('discards late results after a deliberate input invalidation', async () => {
  const next = deferred<string>();
  const convert = vi.fn().mockReturnValue(next.promise);
  render(<ConverterCard {...props} convert={convert} />);
  fireEvent.change(input(), { target: { value: '12:32:00' } });
  expect(convert.mock.calls[0][1].aborted).toBe(true);
  await act(async () => {
    next.resolve('stale');
  });
  expect(output()).toHaveTextContent('—');
  expect(input()).toHaveValue('12:32:00');
});
it('copies the exact current result and clears feedback on edit and submission', async () => {
  const user = userEvent.setup();
  const writeText = vi.fn().mockResolvedValue(undefined);
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText },
  });
  render(
    <ConverterCard {...props} convert={vi.fn().mockResolvedValue(reading)} />,
  );
  await waitFor(() => expect(copyButton()).toBeEnabled());
  await user.click(copyButton());
  expect(writeText).toHaveBeenCalledWith(reading);
  await screen.findByText('Copied');
  await user.click(convertButton());
  await waitFor(() => expect(copyButton()).toBeEnabled());
  expect(screen.queryByText('Copied')).not.toBeInTheDocument();
  await user.click(copyButton());
  await screen.findByText('Copied');
  await user.clear(input());
  expect(screen.queryByText('Copied')).not.toBeInTheDocument();
  expect(copyButton()).toBeDisabled();
});
it('the StrictMode replacement sample ignores the aborted request', async () => {
  const requests: ReturnType<typeof deferred<string>>[] = [];
  const signals: AbortSignal[] = [];
  const convert = vi.fn((_value: string, signal: AbortSignal) => {
    signals.push(signal);
    const request = deferred<string>();
    requests.push(request);
    return request.promise;
  });
  const { unmount } = render(
    <StrictMode>
      <ConverterCard {...props} convert={convert} />
    </StrictMode>,
  );
  expect(signals.some((signal) => signal.aborted)).toBe(true);
  await act(async () => {
    requests
      .filter((_r, i) => signals[i].aborted)
      .forEach((r) => r.resolve('stale'));
  });
  expect(output()).toHaveTextContent('—');
  await act(async () => {
    requests.at(-1)!.resolve(reading);
  });
  expect(output()).toHaveTextContent(reading);
  unmount();
  expect(signals.every((signal) => signal.aborted)).toBe(true);
});
