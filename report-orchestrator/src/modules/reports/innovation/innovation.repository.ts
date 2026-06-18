import { DataSource } from 'typeorm';

import {
  ALLIANCE_INSTITUTION_NAME,
  GEOGRAPHIC_SCOPE,
  LOC_ELEMENT_TYPE_COUNTRY,
} from '../oicr/oicr.constants';
import {
  INSTITUTION_COMPOSED_NAME_SQL,
  INSTITUTION_HQ_SQL,
} from './innovation.constants';
import {
  InnovationActorRow,
  InnovationAllianceOrgRow,
  InnovationBundleRow,
  InnovationCenterRow,
  InnovationComplementarySolutionRow,
  InnovationContext,
  InnovationCoreRow,
  InnovationGeographicScopeRow,
  InnovationInstitutionRow,
  InnovationLocElementRow,
  InnovationMilestoneRow,
  InnovationReferenceRow,
  InnovationReferenceUrlRow,
  InnovationSdgRow,
  InnovationStudyRow,
} from './innovation.types';

/**
 * Loads innovation data for pdf.generate JSON assembly.
 * SQL mirrors ProjectInnovationSummaryAction.generateAndSendJson() field sources.
 */
export class InnovationRepository {
  constructor(private readonly dataSource: DataSource) {}

  async loadInnovationContext(
    innovationId: number,
    phaseId: number,
  ): Promise<InnovationContext | null> {
    const core = await this.loadCore(innovationId, phaseId);
    if (!core) {
      return null;
    }

    const geographicScopes = await this.loadGeographicScopes(innovationId, phaseId);
    const isNational = geographicScopes.some(
      (scope) =>
        scope.scopeId !== GEOGRAPHIC_SCOPE.GLOBAL && scope.scopeId !== GEOGRAPHIC_SCOPE.REGIONAL,
    );
    const isRegional = geographicScopes.some((scope) => scope.scopeId === GEOGRAPHIC_SCOPE.REGIONAL);
    const shouldLoadCountries = isNational || core.hasSpecifiedOutputCountries === 1;

    const [
      countries,
      regions,
      contributingOrganizations,
      centers,
      studies,
      sharedProjects,
      allianceLevers,
      sdgs,
      impactAreas,
      allianceOrganizations,
      actors,
      toolCategories,
      references,
      referenceUrls,
      referenceComplementarySolutions,
      crpOutcomes,
      crps,
      projectOutcomes,
      milestones,
      subIdos,
      contacts,
      linkedDeliverables,
      organizations,
      bundles,
      complementarySolutions,
      hasAllianceInstitution,
    ] = await Promise.all([
      shouldLoadCountries ? this.loadCountries(innovationId, phaseId) : Promise.resolve([]),
      isRegional ? this.loadRegions(innovationId, phaseId) : Promise.resolve([]),
      this.loadContributingOrganizations(innovationId, phaseId),
      this.loadCenters(innovationId, phaseId),
      this.loadStudies(innovationId, phaseId),
      this.loadSharedProjects(innovationId, phaseId),
      this.loadAllianceLevers(innovationId, phaseId),
      this.loadSdgs(innovationId, phaseId),
      this.loadImpactAreas(innovationId, phaseId),
      this.loadAllianceOrganizations(innovationId, phaseId),
      this.loadActors(innovationId, phaseId),
      this.loadToolCategories(innovationId, phaseId),
      this.loadReferences(innovationId, phaseId),
      this.loadReferenceUrls(innovationId, phaseId),
      this.loadReferenceComplementarySolutions(innovationId, phaseId),
      this.loadCrpOutcomes(innovationId, phaseId),
      this.loadCrps(innovationId, phaseId),
      this.loadProjectOutcomes(innovationId, phaseId),
      this.loadMilestones(innovationId, phaseId),
      this.loadSubIdos(innovationId, phaseId),
      this.loadContacts(innovationId, phaseId),
      this.loadLinkedDeliverables(innovationId, phaseId),
      this.loadOrganizations(innovationId, phaseId),
      this.loadBundles(innovationId, phaseId),
      this.loadComplementarySolutions(innovationId, phaseId),
      this.hasAllianceInstitution(innovationId, phaseId),
    ]);

    return {
      core,
      geographicScopes,
      countries,
      regions,
      contributingOrganizations,
      centers,
      studies,
      sharedProjects,
      allianceLevers,
      sdgs,
      impactAreas,
      allianceOrganizations,
      actors,
      toolCategories,
      references,
      referenceUrls,
      referenceComplementarySolutions,
      crpOutcomes,
      crps,
      projectOutcomes,
      milestones,
      subIdos,
      contacts,
      linkedDeliverables,
      organizations,
      bundles,
      complementarySolutions,
      hasAllianceInstitution,
      isNational,
      isRegional,
    };
  }

