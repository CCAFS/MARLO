import { DataSource } from 'typeorm';

import {
  INSTITUTION_COMPOSED_NAME_SQL,
} from '../innovation/innovation.constants';
import {
  CRP_PROGRAM_TYPE_FLAGSHIP,
  CRP_PROGRAM_TYPE_REGIONAL,
  LOC_ELEMENT_TYPE_COUNTRY,
  LOC_ELEMENT_TYPE_REGION,
} from './cluster.constants';
import {
  ClusterActivityDeliverableRow,
  ClusterActivityMetaRow,
  ClusterActivitySummaryRow,
  ClusterContext,
  ClusterCoreRow,
  ClusterDeliverableSummaryRow,
  ClusterInnovationSummaryRow,
  ClusterLocationGroupRow,
  ClusterOutcomeCommunicationRow,
  ClusterOutcomeIndicatorRow,
  ClusterOutcomeMilestoneRow,
  ClusterOutcomeNextUserRow,
  ClusterOutcomeRow,
  ClusterPartnerLocationRow,
  ClusterPartnerPersonRow,
  ClusterPartnerRow,
  ClusterProgramRow,
} from './cluster.types';
import { deliverableMatchesSelectedYear } from './cluster-deliverable-filter';

/**
 * Loads cluster / project summary data for pdf.generate JSON assembly.
 * Mirrors ReportingSummaryAction.generateAndSendJson() field sources.
 */
export class ClusterRepository {
  constructor(private readonly dataSource: DataSource) {}

  async loadClusterContext(projectId: number, phaseId: number): Promise<ClusterContext | null> {
    const core = await this.loadCore(projectId, phaseId);
    if (!core) {
      return null;
    }

    const hasRegions = core.noRegional !== 1;

    const [
      flagships,
      regions,
      clusterActivities,
      partners,
      partnerLocations,
      partnerPersons,
      locationGroups,
      outcomes,
      outcomeMilestones,
      outcomeIndicators,
      outcomeCommunications,
      outcomeNextUsers,
      studyIds,
      innovations,
      deliverableRows,
      activities,
      activityDeliverables,
    ] = await Promise.all([
      this.loadProgramFocus(projectId, phaseId, CRP_PROGRAM_TYPE_FLAGSHIP),
      hasRegions ? this.loadProgramFocus(projectId, phaseId, CRP_PROGRAM_TYPE_REGIONAL) : Promise.resolve([]),
      this.loadClusterActivities(projectId, phaseId),
      this.loadPartners(projectId, phaseId),
      this.loadPartnerLocations(projectId, phaseId),
      this.loadPartnerPersons(projectId, phaseId),
      this.loadLocationGroups(projectId, phaseId),
      this.loadOutcomes(projectId, phaseId),
      this.loadOutcomeMilestones(projectId, phaseId, core.phaseYear),
      this.loadOutcomeIndicators(projectId, phaseId),
      this.loadOutcomeCommunications(projectId, phaseId, core.phaseYear),
      this.loadOutcomeNextUsers(projectId, phaseId),
      this.loadStudyIds(projectId, phaseId, core.phaseYear),
      this.loadInnovationSummaries(projectId, phaseId, core.phaseYear),
      this.loadDeliverableSummaries(projectId, phaseId),
      this.loadActivitySummaries(projectId, phaseId),
      this.loadActivityDeliverables(projectId, phaseId),
    ]);

    const deliverables = deliverableRows.filter((row) =>
      deliverableMatchesSelectedYear(row, core.phaseYear),
    );

    return {
      core,
      flagships,
      regions,
      clusterActivities,
      partners,
      partnerLocations,
      partnerPersons,
      locationGroups,
      outcomes,
      outcomeMilestones,
      outcomeIndicators,
      outcomeCommunications,
      outcomeNextUsers,
      studyIds,
      innovations,
      deliverables,
      activities,
      activityDeliverables,
      hasRegions,
    };
  }

