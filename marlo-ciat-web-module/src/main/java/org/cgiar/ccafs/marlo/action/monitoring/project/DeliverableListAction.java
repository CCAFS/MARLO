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

package org.cgiar.ccafs.marlo.action.monitoring.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConfig;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableCrosscutingTheme;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterProjectStatus;
import org.cgiar.ccafs.marlo.data.service.ICenterDeliverableManager;
import org.cgiar.ccafs.marlo.data.service.ICenterProjectManager;
import org.cgiar.ccafs.marlo.data.service.ICenterManager;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverableListAction extends BaseAction {

  private static final long serialVersionUID = 2828289817791232470L;

  private ICenterManager centerService;
  private ICenterProjectManager projectService;
  private ICenterDeliverableManager deliverableService;


  private CenterArea selectedResearchArea;
  private CenterProgram selectedProgram;
  private Center loggedCenter;
  private CenterProject project;
  private List<CenterDeliverable> deliverables;
  private List<CenterArea> researchAreas;
  private List<CenterProgram> researchPrograms;
  private long programID;
  private long areaID;
  private long projectID;
  private long deliverableID;

  @Inject
  public DeliverableListAction(APConfig config, ICenterManager centerService, ICenterProjectManager projectService,
    ICenterDeliverableManager deliverableService) {
    super(config);
    this.centerService = centerService;
    this.projectService = projectService;
    this.deliverableService = deliverableService;
  }

  @Override
  public String add() {

    CenterDeliverable deliverable = new CenterDeliverable();


    deliverable.setActive(true);
    deliverable.setActiveSince(new Date());
    deliverable.setCreatedBy(this.getCurrentUser());
    deliverable.setModifiedBy(this.getCurrentUser());
    deliverable.setStartDate(new Date());
    deliverable.setDateCreated(new Date());
    deliverable.setProject(project);
    deliverable.setProjectStatus(new CenterProjectStatus(new Long(2), true));


    CenterDeliverableCrosscutingTheme deliverableCrosscutingTheme = new CenterDeliverableCrosscutingTheme();

    deliverableCrosscutingTheme.setActive(true);
    deliverableCrosscutingTheme.setActiveSince(new Date());
    deliverableCrosscutingTheme.setCreatedBy(this.getCurrentUser());
    deliverableCrosscutingTheme.setModifiedBy(this.getCurrentUser());
    deliverableCrosscutingTheme.setModificationJustification("");

    deliverableCrosscutingTheme.setClimateChange(false);
    deliverableCrosscutingTheme.setGender(false);
    deliverableCrosscutingTheme.setYouth(false);
    deliverableCrosscutingTheme.setPoliciesInstitutions(false);
    deliverableCrosscutingTheme.setCapacityDevelopment(false);
    deliverableCrosscutingTheme.setBigData(false);
    deliverableCrosscutingTheme.setImpactAssessment(false);
    deliverableCrosscutingTheme.setNa(false);

    deliverable.setDeliverableCrosscutingTheme(deliverableCrosscutingTheme);
    deliverableCrosscutingTheme.setDeliverable(deliverable);

    deliverableID = deliverableService.saveDeliverable(deliverable);

    if (deliverableID > 0) {
      return SUCCESS;
    } else {
      return NOT_FOUND;
    }
  }

  public long getAreaID() {
    return areaID;
  }

  public long getDeliverableID() {
    return deliverableID;
  }

  public List<CenterDeliverable> getDeliverables() {
    return deliverables;
  }

  public Center getLoggedCenter() {
    return loggedCenter;
  }

  public long getProgramID() {
    return programID;
  }


  public CenterProject getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }


  public List<CenterArea> getResearchAreas() {
    return researchAreas;
  }


  public List<CenterProgram> getResearchPrograms() {
    return researchPrograms;
  }

  public CenterProgram getSelectedProgram() {
    return selectedProgram;
  }

  public CenterArea getSelectedResearchArea() {
    return selectedResearchArea;
  }

  @Override
  public void prepare() throws Exception {
    loggedCenter = (Center) this.getSession().get(APConstants.SESSION_CENTER);
    loggedCenter = centerService.getCrpById(loggedCenter.getId());

    researchAreas = new ArrayList<>(
      loggedCenter.getResearchAreas().stream().filter(ra -> ra.isActive()).collect(Collectors.toList()));

    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_ID)));
    } catch (Exception e) {
      projectID = -1;
    }

    project = projectService.getCenterProjectById(projectID);

    if (project != null) {

      selectedProgram = project.getResearchProgram();
      programID = selectedProgram.getId();
      selectedResearchArea = selectedProgram.getResearchArea();
      areaID = selectedResearchArea.getId();
      researchPrograms = new ArrayList<>(
        selectedResearchArea.getResearchPrograms().stream().filter(rp -> rp.isActive()).collect(Collectors.toList()));

      deliverables =
        new ArrayList<>(project.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList()));

      String params[] =
        {loggedCenter.getAcronym(), selectedResearchArea.getId() + "", selectedProgram.getId() + "", projectID + ""};
      this.setBasePermission(this.getText(Permission.PROJECT_BASE_PERMISSION, params));

    }
  }

  public void setAreaID(long areaID) {
    this.areaID = areaID;
  }

  public void setDeliverableID(long deliverableID) {
    this.deliverableID = deliverableID;
  }

  public void setDeliverables(List<CenterDeliverable> deliverables) {
    this.deliverables = deliverables;
  }

  public void setLoggedCenter(Center loggedCenter) {
    this.loggedCenter = loggedCenter;
  }

  public void setProgramID(long programID) {
    this.programID = programID;
  }

  public void setProject(CenterProject project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setResearchAreas(List<CenterArea> researchAreas) {
    this.researchAreas = researchAreas;
  }

  public void setResearchPrograms(List<CenterProgram> researchPrograms) {
    this.researchPrograms = researchPrograms;
  }

  public void setSelectedProgram(CenterProgram selectedProgram) {
    this.selectedProgram = selectedProgram;
  }

  public void setSelectedResearchArea(CenterArea selectedResearchArea) {
    this.selectedResearchArea = selectedResearchArea;
  }

}
