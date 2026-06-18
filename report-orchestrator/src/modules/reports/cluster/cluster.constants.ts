import { CRP_PROGRAM_TYPE_FLAGSHIP, CRP_PROGRAM_TYPE_REGIONAL } from '../oicr/oicr.constants';

export { CRP_PROGRAM_TYPE_FLAGSHIP, CRP_PROGRAM_TYPE_REGIONAL };

/** Mirrors ProjectStatusEnum status ids used in deliverable year filtering. */
export const DELIVERABLE_STATUS = {
  ONGOING: 2,
  COMPLETE: 3,
  EXTENDED: 4,
  CANCELLED: 5,
} as const;

/** Mirrors ProjectStatusEnum labels for projects and activities. */
export const PROJECT_STATUS_LABELS: Record<number, string> = {
  2: 'On-going',
  3: 'Complete',
  4: 'Extended',
  5: 'Cancelled',
};

export const CLUSTER_PARTNER_ROLE_LABELS: Record<string, string> = {
  PL: 'Cluster leader',
  PC: 'Cluster Coordinator',
  CP: 'Cluster Collaborator',
};

export const FLAG_ASSET_BASE_URL =
  'https://marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com/flags';

export const SDG_ASSET_BASE_URL =
  'https://marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com/sdg';

/** Metadata element ids shown separately from metadataElements[]. */
export const METADATA_ELEMENT_ID_HANDLE = 35;
export const METADATA_ELEMENT_ID_DOI = 36;

export const LOC_ELEMENT_TYPE_REGION = 1;
export const LOC_ELEMENT_TYPE_COUNTRY = 2;

export const LIAISON_INSTITUTION_LABEL = 'Liaison institution';
export const LIAISON_CONTACT_LABEL = 'Liaison contact';
