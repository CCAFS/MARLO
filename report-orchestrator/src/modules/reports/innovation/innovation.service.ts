import { DataSource } from 'typeorm';

import { AppConfig, assertDbConfig, assertQueueConfig } from '../../../config/env';
import { PdfGeneratePayload } from '../../../shared/pdf-payload.types';
import { InnovationReportRequest, InnovationReportResponse } from '../../../shared/pdf-payload.types';
import { QueueService } from '../../queue/queue.service';
import { fetchPdfBuffer } from '../../storage/pdf-stream.service';
import { S3PollService } from '../../storage/s3-poll.service';
import { TemplateService } from '../../template/template.service';
import {
  buildInnovationFileName,
  buildInnovationPdfPayload,
  mapInnovationContextToJsonData,
} from './innovation.builder';
import { InnovationRepository } from './innovation.repository';

export interface InnovationPdfStreamResult {
  fileName: string;
  pdfBuffer: Buffer;
}

export class InnovationReportService {
  private readonly repository: InnovationRepository;

  private readonly templateService: TemplateService;

  private readonly queueService: QueueService;

  private readonly s3PollService: S3PollService;

  constructor(
    private readonly config: AppConfig,
    dataSource: DataSource,
  ) {
    this.repository = new InnovationRepository(dataSource);
    this.templateService = new TemplateService(dataSource);
    this.queueService = new QueueService(config);
    this.s3PollService = new S3PollService(config);
  }

  async generate(request: InnovationReportRequest): Promise<InnovationReportResponse> {
    assertDbConfig(this.config);

    const dryRun = request.dryRun ?? this.config.reportDryRun;
    const skipS3Poll = request.skipS3Poll ?? this.config.reportSkipS3Poll;
    const { fileName, payload } = await this.buildPayload(
      request.innovationId,
      request.phaseId,
      request.crpAcronym,
    );

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

  async generatePdfForBrowser(
    innovationId: number,
    phaseId: number,
    crpAcronym?: string,
  ): Promise<InnovationPdfStreamResult> {
    assertDbConfig(this.config);
    assertQueueConfig(this.config);

    const { fileName, payload } = await this.buildPayload(innovationId, phaseId, crpAcronym);
    await this.queueService.publishPdfGenerate(payload);
    const downloadUrl = await this.s3PollService.waitForPublicPdfUrl(fileName);
    const pdfBuffer = await fetchPdfBuffer(downloadUrl);

    return { fileName, pdfBuffer };
  }

  private async buildPayload(
    innovationId: number,
    phaseId: number,
    crpAcronym?: string,
  ): Promise<{ fileName: string; payload: PdfGeneratePayload }> {
    const context = await this.repository.loadInnovationContext(innovationId, phaseId);
    if (!context) {
      throw new Error(`Innovation ${innovationId} not found for phase ${phaseId}`);
    }

    const templateData = await this.templateService.getInnovationTemplate();
    const innovationData = mapInnovationContextToJsonData(context, this.config, crpAcronym);
    const fileName = buildInnovationFileName(this.config.reportNamePrefix, innovationId);
    const payload = buildInnovationPdfPayload(this.config, templateData, innovationData, fileName);

    return { fileName, payload };
  }
}