  private async loadCore(innovationId: number, phaseId: number): Promise<InnovationCoreRow | null> {
    const rows = await this.dataSource.query<InnovationCoreRow[]>(
      `
      SELECT
        pi.id AS innovationId,
        pi.project_id AS projectId,
        p.acronym AS projectAcronym,
        (
          SELECT gu.acronym
          FROM global_unit_projects gup
          INNER JOIN global_units gu ON gu.id = gup.global_unit_id
          WHERE gup.project_id = p.id AND gup.is_active = 1
          LIMIT 1
        ) AS crpAcronym,
        pii.id_phase AS phaseId,
        pii.title AS title,
        pii.short_title AS shortTitle,
        pii.number_of_innovations AS innovationNumber,
        pii.narrative AS narrative,
        pii.innovation_importance AS innovationImportance,
        pii.year AS year,
        pii.is_clear_lead AS clearLead,
        SUBSTRING_INDEX(sr.name, ' - ', 1) AS readinessScale,
        sr.name AS readinessScaleFull,
        ripr.name AS repIndPhaseResearchPartnership,
        risi.name AS repIndStageInnovation,
        pii.description_stage AS descriptionStage,
        rir.name AS repIndRegion,
        riit.name AS repIndInnovationType,
        pii.other_innovation_type AS otherInnovationType,
        riit.prms_name_equivalent AS repIndInnovationTypePrms,
        riin.name AS repIndInnovationNature,
        ridi.name AS repIndDegreeInnovation,
        pii.evidence_link AS evidenceLink,
        pii.adaptative_research_narrative AS adaptativeResearchNarrative,
        pii.is_innovation_bundle AS innovationBundle,
        rigfl_g.name AS genderFocusLevel,
        pii.gender_explaniation AS genderExplanation,
        rigfl_y.name AS youthFocusLevel,
        pii.youth_explaniation AS youthExplanation,
        pii.are_users_determined AS areUsersDetermined,
        pii.has_knowledge_potential_id AS hasKnowledgePotentialId,
        pii.reason_knowledge_potential AS reasonKnowledgePotential,
        pii.has_specified_output_countries AS hasSpecifiedOutputCountries,
        CASE
          WHEN i_lo.id IS NULL THEN NULL
          WHEN i_lo.acronym IS NOT NULL AND TRIM(i_lo.acronym) != ''
            THEN CONCAT(i_lo.acronym, ' - ', i_lo.name)
          ELSE i_lo.name
        END AS leadOrganization,
        i_ip.acronym AS intellectualProperty,
        CASE
          WHEN i_ip.id IS NULL THEN NULL
          WHEN i_ip.acronym IS NOT NULL AND TRIM(i_ip.acronym) != ''
            THEN CONCAT(i_ip.acronym, ' - ', i_ip.name)
          ELSE i_ip.name
        END AS intellectualPropertyInstitution,
        pii.has_legal_restrictions AS hasLegalRestrictions,
        pii.has_asset_potential AS hasAssetPotential,
        pii.has_further_development AS hasFurtherDevelopment,
        pii.has_cgiar_contribution AS hasCgiarContribution,
        pii.reason_not_cgiar_contribution AS reasonNotCgiarContribution,
        pii.beneficiaries_narrative AS beneficiariesNarrative,
        pii.knowledge_methods_and_tools_narrative AS knowledgeMethodsAndToolsNarrative,
        pii.knowledge_results_narrative AS knowledgeResultsNarrative,
        pii.knowledge_tool_uses_narrative AS knowledgeToolUsesNarrative,
        pii.is_cheaper_alternatives AS cheaperAlternatives,
        pii.is_simpler_use AS simplerUse,
        pii.is_perform_better AS performBetter,
        pii.is_innovation_desirable AS innovationDesirable,
        pii.is_innovation_commercially AS innovationCommercially,
        pii.is_innovation_supported AS innovationSupported,
        pii.is_evidence_uptake AS evidenceUptake,
        pii.is_foresee_barriers AS foreseeBarriers,
        ias_g.description AS genderScore,
        ias_cc.description AS climateChangeScore,
        ias_fs.description AS foodSecurityScore,
        ias_env.description AS environmentalScore,
        ias_p.description AS povertyScore
      FROM project_innovations pi
      INNER JOIN project_innovation_info pii
        ON pii.project_innovation_id = pi.id AND pii.id_phase = ?
      INNER JOIN projects p ON p.id = pi.project_id
      LEFT JOIN scaling_readiness sr ON sr.id = pii.readiness_scale
      LEFT JOIN rep_ind_phase_research_partnerships ripr ON ripr.id = pii.phase_research_id
      LEFT JOIN rep_ind_stage_innovations risi ON risi.id = pii.stage_innovation_id
      LEFT JOIN rep_ind_regions rir ON rir.id = pii.rep_ind_region_id
      LEFT JOIN rep_ind_innovation_types riit ON riit.id = pii.innovation_type_id
      LEFT JOIN rep_ind_innovation_natures riin ON riin.id = pii.innovation_nature_id
      LEFT JOIN rep_ind_degree_innovation ridi ON ridi.id = pii.rep_ind_degree_innovation_id
      LEFT JOIN rep_ind_gender_youth_focus_levels rigfl_g ON rigfl_g.id = pii.gender_focus_level_id
      LEFT JOIN rep_ind_gender_youth_focus_levels rigfl_y ON rigfl_y.id = pii.youth_focus_level_id
      LEFT JOIN institutions i_lo ON i_lo.id = pii.lead_organization_id
      LEFT JOIN institutions i_ip ON i_ip.id = pii.intellectual_property_institution_id
      LEFT JOIN impact_area_scores ias_g ON ias_g.id = pii.gender_score_id
      LEFT JOIN impact_area_scores ias_cc ON ias_cc.id = pii.climate_change_score_id
      LEFT JOIN impact_area_scores ias_fs ON ias_fs.id = pii.food_security_score_id
      LEFT JOIN impact_area_scores ias_env ON ias_env.id = pii.environmental_score_id
      LEFT JOIN impact_area_scores ias_p ON ias_p.id = pii.poverty_jobs_score_id
      WHERE pi.id = ?
      LIMIT 1
      `,
      [phaseId, innovationId],
    );
    return rows[0] ?? null;
  }

