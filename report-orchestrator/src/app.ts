import express, { Express, Request, Response, NextFunction } from 'express';

import { AppConfig } from './config/env';
import { createMarloCompatRouter } from './modules/reports/marlo-compat.router';
import { createReportsRouter } from './modules/reports/reports.router';

export function createApp(config: AppConfig): Express {
  const app = express();

  app.use(express.json({ limit: '2mb' }));

  app.get('/health', (_req: Request, res: Response) => {
    res.json({
      status: 'ok',
      service: 'report-orchestrator',
      dryRunDefault: config.reportDryRun,
    });
  });

  app.use('/reports', createReportsRouter(config));
  app.use(createMarloCompatRouter(config));

  app.use((error: Error, _req: Request, res: Response, _next: NextFunction) => {
    console.error('[report-orchestrator]', error.message);
    res.status(500).json({
      error: error.message,
    });
  });

  return app;
}
