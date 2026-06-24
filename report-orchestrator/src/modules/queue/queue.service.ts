import amqp from 'amqplib';

import { AppConfig } from '../../config/env';
import { PdfGeneratePayload } from '../../shared/pdf-payload.types';

export class QueueService {
  constructor(private readonly config: AppConfig) {}

  async publishPdfGenerate(payload: PdfGeneratePayload): Promise<void> {
    const connection = await amqp.connect(this.config.mq.url);
    try {
      const channel = await connection.createChannel();
      await channel.assertQueue(this.config.mq.queueName, { durable: true });
      channel.sendToQueue(
        this.config.mq.queueName,
        Buffer.from(JSON.stringify(payload)),
        { persistent: true },
      );
      await channel.close();
    } finally {
      await connection.close();
    }
  }
}
