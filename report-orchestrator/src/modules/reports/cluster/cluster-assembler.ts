import { formatInnovationTimeCreation } from '../../../shared/report-json.utils';
import { InnovationContext } from '../innovation/innovation.types';
import { OicrStudyContext } from '../oicr/oicr.types';
import {
  CLUSTER_PARTNER_ROLE_LABELS,
  FLAG_ASSET_BASE_URL,
  LIAISON_CONTACT_LABEL,
  LIAISON_INSTITUTION_LABEL,
  PROJECT_STATUS_LABELS,
} from './cluster.constants';
import { mapDeliverableForCluster } from './cluster-deliverable.mapper';
import { mapInnovationContextForClusterEmbed } from './cluster-innovation.mapper';
import { mapOicrContextForClusterEmbed } from './cluster-oicr.mapper';
import {
  buildClusterActivitiesSummary,
  buildLocationsSummary,
  buildProgramSummary,
  formatCoordinate,
  formatNumericValue,
  mysqlBool,
  sanitizeText,
} from './cluster-text.utils';
import {
  ClusterActivitySummaryRow,
  ClusterContext,
  ClusterDeliverableExtendedContext,
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
  ClusterReportData,
} from './cluster.types';

export function assembleClusterData(
  context: ClusterContext,
  oicrContexts: OicrStudyContext[],
  innovationContexts: Map<number, InnovationContext>,
  deliverableContexts: Map<number, ClusterDeliverableExtendedContext>,
): ClusterReportData {
  const { core } = context;
  const partners = buildPartners(context);
  const projectDescription = buildProjectDescription(context, partners);

  return {
    projectID: String(core.projectId),
    projectTitle: core.projectTitle,
    projectAcronym: core.projectAcronym ?? '',
    phaseID: String(core.phaseId),
    cycle: core.cycle,
    year: String(core.phaseYear),
    loggedCenter: core.loggedCenter,
    timeCreation: formatInnovationTimeCreation(),
    projectDescription,
    projectPartners: partners,
    projectLocations: buildProjectLocations(context),
    performanceIndicatorContributions: buildPerformanceContributions(context),
    oicrs: oicrContexts.map(mapOicrContextForClusterEmbed),
    deliverables: context.deliverables.map((row) =>
      mapDeliverableForCluster(row, deliverableContexts.get(row.id)),
    ),
    activities: context.activities.map(mapActivity),
    innovations: context.innovations.map((row) => mapInnovation(row, innovationContexts.get(row.id))),
    crossCutting: buildCrossCuttingSummary(context),
  };
}

function buildProjectDescription(
  context: ClusterContext,
  partners: Array<Record<string, unknown>>,
): Record<string, unknown> {
  const { core } = context;
  const flagships = context.flagships.map(mapProgram);
  const regions = context.regions.map(mapProgram);
  const clusterActivities = context.clusterActivities.map((activity) => ({
    id: activity.id,
    name: activity.name,
    identifier: activity.identifier,
  }));

  return {
    title: core.projectTitle,
    startDate: core.startDate,
    endDate: core.endDate,
    liaisonInstitution: core.liaisonInstitution,
    liaisonLabel: LIAISON_INSTITUTION_LABEL,
    liaisonContactLabel: LIAISON_CONTACT_LABEL,
    type: core.clusterType,
    status: core.status != null ? PROJECT_STATUS_LABELS[core.status] ?? String(core.status) : null,
    leadOrganization: core.leadOrganization,
    leader: core.leaderName,
    summary: sanitizeText(core.summary),
    cycle: core.cycle,
    crossCutting: buildCrossCuttingSummary(context),
    hasRegions: context.hasRegions,
    flagships,
    flagshipsSummary: buildProgramSummary(flagships),
    regions,
    regionsSummary: buildProgramSummary(regions),
    clusterActivities,
    clusterActivitiesSummary: buildClusterActivitiesSummary(clusterActivities),
    challengesSolutions: sanitizeText(core.challengesSolutions),
    lessonsLearned: sanitizeText(core.lessonsLearned),
    partners,
  };
}

