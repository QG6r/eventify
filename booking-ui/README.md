# Eventify UI

React + TypeScript frontend for the [Eventify](../README.md) event booking service.

## Features

- Registration and login
- Event list with pagination, event details and booking
- "My bookings" and notification settings
- Admin pages: event management (create / edit / delete) and booking confirmation

## Tech stack

React 19, TypeScript, React Router, React Hook Form + Yup, Axios, Tailwind CSS, Lucide icons, React Hot Toast.

## Run locally

```bash
npm ci

npm run start:mocks      # built-in mock API, no backend required
npm run start:backend    # real backend at http://localhost:8080
```

The app starts on http://localhost:3000.

Mock accounts: `user@example.com` / `password123`, `admin@example.com` / `password123`.

## Configuration

| Variable | Meaning |
|---|---|
| `REACT_APP_API_URL` | backend base URL, e.g. `http://localhost:8080` |
| `REACT_APP_USE_MOCKS` | `true` to use the built-in mock API |

If `REACT_APP_API_URL` is not set, the app runs on mock data. See `env.example`.

## Docker

The root `docker-compose.yml` of the repository builds this app together with the backend and the database.
To run only the frontend in a container:

```bash
docker compose --profile docker-mocks up --build     # with mock data
docker compose --profile docker-backend up --build   # with a backend on the host, port 8080
```

## Scripts

```bash
npm test -- --watchAll=false   # tests
npm run build                  # production build
```