  private async loadGeographicScopes(
    innovationId: number,
    phaseId: number,
  ): Promise<InnovationGeographicScopeRow[]> {
    return this.dataSource.query<InnovationGeographicScopeRow[]>(
      `
      SELECT
        rigs.id AS scopeId,
        rigs.name AS scopeName
      FROM project_innovation_geographic_scopes pigs
      INNER JOIN rep_ind_geographic_scopes rigs ON rigs.id = pigs.rep_ind_geographic_scope_id
      WHERE pigs.project_innovation_id = ?
        AND pigs.id_phase = ?
      `,
      [innovationId, phaseId],
    );
  }

  private async loadCountries(
    innovationId: number,
    phaseId: number,
  ): Promise<InnovationLocElementRow[]> {
    return this.dataSource.query<InnovationLocElementRow[]>(
      `
      SELECT le.name AS name, LOWER(le.iso_alpha_2) AS isoAlpha2
      FROM project_innovation_countries pic
      INNER JOIN loc_elements le ON le.id = pic.id_country
      WHERE pic.project_innovation_id = ?
        AND pic.id_phase = ?
        AND le.element_type_id = ?
      `,
      [innovationId, phaseId, LOC_ELEMENT_TYPE_COUNTRY],
    );
  }

  private async loadRegions(
    innovationId: number,
    phaseId: number,
  ): Promise<InnovationLocElementRow[]> {
    return this.dataSource.query<InnovationLocElementRow[]>(
      `
      SELECT le.name AS name
      FROM project_innovation_regions pir
      INNER JOIN loc_elements le ON le.id = pir.id_region
      WHERE pir.project_innovation_id = ?
        AND pir.id_phase = ?
      `,
      [innovationId, phaseId],
    );
  }

