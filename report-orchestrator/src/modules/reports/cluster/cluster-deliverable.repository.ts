import { DataSource } from 'typeorm';

import { INSTITUTION_COMPOSED_NAME_SQL } from '../innovation/innovation.constants';
import { LOC_ELEMENT_TYPE_COUNTRY, LOC_ELEMENT_TYPE_REGION } from './cluster.constants';
import {
  ClusterDeliverableCountryRow,
  ClusterDeliverableExtendedContext,
  ClusterDeliverableExtendedCoreRow,
  ClusterDeliverableFundingLocationRow,
  ClusterDeliverableFundingSourceRow,
} from './cluster.types';

const DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE = 1;

function buildInClause(count: number): string {
  return Array.from({ length: count }, () => '?').join(', ');
}

function groupByDeliverableId<T extends { deliverableId: number }>(rows: T[]): Map<number, T[]> {
  const map = new Map<number, T[]>();
  for (const row of rows) {
    const group = map.get(row.deliverableId);
    if (group) {
      group.push(row);
    } else {
      map.set(row.deliverableId, [row]);
    }
  }
  return map;
}

function groupByFundingSourceId<T extends { fundingSourceId: number }>(rows: T[]): Map<number, T[]> {
  const map = new Map<number, T[]>();
  for (const row of rows) {
    const group = map.get(row.fundingSourceId);
    if (group) {
      group.push(row);
    } else {
      map.set(row.fundingSourceId, [row]);
    }
  }
  return map;
}

/** Batch loaders for deliverable fields used by ReportingSummaryAction.buildDeliverableData(). */
export class ClusterDeliverableRepository {
  constructor(private readonly dataSource: DataSource) {}

  async loadExtendedByDeliverableIds(
    deliverableIds: number[],
    phaseId: number,
  ): Promise<Map<number, ClusterDeliverableExtendedContext>> {
    if (deliverableIds.length === 0) {
      return new Map();
    }

    const inClause = buildInClause(deliverableIds.length);
    const ids = [...deliverableIds];

    const [
      coreRows,
      countryRows,
      regionRows,
      crpRows,
      crpOutcomeRows,
      projectOutcomeRows,
      activityRows,
      fundingRows,
      contactRows,
    ] = await Promise.all([
      this.loadCore(ids, phaseId, inClause),
      this.loadCountries(ids, phaseId, inClause),
      this.loadRegions(ids, phaseId, inClause),
      this.loadCrps(ids, phaseId, inClause),
      this.loadCrpOutcomes(ids, phaseId, inClause),
      this.loadProjectOutcomes(ids, phaseId, inClause),
      this.loadActivities(ids, phaseId, inClause),
      this.loadFundingSources(ids, phaseId, inClause),
      this.loadContacts(ids, phaseId, inClause),
    ]);

    const fundingSourceIds = [...new Set(fundingRows.map((row) => row.fundingSourceId))];
    const fundingLocations = fundingSourceIds.length > 0
      ? await this.loadFundingLocations(fundingSourceIds, phaseId, buildInClause(fundingSourceIds.length))
      : [];

    const countriesById = groupByDeliverableId(countryRows);
    const regionsById = groupByDeliverableId(regionRows);
    const crpsById = groupByDeliverableId(crpRows);
    const crpOutcomesById = groupByDeliverableId(crpOutcomeRows);
    const projectOutcomesById = groupByDeliverableId(projectOutcomeRows);
    const activitiesById = groupByDeliverableId(activityRows);
    const fundingById = groupByDeliverableId(fundingRows);
    const contactsById = groupByDeliverableId(contactRows);
    const locationsByFundingId = groupByFundingSourceId(fundingLocations);

    const result = new Map<number, ClusterDeliverableExtendedContext>();
    for (const core of coreRows) {
      result.set(core.deliverableId, {
        core,
        countries: countriesById.get(core.deliverableId) ?? [],
        regionNames: (regionsById.get(core.deliverableId) ?? []).map((row) => row.name),
        crps: (crpsById.get(core.deliverableId) ?? []).map((row) => row.name),
        crpOutcomes: (crpOutcomesById.get(core.deliverableId) ?? []).map((row) => row.name),
        projectOutcomes: (projectOutcomesById.get(core.deliverableId) ?? []).map((row) => row.name),
        activities: (activitiesById.get(core.deliverableId) ?? []).map((row) => row.name),
        fundingSources: fundingById.get(core.deliverableId) ?? [],
        fundingLocations: (fundingById.get(core.deliverableId) ?? []).flatMap(
          (row) => locationsByFundingId.get(row.fundingSourceId) ?? [],
        ),
        contacts: (contactsById.get(core.deliverableId) ?? []).map((row) => row.name),
      });
    }

    return result;
  }

