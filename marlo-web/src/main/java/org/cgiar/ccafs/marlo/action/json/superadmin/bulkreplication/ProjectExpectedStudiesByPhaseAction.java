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
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
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

public class ProjectExpectedStudiesByPhaseAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 7929740183931157327L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ProjectExpectedStudiesByPhaseAction.class);

  // Parameters
  private List<Map<String, Object>> entityByPhaseList;

  // Managers
  private PhaseManager phaseManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;

  // Variables
  private long selectedPhaseID;
  private Phase selectedPhase;

  @Inject
  public ProjectExpectedStudiesByPhaseAction(APConfig config, PhaseManager phaseManager,
    ProjectExpectedStudyManager projectExpectedStudyManager) {
    super(config);
    this.phaseManager = phaseManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
  }

  @Override
  public String execute() throws Exception {
    entityByPhaseList = new ArrayList<Map<String, Object>>();
    if (selectedPhaseID > 0) {
      selectedPhase = phaseManager.getPhaseById(selectedPhaseID);
      // Get studies by Phase
      List<ProjectExpectedStudy> projectExpectedStudiesByPhase =
        projectExpectedStudyManager.getAllStudiesByPhase(selectedPhase.getId());

      if (this.isNotEmpty(projectExpectedStudiesByPhase)) {
        projectExpectedStudiesByPhase.sort((po1, po2) -> po1.getId().compareTo(po2.getId()));
        // Build the list into a Map
        for (ProjectExpectedStudy projectExpectedStudy : projectExpectedStudiesByPhase) {
          try {
            if (projectExpectedStudy != null) {
              projectExpectedStudy.getProjectExpectedStudyInfo(selectedPhase);
              Map<String, Object> outcomeMap = new HashMap<String, Object>();
              outcomeMap.put("id", projectExpectedStudy.getId());
              outcomeMap.put("composedName", projectExpectedStudy.getComposedName());
              outcomeMap.put("project", projectExpectedStudy.getProject().getId());
              this.entityByPhaseList.add(outcomeMap);
            }
          } catch (Exception e) {
            LOG.error("Unable to add ProjectExpectedStudy to ProjectExpectedStudy list", e);
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
