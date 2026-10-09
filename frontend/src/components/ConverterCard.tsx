import { useCallback, useEffect, useId, useState } from 'react';
import { ArrowRightLeft, Check, Copy } from 'lucide-react';
import type { Conversion } from '../api';
import { useConversion } from '../hooks/useConversion';
import { useClipboardFeedback } from '../hooks/useClipboardFeedback';
import IconButton from './IconButton';

export type ConverterCardProps = {
  inputLabel: string;
  resultLabel: string;
  resultName: string;
  initialInput: string;
  hint: string;
  allowedCharacters: RegExp;
  characterHint: string;
  convert: Conversion;
  explanation?: string;
};

export default function ConverterCard({
                                        inputLabel,
                                        resultLabel,
                                        resultName,
                                        initialInput,
                                        hint,
                                        allowedCharacters,
                                        characterHint,
                                        convert,
                                        explanation,
                                      }: ConverterCardProps) {
  const id = useId();
  const [input, setInput] = useState(initialInput);
  const { state, run, invalidate } = useConversion(convert);
  const { feedback, reset, copy } = useClipboardFeedback();
  const pending = state.status === 'pending';
  const result = state.status === 'success' ? state.result : null;
  const error = state.status === 'error' ? state.message : '';
  const request = useCallback(
      (value: string) => {
        reset();
        void run(value);
      },
      [reset, run],
  );
  useEffect(() => {
    request(initialInput);
  }, [initialInput, request]);
  const clientError =
      input.length === 0
          ? `Enter ${inputLabel.toLowerCase()}.`
          : !allowedCharacters.test(input)
              ? characterHint
              : '';
  const validation = clientError || error;
  function edit(value: string) {
    invalidate();
    reset();
    setInput(value);
  }
  return (
      <form
          className="converter-card"
          aria-label={`${inputLabel} to ${resultName}`}
          aria-busy={pending}
          onSubmit={(event) => {
            event.preventDefault();
            if (!pending && !clientError) {
              request(input);
            }
          }}
      >
        <div className="card-section">
          <label className="field-label" htmlFor={`${id}-input`}>
            {inputLabel}
          </label>
          <div className="control-row">
            <input
                id={`${id}-input`}
                type="text"
                value={input}
                disabled={pending}
                onChange={(event) => edit(event.target.value)}
                aria-invalid={Boolean(validation)}
                aria-describedby={validation ? `${id}-hint ${id}-error` : `${id}-hint`}
                autoComplete="off"
                autoCapitalize="none"
                spellCheck={false}
            />
            <IconButton
                type="submit"
                variant="convert"
                label={`Convert to ${resultName}`}
                disabled={pending || Boolean(clientError)}
            >
              <ArrowRightLeft aria-hidden="true" size={25} />
            </IconButton>
          </div>
          <p id={`${id}-hint`} className="hint">{hint}</p>
          {pending && <p className="status" role="status">Converting…</p>}
          <p id={`${id}-error`} className="error" role="alert" hidden={!validation}>
            {validation}
          </p>
        </div>
        <div className="card-section">
          <span className="field-label" id={`${id}-result-label`}>{resultLabel}</span>
          <div className="control-row">
            <output
                tabIndex={0}
                aria-labelledby={`${id}-result-label`}
                aria-live="polite"
                aria-atomic="true"
                aria-describedby={explanation ? `${id}-explanation` : undefined}
            >
              {result ?? '—'}
            </output>
            <IconButton
                label={`Copy ${resultName}`}
                disabled={result === null || pending}
                onClick={() => {
                  if (result !== null) {
                    void copy(result);
                  }
                }}
            >
              {feedback === 'Copied'
                  ? <Check aria-hidden="true" size={24} />
                  : <Copy aria-hidden="true" size={24} />}
            </IconButton>
          </div>
          {explanation && (
              <p id={`${id}-explanation`} className="explanation">{explanation}</p>
          )}
          {feedback && (
              <p role="status" className={feedback === 'Copied' ? 'status' : 'error'}>
                {feedback}
              </p>
          )}
        </div>
      </form>
  );
}
