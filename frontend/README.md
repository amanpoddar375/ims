# Post Anything Frontend

## Tech Stack
- Angular 17 (standalone components)
- Angular Router, HttpClient
- Reactive Forms
- SCSS styling

## Run
```bash
npm install
npm start
```
App runs at http://localhost:4200 (proxying direct to backend at http://localhost:8080/api).

## Build
```bash
npm run build
```
Output in `dist/post-anything`.

## Testing
```bash
npm test
```
(Runs Karma/Jasmine).

## Configuration
- API base: `src/environments/environment*.ts` (`api: 'http://localhost:8080/api'`).

## Features
- Login page with Basic Auth (header stored in localStorage)
- Post list/detail, create form, status close from detail
- Admin dashboard view of all posts
- Auth guard + HTTP interceptor attaching Basic Auth header

## Structure
- `app.routes.ts` routes
- `core/` guards, interceptors, services (auth/post/error)
- `models/` shared interfaces
- `features/` auth, posts, admin components
- `shared/components/` navigation bar
- `styles.scss` global styles (Inter font, light theme)
