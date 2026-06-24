import { AppConfig } from '../../config/env';

function sleep(ms: number): Promise<void> {
  return new Promise((resolve) => {
    setTimeout(resolve, ms);
  });
}

export class S3PollService {
  constructor(private readonly config: AppConfig) {}

  async waitForPublicPdfUrl(fileName: string): Promise<string> {
    const baseUrl = this.config.microservice.s3Url.endsWith('/')
      ? this.config.microservice.s3Url
      : `${this.config.microservice.s3Url}/`;
    const pdfUrl = `${baseUrl}${fileName}`;

    for (let attempt = 0; attempt < this.config.pdfPoll.maxRetries; attempt += 1) {
      const response = await fetch(pdfUrl, { method: 'HEAD' });
      if (response.ok) {
        return pdfUrl;
      }
      if (attempt < this.config.pdfPoll.maxRetries - 1) {
        await sleep(this.config.pdfPoll.intervalMs);
      }
    }

    throw new Error(`PDF not available after ${this.config.pdfPoll.maxRetries} attempts: ${pdfUrl}`);
  }
}
