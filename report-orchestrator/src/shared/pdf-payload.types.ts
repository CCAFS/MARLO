export interface PdfGenerateOptions {
  format: string;
  orientation: string;
  border: string;
  zoomFactor: number;
  header: { height: string };
  footer: { height: string };
  timeout: string;
}

export interface PdfGenerateInnerData {
  templateData: string;
  data: Record<string, unknown>;
  options: PdfGenerateOptions;
  fileName: string;
  bucketName: string;
  credentials: string;
}

export interface PdfGeneratePayload {
  pattern: 'pdf.generate';
  data: PdfGenerateInnerData;
}

export interface OicrReportRequest {
  studyId: number;
  phaseId: number;
  dryRun?: boolean;
  skipS3Poll?: boolean;
}

export interface OicrReportResponse {
  status: 'dry_run' | 'processing' | 'ready' | 'queued';
  fileName: string;
  payload?: PdfGeneratePayload;
  downloadUrl?: string;
  message?: string;
}

export interface InnovationReportRequest {
  innovationId: number;
  phaseId: number;
  crpAcronym?: string;
  dryRun?: boolean;
  skipS3Poll?: boolean;
}

export interface InnovationReportResponse {
  status: 'dry_run' | 'processing' | 'ready' | 'queued';
  fileName: string;
  payload?: PdfGeneratePayload;
  downloadUrl?: string;
  message?: string;
}

export function defaultPdfOptions(): PdfGenerateOptions {
  return {
    format: 'A4',
    orientation: 'portrait',
    border: '0',
    zoomFactor: 1,
    header: { height: '40mm' },
    footer: { height: '30mm' },
    timeout: '300000',
  };
}

export function buildCredentialsJson(username: string, password: string): string {
  return JSON.stringify({ username, password });
}

export function formatReportTimestamp(): string {
  const now = new Date();
  const pad = (value: number) => String(value).padStart(2, '0');
  return (
    `${now.getFullYear()}${pad(now.getMonth() + 1)}${pad(now.getDate())}_` +
    `${pad(now.getHours())}${pad(now.getMinutes())}`
  );
}
