import { FLAG_ASSET_BASE_URL } from './cluster.constants';
import { InnovationContext, InnovationReferenceRow } from '../innovation/innovation.types';
import { sanitizeText } from './cluster-text.utils';

/** Maps InnovationContext to cluster report innovations[] item — mirrors ReportingSummaryAction.buildInnovationData(). */
export function mapInnovationContextForClusterEmbed(context: InnovationContext): Record<string, unknown> {
  const { core } = context;
  const data: Record<string, unknown> = { id: core.innovationId };

  putIfPresent(data, 'title', sanitizeText(core.title));
  putIfPresent(data, 'shortTitle', sanitizeText(core.shortTitle));
  putIfPresent(data, 'year', core.year);
  putIfPresent(data, 'innovationNumber', core.innovationNumber);
  putIfPresent(data, 'narrative', sanitizeText(core.narrative));
  putIfPresent(data, 'innovationType', sanitizeText(core.repIndInnovationType));
  putIfPresent(data, 'otherInnovationType', sanitizeText(core.otherInnovationType));
  putIfPresent(data, 'phaseResearchPartnership', sanitizeText(core.repIndPhaseResearchPartnership));
  putIfPresent(data, 'stageInnovation', sanitizeText(core.repIndStageInnovation));
  putIfPresent(data, 'descriptionStage', sanitizeText(core.descriptionStage));
  putIfPresent(data, 'degreeInnovation', sanitizeText(core.repIndDegreeInnovation));
  putIfPresent(data, 'region', sanitizeText(core.repIndRegion));
  putIfPresent(data, 'evidenceLink', sanitizeText(core.evidenceLink));
  putIfPresent(data, 'adaptativeResearchNarrative', sanitizeText(core.adaptativeResearchNarrative));
  putIfPresent(data, 'leadOrganization', sanitizeText(core.leadOrganization));
  if (core.clearLead != null) {
    data.clearLead = core.clearLead === 1 ? 'Yes' : 'No';
  }
  if (core.innovationBundle != null) {
    data.innovationBundle = core.innovationBundle === 1 ? 'Yes' : 'No';
  }
  putIfPresent(data, 'innovationNature', sanitizeText(core.repIndInnovationNature));
  putIfPresent(data, 'genderFocusLevel', sanitizeText(core.genderFocusLevel));
  putIfPresent(data, 'genderExplanation', sanitizeText(core.genderExplanation));
  putIfPresent(data, 'youthFocusLevel', sanitizeText(core.youthFocusLevel));
  putIfPresent(data, 'youthExplanation', sanitizeText(core.youthExplanation));
  putIfPresent(data, 'beneficiariesNarrative', sanitizeText(core.beneficiariesNarrative));
  putIfPresent(data, 'readinessScale', sanitizeText(core.readinessScale));

  putIfPresent(data, 'cheaperAlternatives', core.cheaperAlternatives);
  putIfPresent(data, 'simplerUse', core.simplerUse);
  putIfPresent(data, 'performBetter', core.performBetter);
  putIfPresent(data, 'innovationDesirable', core.innovationDesirable);
  putIfPresent(data, 'innovationCommercially', core.innovationCommercially);
  putIfPresent(data, 'innovationSupported', core.innovationSupported);
  putIfPresent(data, 'evidenceUptake', core.evidenceUptake);

  if (core.hasKnowledgePotentialId != null) {
    data.hasKnowledgePotential = core.hasKnowledgePotentialId;
    if (
      core.hasKnowledgePotentialId === 2
      && core.reasonKnowledgePotential
      && core.reasonKnowledgePotential.trim() !== ''
    ) {
      data.reasonKnowledgePotential = sanitizeText(core.reasonKnowledgePotential);
      data.showReasonKnowledgePotential = true;
    }
  }

  putIfPresent(
    data,
    'knowledgeMethodsAndToolsNarrative',
    sanitizeText(core.knowledgeMethodsAndToolsNarrative),
  );

  if (core.areUsersDetermined != null) {
    data.areUsersDetermined = core.areUsersDetermined === 1;
    if (core.areUsersDetermined === 1 && context.actors.length > 0) {
      data.actors = context.actors.map((actor) => {
        const actorData: Record<string, unknown> = {};
        putIfPresent(actorData, 'type', sanitizeText(actor.type));
        putIfPresent(actorData, 'total', actor.total);
        putIfPresent(actorData, 'other', sanitizeText(actor.other));
        putIfPresent(actorData, 'womenYouthNumber', actor.womenYouthNumber);
        putIfPresent(actorData, 'womenNonYouthNumber', actor.womenNotYouthNumber);
        putIfPresent(actorData, 'menYouthNumber', actor.menYouthNumber);
        putIfPresent(actorData, 'menNonYouthNumber', actor.menNotYouthNumber);
        return actorData;
      });
      data.hasActors = true;
    }
  }

  if (core.hasCgiarContribution != null) {
    data.hasCgiarContribution = core.hasCgiarContribution === 1 ? 'Yes' : 'No';
  }
  putIfPresent(data, 'reasonNotCgiarContribution', sanitizeText(core.reasonNotCgiarContribution));
  putIfPresent(
    data,
    'intellectualPropertyInstitution',
    sanitizeText(core.intellectualPropertyInstitution ?? core.intellectualProperty),
  );
  if (core.hasLegalRestrictions != null) {
    data.hasLegalRestrictions = core.hasLegalRestrictions === 1 ? 'Yes' : 'No';
  }
  if (core.hasAssetPotential != null) {
    data.hasAssetPotential = core.hasAssetPotential === 1 ? 'Yes' : 'No';
  }
  if (core.hasFurtherDevelopment != null) {
    data.hasFurtherDevelopment = core.hasFurtherDevelopment === 1 ? 'Yes' : 'No';
  }

  const geographicScopes = context.geographicScopes.map((scope) => scope.scopeName).filter(Boolean);
  if (geographicScopes.length > 0) {
    data.geographicScopes = geographicScopes.join(', ');
  }

  if (context.isRegional && context.regions.length > 0) {
    data.regions = context.regions.map((region) => sanitizeText(region.name)).filter(Boolean).join(', ');
  }

  const shouldShowCountries = context.isNational || core.hasSpecifiedOutputCountries === 1;
  if (shouldShowCountries && context.countries.length > 0) {
    data.countries = context.countries.map((country) => {
      const entry: Record<string, unknown> = { name: sanitizeText(country.name) };
      if (country.isoAlpha2) {
        entry.isoAlpha2 = country.isoAlpha2;
        entry.flagUrl = `${FLAG_ASSET_BASE_URL}/${country.isoAlpha2}.svg`;
      }
      return entry;
    });
  }

  joinIfPresent(data, 'organizations', context.organizations);

  const contributingPartners = context.contributingOrganizations.map((partner) => {
    const roles: string[] = [];
    if (partner.isScaling === 1) {
      roles.push('Scaling');
    }
    if (partner.isDemand === 1) {
      roles.push('Demand');
    }
    if (partner.isInnovation === 1) {
      roles.push('Innovation');
    }
    if (partner.isOther === 1) {
      roles.push('Other');
    }
    const partnerData: Record<string, unknown> = { name: sanitizeText(partner.name) };
    if (roles.length > 0) {
      partnerData.role = roles.join(', ');
    }
    return partnerData;
  }).filter((partner) => partner.name);
  if (contributingPartners.length > 0) {
    data.contributingPartners = contributingPartners;
    data.hasContributingPartners = true;
  }

  if (context.allianceOrganizations.length > 0) {
    data.allianceOrganizations = context.allianceOrganizations.map((org) => {
      const orgData: Record<string, unknown> = { name: sanitizeText(org.name) };
      putIfPresent(orgData, 'institutionType', sanitizeText(org.type));
      if (org.scalingPartner != null) {
        orgData.scalingPartner = org.scalingPartner === 1;
      }
      putIfPresent(orgData, 'number', org.howMany);
      return orgData;
    });
    data.hasAllianceOrganizations = true;
  }

  joinIfPresent(data, 'crps', context.crps);
  joinIfPresent(data, 'centers', context.centers.map((center) => center.name));

  if (context.milestones.length > 0) {
    data.hasMilestones = true;
    data.milestones = context.milestones.map((milestone) => {
      const milestoneData: Record<string, unknown> = { name: sanitizeText(milestone.name) };
      if (milestone.primary != null) {
        milestoneData.primary = milestone.primary === 1;
      }
      return milestoneData;
    });
  }

  joinIfPresent(data, 'subIdos', context.subIdos);
  joinIfPresent(data, 'projectOutcomes', context.projectOutcomes);
  joinIfPresent(data, 'crpOutcomes', context.crpOutcomes);

  if (context.contacts.length > 0) {
    data.contacts = context.contacts.map((contact) => sanitizeText(contact)).filter(Boolean).join('; ');
  }

  joinIfPresent(data, 'deliverables', context.linkedDeliverables);
  joinIfPresent(data, 'allianceLevers', context.allianceLevers);

  if (context.sdgs.length > 0) {
    data.sdgs = context.sdgs.map((sdg) => ({ name: sanitizeText(sdg) }));
    data.hasSdgs = true;
  }

  putIfPresent(data, 'genderScore', sanitizeText(core.genderScore));
  putIfPresent(data, 'climateChangeScore', sanitizeText(core.climateChangeScore));
  putIfPresent(data, 'foodSecurityScore', sanitizeText(core.foodSecurityScore));
  putIfPresent(data, 'environmentalScore', sanitizeText(core.environmentalScore));
  putIfPresent(data, 'povertyScore', sanitizeText(core.povertyScore));
  joinIfPresent(data, 'impactAreas', context.impactAreas);

  const references = mapReferences(context.references);
  if (references.length > 0) {
    data.hasReferences = true;
    data.references = references;
  }

  return data;
}

