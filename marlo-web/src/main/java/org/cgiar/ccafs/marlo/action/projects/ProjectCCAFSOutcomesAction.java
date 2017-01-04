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
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.IpElement;
import org.cgiar.ccafs.marlo.data.model.IpIndicator;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.IpProjectContribution;
import org.cgiar.ccafs.marlo.data.model.IpProjectIndicator;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocusPrev;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/***
 * Christian Garcia
 */
public class ProjectCCAFSOutcomesAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -6970673687247245375L;
  private List<IpElement> midOutcomes;
  private List<IpElement> midOutcomesSelected;


  private List<IpProgram> projectFocusList;


  private List<IpElement> previousOutputs;
  private List<IpIndicator> previousIndicators;
  private List<Integer> allYears;


  private long projectID;
  private Project project;


  // Managers
  private ProjectManager projectManager;
  private CrpProgramManager crpProgramManager;
  private CrpManager crpManager;

  private Crp loggedCrp;

  @Inject
  public ProjectCCAFSOutcomesAction(APConfig config, ProjectManager projectManager, CrpProgramManager crpProgramManager,
    CrpManager crpManager) {
    super(config);
    this.crpProgramManager = crpProgramManager;
    this.projectManager = projectManager;
    this.crpManager = crpManager;

  }


  public int getMidOutcomeYear() {
    return APConstants.MID_OUTCOME_YEAR;
  }

  @Override
  public void prepare() throws Exception {
    super.prepare();
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    midOutcomes = new ArrayList<>();
    midOutcomesSelected = new ArrayList<>();
    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    project = projectManager.getProjectById(projectID);

    // Get all years
    allYears = project.getAllYears();
    allYears.add(this.getMidOutcomeYear());

    projectFocusList = new ArrayList<>();

    List<ProjectFocusPrev> focusPrevs =
      project.getProjectFocusPrevs().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    for (ProjectFocusPrev projectFocusPrev : focusPrevs) {
      projectFocusList.add(projectFocusPrev.getIpProgram());
    }
    List<IpProjectContribution> ipProjectContributions =
      project.getIpProjectContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    project.setOutputs(new ArrayList<>());
    for (IpProjectContribution ipProjectContribution : ipProjectContributions) {
      project.getOutputs().add(ipProjectContribution.getIpElementByMogId());
    }
    List<IpProjectIndicator> ipProjectIndicators =
      project.getIpProjectIndicators().stream().filter(c -> c.isActive()).collect(Collectors.toList());

    project.setIndicators(new ArrayList<>());
    for (IpProjectIndicator ipProjectIndicator : ipProjectIndicators) {
      project.getIndicators().add(ipProjectIndicator.getIpIndicator());
    }

  }
}
