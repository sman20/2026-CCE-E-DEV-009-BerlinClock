import type { Conversion } from '../api';

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
export default function ConverterCard(_props: ConverterCardProps) {
  return <form aria-label="Digital time to Berlin Clock time"><label htmlFor="time">Digital time</label>
    <input id="time" /><output aria-label="To Berlin Clock time">—</output></form>;
}