function mapReferences(references: InnovationReferenceRow[]): Array<Record<string, unknown>> {
  return references.map((reference) => {
    const referenceData: Record<string, unknown> = {};
    const evidenceByDeliverable = reference.evidenceByDeliverable === 1;

    if (!evidenceByDeliverable) {
      putIfPresent(referenceData, 'reference', sanitizeText(reference.reference));
      putIfPresent(referenceData, 'link', sanitizeText(reference.link));
      putIfPresent(referenceData, 'category', sanitizeText(reference.deliverableCategory));
      putIfPresent(referenceData, 'subCategory', sanitizeText(reference.typeName));
    } else {
      putIfPresent(referenceData, 'deliverableId', reference.deliverableId);
      putIfPresent(referenceData, 'deliverableTitle', sanitizeText(reference.deliverableTitle));
    }

    const crossCuttingDimensions: string[] = [];
    if (reference.gender === 1) {
      crossCuttingDimensions.push('Gender');
    }
    if (reference.climateChange === 1) {
      crossCuttingDimensions.push('Climate Change');
    }
    if (reference.nutrition === 1) {
      crossCuttingDimensions.push('Nutrition');
    }
    if (reference.environmental === 1) {
      crossCuttingDimensions.push('Environmental');
    }
    if (reference.poverty === 1) {
      crossCuttingDimensions.push('Poverty');
    }
    if (reference.innovationReadiness === 1) {
      crossCuttingDimensions.push('Innovation Readiness');
    }
    if (crossCuttingDimensions.length > 0) {
      referenceData.crossCuttingDimensions = crossCuttingDimensions;
    }

    return referenceData;
  }).filter((reference) => Object.keys(reference).length > 0);
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
