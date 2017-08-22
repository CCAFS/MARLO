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

package org.cgiar.ccafs.marlo.action.center.monitoring.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ICenterAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectCrosscutingThemeManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.impl.CenterProjectManager;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.CenterLeader;
import org.cgiar.ccafs.marlo.data.model.CenterLeaderTypeEnum;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterProjectCrosscutingTheme;
import org.cgiar.ccafs.marlo.data.model.CenterProjectStatus;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectListAction extends BaseAction {


  private static final long serialVersionUID = -5994329141897042670L;


  private long areaID;

  private ICenterManager centerService;


  private ICenterProjectCrosscutingThemeManager projectCrosscutingService;

  private Center loggedCenter;
  private long programID;
  private ICenterProgramManager programService;
  private long projectID;
  private List<CenterProject> projects;
  private CenterProjectManager projectService;
  private List<CenterArea> researchAreas;

  private ICenterAreaManager researchAreaService;
  private List<CenterProgram> researchPrograms;
  private CenterProgram selectedProgram;
  private CenterArea selectedResearchArea;
  private UserManager userService;
  private String justification;

  @Inject
  public ProjectListAction(APConfig config, ICenterManager centerService, ICenterProgramManager programService,
    CenterProjectManager projectService, UserManager userService, ICenterAreaManager researchAreaService,
    ICenterProjectCrosscutingThemeManager projectCrosscutingService) {
    super(config);
    this.centerService = centerService;
    this.programService = programService;
    this.projectService = projectService;
    this.userService = userService;
    this.researchAreaService = researchAreaService;
    this.projectCrosscutingService = projectCrosscutingService;
  }

  @Override
  public String add() {

    CenterProject project = new CenterProject();
    project.setActive(true);
    project.setActiveSince(new Date());
    project.setCreatedBy(this.getCurrentUser());
    project.setModifiedBy(this.getCurrentUser());
    project.setStartDate(new Date());
    project.setDateCreated(new Date());
    project.setResearchProgram(selectedProgram);
    project.setProjectStatus(new CenterProjectStatus(new Long(2), true));


    CenterProjectCrosscutingTheme projectCrosscutingTheme = new CenterProjectCrosscutingTheme();


    projectCrosscutingTheme.setActive(true);
    projectCrosscutingTheme.setActiveSince(new Date());
    projectCrosscutingTheme.setCreatedBy(this.getCurrentUser());
    projectCrosscutingTheme.setModifiedBy(this.getCurrentUser());
    projectCrosscutingTheme.setModificationJustification("");

    projectCrosscutingTheme.setClimateChange(false);
    projectCrosscutingTheme.setGender(false);
    projectCrosscutingTheme.setYouth(false);
    projectCrosscutingTheme.setPoliciesInstitutions(false);
    projectCrosscutingTheme.setCapacityDevelopment(false);
    projectCrosscutingTheme.setBigData(false);
    projectCrosscutingTheme.setImpactAssessment(false);
    projectCrosscutingTheme.setNa(false);

    project.setProjectCrosscutingTheme(projectCrosscutingTheme);
    projectCrosscutingTheme.setProject(project);

    project = projectService.saveCenterProject(project);
    projectID = project.getId();

    if (projectID > 0) {
      return SUCCESS;
    } else {
      return NOT_FOUND;
    }


  }

  @Override
  public String delete() {
    Map<String, Object> parameters = this.getParameters();
    projectID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_ID))[0]));

    CenterProject project = projectService.getCenterProjectById(projectID);

    if (project != null) {
      programID = project.getResearchProgram().getId();
      project.setModificationJustification(
        this.getJustification() == null ? "CenterProject deleted" : this.getJustification());
      project.setModifiedBy(this.getCurrentUser());

      projectService.saveCenterProject(project);

      projectService.deleteCenterProject(project.getId());

      this.addActionMessage("message:" + this.getText("deleting.success"));
    }

    return SUCCESS;
  }

  public long getAreaID() {
    return areaID;
  }

  @Override
  public String getJustification() {
    return justification;
  }

  public Center getLoggedCenter() {
    return loggedCenter;
  }


  public long getProgramID() {
    return programID;
  }


  public long getProjectID() {
    return projectID;
  }


  public List<CenterProject> getProjects() {
    return projects;
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


    areaID = -1;
    programID = -1;

    loggedCenter = (Center) this.getSession().get(APConstants.SESSION_CENTER);
    loggedCenter = centerService.getCrpById(loggedCenter.getId());

    researchAreas = new ArrayList<>(
      loggedCenter.getResearchAreas().stream().filter(ra -> ra.isActive()).collect(Collectors.toList()));

    Collections.sort(researchAreas, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));

    if (researchAreas != null) {

      try {
        areaID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CENTER_AREA_ID)));
      } catch (Exception e) {
        try {
          programID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CENTER_PROGRAM_ID)));
        } catch (Exception ex) {
          User user = userService.getUser(this.getCurrentUser().getId());

          List<CenterLeader> userAreaLeads = new ArrayList<>(user.getResearchLeaders().stream()
            .filter(
              rl -> rl.isActive() && rl.getType().getId() == CenterLeaderTypeEnum.RESEARCH_AREA_LEADER_TYPE.getValue())
            .collect(Collectors.toList()));
          if (!userAreaLeads.isEmpty()) {
            areaID = userAreaLeads.get(0).getResearchArea().getId();
          } else {
            List<CenterLeader> userProgramLeads = new ArrayList<>(user.getResearchLeaders().stream()
              .filter(rl -> rl.isActive()
                && rl.getType().getId() == CenterLeaderTypeEnum.RESEARCH_PROGRAM_LEADER_TYPE.getValue())
              .collect(Collectors.toList()));
            if (!userProgramLeads.isEmpty()) {
              programID = userProgramLeads.get(0).getResearchProgram().getId();
            } else {
              List<CenterProgram> rps = researchAreas.get(0).getResearchPrograms().stream().filter(r -> r.isActive())
                .collect(Collectors.toList());
              Collections.sort(rps, (rp1, rp2) -> rp1.getId().compareTo(rp2.getId()));
              CenterProgram rp = rps.get(0);
              programID = rp.getId();
              areaID = rp.getResearchArea().getId();
            }
          }
        }
      }

      if (areaID != -1 && programID == -1) {
        selectedResearchArea = researchAreaService.find(areaID);
        researchPrograms = new ArrayList<>(
          selectedResearchArea.getResearchPrograms().stream().filter(rp -> rp.isActive()).collect(Collectors.toList()));
        Collections.sort(researchPrograms, (rp1, rp2) -> rp1.getId().compareTo(rp2.getId()));
        if (researchPrograms != null) {
          try {
            programID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CENTER_PROGRAM_ID)));
          } catch (Exception e) {
            User user = userService.getUser(this.getCurrentUser().getId());
            List<CenterLeader> userLeads = new ArrayList<>(user.getResearchLeaders().stream()
              .filter(rl -> rl.isActive()
                && rl.getType().getId() == CenterLeaderTypeEnum.RESEARCH_PROGRAM_LEADER_TYPE.getValue())
              .collect(Collectors.toList()));

            if (!userLeads.isEmpty()) {
              programID = userLeads.get(0).getResearchProgram().getId();
            } else {
              if (!researchPrograms.isEmpty()) {
                programID = researchPrograms.get(0).getId();
              }
            }
          }
        }


        if (programID != -1) {
          selectedProgram = programService.getProgramById(programID);
        }

      } else {

        if (programID != -1) {
          selectedProgram = programService.getProgramById(programID);
          areaID = selectedProgram.getResearchArea().getId();
          selectedResearchArea = researchAreaService.find(areaID);
        }

      }

      projects =
        new ArrayList<>(selectedProgram.getProjects().stream().filter(p -> p.isActive()).collect(Collectors.toList()));

      String params[] = {loggedCenter.getAcronym(), selectedResearchArea.getId() + "", selectedProgram.getId() + ""};
      this.setBasePermission(this.getText(Permission.RESEARCH_PROGRAM_BASE_PERMISSION, params));

    }

  }

  public void setAreaID(long areaID) {
    this.areaID = areaID;
  }

  @Override
  public void setJustification(String justification) {
    this.justification = justification;
  }

  public void setLoggedCenter(Center loggedCenter) {
    this.loggedCenter = loggedCenter;
  }

  public void setProgramID(long programID) {
    this.programID = programID;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setProjects(List<CenterProject> projects) {
    this.projects = projects;
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
