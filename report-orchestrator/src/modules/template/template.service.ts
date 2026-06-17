import { DataSource } from 'typeorm';

import { ReportConfiguration } from './entities/report-configuration.entity';

export class TemplateService {
  constructor(private readonly dataSource: DataSource) {}

  async getOicrTemplate(): Promise<string> {
    const repo = this.dataSource.getRepository(ReportConfiguration);
    const rows = await repo.find({ take: 1, order: { id: 'ASC' } });
    const template = rows[0]?.oicrTemplateData;
    if (!template) {
      throw new Error('OICR template not found in report_configurations.oicr_template_data');
    }
    return template;
  }
}