  private async loadCore(
    deliverableIds: number[],
    phaseId: number,
    inClause: string,
  ): Promise<ClusterDeliverableExtendedCoreRow[]> {
    return this.dataSource.query<ClusterDeliverableExtendedCoreRow[]>(
      `
      SELECT
        di.deliverable_id AS deliverableId,
        di.type_other AS typeOther,
        rigs.name AS geographicScope,
        di.is_location_global AS isLocationGlobal,
        rir.name AS regionName,
        CASE
          WHEN gu_po.acronym IS NOT NULL AND cpo.description IS NOT NULL
            THEN CONCAT(gu_po.acronym, ' Outcome: ', cpo.description)
          ELSE NULL
        END AS crpProgramOutcome,
        CASE
          WHEN cca.identifier IS NOT NULL AND ccko.key_output IS NOT NULL AND TRIM(ccko.key_output) != ''
            THEN CONCAT(cca.identifier, ' - ', ccko.key_output)
          WHEN ccko.key_output IS NOT NULL AND TRIM(ccko.key_output) != ''
            THEN ccko.key_output
          ELSE NULL
        END AS crpClusterKeyOutput
      FROM deliverables_info di
      LEFT JOIN rep_ind_geographic_scopes rigs ON rigs.id = di.geographic_scope_id
      LEFT JOIN rep_ind_regions rir ON rir.id = di.region_id
      LEFT JOIN crp_program_outcomes cpo ON cpo.id = di.outcome_id
      LEFT JOIN crp_programs cp_po ON cp_po.id = cpo.crp_program_id
      LEFT JOIN global_units gu_po ON gu_po.id = cp_po.global_unit_id
      LEFT JOIN crp_cluster_key_outputs ccko ON ccko.id = di.key_output_id
      LEFT JOIN crp_cluster_of_activities cca ON cca.id = ccko.cluster_activity_id
      WHERE di.deliverable_id IN (${inClause})
        AND di.id_phase = ?
        AND di.is_active = 1
      `,
      [...deliverableIds, phaseId],
    );
  }

  private async loadCountries(
    deliverableIds: number[],
    phaseId: number,
    inClause: string,
  ): Promise<ClusterDeliverableCountryRow[]> {
    return this.dataSource.query<ClusterDeliverableCountryRow[]>(
      `
      SELECT
        dl.deliverable_id AS deliverableId,
        le.name AS name,
        LOWER(le.iso_alpha_2) AS isoAlpha2
      FROM deliverable_locations dl
      INNER JOIN loc_elements le ON le.id = dl.loc_element_id
      WHERE dl.deliverable_id IN (${inClause})
        AND dl.id_phase = ?
        AND le.element_type_id = ?
      ORDER BY le.name
      `,
      [...deliverableIds, phaseId, LOC_ELEMENT_TYPE_COUNTRY],
    );
  }

