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
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectsBilaterCofinancingNewAction extends BaseAction {


  private static final long serialVersionUID = 8684072284268214629L;


  private String title;


  private String startDate;


  private String endDate;


  private String financeCode;


  private String status;


  private String budget;


  private String liaisonInstitution;


  private String institution;


  private String contactName;


  private String contactEmail;


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
      projectProp.put("status", "Ok");

    } else {
      projectProp.put("status", "Fail");
    }

    projectCreated.add(projectProp);

    return SUCCESS;
  }


  public String getBudget() {
    return budget;
  }


  public String getContactEmail() {
    return contactEmail;
  }

  public String getContactName() {
    return contactName;
  }

  public String getEndDate() {
    return endDate;
  }

  public String getFinanceCode() {
    return financeCode;
  }

  public String getInstitution() {
    return institution;
  }

  public String getLiaisonInstitution() {
    return liaisonInstitution;
  }

  public ProjectBilateralCofinancing getProject() {
    return project;
  }

  public List<Map<String, Object>> getProjectCreated() {
    return projectCreated;
  }

  public String getStartDate() {
    return startDate;
  }

  public String getStatus() {
    return status;
  }

  public String getTitle() {
    return title;
  }


  @Override
  public void prepare() throws Exception {

    Map<String, Object> parameters = this.getParameters();

    project = new ProjectBilateralCofinancing();
    project.setId((long) -1);

    project.setTitle(StringUtils.trim(((String[]) parameters.get(title))[0]));


    LiaisonInstitution liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(this.liaisonInstitution))[0])));
    project.setLiaisonInstitution(liaisonInstitution);

    Institution institution = institutionManager
      .getInstitutionById(Long.parseLong(StringUtils.trim(((String[]) parameters.get(this.institution))[0])));
    project.setInstitution(institution);


    SimpleDateFormat dateFormat = new SimpleDateFormat(APConstants.DATE_FORMAT);

    project.setStartDate(dateFormat.parse(StringUtils.trim(((String[]) parameters.get(this.startDate))[0])));
    project.setEndDate(dateFormat.parse(StringUtils.trim(((String[]) parameters.get(this.endDate))[0])));

    project.setFinanceCode(StringUtils.trim(((String[]) parameters.get(financeCode))[0]));
    project.setContactPersonEmail(StringUtils.trim(((String[]) parameters.get(contactEmail))[0]));
    project.setContactPersonName(StringUtils.trim(((String[]) parameters.get(contactName))[0]));
    project.setBudget(Long.parseLong(StringUtils.trim(((String[]) parameters.get(budget))[0])));


  }


  public void setBudget(String budget) {
    this.budget = budget;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }


  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }


  public void setFinanceCode(String financeCode) {
    this.financeCode = financeCode;
  }

  public void setInstitution(String institution) {
    this.institution = institution;
  }

  public void setLiaisonInstitution(String liaisonInstitution) {
    this.liaisonInstitution = liaisonInstitution;
  }

  public void setProject(ProjectBilateralCofinancing project) {
    this.project = project;
  }

  public void setProjectCreated(List<Map<String, Object>> projectCreated) {
    this.projectCreated = projectCreated;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setTittle(String title) {
    this.title = title;
  }


}