  private async loadContributingOrganizations(
    innovationId: number,
    phaseId: number,
  ): Promise<InnovationInstitutionRow[]> {
    return this.dataSource.query<InnovationInstitutionRow[]>(
      `
      SELECT
        ${INSTITUTION_COMPOSED_NAME_SQL} AS name,
        it.name AS type,
        ${INSTITUTION_HQ_SQL} AS headquarter,
        pico.is_scaling AS isScaling,
        pico.is_demand AS isDemand,
        pico.is_innovation AS isInnovation,
        pico.is_other AS isOther
      FROM project_innovation_contributing_organizations pico
      INNER JOIN institutions i ON i.id = pico.institution_id
      LEFT JOIN institution_types it ON it.id = i.institution_type_id
      WHERE pico.project_innovation_id = ?
        AND pico.id_phase = ?
      ORDER BY name
      `,
      [innovationId, phaseId],
    );
  }

  private async loadCenters(innovationId: number, phaseId: number): Promise<InnovationCenterRow[]> {
    return this.dataSource.query<InnovationCenterRow[]>(
      `
      SELECT
        i.name AS name,
        it.name AS type,
        ${INSTITUTION_HQ_SQL} AS headquarter
      FROM project_innovation_centers pic
      INNER JOIN institutions i ON i.id = pic.institution_id
      LEFT JOIN institution_types it ON it.id = i.institution_type_id
      WHERE pic.project_innovation_id = ?
        AND pic.id_phase = ?
      `,
      [innovationId, phaseId],
    );
  }

  private async loadStudies(innovationId: number, phaseId: number): Promise<InnovationStudyRow[]> {
    return this.dataSource.query<InnovationStudyRow[]>(
      `
      SELECT
        pesi.title AS name,
        st.name AS studyType
      FROM project_expected_study_innovations pesi_link
      INNER JOIN project_expected_studies pes ON pes.id = pesi_link.expected_id
      INNER JOIN project_expected_study_info pesi
        ON pesi.project_expected_study_id = pes.id AND pesi.id_phase = ?
      LEFT JOIN study_types st ON st.id = pesi.study_type_id
      WHERE pesi_link.project_innovation_id = ?
        AND pesi_link.id_phase = ?
      `,
      [phaseId, innovationId, phaseId],
    );
  }

