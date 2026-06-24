import { DataSource } from 'typeorm';

import { AppConfig, assertDbConfig, assertQueueConfig } from '../../../config/env';
import { ClusterReportRequest, ClusterReportResponse, PdfGeneratePayload } from '../../../shared/pdf-payload.types';
import { QueueService } from '../../queue/queue.service';
import { fetchPdfBuffer } from '../../storage/pdf-stream.service';
import { S3PollService } from '../../storage/s3-poll.service';
import { TemplateService } from '../../template/template.service';
import { OicrRepository } from '../oicr/oicr.repository';
import { OicrStudyContext } from '../oicr/oicr.types';
import { InnovationRepository } from '../innovation/innovation.repository';
import { InnovationContext } from '../innovation/innovation.types';
import {
  buildClusterFileName,
  buildClusterPdfPayload,
  mapClusterContextToJsonData,
} from './cluster.builder';
import { ClusterDeliverableRepository } from './cluster-deliverable.repository';
import { ClusterRepository } from './cluster.repository';

export interface ClusterPdfStreamResult {
  fileName: string;
  pdfBuffer: Buffer;
}

export class ClusterReportService {
  private readonly repository: ClusterRepository;

  private readonly oicrRepository: OicrRepository;

  private readonly innovationRepository: InnovationRepository;

  private readonly deliverableRepository: ClusterDeliverableRepository;

  private readonly templateService: TemplateService;

  private readonly queueService: QueueService;

  private readonly s3PollService: S3PollService;

  constructor(
    private readonly config: AppConfig,
    dataSource: DataSource,
  ) {
    this.repository = new ClusterRepository(dataSource);
    this.oicrRepository = new OicrRepository(dataSource);
    this.innovationRepository = new InnovationRepository(dataSource);
    this.deliverableRepository = new ClusterDeliverableRepository(dataSource);
    this.templateService = new TemplateService(dataSource);
    this.queueService = new QueueService(config);
    this.s3PollService = new S3PollService(config);
  }

  async generate(request: ClusterReportRequest): Promise<ClusterReportResponse> {
    assertDbConfig(this.config);

    const dryRun = request.dryRun ?? this.config.reportDryRun;
    const skipS3Poll = request.skipS3Poll ?? this.config.reportSkipS3Poll;
    const { fileName, payload } = await this.buildPayload(request.projectId, request.phaseId);

    if (dryRun) {
      return {
        status: 'dry_run',
        fileName,
        payload,
        message: 'REPORT_DRY_RUN enabled — JSON built, queue and S3 skipped',
      };
    }

    assertQueueConfig(this.config);
    await this.queueService.publishPdfGenerate(payload);

    if (skipS3Poll) {
      return {
        status: 'queued',
        fileName,
        payload,
        message: 'Published to queue — S3 poll skipped',
      };
    }

    const downloadUrl = await this.s3PollService.waitForPublicPdfUrl(fileName);
    return {
      status: 'ready',
      fileName,
      downloadUrl,
      payload,
    };
  }

  async generatePdfForBrowser(projectId: number, phaseId: number): Promise<ClusterPdfStreamResult> {
    assertDbConfig(this.config);
    assertQueueConfig(this.config);

    const { fileName, payload } = await this.buildPayload(projectId, phaseId);
    await this.queueService.publishPdfGenerate(payload);
    const downloadUrl = await this.s3PollService.waitForPublicPdfUrl(fileName);
    const pdfBuffer = await fetchPdfBuffer(downloadUrl);

    return { fileName, pdfBuffer };
  }

  private async buildPayload(
    projectId: number,
    phaseId: number,
  ): Promise<{ fileName: string; payload: PdfGeneratePayload }> {
    const context = await this.repository.loadClusterContext(projectId, phaseId);
    if (!context) {
      throw new Error(`Project ${projectId} not found for phase ${phaseId}`);
    }

    const oicrContexts = await this.loadOicrContexts(context.studyIds, phaseId);
    const innovationContexts = await this.loadInnovationContexts(
      context.innovations.map((innovation) => innovation.id),
      phaseId,
    );
    const deliverableContexts = await this.deliverableRepository.loadExtendedByDeliverableIds(
      context.deliverables.map((deliverable) => deliverable.id),
      phaseId,
    );
    const templateData = await this.templateService.getClusterTemplate();
    const clusterData = mapClusterContextToJsonData(
      context,
      oicrContexts,
      innovationContexts,
      deliverableContexts,
    );
    const fileName = buildClusterFileName(this.config.reportNamePrefix, projectId);
    const payload = buildClusterPdfPayload(this.config, templateData, clusterData, fileName);

    return { fileName, payload };
  }

  private async loadOicrContexts(studyIds: number[], phaseId: number): Promise<OicrStudyContext[]> {
    const contexts: OicrStudyContext[] = [];
    for (const studyId of studyIds) {
      const studyContext = await this.oicrRepository.loadStudyContext(studyId, phaseId);
      if (studyContext) {
        contexts.push(studyContext);
      }
    }
    return contexts;
  }

  private async loadInnovationContexts(
    innovationIds: number[],
    phaseId: number,
  ): Promise<Map<number, InnovationContext>> {
    const contexts = new Map<number, InnovationContext>();
    for (const innovationId of innovationIds) {
      const innovationContext = await this.innovationRepository.loadInnovationContext(
        innovationId,
        phaseId,
      );
      if (innovationContext) {
        contexts.set(innovationId, innovationContext);
      }
    }
    return contexts;
  }
}
