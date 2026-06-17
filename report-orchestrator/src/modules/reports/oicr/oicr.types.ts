export interface OicrStudyRow {
  id: number;
  year: number | null;
  title: string | null;
  commissioningStudy: string | null;
  status: string | null;
  type: string | null;
  outcomeImpactStatement: string | null;
  topLevelComments: string | null;
  scopeComments: string | null;
  allianceOicr: string | null;
  stageStudy: string | null;
}

export interface OicrStudyData extends Record<string, unknown> {
  id: number;
  year: number | null;
  title: string | null;
  commissioningStudy: string | null;
  status: string | null;
  type: string | null;
  outcomeImpactStatement: string | null;
  topLevelComments: string | null;
  scopeComments: string | null;
  allianceOICRID: string | null;
  stageStudy: string | null;
  timeCreation: string;
}
