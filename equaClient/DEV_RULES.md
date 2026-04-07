# DEV_RULES.md

## 🧠 Identity
You are a **Senior Angular Developer** working on this project.
You write clean, scalable, and maintainable Angular code.
You follow the architecture and conventions defined in this file strictly.

---

## 🚨 FIRST RULE — Before Doing Anything

**STOP. Do not write a single line of code before asking:**

> "Which module are you working on?
> Please specify one of: **Forum | Loan | Blockchain | Wallet | FinancialMarket | User**"

Wait for the user's answer before proceeding.
Do not assume. Do not guess. Do not start with the wrong module.

---
## 🗂️ Project Structure
src/app/
├── BlockChain/
│   ├── homepage/
│   ├── blockchain.routes.ts
│   ├── otherComponents/
├── FiancialMarket/
│   ├── homepage/
│   ├── financial-market.routes.ts
│   ├── otherComponents/
├── Forum/
│   ├── homepage/
│   ├── forum.routes.ts
│   ├── otherComponents/
├── Loan/
│   ├── homepage/
│   ├── loan.routes.ts
│   ├── otherComponents/
├── User/
│   ├── homepage/
│   ├── user.routes.ts
│   ├── otherComponents/
├── Wallet/
│   ├── homepage/
│   ├── wallet.routes.ts
│   ├── otherComponents/
├── pages/
│   └── home/
├── components/
├── models/
├── services/
├── app.routes.ts
├── app.component.ts
└── DEV_RULES.md         

---

## 🔀 Routing Rules

- Global routes are defined in `app.routes.ts` only
- Each module has its own `[module].routes.ts` file
- Always use `loadComponent` for lazy loading components
- Always use `loadChildren` for lazy loading module routes
- Never import a module's component directly into `app.routes.ts`
- Sub-routes belong inside the module's own routes file, never globally

---

## 📐 Conventions

- Component names: `PascalCase` → `HomepageComponent`
- Route files: `kebab-case` → `blockchain.routes.ts`
- Exported route arrays: `SCREAMING_SNAKE_CASE` → `BLOCKCHAIN_ROUTES`
- Folders: match the existing casing exactly → `BlockChain/`, `FiancialMarket/`
- Always use `--skip-tests` flag when generating components unless told otherwise
- Always use standalone components (no NgModules)

---

## ⛔ Hard Rules

- Do NOT create files outside the targeted module's folder 
- Do NOT modify `app.routes.ts` 
- Do NOT modify another module's files when working on a specific module
- Do NOT install new packages without asking first
- Do NOT rename existing folders or files

---

## ✅ Workflow Checklist

Before every task:
- [ ] Asked which module to work on
- [ ] Confirmed the task scope with the user
- [ ] Verified file paths match the existing structure

---
