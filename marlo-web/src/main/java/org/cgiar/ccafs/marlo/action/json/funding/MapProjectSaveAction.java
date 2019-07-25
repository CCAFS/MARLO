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
import org.cgiar.ccafs.marlo.data.manager.BudgetTypeManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.BudgetType;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class MapProjectSaveAction extends BaseAction {


  private static final long serialVersionUID = -5595055892247130791L;


  private InstitutionManager institutionManager;
  private ProjectManager projectManager;
  private GlobalUnitManager globalUnitManager;
  private FundingSourceManager fundingSourceManager;
  private BudgetTypeManager budgetTypeManager;
  private ProjectBudgetManager projectBudgetManager;

  private Map<String, Object> status;


  private Long institutionId;
  private GlobalUnit loggedCrp;
  private Long fundingId;
  private Double amount;
  private Long budgetTypeId;
  private int year;
  private Double genderPercentage;
  private Long projectId;


  @Inject
  public MapProjectSaveAction(APConfig config, InstitutionManager institutionManager, ProjectManager projectManager,
    GlobalUnitManager globalUnitManager, BudgetTypeManager budgetTypeManager, FundingSourceManager fundingSourceManager,
    ProjectBudgetManager projectBudgetManager) {
    super(config);
    this.institutionManager = institutionManager;
    this.projectManager = projectManager;
    this.globalUnitManager = globalUnitManager;
    this.budgetTypeManager = budgetTypeManager;
    this.fundingSourceManager = fundingSourceManager;
    this.projectBudgetManager = projectBudgetManager;
  }

  @Override
  public String execute() throws Exception {

    // Load the CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = globalUnitManager.getGlobalUnitById(loggedCrp.getId());

    Project project = projectManager.getProjectById(projectId);
    FundingSource fundingSource = fundingSourceManager.getFundingSourceById(fundingId);
    Institution institution = institutionManager.getInstitutionById(institutionId);
    BudgetType budgetType = budgetTypeManager.getBudgetTypeById(budgetTypeId);

    if (project != null) {

      ProjectBudget projectBudget = new ProjectBudget();

      projectBudget.setProject(project);
      projectBudget.setFundingSource(fundingSource);
      projectBudget.setBudgetType(budgetType);
      projectBudget.setInstitution(institution);
      projectBudget.setYear(year);
      projectBudget.setGenderPercentage(genderPercentage);
      projectBudget.setAmount(amount);
      projectBudget.setPhase(this.getActualPhase());
      projectBudget.setActive(true);

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
    institutionId =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.INSTITUTION_REQUEST_ID).getMultipleValues()[0]));
    fundingId =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.FUNDING_SOURCE_REQUEST_ID).getMultipleValues()[0]));
    projectId = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PROJECT_REQUEST_ID).getMultipleValues()[0]));
    budgetTypeId =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.BUDGET_TYPE_REQUEST_ID).getMultipleValues()[0]));
    year = Integer.parseInt(StringUtils.trim(parameters.get(APConstants.YEAR_REQUEST).getMultipleValues()[0]));
    amount = Double.parseDouble(StringUtils.trim(parameters.get(APConstants.AMOUNT_REQUEST).getMultipleValues()[0]));
    genderPercentage = Double
      .parseDouble(StringUtils.trim(parameters.get(APConstants.GENDER_PERCENTAGE_REQUEST).getMultipleValues()[0]));

  }


  public void setStatus(Map<String, Object> status) {
    this.status = status;
  }


}
