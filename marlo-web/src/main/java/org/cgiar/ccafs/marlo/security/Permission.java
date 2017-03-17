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
  public static final String IMPACT_PATHWAY_EDIT_PRIVILEGES = "crp:{0}:impactPathway:{1}:*";
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
  public static final String PROJECT_FUNDING_W3_PROJECT_INSTITUTION_BASE_PERMISSION =
    "crp:{0}:project:{1}:fundingSource:{2}:w3";


  // Create Projects Permissions
  public static final String PROJECT_CORE_ADD = "crp:{0}:project:projectsList:addCoreProject";
  public static final String PROJECT_BILATERAL_ADD = "crp:{0}:project:projectsList:addBilateralProject";


}
