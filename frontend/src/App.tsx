import ConverterCard from './components/ConverterCard';
import { toBerlinClockTime } from './api';
import './styles.css';

export default function App() {
  return (
      <main>
        <header><img src="/berlin-clock.jpg" width={40} height={40} alt="" />
          <h1>Berlin Clock converter</h1></header>
        <ConverterCard inputLabel="Digital time" resultLabel="To Berlin Clock time" resultName="Berlin Clock time"
                       initialInput="13:17:01" hint="24-hour, e.g. 13:17:01" allowedCharacters={/^[0-9:]+$/}
                       characterHint="Use only ASCII digits (0–9) and colons (:)." convert={toBerlinClockTime} />
      </main>
  );
}
