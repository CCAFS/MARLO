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

package org.cgiar.ccafs.marlo.action.json.superadmin.bulkreplication;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class ProjectPoliciesByPhaseAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 592604430237828348L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ProjectPoliciesByPhaseAction.class);

  // Parameters
  private List<Map<String, Object>> entityByPhaseList;

  // Managers
  private PhaseManager phaseManager;
  private ProjectPolicyManager projectPolicyManager;

  // Variables
  private long selectedPhaseID;
  private Phase selectedPhase;

  @Inject
  public ProjectPoliciesByPhaseAction(APConfig config, PhaseManager phaseManager,
    ProjectPolicyManager projectPolicyManager) {
    super(config);
    this.phaseManager = phaseManager;
    this.projectPolicyManager = projectPolicyManager;
  }

  @Override
  public String execute() throws Exception {
    entityByPhaseList = new ArrayList<Map<String, Object>>();
    if (selectedPhaseID > 0) {
      selectedPhase = phaseManager.getPhaseById(selectedPhaseID);
      // Get policies by Phase
      List<ProjectPolicy> projectPoliciesByPhase = projectPolicyManager.getProjectPolicyByPhase(selectedPhase);

      if (this.isNotEmpty(projectPoliciesByPhase)) {
        projectPoliciesByPhase.sort((po1, po2) -> po1.getId().compareTo(po2.getId()));
        // Build the list into a Map
        for (ProjectPolicy projectPolicy : projectPoliciesByPhase) {
          try {
            if (projectPolicy != null) {
              projectPolicy.getProjectPolicyInfo(selectedPhase);
              Map<String, Object> outcomeMap = new HashMap<String, Object>();
              outcomeMap.put("id", projectPolicy.getId());
              outcomeMap.put("composedName", projectPolicy.getComposedName());
              outcomeMap.put("project", projectPolicy.getProject().getId());
              this.entityByPhaseList.add(outcomeMap);
            }
          } catch (Exception e) {
            LOG.error("Unable to add ProjectPolicy to ProjectPolicy list", e);
          }
        }
      }
    }

    return SUCCESS;
  }

  public List<Map<String, Object>> getEntityByPhaseList() {
    return entityByPhaseList;
  }

  public long getSelectedPhaseID() {
    return selectedPhaseID;
  }


  @Override
  public void prepare() throws Exception {

  }


  public void setEntityByPhaseList(List<Map<String, Object>> entityByPhaseList) {
    this.entityByPhaseList = entityByPhaseList;
  }

  public void setSelectedPhaseID(long selectedPhaseID) {
    this.selectedPhaseID = selectedPhaseID;
  }
}
