/** Strip HTML tags and collapse whitespace — mirrors ReportingSummaryAction.getSanitizedText(). */
export function sanitizeText(value: string | null | undefined): string | null {
  if (value == null) {
    return null;
  }
  const cleaned = value
    .replace(/<[^>]*>/g, ' ')
    .replace(/&nbsp;/gi, ' ')
    .replace(/\s+/g, ' ')
    .trim();
  return cleaned.length > 0 ? cleaned : null;
}

export function formatMonthYear(value: Date | string | null | undefined): string | null {
  if (value == null) {
    return null;
  }
  const date = value instanceof Date ? value : new Date(value);
  if (Number.isNaN(date.getTime())) {
    return null;
  }
  return date.toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
}

export function formatCoordinate(value: number | null | undefined): string | null {
  if (value == null) {
    return null;
  }
  return value.toFixed(4);
}

export function formatNumericValue(value: number | string | null | undefined): string | null {
  if (value == null || value === '') {
    return null;
  }
  const numeric = typeof value === 'number' ? value : Number.parseFloat(String(value));
  if (Number.isNaN(numeric)) {
    return String(value);
  }
  return Number.isInteger(numeric) ? String(numeric) : numeric.toFixed(2).replace(/\.?0+$/, '');
}

export function mysqlBool(value: number | null | undefined): boolean {
  return value === 1;
}

export function buildProgramSummary(programs: Array<Record<string, unknown>>, limit = 4): string | null {
  const names = programs
    .map((program) => {
      const composed = program.composedName;
      if (typeof composed === 'string' && composed.trim()) {
        return composed.trim();
      }
      const name = program.name;
      if (typeof name === 'string' && name.trim()) {
        return name.trim();
      }
      const acronym = program.acronym;
      if (typeof acronym === 'string' && acronym.trim()) {
        return acronym.trim();
      }
      return null;
    })
    .filter((value): value is string => value != null);

  if (names.length === 0) {
    return null;
  }
  const summary = names.slice(0, limit).join(', ');
  return names.length > limit ? `${summary} +${names.length - limit} more` : summary;
}

export function buildClusterActivitiesSummary(
  activities: Array<Record<string, unknown>>,
  limit = 4,
): string | null {
  const names = activities
    .map((activity) => {
      const name = activity.name ?? activity.identifier;
      return typeof name === 'string' && name.trim() ? name.trim() : null;
    })
    .filter((value): value is string => value != null);

  if (names.length === 0) {
    return null;
  }
  const summary = names.slice(0, limit).join(', ');
  return names.length > limit ? `${summary} +${names.length - limit} more` : summary;
}

export function buildLocationsSummary(
  locations: Array<Record<string, unknown>>,
  limit = 3,
): string | null {
  const names = locations
    .map((location) => {
      const name = location.name ?? location.country;
      return typeof name === 'string' && name.trim() ? name.trim() : null;
    })
    .filter((value): value is string => value != null);

  if (names.length === 0) {
    return null;
  }
  const summary = names.slice(0, limit).join(', ');
  return names.length > limit ? `${summary} +${names.length - limit} more` : summary;
}
