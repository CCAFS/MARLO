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
import org.cgiar.ccafs.marlo.data.manager.ActivityManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.DeliverableActivity;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.utils.APConfig;

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
public class ActivitiesReplicationAction extends BaseAction {

  private static final long serialVersionUID = 6392973543544674655L;

  private static Logger logger = LoggerFactory.getLogger(ActivitiesReplicationAction.class);

  // Managers
  private GlobalUnitManager globalUnitManager;
  private ActivityManager activityManager;
  private PhaseManager phaseManager;

  // Variables
  private String entityByPhaseList;
  private List<GlobalUnit> crps;
  private long selectedPhaseID;
  private Phase phase;
  private Activity activity;


  @Inject
  public ActivitiesReplicationAction(APConfig config, PhaseManager phaseManager, GlobalUnitManager globalUnitManager,
    ActivityManager activityManager) {
    super(config);
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.activityManager = activityManager;
  }


  public List<GlobalUnit> getCrps() {
    return crps;
  }


  public String getEntityByPhaseList() {
    return entityByPhaseList;
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
      if (entityByPhaseList != null && !entityByPhaseList.isEmpty()) {
        logger.debug("Start replication for phase: " + selectedPhaseID);
        phase = phaseManager.getPhaseById(selectedPhaseID);

        for (String id : entityByPhaseList.trim().split(",")) {
          logger.debug("Replicating activity: " + id);

          activity = activityManager.getActivityById(new Long(id.trim()));
          if (activity != null) {

            // Set deliverables here to add inside saveActivity
            List<DeliverableActivity> deliverableActivities = activity.getDeliverableActivities().stream()
              .filter(da -> da.isActive() && da.getPhase().equals(phase)).collect(Collectors.toList());
            activity.setDeliverables(deliverableActivities);

            // Save activity and deliverable activities
            activityManager.saveActivity(activity);

          }
        }
      } else {
        logger.debug("No activity selected");
      }
      logger.debug("Finished replication succesfully");
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }


  public void setCrps(List<GlobalUnit> crps) {
    this.crps = crps;
  }

  public void setEntityByPhaseList(String entityByPhaseList) {
    this.entityByPhaseList = entityByPhaseList;
  }


  public void setSelectedPhaseID(long selectedPhaseID) {
    this.selectedPhaseID = selectedPhaseID;
  }


}