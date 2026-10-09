# Berlin Clock converter

A fresh private BerlinClock-4 implementation, developed incrementally from the BerlinClock-3 reference.

## Prerequisites and verification

Use JDK 21 and installed Maven 3.9.x on PATH. Maven is not supplied by a wrapper.

```sh
cd backend
mvn verify
mvn spring-boot:run
```

The backend listens on port 8080. Frontend setup and API contracts will accompany their implementation stages.

See the [glossary](CONTEXT.md) and [reverse seconds decision](docs/adr/0001-berlin-clock-time-to-digital-time-uses-earliest-consistent-second.md).
