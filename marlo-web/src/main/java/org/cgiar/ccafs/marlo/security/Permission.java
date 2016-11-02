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
  public static final String PROJECT_DESCRIPTION_BASE_PERMISSION = "crp:{0}:project:{1}:description";
  public static final String PROJECT_LIST_BASE_PERMISSION = "crp:{0}:project:projectsList";
  public static final String PROJECT_CONTRIBRUTIONCRP_BASE_PERMISSION = "crp:{0}:project:{1}:contributionCrp";
  public static final String PROJECT_DESCRIPTION_PERMISSION = "crp:{0}:project:{1}:description:*";
  public static final String PROJECT_PARTNER_BASE_PERMISSION = "crp:{0}:project:{1}:partners";
  public static final String PROJECT_BUDGET_BASE_PERMISSION = "crp:{0}:project:{1}:budgetByPartners";
  public static final String PROJECT_BUDGET_CLUSTER_BASE_PERMISSION = "crp:{0}:project:{1}:budgetByCoAs";
  public static final String PROJECT_MANAGE_BASE_PERMISSION = "crp:{0}:project:{1}:manage";
  public static final String PROJECT_SUBMISSION_PERMISSION = "crp:{0}:project:{1}:manage:submitProject";
  public static final String PROJECT_LOCATION_BASE_PERMISSION = "crp:{0}:project:{1}:location";
  public static final String PROJECT_DELETE_BASE_PERMISSION = "crp:{0}:project:{1}:deleteProject";
  public static final String PROJECT__PERMISSION = "crp:{0}:project:{1}:{2}:canEdit";


  public static final String PROJECT__SWITCH = "crp:{0}:project:{1}:projectSwitch";
  public static final String PROJECT_CONTRIBRUTIONCRP_EDIT_PERMISSION = "crp:{0}:project:{1}:contributionCrp:canEdit";
  public static final String PROJECT_DELIVERABLE_LIST_BASE_PERMISSION = "crp:{0}:project:{1}:deliverableList";
  public static final String PROJECT_DELIVERABLE_LIST_EDIT_PERMISSION = "crp:{0}:project:{1}:deliverableList:canEdit";
  public static final String PROJECT_DELIVERABLE_LIST_ADD_PERMISSION =
    "crp:{0}:project:{1}:deliverableList:addDeliverable";
  public static final String PROJECT_DELIVERABLE_LIST_REMOVE_PERMISSION =
    "crp:{0}:project:{1}:deliverableList:removeOldDeliverables";
  public static final String PROJECT_DELIVERABLE_BASE_PERMISSION = "crp:{0}:project:{1}:deliverable";
  public static final String PROJECT_DELIVERABLE_EDIT_PERMISSION = "crp:{0}:project:{1}:deliverable:canEdit";
  public static final String PROJECT_ACTIVITIES_BASE_PERMISSION = "crp:{0}:project:{1}:activities";
  public static final String PROJECT_FUNDING_SOURCE_BASE_PERMISSION = "crp:{0}:fundingSource:{1}:canEdit";

  // Create Projects Permissions
  public static final String PROJECT_CORE_ADD = "crp:{0}:project:projectsList:addCoreProject";
  public static final String PROJECT_BILATERAL_ADD = "crp:{0}:project:projectsList:addBilateralProject";


}
