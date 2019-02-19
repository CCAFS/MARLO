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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectNextuser;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcomeIndicator;
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
public class ProjectsOutcomesReplicationAction extends BaseAction {

  private static final long serialVersionUID = 6392973543544674655L;

  private static Logger logger = LoggerFactory.getLogger(ProjectsOutcomesReplicationAction.class);

  // Managers
  private GlobalUnitManager globalUnitManager;
  private ProjectOutcomeManager projectOutcomeManager;

  // Variables
  private String entityByPhaseList;
  private List<GlobalUnit> crps;
  private long selectedPhaseID;
  private ProjectOutcome projectOutcome;


  @Inject
  public ProjectsOutcomesReplicationAction(APConfig config, GlobalUnitManager globalUnitManager,
    ProjectOutcomeManager projectOutcomeManager) {
    super(config);
    this.globalUnitManager = globalUnitManager;
    this.projectOutcomeManager = projectOutcomeManager;
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

        for (String id : entityByPhaseList.trim().split(",")) {
          logger.debug("Replicating outcome: " + id);

          projectOutcome = projectOutcomeManager.getProjectOutcomeById(new Long(id.trim()));
          if (projectOutcome != null) {

            /** Load relation list of project Outcomes to save correctly **/
            // Milestones
            List<ProjectMilestone> projectMilestones =
              projectOutcome.getProjectMilestones().stream().filter(pm -> pm.isActive()).collect(Collectors.toList());
            if (projectMilestones != null && !projectMilestones.isEmpty()) {
              projectOutcome.setMilestones(projectMilestones);
            } else {
              projectOutcome.setMilestones(new ArrayList<ProjectMilestone>());
            }

            // Indicators
            List<ProjectOutcomeIndicator> projectOutcomeIndicators = projectOutcome.getProjectOutcomeIndicators()
              .stream().filter(pm -> pm.isActive()).collect(Collectors.toList());
            if (projectOutcomeIndicators != null && !projectOutcomeIndicators.isEmpty()) {
              projectOutcome.setIndicators(projectOutcomeIndicators);
            } else {
              projectOutcome.setIndicators(new ArrayList<ProjectOutcomeIndicator>());
            }

            // Next users
            List<ProjectNextuser> projectNextusers =
              projectOutcome.getProjectNextusers().stream().filter(pm -> pm.isActive()).collect(Collectors.toList());
            if (projectNextusers != null && !projectNextusers.isEmpty()) {
              projectOutcome.setNextUsers(projectNextusers);
            } else {
              projectOutcome.setNextUsers(new ArrayList<ProjectNextuser>());
            }

            projectOutcomeManager.saveProjectOutcome(projectOutcome);

          }
        }
      } else {
        logger.debug("No projectOutcome selected");
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