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
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
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

public class ProjectInnovationsByPhaseAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -8029590385469599747L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ProjectInnovationsByPhaseAction.class);

  // Parameters
  private List<Map<String, Object>> entityByPhaseList;

  // Managers
  private PhaseManager phaseManager;
  private ProjectInnovationManager projectInnovationManager;

  // Variables
  private long selectedPhaseID;
  private Phase selectedPhase;

  @Inject
  public ProjectInnovationsByPhaseAction(APConfig config, PhaseManager phaseManager,
    ProjectInnovationManager projectInnovationManager) {
    super(config);
    this.phaseManager = phaseManager;
    this.projectInnovationManager = projectInnovationManager;
  }

  @Override
  public String execute() throws Exception {
    entityByPhaseList = new ArrayList<Map<String, Object>>();
    if (selectedPhaseID > 0) {
      selectedPhase = phaseManager.getPhaseById(selectedPhaseID);
      // Get innovations by Phase
      List<ProjectInnovation> projectInnovationsByPhase = projectInnovationManager.getInnovationsByPhase(selectedPhase);

      if (this.isNotEmpty(projectInnovationsByPhase)) {
        projectInnovationsByPhase.sort((po1, po2) -> po1.getId().compareTo(po2.getId()));
        // Build the list into a Map
        for (ProjectInnovation projectInnovation : projectInnovationsByPhase) {
          try {
            if (projectInnovation != null) {
              projectInnovation.getProjectInnovationInfo(selectedPhase);
              Map<String, Object> outcomeMap = new HashMap<String, Object>();
              outcomeMap.put("id", projectInnovation.getId());
              outcomeMap.put("composedName", projectInnovation.getComposedName());
              outcomeMap.put("project", projectInnovation.getProject().getId());
              this.entityByPhaseList.add(outcomeMap);
            }
          } catch (Exception e) {
            LOG.error("Unable to add ProjectInnovation to ProjectInnovation list", e);
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
