import { DELIVERABLE_STATUS, FLAG_ASSET_BASE_URL, PROJECT_STATUS_LABELS } from './cluster.constants';
import { resolveDisplayYear } from './cluster-deliverable-filter';
import { sanitizeText } from './cluster-text.utils';
import {
  ClusterDeliverableExtendedContext,
  ClusterDeliverableFundingSourceRow,
  ClusterDeliverableSummaryRow,
} from './cluster.types';

/** Maps deliverable summary + extended context to cluster report deliverables[] item. */
export function mapDeliverableForCluster(
  summary: ClusterDeliverableSummaryRow,
  extended: ClusterDeliverableExtendedContext | undefined,
): Record<string, unknown> {
  const data: Record<string, unknown> = {
    id: summary.id,
    year: summary.year,
    displayYear: resolveDisplayYear(summary),
  };

  putIfPresent(data, 'title', sanitizeText(summary.title));
  if (summary.newExpectedYear != null && summary.newExpectedYear !== -1) {
    data.newExpectedYear = summary.newExpectedYear;
  }
  if (summary.status != null) {
    data.status = PROJECT_STATUS_LABELS[summary.status] ?? String(summary.status);
    if (
      summary.newExpectedYear != null
      && summary.newExpectedYear !== -1
      && (summary.status === DELIVERABLE_STATUS.EXTENDED
        || summary.status === DELIVERABLE_STATUS.ONGOING
        || summary.status === DELIVERABLE_STATUS.COMPLETE)
    ) {
      data.isExtended = true;
    }
  }
  putIfPresent(data, 'statusDescription', sanitizeText(summary.statusDescription));
  putIfPresent(data, 'description', sanitizeText(summary.description));
  putIfPresent(data, 'type', sanitizeText(summary.typeName));

  if (!extended) {
    return data;
  }

  const { core } = extended;
  putIfPresent(data, 'typeOther', sanitizeText(core.typeOther));
  putIfPresent(data, 'geographicScope', sanitizeText(core.geographicScope));
  if (core.isLocationGlobal != null) {
    data.isLocationGlobal = core.isLocationGlobal === 1 ? 'Yes' : 'No';
  }
  putIfPresent(data, 'region', sanitizeText(core.regionName));
  putIfPresent(data, 'crpProgramOutcome', sanitizeText(core.crpProgramOutcome));
  putIfPresent(data, 'crpClusterKeyOutput', sanitizeText(core.crpClusterKeyOutput));

  if (extended.countries.length > 0) {
    data.countries = extended.countries.map((country) => {
      const entry: Record<string, unknown> = { name: sanitizeText(country.name) };
      if (country.isoAlpha2) {
        entry.isoAlpha2 = country.isoAlpha2;
        entry.flagUrl = `${FLAG_ASSET_BASE_URL}/${country.isoAlpha2}.svg`;
      }
      return entry;
    });
  }

  joinIfPresent(data, 'regions', extended.regionNames);
  joinIfPresent(data, 'crps', extended.crps);
  joinIfPresent(data, 'crpOutcomes', extended.crpOutcomes);
  joinIfPresent(data, 'projectOutcomes', extended.projectOutcomes);
  joinIfPresent(data, 'activities', extended.activities);

  const fundingSourcesList = buildFundingSourcesList(extended);
  if (fundingSourcesList.length > 0) {
    data.hasFundingSources = true;
    data.fundingSourcesList = fundingSourcesList;
    data.fundingSources = buildFundingSourcesText(fundingSourcesList);
  }

  if (extended.contacts.length > 0) {
    data.contacts = extended.contacts.map((contact) => sanitizeText(contact)).filter(Boolean).join('; ');
  }

  return data;
}

function buildFundingSourcesList(
  extended: ClusterDeliverableExtendedContext,
): Array<Record<string, unknown>> {
  const locationsByFundingId = new Map<number, string[]>();
  for (const location of extended.fundingLocations) {
    const names = locationsByFundingId.get(location.fundingSourceId) ?? [];
    if (location.name && !names.includes(location.name)) {
      names.push(location.name);
    }
    locationsByFundingId.set(location.fundingSourceId, names);
  }

  return extended.fundingSources.map((source) => mapFundingSource(source, locationsByFundingId));
}

function mapFundingSource(
  source: ClusterDeliverableFundingSourceRow,
  locationsByFundingId: Map<number, string[]>,
): Record<string, unknown> {
  const entry: Record<string, unknown> = { id: source.fundingSourceId };
  const title = sanitizeText(source.title);
  if (title) {
    entry.name = title;
    entry.title = title;
  } else {
    const fallback = sanitizeText(source.composedName);
    if (fallback) {
      entry.name = fallback;
      entry.title = fallback;
    }
  }
  putIfPresent(entry, 'financeCode', sanitizeText(source.financeCode));
  putIfPresent(entry, 'description', sanitizeText(source.description));

  const scopeParts: string[] = [];
  if (source.isGlobal === 1) {
    scopeParts.push('Global');
  }
  const locations = locationsByFundingId.get(source.fundingSourceId) ?? [];
  for (const location of locations) {
    const sanitized = sanitizeText(location);
    if (sanitized && !scopeParts.includes(sanitized)) {
      scopeParts.push(sanitized);
    }
  }
  if (scopeParts.length > 0) {
    entry.geographicScope = scopeParts.join(', ');
  }

  return entry;
}

function buildFundingSourcesText(fundingSourcesList: Array<Record<string, unknown>>): string {
  const lines = fundingSourcesList.map((source) => {
    let line = '● ';
    if (source.id != null) {
      line += `FS${source.id}`;
    }
    const financeCode = source.financeCode;
    if (typeof financeCode === 'string' && financeCode.trim() !== '') {
      line += ` (${financeCode})`;
    }
    line += ' - ';
    const title = source.title ?? source.name;
    if (typeof title === 'string') {
      line += title;
    }
    return `${line}<br>`;
  });
  return lines.join('').replace(/<br>$/, '');
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
