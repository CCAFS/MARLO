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
import org.cgiar.ccafs.marlo.data.manager.ProjectHighlightInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectHighligthManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightInfo;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.ibm.icu.util.Calendar;
import org.apache.commons.lang3.StringUtils;

/***
 * Christian Garcia
 */
public class ProjectHighListAction extends BaseAction {

  private static final long serialVersionUID = -6143944536558245482L;

  // Manager
  private ProjectHighligthManager projectHighligthManager;
  private ProjectHighlightInfoManager projectHighlightInfoManager;

  private SectionStatusManager sectionStatusManager;
  private ProjectManager projectManager;

  // Model for the back-end
  private Project project;

  // Model for the front-end
  private long projectID;
  private long higlightID;

  private List<Integer> allYears;

  @Inject
  public ProjectHighListAction(APConfig config, ProjectHighligthManager projectHighligthManager,
    SectionStatusManager sectionStatusManager, ProjectManager projectManager,
    ProjectHighlightInfoManager projectHighlightInfoManager) {
    super(config);
    this.projectHighligthManager = projectHighligthManager;
    this.sectionStatusManager = sectionStatusManager;
    this.projectManager = projectManager;
    this.projectHighlightInfoManager = projectHighlightInfoManager;
  }

  @Override
  public String add() {
    ProjectHighlight projectHighlight = new ProjectHighlight();
    // newDeliverable.setType(deliverableTypeManager.getDeliverableSubTypes().get(0));
    projectHighlight.setModifiedBy(this.getCurrentUser());
    projectHighlight.setActiveSince(new Date());
    // newDeliverable.setContributor("");
    // newDeliverable.setCoverage("");
    projectHighlight.setCreatedBy(this.getCurrentUser());


    projectHighlight.setActive(true);
    // newDeliverable.setIsGlobal(false);
    // newDeliverable.setLeader(0);
    // newDeliverable.setObjectives("");
    projectHighlight.setProject(project);
    // newDeliverable.setPublisher("");
    // newDeliverable.setRelation("");
    // newDeliverable.setRights("");
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, this.getCurrentCycleYear());
    cal.set(Calendar.DAY_OF_YEAR, 1);
    Date start = cal.getTime();

    cal.set(Calendar.YEAR, this.getCurrentCycleYear());
    cal.set(Calendar.MONTH, 11); // 11 = december
    cal.set(Calendar.DAY_OF_MONTH, 31); // new years eve


    projectHighlight.setModificationJustification("");

    projectHighlight = projectHighligthManager.saveProjectHighligth(projectHighlight);
    ProjectHighlightInfo projectHighlightInfo = new ProjectHighlightInfo();
    projectHighlightInfo.setKeywords("");
    projectHighlightInfo.setStartDate(start);
    projectHighlightInfo.setYear(new Long(this.getCurrentCycleYear()));

    projectHighlightInfo.setAuthor("");

    projectHighlightInfo.setDescription("");
    projectHighlightInfo.setLinks("");
    projectHighlightInfo.setPartners("");
    projectHighlightInfo.setResults("");
    projectHighlightInfo.setEndDate(cal.getTime());
    projectHighlightInfo.setStatus(new Long(1));
    projectHighlightInfo.setSubject("");
    projectHighlightInfo.setTitle("");
    projectHighlightInfo.setProjectHighlight(projectHighlight);
    projectHighlightInfo.setPhase(this.getActualPhase());
    projectHighlightInfoManager.saveProjectHighlightInfo(projectHighlightInfo);
    higlightID = projectHighlight.getId();

    if (higlightID > 0) {
      return SUCCESS;
    }

    return INPUT;
  }

  @Override
  public String delete() {
    // Deleting deliverable.
    for (ProjectHighlight projectHighlight : project.getHighligths()) {
      if (projectHighlight.getId().longValue() == higlightID) {

        ProjectHighlight projectHighlightBD = projectHighligthManager.getProjectHighligthById(higlightID);

        for (SectionStatus sectionStatus : projectHighlightBD.getSectionStatuses()) {
          sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
        }
        projectHighligthManager.deleteProjectHighligth(projectHighlight.getId());


      }
    }
    return SUCCESS;
  }

  @Override
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


    allYears = project.getProjecInfoPhase(this.getActualPhase()).getAllYears();

    // Getting the List of Expected Deliverables

    List<ProjectHighlight> hightLihglihts =
      project.getProjectHighligths().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    project.setHighligths(new ArrayList<ProjectHighlight>());
    for (ProjectHighlight projectHighlight : hightLihglihts) {
      if (projectHighlight.getProjectHighlightInfo(this.getActualPhase()) != null) {
        project.getHighligths().add(projectHighlight);
      }
    }
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
