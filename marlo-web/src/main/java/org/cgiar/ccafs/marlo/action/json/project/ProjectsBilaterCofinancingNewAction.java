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
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBilateralCofinancingManager;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectBilateralCofinancing;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectsBilaterCofinancingNewAction extends BaseAction {


  private static final long serialVersionUID = 8684072284268214629L;


  private static String TITLE = "title";


  private static String START_DATE = "startDate";


  private static String END_DATE = "endDate";
  private static String FINANCE_CODE = "financeCode";
  private static String STATUS = "status";
  private static String BUDGET = "budget";
  private static String LEAD_CENTER = "liaisonInstitution";
  private static String DONOR = "institution";
  private static String CONTACT_NAME = "contactName";
  private static String CONTACT_EMAIL = "contactEmail";
  private static String COFUNDED_MODE = "cofundedMode";
  private ProjectBilateralCofinancingManager projectBilateralCofinancingManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;


  private InstitutionManager institutionManager;
  private ProjectBilateralCofinancing project;
  private List<Map<String, Object>> projectCreated;

  @Inject
  public ProjectsBilaterCofinancingNewAction(APConfig config,
    ProjectBilateralCofinancingManager projectBilateralCofinancingManager, InstitutionManager institutionManager,
    LiaisonInstitutionManager liaisonInstitutionManager) {
    super(config);
    this.projectBilateralCofinancingManager = projectBilateralCofinancingManager;
    this.institutionManager = institutionManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
  }

  @Override
  public String execute() throws Exception {

    Map<String, Object> parameters = ActionContext.getContext().getParameters();

    project = new ProjectBilateralCofinancing();

    project.setTitle(StringUtils.trim(((String[]) parameters.get(TITLE))[0]));


    Institution institutionLead = institutionManager
      .getInstitutionById(Long.parseLong(StringUtils.trim(((String[]) parameters.get(LEAD_CENTER))[0])));

    LiaisonInstitution liaisonInstitution = liaisonInstitutionManager.findByAcronym(institutionLead.getAcronym());

    project.setLiaisonInstitution(liaisonInstitution);


    Institution institutionDonor =
      institutionManager.getInstitutionById(Long.parseLong(StringUtils.trim(((String[]) parameters.get(DONOR))[0])));
    project.setInstitution(institutionDonor);


    SimpleDateFormat dateFormat = new SimpleDateFormat(APConstants.DATE_FORMAT);

    project.setStartDate(dateFormat.parse(StringUtils.trim(((String[]) parameters.get(START_DATE))[0])));
    project.setEndDate(dateFormat.parse(StringUtils.trim(((String[]) parameters.get(END_DATE))[0])));

    project.setFinanceCode(StringUtils.trim(((String[]) parameters.get(FINANCE_CODE))[0]));
    project.setContactPersonEmail(StringUtils.trim(((String[]) parameters.get(CONTACT_EMAIL))[0]));
    project.setContactPersonName(StringUtils.trim(((String[]) parameters.get(CONTACT_NAME))[0]));
    project.setCofundedMode(Integer.parseInt(StringUtils.trim(((String[]) parameters.get(COFUNDED_MODE))[0])));
    project.setBudget(Long.parseLong(StringUtils.trim(((String[]) parameters.get(BUDGET))[0])));
    project.setAgreement(Integer.parseInt(StringUtils.trim(((String[]) parameters.get(STATUS))[0])));

    projectCreated = new ArrayList<>();

    project.setActive(true);
    project.setCreatedBy(this.getCurrentUser());
    project.setModifiedBy(this.getCurrentUser());
    project.setModificationJustification("");
    project.setActiveSince(new Date());

    Long projectId = projectBilateralCofinancingManager.saveProjectBilateralCofinancing(project);

    Map<String, Object> projectProp = new HashMap<>();

    if (projectId > 0) {
      projectProp.put("id", projectId);
      projectProp.put("title", project.getTitle());
      projectProp.put("budget", project.getBudget());
      projectProp.put("status", "OK");

    } else {
      projectProp.put("status", "FAIL");
      projectProp.put("message", this.getText("manageUsers.email.notAdded"));
    }

    projectCreated.add(projectProp);

    return SUCCESS;
  }


  public ProjectBilateralCofinancing getProject() {
    return project;
  }


  public List<Map<String, Object>> getProjectCreated() {
    return projectCreated;
  }

  @Override
  public void prepare() throws Exception {


  }


  public void setProjectCreated(List<Map<String, Object>> projectCreated) {
    this.projectCreated = projectCreated;
  }

}
