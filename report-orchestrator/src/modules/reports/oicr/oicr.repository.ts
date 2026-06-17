import { DataSource } from 'typeorm';

import { OicrStudyRow } from './oicr.types';

/**
 * MVP OICR load — core fields only. Extend with additional joins as parity work progresses.
 * Mirrors the first fields mapped in BaseStudySummaryData.generateAndSendJson().
 */
export class OicrRepository {
  constructor(private readonly dataSource: DataSource) {}

  async findStudyByIdAndPhase(studyId: number, phaseId: number): Promise<OicrStudyRow | null> {
    const rows = await this.dataSource.query<OicrStudyRow[]>(
      `
      SELECT
        pes.id AS id,
        pesi.year AS year,
        pesi.title AS title,
        pesi.commissioning_study AS commissioningStudy,
        gs.name AS status,
        CASE
          WHEN st.id = 1 THEN 'Outcome Impact Case Report (OICR)'
          ELSE st.name
        END AS type,
        pesi.outcome_impact_statement AS outcomeImpactStatement,
        pesi.top_level_comments AS topLevelComments,
        pesi.scope_comments AS scopeComments,
        pesi.alliance_oicr_id AS allianceOicr,
        CONCAT(
          COALESCE(riss.name, ''),
          CASE
            WHEN riss.name IS NOT NULL AND riss.description_af IS NOT NULL THEN ' - '
            ELSE ''
          END,
          COALESCE(riss.description_af, '')
        ) AS stageStudy
      FROM project_expected_studies pes
      INNER JOIN project_expected_study_info pesi
        ON pesi.project_expected_study_id = pes.id
        AND pesi.id_phase = ?
      LEFT JOIN general_statuses gs ON gs.id = pesi.status
      LEFT JOIN study_types st ON st.id = pesi.study_type_id
      LEFT JOIN rep_ind_stage_studies riss ON riss.id = pesi.rep_ind_stage_study_id
      WHERE pes.id = ?
        AND pes.is_active = 1
      LIMIT 1
      `,
      [phaseId, studyId],
    );

    return rows[0] ?? null;
  }
}