  private async loadCore(projectId: number, phaseId: number): Promise<ClusterCoreRow | null> {
    const rows = await this.dataSource.query<ClusterCoreRow[]>(
      `
      SELECT
        p.id AS projectId,
        UPPER(p.acronym) AS projectAcronym,
        pi.title AS projectTitle,
        pi.id_phase AS phaseId,
        ph.year AS phaseYear,
        ph.description AS cycle,
        gu.acronym AS loggedCenter,
        pi.summary AS summary,
        pi.challenges_solutions AS challengesSolutions,
        pi.lessons_learned AS lessonsLearned,
        DATE_FORMAT(pi.start_date, '%b %Y') AS startDate,
        DATE_FORMAT(pi.end_date, '%b %Y') AS endDate,
        li.name AS liaisonInstitution,
        ct.name AS clusterType,
        pi.status AS status,
        pi.no_regional AS noRegional,
        pi.cross_cutting_na AS crossCuttingNa,
        pi.cross_cutting_capacity AS crossCuttingCapacity,
        pi.cross_cutting_gender AS crossCuttingGender,
        pi.cross_cutting_youth AS crossCuttingYouth,
        pi.is_location_global AS locationGlobal,
        pi.is_location_regional AS locationRegional,
        (
          SELECT ${INSTITUTION_COMPOSED_NAME_SQL.replace(/\bi\./g, 'i_lo.')}
          FROM project_partners pp
          INNER JOIN project_partner_persons ppp
            ON ppp.project_partner_id = pp.id AND ppp.is_active = 1 AND ppp.contact_type = 'PL'
          INNER JOIN institutions i_lo ON i_lo.id = pp.institution_id
          WHERE pp.project_id = p.id AND pp.id_phase = pi.id_phase AND pp.is_active = 1
          LIMIT 1
        ) AS leadOrganization,
        (
          SELECT CONCAT(u.last_name, ', ', u.first_name)
          FROM project_partners pp
          INNER JOIN project_partner_persons ppp
            ON ppp.project_partner_id = pp.id AND ppp.is_active = 1 AND ppp.contact_type = 'PL'
          INNER JOIN users u ON u.id = ppp.user_id
          WHERE pp.project_id = p.id AND pp.id_phase = pi.id_phase AND pp.is_active = 1
          LIMIT 1
        ) AS leaderName
      FROM projects p
      INNER JOIN projects_info pi ON pi.project_id = p.id AND pi.id_phase = ?
      INNER JOIN phases ph ON ph.id = pi.id_phase
      LEFT JOIN global_units gu ON gu.id = ph.global_unit_id
      LEFT JOIN liaison_institutions li ON li.id = pi.liaison_institution_id
      LEFT JOIN cluster_types ct ON ct.id = pi.type_id
      WHERE p.id = ?
      LIMIT 1
      `,
      [phaseId, projectId],
    );
    return rows[0] ?? null;
  }

  private async loadProgramFocus(
    projectId: number,
    phaseId: number,
    programType: number,
  ): Promise<ClusterProgramRow[]> {
    return this.dataSource.query<ClusterProgramRow[]>(
      `
      SELECT
        cp.id AS id,
        cp.name AS name,
        cp.acronym AS acronym,
        CASE
          WHEN cp.acronym IS NOT NULL AND TRIM(cp.acronym) != '' THEN CONCAT(cp.acronym, ': ', cp.name)
          ELSE cp.name
        END AS composedName
      FROM project_focuses pf
      INNER JOIN crp_programs cp ON cp.id = pf.program_id
      LEFT JOIN global_units gu ON gu.id = cp.global_unit_id
      WHERE pf.project_id = ?
        AND pf.id_phase = ?
        AND pf.is_active = 1
        AND cp.program_type = ?
        AND cp.area_id IS NULL
        AND (gu.global_unit_type_id IS NULL OR gu.global_unit_type_id != 4)
      ORDER BY cp.acronym
      `,
      [projectId, phaseId, programType],
    );
  }

  private async loadClusterActivities(
    projectId: number,
    phaseId: number,
  ): Promise<ClusterActivityMetaRow[]> {
    return this.dataSource.query<ClusterActivityMetaRow[]>(
      `
      SELECT DISTINCT
        cca.id AS id,
        CASE
          WHEN cca.identifier IS NOT NULL AND TRIM(cca.identifier) != ''
            THEN CONCAT(cca.identifier, ' : ', cca.description)
          ELSE cca.description
        END AS name,
        cca.identifier AS identifier
      FROM project_cluster_activities pca
      INNER JOIN crp_cluster_of_activities cca ON cca.id = pca.cluster_activity_id
      WHERE pca.project_id = ?
        AND pca.id_phase = ?
        AND pca.is_active = 1
      ORDER BY name
      `,
      [projectId, phaseId],
    );
  }

