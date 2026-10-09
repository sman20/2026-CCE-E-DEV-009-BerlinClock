# Berlin Clock converter

## Build and verify from a clean checkout

```sh
cd backend
mvn verify
cd ../frontend
npm ci
npm run verify
```

## Run locally

Terminal 1:

```sh
cd backend
mvn spring-boot:run
```

Terminal 2:

```sh
cd frontend
npm ci
npm run dev
```

Open **http://localhost:5173**. The backend listens on **8080**; Vite proxies `/api` to `http://localhost:8080`. 
Port conflicts fail visibly; Vite does not silently switch ports. These commands run the development application. 

The backend listens on port 8080. Frontend setup and API contracts will accompany their implementation stages.

See the [glossary](CONTEXT.md) and [reverse seconds decision](docs/adr/0001-berlin-clock-time-to-digital-time-uses-earliest-consistent-second.md).
