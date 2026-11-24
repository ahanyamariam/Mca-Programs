# Lab 2: Basic CI Workflow with GitHub Actions

A small Node.js calculator project with a CI workflow that runs on every commit.

Workflow file: `.github/workflows/lab-2-ci.yml` (at the repo root, since GitHub only reads workflows from there).

## Pipeline

```
push / pull_request -> lint -> test (Node 20, 22) -> build -> upload artifact
```

| Stage | Command | What it does |
|-------|---------|--------------|
| Lint  | `npm run lint`  | Syntax checks every .js file |
| Test  | `npm test`      | Runs the tests on Node 20 and 22 |
| Build | `npm run build` | Copies src/ to dist/ and uploads it as an artifact |

Each job needs the previous one, so if lint or tests fail the build is skipped.
The workflow only runs when something in this folder or the workflow file changes.

## Run locally

```sh
npm run lint && npm test && npm run build
```