  private async loadPartners(projectId: number, phaseId: number): Promise<ClusterPartnerRow[]> {
    return this.dataSource.query<ClusterPartnerRow[]>(
      `
      SELECT
        pp.id AS id,
        ${INSTITUTION_COMPOSED_NAME_SQL} AS institutionName,
        i.acronym AS institutionAcronym,
        pp.responsibilities AS responsibilities
      FROM project_partners pp
      INNER JOIN institutions i ON i.id = pp.institution_id
      WHERE pp.project_id = ?
        AND pp.id_phase = ?
        AND pp.is_active = 1
      ORDER BY institutionName
      `,
      [projectId, phaseId],
    );
  }

  private async loadPartnerLocations(
    projectId: number,
    phaseId: number,
  ): Promise<ClusterPartnerLocationRow[]> {
    return this.dataSource.query<ClusterPartnerLocationRow[]>(
      `
      SELECT
        pp.id AS partnerId,
        CASE
          WHEN il.is_headquater = 1 THEN CONCAT('HQ: ', le.name)
          ELSE le.name
        END AS name,
        le.name AS country,
        LOWER(le.iso_alpha_2) AS isoAlpha2,
        il.city AS city,
        il.is_headquater AS headquarter
      FROM project_partner_locations ppl
      INNER JOIN project_partners pp ON pp.id = ppl.project_partner_id
      INNER JOIN institutions_locations il ON il.id = ppl.institution_loc_id
      LEFT JOIN loc_elements le ON le.id = il.loc_element_id
      WHERE pp.project_id = ?
        AND pp.id_phase = ?
        AND pp.is_active = 1
        AND ppl.is_active = 1
      ORDER BY le.name
      `,
      [projectId, phaseId],
    );
  }

  private async loadPartnerPersons(
    projectId: number,
    phaseId: number,
  ): Promise<ClusterPartnerPersonRow[]> {
    return this.dataSource.query<ClusterPartnerPersonRow[]>(
      `
      SELECT
        pp.id AS partnerId,
        ppp.id AS id,
        CONCAT(u.last_name, ', ', u.first_name) AS name,
        u.email AS email,
        ppp.contact_type AS role,
        CASE
          WHEN pd.acronym IS NOT NULL AND TRIM(pd.acronym) != '' THEN CONCAT(pd.acronym, ' - ', pd.name)
          ELSE pd.name
        END AS division
      FROM project_partner_persons ppp
      INNER JOIN project_partners pp ON pp.id = ppp.project_partner_id
      INNER JOIN users u ON u.id = ppp.user_id
      LEFT JOIN partner_divisions pd ON pd.id = ppp.partner_division_id
      WHERE pp.project_id = ?
        AND pp.id_phase = ?
        AND pp.is_active = 1
        AND ppp.is_active = 1
      ORDER BY name
      `,
      [projectId, phaseId],
    );
  }

  private async loadLocationGroups(
    projectId: number,
    phaseId: number,
  ): Promise<ClusterLocationGroupRow[]> {
    return this.dataSource.query<ClusterLocationGroupRow[]>(
      `
      SELECT
        let.id AS typeId,
        let.name AS typeName,
        le.name AS locName,
        parent_le.name AS parentName,
        lgp.latitude AS latitude,
        lgp.longitude AS longitude
      FROM project_locations pl
      LEFT JOIN loc_elements le ON le.id = pl.loc_element_id
      LEFT JOIN loc_element_types let ON let.id = COALESCE(pl.loc_element_type_id, le.element_type_id)
      LEFT JOIN loc_elements parent_le ON parent_le.id = le.parent_id
      LEFT JOIN loc_geopositions lgp ON lgp.id = le.geoposition_id
      WHERE pl.project_id = ?
        AND pl.id_phase = ?
        AND pl.is_active = 1
      ORDER BY let.name, le.name
      `,
      [projectId, phaseId],
    );
  }

