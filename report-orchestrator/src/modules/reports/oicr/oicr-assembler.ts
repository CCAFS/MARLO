import {
  GEOGRAPHIC_SCOPE,
  SRF_TARGET_OPTION_YES,
} from './oicr.constants';
import {
  OicrAllianceLeverRow,
  OicrCoreRow,
  OicrInstitutionRow,
  OicrInnovationRow,
  OicrPublicationRow,
  OicrQuantificationRow,
  OicrReferenceRow,
  OicrSdgLeverRow,
  OicrStudyContext,
  OicrStudyData,
  OicrStudyProjectRow,
} from './oicr.types';

function removeLeadingSemicolon(value: string | null | undefined): string | null {
  if (value == null || value === '') {
    return null;
  }
  return value.replace(/^;\s*/, '');
}

function toJsonString(value: unknown): string | null {
  if (value == null) {
    return null;
  }
  if (Array.isArray(value) && value.length === 0) {
    return '[]';
  }
  return JSON.stringify(value);
}

function joinPrefixed(items: string[], prefix: string, separator: string): string | null {
  if (items.length === 0) {
    return null;
  }
  return items.map((item) => `${prefix}${item}`).join(separator);
}

function extractCodeFromName(name: string): string {
  const match = name.match(/\d+/);
  return match ? match[0] : 'N/A';
}

