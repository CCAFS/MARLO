import dotenv from 'dotenv';

import { createApp } from './app';
import { loadConfig } from './config/env';
import { closeDataSource } from './config/data-source';

dotenv.config();

async function main(): Promise<void> {
  const config = loadConfig();
  const app = createApp(config);

  const server = app.listen(config.port, () => {
    console.log(`Report orchestrator listening on http://localhost:${config.port}`);
    console.log(`  GET  /health`);
    console.log(`  POST /reports/oicr  { "studyId": 3517, "phaseId": 407 }`);
    console.log(`  REPORT_DRY_RUN=${config.reportDryRun}`);
  });

  const shutdown = async () => {
    server.close();
    await closeDataSource();
    process.exit(0);
  };

  process.on('SIGINT', shutdown);
  process.on('SIGTERM', shutdown);
}

main().catch((error) => {
  console.error('Failed to start server', error);
  process.exit(1);
});