  private async loadOutcomes(projectId: number, phaseId: number): Promise<ClusterOutcomeRow[]> {
    return this.dataSource.query<ClusterOutcomeRow[]>(
      `
      SELECT
        po.id AS outcomeId,
        po.order_index AS orderValue,
        po.narrative_target AS narrativeTarget,
        po.narrative_achieved AS narrativeAchieved,
        po.expected_value AS expectedValue,
        po.achieved_value AS achievedValue,
        po.expected_unit AS expectedUnitId,
        po.achieved_unit AS achievedUnitId,
        cpo.description AS programOutcomeDescription,
        cpo.indicator AS programOutcomeIndicator,
        cpo.year AS programOutcomeYear,
        cpo.value AS programOutcomeValue,
        stu.name AS programOutcomeUnit,
        stu_exp.name AS expectedUnitName,
        stu_ach.name AS achievedUnitName,
        CASE
          WHEN cp_flag.acronym IS NOT NULL AND TRIM(cp_flag.acronym) != ''
            THEN CONCAT(cp_flag.acronym, ': ', cp_flag.name)
          ELSE cp_flag.name
        END AS flagshipComposedName,
        cp_flag.acronym AS flagshipAcronym,
        pcl.lessons AS lessons
      FROM project_outcomes po
      LEFT JOIN crp_program_outcomes cpo ON cpo.id = po.outcome_id
      LEFT JOIN srf_target_units stu ON stu.id = cpo.target_unit_id
      LEFT JOIN srf_target_units stu_exp ON stu_exp.id = po.expected_unit
      LEFT JOIN srf_target_units stu_ach ON stu_ach.id = po.achieved_unit
      LEFT JOIN crp_programs cp_flag ON cp_flag.id = cpo.crp_program_id
      LEFT JOIN project_component_lessons pcl ON pcl.project_outcome_id = po.id AND pcl.is_active = 1
      WHERE po.project_id = ?
        AND po.id_phase = ?
        AND po.is_active = 1
      ORDER BY po.order_index, po.id
      `,
      [projectId, phaseId],
    );
  }

  private async loadOutcomeMilestones(
    projectId: number,
    phaseId: number,
    selectedYear: number,
  ): Promise<ClusterOutcomeMilestoneRow[]> {
    return this.dataSource.query<ClusterOutcomeMilestoneRow[]>(
      `
      SELECT
        pm.project_outcome_id AS outcomeId,
        CASE
          WHEN cm.extended_year IS NOT NULL AND cm.extended_year != -1
            THEN CONCAT(cm.year, ' extended to ', cm.extended_year, ' - ', cm.title)
          ELSE CONCAT(cm.year, ' - ', cm.title)
        END AS title,
        pm.year AS year,
        pm.narrative_target AS narrative,
        pm.expected_value AS expectedValue,
        pm.achieved_value AS achievedValue
      FROM project_milestones pm
      INNER JOIN project_outcomes po ON po.id = pm.project_outcome_id
      LEFT JOIN crp_milestones cm ON cm.id = pm.crp_milestone_id
      WHERE po.project_id = ?
        AND po.id_phase = ?
        AND pm.is_active = 1
        AND (pm.year <= 0 OR pm.year = ?)
      ORDER BY pm.year, pm.id
      `,
      [projectId, phaseId, selectedYear],
    );
  }

  private async loadOutcomeIndicators(
    projectId: number,
    phaseId: number,
  ): Promise<ClusterOutcomeIndicatorRow[]> {
    return this.dataSource.query<ClusterOutcomeIndicatorRow[]>(
      `
      SELECT
        poi.project_outcome_id AS outcomeId,
        cpoi.indicator AS question,
        poi.narrative AS narrative,
        poi.achieved_narrative AS achievedNarrative
      FROM project_outcome_indicators poi
      INNER JOIN project_outcomes po ON po.id = poi.project_outcome_id
      LEFT JOIN crp_program_outcome_indicator cpoi ON cpoi.id = poi.crp_outcome_indicator
      WHERE po.project_id = ?
        AND po.id_phase = ?
        AND poi.is_active = 1
      ORDER BY poi.id
      `,
      [projectId, phaseId],
    );
  }

