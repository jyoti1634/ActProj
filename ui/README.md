# CashBook UI (React + Vite)

This is the standalone frontend for the CashBook app.

Quick start:

1. cd ui
2. npm install
3. npm run dev

Dev notes:
- The Vite dev server proxies `/api` to `http://localhost:8080` (see `vite.config.js`).
- Set `VITE_API_URL` in production to your backend API base (e.g., `https://api.example.com/api/v1`).

MVP pages:
- /login — Login form
- / — Main skeleton with Entry page and ledger table

Next steps:
- Wire actual API calls to ledger endpoints, add tests, and implement other pages.