  private async loadSharedProjects(innovationId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ acronym: string | null }>>(
      `
      SELECT p.acronym AS acronym
      FROM project_innovation_shared pis
      INNER JOIN projects p ON p.id = pis.project_id
      WHERE pis.project_innovation_id = ?
        AND pis.id_phase = ?
        AND pis.is_active = 1
      `,
      [innovationId, phaseId],
    );
    return rows.map((row) => row.acronym ?? '');
  }

  private async loadAllianceLevers(innovationId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ leverName: string | null }>>(
      `
      SELECT DISTINCT al.name AS leverName
      FROM project_innovation_alliance_levers pial
      INNER JOIN alliance_levers al ON al.id = pial.alliance_lever_id
      WHERE pial.innovation_id = ?
        AND pial.id_phase = ?
        AND pial.is_active = 1
      `,
      [innovationId, phaseId],
    );
    return rows.map((row) => row.leverName ?? '');
  }

  private async loadSdgs(innovationId: number, phaseId: number): Promise<InnovationSdgRow[]> {
    const rows = await this.dataSource.query<Array<{ shortName: string | null; fullName: string | null; icon: string | null }>>(
      `
      SELECT s.short_name AS shortName, s.full_name AS fullName, s.icon AS icon
      FROM project_innovation_sdgs pis
      INNER JOIN sustainable_development_goals s ON s.id = pis.sdg_id
      WHERE pis.innovation_id = ?
        AND pis.id_phase = ?
        AND pis.is_active = 1
      `,
      [innovationId, phaseId],
    );
    return rows.map((row) => ({
      name: row.shortName ?? row.fullName ?? '',
      icon: row.icon,
    })).filter((row) => row.name !== '');
  }

  private async loadImpactAreas(innovationId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ name: string | null }>>(
      `
      SELECT ia.name AS name
      FROM project_innovation_impact_areas piia
      INNER JOIN st_impact_areas ia ON ia.id = piia.st_impact_area_id
      WHERE piia.project_innovation_id = ?
        AND piia.id_phase = ?
        AND piia.is_active = 1
      `,
      [innovationId, phaseId],
    );
    return rows.map((row) => row.name ?? '');
  }

  private async loadAllianceOrganizations(
    innovationId: number,
    phaseId: number,
  ): Promise<InnovationAllianceOrgRow[]> {
    return this.dataSource.query<InnovationAllianceOrgRow[]>(
      `
      SELECT
        ${INSTITUTION_COMPOSED_NAME_SQL} AS name,
        piao.is_scaling_partner AS scalingPartner,
        it.name AS type,
        piao.number AS howMany
      FROM project_innovation_alliance_organizations piao
      INNER JOIN institutions i ON i.id = piao.institution_id
      LEFT JOIN institution_types it ON it.id = i.institution_type_id
      WHERE piao.project_innovation_id = ?
        AND piao.id_phase = ?
        AND piao.is_active = 1
      `,
      [innovationId, phaseId],
    );
  }

  private async loadActors(innovationId: number, phaseId: number): Promise<InnovationActorRow[]> {
    return this.dataSource.query<InnovationActorRow[]>(
      `
      SELECT
        a.name AS type,
        pia.total AS total,
        pia.other AS other,
        a.name AS name,
        pia.is_sex_age_not_apply AS sexAgeNotApply,
        pia.is_women_youth AS womenYouth,
        pia.women_youth_number AS womenYouthNumber,
        pia.is_women_not_youth AS womenNotYouth,
        pia.women_non_youth_number AS womenNotYouthNumber,
        pia.is_men_youth AS menYouth,
        pia.men_youth_number AS menYouthNumber,
        pia.is_men_not_youth AS menNotYouth,
        pia.men_non_youth_number AS menNotYouthNumber
      FROM project_innovation_actors pia
      INNER JOIN actors a ON a.id = pia.actor_id
      WHERE pia.innovation_id = ?
        AND pia.id_phase = ?
        AND pia.is_active = 1
      `,
      [innovationId, phaseId],
    );
  }

  private async loadToolCategories(innovationId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ name: string | null }>>(
      `
      SELECT tc.name AS name
      FROM project_innovation_tool_categories pitc
      INNER JOIN tool_function_categories tc ON tc.id = pitc.tool_category_id
      WHERE pitc.innovation_id = ?
        AND pitc.id_phase = ?
        AND pitc.is_active = 1
      `,
      [innovationId, phaseId],
    );
    return rows.filter((row) => row.name != null).map((row) => row.name as string);
  }

  private async loadReferences(
    innovationId: number,
    phaseId: number,
  ): Promise<InnovationReferenceRow[]> {
    return this.dataSource.query<InnovationReferenceRow[]>(
      `
      SELECT
        pir.has_evidence_by_deliverable AS evidenceByDeliverable,
        pir.deliverable_id AS deliverableId,
        di.title AS deliverableTitle,
        dc.name AS deliverableCategory,
        dt.name AS deliverableType,
        pir.link AS link,
        pir.reference AS reference,
        dc2.name AS typeCategory,
        dt2.name AS typeName,
        pir.is_gender AS gender,
        pir.is_climate_change AS climateChange,
        pir.is_nutrition AS nutrition,
        pir.is_environmental AS environmental,
        pir.is_poverty AS poverty,
        pir.is_innovation_readiness AS innovationReadiness
      FROM project_innovation_references pir
      LEFT JOIN deliverables d ON d.id = pir.deliverable_id
      LEFT JOIN deliverables_info di ON di.deliverable_id = d.id AND di.id_phase = ?
      LEFT JOIN deliverable_types dt ON dt.id = di.type_id
      LEFT JOIN deliverable_types dc ON dc.id = dt.parent_id
      LEFT JOIN deliverable_types dt2 ON dt2.id = pir.type_id
      LEFT JOIN deliverable_types dc2 ON dc2.id = dt2.parent_id
      WHERE pir.project_innovation_id = ?
        AND pir.id_phase = ?
        AND pir.is_active = 1
      ORDER BY pir.id
      `,
      [phaseId, innovationId, phaseId],
    );
  }

  private async loadReferenceUrls(
    innovationId: number,
    phaseId: number,
  ): Promise<InnovationReferenceUrlRow[]> {
    return this.dataSource.query<InnovationReferenceUrlRow[]>(
      `
      SELECT
        piru.has_evidence_by_deliverable AS evidenceByDeliverable,
        piru.deliverable_id AS deliverableId,
        di.title AS deliverableTitle,
        dc.name AS deliverableCategory,
        dt.name AS deliverableType,
        piru.link AS link,
        piru.reference AS reference,
        dc2.name AS typeCategory,
        dt2.name AS typeName
      FROM project_innovation_reference_urls piru
      LEFT JOIN deliverables d ON d.id = piru.deliverable_id
      LEFT JOIN deliverables_info di ON di.deliverable_id = d.id AND di.id_phase = ?
      LEFT JOIN deliverable_types dt ON dt.id = di.type_id
      LEFT JOIN deliverable_types dc ON dc.id = dt.parent_id
      LEFT JOIN deliverable_types dt2 ON dt2.id = piru.type_id
      LEFT JOIN deliverable_types dc2 ON dc2.id = dt2.parent_id
      WHERE piru.project_innovation_id = ?
        AND piru.id_phase = ?
        AND piru.is_active = 1
      ORDER BY piru.id
      `,
      [phaseId, innovationId, phaseId],
    );
  }

  private async loadReferenceComplementarySolutions(
    innovationId: number,
    phaseId: number,
  ): Promise<InnovationReferenceRow[]> {
    return this.dataSource.query<InnovationReferenceRow[]>(
      `
      SELECT
        pircs.has_evidence_by_deliverable AS evidenceByDeliverable,
        pircs.deliverable_id AS deliverableId,
        di.title AS deliverableTitle,
        dc.name AS deliverableCategory,
        dt.name AS deliverableType,
        pircs.link AS link,
        pircs.reference AS reference,
        dc2.name AS typeCategory,
        dt2.name AS typeName,
        NULL AS gender,
        NULL AS climateChange,
        NULL AS nutrition,
        NULL AS environmental,
        NULL AS poverty,
        NULL AS innovationReadiness
      FROM project_innovation_reference_complementary_solutions pircs
      LEFT JOIN deliverables d ON d.id = pircs.deliverable_id
      LEFT JOIN deliverables_info di ON di.deliverable_id = d.id AND di.id_phase = ?
      LEFT JOIN deliverable_types dt ON dt.id = di.type_id
      LEFT JOIN deliverable_types dc ON dc.id = dt.parent_id
      LEFT JOIN deliverable_types dt2 ON dt2.id = pircs.type_id
      LEFT JOIN deliverable_types dc2 ON dc2.id = dt2.parent_id
      WHERE pircs.project_innovation_id = ?
        AND pircs.id_phase = ?
        AND pircs.is_active = 1
      ORDER BY pircs.id
      `,
      [phaseId, innovationId, phaseId],
    );
  }

  private async loadCrpOutcomes(innovationId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ composedName: string | null }>>(
      `
      SELECT
        CASE
          WHEN gu.acronym IS NOT NULL AND cpo.description IS NOT NULL
            THEN CONCAT(gu.acronym, ' Outcome: ', cpo.description)
          ELSE '-'
        END AS composedName
      FROM project_innovation_crp_outcomes pico
      INNER JOIN crp_program_outcomes cpo ON cpo.id = pico.crp_outcome_id
      INNER JOIN crp_programs cp ON cp.id = cpo.crp_program_id
      INNER JOIN global_units gu ON gu.id = cp.global_unit_id
      WHERE pico.project_innovation_id = ?
        AND pico.id_phase = ?
      `,
      [innovationId, phaseId],
    );
    return rows.map((row) => row.composedName ?? null).filter((value): value is string => value != null);
  }

  private async loadCrps(innovationId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ name: string | null }>>(
      `
      SELECT
        CASE
          WHEN gu.acronym IS NOT NULL AND TRIM(gu.acronym) != ''
            THEN CONCAT(gu.acronym, ': ', gu.name)
          ELSE gu.name
        END AS name
      FROM project_innovation_crps pic
      INNER JOIN global_units gu ON gu.id = pic.global_unit_id
      WHERE pic.project_innovation_id = ?
        AND pic.id_phase = ?
      ORDER BY gu.name
      `,
      [innovationId, phaseId],
    );
    return rows.map((row) => row.name ?? null).filter((value): value is string => value != null);
  }

  private async loadProjectOutcomes(innovationId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ name: string | null }>>(
      `
      SELECT
        CASE
          WHEN gu.acronym IS NOT NULL AND cpo.description IS NOT NULL
            THEN CONCAT(gu.acronym, ' Outcome: ', cpo.description)
          ELSE CONCAT('Outcome ', po.id)
        END AS name
      FROM project_innovation_project_outcomes pipo
      INNER JOIN project_outcomes po ON po.id = pipo.project_outcome_id
      INNER JOIN crp_program_outcomes cpo ON cpo.id = po.outcome_id
      INNER JOIN crp_programs cp ON cp.id = cpo.crp_program_id
      INNER JOIN global_units gu ON gu.id = cp.global_unit_id
      WHERE pipo.project_innovation_id = ?
        AND pipo.id_phase = ?
        AND po.is_active = 1
      ORDER BY name
      `,
      [innovationId, phaseId],
    );
    return rows.map((row) => row.name ?? null).filter((value): value is string => value != null);
  }

  private async loadMilestones(innovationId: number, phaseId: number): Promise<InnovationMilestoneRow[]> {
    return this.dataSource.query<InnovationMilestoneRow[]>(
      `
      SELECT
        CASE
          WHEN cm.extended_year IS NOT NULL AND cm.extended_year != -1
            THEN CONCAT(cm.year, ' extended to ', cm.extended_year, ' - ', cm.title)
          ELSE CONCAT(cm.year, ' - ', cm.title)
        END AS name,
        pim.is_primary AS \`primary\`
      FROM project_innovation_milestones pim
      INNER JOIN crp_milestones cm ON cm.id = pim.crp_milestone_id
      WHERE pim.project_innovation_id = ?
        AND pim.id_phase = ?
      ORDER BY name
      `,
      [innovationId, phaseId],
    );
  }

  private async loadSubIdos(innovationId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ name: string | null }>>(
      `
      SELECT ssi.description AS name
      FROM project_innovation_sub_idos pisi
      INNER JOIN srf_sub_idos ssi ON ssi.id = pisi.sub_ido_id
      WHERE pisi.project_innovation_id = ?
        AND pisi.id_phase = ?
      ORDER BY ssi.description
      `,
      [innovationId, phaseId],
    );
    return rows.map((row) => row.name ?? null).filter((value): value is string => value != null);
  }

  private async loadContacts(innovationId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ contact: string | null }>>(
      `
      SELECT DISTINCT
        CASE
          WHEN inst_name IS NOT NULL AND user_name IS NOT NULL
            THEN CONCAT(inst_name, ' - ', user_name)
          ELSE COALESCE(inst_name, user_name)
        END AS contact
      FROM (
        SELECT
          ${INSTITUTION_COMPOSED_NAME_SQL.replace(/\bi\./g, 'inst.')} AS inst_name,
          CONCAT(u.last_name, ', ', u.first_name) AS user_name
        FROM project_innovation_partnerships pip
        INNER JOIN project_innovation_partnership_persons pipp
          ON pipp.partnership_id = pip.id AND pipp.is_active = 1
        INNER JOIN users u ON u.id = pipp.user_id
        LEFT JOIN institutions inst ON inst.id = pip.institution_id
        WHERE pip.project_innovation_id = ?
          AND pip.id_phase = ?
          AND pip.is_active = 1
          AND u.email IS NOT NULL
          AND TRIM(u.email) != ''
      ) contacts
      ORDER BY contact
      `,
      [innovationId, phaseId],
    );
    return rows.map((row) => row.contact ?? null).filter((value): value is string => value != null);
  }

  private async loadLinkedDeliverables(innovationId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ title: string | null }>>(
      `
      SELECT di.title AS title
      FROM project_innovation_deliverables pid
      INNER JOIN deliverables_info di
        ON di.deliverable_id = pid.deliverable_id AND di.id_phase = ? AND di.is_active = 1
      WHERE pid.project_innovation_id = ?
        AND pid.id_phase = ?
      ORDER BY di.title
      `,
      [phaseId, innovationId, phaseId],
    );
    return rows.map((row) => row.title ?? null).filter((value): value is string => value != null);
  }

  private async loadOrganizations(innovationId: number, phaseId: number): Promise<string[]> {
    const rows = await this.dataSource.query<Array<{ name: string | null }>>(
      `
      SELECT DISTINCT riot.name AS name
      FROM project_innovation_organizations pio
      INNER JOIN rep_ind_organization_types riot ON riot.id = pio.rep_ind_organization_type_id
      WHERE pio.project_innovation_id = ?
        AND pio.id_phase = ?
      ORDER BY riot.name
      `,
      [innovationId, phaseId],
    );
    return rows.map((row) => row.name ?? null).filter((value): value is string => value != null);
  }

  private async loadBundles(innovationId: number, phaseId: number): Promise<InnovationBundleRow[]> {
    return this.dataSource.query<InnovationBundleRow[]>(
      `
      SELECT
        pib.selected_innovation_id AS selectedInnovationId,
        sel_pii.title AS title,
        sel_p.acronym AS projectAcronym,
        riit.name AS projectType,
        sr_sel.name AS projectReadinessLevel
      FROM project_innovation_bundles pib
      INNER JOIN project_innovations sel_pi ON sel_pi.id = pib.selected_innovation_id
      LEFT JOIN project_innovation_info sel_pii
        ON sel_pii.project_innovation_id = sel_pi.id AND sel_pii.id_phase = ?
      LEFT JOIN projects sel_p ON sel_p.id = sel_pi.project_id
      LEFT JOIN rep_ind_innovation_types riit ON riit.id = sel_pii.innovation_type_id
      LEFT JOIN scaling_readiness sr_sel ON sr_sel.id = sel_pii.readiness_scale
      WHERE pib.project_innovation_id = ?
        AND pib.id_phase = ?
        AND pib.is_active = 1
      `,
      [phaseId, innovationId, phaseId],
    );
  }

  private async loadComplementarySolutions(
    innovationId: number,
    phaseId: number,
  ): Promise<InnovationComplementarySolutionRow[]> {
    return this.dataSource.query<InnovationComplementarySolutionRow[]>(
      `
      SELECT
        pics.id AS id,
        pics.title AS title,
        pics.short_title AS shortTitle,
        pics.short_description AS shortDescription,
        riit.name AS type,
        (
          SELECT GROUP_CONCAT(pif.title ORDER BY pif.title SEPARATOR '||')
          FROM project_innovation_complementary_solution_functions picsf
          INNER JOIN project_innovation_functions pif ON pif.id = picsf.project_innovation_function_id
          WHERE picsf.complementary_solution_id = pics.id
        ) AS functionTitles
      FROM project_innovation_complementary_solutions pics
      LEFT JOIN rep_ind_innovation_types riit ON riit.id = pics.project_innovation_type_id
      WHERE pics.project_innovation_id = ?
        AND pics.id_phase = ?
        AND pics.is_active = 1
      `,
      [innovationId, phaseId],
    );
  }

  private async hasAllianceInstitution(innovationId: number, phaseId: number): Promise<boolean> {
    const rows = await this.dataSource.query<Array<{ found: number }>>(
      `
      SELECT 1 AS found
      FROM project_innovation_centers pic
      INNER JOIN institutions i ON i.id = pic.institution_id
      WHERE pic.project_innovation_id = ?
        AND pic.id_phase = ?
        AND LOWER(i.name) LIKE ?
      LIMIT 1
      `,
      [innovationId, phaseId, `%${ALLIANCE_INSTITUTION_NAME}%`],
    );
    return rows.length > 0;
  }
}
