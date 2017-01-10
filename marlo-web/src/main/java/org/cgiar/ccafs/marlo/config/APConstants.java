/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.config;

/**
 * All Constants should be here.
 * 
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Christian Garcia.
 * @author Héctor Fabio Tobón R.
 */
public final class APConstants {

  // end year
  public static Integer END_YEAR = 2022;

  public static final String CUSTOM_LAGUAGE = "en";
  public static final String PATH_CUSTOM_FILES = "custom/";
  public static final String CUSTOM_FILE = "global";
  public static final int ELEMENT_RELATION_CONTRIBUTION = 1;
  public static final int ELEMENT_RELATION_TRANSLATION = 2;
  // Session variables
  public static final String SESSION_USER = "current_user";
  public static final String SESSION_CRP = "current_crp";
  public static final String USER_TOKEN = "user_token";
  public static final int GLOBAL_PROGRAM = 11;
  public static final int ELEMENT_TYPE_OUTCOME2019 = 3;
  // Crp Parameters
  public static final String CRP_PARAMETERS = "crp_parameters";
  public static final String CRP_LANGUAGE = "crp_language";
  public static final String CRP_CUSTOM_FILE = "crp_custom_file";
  public static final String CRP_HAS_REGIONS = "crp_has_regions";
  public static final String CRP_PMU_ROLE = "crp_pmu_rol";
  public static final String CRP_CL_ROLE = "crp_cl_rol";
  public static final String CRP_FPL_ROLE = "crp_fpl_rol";
  public static final String CRP_PL_ROLE = "crp_pl_rol";
  public static final String CRP_PC_ROLE = "crp_pc_rol";
  public static final String CRP_RPL_ROLE = "crp_rpl_rol";
  public static final String CRP_SL_ROLE = "crp_sl_rol";
  public static final String CRP_PLANNING_YEAR = "crp_planning_year";
  public static final String CRP_PLANNING_ACTIVE = "crp_planning_active";
  public static final String CRP_REPORTING_YEAR = "crp_reporting_year";
  public static final String CRP_REPORTING_ACTIVE = "crp_reporting_active";
  public static final String CRP_LESSONS_ACTIVE = "crp_lessons_active";
  public static final String CRP_OPEN_PLANNING_DATE = "crp_open_planing_date";
  public static final String CRP_OPEN_REPORTING_DATE = "crp_open_reporting_date";
  public static final String CRP_CU = "crp_cu";
  public static final int MID_OUTCOME_YEAR = 2019;
  public static final String CRP_PROGRAM_ID = "crpProgramID";
  public static final String LIASON_INSTITUTION_ID = "liasonInstitutionID";
  public static final String CRP_ID = "crpID";
  public static final String TRANSACTION_ID = "transactionId";
  public static final String FILTER_BY = "filterBy";
  public static final String CRP_ADMIN_ACTIVE = "crp_admin_active";
  public static final String CRP_IMPACT_PATHWAY_ACTIVE = "crp_impPath_active";
  public static final String SECTION_NAME = "sectionName";
  public static final String IDO_ID = "idoID";
  public static final String CRP_CLUSTER_ACTIVITY_ID = "clusterActivityID";
  public static final String ELEMENT_TYPE_ID = "parentId";
  public static final String DELIVERABLE_TYPE_ID = "deliverableTypeId";
  public static final String DELIVERABLE_ID = "deliverableId";
  public static final String CRP_ADMIN_ROLE = "crp_admin_rol";

  // Query parameter
  public static final String QUERY_PARAMETER = "q";

  // Outlook institutional email
  public static final String OUTLOOK_EMAIL = "cgiar.org";

  // Types of project
  public static final String PROJECT_CORE = "W1W2";
  public static final String PROJECT_CCAFS_COFUNDED = "COFUNDED";
  public static final String PROJECT_BILATERAL = "BILATERAL";
  // public static final String PROJECT_CORE_TYPE = "CORE";


  public static final String PROJECT_SCAPE_NATIONAL = "1";
  public static final String PROJECT_SCAPE_REGIONAL = "2";
  public static final String PROJECT_SCAPE_GLOBAL = "3";
  public static final String PLANNING = "Planning";
  public static final String REPORTING = "Reporting";
  // Request variables
  public static final String EDITABLE_REQUEST = "edit";
  public static final String CRP_REQUEST = "crp";
  public static final String PROJECT_REQUEST_ID = "projectID";
  public static final String JUSTIFICATION_REQUEST = "justification";
  public static final String FUNDING_SOURCE_REQUEST_ID = "fundingSourceID";
  public static final String YEAR_REQUEST = "year";
  public static final String MILESTONE_REQUEST_ID = "milestoneID";
  public static final String PROJECT_OUTCOME_REQUEST_ID = "projectOutcomeID";
  public static final String OUTCOME_REQUEST_ID = "outcomeID";
  public static final String AUTOSAVE_REQUEST = "autoSave";
  public static final String COUNTRY_REQUEST_ID = "countryID";
  public static final String INSTITUTION_TYPE_REQUEST_ID = "institutionTypeID";
  public static final String INSTITUTION_REQUEST_ID = "institutionID";
  public static final String PROJECT_DELIVERABLE_REQUEST_ID = "deliverableID";
  public static final String LOC_ELEMENT_ID = "locElementID";
  public static final String HIGHLIGHT_REQUEST_ID = "highlightID";
  public static final String CASE_STUDY_REQUEST_ID = "caseStudyID";
  public static final String CYCLE = "cycle";

