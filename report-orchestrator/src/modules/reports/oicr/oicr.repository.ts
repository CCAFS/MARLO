import { DataSource } from 'typeorm';

import {
  ALLIANCE_INSTITUTION_NAME,
  CRP_PROGRAM_TYPE_FLAGSHIP,
  CRP_PROGRAM_TYPE_REGIONAL,
  EXPECTED_STUDIES_PARTNERSHIP_TYPE_CENTER,
  LOC_ELEMENT_TYPE_COUNTRY,
} from './oicr.constants';
import {
  OicrAllianceLeverRow,
  OicrCoreRow,
  OicrCountryRow,
  OicrGeographicScopeRow,
  OicrImpactAreaRow,
  OicrInstitutionRow,
  OicrInnovationRow,
  OicrPublicationRow,
  OicrQuantificationRow,
  OicrReferenceRow,
  OicrSdgLeverRow,
  OicrStudyContext,
  OicrStudyProjectRow,
} from './oicr.types';

const INSTITUTION_COMPOSED_NAME_SQL = `
  CASE
    WHEN i.acronym IS NOT NULL AND TRIM(i.acronym) != '' THEN CONCAT(i.acronym, ' - ', i.name)
    ELSE i.name
  END
`;

const INSTITUTION_HQ_SQL = `
  COALESCE(
    (
      SELECT le.name
      FROM institutions_locations il
      INNER JOIN loc_elements le ON le.id = il.loc_element_id
      WHERE il.institution_id = i.id
        AND il.is_headquater = 1
      LIMIT 1
    ),
    'Not available'
  )
`;

/**
 * Loads OICR study data and related entities for pdf.generate JSON assembly.
 * SQL mirrors BaseStudySummaryData.generateAndSendJson() field sources.
 */
export class OicrRepository {
  constructor(private readonly dataSource: DataSource) {}

  async loadStudyContext(studyId: number, phaseId: number): Promise<OicrStudyContext | null> {
    const core = await this.loadCore(studyId, phaseId);
    if (!core) {
      return null;
    }

    const [
      geographicScopes,
      countryDetails,
      regions,
      centers,
      institutions,
      innovations,
      references,
      quantifications,
      performanceIndicators,
      links,
      publications,
      studyProjects,
      allianceLevers,
      sdgLevers,
      impactAreas,
      globalTargets,
      srfTargets,
      subIdos,
      crps,
      flagships,
      regionalPrograms,
      policies,
      projectOutcomes,
      crpOutcomes,
      hasAllianceInstitution,
    ] = await Promise.all([
      this.loadGeographicScopes(studyId, phaseId),
      this.loadCountryDetails(studyId, phaseId),
      this.loadRegions(studyId, phaseId),
      this.loadCenterPartners(studyId, phaseId),
      this.loadExternalInstitutions(studyId, phaseId),
      this.loadInnovations(studyId, phaseId),
      this.loadReferences(studyId, phaseId),
      this.loadQuantifications(studyId, phaseId),
      this.loadPerformanceIndicators(studyId, phaseId),
      this.loadLinks(studyId, phaseId),
      this.loadPublications(studyId, phaseId),
      this.loadStudyProjects(studyId, phaseId),
      this.loadAllianceLevers(studyId, phaseId),
      this.loadSdgLevers(studyId, phaseId),
      this.loadImpactAreas(studyId, phaseId),
      this.loadGlobalTargets(studyId, phaseId),
      this.loadSrfTargets(studyId, phaseId),
      this.loadSubIdos(studyId, phaseId),
      this.loadCrps(studyId, phaseId),
      this.loadFlagships(studyId, phaseId),
      this.loadRegionalPrograms(studyId, phaseId),
      this.loadPolicies(studyId, phaseId),
      this.loadProjectOutcomes(studyId, phaseId),
      this.loadCrpOutcomes(studyId, phaseId),
      this.hasAllianceInstitution(studyId, phaseId),
    ]);

    const countries = countryDetails.map((row) => row.name);

    return {
      core,
      geographicScopes,
      countries,
      countryDetails,
      regions,
      centers,
      institutions,
      innovations,
      references,
      quantifications,
      performanceIndicators,
      links,
      publications,
      studyProjects,
      allianceLevers,
      sdgLevers,
      impactAreas,
      globalTargets,
      srfTargets,
      subIdos,
      crps,
      flagships,
      regionalPrograms,
      policies,
      projectOutcomes,
      crpOutcomes,
      hasAllianceInstitution,
    };
  }

