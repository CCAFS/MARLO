import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';

@Entity({ name: 'report_configurations' })
export class ReportConfiguration {
  @PrimaryGeneratedColumn({ type: 'bigint' })
  id!: number;

  @Column({ name: 'oicr_template_data', type: 'text', nullable: true })
  oicrTemplateData!: string | null;

  @Column({ name: 'innovation_template_data', type: 'text', nullable: true })
  innovationTemplateData!: string | null;

  @Column({ name: 'project_template_data', type: 'text', nullable: true })
  projectTemplateData!: string | null;
}
