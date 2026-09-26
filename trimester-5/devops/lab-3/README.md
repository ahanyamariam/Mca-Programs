# Lab 3 – Basic CI with GitHub Actions

A commit-triggered CI pipeline for a small dependency-free Node.js project (a calculator module).

## Pipeline design

Workflow file: [`.github/workflows/lab-3-ci.yml`](../../../.github/workflows/lab-3-ci.yml)
(it lives at the repository root because GitHub only reads workflows from there).

```
push / pull_request ──> lint ──> test (Node 20, Node 22) ──> build ──> upload artifact
```

| Stage | Command | Purpose |
|-------|---------|---------|
| Lint  | `npm run lint`  | Syntax-checks every `.js` file (`node --check`) – fast feedback first |
| Test  | `npm test`      | Runs the `node:test` suite on a Node 20/22 matrix |
| Build | `npm run build` | Copies `src/` to `dist/` and uploads it as the `lab-3-dist` artifact |

Each stage `needs` the previous one, so a broken commit fails early and never reaches the build.

### Triggers
- **push** to any branch – every commit runs CI
- **pull_request** targeting `main`
- **workflow_dispatch** – manual run from the Actions tab
- A `paths` filter means only changes under `trimester-5/devops/lab-3/` (or the workflow itself) trigger it, since the repo holds many unrelated programs.

### Other choices
- `concurrency` cancels an in-progress run when a newer commit lands on the same ref.
- `permissions: contents: read` – least privilege for the token.
- `defaults.run.working-directory` points steps at this lab's folder.

## Run locally

```sh
npm run lint && npm test && npm run build
```

## See it work
1. Commit and push to GitHub, then open the repo's **Actions** tab.
2. To see a failure, break a test in `test/calculator.test.js`, push, and watch `Test` go red and `Build` get skipped.
