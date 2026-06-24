import { FLAG_ASSET_BASE_URL } from './cluster.constants';
import { SRF_TARGET_OPTION_YES } from '../oicr/oicr.constants';
import { OicrStudyContext } from '../oicr/oicr.types';
import { formatNumericValue, sanitizeText } from './cluster-text.utils';

/** Maps OicrStudyContext to cluster report oicrs[] item — mirrors ReportingSummaryAction.buildOICRData(). */
export function mapOicrContextForClusterEmbed(context: OicrStudyContext): Record<string, unknown> {
  const { core } = context;
  const data: Record<string, unknown> = { id: core.id };

  putIfPresent(data, 'title', sanitizeText(core.title));
  putIfPresent(data, 'year', core.year);
  putIfPresent(data, 'status', sanitizeText(core.status));
  putIfPresent(data, 'type', sanitizeText(core.type));
  putIfPresent(data, 'commissioningStudy', sanitizeText(core.commissioningStudy));
  putIfPresent(data, 'outcomeImpactStatement', sanitizeText(core.outcomeImpactStatement));
  putIfPresent(
    data,
    'elaborationOutcomeImpactStatement',
    sanitizeText(core.elaborationOutcomeImpactStatement),
  );
  putIfPresent(data, 'topLevelComments', sanitizeText(core.topLevelComments));
  putIfPresent(data, 'scopeComments', sanitizeText(core.scopeComments));
  putIfPresent(data, 'quantification', sanitizeText(core.quantification));
  putIfPresent(data, 'stageStudy', sanitizeText(core.stageStudy));
  putIfPresent(data, 'stageProcess', sanitizeText(core.stageProcess));
  putIfPresent(data, 'organizationType', sanitizeText(core.organizationType));
  putIfPresent(data, 'policyInvestimentType', sanitizeText(core.policyInvestimentType));
  putIfPresent(data, 'policyAmount', formatNumericValue(core.policyAmount));
  putIfPresent(data, 'cgiarInnovation', sanitizeText(core.cgiarInnovation));
  putIfPresent(data, 'otherInnovationsNarrative', sanitizeText(core.otherInnovationsNarrative));
  putIfPresent(data, 'comunicationsMaterial', sanitizeText(core.communicationsMaterial));
  putIfPresent(data, 'outcomeStory', sanitizeText(core.outcomeStory));
  putIfPresent(data, 'meliaPublications', sanitizeText(core.meliaPublications));
  putIfPresent(data, 'contacts', sanitizeText(core.contacts));
  putIfPresent(data, 'tag', sanitizeText(core.tag));
  putIfPresent(data, 'genderRelevance', sanitizeText(core.genderRelevance));
  putIfPresent(data, 'youthRelevance', sanitizeText(core.youthRelevance));
  putIfPresent(data, 'capacityRelevance', sanitizeText(core.capacityRelevance));
  putIfPresent(data, 'climateRelevance', sanitizeText(core.climateRelevance));
  putIfPresent(data, 'commentsRelevance', sanitizeText(core.commentsRelevance));
  putIfPresent(
    data,
    'otherCrossCuttingSelection',
    sanitizeText(core.otherCrossCuttingSelection),
  );
  putIfPresent(
    data,
    'otherCrossCuttingDimensions',
    sanitizeText(core.otherCrossCuttingDimensions),
  );

  if (core.isContribution != null) {
    data.isContribution = core.isContribution;
    data.isContributionText = core.isContribution ? 'Yes' : 'No';
  }

  if (core.hasCovidAnalysis != null) {
    data.hasCovidAnalysis = core.hasCovidAnalysis;
  }

  if (core.isSrfTarget != null && core.isSrfTarget !== '') {
    data.isSrfTarget = core.isSrfTarget;
    data.isSrfTargetText = core.isSrfTarget === SRF_TARGET_OPTION_YES ? 'Yes' : 'No';
  }

  const geographicScopes = context.geographicScopes.map((row) => row.scopeName).filter(Boolean);
  if (geographicScopes.length > 0) {
    data.geographicScopes = geographicScopes.join(', ');
  }

  if (context.regions.length > 0) {
    data.regions = context.regions.join(', ');
  }

  if (context.countryDetails.length > 0) {
    data.countries = context.countryDetails.map((country) => {
      const entry: Record<string, unknown> = {
        name: sanitizeText(country.name),
      };
      if (country.isoAlpha2) {
        entry.isoAlpha2 = country.isoAlpha2;
        entry.flagUrl = `${FLAG_ASSET_BASE_URL}/${country.isoAlpha2}.svg`;
      }
      return entry;
    });
  }

  joinIfPresent(data, 'flagships', context.flagships);
  joinIfPresent(data, 'regionalPrograms', context.regionalPrograms);
  joinIfPresent(data, 'subIdos', context.subIdos);
  joinIfPresent(data, 'crps', context.crps);
  joinIfPresent(data, 'policies', context.policies);
  joinIfPresent(data, 'projectOutcomes', context.projectOutcomes);
  joinIfPresent(data, 'crpOutcomes', context.crpOutcomes);

  if (context.links.length > 0) {
    data.links = context.links.map((link) => sanitizeText(link)).filter(Boolean);
  }

  if (core.isSrfTarget === SRF_TARGET_OPTION_YES && context.srfTargets.length > 0) {
    data.srfTargets = context.srfTargets.join(', ');
  }

  const institutions = context.institutions.map((row) => row.name).filter(Boolean);
  if (institutions.length > 0) {
    data.institutions = institutions.join(', ');
  }

  const centers = context.centers.map((row) => row.name).filter(Boolean);
  if (centers.length > 0) {
    data.centers = centers.join(', ');
  }

  const innovations = context.innovations
    .map((row) => {
      const title = row.title?.trim() ? `${row.innovationId} - ${row.title.trim()}` : `${row.innovationId}`;
      return sanitizeText(title);
    })
    .filter(Boolean);
  if (innovations.length > 0) {
    data.innovations = innovations.join(', ');
  }

  const allianceLeverNames = [
    ...new Set(context.allianceLevers.map((row) => row.leverName).filter(Boolean)),
  ];
  if (allianceLeverNames.length > 0) {
    data.allianceLevers = allianceLeverNames.join(', ');
  }

  const impactAreaNames = context.impactAreas.map((row) => row.name).filter(Boolean);
  if (impactAreaNames.length > 0) {
    data.impactAreas = impactAreaNames.join(', ');
  }

  if (context.globalTargets.length > 0) {
    data.globalTargets = context.globalTargets.join(', ');
  }

  if (context.quantifications.length > 0) {
    const quantifications = context.quantifications
      .map((row) => {
        const entry: Record<string, unknown> = {};
        putIfPresent(entry, 'type', sanitizeText(row.type));
        putIfPresent(entry, 'number', formatNumericValue(row.number));
        putIfPresent(entry, 'unit', sanitizeText(row.unit));
        putIfPresent(entry, 'comments', sanitizeText(row.comments));
        return Object.keys(entry).length > 0 ? entry : null;
      })
      .filter((entry): entry is Record<string, unknown> => entry != null);
    if (quantifications.length > 0) {
      data.quantifications = quantifications;
      data.hasQuantifications = true;
    }
  }

  if (context.publications.length > 0) {
    const publications = context.publications
      .map((row) => {
        const entry: Record<string, unknown> = {};
        putIfPresent(entry, 'name', sanitizeText(row.name));
        putIfPresent(entry, 'position', row.position);
        putIfPresent(entry, 'affiliation', sanitizeText(row.affiliation));
        return Object.keys(entry).length > 0 ? entry : null;
      })
      .filter((entry): entry is Record<string, unknown> => entry != null);
    if (publications.length > 0) {
      data.publications = publications;
      data.hasPublications = true;
    }
  }

  if (context.references.length > 0) {
    const references = context.references
      .map((row) => {
        const entry: Record<string, unknown> = {};
        putIfPresent(entry, 'reference', sanitizeText(row.reference));
        putIfPresent(entry, 'link', sanitizeText(row.link));
        if (row.externalAuthor != null) {
          entry.externalAuthor = row.externalAuthor;
        }
        return Object.keys(entry).length > 0 ? entry : null;
      })
      .filter((entry): entry is Record<string, unknown> => entry != null);
    if (references.length > 0) {
      data.references = references;
      data.hasReferences = true;
    }
  } else if (core.referencesText?.trim()) {
    data.referencesText = sanitizeText(core.referencesText);
  }

  return data;
}

function putIfPresent(
  target: Record<string, unknown>,
  key: string,
  value: unknown,
): void {
  if (value == null) {
    return;
  }
  if (typeof value === 'string' && value.trim() === '') {
    return;
  }
  target[key] = value;
}

function joinIfPresent(
  target: Record<string, unknown>,
  key: string,
  items: string[],
  separator = ', ',
): void {
  const filtered = items.map((item) => sanitizeText(item)).filter(Boolean) as string[];
  if (filtered.length > 0) {
    target[key] = filtered.join(separator);
  }
}
