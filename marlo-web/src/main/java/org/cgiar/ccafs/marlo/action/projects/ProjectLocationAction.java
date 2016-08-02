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

package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectLocationAction extends BaseAction {


  private static final long serialVersionUID = -3215013554941621274L;


  private Crp loggedCrp;

  private Project project;


  private HashMap<String, List<Object>> locationsLevels;

  private CrpManager crpManager;


  private ProjectManager projectManager;

  private LocElementTypeManager locElementTypeManager;

  private CrpProgramManager crpProgramManager;

  private Long projectId;

  @Inject
  public ProjectLocationAction(APConfig config, CrpManager crpManager, ProjectManager projectManager,
    LocElementTypeManager locElementTypeManager, CrpProgramManager crpProgramManager) {
    super(config);
    this.crpManager = crpManager;
    this.projectManager = projectManager;
    this.locElementTypeManager = locElementTypeManager;
    this.crpProgramManager = crpProgramManager;
  }


  public HashMap<String, List<Object>> getLocationsLevels() {
    return locationsLevels;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  public Project getProject() {
    return project;
  }

  public Long getProjectId() {
    return projectId;
  }

  /**
  * 
  */
  public void locationLevels() {
    locationsLevels = new HashMap<>();

    LocElementType regionsElementType = locElementTypeManager.getLocElementTypeById(1);

    locationsLevels.put(regionsElementType.getName(),
      regionsElementType.getLocElements().stream().filter(le -> le.isActive()).collect(Collectors.toList()));


    List<Object> regionPrograms = crpProgramManager
      .findAll().stream().filter(cp -> cp.isActive()
        && (cp.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()) && cp.getCrp().equals(loggedCrp))
      .collect(Collectors.toList());

    locationsLevels.put("Custom Regions", regionPrograms);

    List<Object> customElementTypes = locElementTypeManager.findAll().stream()
      .filter(let -> let.isActive() && let.getCrp() != null && let.getCrp().equals(loggedCrp) && let.getId() != 1)
      .collect(Collectors.toList());

    locationsLevels.put("Custom Locations", customElementTypes);

    List<Object> elementTypes = locElementTypeManager.findAll().stream()
      .filter(let -> let.isActive() && let.getCrp() == null && let.getId() != 1).collect(Collectors.toList());

    locationsLevels.put("Other Locations", elementTypes);

  }

  @Override
  public void prepare() throws Exception {
    // Get current CRP
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    projectId = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));

    project = projectManager.getProjectById(projectId);

    this.locationLevels();

    project.setLocations(new ArrayList<ProjectLocation>(
      project.getProjectLocations().stream().filter(p -> p.isActive()).collect(Collectors.toList())));
    System.out.println("");


  }

  public void setLocationsLevels(HashMap<String, List<Object>> locationsLevels) {
    this.locationsLevels = locationsLevels;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }
}
