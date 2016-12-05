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
import org.cgiar.ccafs.marlo.data.manager.ProjectHighligthManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectHighligth;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/***
 * Christian Garcia
 */
public class ProjectHighListAction extends BaseAction {

  private static final long serialVersionUID = -6143944536558245482L;

  // Manager
  private ProjectHighligthManager projectHighligthManager;

  private ProjectManager projectManager;

  // Model for the back-end
  private Project project;

  // Model for the front-end
  private long projectID;
  private long higlightID;

  private List<Integer> allYears;

  @Inject
  public ProjectHighListAction(APConfig config, ProjectHighligthManager projectHighligthManager,
    ProjectManager projectManager) {
    super(config);
    this.projectHighligthManager = projectHighligthManager;

    this.projectManager = projectManager;
  }

  @Override
  public String add() {
    ProjectHighligth projectHighligth = new ProjectHighligth();
    // newDeliverable.setType(deliverableTypeManager.getDeliverableSubTypes().get(0));
    projectHighligth.setYear(new Long(this.getCurrentCycleYear()));

    projectHighligth.setActiveSince(new Date());
    projectHighligth.setAuthor("");
    // newDeliverable.setContributor("");
    // newDeliverable.setCoverage("");
    projectHighligth.setCreatedBy(this.getCurrentUser());
    projectHighligth.setDescription("");
    projectHighligth.setEndDate(new Date());

    projectHighligth.setActive(true);
    // newDeliverable.setIsGlobal(false);
    projectHighligth.setKeywords("");
    // newDeliverable.setLeader(0);
    projectHighligth.setLinks("");
    // newDeliverable.setObjectives("");
    projectHighligth.setPartners("");
    projectHighligth.setProject(project);
    // newDeliverable.setPublisher("");
    // newDeliverable.setRelation("");
    projectHighligth.setResults("");
    // newDeliverable.setRights("");
    projectHighligth.setStartDate(new Date());
    projectHighligth.setStatus(new Long(1));
    projectHighligth.setSubject("");
    projectHighligth.setTitle("");
    projectHighligth.setModificationJustification("");

    higlightID = projectHighligthManager.saveProjectHighligth(projectHighligth);

    if (higlightID > 0) {
      return SUCCESS;
    }

    return INPUT;
  }

  @Override
  public String delete() {
    // Deleting deliverable.
    for (ProjectHighligth projectHighligth : project.getHighligths()) {
      if (projectHighligth.getId() == higlightID) {
        boolean deleted = projectHighligthManager.deleteProjectHighligth(projectHighligth.getId());

        if (deleted) {
          this.addActionMessage(
            this.getText("deleting.success", new String[] {this.getText("planning.projectDeliverable").toLowerCase()}));
        } else {
          this.addActionError(
            this.getText("deleting.problem", new String[] {this.getText("planning.projectDeliverable").toLowerCase()}));
        }
      }
    }
    return SUCCESS;
  }

  public List<Integer> getAllYears() {
    return allYears;
  }


  public long getHiglightID() {
    return higlightID;
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


    allYears = project.getAllYears();

    // Getting the List of Expected Deliverables

    List<ProjectHighligth> hightLihglihts =
      project.getProjectHighligths().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    project.setHighligths(hightLihglihts);

    // Initializing Section Statuses:


  }


  public void setAllYears(List<Integer> allYears) {
    this.allYears = allYears;
  }


  public void setHiglightID(long higlightID) {
    this.higlightID = higlightID;
  }


  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


}
