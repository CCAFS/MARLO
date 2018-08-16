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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.DeliverableFundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * DeliverablesReplicationAction:
 * 
 * @author Andrés Valencia - CIAT/CCAFS
 */
public class DeliverablesReplicationAction extends BaseAction {

  private static final long serialVersionUID = 6392973543544674655L;

  private static Logger logger = LoggerFactory.getLogger(DeliverablesReplicationAction.class);

  // Managers
  private GlobalUnitManager globalUnitManager;
  private DeliverableManager deliverableManager;
  private PhaseManager phaseManager;
  private DeliverableFundingSourceManager deliverableFundingSourceManager;

  // Variables
  private String deliverablesbyPhaseList;
  private List<GlobalUnit> crps;
  private long selectedPhaseID;


  @Inject
  public DeliverablesReplicationAction(APConfig config, PhaseManager phaseManager, GlobalUnitManager globalUnitManager,
    DeliverableFundingSourceManager deliverableFundingSourceManager, DeliverableManager deliverableManager) {
    super(config);
    this.phaseManager = phaseManager;
    this.deliverableFundingSourceManager = deliverableFundingSourceManager;
    this.globalUnitManager = globalUnitManager;
    this.deliverableManager = deliverableManager;
  }


  public List<GlobalUnit> getCrps() {
    return crps;
  }

  public String getDeliverablesbyPhaseList() {
    return deliverablesbyPhaseList;
  }

  public long getSelectedPhaseID() {
    return selectedPhaseID;
  }


  @Override
  public void prepare() throws Exception {
    super.prepare();
    crps = globalUnitManager.findAll().stream().filter(c -> c.isMarlo() && c.isActive()).collect(Collectors.toList());
  }


  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      if (deliverablesbyPhaseList != null && !deliverablesbyPhaseList.isEmpty()) {
        logger.debug("Start replication for phase: " + selectedPhaseID);
        Phase phase = phaseManager.getPhaseById(selectedPhaseID);

        for (String id : deliverablesbyPhaseList.trim().split(",")) {
          logger.debug("Replicating deliverable: " + id);

          Deliverable deliverable = deliverableManager.getDeliverableById(new Long(id.trim()));
          if (deliverable.getDeliverableInfo(phase) != null) {
            // Load relations for auditlog
            List<String> relationsName = new ArrayList<>();
            relationsName.add(APConstants.PROJECT_DELIVERABLE_PARTNERSHIPS_RELATION);
            relationsName.add(APConstants.PROJECT_DELIVERABLE_INFO);
            relationsName.add(APConstants.PROJECT_DELIVERABLE_FUNDING_RELATION);
            relationsName.add(APConstants.PROJECT_DELIVERABLE_GENDER_LEVELS);
            if (this.isReportingActive() || (this.isPlanningActive() && this.getActualPhase().getUpkeep())) {
              relationsName.add(APConstants.PROJECT_DELIVERABLE_QUALITY_CHECK);
              relationsName.add(APConstants.PROJECT_DELIVERABLE_METADATA_ELEMENT);
              relationsName.add(APConstants.PROJECT_DELIVERABLE_DATA_SHARING_FILES);
              relationsName.add(APConstants.PROJECT_DELIVERABLE_PUBLICATION_METADATA);
              relationsName.add(APConstants.PROJECT_DELIVERABLE_DISEMINATIONS);
              relationsName.add(APConstants.PROJECT_DELIVERABLE_CRPS);
              relationsName.add(APConstants.PROJECT_DELIVERABLE_USERS);
              relationsName.add(APConstants.PROJECT_DELIVERABLES_INTELLECTUAL_RELATION);
              relationsName.add(APConstants.PROJECT_DELIVERABLES_PARTICIPANT_RELATION);
              relationsName.add(APConstants.PROJECT_DELIVERABLES_PARTICIPANT_LOCATION_RELATION);
            }
            List<DeliverableFundingSource> deliverableFundingSources = deliverable.getDeliverableFundingSources()
              .stream().filter(df -> df.isActive() && df.getPhase() != null && df.getPhase().equals(phase))
              .collect(Collectors.toList());
            // Deliverable Funding sources
            if (deliverableFundingSources != null && !deliverableFundingSources.isEmpty()) {
              for (DeliverableFundingSource deliverableFundingSource : deliverableFundingSources) {
                logger
                  .debug("Saving deliverable funding source: " + deliverableFundingSource.getFundingSource().getId());
                deliverableFundingSourceManager.saveDeliverableFundingSource(deliverableFundingSource);
              }
            }

            // Deliverable info
            deliverableManager.saveDeliverable(deliverable, this.getActionName(), relationsName, phase);

          }

        }
      } else {
        logger.debug("No deliverable selected");
      }
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }


  public void setCrps(List<GlobalUnit> crps) {
    this.crps = crps;
  }


  public void setDeliverablesbyPhaseList(String deliverablesbyPhaseList) {
    this.deliverablesbyPhaseList = deliverablesbyPhaseList;
  }

  public void setSelectedPhaseID(long selectedPhaseID) {
    this.selectedPhaseID = selectedPhaseID;
  }


}