  private async loadRegions(
    deliverableIds: number[],
    phaseId: number,
    inClause: string,
  ): Promise<Array<{ deliverableId: number; name: string }>> {
    return this.dataSource.query<Array<{ deliverableId: number; name: string }>>(
      `
      SELECT
        dgr.deliverable_id AS deliverableId,
        le.name AS name
      FROM deliverable_geographic_regions dgr
      INNER JOIN loc_elements le ON le.id = dgr.loc_element_id
      WHERE dgr.deliverable_id IN (${inClause})
        AND dgr.id_phase = ?
        AND le.element_type_id = ?
      ORDER BY le.name
      `,
      [...deliverableIds, phaseId, LOC_ELEMENT_TYPE_REGION],
    );
  }

  private async loadCrps(
    deliverableIds: number[],
    phaseId: number,
    inClause: string,
  ): Promise<Array<{ deliverableId: number; name: string }>> {
    return this.dataSource.query<Array<{ deliverableId: number; name: string }>>(
      `
      SELECT
        dc.deliverable_id AS deliverableId,
        CASE
          WHEN gu.acronym IS NOT NULL AND TRIM(gu.acronym) != ''
            THEN CONCAT(gu.acronym, ': ', gu.name)
          ELSE gu.name
        END AS name
      FROM deliverable_crps dc
      INNER JOIN global_units gu ON gu.id = dc.global_unit
      WHERE dc.deliverable_id IN (${inClause})
        AND dc.id_phase = ?
      ORDER BY gu.name
      `,
      [...deliverableIds, phaseId],
    );
  }

  private async loadCrpOutcomes(
    deliverableIds: number[],
    phaseId: number,
    inClause: string,
  ): Promise<Array<{ deliverableId: number; name: string }>> {
    return this.dataSource.query<Array<{ deliverableId: number; name: string }>>(
      `
      SELECT
        dco.deliverable_id AS deliverableId,
        CASE
          WHEN gu.acronym IS NOT NULL AND cpo.description IS NOT NULL
            THEN CONCAT(gu.acronym, ' Outcome: ', cpo.description)
          ELSE '-'
        END AS name
      FROM deliverable_crp_outcomes dco
      INNER JOIN crp_program_outcomes cpo ON cpo.id = dco.crp_outcome_id
      INNER JOIN crp_programs cp ON cp.id = cpo.crp_program_id
      INNER JOIN global_units gu ON gu.id = cp.global_unit_id
      WHERE dco.deliverable_id IN (${inClause})
        AND dco.id_phase = ?
      ORDER BY name
      `,
      [...deliverableIds, phaseId],
    );
  }

  private async loadProjectOutcomes(
    deliverableIds: number[],
    phaseId: number,
    inClause: string,
  ): Promise<Array<{ deliverableId: number; name: string }>> {
    return this.dataSource.query<Array<{ deliverableId: number; name: string }>>(
      `
      SELECT
        dpo.deliverable_id AS deliverableId,
        CASE
          WHEN gu.acronym IS NOT NULL AND cpo.description IS NOT NULL
            THEN CONCAT(gu.acronym, ' Outcome: ', cpo.description)
          ELSE CONCAT('Outcome ', po.id)
        END AS name
      FROM deliverable_project_outcomes dpo
      INNER JOIN project_outcomes po ON po.id = dpo.project_outcome_id
      INNER JOIN crp_program_outcomes cpo ON cpo.id = po.outcome_id
      INNER JOIN crp_programs cp ON cp.id = cpo.crp_program_id
      INNER JOIN global_units gu ON gu.id = cp.global_unit_id
      WHERE dpo.deliverable_id IN (${inClause})
        AND dpo.id_phase = ?
        AND po.is_active = 1
      ORDER BY name
      `,
      [...deliverableIds, phaseId],
    );
  }

  private async loadActivities(
    deliverableIds: number[],
    phaseId: number,
    inClause: string,
  ): Promise<Array<{ deliverableId: number; name: string }>> {
    return this.dataSource.query<Array<{ deliverableId: number; name: string }>>(
      `
      SELECT
        da.deliverable_id AS deliverableId,
        a.title AS name
      FROM deliverable_activities da
      INNER JOIN activities a ON a.id = da.activity_id
      WHERE da.deliverable_id IN (${inClause})
        AND da.id_phase = ?
        AND da.is_active = 1
        AND a.title IS NOT NULL
        AND TRIM(a.title) != ''
      ORDER BY a.title
      `,
      [...deliverableIds, phaseId],
    );
  }

