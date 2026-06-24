import { Router, Request, Response, NextFunction } from 'express';

import { AppConfig } from '../../config/env';
import { getDataSource } from '../../config/data-source';
import { parseOptionalPositiveInt, parsePositiveInt } from '../../shared/parse-query';
import { InnovationReportService } from './innovation/innovation.service';
import { ClusterReportService } from './cluster/cluster.service';
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

  router.post('/innovation', async (req: Request, res: Response, next: NextFunction) => {
    try {
      const innovationId = parsePositiveInt(req.body.innovationId, 'innovationId');
      const phaseId = parsePositiveInt(req.body.phaseId, 'phaseId');
      const crpAcronym = req.body.crpAcronym ? String(req.body.crpAcronym) : undefined;
      const dryRun = req.body.dryRun as boolean | undefined;
      const skipS3Poll = req.body.skipS3Poll as boolean | undefined;

      const dataSource = await getDataSource(config);
      const service = new InnovationReportService(config, dataSource);
      const result = await service.generate({
        innovationId,
        phaseId,
        crpAcronym,
        dryRun,
        skipS3Poll,
      });

      res.status(200).json(result);
    } catch (error) {
      next(error);
    }
  });

  router.post('/cluster', async (req: Request, res: Response, next: NextFunction) => {
    try {
      const projectId = parsePositiveInt(req.body.projectId, 'projectId');
      const phaseId = parsePositiveInt(req.body.phaseId, 'phaseId');
      const dryRun = req.body.dryRun as boolean | undefined;
      const skipS3Poll = req.body.skipS3Poll as boolean | undefined;

      const dataSource = await getDataSource(config);
      const service = new ClusterReportService(config, dataSource);
      const result = await service.generate({ projectId, phaseId, dryRun, skipS3Poll });

      res.status(200).json(result);
    } catch (error) {
      next(error);
    }
  });

  return router;
}
