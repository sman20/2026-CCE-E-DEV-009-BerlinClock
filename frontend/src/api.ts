export type Conversion = (input: string, signal: AbortSignal) => Promise<string>;

export const toBerlinClockTime: Conversion = async () => '';
