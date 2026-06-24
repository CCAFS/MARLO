import {
  booleanToString,
  formatInnovationTimeCreation,
  joinPrefixed,
  normalizeJsonField,
  removeLeadingSemicolon,
  toJsonString,
  yesNo,
} from '../../../shared/report-json.utils';
import {
  InnovationActorRow,
  InnovationAllianceOrgRow,
  InnovationBundleRow,
  InnovationCenterRow,
  InnovationComplementarySolutionRow,
  InnovationContext,
  InnovationCoreRow,
  InnovationInstitutionRow,
  InnovationReferenceRow,
  InnovationReferenceUrlRow,
  InnovationReportData,
} from './innovation.types';

function intToString(value: number | null | undefined): string | null {
  return value != null ? String(value) : null;
}

function mysqlBool(value: number | null | undefined): boolean | null {
  if (value == null) {
    return null;
  }
  return value === 1;
}

function buildInstitutionsJson(rows: InnovationInstitutionRow[]): string | null {
  if (rows.length === 0) {
    return null;
  }
  return toJsonString(
    rows.map((row) => ({
      name: row.name,
      type: row.type,
      headquarter: row.headquarter,
      isScaling: booleanToString(mysqlBool(row.isScaling)),
      isDemand: booleanToString(mysqlBool(row.isDemand)),
      isInnovation: booleanToString(mysqlBool(row.isInnovation)),
      isOther: booleanToString(mysqlBool(row.isOther)),
    })),
  );
}

function buildCentersJson(rows: InnovationCenterRow[]): string | null {
  if (rows.length === 0) {
    return null;
  }
  return toJsonString(
    rows.map((row) => ({
      name: row.name,
      type: row.type,
      headquarter: row.headquarter,
    })),
  );
}

function buildStudiesJson(context: InnovationContext): string | null {
  if (context.studies.length === 0) {
    return null;
  }
  return toJsonString(
    context.studies.map((row) => ({
      name: row.name,
      studyType: row.studyType,
    })),
  );
}

function buildAllianceOrganizationsJson(rows: InnovationAllianceOrgRow[]): string | null {
  if (rows.length === 0) {
    return null;
  }
  const seen = new Set<string>();
  const items: Array<Record<string, string | null>> = [];
  for (const row of rows) {
    const key = `${row.name}|${row.type}|${row.howMany}`;
    if (seen.has(key)) {
      continue;
    }
    seen.add(key);
    items.push({
      name: row.name,
      scalingPartner: booleanToString(mysqlBool(row.scalingPartner)),
      type: row.type,
      howMany: row.howMany != null ? String(row.howMany) : null,
    });
  }
  return items.length > 0 ? toJsonString(items) : null;
}

function buildActorsJson(rows: InnovationActorRow[]): string | null {
  if (rows.length === 0) {
    return null;
  }
  return toJsonString(
    rows.map((row) => ({
      type: row.type,
      total: row.total,
      other: row.other,
      name: row.name,
      sexAgeNotApply: yesNo(mysqlBool(row.sexAgeNotApply)),
      womenYouth: yesNo(mysqlBool(row.womenYouth)),
      womenYouthNumber: row.womenYouthNumber,
      womenNotYouth: yesNo(mysqlBool(row.womenNotYouth)),
      womenNotYouthNumber: row.womenNotYouthNumber,
      menYouth: yesNo(mysqlBool(row.menYouth)),
      menYouthNumber: row.menYouthNumber,
      menNotYouth: yesNo(mysqlBool(row.menNotYouth)),
      menNotYouthNumber: row.menNotYouthNumber,
    })),
  );
}

function buildDeliverableReference(
  row: InnovationReferenceRow | InnovationReferenceUrlRow,
  crpAcronym: string,
  phaseId: number,
  baseUrl: string,
): { url: string | null; reference: string | null; deliverableCategory: string | null; deliverableType: string | null } {
  if (mysqlBool(row.evidenceByDeliverable) && row.deliverableId != null) {
    const url =
      `${baseUrl}/clusters/${crpAcronym}/deliverable.do?deliverableID=${row.deliverableId}` +
      `&edit=true&phaseID=${phaseId}`;
    const reference =
      row.deliverableTitle != null ? `D${row.deliverableId} - ${row.deliverableTitle}` : null;
    return {
      url,
      reference,
      deliverableCategory: row.deliverableCategory,
      deliverableType: row.deliverableType,
    };
  }
  return {
    url: row.link,
    reference: row.reference,
    deliverableCategory: row.typeCategory,
    deliverableType: row.typeName,
  };
}

