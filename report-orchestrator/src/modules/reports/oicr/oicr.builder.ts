import { AppConfig } from '../../../config/env';
import {
  buildCredentialsJson,
  defaultPdfOptions,
  formatReportTimestamp,
  PdfGeneratePayload,
} from '../../../shared/pdf-payload.types';
import { assembleOicrStudyData } from './oicr-assembler';
import { OicrStudyContext, OicrStudyData } from './oicr.types';

function formatTimeCreation(): string {
  const formatter = new Intl.DateTimeFormat('en-US', {
    weekday: 'long',
    month: 'long',
    day: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
    timeZone: 'Europe/Paris',
  });
  return `${formatter.format(new Date())} (CET)`;
}

export function mapStudyContextToJsonData(context: OicrStudyContext): OicrStudyData {
  return assembleOicrStudyData(context, formatTimeCreation());
}

export function buildOicrFileName(prefix: string, studyId: number): string {
  return `${prefix}-OICR${studyId}-Summary-${formatReportTimestamp()}.pdf`;
}

export function buildOicrPdfPayload(
  config: AppConfig,
  templateData: string,
  studyData: OicrStudyData,
  fileName: string,
): PdfGeneratePayload {
  return {
    pattern: 'pdf.generate',
    data: {
      templateData,
      data: studyData,
      options: defaultPdfOptions(),
      fileName,
      bucketName: config.microservice.bucket,
      credentials: buildCredentialsJson(
        config.microservice.username,
        config.microservice.password,
      ),
    },
  };
}