  private async loadFundingSources(
    deliverableIds: number[],
    phaseId: number,
    inClause: string,
  ): Promise<ClusterDeliverableFundingSourceRow[]> {
    return this.dataSource.query<ClusterDeliverableFundingSourceRow[]>(
      `
      SELECT
        dfs.deliverable_id AS deliverableId,
        fs.id AS fundingSourceId,
        fsi.title AS title,
        fsi.finance_code AS financeCode,
        fsi.description AS description,
        CONCAT('FS', fs.id) AS composedName,
        fsi.global AS isGlobal
      FROM deliverable_funding_sources dfs
      INNER JOIN funding_sources fs ON fs.id = dfs.funding_source_id
      INNER JOIN funding_sources_info fsi
        ON fsi.funding_source_id = fs.id AND fsi.id_phase = ? AND fsi.is_active = 1
      WHERE dfs.deliverable_id IN (${inClause})
        AND dfs.id_phase = ?
        AND dfs.is_active = 1
      ORDER BY fs.id
      `,
      [phaseId, ...deliverableIds, phaseId],
    );
  }

  private async loadFundingLocations(
    fundingSourceIds: number[],
    phaseId: number,
    inClause: string,
  ): Promise<ClusterDeliverableFundingLocationRow[]> {
    return this.dataSource.query<ClusterDeliverableFundingLocationRow[]>(
      `
      SELECT
        fsl.funding_source_id AS fundingSourceId,
        COALESCE(le.name, let.name) AS name
      FROM funding_source_locations fsl
      LEFT JOIN loc_elements le ON le.id = fsl.loc_element_id
      LEFT JOIN loc_element_types let ON let.id = fsl.loc_element_type_id
      WHERE fsl.funding_source_id IN (${inClause})
        AND fsl.id_phase = ?
        AND fsl.is_active = 1
        AND COALESCE(le.name, let.name) IS NOT NULL
      ORDER BY name
      `,
      [...fundingSourceIds, phaseId],
    );
  }

  private async loadContacts(
    deliverableIds: number[],
    phaseId: number,
    inClause: string,
  ): Promise<Array<{ deliverableId: number; name: string }>> {
    return this.dataSource.query<Array<{ deliverableId: number; name: string }>>(
      `
      SELECT DISTINCT
        contacts.deliverable_id AS deliverableId,
        CASE
          WHEN contacts.inst_name IS NOT NULL AND contacts.user_name IS NOT NULL
            THEN CONCAT(contacts.inst_name, ' - ', contacts.user_name)
          ELSE COALESCE(contacts.inst_name, contacts.user_name)
        END AS name
      FROM (
        SELECT
          dup.deliverable_id,
          ${INSTITUTION_COMPOSED_NAME_SQL.replace(/\bi\./g, 'inst.')} AS inst_name,
          CONCAT(u.last_name, ', ', u.first_name) AS user_name
        FROM deliverable_user_partnerships dup
        INNER JOIN deliverable_user_partnership_persons dupp
          ON dupp.user_partnership_id = dup.id AND dupp.is_active = 1
        INNER JOIN users u ON u.id = dupp.user_id
        LEFT JOIN institutions inst ON inst.id = dup.institution_id
        WHERE dup.deliverable_id IN (${inClause})
          AND dup.id_phase = ?
          AND dup.is_active = 1
          AND dup.deliverable_partner_type_id = ?
          AND u.email IS NOT NULL
          AND TRIM(u.email) != ''
      ) contacts
      ORDER BY name
      `,
      [...deliverableIds, phaseId, DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE],
    );
  }
}