function buildReferencesJson(
  rows: InnovationReferenceRow[],
  crpAcronym: string,
  phaseId: number,
  baseUrl: string,
): string | null {
  if (rows.length === 0) {
    return null;
  }
  const items = rows.map((row) => {
    const ref = buildDeliverableReference(row, crpAcronym, phaseId, baseUrl);
    return {
      url: ref.url,
      reference: ref.reference,
      deliverableCategory: ref.deliverableCategory,
      deliverableType: ref.deliverableType,
      gender: booleanToString(mysqlBool(row.gender)),
      climateChange: booleanToString(mysqlBool(row.climateChange)),
      nutrition: booleanToString(mysqlBool(row.nutrition)),
      enviromental: booleanToString(mysqlBool(row.environmental)),
      poverty: booleanToString(mysqlBool(row.poverty)),
      innovationReadiness: booleanToString(mysqlBool(row.innovationReadiness)),
    };
  });
  return toJsonString(items);
}

function buildReferenceUrlsJson(
  rows: InnovationReferenceUrlRow[],
  crpAcronym: string,
  phaseId: number,
  baseUrl: string,
): string | null {
  if (rows.length === 0) {
    return null;
  }
  const items = rows.map((row) => {
    const ref = buildDeliverableReference(row, crpAcronym, phaseId, baseUrl);
    return {
      url: ref.url,
      reference: ref.reference,
      deliverableCategory: ref.deliverableCategory,
      deliverableType: ref.deliverableType,
    };
  });
  return toJsonString(items);
}

function buildBundlesJson(
  rows: InnovationBundleRow[],
  crpAcronym: string,
  phaseId: number,
  baseUrl: string,
): string | null {
  if (rows.length === 0) {
    return null;
  }
  return toJsonString(
    rows.map((row) => ({
      id: row.selectedInnovationId,
      title: row.title,
      projectAcronym: row.projectAcronym,
      projectType: row.projectType,
      projectReadinessLevel: row.projectReadinessLevel,
      url:
        `${baseUrl}/clusters/${crpAcronym}/innovation.do?innovationID=${row.selectedInnovationId}` +
        `&edit=true&phaseID=${phaseId}`,
    })),
  );
}

function buildComplementarySolutionsJson(rows: InnovationComplementarySolutionRow[]): string | null {
  if (rows.length === 0) {
    return null;
  }
  return toJsonString(
    rows.map((row) => {
      const functions =
        row.functionTitles != null && row.functionTitles.length > 0
          ? row.functionTitles.split('||')
          : null;
      return {
        id: row.id,
        title: row.title,
        shortDescription: row.shortDescription,
        shortTitle: row.shortTitle,
        type: row.type,
        functions: functions && functions.length > 0 ? functions : null,
      };
    }),
  );
}

function buildGeographicScopes(context: InnovationContext): string | null {
  const names = [...new Set(context.geographicScopes.map((row) => row.scopeName))];
  return names.length > 0 ? names.join(', ') : null;
}

function buildCoreScalars(core: InnovationCoreRow): Record<string, unknown> {
  return {
    intellectualProperty: core.intellectualProperty,
    hasFurtherDevelopment: booleanToString(mysqlBool(core.hasFurtherDevelopment)),
    hasLegalRestrictions: booleanToString(mysqlBool(core.hasLegalRestrictions)),
    hasAssetPotential: booleanToString(mysqlBool(core.hasAssetPotential)),
    hasCgiarContribution: booleanToString(mysqlBool(core.hasCgiarContribution)),
    reasonNotCgiarContribution: core.reasonNotCgiarContribution,
    beneficiariesNarrative: core.beneficiariesNarrative,
    knowledgeMethodsAndToolsNarrative: core.knowledgeMethodsAndToolsNarrative,
    knowledgeResultsNarrative: core.knowledgeResultsNarrative,
    scalingBarriers: core.knowledgeToolUsesNarrative,
    cheaperAlternatives: intToString(core.cheaperAlternatives),
    simplerUse: intToString(core.simplerUse),
    performBetter: intToString(core.performBetter),
    innovationDesirable: intToString(core.innovationDesirable),
    innovationCommercially: intToString(core.innovationCommercially),
    innovationSupported: intToString(core.innovationSupported),
    evidenceUptake: intToString(core.evidenceUptake),
    foreseeBarriers: yesNo(mysqlBool(core.foreseeBarriers)),
    genderScore: core.genderScore,
    environmentalScore: core.environmentalScore,
    povertyScore: core.povertyScore,
    climateChangeScore: core.climateChangeScore,
    foodSecurityScore: core.foodSecurityScore,
    readinessScale: core.readinessScale,
    repIndPhaseResearchPartnership: core.repIndPhaseResearchPartnership,
    repIndStageInnovation: core.repIndStageInnovation,
    repIndRegion: core.repIndRegion,
    repIndInnovationType: core.repIndInnovationType,
    repIndInnovationTypePRMS: core.repIndInnovationTypePrms,
    repIndInnovationNature: core.repIndInnovationNature,
    repIndDegreeInnovation: core.repIndDegreeInnovation,
    leadOrganization: core.leadOrganization,
  };
}