function mapProgram(program: { id: number; name: string | null; acronym: string | null; composedName: string | null }) {
  return {
    id: program.id,
    name: program.name,
    acronym: program.acronym,
    composedName: program.composedName,
  };
}

function buildCrossCuttingSummary(context: ClusterContext): string | null {
  const { core } = context;
  const items: string[] = [];
  if (mysqlBool(core.crossCuttingNa)) {
    items.push('● N/A');
  }
  if (mysqlBool(core.crossCuttingCapacity)) {
    items.push('● Capacity Development');
  }
  if (mysqlBool(core.crossCuttingGender)) {
    items.push('● Gender');
  }
  if (mysqlBool(core.crossCuttingYouth)) {
    items.push('● Youth');
  }
  if (items.length === 0) {
    return null;
  }
  return items.join(' <br>');
}

function buildPartners(context: ClusterContext): Array<Record<string, unknown>> {
  const locationsByPartner = groupBy(context.partnerLocations, (row) => row.partnerId);
  const personsByPartner = groupBy(context.partnerPersons, (row) => row.partnerId);

  return context.partners.map((partner) => {
    const locations = (locationsByPartner.get(partner.id) ?? []).map(mapPartnerLocation);
    return {
      id: partner.id,
      institutionName: partner.institutionName,
      institutionAcronym: partner.institutionAcronym,
      responsibilities: sanitizeText(partner.responsibilities),
      locations,
      locationsSummary: buildLocationsSummary(locations),
      persons: (personsByPartner.get(partner.id) ?? []).map(mapPartnerPerson),
    };
  });
}

function mapPartnerLocation(row: ClusterPartnerLocationRow): Record<string, unknown> {
  const location: Record<string, unknown> = {
    name: row.name,
    country: row.country,
    city: row.city,
    headquarter: row.headquarter === 1,
  };
  if (row.isoAlpha2) {
    location.isoAlpha2 = row.isoAlpha2;
    location.flagUrl = `${FLAG_ASSET_BASE_URL}/${row.isoAlpha2}.svg`;
  }
  return location;
}

function mapPartnerPerson(row: ClusterPartnerPersonRow): Record<string, unknown> {
  let role = sanitizeText(row.role);
  if (role != null) {
    const normalized = role.toUpperCase();
    role = CLUSTER_PARTNER_ROLE_LABELS[normalized] ?? role;
  }
  const person: Record<string, unknown> = {
    id: row.id,
    name: row.name,
    email: row.email,
    role,
  };
  if (row.division) {
    person.division = row.division;
  }
  return person;
}

function buildProjectLocations(context: ClusterContext): Record<string, unknown> {
  const groups = buildLocationGroups(context.locationGroups);
  return {
    hasLocations: groups.length > 0,
    globalDimension: mysqlBool(context.core.locationGlobal),
    regionalDimension: mysqlBool(context.core.locationRegional),
    locationGroups: groups,
  };
}

function buildLocationGroups(rows: ClusterLocationGroupRow[]): Array<Record<string, unknown>> {
  const grouped = new Map<number, { typeId: number; typeName: string; locations: Array<Record<string, unknown>> }>();

  for (const row of rows) {
    let group = grouped.get(row.typeId);
    if (!group) {
      group = { typeId: row.typeId, typeName: row.typeName, locations: [] };
      grouped.set(row.typeId, group);
    }
    const entry: Record<string, unknown> = {};
    if (row.locName) {
      entry.name = row.locName;
      entry.parentName = row.parentName;
      const latitude = formatCoordinate(row.latitude);
      const longitude = formatCoordinate(row.longitude);
      if (latitude) {
        entry.latitude = latitude;
      }
      if (longitude) {
        entry.longitude = longitude;
      }
    } else {
      entry.name = row.typeName;
    }
    group.locations.push(entry);
  }

  return [...grouped.values()].map((group) => ({
    ...group,
    locations: group.locations.sort((a, b) =>
      String(a.name ?? '').localeCompare(String(b.name ?? '')),
    ),
  }));
}

