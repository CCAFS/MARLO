import 'reflect-metadata';

export interface AppConfig {
  port: number;
  nodeEnv: string;
  db: {
    host: string;
    port: number;
    database: string;
    username: string;
    password: string;
  };
  mq: {
    url: string;
    queueName: string;
  };
  microservice: {
    username: string;
    password: string;
    bucket: string;
    s3Url: string;
    reportingUrl: string;
  };
  reportNamePrefix: string;
  pdfPoll: {
    maxRetries: number;
    intervalMs: number;
  };
  reportDryRun: boolean;
  reportSkipS3Poll: boolean;
}

function required(name: string, value: string | undefined): string {
  if (!value || value.trim() === '') {
    throw new Error(`Missing required environment variable: ${name}`);
  }
  return value;
}

function optionalBool(name: string, defaultValue: boolean): boolean {
  const raw = process.env[name];
  if (raw === undefined) {
    return defaultValue;
  }
  return raw.toLowerCase() === 'true' || raw === '1';
}

function optionalInt(name: string, defaultValue: number): number {
  const raw = process.env[name];
  if (!raw) {
    return defaultValue;
  }
  const parsed = Number.parseInt(raw, 10);
  if (Number.isNaN(parsed)) {
    return defaultValue;
  }
  return parsed;
}

export function loadConfig(): AppConfig {
  return {
    port: optionalInt('PORT', 3000),
    nodeEnv: process.env.NODE_ENV ?? 'development',
    db: {
      host: process.env.DB_HOST ?? 'localhost',
      port: optionalInt('DB_PORT', 3306),
      database: process.env.DB_NAME ?? 'marlo',
      username: process.env.DB_USER ?? 'marlo',
      password: process.env.DB_PASSWORD ?? '',
    },
    mq: {
      url: process.env.MQ_URL ?? '',
      queueName: process.env.MQ_QUEUE_NAME ?? '',
    },
    microservice: {
      username: process.env.MS_USERNAME ?? '',
      password: process.env.MS_PASSWORD ?? '',
      bucket: process.env.MS_BUCKET ?? 'microservice-reports',
      s3Url: process.env.MS_S3_URL ?? '',
      reportingUrl: process.env.MS_REPORTING_URL ?? '',
    },
    reportNamePrefix: process.env.REPORT_NAME_PREFIX ?? 'AICCRA',
    pdfPoll: {
      maxRetries: optionalInt('PDF_POLL_MAX_RETRIES', 30),
      intervalMs: optionalInt('PDF_POLL_INTERVAL_MS', 2000),
    },
    reportDryRun: optionalBool('REPORT_DRY_RUN', false),
    reportSkipS3Poll: optionalBool('REPORT_SKIP_S3_POLL', false),
  };
}

export function assertQueueConfig(config: AppConfig): void {
  required('MQ_URL', config.mq.url);
  required('MQ_QUEUE_NAME', config.mq.queueName);
  required('MS_USERNAME', config.microservice.username);
  required('MS_PASSWORD', config.microservice.password);
  required('MS_BUCKET', config.microservice.bucket);
}

export function assertDbConfig(config: AppConfig): void {
  required('DB_HOST', config.db.host);
  required('DB_NAME', config.db.database);
  required('DB_USER', config.db.username);
  required('DB_PASSWORD', config.db.password);
}
