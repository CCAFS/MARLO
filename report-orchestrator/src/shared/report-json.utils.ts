export function removeLeadingSemicolon(value: string | null | undefined): string | null {
  if (value == null || value === '') {
    return null;
  }
  return value.replace(/^;\s*/, '');
}

export function booleanToString(value: boolean | null | undefined): string | null {
  if (value == null) {
    return null;
  }
  return value ? 'true' : 'false';
}

export function yesNo(value: boolean | null | undefined): string {
  return value ? 'Yes' : 'No';
}

export function toJsonString(value: unknown): string | null {
  if (value == null) {
    return null;
  }
  if (Array.isArray(value) && value.length === 0) {
    return '[]';
  }
  return JSON.stringify(value);
}

/** Mirrors ProjectInnovationSummaryAction.normalizeJson() — empty arrays become null. */
export function normalizeJsonField(json: string | null | undefined): string | null {
  if (json == null || json === '[]' || json === '') {
    return null;
  }
  return json;
}

export function joinPrefixed(items: string[], prefix: string, separator: string): string | null {
  if (items.length === 0) {
    return null;
  }
  return items.map((item) => `${prefix}${item}`).join(separator);
}

function getDayOrdinal(day: number): string {
  if (day >= 11 && day <= 13) {
    return 'th';
  }
  switch (day % 10) {
    case 1:
      return 'st';
    case 2:
      return 'nd';
    case 3:
      return 'rd';
    default:
      return 'th';
  }
}

/** Mirrors ProjectInnovationSummaryAction.getCurrentDatev2(). */
/** Mirrors ReportingSummaryAction.transformSdgIconName(). */
export function transformSdgIconName(dbIconName: string | null | undefined): string | null {
  if (dbIconName == null || dbIconName.trim() === '') {
    return dbIconName ?? null;
  }

  const hasPngExtension = dbIconName.toLowerCase().endsWith('.png');
  let baseName = hasPngExtension ? dbIconName.substring(0, dbIconName.length - 4) : dbIconName;
  baseName = baseName.replace('_SDG-goals_Goal-', '-WEB-Goal-');
  baseName = baseName.replace(/-Goal-0(\d)$/, '-Goal-$1');
  return hasPngExtension ? `${baseName}.png` : baseName;
}

export function formatInnovationTimeCreation(): string {
  const formatter = new Intl.DateTimeFormat('en-US', {
    weekday: 'long',
    month: 'long',
    day: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
    timeZone: 'Europe/Paris',
  });
  const formatted = formatter.format(new Date());
  const day = Number.parseInt(
    new Intl.DateTimeFormat('en-US', { day: 'numeric', timeZone: 'Europe/Paris' }).format(new Date()),
    10,
  );
  const ordinal = getDayOrdinal(day);
  return formatted.replace(new RegExp(`\\b${day}\\b`), `${day}${ordinal}`);
}
