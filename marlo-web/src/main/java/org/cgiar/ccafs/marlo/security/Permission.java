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

package org.cgiar.ccafs.marlo.security;


/**
 * This class have the statics texts of the names and
 * paths of the privileges that have a user.
 * 
 * @author Hernán David Carvajal
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class Permission {

  public static final String FULL_PRIVILEGES = "*"; // path full privileges
  public static final String CRP_ADMIN_VISIBLE_PRIVILEGES = "crp:{0}:admin:canAcess"; // path full privileges
  public static final String CRP_ADMIN_EDIT_PRIVILEGES = "crp:{0}:admin:*"; // path full privileges
  public static final String CRP_ADMIN_BASE_PERMISSION = "crp:{0}:admin";
  public static final String IMPACT_PATHWAY_BASE_PERMISSION = "crp:{0}:impactPathway:{1}";
  public static final String IMPACT_PATHWAY_EDIT_PRIVILEGES = "crp:{0}:impactPathway:{1}:canEdit";
  public static final String IMPACT_PATHWAY_VISIBLE_PRIVILEGES = "crp:{0}:impactPathway:canAcess";

  public static final String IMPACT_PATHWAY_SUBMISSION_PERMISSION = "crp:{0}:impactPathway:{1}:submit";
  public static final String IMPACT_PATHWAY_UNSUBMISSION_PERMISSION = "crp:{0}:impactPathway:{1}:unsubmitted";
  public static final String PROJECT_DESCRIPTION_BASE_PERMISSION = "crp:{0}:project:{1}:description";
  public static final String PROJECT_LIST_BASE_PERMISSION = "crp:{0}:project:projectsList";
  public static final String PROJECT_CONTRIBRUTIONCRP_BASE_PERMISSION = "crp:{0}:project:{1}:contributionCrp";
  public static final String PROJECT_DESCRIPTION_PERMISSION = "crp:{0}:project:{1}:description:*";
  public static final String PROJECT_PARTNER_BASE_PERMISSION = "crp:{0}:project:{1}:partners";
  public static final String PROJECT_BUDGET_BASE_PERMISSION = "crp:{0}:project:{1}:budgetByPartners";
  public static final String PROJECT_BUDGET_CLUSTER_BASE_PERMISSION = "crp:{0}:project:{1}:budgetByCoAs";
  public static final String PROJECT_MANAGE_BASE_PERMISSION = "crp:{0}:project:{1}:manage";
  public static final String PROJECT_SUBMISSION_PERMISSION = "crp:{0}:project:{1}:manage:submitProject";
  public static final String PROJECT_UNSUBMISSION_PERMISSION = "crp:{0}:project:{1}:unsubmitted";
  public static final String PROJECT_LOCATION_BASE_PERMISSION = "crp:{0}:project:{1}:locations";
  public static final String PROJECT_DELETE_BASE_PERMISSION = "crp:{0}:project:{1}:deleteProject";
  public static final String PROJECT_CCFASOUTCOME_BASE_PERMISSION = "crp:{0}:project:{1}:ccafsOutcomes";
  public static final String SYNTHESIS_BY_MOG_BASE_PERMISSION = "crp:{0}:synthesisProgram:{1}";
  public static final String SYNTHESIS_BY_MOG_PERMISSION = "crp:{0}:synthesisProgram:{1}:*";
  public static final String POWB_SYNTHESIS_CAN_VIEW = "crp:{0}:powbSynthesis:manage:canAcess";
  public static final String POWB_SYNTHESIS_RPL_EFFORT = "crp:{0}:powbSynthesis:{1}:collaboration{2}:effort";


  public static final String PROJECT_BUDGET_FLAGSHIP_BASE_PERMISSION = "crp:{0}:project:{1}:budgetByFlagship";
  public static final String POWB_SYNTHESIS_TOC_BASE_PERMISSION = "crp:{0}:powbSynthesis:{1}:tocAdjustments";
  public static final String POWB_SYNTHESIS_FLAGSHIPPLANS_BASE_PERMISSION = "crp:{0}:powbSynthesis:{1}:flagshipPlans";
  public static final String POWB_SYNTHESIS_PERMISSION = "crp:{0}:powbSynthesis:{1}:canEdit";
  public static final String POWB_SYNTHESIS_EVIDENCES_BASE_PERMISSION = "crp:{0}:powbSynthesis:{1}:evidences";
  public static final String POWB_SYNTHESIS_EXPECTED_BASE_PERMISSION = "crp:{0}:powbSynthesis:{1}:expectedProgress";
  public static final String POWB_SYNTHESIS_MEL_BASE_PERMISSION =
    "crp:{0}:powbSynthesis:{1}:monitoringEvaluationLearning";
  public static final String POWB_SYNTHESIS_COLLABORATION_BASE_PERMISSION = "crp:{0}:powbSynthesis:{1}:collaboration";
  public static final String POWB_SYNTHESIS_CROSS_CUTING_BASE_PERMISSION = "crp:{0}:powbSynthesis:{1}:crossCuting";

  public static final String POWB_SYNTHESIS_COLLABORATION_CAN_EDIT_PERMISSION =
    "crp:{0}:powbSynthesis:{1}:collaboration:canEdit";


  public static final String POWB_SYNTHESIS_CRPSTAFFING_BASE_PERMISSION = "crp:{0}:powbSynthesis:{1}:crpStaffing";
  public static final String POWB_SYNTHESIS_FINANCIAL_PLAN_BASE_PERMISSION = "crp:{0}:powbSynthesis:{1}:financialPlan";
  public static final String POWB_SYNTHESIS_MANAGEMENT_RISK_BASE_PERMISSION =
    "crp:{0}:powbSynthesis:{1}:managementRisk";
  public static final String POWB_SYNTHESIS_MANAGEMENT_GOVERNANCE_BASE_PERMISSION =
    "crp:{0}:powbSynthesis:{1}:managementGovernance";
  public static final String POWB_SYNTHESIS_MANAGE_PERMISSION = "crp:{0}:powbSynthesis:{1}:manage";
  public static final String POWB_SYNTHESIS_SUBMISSION_PERMISSION = "crp:{0}:powbSynthesis:{1}:manage:canSubmmit";

  public static final String CRP_INDICATORS_BASE_PERMISSION = "crp:{0}:crpIndicators:{1}";
  public static final String CRP_INDICATORS_PERMISSION = "crp:{0}:crpIndicators:{1}:*";


  public static final String PROJECT__PERMISSION = "crp:{0}:project:{1}:{2}:canEdit";


  public static final String PROJECT__SWITCH = "crp:{0}:project:{1}:projectSwitch";
  public static final String PROJECT_CONTRIBRUTIONCRP_EDIT_PERMISSION = "crp:{0}:project:{1}:contributionCrp:canEdit";
  public static final String PROJECT_DELIVERABLE_LIST_BASE_PERMISSION = "crp:{0}:project:{1}:deliverableList";
  public static final String PROJECT_DELIVERABLE_LIST_EDIT_PERMISSION = "crp:{0}:project:{1}:deliverableList:canEdit";
  public static final String PROJECT_CCFASOUTCOME_LIST_EDIT_PERMISSION = "crp:{0}:project:{1}:ccafsOutcomes:canEdit";

  public static final String PROJECT_DELIVERABLE_LIST_ADD_PERMISSION =
    "crp:{0}:project:{1}:deliverableList:addDeliverable";
  public static final String PROJECT_DELIVERABLE_LIST_REMOVE_PERMISSION =
    "crp:{0}:project:{1}:deliverableList:removeOldDeliverables";
  public static final String PROJECT_DELIVERABLE_BASE_PERMISSION = "crp:{0}:project:{1}:deliverable";

  public static final String PROJECT_LEVERAGES_BASE_PERMISSION = "crp:{0}:project:{1}:leverages";
  public static final String PROJECT_HIGHLIGHT_BASE_PERMISSION = "crp:{0}:project:{1}:highlights";
  public static final String PROJECT_CASE_STUDY_BASE_PERMISSION = "crp:{0}:project:{1}:caseStudies";

  public static final String PROJECT_CASE_STUDY_PERMISSION = "crp:{0}:project:{1}:caseStudies:*";

  public static final String PROJECT_OVERVIEWS_BASE_PERMISSION = "crp:{0}:project:{1}:outputs";
  public static final String PROJECT_OTHER_CONTRIBRUTIONS_BASE_PERMISSION = "crp:{0}:project:{1}:otherContributions";
  public static final String PROJECT_OUTCOMES_PANDR_BASE_PERMISSION = "crp:{0}:project:{1}:outcomesPandR";

  public static final String PROJECT_DELIVERABLE_EDIT_PERMISSION = "crp:{0}:project:{1}:deliverable:canEdit";
  public static final String PROJECT_HIGH_LIGHTS_EDIT_PERMISSION = "crp:{0}:project:{1}:highlights:canEdit";
  public static final String PROJECT_CASE_STUDY_EDIT_PERMISSION = "crp:{0}:project:{1}:caseStudies:canEdit";
  public static final String PROJECT_ACTIVITIES_BASE_PERMISSION = "crp:{0}:project:{1}:activities";
  public static final String PROJECT_EXPECTED_STUDIES_BASE_PERMISSION = "crp:{0}:project:{1}:expectedStudies";

  public static final String PROJECT_INNOVATIONS_BASE_PERMISSION = "crp:{0}:project:{1}:innovations";
  public static final String PROJECT_INNOVATIONS_EDIT_PERMISSION = "crp:{0}:project:{1}:innovations:canEdit";

  public static final String PROJECT_FUNDING_SOURCE_BASE_PERMISSION = "crp:{0}:fundingSource:{1}:canEdit";
  public static final String PROJECT_FUNDING_SOURCE_BUDGET_PERMISSION = "crp:{0}:fundingSource:{1}:budget";
  public static final String PROJECT_FUNDING_SOURCE_ADD_BUDGET_PERMISSION = "crp:{0}:fundingSource:budget";


  public static final String PUBLICATION_ADD = "crp:{0}:publication:add";
  public static final String PUBLICATION_INSTITUTION = "crp:{0}:publication:{1}:*";
  public static final String PUBLICATION_FULL_PERMISSION = "crp:{0}:publication:*";
  public static final String PUBLICATION_BASE_INSTITUTION = "crp:{0}:publication:{1}";
  public static final String PUBLICATION_BASE_FULL_PERMISSION = "crp:{0}:publication";

  public static final String PROJECT_FUNDING_ = "crp:{0}:fundingSource:canEdit";
  public static final String PROJECT_FUNDING_W1_BASE_PERMISSION = "crp:{0}:fundingSource:w1";
  public static final String PROJECT_FUNDING_W3_BASE_PERMISSION = "crp:{0}:fundingSource:w3";
  public static final String FUNDING_SOURCE_EDIT_PERMISSION = "crp:{0}:fundingSource:canEdit";


  public static final String PROJECT_FUNDING_W3_PROJECT_BASE_PERMISSION = "crp:{0}:project:{1}:fundingSource:w3";
  public static final String PROJECT_GENDER_PROJECT_BASE_PERMISSION = "crp:{0}:project:{1}:fundingSource:gender";

  public static final String PROJECT_FUNDING_W3_PROJECT_INSTITUTION_BASE_PERMISSION =
    "crp:{0}:project:{1}:fundingSource:{2}:w3";


  // Create Projects Permissions
  public static final String PROJECT_CORE_ADD = "crp:{0}:project:projectsList:addCoreProject";
  public static final String PROJECT_BILATERAL_ADD = "crp:{0}:project:projectsList:addBilateralProject";

  /**
   * ---------------------------------------------------------------------------------------------------------
   * *********************************************************************************************************
   * CENTER PERMISSIONS
   * *********************************************************************************************************
   * ----------------------------------------------------------------------------------------------------------
   */

  public static final String RESEARCH_AREA_FULL_PRIVILEGES = "center:{0}:area:{1}:*";
  public static final String RESEARCH_PROGRAM_FULL_PRIVILEGES = "center:{0}:area:{1}:program:{2}:*";
  public static final String RESEARCH_AREA_BASE_PERMISSION = "center:{0}:area:{1}";
  public static final String RESEARCH_PROGRAM_BASE_PERMISSION = "center:{0}:area:{1}:program:{2}";
  public static final String CENTER_ADMIN_BASE_PERMISSION = "center:{0}:admin:";


  public static final String RESEARCH_PROGRAM_SUBMISSION_PERMISSION = "center:{0}:area:{1}:program:{2}:submitIP";

  // Monitoring
  public static final String PROJECT_BASE_PERMISSION = "center:{0}:area:{1}:program:{2}:project:{3}";
  public static final String CENTER_PROJECT_DESCRIPTION_BASE_PERMISSION =
    "center:{0}:area:{1}:program:{2}:project:{3}:projectDescription";
  public static final String CENTER_PROJECT_PARTNERS_BASE_PERMISSION =
    "center:{0}:area:{1}:program:{2}:project:{3}:projectPartners";
  public static final String CENTER_PROJECT_DEIVERABLE_BASE_PERMISSION =
    "center:{0}:area:{1}:program:{2}:project:{3}:deliverable:{4}";
  public static final String CENTER_PROJECT_SUBMISSION_PERMISSION =
    "center:{0}:area:{1}:program:{2}:project:{3}:submitProject";

  /**
   * REST API Permissions
   */
  public static final String FULL_REST_API_PERMISSION = "api:*";

  public static final String FULL_READ_REST_API_PERMISSION = "api:*:read";
  public static final String FULL_CREATE_REST_API_PERMISSION = "api:*:create";
  public static final String FULL_UPDATE_REST_API_PERMISSION = "api:*:update";
  public static final String FULL_DELETE_REST_API_PERMISSION = "api:*:delete";

  // Institutions
  public static final String INSTITUTIONS_READ_REST_API_PERMISSION = "api:institutions:read";
  public static final String INSTITUTIONS_CREATE_REST_API_PERMISSION = "api:institutions:create";
  public static final String INSTITUTIONS_UPDATE_REST_API_PERMISSION = "api:institutions:update";
  public static final String INSTITUTIONS_DELETE_REST_API_PERMISSION = "api:institutions:delete";
  public static final String INSTITUTIONS_FULL_REST_API_PERMISSION = "api:institutions:*";

  // Crps
  public static final String CRPS_READ_REST_API_PERMISSION = "api:crps:read";
  public static final String CRPS_CREATE_REST_API_PERMISSION = "api:crps:create";
  public static final String CRPS_UPDATE_REST_API_PERMISSION = "api:crps:update";
  public static final String CRPS_DELETE_REST_API_PERMISSION = "api:crps:delete";
  public static final String CRPS_FULL_REST_API_PERMISSION = "api:crps:*";
}
