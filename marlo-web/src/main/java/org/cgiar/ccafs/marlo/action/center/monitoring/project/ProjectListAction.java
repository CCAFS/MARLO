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
import org.cgiar.ccafs.marlo.data.manager.CenterFundingSyncTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterFundingSourceTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectCrosscutingThemeManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectFundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectLocationManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.impl.CenterProjectManager;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.CenterFundingSourceType;
import org.cgiar.ccafs.marlo.data.model.CenterFundingSyncType;
import org.cgiar.ccafs.marlo.data.model.CenterLeader;
import org.cgiar.ccafs.marlo.data.model.CenterLeaderTypeEnum;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterProjectCrosscutingTheme;
import org.cgiar.ccafs.marlo.data.model.CenterProjectFundingSource;
import org.cgiar.ccafs.marlo.data.model.CenterProjectLocation;
import org.cgiar.ccafs.marlo.data.model.CenterProjectPartner;
import org.cgiar.ccafs.marlo.data.model.CenterProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.CenterProjectStatus;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.ocs.model.AgreementOCS;
import org.cgiar.ccafs.marlo.ocs.ws.MarloOcsClient;
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

  private long syncTypeID;


  private String syncCode;
  private ICenterManager centerService;
  private ICenterProjectCrosscutingThemeManager projectCrosscutingService;
  private ICenterProjectLocationManager projectLocationService;
  private ICenterProjectFundingSourceManager centerProjectFudingSourceManager;
  private ICenterFundingSourceTypeManager centerFundingTypeManager;
  private ICenterProjectPartnerManager partnerService;
  private ICenterProjectPartnerPersonManager partnerPersonService;
  private CenterFundingSyncTypeManager fundingSyncTypeManager;
  private Center loggedCenter;
  private long programID;
  private ICenterProgramManager programService;
  private ProjectManager projectManager;
  private long projectID;
  private List<CenterFundingSyncType> syncTypes;
  private List<CenterProject> projects;

  private CenterProjectManager projectService;
  private List<CenterArea> researchAreas;
  private ICenterAreaManager researchAreaService;
  private List<CenterProgram> researchPrograms;
  private CenterProgram selectedProgram;
  private CenterArea selectedResearchArea;
  private UserManager userService;
  private String justification;

  // OCS Agreement Servcie Class
  private MarloOcsClient ocsClient;
  private AgreementOCS agreement;

  @Inject
  public ProjectListAction(APConfig config, ICenterManager centerService, ICenterProgramManager programService,
    CenterProjectManager projectService, UserManager userService, ICenterAreaManager researchAreaService,
    ICenterProjectCrosscutingThemeManager projectCrosscutingService, MarloOcsClient ocsClient,
    ProjectManager projectManager, ICenterProjectFundingSourceManager centerProjectFudingSourceManager,
    CenterFundingSyncTypeManager fundingSyncTypeManager, ICenterFundingSourceTypeManager centerFundingTypeManager,
    ICenterProjectLocationManager projectLocationService, ICenterProjectPartnerManager partnerService,
    ICenterProjectPartnerPersonManager partnerPersonService) {
    super(config);
    this.centerService = centerService;
    this.programService = programService;
    this.projectService = projectService;
    this.userService = userService;
    this.researchAreaService = researchAreaService;
    this.projectCrosscutingService = projectCrosscutingService;
    this.ocsClient = ocsClient;
    this.projectManager = projectManager;
    this.centerProjectFudingSourceManager = centerProjectFudingSourceManager;
    this.fundingSyncTypeManager = fundingSyncTypeManager;
    this.centerFundingTypeManager = centerFundingTypeManager;
    this.projectLocationService = projectLocationService;
    this.partnerService = partnerService;
    this.partnerPersonService = partnerPersonService;

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
    project.setAutoFill(false);


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

    projectID = projectService.saveCenterProject(project).getId();

    /**
     * Add Project sync information
     */
    Map<String, Object> parameters = this.getParameters();
    syncTypeID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.CENTER_PROJECT_SYNC_TYPE))[0]));
    syncCode = StringUtils.trim(((String[]) parameters.get(APConstants.CENTER_PROJECT_SYNC_CODE))[0]);


    switch (Math.toIntExact(syncTypeID)) {
      case 1:
        this.addOcsProjectInformation(projectID);
        break;
      case 2:
        if (syncCode.toUpperCase().contains("P")) {
          syncCode = syncCode.toUpperCase().replaceFirst("P", "");
        }
        this.addCrpProjectInformation(projectID);
        break;
    }

    if (projectID > 0) {
      return SUCCESS;
    } else {
      return NOT_FOUND;
    }


  }

  /**
   * Add CRP project information in the center project Created.
   * 
   * @param centerProjectID
   */
  public void addCrpProjectInformation(long centerProjectID) {

    long pID = Long.parseLong(syncCode);
    Project project = projectManager.getProjectById(pID);

    CenterProject centerProject = projectService.getCenterProjectById(centerProjectID);

    centerProject.setName(project.getTitle());
    centerProject.setDescription(project.getSummary());
    centerProject.setStartDate(project.getStartDate());
    centerProject.setEndDate(project.getEndDate());
    centerProject.setProjectLeader(project.getLeaderPerson().getUser());
    centerProject.setSync(true);
    centerProject.setSyncDate(new Date());
    centerProject.setAutoFill(true);

    // Add Project Status
    centerProject.setProjectStatus(new CenterProjectStatus(project.getStatus(), true));

    // Add Crp Project CrossCutting to Center Project
    this.crpCrossCuttingInformation(project, centerProject);

    // Add Crp Project Locations to Center Project
    this.crpProjectLocation(project, centerProject);

    // Add Crp Project Partners to Center Project
    this.crpProjectPartners(project, centerProject);

    projectService.saveCenterProject(centerProject);

    CenterProjectFundingSource fundingSource = new CenterProjectFundingSource();

    fundingSource.setCenterProject(centerProject);
    fundingSource.setCode("P" + syncCode);
    fundingSource.setSync(true);
    fundingSource.setSyncDate(new Date());
    fundingSource.setAutoFill(true);
    fundingSource.setCrp(project.getCrp());
    fundingSource.setTitle(project.getTitle());
    fundingSource.setDescription(project.getSummary());
    fundingSource.setStartDate(project.getStartDate());
    fundingSource.setEndDate(project.getEndDate());

    // Setting the sync type (2 = MARLO-CRP)
    CenterFundingSyncType fundingSyncType = fundingSyncTypeManager.getCenterFundingSyncTypeById(2);
    fundingSource.setCenterFundingSyncType(fundingSyncType);

    fundingSource.setActive(true);
    fundingSource.setCreatedBy(this.getCurrentUser());
    fundingSource.setModifiedBy(this.getCurrentUser());
    fundingSource.setActiveSince(new Date());

    centerProjectFudingSourceManager.saveProjectFundingSource(fundingSource);

  }

  /**
   * Add OCS project information in the center project Created.
   * 
   * @param centerProjectID
   */
  public void addOcsProjectInformation(long centerProjectID) {

    agreement = ocsClient.getagreement(syncCode);

    CenterProject centerProject = projectService.getCenterProjectById(centerProjectID);

    centerProject.setName(agreement.getDescription());
    centerProject.setDescription(agreement.getDescription());
    centerProject.setStartDate(agreement.getStartDate());
    centerProject.setEndDate(agreement.getEndDate());
    centerProject.setSync(true);
    centerProject.setSyncDate(new Date());
    centerProject.setAutoFill(true);

    projectService.saveCenterProject(centerProject);

    CenterProjectFundingSource fundingSource = new CenterProjectFundingSource();

    fundingSource.setCenterProject(centerProject);
    fundingSource.setCode(syncCode);
    fundingSource.setSync(true);
    fundingSource.setSyncDate(new Date());
    fundingSource.setAutoFill(true);

    fundingSource.setTitle(agreement.getDescription());
    fundingSource.setDescription(agreement.getDescription());

    try {
      fundingSource.setStartDate(agreement.getStartDate());
    } catch (Exception e) {
      // OCS sends a bad Date format
      fundingSource.setStartDate(null);
    }
    try {
      fundingSource.setEndDate(agreement.getEndDate());
    } catch (Exception e) {
      // OCS sends a bad Date format
      fundingSource.setEndDate(null);
    }
    try {

      if (agreement.getExtensionDate().after(agreement.getEndDate())) {
        fundingSource.setExtensionDate(agreement.getExtensionDate());
      } else {
        fundingSource.setExtensionDate(null);
      }
    } catch (Exception e) {
      // OCS sends a bad Date format
      fundingSource.setExtensionDate(null);
    }

    if (agreement.getOriginalDonor() != null) {
      fundingSource.setOriginalDonor(agreement.getOriginalDonor().getName());
    }
    if (agreement.getDirectDonor() != null) {
      fundingSource.setDirectDonor(agreement.getDirectDonor().getName());
    }
    fundingSource.setTotalAmount(Double.parseDouble(agreement.getGrantAmount()));

    // Setting the sync type (1 = OCS CIAT)
    CenterFundingSyncType fundingSyncType = fundingSyncTypeManager.getCenterFundingSyncTypeById(1);
    fundingSource.setCenterFundingSyncType(fundingSyncType);

    // Setting the budget Type
    String fundingType = agreement.getFundingType();
    long fundingtypeID = -1L;
    if (fundingType != null) {
      switch (fundingType) {
        case "BLR":
          fundingtypeID = 3;
          break;
        case "W1/W2":
          fundingtypeID = 1;
          break;
        case "W3R":
          fundingtypeID = 2;
          break;
        case "W3U":
          fundingtypeID = 2;
          break;
        default:
          fundingtypeID = -1L;
          break;
      }
    }

    CenterFundingSourceType fundingSourceType = centerFundingTypeManager.getFundingSourceTypeById(fundingtypeID);
    fundingSource.setCenterFundingSourceType(fundingSourceType);

    fundingSource.setActive(true);
    fundingSource.setCreatedBy(this.getCurrentUser());
    fundingSource.setModifiedBy(this.getCurrentUser());
    fundingSource.setActiveSince(new Date());

    centerProjectFudingSourceManager.saveProjectFundingSource(fundingSource);

  }


  /**
   * Add CRP project Cross-cutting information in the center project Created.
   * 
   * @param project
   * @param centerProject
   */

  public void crpCrossCuttingInformation(Project project, CenterProject centerProject) {

    boolean hasChanges = false;

    CenterProjectCrosscutingTheme crosscutingThemeSave = centerProject.getProjectCrosscutingTheme();

    if (project.getCrossCuttingGender() != null && project.getCrossCuttingGender()) {
      hasChanges = true;
      crosscutingThemeSave.setGender(true);
    }

    if (project.getCrossCuttingYouth() != null && project.getCrossCuttingYouth()) {
      hasChanges = true;
      crosscutingThemeSave.setYouth(true);
    }

    if (project.getCrossCuttingCapacity() != null && project.getCrossCuttingCapacity()) {
      hasChanges = true;
      crosscutingThemeSave.setCapacityDevelopment(true);
    }

    if (hasChanges) {
      crosscutingThemeSave.setProject(centerProject);
      projectCrosscutingService.saveProjectCrosscutingTheme(crosscutingThemeSave);
    }


  }

  /**
   * Add CRP project location information in the center project Created.
   * 
   * @param project
   * @param centerProject
   */

  public void crpProjectLocation(Project project, CenterProject centerProject) {

    List<ProjectLocation> projectLocations = new ArrayList<>(project.getProjectLocations().stream()
      .filter(pl -> pl.isActive()
        && (pl.getLocElement().getLocElementType().getId() == 1 || pl.getLocElement().getLocElementType().getId() == 2))
      .collect(Collectors.toList()));

    boolean haveRegion = false;


    for (ProjectLocation projectLocation : projectLocations) {


      CenterProjectLocation centerProjectLocation = new CenterProjectLocation();


      centerProjectLocation.setActive(true);
      centerProjectLocation.setActiveSince(new Date());
      centerProjectLocation.setCreatedBy(this.getCurrentUser());
      centerProjectLocation.setModifiedBy(this.getCurrentUser());
      centerProjectLocation.setModificationJustification("");
      centerProjectLocation.setProject(centerProject);

      centerProjectLocation.setLocElement(projectLocation.getLocElement());

      projectLocationService.saveProjectLocation(centerProjectLocation);

      if (centerProjectLocation.getLocElement().getLocElementType().getId() == 1) {
        haveRegion = true;
      }

    }

    if (haveRegion) {
      centerProject.setRegion(true);
      projectService.saveCenterProject(centerProject);
    }

  }

  /**
   * Add CRP project partners information in the center project Created - Only the parters that collaborate by the
   * center.
   * 
   * @param project
   * @param centerProject
   */
  public void crpProjectPartners(Project project, CenterProject centerProject) {


    List<ProjectPartner> projectPartners =
      new ArrayList<>(project.getProjectPartners().stream().filter(pp -> pp.isActive()).collect(Collectors.toList()));

    for (ProjectPartner projectPartner : projectPartners) {

      // TODO fix the filter to work whit all centers (in the future)
      List<ProjectPartnerContribution> contributions = new ArrayList<>(projectPartner.getProjectPartnerContributions()
        .stream().filter(pc -> pc.isActive() && pc.getProjectPartnerContributor().getInstitution().getId() == 46)
        .collect(Collectors.toList()));

      for (ProjectPartnerContribution projectPartnerContribution : contributions) {


        CenterProjectPartner partnerNew = new CenterProjectPartner();
        partnerNew.setActive(true);
        partnerNew.setActiveSince(new Date());
        partnerNew.setCreatedBy(this.getCurrentUser());
        partnerNew.setModifiedBy(this.getCurrentUser());
        partnerNew.setModificationJustification("");
        partnerNew.setProject(centerProject);

        Institution institution = new Institution();
        institution.setId(projectPartnerContribution.getProjectPartner().getInstitution().getId());
        partnerNew.setInstitution(institution);

        partnerService.saveProjectPartner(partnerNew);


        List<ProjectPartnerPerson> partnerPerson = new ArrayList<>(projectPartnerContribution.getProjectPartner()
          .getProjectPartnerPersons().stream().filter(pp -> pp.isActive()).collect(Collectors.toList()));

        for (ProjectPartnerPerson projectPartnerPerson : partnerPerson) {

          CenterProjectPartnerPerson partnerPersonNew = new CenterProjectPartnerPerson();
          partnerPersonNew.setActive(true);
          partnerPersonNew.setActiveSince(new Date());
          partnerPersonNew.setCreatedBy(this.getCurrentUser());
          partnerPersonNew.setModifiedBy(this.getCurrentUser());
          partnerPersonNew.setModificationJustification("");

          partnerPersonNew.setProjectPartner(partnerNew);

          User user = userService.getUser(projectPartnerPerson.getUser().getId());
          partnerPersonNew.setUser(user);

          partnerPersonService.saveProjectPartnerPerson(partnerPersonNew);

        }


      }

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


  public String getSyncCode() {
    return syncCode;
  }


  public long getSyncTypeID() {
    return syncTypeID;
  }

  public List<CenterFundingSyncType> getSyncTypes() {
    return syncTypes;
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

          // Check if the User is an Area Leader
          List<CenterLeader> userAreaLeads =
            new ArrayList<>(user.getResearchLeaders().stream()
              .filter(rl -> rl.isActive()
                && rl.getType().getId() == CenterLeaderTypeEnum.RESEARCH_AREA_LEADER_TYPE.getValue())
              .collect(Collectors.toList()));
          if (!userAreaLeads.isEmpty()) {
            areaID = userAreaLeads.get(0).getResearchArea().getId();
          } else {
            // Check if the User is a Program Leader
            List<CenterLeader> userProgramLeads = new ArrayList<>(user.getResearchLeaders().stream()
              .filter(rl -> rl.isActive()
                && rl.getType().getId() == CenterLeaderTypeEnum.RESEARCH_PROGRAM_LEADER_TYPE.getValue())
              .collect(Collectors.toList()));
            if (!userProgramLeads.isEmpty()) {
              programID = userProgramLeads.get(0).getResearchProgram().getId();
            } else {
              // Check if the User is a Scientist Leader
              List<CenterLeader> userScientistLeader = new ArrayList<>(user.getResearchLeaders().stream()
                .filter(rl -> rl.isActive()
                  && rl.getType().getId() == CenterLeaderTypeEnum.PROGRAM_SCIENTIST_LEADER_TYPE.getValue())
                .collect(Collectors.toList()));
              if (!userScientistLeader.isEmpty()) {
                programID = userScientistLeader.get(0).getResearchProgram().getId();
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

      syncTypes = new ArrayList<>(
        fundingSyncTypeManager.findAll().stream().filter(fs -> fs.isActive()).collect(Collectors.toList()));

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

  public void setSyncCode(String syncCode) {
    this.syncCode = syncCode;
  }

  public void setSyncTypeID(long syncTypeID) {
    this.syncTypeID = syncTypeID;
  }

  public void setSyncTypes(List<CenterFundingSyncType> syncTypes) {
    this.syncTypes = syncTypes;
  }

}