  private async loadCore(studyId: number, phaseId: number): Promise<OicrCoreRow | null> {
    const rows = await this.dataSource.query<OicrCoreRow[]>(
      `
      SELECT
        pes.id AS id,
        pes.project_id AS projectId,
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
        ) AS stageStudy,
        pet.tag_name AS tagAs,
        et.name AS tagged,
        pesi.cgiar_innovation AS cgiarInnovation,
        pesi.elaboration_outcome_impact_statement AS elaborationOutcomeImpactStatement,
        pesi.comunications_material AS communicationsMaterial,
        pesi.melia_publications AS meliaPublications,
        pesi.references_text AS referencesText,
        pesi.other_cross_cutting_dimensions AS otherCrossCuttingDimensions,
        pesi.other_cross_cutting_selection AS otherCrossCuttingSelection,
        pesi.has_cgiar_contribution AS hasCgiarContribution,
        pesi.reason_not_cgiar_contribution AS reasonNotCgiarContribution,
        pesi.has_covid_analysis AS hasCovidAnalysis,
        pesi.is_srf_target AS isSrfTarget,
        pesi.contacts AS contacts,
        gender.powb_name AS genderRelevance,
        youth.powb_name AS youthRelevance,
        capdev.powb_name AS capacityRelevance,
        climate.powb_name AS climateRelevance,
        p.acronym AS projectAcronym,
        pi.title AS projectTitle,
        pesi.quantification AS quantification,
        rsp.name AS stageProcess,
        riot.name AS organizationType,
        rpit.name AS policyInvestimentType,
        pesi.policy_amount AS policyAmount,
        pesi.is_contribution AS isContribution,
        pesi.other_innovations_narrative AS otherInnovationsNarrative,
        pesi.outcome_story AS outcomeStory,
        pesi.comments_relevance AS commentsRelevance,
        pet.tag_name AS tag,
        (
          SELECT CONCAT(u.last_name, ', ', u.first_name)
          FROM project_partners pp
          INNER JOIN project_partner_persons ppp ON ppp.project_partner_id = pp.id
          INNER JOIN users u ON u.id = ppp.user_id
          WHERE pp.project_id = pes.project_id
            AND pp.id_phase = ?
            AND pp.is_active = 1
            AND ppp.is_active = 1
            AND ppp.contact_type = 'PL'
          LIMIT 1
        ) AS leadPerson
      FROM project_expected_studies pes
      INNER JOIN project_expected_study_info pesi
        ON pesi.project_expected_study_id = pes.id
        AND pesi.id_phase = ?
      INNER JOIN projects p ON p.id = pes.project_id
      LEFT JOIN projects_info pi ON pi.project_id = p.id AND pi.id_phase = ?
      LEFT JOIN general_statuses gs ON gs.id = pesi.status
      LEFT JOIN study_types st ON st.id = pesi.study_type_id
      LEFT JOIN rep_ind_stage_studies riss ON riss.id = pesi.rep_ind_stage_study_id
      LEFT JOIN project_expected_study_tags pet ON pet.id = pesi.tag_id
      LEFT JOIN evidence_tags et ON et.id = pesi.evidence_tag_id
      LEFT JOIN rep_ind_gender_youth_focus_levels gender ON gender.id = pesi.gender_focus_level_id
      LEFT JOIN rep_ind_gender_youth_focus_levels youth ON youth.id = pesi.youth_focus_level_id
      LEFT JOIN rep_ind_gender_youth_focus_levels capdev ON capdev.id = pesi.capdev_focus_level_id
      LEFT JOIN rep_ind_gender_youth_focus_levels climate ON climate.id = pesi.climate_change_level_id
      LEFT JOIN rep_ind_stage_process rsp ON rsp.id = pesi.rep_ind_stage_process_id
      LEFT JOIN rep_ind_organization_types riot ON riot.id = pesi.rep_ind_organization_type_id
      LEFT JOIN rep_ind_policy_investiment_types rpit ON rpit.id = pesi.rep_ind_policy_id
      WHERE pes.id = ?
        AND pes.is_active = 1
      LIMIT 1
      `,
      [phaseId, phaseId, phaseId, studyId],
    );

    return rows[0] ?? null;
  }

