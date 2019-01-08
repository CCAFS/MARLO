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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLp6ContributionManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import com.opensymphony.xwork2.LocalizedTextProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;


@Named
public class ProjectCollaborationValue extends BaseAction {

  private static final long serialVersionUID = -7458726524471438475L;
  private GlobalUnitManager crpManager;
  private long projectId;
  private ProjectLp6ContributionManager projectLp6ContributionManager;

  // Front-end
  private long projectID;
  private GlobalUnit loggedCrp;
  private Project project;
  private ProjectLp6Contribution projectLp6Contribution;
  private Map<String, Object> status;
  private boolean contributionValue;
  private Phase phase;
  private final LocalizedTextProvider localizedTextProvider;

  @Inject
  public ProjectCollaborationValue(APConfig config, GlobalUnitManager crpManager,
    LocalizedTextProvider localizedTextProvider, ProjectLp6ContributionManager projectLp6ContributionManager) {
    super(config);
    this.localizedTextProvider = localizedTextProvider;
    this.projectLp6ContributionManager = projectLp6ContributionManager;
    this.crpManager = crpManager;
  }

  @Override
  public String execute() throws Exception {
    status = new HashMap<String, Object>();
    status.put("status", contributionValue);

    try {
      projectLp6Contribution =
        projectLp6ContributionManager.findAll().stream().filter(c -> c.isActive() && c.getProject().getId() == projectID
          && c.getPhase().getId() == this.getActualPhase().getId()).collect(Collectors.toList()).get(0);
      if (contributionValue == true) {

        if (projectLp6Contribution == null) {
          projectLp6Contribution = new ProjectLp6Contribution();
          projectLp6Contribution.setActive(true);
          projectLp6Contribution.setPhase(phase);
          projectLp6Contribution.setProject(project);
          projectLp6Contribution.setContribution(contributionValue);
          projectLp6ContributionManager.saveProjectLp6Contribution(projectLp6Contribution);
        } else {
          projectLp6Contribution.setContribution(contributionValue);
          projectLp6ContributionManager.saveProjectLp6Contribution(projectLp6Contribution);
        }

      } else {
        /*
         * If contribution value is false update the value to existent projectLp6contribution
         */
        if (projectLp6Contribution != null) {
          projectLp6Contribution.setContribution(contributionValue);
          projectLp6ContributionManager.saveProjectLp6Contribution(projectLp6Contribution);
        }
      }

    } catch (Exception e) {
    }
    return SUCCESS;
  }


  public Map<String, Object> getStatus() {
    return status;
  }


  @Override
  public void prepare() throws Exception {
    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    } catch (Exception e) {

    }

    contributionValue =
      Boolean.parseBoolean(StringUtils.trim(this.getRequest().getParameter(APConstants.CRP_LP6_CONTRIBUTION_VALUE)));

    phase = this.getActualPhase();

    Map<String, Parameter> parameters = this.getParameters();
    projectId = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PROJECT_REQUEST_ID).getMultipleValues()[0]));
  }


  public void setStatus(Map<String, Object> status) {
    this.status = status;
  }


}
