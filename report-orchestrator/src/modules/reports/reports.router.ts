import { Router, Request, Response, NextFunction } from 'express';

import { AppConfig } from '../../config/env';
import { getDataSource } from '../../config/data-source';
import { parseOptionalPositiveInt, parsePositiveInt } from '../../shared/parse-query';
import { OicrReportService } from './oicr/oicr.service';

export function createReportsRouter(config: AppConfig): Router {
  const router = Router();

  router.post('/oicr', async (req: Request, res: Response, next: NextFunction) => {
    try {
      const studyId = parsePositiveInt(req.body.studyId, 'studyId');
      const phaseId = parsePositiveInt(req.body.phaseId, 'phaseId');
      const dryRun = req.body.dryRun as boolean | undefined;
      const skipS3Poll = req.body.skipS3Poll as boolean | undefined;

      const dataSource = await getDataSource(config);
      const service = new OicrReportService(config, dataSource);
      const result = await service.generate({ studyId, phaseId, dryRun, skipS3Poll });

      res.status(200).json(result);
    } catch (error) {
      next(error);
    }
  });

  return router;
}
