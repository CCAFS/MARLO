export interface OicrCoreRow {
  id: number;
  projectId: number;
  year: number | null;
  title: string | null;
  commissioningStudy: string | null;
  status: string | null;
  type: string | null;
  outcomeImpactStatement: string | null;
  topLevelComments: string | null;
  scopeComments: string | null;
  allianceOicr: string | null;
  stageStudy: string | null;
  tagAs: string | null;
  tagged: string | null;
  cgiarInnovation: string | null;
  elaborationOutcomeImpactStatement: string | null;
  communicationsMaterial: string | null;
  meliaPublications: string | null;
  referencesText: string | null;
  otherCrossCuttingDimensions: string | null;
  otherCrossCuttingSelection: string | null;
  hasCgiarContribution: boolean | null;
  reasonNotCgiarContribution: string | null;
  hasCovidAnalysis: boolean | null;
  isSrfTarget: string | null;
  contacts: string | null;
  genderRelevance: string | null;
  youthRelevance: string | null;
  capacityRelevance: string | null;
  climateRelevance: string | null;
  projectAcronym: string | null;
  projectTitle: string | null;
  leadPerson: string | null;
  quantification: string | null;
  stageProcess: string | null;
  organizationType: string | null;
  policyInvestimentType: string | null;
  policyAmount: number | null;
  isContribution: boolean | null;
  otherInnovationsNarrative: string | null;
  outcomeStory: string | null;
  commentsRelevance: string | null;
  tag: string | null;
}

export interface OicrCountryRow {
  name: string;
  isoAlpha2: string | null;
}

export interface OicrGeographicScopeRow {
  scopeId: number;
  scopeName: string;
}

export interface OicrInstitutionRow {
  name: string;
  type: string;
  headquarter: string;
}

export interface OicrInnovationRow {
  innovationId: number;
  title: string | null;
}

export interface OicrReferenceRow {
  id: number;
  reference: string | null;
  link: string | null;
  externalAuthor: boolean | null;
}

export interface OicrQuantificationRow {
  type: string;
  number: string;
  unit: string;
  comments: string | null;
}

export interface OicrPublicationRow {
  name: string | null;
  position: string | null;
  affiliation: string | null;
}

export interface OicrStudyProjectRow {
  projectId: number;
  acronym: string | null;
}

export interface OicrAllianceLeverRow {
  leverId: number;
  leverName: string;
  leverDescription: string | null;
  outcomeDescription: string | null;
}

export interface OicrSdgLeverRow {
  isPrimary: boolean;
  sdgCode: string | null;
  sdgName: string | null;
  leverName: string | null;
  leverDescription: string | null;
}

export interface OicrImpactAreaRow {
  id: number;
  name: string;
}

export interface OicrStudyContext {
  core: OicrCoreRow;
  geographicScopes: OicrGeographicScopeRow[];
  countries: string[];
  countryDetails: OicrCountryRow[];
  regions: string[];
  centers: OicrInstitutionRow[];
  institutions: OicrInstitutionRow[];
  innovations: OicrInnovationRow[];
  references: OicrReferenceRow[];
  quantifications: OicrQuantificationRow[];
  performanceIndicators: string[];
  links: string[];
  publications: OicrPublicationRow[];
  studyProjects: OicrStudyProjectRow[];
  allianceLevers: OicrAllianceLeverRow[];
  sdgLevers: OicrSdgLeverRow[];
  impactAreas: OicrImpactAreaRow[];
  globalTargets: string[];
  srfTargets: string[];
  subIdos: string[];
  crps: string[];
  flagships: string[];
  regionalPrograms: string[];
  policies: string[];
  projectOutcomes: string[];
  crpOutcomes: string[];
  hasAllianceInstitution: boolean;
}

/** Full pdf.generate study payload — mirrors BaseStudySummaryData.generateAndSendJson(). */
export interface OicrStudyData extends Record<string, unknown> {
  id: number;
  year: number | null;
  title: string | null;
  commissioningStudy: string | null;
  status: string | null;
  type: string | null;
  outcomeImpactStatement: string | null;
  isContributionText: string | null;
  stageStudy: string | null;
  srfTargets: string | null;
  subIdos: string | null;
  topLevelComments: string | null;
  geographicScopes: string | null;
  regions: string | null;
  countries: string | null;
  scopeComments: string | null;
  crps: string | null;
  flagships: string | null;
  regionalPrograms: string | null;
  institutions: string | null;
  elaborationOutcomeImpactStatement: string | null;
  referenceText: string | null;
  quantification: string | null;
  genderRelevance: string | null;
  youthRelevance: string | null;
  capacityRelevance: string | null;
  otherCrossCuttingDimensions: string | null;
  communicationsMaterial: string | null;
  contacts: string | null;
  studyProjects: string | null;
  tagged: string | null;
  cgiarInnovation: string | null;
  cgiarInnovations: string | null;
  climateRelevance: string | null;
  link: string | null;
  links: string | null;
  studyPolicies: string | null;
  url: string | null;
  studiesReference: string | null;
  meliaPublications: string | null;
  performanceIndicator: string | null;
  covidAnalysis: string | null;
  centers: string | null;
  clusterAcronym: string | null;
  allianceOICRID: string | null;
  primaryAllianceLever: string | null;
  strategicOutcome: string | null;
  primarySDGcontribution: string | null;
  relatedLever: string | null;
  relatedSDGContribution: string | null;
  hasCgiarContribution: string | null;
  impactArea: string | null;
  globalTargets: string | null;
  publications: string | null;
  tagAs: string | null;
  clusterName: string | null;
  leadPerson: string | null;
  isAllianceContribution: string | null;
  impactAreaCode: string | null;
  reasonNotCgiarContribution: string | null;
  otherCrossCuttingSelection: string | null;
  timeCreation: string;
}
