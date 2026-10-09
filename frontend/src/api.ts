export type Conversion = (input: string, signal: AbortSignal) => Promise<string>;
const failure = 'Conversion failed. Please try again.';

function object(value: unknown): value is Record<string, unknown> {
    return value !== null && typeof value === 'object' && !Array.isArray(value);
}

function nonempty(value: unknown): value is string {
    return typeof value === 'string' && value.trim().length > 0;
}

async function request(path: string, field: string, signal: AbortSignal): Promise<string> {
    signal.throwIfAborted();
    let response: Response;
    try {
        response = await fetch(path, { signal });
    } catch (cause) {
        signal.throwIfAborted();
        throw new Error('Unable to reach the converter. Please try again.', { cause });
    }
    signal.throwIfAborted();
    let body: unknown;
    try {
        body = await response.json();
    } catch {
        signal.throwIfAborted();
        throw new Error(failure);
    }
    signal.throwIfAborted();
    const contentType = response.headers.get('Content-Type')?.split(';')[0].trim().toLowerCase();
    if (response.status === 400 && contentType === 'application/problem+json' && object(body)
        && nonempty(body.type) && nonempty(body.title) && body.status === 400 && nonempty(body.detail)) {
        throw new Error(body.detail);
    }
    if (response.status !== 200 || contentType !== 'application/json' || !object(body) || !nonempty(body[field])) {
        throw new Error(failure);
    }
    return body[field];
}

export const toBerlinClockTime: Conversion = (input, signal) => request(
    `/api/berlin-clock-time?${new URLSearchParams({ digitalTime: input })}`, 'berlinClockTime', signal);

export const toDigitalTime: Conversion = (input, signal) => request(
    `/api/digital-time?${new URLSearchParams({ berlinClockTime: input })}`, 'digitalTime', signal);