function buildPerformanceContributions(context: ClusterContext): Array<Record<string, unknown>> {
  const milestonesByOutcome = groupBy(context.outcomeMilestones, (row) => row.outcomeId);
  const indicatorsByOutcome = groupBy(context.outcomeIndicators, (row) => row.outcomeId);
  const communicationsByOutcome = groupBy(context.outcomeCommunications, (row) => row.outcomeId);
  const nextUsersByOutcome = groupBy(context.outcomeNextUsers, (row) => row.outcomeId);

  return context.outcomes.map((outcome, index) =>
    buildPerformanceContribution(
      outcome,
      milestonesByOutcome.get(outcome.outcomeId) ?? [],
      indicatorsByOutcome.get(outcome.outcomeId) ?? [],
      communicationsByOutcome.get(outcome.outcomeId) ?? [],
      nextUsersByOutcome.get(outcome.outcomeId) ?? [],
      index + 1,
    ),
  );
}

function buildPerformanceContribution(
  outcome: ClusterOutcomeRow,
  milestones: ClusterOutcomeMilestoneRow[],
  indicators: ClusterOutcomeIndicatorRow[],
  communications: ClusterOutcomeCommunicationRow[],
  nextUsers: ClusterOutcomeNextUserRow[],
  order: number,
): Record<string, unknown> {
  const data: Record<string, unknown> = {
    order,
    outcomeId: outcome.outcomeId,
  };

  putIfPresent(data, 'programOutcome', sanitizeText(outcome.programOutcomeDescription));
  putIfPresent(data, 'indicator', sanitizeText(outcome.programOutcomeIndicator));
  putIfPresent(data, 'programOutcomeYear', outcome.programOutcomeYear);
  putIfPresent(data, 'programOutcomeValue', formatNumericValue(outcome.programOutcomeValue));
  putIfPresent(data, 'programOutcomeUnit', sanitizeText(outcome.programOutcomeUnit));

  if (outcome.flagshipComposedName) {
    data.flagship = sanitizeText(outcome.flagshipComposedName);
    const badge = outcome.flagshipAcronym ?? outcome.flagshipComposedName;
    data.flagshipBadge = sanitizeText(badge);
  }

  putIfPresent(data, 'targetNarrative', sanitizeText(outcome.narrativeTarget));
  putIfPresent(data, 'achievementNarrative', sanitizeText(outcome.narrativeAchieved));
  putIfPresent(data, 'targetValue', formatNumericValue(outcome.expectedValue));
  putIfPresent(
    data,
    'targetUnit',
    sanitizeText(outcome.expectedUnitName ?? outcome.programOutcomeUnit),
  );
  putIfPresent(data, 'achievementValue', formatNumericValue(outcome.achievedValue));
  putIfPresent(
    data,
    'achievementUnit',
    sanitizeText(outcome.achievedUnitName ?? outcome.programOutcomeUnit),
  );

  const communicationsText = communications
    .map((row) => sanitizeText(row.communication))
    .filter(Boolean)
    .join('\n\n');
  if (communicationsText) {
    data.communications = communicationsText;
  }

  putIfPresent(data, 'lessonsLearned', sanitizeText(outcome.lessons));

  const milestoneData = milestones
    .map((milestone) => {
      const entry: Record<string, unknown> = {};
      putIfPresent(entry, 'title', sanitizeText(milestone.title));
      if (milestone.year != null && milestone.year > 0) {
        entry.year = milestone.year;
      }
      putIfPresent(entry, 'narrative', sanitizeText(milestone.narrative));
      putIfPresent(entry, 'expectedValue', formatNumericValue(milestone.expectedValue));
      putIfPresent(entry, 'achievedValue', formatNumericValue(milestone.achievedValue));
      return Object.keys(entry).length > 0 ? entry : null;
    })
    .filter((entry): entry is Record<string, unknown> => entry != null);

  data.milestones = milestoneData;
  data.hasMilestones = milestoneData.length > 0;

  const indicatorResponses = indicators
    .map((indicator) => {
      const entry: Record<string, unknown> = {};
      putIfPresent(entry, 'question', sanitizeText(indicator.question));
      putIfPresent(entry, 'narrative', sanitizeText(indicator.narrative));
      putIfPresent(entry, 'achievedNarrative', sanitizeText(indicator.achievedNarrative));
      return Object.keys(entry).length > 0 ? entry : null;
    })
    .filter((entry): entry is Record<string, unknown> => entry != null);

  data.indicatorResponses = indicatorResponses;
  data.hasIndicatorResponses = indicatorResponses.length > 0;

  const nextUserData = nextUsers
    .map((nextUser) => {
      const entry: Record<string, unknown> = {};
      putIfPresent(entry, 'name', sanitizeText(nextUser.name));
      putIfPresent(entry, 'knowledge', sanitizeText(nextUser.knowledge));
      putIfPresent(entry, 'strategies', sanitizeText(nextUser.strategies));
      putIfPresent(entry, 'knowledgeReport', sanitizeText(nextUser.knowledgeReport));
      putIfPresent(entry, 'strategiesReport', sanitizeText(nextUser.strategiesReport));
      return Object.values(entry).some((value) => value != null) ? entry : null;
    })
    .filter((entry): entry is Record<string, unknown> => entry != null);

  data.nextUsers = nextUserData;
  data.hasNextUsers = nextUserData.length > 0;

  return data;
}

