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

### Browser PDF (MARLO-compatible URL)

Open in the browser — same query shape as MARLO `studySummary.do`. Requires `REPORT_DRY_RUN=false` and valid MQ credentials.

```
http://localhost:3000/projects/AICCRA/studySummary.do?studyID=3589&cycle=Reporting&year=2025
http://localhost:3000/projects/AICCRA/studySummary.do?studyID=3589&phaseID=407
```

The server publishes to the microservice queue, polls S3, and streams the PDF inline (`Content-Type: application/pdf`).

On Lambda (Function URL), replace the host:

```
https://<lambda-function-url>/projects/AICCRA/studySummary.do?studyID=3589&phaseID=407
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

## Lambda deployment (Jenkins)

Entry point: `src/lambda.ts` → bundled to `dist/lambda.js` via `npm run build`.

| Artifact | Purpose |
|---|---|
| `Jenkinsfile` | CI/CD pipeline (ZIP or ECR deploy + Secrets Manager env sync) |
| `scripts/package-lambda.mjs` | Copies `dist/lambda.js` into `lambda-package/` |
| `scripts/sync-lambda-env-from-secret.sh` | Maps AWS secret JSON → Lambda environment variables |
| `Dockerfile` | Container image path when package size triggers ECR mode |

### Jenkins job configuration

Set these **environment variables** on the Jenkins job (or folder):

| Variable | Example | Notes |
|---|---|---|
| `LAMBDA_FUNCTION_NAME` | `marlo-report-orchestrator-dev` | Target Lambda |
| `AWS_SECRET_NAME` | `marlo/report-orchestrator/dev` | JSON secret (see below) |
| `AWS_REGION` | `us-east-1` | |
| `AWS_CREDENTIALS_ID` | `prms-test-aws-creds` | Jenkins AWS credential id |
| `ENVIRONMENT_LABEL` | `DEV` | Slack label only |
| `ECR_REPO` | `marlo-report-orchestrator` | ECR path when `DEPLOY_MODE=ECR` |

Build parameter `AWS_SECRET_NAME` can override the job default. When `SYNC_SECRETS=true`, the pipeline runs `sync-lambda-env-from-secret.sh` after code deploy.

### AWS Secrets Manager JSON shape

Keys must match `.env.example` / `loadConfig()`:

```json
{
  "NODE_ENV": "production",
  "DB_HOST": "your-rds-host",
  "DB_PORT": "3306",
  "DB_NAME": "aiccra",
  "DB_USER": "app_user",
  "DB_PASSWORD": "***",
  "MQ_URL": "amqps://...",
  "MQ_QUEUE_NAME": "cgiar_ms2_prod_reports_queue",
  "MS_USERNAME": "***",
  "MS_PASSWORD": "***",
  "MS_BUCKET": "microservice-reports",
  "MS_S3_URL": "https://microservice-reports.s3.us-east-1.amazonaws.com/",
  "MS_REPORTING_URL": "https://reports.prms.cgiar.org/api/",
  "REPORT_NAME_PREFIX": "AICCRA",
  "PDF_POLL_MAX_RETRIES": "30",
  "PDF_POLL_INTERVAL_MS": "2000",
  "REPORT_DRY_RUN": "false",
  "REPORT_SKIP_S3_POLL": "false"
}
```

Lambda handler: `lambda.handler` (file `lambda.js` at package root). Use **Lambda Function URL** (no API Gateway cost). VPC + S3 Gateway Endpoint required for production.

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
