import type { Conversion } from '../api';
export type ConversionState = { status: 'idle' } | { status: 'pending' }
  | { status: 'success'; result: string } | { status: 'error'; message: string };
export function useConversion(_convert: Conversion) {
  return { state: { status: 'idle' } as ConversionState,
    run: async (_input: string) => {}, invalidate: () => {} };
}