function mapActivity(row: ClusterActivitySummaryRow): Record<string, unknown> {
  const data: Record<string, unknown> = { id: row.id };
  putIfPresent(data, 'title', sanitizeText(row.title));
  putIfPresent(data, 'description', sanitizeText(row.description));
  putIfPresent(data, 'startDate', row.startDate);
  putIfPresent(data, 'endDate', row.endDate);
  if (row.activityStatus != null) {
    data.status = PROJECT_STATUS_LABELS[row.activityStatus] ?? String(row.activityStatus);
  }
  putIfPresent(data, 'activityProgress', sanitizeText(row.activityProgress));
  putIfPresent(data, 'leader', sanitizeText(row.leaderName));
  putIfPresent(data, 'institution', sanitizeText(row.institutionName));
  putIfPresent(data, 'activityTitle', sanitizeText(row.activityTitle));
  return data;
}

function mapInnovation(
  row: ClusterInnovationSummaryRow,
  innovationContext: InnovationContext | undefined,
): Record<string, unknown> {
  if (innovationContext) {
    return mapInnovationContextForClusterEmbed(innovationContext);
  }

  const data: Record<string, unknown> = { id: row.id };
  putIfPresent(data, 'title', sanitizeText(row.title));
  putIfPresent(data, 'shortTitle', sanitizeText(row.shortTitle));
  putIfPresent(data, 'year', row.year);
  putIfPresent(data, 'innovationNumber', row.innovationNumber);
  putIfPresent(data, 'narrative', sanitizeText(row.narrative));
  putIfPresent(data, 'innovationType', sanitizeText(row.innovationType));
  putIfPresent(data, 'stageInnovation', sanitizeText(row.stageInnovation));
  putIfPresent(data, 'degreeInnovation', sanitizeText(row.degreeInnovation));
  putIfPresent(data, 'leadOrganization', sanitizeText(row.leadOrganization));
  putIfPresent(data, 'readinessScale', sanitizeText(row.readinessScale));
  return data;
}

function groupBy<T, K>(items: T[], keyFn: (item: T) => K): Map<K, T[]> {
  const map = new Map<K, T[]>();
  for (const item of items) {
    const key = keyFn(item);
    const group = map.get(key);
    if (group) {
      group.push(item);
    } else {
      map.set(key, [item]);
    }
  }
  return map;
}

function putIfPresent(target: Record<string, unknown>, key: string, value: unknown): void {
  if (value == null) {
    return;
  }
  if (typeof value === 'string' && value.trim() === '') {
    return;
  }
  target[key] = value;
}
