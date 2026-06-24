import { DELIVERABLE_STATUS } from './cluster.constants';
import { ClusterDeliverableSummaryRow } from './cluster.types';

/** Mirrors ReportingSummaryAction.buildDeliverablesList() year/status filter. */
export function deliverableMatchesSelectedYear(
  row: ClusterDeliverableSummaryRow,
  selectedYear: number,
): boolean {
  const status = row.status;
  const year = row.year;
  const newExpectedYear = row.newExpectedYear;

  if (status == null) {
    return year === selectedYear;
  }

  if (status === DELIVERABLE_STATUS.EXTENDED) {
    if (newExpectedYear != null && newExpectedYear !== -1) {
      return newExpectedYear === selectedYear;
    }
    return year === selectedYear;
  }

  if (status === DELIVERABLE_STATUS.ONGOING) {
    return year === selectedYear;
  }

  if (status === DELIVERABLE_STATUS.COMPLETE) {
    if (newExpectedYear != null && newExpectedYear !== -1) {
      return newExpectedYear === selectedYear;
    }
    return year === selectedYear;
  }

  return false;
}

export function resolveDisplayYear(row: ClusterDeliverableSummaryRow): number {
  const status = row.status;
  const newExpectedYear = row.newExpectedYear;
  if (
    status != null
    && (status === DELIVERABLE_STATUS.EXTENDED
      || status === DELIVERABLE_STATUS.ONGOING
      || status === DELIVERABLE_STATUS.COMPLETE)
    && newExpectedYear != null
    && newExpectedYear !== -1
  ) {
    return newExpectedYear;
  }
  return row.year;
}
