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

package org.cgiar.ccafs.marlo.action.json.funding;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;


public class UnmapProjectAction extends BaseAction {


  private static final long serialVersionUID = -5595055892247130791L;

  private GlobalUnitManager globalUnitManager;
  private FundingSourceManager fundingSourceManager;
  private ProjectBudgetManager projectBudgetManager;

  private Map<String, Object> status;

  private GlobalUnit loggedCrp;
  private Long phaseID;
  private Long projectBudgetID;
  private int year;

  @Inject
  public UnmapProjectAction(APConfig config, GlobalUnitManager globalUnitManager,
    FundingSourceManager fundingSourceManager, ProjectBudgetManager projectBudgetManager) {
    super(config);
    this.globalUnitManager = globalUnitManager;
    this.fundingSourceManager = fundingSourceManager;
    this.projectBudgetManager = projectBudgetManager;
  }

  @Override
  public String execute() throws Exception {

    // Load the CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = globalUnitManager.getGlobalUnitById(loggedCrp.getId());

    status = new HashMap<String, Object>();

    ProjectBudget projectBudget = projectBudgetManager.getProjectBudgetById(projectBudgetID);

    if (projectBudget != null) {
      projectBudget.setActive(false);
      projectBudget = projectBudgetManager.saveProjectBudget(projectBudget);

      if (projectBudget.getId() != null) {
        status.put("save", true);
      } else {
        status.put("save", false);
      }
    }

    return SUCCESS;
  }

  public Map<String, Object> getStatus() {
    return status;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    projectBudgetID =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.PROJECT_BUDGET_ID).getMultipleValues()[0]));
    phaseID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0]));
    year = Integer.parseInt(StringUtils.trim(parameters.get(APConstants.YEAR_REQUEST).getMultipleValues()[0]));
  }

  public void setStatus(Map<String, Object> status) {
    this.status = status;
  }
}
