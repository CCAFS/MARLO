# Report Orchestrator (Lambda)

Standalone AWS Lambda service to replace MARLO as the report generation orchestrator before platform sunset.

**Constraint:** No dedicated project budget — processing must avoid new AWS charges where possible.

**Living design document:** [analysis.md](./analysis.md)

## Status

OICR report — full JSON field parity with MARLO (local Express + Lambda handler scaffold).

## Quick start (local)

```bash
cd report-orchestrator
npm install
cp .env.example .env   # if .env does not exist — edit DB and MQ credentials
npm run dev
```

Health check:

```bash
curl http://localhost:3000/health
```

Generate OICR payload (dry run — default in `.env`):

```bash
curl -X POST http://localhost:3000/reports/oicr \
  -H "Content-Type: application/json" \
  -d "{\"studyId\": 3517, \"phaseId\": 407}"
```

Full pipeline (set `REPORT_DRY_RUN=false` and valid MQ credentials):

```bash
curl -X POST http://localhost:3000/reports/oicr \
  -H "Content-Type: application/json" \
  -d "{\"studyId\": 3517, \"phaseId\": 407, \"dryRun\": false}"
```

## Scripts

| Script | Description |
|---|---|
| `npm run dev` | Local Express server with hot reload (`tsx watch`) |
| `npm run build` | Compile TypeScript + bundle Lambda handler |
| `npm run typecheck` | Type check only |

## Environment

See [.env.example](./.env.example). Key flags for local testing:

| Variable | Purpose |
|---|---|
| `REPORT_DRY_RUN=true` | Build JSON only — skip MQ and S3 |
| `REPORT_SKIP_S3_POLL=true` | Publish to queue but do not wait for PDF |

## Lambda (deploy later)

Entry point: `src/lambda.ts` → bundled to `dist/lambda.js` via `npm run build`.

Use **Lambda Function URL** (no API Gateway cost). VPC + S3 Gateway Endpoint required for production.

## Scope

- Read report data from the MARLO MySQL database
- Build the `pdf.generate` payload (templates + entity data)
- Publish to the existing reporting microservice queue (Amazon MQ)
- Retrieve the generated PDF from S3 and return it to the caller

## Out of scope (for now)

- Full OICR field parity with MARLO `BaseStudySummaryData.generateAndSendJson()` (geography, contributors, innovations, quantification, alliance/SDG, cross-cutting markers, etc.)
- Innovation and cluster reports
- AWS deployment / SAM / Serverless config
- Replacing the external PDF rendering microservice
