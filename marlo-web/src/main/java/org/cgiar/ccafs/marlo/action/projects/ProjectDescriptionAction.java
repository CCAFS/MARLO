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

package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 * @author Christian Garcia- CIAT/CCAFS
 */
public class ProjectDescriptionAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  // Managers
  private ProjectManager projectManager;
  private ProjectFocusManager projectFocusManager;
  private CrpManager crpManager;
  private CrpProgramManager programManager;
  /*
   * private LiaisonInstitutionManager liaisonInstitutionManager;
   * private LiaisonUserManager liaisonUserManager;
   * private UserManager userManager;
   */

  // Front-end
  private long projectID;
  private Crp loggedCrp;
  private Project project;
  private List<CrpProgram> programFlagships;
  private List<LiaisonInstitution> liaisonInstitutions;
  private Map<String, String> projectStauses;
  private List<LiaisonUser> allOwners;
  private Map<String, String> projectTypes;

  private File file;
  private File fileReporting;
  private String fileContentType;
  private String fileFileName;
  private String fileReportingFileName;


  @Inject
  public ProjectDescriptionAction(APConfig config, ProjectManager projectManager, CrpManager crpManager,
    CrpProgramManager programManager, LiaisonUserManager liaisonUserManager,
    LiaisonInstitutionManager liaisonInstitutionManager, UserManager userManager,
    ProjectFocusManager projectFocusManager) {
    super(config);
    this.projectManager = projectManager;
    this.programManager = programManager;
    this.crpManager = crpManager;
    // this.userManager = userManager;
    // this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.projectManager = projectManager;
    this.projectFocusManager = projectFocusManager;
    // this.liaisonUserManager = liaisonUserManager;
  }


  public List<LiaisonUser> getAllOwners() {
    return allOwners;
  }


  public File getFile() {
    return file;
  }


  public String getFileContentType() {
    return fileContentType;
  }


  public String getFileFileName() {
    return fileFileName;
  }


  public File getFileReporting() {
    return fileReporting;
  }


  public String getFileReportingFileName() {
    return fileReportingFileName;
  }


  /**
   * This method returns an array of flagship ids depending on the project.flagships attribute.
   * 
   * @return an array of integers.
   */
  public long[] getFlagshipIds() {

    List<ProjectFocus> projectFocuses = project.getProjectFocuses().stream()
      .filter(c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());

    if (projectFocuses != null) {
      long[] ids = new long[projectFocuses.size()];
      for (int c = 0; c < ids.length; c++) {
        ids[c] = projectFocuses.get(c).getCrpProgram().getId();
      }
      return ids;
    }
    return null;
  }


  public List<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }


  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  public List<CrpProgram> getProgramFlagships() {
    return programFlagships;
  }


  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }


  public Map<String, String> getProjectStauses() {
    return projectStauses;
  }


  public Map<String, String> getProjectTypes() {
    return projectTypes;
  }

  @Override
  public void prepare() throws Exception {

    // Get current CRP
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    } catch (Exception e) {

    }
    project = projectManager.getProjectById(projectID);
    allOwners = new ArrayList<LiaisonUser>();
    allOwners.addAll(loggedCrp.getLiasonUsers());
    liaisonInstitutions = new ArrayList<LiaisonInstitution>();
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions());
    programFlagships = new ArrayList<>();
    programFlagships.addAll(loggedCrp.getCrpPrograms().stream()
      .filter(c -> c.isActive() && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList()));
    List<CrpProgram> programs = new ArrayList<>();
    for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
      .filter(c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList())) {
      programs.add(projectFocuses.getCrpProgram());
    }
    project.setFlagships(programs);

    projectTypes = new HashMap<>();
    projectTypes.put(APConstants.PROJECT_CORE, this.getText("project.projectType.core"));
    projectTypes.put(APConstants.PROJECT_BILATERAL, this.getText("project.projectType.bilateral"));
    projectTypes.put(APConstants.PROJECT_CCAFS_COFUNDED, this.getText("project.projectType.cofounded"));


    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_DESCRIPTION_BASE_PERMISSION, params));

  }


  @Override
  public String save() {

    if (this.hasPermission("canEdit")) {

      Project projectDB = projectManager.getProjectById(project.getId());
      project.setActive(true);
      project.setCreatedBy(projectDB.getCreatedBy());
      project.setModifiedBy(this.getCurrentUser());
      project.setModificationJustification("");
      project.setActiveSince(projectDB.getActiveSince());
      projectManager.saveProject(project);

      for (String programID : project.getFlagshipValue().trim().split(",")) {
        CrpProgram program = programManager.getCrpProgramById(Long.parseLong(programID));
        ProjectFocus projectFocus = new ProjectFocus();
        projectFocus.setCrpProgram(program);
        projectFocus.setProject(project);
        if (!projectDB.getProjectFocuses().contains(projectFocus)) {
          projectFocus.setActive(true);
          projectFocus.setActiveSince(new Date());
          projectFocus.setCreatedBy(this.getCurrentUser());
          projectFocus.setModifiedBy(this.getCurrentUser());
          projectFocus.setModificationJustification("");
          projectFocusManager.saveProjectFocus(projectFocus);
        }
      }

      return SUCCESS;
    } else {
      projectManager.saveProject(project);
      return SUCCESS;
    }

  }


  public void setAllOwners(List<LiaisonUser> allOwners) {
    this.allOwners = allOwners;
  }


  public void setFile(File file) {
    this.file = file;
  }


  public void setFileContentType(String fileContentType) {
    this.fileContentType = fileContentType;
  }


  public void setFileFileName(String fileFileName) {
    this.fileFileName = fileFileName;
  }


  public void setFileReporting(File fileReporting) {
    this.fileReporting = fileReporting;
  }


  public void setFileReportingFileName(String fileReportingFileName) {
    this.fileReportingFileName = fileReportingFileName;
  }


  public void setLiaisonInstitutions(List<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }


  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setProgramFlagships(List<CrpProgram> programFlagships) {
    this.programFlagships = programFlagships;
  }


  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setProjectStauses(Map<String, String> projectStauses) {
    this.projectStauses = projectStauses;
  }

  public void setProjectTypes(Map<String, String> projectTypes) {
    this.projectTypes = projectTypes;
  }


  @Override
  public void validate() {
    if (save) {

    }
  }

}