export function assembleInnovationData(
  context: InnovationContext,
  baseUrl: string,
  crpAcronymOverride?: string,
): InnovationReportData {
  const { core } = context;
  const crpAcronym = crpAcronymOverride ?? core.crpAcronym ?? 'AICCRA';
  const phaseId = core.phaseId;

  const countries = joinPrefixed(context.countries.map((row) => row.name), '; ', '');
  const regions = joinPrefixed(context.regions.map((row) => row.name), '; ', '');
  const toolCategories =
    context.toolCategories.length > 0 ? context.toolCategories.join('; ') : null;

  const uniqueAllianceLevers = [...new Set(context.allianceLevers)];

  return {
    type: 'AICCRA Innovation',
    id: String(core.innovationId),
    projectID: core.projectId,
    clusterAcronym: core.projectAcronym,
    title: core.title,
    narrative: core.narrative,
    innovationImportance: core.innovationImportance,
    year: core.year != null ? String(core.year) : null,
    impactAreaCode: null,
    phaseID: String(phaseId),
    clearLead: mysqlBool(core.clearLead) ?? false,
    haveRegions: false,
    haveCountries: false,
    ...buildCoreScalars(core),
    sdgs: normalizeJsonField(toJsonString(context.sdgs)),
    allianceLevers: normalizeJsonField(toJsonString(uniqueAllianceLevers)),
    impactArea: normalizeJsonField(toJsonString(context.impactAreas)),
    actors: normalizeJsonField(buildActorsJson(context.actors)),
    allianceOrganizations: normalizeJsonField(buildAllianceOrganizationsJson(context.allianceOrganizations)),
    centers: normalizeJsonField(buildCentersJson(context.centers)),
    geographicScopes: buildGeographicScopes(context),
    regions: removeLeadingSemicolon(regions),
    countries: removeLeadingSemicolon(countries),
    deliverables: null,
    institutions: normalizeJsonField(buildInstitutionsJson(context.contributingOrganizations)),
    studies: normalizeJsonField(buildStudiesJson(context)),
    studyProjects: normalizeJsonField(toJsonString(context.sharedProjects)),
    toolCategories: normalizeJsonField(toolCategories),
    references: normalizeJsonField(buildReferencesJson(context.references, crpAcronym, phaseId, baseUrl)),
    referenceUrls: normalizeJsonField(
      buildReferenceUrlsJson(context.referenceUrls, crpAcronym, phaseId, baseUrl),
    ),
    referenceComplementarySolutions: normalizeJsonField(
      buildReferencesJson(context.referenceComplementarySolutions, crpAcronym, phaseId, baseUrl),
    ),
    performanceIndicator: normalizeJsonField(toJsonString(context.crpOutcomes)),
    partners: null,
    partnerInstitutions: null,
    partnerPersons: null,
    myProjects: null,
    partnerships: null,
    isAllianceContribution: context.hasAllianceInstitution ? 'Yes' : 'No',
    innovationBundle: normalizeJsonField(buildBundlesJson(context.bundles, crpAcronym, phaseId, baseUrl)),
    complementarySolutions: normalizeJsonField(buildComplementarySolutionsJson(context.complementarySolutions)),
    timeCreation: formatInnovationTimeCreation(),
  };
}