  public static final String ID = "id";
  public static final String CLASS_NAME = "className";

  // Types of Project Partners
  public static final String PROJECT_PARTNER_PL = "PL";
  public static final String PROJECT_PARTNER_PC = "PC";
  public static final String PROJECT_PARTNER_CP = "CP";

  // login messages and status
  public static final String LOGIN_STATUS = "loginStatus";
  public static final String LOGIN_MESSAGE = "loginMessage";

  public static final String LOGON_SUCCES = "LOGON_SUCCES";
  public static final String ERROR_NO_SUCH_USER = "ERROR_NO_SUCH_USER";
  public static final String ERROR_LOGON_FAILURE = "ERROR_LOGON_FAILURE";
  public static final String ERROR_INVALID_LOGON_HOURS = "ERROR_INVALID_LOGON_HOURS";
  public static final String ERROR_PASSWORD_EXPIRED = "ERROR_PASSWORD_EXPIRED";
  public static final String ERROR_ACCOUNT_DISABLED = "ERROR_ACCOUNT_DISABLED";
  public static final String ERROR_ACCOUNT_EXPIRED = "ERROR_ACCOUNT_EXPIRED";
  public static final String ERROR_ACCOUNT_LOCKED_OUT = "ERROR_ACCOUNT_LOCKED_OUT";
  public static final String ERROR_LDAP_CONNECTION = "ERROR_LDAP_CONNECTION";
  public static final String USER_DISABLED = "USER_DISABLED";
  public static final String SCOPE_TYPE = "Region";

  // new Target unit
  public static final String TARGET_UNIT_NAME = "targetUnitName";
  public static final long TARGET_UNIT_OTHER_ID = 18;

  // project Locations Constants
  public static final String MODEL_CLASS_PARAMETER = "modelClass";
  public static final String LOCATION_PARENT_ID_PARAMETER = "parentId";

  // Date Formats
  public static final String DATE_FORMAT = "yyyy-MM-dd";

  // Institutions Types
  public static final long INSTITUTION_DONOR_TYPE = 5;

  // Relations Name
  public static final String PROGRAM_OUTCOMES_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.CrpProgram.crpProgramOutcomes)";


  public static final String PROGRAM_ACTIVITIES_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.CrpProgram.crpClusterOfActivities)";


  public static final String FUNDING_SOURCES_BUDGETS_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.FundingSource.fundingSourceBudgets)";

  public static final String FUNDING_SOURCES_INSTITUTIONS_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.FundingSource.fundingInstitutions)";
  public static final String PROJECT_FOCUSES_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.Project.projectFocuses)";

  public static final String PROJECT_BUDGETS_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.Project.projectBudgets)";

  public static final String PROJECT_BUDGETS_ACTVITIES_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.Project.projectBudgetsCluserActvities)";

  public static final String PROJECT_OUTCOMES_MILESTONE_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.ProjectOutcome.projectMilestones)";
  public static final String PROJECT_OUTCOMES_COMMUNICATION_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.ProjectOutcome.projectCommunications)";

  public static final String PROJECT_NEXT_USERS_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.ProjectOutcome.projectNextusers)";

  public static final String PROJECT_CLUSTER_ACTIVITIES_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.Project.projectClusterActivities)";

  public static final String PROJECT_LOCATIONS_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.Project.projectLocations)";

  public static final String PROJECT_DELIVERABLES_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.Project.deliverables)";

  public static final String PROJECT_DELIVERABLE_PARTNERSHIPS_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.Deliverable.deliverablePartnerships)";

  public static final String PROJECT_PROJECT_HIGHLIGTH_TYPE_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.ProjectHighlight.projectHighligthsTypes)";

  public static final String PROJECT_CASE_STUDIES_PROJECTS_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.CaseStudy.caseStudyProjects)";


  public static final String PROJECT_PROJECT_HIGHLIGTH_COUNTRY_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.ProjectHighlight.projectHighligthCountries)";


  public static final String PROJECT_DELIVERABLE_FUNDING_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.Deliverable.deliverableFundingSources)";

  public static final String PROJECT_DELIVERABLE_QUALITY_CHECK =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.Deliverable.deliverableQualityChecks)";

  public static final String PROJECT_ACTIVITIES_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.Project.activities)";
  public static final String PROJECT_LEVERAGES_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.Project.projectLeverages)";


  public static final String PROJECT_OVERVIEWS_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.Project.ipProjectContributionOverviews)";

  public static final String PROJECT_OUTCOMES_PANDR_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.Project.projectOutcomesPandr)";

  public static final String PROJECT_CCFASOTUCOME_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.Project.ipProjectIndicators)";

  public static final String PROJECT_SCOPES_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.Project.projectScopes)";

  public static final String PROJECT_PARTNERS_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.Project.projectPartners)";

  public static final String PROJECT_LESSONS_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.Project.projectComponentLessons)";

  public static final String PROJECT_OUTCOME_LESSONS_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.ProjectOutcome.projectComponentLessons)";

  public static String getFilterBy() {
    return FILTER_BY;
  }


}

