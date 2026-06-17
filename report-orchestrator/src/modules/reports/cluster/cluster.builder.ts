import { AppConfig } from '../../../config/env';
import {
  buildCredentialsJson,
  defaultPdfOptions,
  formatReportTimestamp,
  PdfGeneratePayload,
} from '../../../shared/pdf-payload.types';
import { assembleClusterData } from './cluster-assembler';
import { OicrStudyContext } from '../oicr/oicr.types';
import { ClusterContext, ClusterReportData } from './cluster.types';

export function mapClusterContextToJsonData(
  context: ClusterContext,
  oicrContexts: OicrStudyContext[],
): ClusterReportData {
  return assembleClusterData(context, oicrContexts);
}

export function buildClusterFileName(prefix: string, projectId: number): string {
  return `${prefix}-Cluster-${projectId}-Summary-${formatReportTimestamp()}.pdf`;
}

export function buildClusterPdfPayload(
  config: AppConfig,
  templateData: string,
  clusterData: ClusterReportData,
  fileName: string,
): PdfGeneratePayload {
  return {
    pattern: 'pdf.generate',
    data: {
      templateData,
      data: clusterData,
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