function buildInstitutionJson(rows: OicrInstitutionRow[]): string | null {
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

function buildInnovationsJson(rows: OicrInnovationRow[]): string | null {
  const items = rows
    .map((row) => {
      const title = row.title?.trim()
        ? `${row.innovationId} - ${row.title.trim()}`
        : `${row.innovationId} - Untitled`;
      return {
        code: String(row.innovationId),
        title,
        pdf: 'Comming soon',
      };
    })
    .filter((item) => item.title.length > 0);

  return items.length > 0 ? toJsonString(items) : null;
}

function buildReferencesJson(rows: OicrReferenceRow[]): string | null {
  if (rows.length === 0) {
    return null;
  }
  let count = 1;
  const items = rows
    .filter((row) => row.reference != null || row.link != null)
    .map((row) => ({
      code: String(count++),
      title: row.reference,
      link: row.link,
    }));
  return items.length > 0 ? toJsonString(items) : null;
}

function buildQuantificationJson(rows: OicrQuantificationRow[]): string {
  if (rows.length === 0) {
    return '';
  }
  return JSON.stringify(
    rows.map((row) => ({
      type: row.type,
      number: row.number,
      unit: row.unit,
      comments: row.comments,
    })),
  );
}

function buildPublicationsJson(rows: OicrPublicationRow[]): string {
  if (rows.length === 0) {
    return '';
  }
  const items = rows.filter(
    (row) => row.name != null || row.position != null || row.affiliation != null,
  );
  return items.length > 0 ? JSON.stringify(items) : '';
}

function buildStudyProjectsJson(rows: OicrStudyProjectRow[]): string | null {
  const acronyms: string[] = [];
  const seen = new Set<string>();
  for (const row of rows) {
    const label = row.acronym?.trim() ? row.acronym : `C${row.projectId}`;
    if (!seen.has(label)) {
      seen.add(label);
      acronyms.push(label);
    }
  }
  return toJsonString(acronyms);
}

function buildPrimaryAllianceLeverJson(rows: OicrAllianceLeverRow[]): string | null {
  if (rows.length === 0) {
    return null;
  }
  const first = rows[0];
  const strategicOutcome = rows
    .filter((row) => row.leverId === first.leverId && row.outcomeDescription)
    .map((row) => row.outcomeDescription as string)
    .filter((value, index, array) => array.indexOf(value) === index);

  return toJsonString({
    code: extractCodeFromName(first.leverName),
    name: first.leverName,
    description: first.leverDescription,
    strategicOutcome,
  });
}

function buildSdgJson(rows: OicrSdgLeverRow[]): {
  primarySDGcontribution: string | null;
  relatedSDGContribution: string | null;
  relatedLever: string | null;
} {
  if (rows.length === 0) {
    return {
      primarySDGcontribution: null,
      relatedSDGContribution: null,
      relatedLever: null,
    };
  }

  const primary = new Set<string>();
  const related = new Set<string>();
  const relatedLevers: string[] = [];

  for (const row of rows) {
    if (row.sdgName == null) {
      continue;
    }
    const sdgLabel = row.sdgCode ? `${row.sdgCode} ${row.sdgName}` : row.sdgName;
    if (row.isPrimary) {
      primary.add(sdgLabel);
    } else {
      related.add(sdgLabel);
      if (row.leverName && row.leverDescription) {
        const entry = `${row.leverName}: ${row.leverDescription}`;
        if (!relatedLevers.includes(entry)) {
          relatedLevers.push(entry);
        }
      }
    }
  }

  return {
    primarySDGcontribution: toJsonString([...primary]),
    relatedSDGContribution: toJsonString([...related]),
    relatedLever: relatedLevers.length > 0 ? toJsonString(relatedLevers) : null,
  };
}

function resolveClusterLabel(core: OicrCoreRow): string {
  if (core.projectTitle != null && core.projectTitle.trim() !== '') {
    return core.projectAcronym?.trim() ? core.projectAcronym : `C${core.projectId}`;
  }
  return `C${core.projectId}`;
}

function resolveClusterAcronym(core: OicrCoreRow): string {
  return core.projectAcronym?.trim() ? core.projectAcronym : `C${core.projectId}`;
}

function resolveGeography(context: OicrStudyContext): {
  geographicScopes: string | null;
  countries: string | null;
  regions: string | null;
} {
  const scopeNames = context.geographicScopes.map((row) => row.scopeName);
  const isNational = context.geographicScopes.some(
    (row) =>
      row.scopeId !== GEOGRAPHIC_SCOPE.GLOBAL
      && row.scopeId !== GEOGRAPHIC_SCOPE.REGIONAL,
  );
  const isRegional = context.geographicScopes.some(
    (row) => row.scopeId === GEOGRAPHIC_SCOPE.REGIONAL,
  );

  return {
    geographicScopes: scopeNames.length > 0 ? scopeNames.join(', ') : null,
    countries: isNational ? joinPrefixed(context.countries, '; ', '') : null,
    regions: isRegional ? joinPrefixed(context.regions, '; ', '') : null,
  };
}

function yesNo(value: boolean | null | undefined): string | null {
  if (value == null) {
    return null;
  }
  return value ? 'Yes' : 'No';
}

export function assembleOicrStudyData(context: OicrStudyContext, timeCreation: string): OicrStudyData {
  const { core } = context;
  const geography = resolveGeography(context);
  const sdg = buildSdgJson(context.sdgLevers);
  const studiesReference =
    buildReferencesJson(context.references)
    ?? (core.referencesText?.trim() ? core.referencesText : null);

  const globalTargetsJoined = joinPrefixed(context.globalTargets, '; ', '');

  return {
    id: core.id,
    year: core.year,
    title: core.title,
    commissioningStudy: core.commissioningStudy,
    status: core.status,
    type: core.type,
    outcomeImpactStatement: core.outcomeImpactStatement,
    isContributionText: null,
    stageStudy: core.stageStudy?.trim() ? core.stageStudy : null,
    srfTargets:
      core.isSrfTarget === SRF_TARGET_OPTION_YES
        ? joinPrefixed(context.srfTargets, '; ', '')
        : null,
    subIdos: joinPrefixed(context.subIdos, '; ', ''),
    topLevelComments: core.topLevelComments,
    geographicScopes: geography.geographicScopes,
    regions: removeLeadingSemicolon(geography.regions),
    countries: removeLeadingSemicolon(geography.countries),
    scopeComments: core.scopeComments,
    crps: joinPrefixed(context.crps, '; ', ''),
    flagships: joinPrefixed(context.flagships, '; ', ''),
    regionalPrograms: joinPrefixed(context.regionalPrograms, '; ', ''),
    institutions: removeLeadingSemicolon(buildInstitutionJson(context.institutions)),
    elaborationOutcomeImpactStatement: core.elaborationOutcomeImpactStatement,
    referenceText: studiesReference,
    quantification: buildQuantificationJson(context.quantifications),
    genderRelevance: core.genderRelevance,
    youthRelevance: core.youthRelevance,
    capacityRelevance: core.capacityRelevance,
    otherCrossCuttingDimensions: core.otherCrossCuttingDimensions,
    communicationsMaterial: core.communicationsMaterial,
    contacts: removeLeadingSemicolon(core.contacts),
    studyProjects: removeLeadingSemicolon(buildStudyProjectsJson(context.studyProjects)),
    tagged: core.tagged,
    cgiarInnovation: core.cgiarInnovation,
    cgiarInnovations: buildInnovationsJson(context.innovations),
    climateRelevance: core.climateRelevance,
    link: null,
    links: toJsonString(context.links),
    studyPolicies: null,
    url: null,
    studiesReference,
    meliaPublications: core.meliaPublications,
    performanceIndicator: toJsonString(context.performanceIndicators),
    covidAnalysis: yesNo(core.hasCovidAnalysis),
    centers: removeLeadingSemicolon(buildInstitutionJson(context.centers)),
    clusterAcronym: resolveClusterAcronym(core),
    allianceOICRID: core.allianceOicr ?? '',
    primaryAllianceLever: buildPrimaryAllianceLeverJson(context.allianceLevers),
    strategicOutcome: null,
    primarySDGcontribution: removeLeadingSemicolon(sdg.primarySDGcontribution),
    relatedLever: removeLeadingSemicolon(sdg.relatedLever),
    relatedSDGContribution: removeLeadingSemicolon(sdg.relatedSDGContribution),
    hasCgiarContribution: yesNo(core.hasCgiarContribution),
    impactArea: context.impactAreas[0]?.name ?? null,
    globalTargets: removeLeadingSemicolon(globalTargetsJoined),
    publications: buildPublicationsJson(context.publications),
    tagAs: core.tagAs,
    clusterName: resolveClusterLabel(core),
    leadPerson: core.leadPerson,
    isAllianceContribution: context.hasAllianceInstitution ? 'Yes' : 'No',
    impactAreaCode: context.impactAreas[0] ? String(context.impactAreas[0].id) : null,
    reasonNotCgiarContribution: core.reasonNotCgiarContribution,
    otherCrossCuttingSelection: core.otherCrossCuttingSelection,
    timeCreation,
  };
}
