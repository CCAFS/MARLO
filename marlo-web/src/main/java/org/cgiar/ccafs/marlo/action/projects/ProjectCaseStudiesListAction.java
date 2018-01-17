/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
package org.cgiar.ccafs.marlo.action.projects;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CaseStudyManager;
import org.cgiar.ccafs.marlo.data.manager.CaseStudyProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.CaseStudy;
import org.cgiar.ccafs.marlo.data.model.CaseStudyProject;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/***
 * Christian Garcia
 */
public class ProjectCaseStudiesListAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = -3082621120976012917L;

  private List<Integer> allYears;

  private long caseStudyID;
  // Manager
  private CaseStudyManager caseStudyManager;
  private CaseStudyProjectManager caseStudyProjectManager;

  // Model for the back-end
  private Project project;

  // Model for the front-end
  private long projectID;
  private ProjectManager projectManager;

  @Inject
  public ProjectCaseStudiesListAction(APConfig config, CaseStudyManager caseStudyManager, ProjectManager projectManager,
    CaseStudyProjectManager caseStudyProjectManager) {
    super(config);
    this.caseStudyManager = caseStudyManager;
    this.caseStudyProjectManager = caseStudyProjectManager;
    this.projectManager = projectManager;
  }

  @Override
  public String add() {

    String params[] = {this.getCrpSession(), project.getId().toString()};

    if (this.hasPermission(this.generatePermission(Permission.PROJECT_CASE_STUDY_PERMISSION, params))) {

      CaseStudy caseStudy = new CaseStudy();
      // newDeliverable.setType(deliverableTypeManager.getDeliverableSubTypes().get(0));

      caseStudy.setActive(true);
      caseStudy.setActiveSince(new Date());
      caseStudy.setActivities("");
      caseStudy.setComment("");
      caseStudy.setCreatedBy(this.getCurrentUser());
      caseStudy.setEvidenceOutcome("");
      caseStudy.setExplainIndicatorRelation("");
      caseStudy.setFile(null);
      caseStudy.setModificationJustification("");
      caseStudy.setModifiedBy(this.getCurrentUser());
      caseStudy.setNonResearchPartneres("");
      caseStudy.setOutcomeStatement("");
      caseStudy.setOutputUsed("");
      caseStudy.setOutputUsers("");
      caseStudy.setReferencesCase("");
      caseStudy.setResearchOutputs("");
      caseStudy.setResearchPartners("");
      caseStudy.setResearchPatern("");
      caseStudy.setTitle("");
      caseStudy.setYear(this.getCurrentCycleYear());
      caseStudy = caseStudyManager.saveCaseStudy(caseStudy);
      caseStudyID = caseStudy.getId();


      if (caseStudyID > 0) {
        CaseStudyProject caseStudyProject = new CaseStudyProject();
        caseStudyProject.setCaseStudy(caseStudy);
        caseStudyProject.setProject(project);
        caseStudyProject.setCreated(true);
        caseStudyProjectManager.saveCaseStudyProject(caseStudyProject);
        return SUCCESS;
      }


    }
    return NOT_AUTHORIZED;
  }

  public boolean canDelete(long owner) {

    if (owner == projectID) {
      return true;
    }
    return false;
  }


  public boolean canEdit() {
    String params[] = {this.getCrpSession(), project.getId().toString()};

    return (this.hasPermission(this.generatePermission(Permission.PROJECT_CASE_STUDY_PERMISSION, params)))
      && (!this.isProjectSubmitted(projectID));
  }

  @Override
  public String delete() {
    // Deleting deliverable.
    for (CaseStudy caseStudy : project.getCaseStudies()) {
      if (caseStudy.getId() == caseStudyID) {
        CaseStudy cStudy = caseStudyManager.getCaseStudyById(caseStudy.getId());
        for (CaseStudyProject caseStudyProject : cStudy.getCaseStudyProjects()) {
          caseStudyProjectManager.deleteCaseStudyProject(caseStudyProject.getId());
        }
        caseStudyManager.deleteCaseStudy(caseStudy.getId());


      }
    }
    return SUCCESS;
  }

  @Override
  public List<Integer> getAllYears() {
    return allYears;
  }


  public long getCaseStudyID() {
    return caseStudyID;
  }


  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  public String getProjectRequestID() {
    return APConstants.PROJECT_REQUEST_ID;
  }


  @Override
  public String next() {
    String result = this.save();
    if (result.equals(BaseAction.SUCCESS)) {
      return BaseAction.NEXT;
    } else {
      return result;
    }
  }


  @Override
  public void prepare() throws Exception {

    projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    project = projectManager.getProjectById(projectID);

    // Getting the Deliverables Main Types.


    allYears = project.getProjecInfoPhase(this.getActualPhase()).getAllYears();

    // Getting the List of Expected Deliverables

    List<CaseStudyProject> caseStudyProjects =
      project.getCaseStudyProjects().stream().filter(c -> c.isActive()).collect(Collectors.toList());


    project.setCaseStudies(new ArrayList<CaseStudy>());
    for (CaseStudyProject caseStudyProject : caseStudyProjects) {
      if (caseStudyProject.getCaseStudy().isActive()) {

        project.getCaseStudies().add(caseStudyProject.getCaseStudy());
      }
    }


    // Initializing Section Statuses:


  }


  public void setAllYears(List<Integer> allYears) {
    this.allYears = allYears;
  }


  public void setCaseStudyID(long higlightID) {
    this.caseStudyID = higlightID;
  }


  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


}
