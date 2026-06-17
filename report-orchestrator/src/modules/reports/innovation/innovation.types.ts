export interface InnovationCoreRow {
  innovationId: number;
  projectId: number;
  projectAcronym: string | null;
  crpAcronym: string | null;
  phaseId: number;
  title: string | null;
  narrative: string | null;
  innovationImportance: string | null;
  year: number | null;
  clearLead: number | null;
  readinessScale: string | null;
  repIndPhaseResearchPartnership: string | null;
  repIndStageInnovation: string | null;
  repIndRegion: string | null;
  repIndInnovationType: string | null;
  repIndInnovationTypePrms: string | null;
  repIndInnovationNature: string | null;
  repIndDegreeInnovation: string | null;
  leadOrganization: string | null;
  intellectualProperty: string | null;
  hasLegalRestrictions: number | null;
  hasAssetPotential: number | null;
  hasFurtherDevelopment: number | null;
  hasCgiarContribution: number | null;
  reasonNotCgiarContribution: string | null;
  beneficiariesNarrative: string | null;
  knowledgeMethodsAndToolsNarrative: string | null;
  knowledgeResultsNarrative: string | null;
  knowledgeToolUsesNarrative: string | null;
  cheaperAlternatives: number | null;
  simplerUse: number | null;
  performBetter: number | null;
  innovationDesirable: number | null;
  innovationCommercially: number | null;
  innovationSupported: number | null;
  evidenceUptake: number | null;
  foreseeBarriers: number | null;
  genderScore: string | null;
  climateChangeScore: string | null;
  foodSecurityScore: string | null;
  environmentalScore: string | null;
  povertyScore: string | null;
}

export interface InnovationGeographicScopeRow {
  scopeId: number;
  scopeName: string;
}

export interface InnovationLocElementRow {
  name: string;
}

export interface InnovationInstitutionRow {
  name: string;
  type: string | null;
  headquarter: string;
  isScaling: number | null;
  isDemand: number | null;
  isInnovation: number | null;
  isOther: number | null;
}

export interface InnovationCenterRow {
  name: string;
  type: string | null;
  headquarter: string;
}

export interface InnovationStudyRow {
  name: string | null;
  studyType: string | null;
}

export interface InnovationAllianceOrgRow {
  name: string;
  scalingPartner: number | null;
  type: string | null;
  howMany: number | null;
}

export interface InnovationActorRow {
  type: string;
  total: number;
  other: string | null;
  name: string;
  sexAgeNotApply: number | null;
  womenYouth: number | null;
  womenYouthNumber: number | null;
  womenNotYouth: number | null;
  womenNotYouthNumber: number | null;
  menYouth: number | null;
  menYouthNumber: number | null;
  menNotYouth: number | null;
  menNotYouthNumber: number | null;
}

export interface InnovationReferenceRow {
  evidenceByDeliverable: number | null;
  deliverableId: number | null;
  deliverableTitle: string | null;
  deliverableCategory: string | null;
  deliverableType: string | null;
  link: string | null;
  reference: string | null;
  typeCategory: string | null;
  typeName: string | null;
  gender: number | null;
  climateChange: number | null;
  nutrition: number | null;
  environmental: number | null;
  poverty: number | null;
  innovationReadiness: number | null;
}

export interface InnovationReferenceUrlRow {
  evidenceByDeliverable: number | null;
  deliverableId: number | null;
  deliverableTitle: string | null;
  deliverableCategory: string | null;
  deliverableType: string | null;
  link: string | null;
  reference: string | null;
  typeCategory: string | null;
  typeName: string | null;
}

export interface InnovationBundleRow {
  selectedInnovationId: number;
  title: string | null;
  projectAcronym: string | null;
  projectType: string | null;
  projectReadinessLevel: string | null;
}

export interface InnovationComplementarySolutionRow {
  id: number;
  title: string | null;
  shortTitle: string | null;
  shortDescription: string | null;
  type: string | null;
  functionTitles: string | null;
}

export interface InnovationContext {
  core: InnovationCoreRow;
  geographicScopes: InnovationGeographicScopeRow[];
  countries: InnovationLocElementRow[];
  regions: InnovationLocElementRow[];
  contributingOrganizations: InnovationInstitutionRow[];
  centers: InnovationCenterRow[];
  studies: InnovationStudyRow[];
  sharedProjects: string[];
  allianceLevers: string[];
  sdgs: string[];
  impactAreas: string[];
  allianceOrganizations: InnovationAllianceOrgRow[];
  actors: InnovationActorRow[];
  toolCategories: string[];
  references: InnovationReferenceRow[];
  referenceUrls: InnovationReferenceUrlRow[];
  referenceComplementarySolutions: InnovationReferenceRow[];
  crpOutcomes: string[];
  bundles: InnovationBundleRow[];
  complementarySolutions: InnovationComplementarySolutionRow[];
  hasAllianceInstitution: boolean;
  isNational: boolean;
  isRegional: boolean;
}

export type InnovationReportData = Record<string, unknown>;
