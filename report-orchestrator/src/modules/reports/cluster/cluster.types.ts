export interface ClusterCoreRow {
  projectId: number;
  projectAcronym: string | null;
  projectTitle: string | null;
  phaseId: number;
  phaseYear: number;
  cycle: string | null;
  loggedCenter: string | null;
  summary: string | null;
  challengesSolutions: string | null;
  lessonsLearned: string | null;
  startDate: string | null;
  endDate: string | null;
  liaisonInstitution: string | null;
  clusterType: string | null;
  status: number | null;
  noRegional: number | null;
  crossCuttingNa: number | null;
  crossCuttingCapacity: number | null;
  crossCuttingGender: number | null;
  crossCuttingYouth: number | null;
  locationGlobal: number | null;
  locationRegional: number | null;
  leadOrganization: string | null;
  leaderName: string | null;
}

export interface ClusterProgramRow {
  id: number;
  name: string | null;
  acronym: string | null;
  composedName: string | null;
}

export interface ClusterActivityMetaRow {
  id: number;
  name: string | null;
  identifier: string | null;
}

export interface ClusterPartnerRow {
  id: number;
  institutionName: string;
  institutionAcronym: string | null;
  responsibilities: string | null;
}

export interface ClusterPartnerLocationRow {
  partnerId: number;
  name: string | null;
  country: string | null;
  isoAlpha2: string | null;
  city: string | null;
  headquarter: number | null;
}

export interface ClusterPartnerPersonRow {
  partnerId: number;
  id: number;
  name: string | null;
  email: string | null;
  role: string | null;
  division: string | null;
}

export interface ClusterLocationGroupRow {
  typeId: number;
  typeName: string;
  locName: string | null;
  parentName: string | null;
  latitude: number | null;
  longitude: number | null;
}

export interface ClusterOutcomeRow {
  outcomeId: number;
  orderValue: number | null;
  narrativeTarget: string | null;
  narrativeAchieved: string | null;
  expectedValue: number | null;
  achievedValue: number | null;
  expectedUnitId: number | null;
  achievedUnitId: number | null;
  programOutcomeDescription: string | null;
  programOutcomeIndicator: string | null;
  programOutcomeYear: number | null;
  programOutcomeValue: number | null;
  programOutcomeUnit: string | null;
  expectedUnitName: string | null;
  achievedUnitName: string | null;
  flagshipComposedName: string | null;
  flagshipAcronym: string | null;
  lessons: string | null;
}

export interface ClusterOutcomeMilestoneRow {
  outcomeId: number;
  title: string | null;
  year: number | null;
  narrative: string | null;
  expectedValue: number | null;
  achievedValue: number | null;
}

export interface ClusterOutcomeIndicatorRow {
  outcomeId: number;
  question: string | null;
  narrative: string | null;
  achievedNarrative: string | null;
}

export interface ClusterOutcomeCommunicationRow {
  outcomeId: number;
  communication: string | null;
  year: number | null;
}

export interface ClusterInnovationSummaryRow {
  id: number;
  title: string | null;
  shortTitle: string | null;
  year: number | null;
  innovationNumber: number | null;
  narrative: string | null;
  innovationType: string | null;
  stageInnovation: string | null;
  degreeInnovation: string | null;
  leadOrganization: string | null;
  readinessScale: string | null;
}

export interface ClusterDeliverableSummaryRow {
  id: number;
  title: string | null;
  year: number;
  newExpectedYear: number | null;
  status: number | null;
  statusDescription: string | null;
  description: string | null;
  typeName: string | null;
}

export interface ClusterActivitySummaryRow {
  id: number;
  title: string | null;
  description: string | null;
  startDate: string | null;
  endDate: string | null;
  activityStatus: number | null;
  activityProgress: string | null;
  leaderName: string | null;
  institutionName: string | null;
  activityTitle: string | null;
}

export interface ClusterContext {
  core: ClusterCoreRow;
  flagships: ClusterProgramRow[];
  regions: ClusterProgramRow[];
  clusterActivities: ClusterActivityMetaRow[];
  partners: ClusterPartnerRow[];
  partnerLocations: ClusterPartnerLocationRow[];
  partnerPersons: ClusterPartnerPersonRow[];
  locationGroups: ClusterLocationGroupRow[];
  outcomes: ClusterOutcomeRow[];
  outcomeMilestones: ClusterOutcomeMilestoneRow[];
  outcomeIndicators: ClusterOutcomeIndicatorRow[];
  outcomeCommunications: ClusterOutcomeCommunicationRow[];
  studyIds: number[];
  innovations: ClusterInnovationSummaryRow[];
  deliverables: ClusterDeliverableSummaryRow[];
  activities: ClusterActivitySummaryRow[];
  hasRegions: boolean;
}

export type ClusterReportData = Record<string, unknown>;
