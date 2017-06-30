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

package org.cgiar.ccafs.marlo.utils;

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

  // Session variables
  public static final String SESSION_USER = "current_user";
  public static final String SESSION_CENTER = "current_center";
  public static final String USER_TOKEN = "user_token";
  public static final String SESSION_RESEARCH_AREA = "current_research_area";
  public static final String SESSION_RESEARCH_PROGRAM = "current_research_program";

  // Center Parameters
  public static final String CRP_CENTER = "CIAT";
  public static final String CENTER_PARAMETERS = "center_parameters";
  public static final String CENTER_LANGUAGE = "center_language";
  public static final String CENTER_CUSTOM_FILE = "center_custom_file";
  public static final String CENTER_COORD_ROLE = "center_coord_role";

  // Query parameter
  public static final String QUERY_PARAMETER = "q";

  // Outlook institutional email
  public static final String OUTLOOK_EMAIL = "cgiar.org";

  // Request variables
  public static final String TRANSACTION_ID = "transactionId";
  public static final String CENTER_PROGRAM_ID = "programID";
  public static final String EDITABLE_REQUEST = "edit";
  public static final String CENTER_REQUEST = "center";
  public static final String YEAR_REQUEST = "year";
  public static final String AUTOSAVE_REQUEST = "autoSave";
  public static final String SECTION_NAME = "sectionName";
  public static final String CENTER_AREA_ID = "areaID";
  public static final String RESEARCH_TOPIC_ID = "topicID";
  public static final String OUTCOME_ID = "outcomeID";
  public static final String OUTPUT_ID = "outputID";
  public static final String BENEFICIARY_TYPE_ID = "beneficiaryTypeID";
  public static final String NEXT_USER_ID = "nextUserID";
  public static final String PROJECT_ID = "projectID";
  public static final String DELIVERABLE_ID = "deliverableID";
  public static final String DELIVERABLE_TYPE_ID = "deliverableTypeId";
  public static final String OUTPUT_NAME = "outputName";
  public static final String OCS_CODE_REQUEST_ID = "ocsCode";

  // login messages and status
  public static final String LOGIN_STATUS = "loginStatus";
  public static final String LOGIN_MESSAGE = "loginMessage";
  // LADP messages
  public static final String LOGON_SUCCES = "LOGON_SUCCES";
  public static final String ERROR_NO_SUCH_USER = "ERROR_NO_SUCH_USER";
  public static final String ERROR_LOGON_FAILURE = "ERROR_LOGON_FAILURE";
  public static final String ERROR_INVALID_LOGON_HOURS = "ERROR_INVALID_LOGON_HOURS";
  public static final String ERROR_PASSWORD_EXPIRED = "ERROR_PASSWORD_EXPIRED";
  public static final String ERROR_ACCOUNT_DISABLED = "ERROR_ACCOUNT_DISABLED";
  public static final String ERROR_ACCOUNT_EXPIRED = "ERROR_ACCOUNT_EXPIRED";
  public static final String ERROR_ACCOUNT_LOCKED_OUT = "ERROR_ACCOUNT_LOCKED_OUT";
  public static final String USER_DISABLED = "USER_DISABLED";

  // new Target unit
  public static final String TARGET_UNIT_NAME = "targetUnitName";
  public static final long TARGET_UNIT_OTHER_ID = 18;

  // project Locations Constants
  public static final String MODEL_CLASS_PARAMETER = "modelClass";
  public static final String LOCATION_PARENT_ID_PARAMETER = "parentId";

  // Date Formats
  public static final String DATE_FORMAT = "yyyy-MM-dd";

  // Relations Name
  public static final String RESEARCH_PROGRAM_IMPACT_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.CenterProgram.researchImpacts)";

  public static final String RESEARCH_PROGRAM_TOPIC_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.CenterProgram.researchTopics)";

  public static final String RESEARCH_OUTCOME_MILESTONE_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.CenterOutcome.researchMilestones)";

  public static final String OUTCOME_MONITORING_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.CenterOutcome.monitoringOutcomes)";

  public static final String RESEARCH_OUTPUT_NEXTUSER_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.CenterOutput.researchOutputsNextUsers)";

  public static final String RESEARCH_OUTPUT_PARTNER_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.CenterOutput.researchOutputPartners)";

  public static final String PROJECT_FUNDING_SOURCE_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.CenterProject.projectFundingSources)";

  public static final String PROJECT_OUTPUT_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.CenterProject.projectOutputs)";

  public static final String PROJECT_LOCATION_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.CenterProject.projectLocations)";

  public static final String PROJECT_PARTNERS_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.CenterProject.projectPartners)";

  public static final String DELIVERABLE_DOCUMENT_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.CenterDeliverable.deliverableDocuments)";

  public static final String DELIVERABLE_OUTPUTS_RELATION =
    "java.util.Set(org.cgiar.ccafs.marlo.data.model.CenterDeliverable.deliverableOutputs)";

}
