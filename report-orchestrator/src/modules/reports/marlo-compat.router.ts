import { Router, Request, Response, NextFunction } from 'express';

import { AppConfig } from '../../config/env';
import { getDataSource } from '../../config/data-source';
import { PhaseResolverService } from '../phase/phase-resolver.service';
import { parseOptionalPositiveInt, parsePositiveInt } from '../../shared/parse-query';
import { sendPdfInline } from '../../shared/pdf-response.utils';
import { InnovationReportService } from './innovation/innovation.service';
import { ClusterReportService } from './cluster/cluster.service';
import { OicrReportService } from './oicr/oicr.service';

/**
 * MARLO-compatible GET routes — open in browser to stream PDF inline.
 *
 * Example (same query shape as MARLO studySummary.do):
 *   GET /projects/AICCRA/studySummary.do?studyID=3589&cycle=Reporting&year=2025
 *   GET /projects/AICCRA/studySummary.do?studyID=3589&phaseID=407
 */
export function createMarloCompatRouter(config: AppConfig): Router {
  const router = Router();

  router.get(
    '/projects/:crp/studySummary.do',
    async (req: Request, res: Response, next: NextFunction) => {
      try {
        const studyId = parsePositiveInt(req.query.studyID, 'studyID');
        const phaseId = parseOptionalPositiveInt(req.query.phaseID, 'phaseID');
        const cycle = req.query.cycle ? String(req.query.cycle) : undefined;
        const year = parseOptionalPositiveInt(req.query.year, 'year');
        const crp = String(req.params.crp);

        const dataSource = await getDataSource(config);
        const phaseResolver = new PhaseResolverService(dataSource);
        const resolvedPhaseId = await phaseResolver.resolvePhaseId({
          crp,
          studyId,
          phaseId,
          cycle,
          year,
        });

        const service = new OicrReportService(config, dataSource);
        const { fileName, pdfBuffer } = await service.generatePdfForBrowser(studyId, resolvedPhaseId);

        sendPdfInline(res, fileName, pdfBuffer);
      } catch (error) {
        next(error);
      }
    },
  );

  router.get(
    '/projects/:crp/projectInnovationSummary.do',
    async (req: Request, res: Response, next: NextFunction) => {
      try {
        const innovationId = parsePositiveInt(req.query.innovationID, 'innovationID');
        const phaseId = parseOptionalPositiveInt(req.query.phaseID, 'phaseID');
        const cycle = req.query.cycle ? String(req.query.cycle) : undefined;
        const year = parseOptionalPositiveInt(req.query.year, 'year');
        const crp = String(req.params.crp);

        const dataSource = await getDataSource(config);
        const phaseResolver = new PhaseResolverService(dataSource);
        const resolvedPhaseId = await phaseResolver.resolveInnovationPhaseId({
          crp,
          phaseId,
          cycle,
          year,
        });

        const service = new InnovationReportService(config, dataSource);
        const { fileName, pdfBuffer } = await service.generatePdfForBrowser(
          innovationId,
          resolvedPhaseId,
          crp,
        );

        sendPdfInline(res, fileName, pdfBuffer);
      } catch (error) {
        next(error);
      }
    },
  );

  router.get(
    '/projects/:crp/reportingSummary.do',
    async (req: Request, res: Response, next: NextFunction) => {
      try {
        const projectId = parsePositiveInt(req.query.projectID, 'projectID');
        const phaseId = parseOptionalPositiveInt(req.query.phaseID, 'phaseID');
        const cycle = req.query.cycle ? String(req.query.cycle) : undefined;
        const year = parseOptionalPositiveInt(req.query.year, 'year');
        const crp = String(req.params.crp);

        const dataSource = await getDataSource(config);
        const phaseResolver = new PhaseResolverService(dataSource);
        const resolvedPhaseId = await phaseResolver.resolveClusterPhaseId({
          crp,
          phaseId,
          cycle,
          year,
        });

        const service = new ClusterReportService(config, dataSource);
        const { fileName, pdfBuffer } = await service.generatePdfForBrowser(projectId, resolvedPhaseId);

        sendPdfInline(res, fileName, pdfBuffer);
      } catch (error) {
        next(error);
      }
    },
  );

  return router;
}
