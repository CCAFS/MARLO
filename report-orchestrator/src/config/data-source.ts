import { DataSource } from 'typeorm';

import { AppConfig } from './env';
import { ReportConfiguration } from '../modules/template/entities/report-configuration.entity';

let dataSource: DataSource | undefined;

export function createDataSource(config: AppConfig): DataSource {
  return new DataSource({
    type: 'mysql',
    host: config.db.host,
    port: config.db.port,
    username: config.db.username,
    password: config.db.password,
    database: config.db.database,
    entities: [ReportConfiguration],
    synchronize: false,
    logging: config.nodeEnv === 'development',
    extra: {
      connectionLimit: 2,
    },
  });
}

export async function getDataSource(config: AppConfig): Promise<DataSource> {
  if (!dataSource) {
    dataSource = createDataSource(config);
  }
  if (!dataSource.isInitialized) {
    await dataSource.initialize();
  }
  return dataSource;
}

export async function closeDataSource(): Promise<void> {
  if (dataSource?.isInitialized) {
    await dataSource.destroy();
    dataSource = undefined;
  }
}
