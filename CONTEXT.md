# Berlin Clock

Converts between Digital Time and Berlin Clock Time - the reading of a Berlin Clock.

## Language

### The clock

**Berlin Clock**:
A clock that shows the time of day with 24 coloured Lamps: a Seconds Lamp above four rows.
_Avoid_: Berlin Uhr, Mengenlehreuhr, Mengenlehreclock

**Lamp**:
One light of the Berlin Clock with a fixed colour, yellow or red, that is either on or off.
_Avoid_: light, block, bulb

**Seconds Lamp**:
The yellow Lamp at the top; on for an even second, off for an odd second.
_Avoid_: blinker, seconds row

**Five Hours Row**:
Four red Lamps, each worth five hours.
_Avoid_: first row, top row, hours row

**Single Hours Row**:
Four red Lamps, each worth one hour.
_Avoid_: second row, hours row

**Five Minutes Row**:
Eleven Lamps, each worth five minutes; the 3rd, 6th and 9th are red (quarter hours), the rest yellow.
_Avoid_: third row, minutes row

**Single Minutes Row**:
Four yellow Lamps, each worth one minute.
_Avoid_: bottom row, minutes row

### Times

**Digital Time**:
A time of day from `00:00:00` to `23:59:59`, written as exactly eight characters in `HH:mm:ss` form with two ASCII digits per part and no surrounding whitespace.
_Avoid_: normal time, time string

**Berlin Clock Time**:
A state the Berlin Clock can show, written as exactly 24 uppercase characters with no surrounding whitespace - `Y` (yellow, on), `R` (red, on), `O` (off) - in the order Seconds Lamp, Five Hours Row, Single Hours Row, Five Minutes Row, Single Minutes Row. Each illuminated Lamp uses its fixed colour; within each row the Lamps that are on come first, and the hours never exceed 23.
_Avoid_: Berlin Time (that means the time in Berlin), clock string, Berlin representation, Berlin Clock (that is the clock itself)
