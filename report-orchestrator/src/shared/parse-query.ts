export function parsePositiveInt(value: unknown, field: string): number {
  const parsed = Number.parseInt(String(value), 10);
  if (Number.isNaN(parsed) || parsed <= 0) {
    throw new Error(`Invalid ${field}: must be a positive integer`);
  }
  return parsed;
}

export function parseOptionalPositiveInt(value: unknown, field: string): number | undefined {
  if (value === undefined || value === null || value === '') {
    return undefined;
  }
  return parsePositiveInt(value, field);
}
