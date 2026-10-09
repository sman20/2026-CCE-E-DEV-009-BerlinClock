# Berlin Clock Time to Digital Time uses the earliest consistent second

## Context

The Seconds Lamp preserves seconds parity, not the exact second. Each valid Berlin Clock Time corresponds to 30 Digital Times in the same minute. The kata nevertheless supplies four reverse examples with exact seconds, without a general rule for selecting those seconds.

## Decision

Return `HH:mm:ss`, preserving hours and minutes and selecting second `00` when the Seconds Lamp is on and `01` when it is off. Document that the result uses representative seconds and does not recover the original second.

This preserves the output format and all information present in the input. Returning only `HH:mm` would discard parity and depart from all four example output formats. Special-casing example inputs would provide no defensible general conversion rule.

## Consequences

The published reverse examples resolve as follows under this policy:

| Berlin Clock Time | Published output | Required output under this decision |
| --- | --- | --- |
| `YOOOOOOOOOOOOOOOOOOOOOOO` | `00:00:00` | `00:00:00` |
| `ORRRRRRROYYRYYRYYRYYYYYY` | `23:59:59` | `23:59:01` |
| `YRRROROOOYYRYYRYYRYOOOOO` | `16:50:06` | `16:50:00` |
| `ORROOROOOYYRYYRYOOOOYYOO` | `11:37:01` | `11:37:01` |

The two differing published outputs are compatible with their readings but cannot be uniquely inferred. Tests must assert the exact selected outputs, not just hours, minutes, and parity. The README must disclose these two acceptance-example deviations.

Converting a valid Berlin Clock Time to Digital Time and back preserves the entire Berlin Clock Time. Converting Digital Time to Berlin Clock Time and back preserves hours, minutes, and seconds parity, while normalizing seconds to `00` or `01`.
