import ConverterCard from './components/ConverterCard';
import { toBerlinClockTime, toDigitalTime } from './api';
import './styles.css';

export default function App() {
    return (
        <main>
            <header>
                <img src="/berlin-clock.jpg" width={100} height={100} alt="Berlin Clock" />
                <h1>Berlin Clock Time converter</h1>
            </header>
            <ConverterCard
                inputLabel="Digital time"
                resultLabel="To Berlin Clock time"
                resultName="Berlin Clock time"
                initialInput="13:17:01"
                hint="24-hour, e.g. 13:17:01"
                allowedCharacters={/^[0-9:]+$/}
                characterHint="Use only ASCII digits (0–9) and colons (:)."
                convert={toBerlinClockTime}
            />
            <ConverterCard
                inputLabel="Berlin Clock time"
                resultLabel="To Digital time"
                resultName="Digital time"
                initialInput="ORROORRROYYROOOOOOOOYYOO"
                hint="24 lamps · Y yellow · R red · O off"
                allowedCharacters={/^[YRO]+$/}
                characterHint="Use only uppercase Y, R, and O (the letter O, not zero)."
                convert={toDigitalTime}
                explanation="The clock preserves only seconds parity. Returned seconds are representative 00 or 01 values."
            />
        </main>
    );
}
