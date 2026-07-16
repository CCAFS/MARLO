import { DataSource } from 'typeorm';

export interface PhaseQueryParams {
  crp: string;
  phaseId?: number;
  cycle?: string;
  year?: number;
}

export interface StudySummaryQueryParams extends PhaseQueryParams {
  studyId: number;
}

export class PhaseResolverService {
  constructor(private readonly dataSource: DataSource) {}

  async resolvePhaseId(params: StudySummaryQueryParams): Promise<number> {
    return this.resolvePhaseFromQuery(params);
  }

  async resolveInnovationPhaseId(params: PhaseQueryParams): Promise<number> {
    return this.resolvePhaseFromQuery(params);
  }

  async resolveClusterPhaseId(params: PhaseQueryParams): Promise<number> {
    return this.resolvePhaseFromQuery(params);
  }

  /**
   * Resolves phase id from MARLO-style query params (phaseID or cycle+year+crp).
   * Mirrors BaseSummariesAction.setPublicAccessParameters().
   */
  private async resolvePhaseFromQuery(params: PhaseQueryParams): Promise<number> {
    if (params.phaseId != null) {
      return params.phaseId;
    }

    if (params.cycle && params.year != null && params.crp) {
      const rows = await this.dataSource.query<Array<{ id: number }>>(
        `
        SELECT p.id AS id
        FROM phases p
        INNER JOIN global_units gu ON gu.id = p.global_unit_id
        WHERE p.description = ?
          AND p.year = ?
          AND p.upkeep = 0
          AND gu.acronym = ?
        LIMIT 1
        `,
        [params.cycle, params.year, params.crp],
      );

      if (rows[0]?.id) {
        return rows[0].id;
      }

      throw new Error(
        `Phase not found for CRP ${params.crp}, cycle=${params.cycle}, year=${params.year}`,
      );
    }

    throw new Error('Missing phase: provide phaseID or cycle+year');
  }
}
