import { Response } from 'express';

/** Stream a PDF buffer to the client without string coercion (required for Lambda Function URL). */
export function sendPdfInline(res: Response, fileName: string, pdfBuffer: Buffer): void {
  res.status(200);
  res.setHeader('Content-Type', 'application/pdf');
  res.setHeader('Content-Disposition', `inline; filename="${fileName}"`);
  res.setHeader('Content-Length', pdfBuffer.length);
  res.end(pdfBuffer);
}
