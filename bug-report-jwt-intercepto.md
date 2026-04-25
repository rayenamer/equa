# Bug Report: JWT Token Not Sent to Backend from Angular

## Summary

API requests from Angular were reaching the Spring Boot backend without the `Authorization: Bearer` header, causing the backend to treat every request as `anonymousUser` and throw a 500 error.

---

## Symptoms

- Swagger requests worked perfectly (token attached manually)
- Angular requests returned 500 with `User not found: anonymousUser`
- Backend logs showed the JWT filter running but finding no authenticated principal
- Browser Network tab showed **no `Authorization` header** on any Angular HTTP requests
- `fetch()` calls with the token manually attached worked fine

---

## Root Cause

**Two conflicting `provideHttpClient()` calls existed in the app bootstrap chain.**

### `main.ts` (the bug)
```typescript
bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient()  // ← bare, no interceptors configured
  ]
});
```

### `app.config.ts` (correct, but overridden)
```typescript
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),  // ← interceptors configured here
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ]
};
```

### What happened

In Angular's standalone bootstrap model, when you pass an inline `providers` array to `bootstrapApplication()` **instead of** your `appConfig` object, Angular creates a **root injector** from those inline providers. The `provideHttpClient()` in `main.ts` registered a bare `HttpClient` instance at the root level with **no interceptors**.

Because `main.ts` providers are registered at the **root injector** level and `app.config.ts` was never passed to `bootstrapApplication()`, the `appConfig` providers were **never registered at all** — including `withInterceptorsFromDi()` and the `AuthInterceptor`.

All services (`ApiService`, `DinarWalletService`, etc.) injected `HttpClient` from the root injector, which was the bare unintercepted instance. Every HTTP request went out with no headers.

### Why it was hard to find

- The `AuthInterceptor` code was correct
- The `app.config.ts` setup was correct
- The token was correctly stored in `localStorage`
- The backend JWT filter logic was correct
- No TypeScript errors were thrown anywhere
- The failure was completely silent — the interceptor simply was never called

---

## Fix

Replace the inline providers in `main.ts` with the existing `appConfig`:

```typescript
// BEFORE (broken)
import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { routes } from './app/app.routes';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient()  // bare, no interceptors
  ]
}).catch(err => console.error(err));
```

```typescript
// AFTER (correct)
import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';

bootstrapApplication(AppComponent, appConfig)
  .catch(err => console.error(err));
```

---

## Key Lesson

In Angular standalone apps, **all providers must live in one place** — either all in `main.ts` or all in `app.config.ts`. Never split them. If you use `app.config.ts`, pass it directly to `bootstrapApplication()` and keep `main.ts` minimal. Duplicating providers across both files causes silent override bugs that are extremely difficult to trace.

---

## Debugging Trail (for reference)

| Step | Finding |
|------|---------|
| Checked backend URLs | Correct |
| Checked backend logs | `anonymousUser` — token never reached backend |
| Checked `AuthInterceptor` | Correct |
| Checked `app.config.ts` | Correct, `withInterceptorsFromDi()` present |
| Checked `HttpClientModule` in components | None found |
| Confirmed token in `localStorage` | Valid, not expired |
| Manual `fetch()` with token | Worked — backend responded correctly |
| Added debug log to interceptor | Never printed — interceptor never called |
| Searched for duplicate `provideHttpClient` | Found in `main.ts` without interceptors |


#########################################################################################################

Here's a plain-language breakdown:

**Standalone** — In older Angular, every component had to be declared inside a `NgModule` (a kind of registry/container). "Standalone" means the component manages its own dependencies directly, no module needed. It's the modern Angular way.

**Bootstrap** — The very first thing Angular does when your app starts. "Bootstrapping" means "launch this component as the root of the entire app." `bootstrapApplication(AppComponent, ...)` is Angular saying: *start here, this is the entry point.*

**`app.config.ts`** — A configuration file where you register all the global services and features your app needs. Think of it as the app's settings file — it's where you say "use this router, use this HTTP client, use these interceptors."

**`providers`** — A list of services and features you're making available to the entire app through Angular's dependency injection system. When a service is "provided," Angular knows how to create it and share it.

**Dependency Injection (DI)** — Instead of a service creating its own dependencies, Angular creates them and hands them in. `ApiService` doesn't create its own `HttpClient` — Angular injects one for it.

**Interceptor** — Middleware that sits between your code and every HTTP request. Every time Angular makes an API call, the interceptor runs first — in our case, to attach the `Authorization` header automatically.

**Root injector** — The top-level container Angular uses to store and share services across the entire app. Whatever is registered here is available everywhere. The problem was `main.ts` was registering a bare `HttpClient` here before `app.config.ts` got a chance to register the correct one with interceptors.

**`provideHttpClient()`** — Registers Angular's HTTP client service. Without `withInterceptorsFromDi()` passed into it, it creates an HTTP client that doesn't know about any interceptors — requests go out naked with no extra headers.

###################################################################################################

When Angular starts, it bootstraps the app using a configuration object that tells it what services are available globally — including the HTTP client and any interceptors. In our case, `main.ts` was passing its own inline `providers` array directly to `bootstrapApplication()` instead of the `appConfig` object, which meant `app.config.ts` was never loaded. So Angular created a bare HTTP client with no interceptors attached, and every API request went out without the `Authorization` header — the backend never saw the token and treated every request as an anonymous stranger.