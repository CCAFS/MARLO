import { DataSource } from 'typeorm';

import { AppConfig, assertDbConfig, assertQueueConfig } from '../../../config/env';
import { QueueService } from '../../queue/queue.service';
import { S3PollService } from '../../storage/s3-poll.service';
import { TemplateService } from '../../template/template.service';
import { OicrReportRequest, OicrReportResponse } from '../../../shared/pdf-payload.types';
import {
  buildOicrFileName,
  buildOicrPdfPayload,
  mapStudyContextToJsonData,
} from './oicr.builder';
import { OicrRepository } from './oicr.repository';

export class OicrReportService {
  private readonly repository: OicrRepository;

  private readonly templateService: TemplateService;

  private readonly queueService: QueueService;

  private readonly s3PollService: S3PollService;

  constructor(
    private readonly config: AppConfig,
    dataSource: DataSource,
  ) {
    this.repository = new OicrRepository(dataSource);
    this.templateService = new TemplateService(dataSource);
    this.queueService = new QueueService(config);
    this.s3PollService = new S3PollService(config);
  }

  async generate(request: OicrReportRequest): Promise<OicrReportResponse> {
    assertDbConfig(this.config);

    const dryRun = request.dryRun ?? this.config.reportDryRun;
    const skipS3Poll = request.skipS3Poll ?? this.config.reportSkipS3Poll;

    const studyContext = await this.repository.loadStudyContext(request.studyId, request.phaseId);
    if (!studyContext) {
      throw new Error(
        `OICR study ${request.studyId} not found for phase ${request.phaseId}`,
      );
    }

    const templateData = await this.templateService.getOicrTemplate();
    const studyData = mapStudyContextToJsonData(studyContext);
    const fileName = buildOicrFileName(this.config.reportNamePrefix, request.studyId);
    const payload = buildOicrPdfPayload(this.config, templateData, studyData, fileName);

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
}