  private async loadGeographicScopes(
    studyId: number,
    phaseId: number,
  ): Promise<OicrGeographicScopeRow[]> {
    return this.dataSource.query<OicrGeographicScopeRow[]>(
      `
      SELECT
        rigs.id AS scopeId,
        rigs.name AS scopeName
      FROM project_expected_study_geographic_scopes pesgs
      INNER JOIN rep_ind_geographic_scopes rigs ON rigs.id = pesgs.rep_ind_geographic_scope_id
      WHERE pesgs.expected_id = ?
        AND pesgs.id_phase = ?
      ORDER BY rigs.id
      `,
      [studyId, phaseId],
    );
  }

  private async loadCountryDetails(studyId: number, phaseId: number): Promise<OicrCountryRow[]> {
    return this.dataSource.query<OicrCountryRow[]>(
      `
      SELECT
        le.name AS name,
        LOWER(le.iso_alpha_2) AS isoAlpha2
      FROM project_expected_study_countries pesc
      INNER JOIN loc_elements le ON le.id = pesc.loc_element_id
      WHERE pesc.expected_id = ?
        AND pesc.id_phase = ?
        AND le.element_type_id = ?
      ORDER BY le.name
      `,
      [studyId, phaseId, LOC_ELEMENT_TYPE_COUNTRY],
    );
  }

  private async loadCountries(studyId: number, phaseId: number): Promise<string[]> {
    const rows = await this.loadCountryDetails(studyId, phaseId);
    return rows.map((row) => row.name);
  }

