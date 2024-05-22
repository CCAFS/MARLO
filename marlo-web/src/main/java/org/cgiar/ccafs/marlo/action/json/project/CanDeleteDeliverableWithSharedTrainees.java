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

package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.DeliverableClusterParticipantManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableClusterParticipant;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kenjitm
 */
public class CanDeleteDeliverableWithSharedTrainees extends BaseAction {

  private static final long serialVersionUID = -8182788196525839215L;
  private Map<String, Object> canDelete;
  private long deliverableID;
  private long phaseID;
  private DeliverableClusterParticipantManager deliverableClusterParticipantManager;
  private final Logger logger = LoggerFactory.getLogger(CanDeleteDeliverableWithSharedTrainees.class);

  @Inject
  public CanDeleteDeliverableWithSharedTrainees(APConfig config,
    DeliverableClusterParticipantManager deliverableClusterParticipantManager) {
    super(config);
    this.deliverableClusterParticipantManager = deliverableClusterParticipantManager;
  }

  @Override
  public String execute() throws Exception {
    boolean response = true;
    try {
      // Check if the shared cluster trainees specificity is active
      if (this.hasSpecificities(APConstants.DELIVERABLE_SHARED_CLUSTERS_TRAINEES_ACTIVE)) {
        // Check if there is no submission in progress phase
        if (!this.isProgressActive()) {
          // Retrieve deliverable cluster participants
          List<DeliverableClusterParticipant> deliverableClusterParticipants = deliverableClusterParticipantManager
            .getDeliverableClusterParticipantByDeliverableAndPhase(deliverableID, phaseID);

          // Check if the list is not empty and process each participant
          if (deliverableClusterParticipants != null && !deliverableClusterParticipants.isEmpty()) {
            for (DeliverableClusterParticipant deliverableShared : deliverableClusterParticipants) {

              Project project = deliverableShared.getProject();
              // Ensure necessary objects are not null and IDs are different, then check submission status
              if (this.isSubmit(project.getId())) {
                // Return true if submission is found
                response = false;
              }
            }
          }

        }
      }
    } catch (Exception e) {
      // Log error if an exception occurs
      logger.error("Error getting shared clusters statuses", e);
    }
    canDelete = new HashMap<String, Object>();
    canDelete.put("response", response);

    return SUCCESS;
  }

  public Map<String, Object> getCanDelete() {
    return canDelete;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    deliverableID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.DELIVERABLE_ID).getMultipleValues()[0]));
    phaseID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0]));
  }

  public void setCanDelete(Map<String, Object> canDelete) {
    this.canDelete = canDelete;
  }

}