  private async loadOutcomeCommunications(
    projectId: number,
    phaseId: number,
    selectedYear: number,
  ): Promise<ClusterOutcomeCommunicationRow[]> {
    return this.dataSource.query<ClusterOutcomeCommunicationRow[]>(
      `
      SELECT
        pc.project_outcome_id AS outcomeId,
        pc.communication AS communication,
        pc.year AS year
      FROM project_communications pc
      INNER JOIN project_outcomes po ON po.id = pc.project_outcome_id
      WHERE po.project_id = ?
        AND po.id_phase = ?
        AND pc.is_active = 1
        AND pc.year = ?
      ORDER BY pc.id
      `,
      [projectId, phaseId, selectedYear],
    );
  }

  private async loadOutcomeNextUsers(
    projectId: number,
    phaseId: number,
  ): Promise<ClusterOutcomeNextUserRow[]> {
    return this.dataSource.query<ClusterOutcomeNextUserRow[]>(
      `
      SELECT
        pn.project_outcome_id AS outcomeId,
        pn.next_user AS name,
        pn.knowledge AS knowledge,
        pn.strategies AS strategies,
        pn.knowledge_report AS knowledgeReport,
        pn.strategies_report AS strategiesReport
      FROM project_nextusers pn
      INNER JOIN project_outcomes po ON po.id = pn.project_outcome_id
      WHERE po.project_id = ?
        AND po.id_phase = ?
        AND pn.is_active = 1
      ORDER BY pn.id
      `,
      [projectId, phaseId],
    );
  }

  private async loadStudyIds(
    projectId: number,
    phaseId: number,
    phaseYear: number,
  ): Promise<number[]> {
    const rows = await this.dataSource.query<Array<{ studyId: number }>>(
      `
      SELECT DISTINCT studyId FROM (
        SELECT pes.id AS studyId
        FROM project_expected_studies pes
        INNER JOIN project_expected_study_info pesi
          ON pesi.project_expected_study_id = pes.id AND pesi.id_phase = ?
        WHERE pes.project_id = ?
          AND pes.is_active = 1
          AND pesi.year = ?
        UNION
        SELECT pes.id AS studyId
        FROM expected_study_projects esp
        INNER JOIN project_expected_studies pes ON pes.id = esp.expected_id
        INNER JOIN project_expected_study_info pesi
          ON pesi.project_expected_study_id = pes.id AND pesi.id_phase = ?
        WHERE esp.project_id = ?
          AND esp.id_phase = ?
          AND esp.is_active = 1
          AND pesi.year = ?
      ) studies
      ORDER BY studyId
      `,
      [phaseId, projectId, phaseYear, phaseId, projectId, phaseId, phaseYear],
    );
    return rows.map((row) => row.studyId);
  }

  private async loadInnovationSummaries(
    projectId: number,
    phaseId: number,
    phaseYear: number,
  ): Promise<ClusterInnovationSummaryRow[]> {
    return this.dataSource.query<ClusterInnovationSummaryRow[]>(
      `
      SELECT DISTINCT
        pi.id AS id,
        pii.title AS title,
        pii.short_title AS shortTitle,
        pii.year AS year,
        pii.number_of_innovations AS innovationNumber,
        pii.narrative AS narrative,
        riit.name AS innovationType,
        risi.name AS stageInnovation,
        ridi.name AS degreeInnovation,
        CASE
          WHEN i_lo.id IS NULL THEN NULL
          WHEN i_lo.acronym IS NOT NULL AND TRIM(i_lo.acronym) != ''
            THEN CONCAT(i_lo.acronym, ' - ', i_lo.name)
          ELSE i_lo.name
        END AS leadOrganization,
        sr.name AS readinessScale
      FROM (
        SELECT pin.id AS innovation_id
        FROM project_innovations pin
        INNER JOIN project_innovation_info pii0
          ON pii0.project_innovation_id = pin.id AND pii0.id_phase = ?
        WHERE pin.project_id = ? AND pin.is_active = 1 AND pii0.year = ?
        UNION
        SELECT pis.project_innovation_id AS innovation_id
        FROM project_innovation_shared pis
        INNER JOIN project_innovation_info pii1
          ON pii1.project_innovation_id = pis.project_innovation_id AND pii1.id_phase = ?
        WHERE pis.project_id = ? AND pis.id_phase = ? AND pis.is_active = 1 AND pii1.year = ?
      ) src
      INNER JOIN project_innovations pi ON pi.id = src.innovation_id
      INNER JOIN project_innovation_info pii ON pii.project_innovation_id = pi.id AND pii.id_phase = ?
      LEFT JOIN rep_ind_innovation_types riit ON riit.id = pii.innovation_type_id
      LEFT JOIN rep_ind_stage_innovations risi ON risi.id = pii.stage_innovation_id
      LEFT JOIN rep_ind_degree_innovation ridi ON ridi.id = pii.rep_ind_degree_innovation_id
      LEFT JOIN institutions i_lo ON i_lo.id = pii.lead_organization_id
      LEFT JOIN scaling_readiness sr ON sr.id = pii.readiness_scale
      ORDER BY pi.id
      `,
      [phaseId, projectId, phaseYear, phaseId, projectId, phaseId, phaseYear, phaseId],
    );
  }

