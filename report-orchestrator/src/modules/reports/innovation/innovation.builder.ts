import { AppConfig } from '../../../config/env';
import {
  buildCredentialsJson,
  defaultPdfOptions,
  formatReportTimestamp,
  PdfGeneratePayload,
} from '../../../shared/pdf-payload.types';
import { assembleInnovationData } from './innovation-assembler';
import { InnovationContext, InnovationReportData } from './innovation.types';

export function mapInnovationContextToJsonData(
  context: InnovationContext,
  config: AppConfig,
  crpAcronym?: string,
): InnovationReportData {
  const baseUrl = config.microservice.reportingUrl.replace(/\/$/, '');
  return assembleInnovationData(context, baseUrl, crpAcronym);
}

export function buildInnovationFileName(prefix: string, innovationId: number): string {
  return `${prefix}-Innovation${innovationId}-Summary-${formatReportTimestamp()}.pdf`;
}

export function buildInnovationPdfPayload(
  config: AppConfig,
  templateData: string,
  innovationData: InnovationReportData,
  fileName: string,
): PdfGeneratePayload {
  return {
    pattern: 'pdf.generate',
    data: {
      templateData,
      data: innovationData,
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
