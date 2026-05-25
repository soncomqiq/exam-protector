---
applyTo: "frontend/**"
description: "Frontend instructions for React + TypeScript + Vite in this repository. Use when creating UI, routes, hooks, API clients, and anti-cheat client logic."
---

# Frontend Copilot Instructions

## Scope
These instructions apply to files under `frontend/`.

## Tech Stack
- React 19 + TypeScript
- Vite 8
- ESLint with TypeScript support

## Coding Rules
- Use TypeScript for all new source files.
- Prefer functional React components and hooks.
- Keep components small and composable.
- Do not hardcode API URLs in components; centralize in an API client module.
- Add explicit types for props, API contracts, and complex state.
- Avoid `any`; if unavoidable, isolate and document why.

## Suggested Frontend Structure
When implementing app features, follow this structure:
- `src/api/` for HTTP client and endpoint modules
- `src/components/` for reusable presentational components
- `src/features/` for domain pages and feature-specific UI
- `src/hooks/` for reusable behavior (anti-cheat, heartbeat, autosave)
- `src/store/` for app state management
- `src/utils/` for constants and helpers

## Anti-Cheat Client Guidelines
- Treat tab visibility, fullscreen exit, and screen-share state as first-class events.
- Debounce noisy browser events before sending violations.
- Keep heartbeat interval constants in one place.
- Send client timestamps with every violation event.
- Do not rely only on client checks; assume server re-validates.

## UX Guidelines
- Clearly communicate required permissions (e.g., screen sharing) before exam start.
- Show non-blocking warnings first; escalate to blocking state only when policy requires.
- Preserve answer state frequently (autosave).
- Keep exam timer authoritative to server time when available.

## Quality Checks
Before finalizing frontend changes, prefer running:
- `npm run lint`
- `npm run build`

## Output Expectations
When generating code:
- Include concise comments only for non-obvious logic.
- Keep naming descriptive and consistent.
- Provide minimal but meaningful error handling for network requests.