  private async loadDeliverableSummaries(
    projectId: number,
    phaseId: number,
  ): Promise<ClusterDeliverableSummaryRow[]> {
    return this.dataSource.query<ClusterDeliverableSummaryRow[]>(
      `
      SELECT DISTINCT
        d.id AS id,
        di.title AS title,
        di.year AS year,
        di.new_expected_year AS newExpectedYear,
        di.status AS status,
        di.status_description AS statusDescription,
        di.description AS description,
        dt.name AS typeName
      FROM (
        SELECT d.id AS deliverable_id
        FROM deliverables d
        WHERE d.project_id = ? AND d.is_active = 1
        UNION
        SELECT pds.deliverable_id AS deliverable_id
        FROM project_deliverable_shared pds
        WHERE pds.project_id = ? AND pds.id_phase = ? AND pds.is_active = 1
      ) src
      INNER JOIN deliverables d ON d.id = src.deliverable_id
      INNER JOIN deliverables_info di ON di.deliverable_id = d.id AND di.id_phase = ? AND di.is_active = 1
      LEFT JOIN deliverable_types dt ON dt.id = di.type_id
      ORDER BY d.id
      `,
      [projectId, projectId, phaseId, phaseId],
    );
  }

  private async loadActivitySummaries(
    projectId: number,
    phaseId: number,
  ): Promise<ClusterActivitySummaryRow[]> {
    return this.dataSource.query<ClusterActivitySummaryRow[]>(
      `
      SELECT
        a.id AS id,
        a.title AS title,
        a.description AS description,
        DATE_FORMAT(a.startDate, '%b %Y') AS startDate,
        DATE_FORMAT(a.endDate, '%b %Y') AS endDate,
        a.activityStatus AS activityStatus,
        a.activityProgress AS activityProgress,
        CONCAT(u.last_name, ', ', u.first_name) AS leaderName,
        ${INSTITUTION_COMPOSED_NAME_SQL} AS institutionName,
        at.title AS activityTitle
      FROM activities a
      LEFT JOIN project_partner_persons ppp ON ppp.id = a.leader_id
      LEFT JOIN users u ON u.id = ppp.user_id
      LEFT JOIN project_partners pp ON pp.id = ppp.project_partner_id
      LEFT JOIN institutions i ON i.id = pp.institution_id
      LEFT JOIN activities_titles at ON at.id = a.title_id
      WHERE a.project_id = ?
        AND a.id_phase = ?
        AND a.is_active = 1
      ORDER BY a.id
      `,
      [projectId, phaseId],
    );
  }

  private async loadActivityDeliverables(
    projectId: number,
    phaseId: number,
  ): Promise<ClusterActivityDeliverableRow[]> {
    return this.dataSource.query<ClusterActivityDeliverableRow[]>(
      `
      SELECT
        da.activity_id AS activityId,
        d.id AS deliverableId,
        di.title AS title
      FROM deliverable_activities da
      INNER JOIN activities a ON a.id = da.activity_id
      INNER JOIN deliverables d ON d.id = da.deliverable_id AND d.is_active = 1
      INNER JOIN deliverables_info di
        ON di.deliverable_id = d.id AND di.id_phase = ? AND di.is_active = 1
      WHERE a.project_id = ?
        AND a.id_phase = ?
        AND da.id_phase = ?
        AND da.is_active = 1
      ORDER BY da.activity_id, d.id
      `,
      [phaseId, projectId, phaseId, phaseId],
    );
  }
}