  private async loadRegions(studyId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ name: string }>>(
      `
      SELECT le.name AS name
      FROM project_expected_study_regions pesr
      INNER JOIN loc_elements le ON le.id = pesr.id_region
      WHERE pesr.expected_id = ?
        AND pesr.id_phase = ?
      ORDER BY le.name
      `,
      [studyId, phaseId],
    );
    return rows.map((row) => row.name);
  }

  private async loadCenterPartners(studyId: number, phaseId: number): Promise<OicrInstitutionRow[]> {
    return this.loadInstitutionPartners(studyId, phaseId, EXPECTED_STUDIES_PARTNERSHIP_TYPE_CENTER);
  }

  private async loadExternalInstitutions(studyId: number, phaseId: number): Promise<OicrInstitutionRow[]> {
    const rows = await this.dataSource.query<Array<{ institutionId: number }>>(
      `
      SELECT pesi.institution_id AS institutionId
      FROM project_expected_study_institutions pesi
      WHERE pesi.expected_id = ?
        AND pesi.id_phase = ?
      ORDER BY pesi.institution_id
      `,
      [studyId, phaseId],
    );
    return this.loadInstitutionsByIds(rows.map((row) => row.institutionId));
  }

  private async loadInstitutionPartners(
    studyId: number,
    phaseId: number,
    partnerTypeId: number,
  ): Promise<OicrInstitutionRow[]> {
    const rows = await this.dataSource.query<Array<{ institutionId: number }>>(
      `
      SELECT pesp.institution_id AS institutionId
      FROM project_expected_study_partnerships pesp
      WHERE pesp.expected_id = ?
        AND pesp.id_phase = ?
        AND pesp.is_active = 1
        AND pesp.expected_study_partner_type_id = ?
      ORDER BY pesp.institution_id
      `,
      [studyId, phaseId, partnerTypeId],
    );
    return this.loadInstitutionsByIds(rows.map((row) => row.institutionId));
  }

  private async loadInstitutionsByIds(institutionIds: number[]): Promise<OicrInstitutionRow[]> {
    if (institutionIds.length === 0) {
      return [];
    }
    const placeholders = institutionIds.map(() => '?').join(', ');
    return this.dataSource.query<OicrInstitutionRow[]>(
      `
      SELECT
        ${INSTITUTION_COMPOSED_NAME_SQL} AS name,
        COALESCE(it.name, 'Not available') AS type,
        ${INSTITUTION_HQ_SQL} AS headquarter
      FROM institutions i
      LEFT JOIN institution_types it ON it.id = i.institution_type_id
      WHERE i.id IN (${placeholders})
      ORDER BY i.id
      `,
      institutionIds,
    );
  }

  private async loadInnovations(studyId: number, phaseId: number): Promise<OicrInnovationRow[]> {
    return this.dataSource.query<OicrInnovationRow[]>(
      `
      SELECT
        pi.id AS innovationId,
        pii.title AS title
      FROM project_expected_study_innovations pesi
      INNER JOIN project_innovations pi ON pi.id = pesi.project_innovation_id
      LEFT JOIN project_innovation_info pii
        ON pii.project_innovation_id = pi.id
        AND pii.id_phase = ?
      WHERE pesi.expected_id = ?
        AND pesi.id_phase = ?
      ORDER BY pi.id
      `,
      [phaseId, studyId, phaseId],
    );
  }

  private async loadReferences(studyId: number, phaseId: number): Promise<OicrReferenceRow[]> {
    return this.dataSource.query<OicrReferenceRow[]>(
      `
      SELECT
        pesr.id AS id,
        pesr.reference AS reference,
        pesr.link AS link,
        pesr.is_external_author AS externalAuthor
      FROM project_expected_study_references pesr
      WHERE pesr.project_expected_study_id = ?
        AND pesr.id_phase = ?
      ORDER BY pesr.id
      `,
      [studyId, phaseId],
    );
  }

  private async loadQuantifications(studyId: number, phaseId: number): Promise<OicrQuantificationRow[]> {
    return this.dataSource.query<OicrQuantificationRow[]>(
      `
      SELECT
        qt.name AS type,
        CAST(pesq.number AS CHAR) AS number,
        COALESCE(pesq.target_unit, '') AS unit,
        pesq.comments AS comments
      FROM project_expected_study_quantifications pesq
      INNER JOIN quantification_types qt ON qt.id = pesq.quantification_type_id
      WHERE pesq.expected_id = ?
        AND pesq.id_phase = ?
        AND qt.name IS NOT NULL
      ORDER BY pesq.id
      `,
      [studyId, phaseId],
    );
  }

  private async loadPerformanceIndicators(studyId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ description: string }>>(
      `
      SELECT cpo.description AS description
      FROM project_expected_study_crp_outcomes pesco
      INNER JOIN crp_program_outcomes cpo ON cpo.id = pesco.crp_outcome_id
      WHERE pesco.expected_id = ?
        AND pesco.id_phase = ?
        AND cpo.description IS NOT NULL
      ORDER BY pesco.id
      `,
      [studyId, phaseId],
    );
    return rows.map((row) => row.description);
  }

  private async loadLinks(studyId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ link: string }>>(
      `
      SELECT pesl.link AS link
      FROM project_expected_study_links pesl
      WHERE pesl.expected_id = ?
        AND pesl.id_phase = ?
        AND pesl.link IS NOT NULL
        AND TRIM(pesl.link) != ''
      ORDER BY pesl.id
      `,
      [studyId, phaseId],
    );
    return rows.map((row) => row.link);
  }

  private async loadPublications(studyId: number, phaseId: number): Promise<OicrPublicationRow[]> {
    return this.dataSource.query<OicrPublicationRow[]>(
      `
      SELECT
        pesp.name AS name,
        pesp.position AS position,
        pesp.affiliation AS affiliation
      FROM project_expected_study_publications pesp
      WHERE pesp.expected_id = ?
        AND pesp.id_phase = ?
        AND pesp.is_active = 1
      ORDER BY pesp.id
      `,
      [studyId, phaseId],
    );
  }

  private async loadStudyProjects(studyId: number, phaseId: number): Promise<OicrStudyProjectRow[]> {
    return this.dataSource.query<OicrStudyProjectRow[]>(
      `
      SELECT
        p.id AS projectId,
        p.acronym AS acronym
      FROM expected_study_projects esp
      INNER JOIN projects p ON p.id = esp.project_id
      WHERE esp.expected_id = ?
        AND esp.id_phase = ?
        AND esp.is_active = 1
      ORDER BY p.id DESC
      `,
      [studyId, phaseId],
    );
  }

  private async loadAllianceLevers(studyId: number, phaseId: number): Promise<OicrAllianceLeverRow[]> {
    return this.dataSource.query<OicrAllianceLeverRow[]>(
      `
      SELECT
        al.id AS leverId,
        al.name AS leverName,
        al.description AS leverDescription,
        alo.description AS outcomeDescription
      FROM project_expected_study_alliance_levers_outcomes pesalo
      INNER JOIN alliance_levers al ON al.id = pesalo.alliance_lever_id
      LEFT JOIN alliance_lever_outcomes alo ON alo.id = pesalo.lever_outcome_id
      WHERE pesalo.expected_id = ?
        AND pesalo.id_phase = ?
        AND pesalo.is_active = 1
      ORDER BY pesalo.id
      `,
      [studyId, phaseId],
    );
  }

  private async loadSdgLevers(studyId: number, phaseId: number): Promise<OicrSdgLeverRow[]> {
    return this.dataSource.query<OicrSdgLeverRow[]>(
      `
      SELECT
        pessal.is_primary AS isPrimary,
        sdg.code AS sdgCode,
        sdg.name AS sdgName,
        al.name AS leverName,
        al.description AS leverDescription
      FROM project_expected_study_sdg_alliance_levers pessal
      LEFT JOIN sdg_contributions sdg ON sdg.id = pessal.sdg_contribution_id
      LEFT JOIN alliance_levers al ON al.id = pessal.alliance_lever_id
      WHERE pessal.expected_id = ?
        AND pessal.id_phase = ?
        AND pessal.is_active = 1
      ORDER BY pessal.id
      `,
      [studyId, phaseId],
    );
  }

  private async loadImpactAreas(studyId: number, phaseId: number): Promise<OicrImpactAreaRow[]> {
    return this.dataSource.query<OicrImpactAreaRow[]>(
      `
      SELECT
        sia.id AS id,
        sia.name AS name
      FROM project_expected_study_impact_areas pesia
      INNER JOIN st_impact_areas sia ON sia.id = pesia.st_impact_area_id
      WHERE pesia.expected_id = ?
        AND pesia.id_phase = ?
        AND pesia.is_active = 1
      ORDER BY pesia.id
      LIMIT 1
      `,
      [studyId, phaseId],
    );
  }

  private async loadGlobalTargets(studyId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ name: string }>>(
      `
      SELECT gt.name AS name
      FROM project_expected_study_global_targets pesgt
      INNER JOIN global_targets gt ON gt.id = pesgt.global_target_id
      WHERE pesgt.expected_id = ?
        AND pesgt.id_phase = ?
        AND pesgt.is_active = 1
        AND gt.name IS NOT NULL
      ORDER BY pesgt.id
      `,
      [studyId, phaseId],
    );
    return [...new Set(rows.map((row) => row.name))];
  }

  private async loadSrfTargets(studyId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ title: string }>>(
      `
      SELECT ssi.title AS title
      FROM project_expected_study_srf_targets pesst
      INNER JOIN srf_slo_indicators ssi ON ssi.id = pesst.srf_target_id
      WHERE pesst.expected_id = ?
        AND pesst.id_phase = ?
        AND ssi.title IS NOT NULL
      ORDER BY pesst.id
      `,
      [studyId, phaseId],
    );
    return rows.map((row) => row.title);
  }

  private async loadSubIdos(studyId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ description: string }>>(
      `
      SELECT ssi.description AS description
      FROM project_expected_study_sub_ido pessi
      INNER JOIN srf_sub_idos ssi ON ssi.id = pessi.sub_ido_id
      WHERE pessi.expected_id = ?
        AND pessi.id_phase = ?
        AND ssi.description IS NOT NULL
      ORDER BY pessi.id
      `,
      [studyId, phaseId],
    );
    return rows.map((row) => row.description);
  }

  private async loadCrps(studyId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ label: string }>>(
      `
      SELECT
        CASE
          WHEN gu.acronym IS NOT NULL AND TRIM(gu.acronym) != '' THEN CONCAT(gu.acronym, ' - ', gu.name)
          ELSE gu.name
        END AS label
      FROM project_expected_study_crp pesc
      INNER JOIN global_units gu ON gu.id = pesc.global_unit_id
      WHERE pesc.expected_id = ?
        AND pesc.id_phase = ?
      ORDER BY gu.id
      `,
      [studyId, phaseId],
    );
    return rows.map((row) => row.label);
  }

  private async loadFlagships(studyId: number, phaseId: number): Promise<string[]> {
    return this.loadCrpPrograms(studyId, phaseId, CRP_PROGRAM_TYPE_FLAGSHIP);
  }

  private async loadRegionalPrograms(studyId: number, phaseId: number): Promise<string[]> {
    return this.loadCrpPrograms(studyId, phaseId, CRP_PROGRAM_TYPE_REGIONAL);
  }

  private async loadCrpPrograms(
    studyId: number,
    phaseId: number,
    programType: number,
  ): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ label: string }>>(
      `
      SELECT
        CASE
          WHEN cp.acronym IS NOT NULL AND TRIM(cp.acronym) != '' THEN CONCAT(cp.acronym, ' - ', cp.name)
          ELSE cp.name
        END AS label
      FROM project_expected_study_flagships pesf
      INNER JOIN crp_programs cp ON cp.id = pesf.crp_program_id
      WHERE pesf.expected_id = ?
        AND pesf.id_phase = ?
        AND cp.program_type = ?
      ORDER BY cp.id
      `,
      [studyId, phaseId, programType],
    );
    return rows.map((row) => row.label);
  }

  private async loadPolicies(studyId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ label: string }>>(
      `
      SELECT
        CONCAT(pp.id, ' - ', COALESCE(ppi.title, 'Untitled')) AS label
      FROM project_expected_study_policies pesp
      INNER JOIN project_policies pp ON pp.id = pesp.project_policy_id
      INNER JOIN project_policy_info ppi
        ON ppi.project_policy_id = pp.id AND ppi.id_phase = ?
      WHERE pesp.expected_id = ?
        AND pesp.id_phase = ?
      ORDER BY pp.id
      `,
      [phaseId, studyId, phaseId],
    );
    return rows.map((row) => row.label);
  }

  private async loadProjectOutcomes(studyId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ label: string }>>(
      `
      SELECT
        CASE
          WHEN cp.acronym IS NOT NULL AND TRIM(cp.acronym) != '' AND cpo.description IS NOT NULL
            THEN CONCAT(cp.acronym, ' Outcome: ', cpo.description)
          WHEN cpo.description IS NOT NULL THEN cpo.description
          ELSE '-'
        END AS label
      FROM project_expected_study_project_outcomes pespo
      INNER JOIN project_outcomes po ON po.id = pespo.project_outcome_id
      INNER JOIN crp_program_outcomes cpo ON cpo.id = po.outcome_id
      LEFT JOIN crp_programs cp ON cp.id = cpo.crp_program_id
      WHERE pespo.expected_id = ?
        AND pespo.id_phase = ?
      ORDER BY pespo.id
      `,
      [studyId, phaseId],
    );
    return rows.map((row) => row.label);
  }

  private async loadCrpOutcomes(studyId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ label: string }>>(
      `
      SELECT
        CASE
          WHEN cp.acronym IS NOT NULL AND TRIM(cp.acronym) != '' AND cpo.description IS NOT NULL
            THEN CONCAT(cp.acronym, ' Outcome: ', cpo.description)
          WHEN cpo.description IS NOT NULL THEN cpo.description
          ELSE '-'
        END AS label
      FROM project_expected_study_crp_outcomes pesco
      INNER JOIN crp_program_outcomes cpo ON cpo.id = pesco.crp_outcome_id
      LEFT JOIN crp_programs cp ON cp.id = cpo.crp_program_id
      WHERE pesco.expected_id = ?
        AND pesco.id_phase = ?
      ORDER BY pesco.id
      `,
      [studyId, phaseId],
    );
    return rows.map((row) => row.label);
  }

  private async hasAllianceInstitution(studyId: number, phaseId: number): Promise<boolean> {
    const rows = await this.dataSource.query<Array<{ found: number }>>(
      `
      SELECT 1 AS found
      FROM project_expected_study_partnerships pesp
      INNER JOIN institutions i ON i.id = pesp.institution_id
      WHERE pesp.expected_id = ?
        AND pesp.id_phase = ?
        AND pesp.is_active = 1
        AND pesp.expected_study_partner_type_id = ?
        AND (
          i.id = 7320
          OR LOWER(i.name) LIKE CONCAT('%', ?, '%')
        )
      LIMIT 1
      `,
      [studyId, phaseId, EXPECTED_STUDIES_PARTNERSHIP_TYPE_CENTER, ALLIANCE_INSTITUTION_NAME],
    );
    return rows.length > 0;
  }
}
