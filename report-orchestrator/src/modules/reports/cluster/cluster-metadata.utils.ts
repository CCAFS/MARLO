/** Mirrors ReportingSummaryAction.getMetadataElementReadableName() using global.properties labels. */
const METADATA_LABELS: Record<string, string> = {
  'metadata.title': 'Disseminated title',
  'metadata.description': 'Description / Abstract',
  'metadata.date': 'Publication / Creation date',
  'metadata.language': 'Language',
  'metadata.countries': 'Country',
  'metadata.keywords': 'Keywords',
  'metadata.citation': 'Citation',
  'metadata.handle': 'Handle',
  'metadata.doi': 'DOI',
  'metadata.creator': 'Creator / Authors',
  'metadata.authors': 'Creators / Authors',
  'metadata.identifier': 'Identifier',
  'metadata.relation': 'Relation',
  'metadata.publisher': 'Publisher',
  'metadata.contributor': 'Contributor',
  'metadata.contributorCRP': 'Contributor CRP',
  'metadata.contributorCenter': 'Contributor Center',
  'metadata.contributorFunder': 'Contributor Funder',
  'metadata.contributorProject': 'Contributor Project',
  'metadata.contributorPartner': 'Contributor Partner',
  'metadata.source': 'Source',
  'metadata.publicationDate': 'Publication / Creation date',
  'metadata.country': 'Country',
  'metadata.coverage': 'Coverage',
  'metadata.coverageRegion': 'Region',
  'metadata.coverageGeoLocation': 'GeoLocation (Coordinates)',
  'metadata.coverageCountry': 'Country',
  'metadata.coverageAdminUnit': 'Administrative unit',
  'metadata.format': 'Format',
  'metadata.rights': 'Rights',
  'metadata.subject': 'Subject',
  'metadata.subjectAgrovoc': 'Agrovoc Subject',
  'metadata.subjectDomainSpecific': 'Domain-specific Subject',
};

export function getMetadataElementReadableName(encodedName: string | null | undefined): string | null {
  if (encodedName == null || encodedName.trim() === '') {
    return encodedName ?? null;
  }

  let i18nKey: string | null = null;
  if (encodedName === 'dc.title') {
    i18nKey = 'metadata.title';
  } else if (encodedName === 'dc.description.abstract') {
    i18nKey = 'metadata.description';
  } else if (encodedName === 'dc.date') {
    i18nKey = 'metadata.date';
  } else if (encodedName === 'dc.language') {
    i18nKey = 'metadata.language';
  } else if (encodedName === 'cg:coverage.country' || encodedName === 'dc.coverage.country') {
    i18nKey = 'metadata.countries';
  } else if (encodedName === 'marlo.keywords' || encodedName === 'dc.subject') {
    i18nKey = 'metadata.keywords';
  } else if (encodedName === 'dc.identifier.citation') {
    i18nKey = 'metadata.citation';
  } else if (encodedName === 'marlo.handle' || encodedName === 'dc.identifier.uri') {
    i18nKey = 'metadata.handle';
  } else if (encodedName === 'marlo.doi' || encodedName === 'dc.identifier.doi') {
    i18nKey = 'metadata.doi';
  } else if (encodedName === 'marlo.authors' || encodedName === 'dc.creator') {
    i18nKey = 'metadata.creator';
  } else if (encodedName.startsWith('dc.')) {
    const suffix = encodedName.substring(3);
    if (suffix === 'identifier.citation') {
      i18nKey = 'metadata.citation';
    } else if (suffix === 'identifier.uri') {
      i18nKey = 'metadata.handle';
    } else if (suffix === 'identifier.doi') {
      i18nKey = 'metadata.doi';
    } else if (suffix === 'coverage.country') {
      i18nKey = 'metadata.countries';
    } else {
      i18nKey = `metadata.${suffix}`;
    }
  } else if (encodedName.startsWith('marlo.')) {
    i18nKey = `metadata.${encodedName.substring(6)}`;
  } else if (encodedName.startsWith('cg:')) {
    const suffix = encodedName.substring(3);
    i18nKey = suffix === 'coverage.country' ? 'metadata.countries' : `metadata.${suffix.replace(':', '.')}`;
  }

  if (i18nKey != null) {
    const readableName = METADATA_LABELS[i18nKey];
    if (readableName) {
      return readableName;
    }
  }

  return encodedName;
}
