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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterFundingSourceTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectCrosscutingThemeManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectFundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectLocationManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.impl.CenterProjectManager;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.CenterFundingSyncType;
import org.cgiar.ccafs.marlo.data.model.CenterLeader;
import org.cgiar.ccafs.marlo.data.model.CenterLeaderTypeEnum;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterProjectCrosscutingTheme;
import org.cgiar.ccafs.marlo.data.model.CenterProjectLocation;
import org.cgiar.ccafs.marlo.data.model.CenterProjectPartner;
import org.cgiar.ccafs.marlo.data.model.CenterProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.CenterProjectStatus;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
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

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectListAction extends BaseAction {


  private static final long serialVersionUID = -5994329141897042670L;

  private long areaID;
  private long syncTypeID;
  private String syncCode;

  // GlobalUnit Manager
  private GlobalUnitManager centerService;
  private GlobalUnitProjectManager globalUnitProjectManager;
  private ICenterProjectCrosscutingThemeManager projectCrosscutingService;
  private ICenterProjectLocationManager projectLocationService;
  private ICenterProjectFundingSourceManager centerProjectFudingSourceManager;
  private ICenterFundingSourceTypeManager centerFundingTypeManager;
  private ICenterProjectPartnerManager partnerService;
  private ICenterProjectPartnerPersonManager partnerPersonService;
  private CenterFundingSyncTypeManager fundingSyncTypeManager;
  private GlobalUnit loggedCenter;
  private long programID;
  private ICenterProgramManager programService;
  private ProjectManager projectManager;
  private long projectID;
  private List<CenterFundingSyncType> syncTypes;
  private List<Project> projects;
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

  // Phases globalUnit
  private PhaseManager phaseManager;
  private ProjectInfoManager projectInfoManager;

  @Inject
  public ProjectListAction(APConfig config, GlobalUnitManager centerService, ICenterProgramManager programService,
    CenterProjectManager projectService, UserManager userService, ICenterAreaManager researchAreaService,
    ICenterProjectCrosscutingThemeManager projectCrosscutingService, MarloOcsClient ocsClient,
    ProjectManager projectManager, ICenterProjectFundingSourceManager centerProjectFudingSourceManager,
    CenterFundingSyncTypeManager fundingSyncTypeManager, ICenterFundingSourceTypeManager centerFundingTypeManager,
    ICenterProjectLocationManager projectLocationService, ICenterProjectPartnerManager partnerService,
    ICenterProjectPartnerPersonManager partnerPersonService, GlobalUnitProjectManager globalUnitProjectManager,
    PhaseManager phaseManager, ProjectInfoManager projectInfoManager) {
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
    this.globalUnitProjectManager = globalUnitProjectManager;
    this.phaseManager = phaseManager;
    this.projectInfoManager = projectInfoManager;

  }

  @Override
  public String add() {

    /**
     * Add Project sync information
     */
    Map<String, Parameter> parameters = this.getParameters();
    syncTypeID =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.CENTER_PROJECT_SYNC_TYPE).getMultipleValues()[0]));
    syncCode = StringUtils.trim(parameters.get(APConstants.CENTER_PROJECT_SYNC_CODE).getMultipleValues()[0]);


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
      default:
        this.createEmptyProject();
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

    // Get The Crp/Center/Platform where the project was created
    GlobalUnitProject globalUnitProject = globalUnitProjectManager.findByProjectId(project.getId());

    // TODO add phase call the parameters
    GlobalUnit crp = globalUnitProject.getGlobalUnit();

    //
    // CustomParameter customParameter = crp.getCustomParameters().stream()
    // .filter(cp -> cp.isActive() && cp.getParameter().getKey().equals(APConstants.CRP_PLANNING_YEAR))
    // .collect(Collectors.toList()).get(0);

    Phase phase = this.getCenterCrpPhase(crp);

    CenterProject centerProject = this.createCenterProject(project, true);

    // Add Project Leader
    if (project.getLeaderPerson(phase).getUser() != null) {
      centerProject.setProjectLeader(project.getLeaderPerson(phase).getUser());
    }

    // Add Project Status
    centerProject.setProjectStatus(new CenterProjectStatus(project.getProjecInfoPhase(phase).getStatus(), true));

    // Add Crp Project CrossCutting to Center Project
    this.crpCrossCuttingInformation(project, centerProject, phase);

    // Add Crp Project Locations to Center Project
    this.crpProjectLocation(project, centerProject, phase);

    // Add Crp Project Partners to Center Project
    this.crpProjectPartners(project, centerProject, phase);

    project = projectManager.saveProject(project);
    projectID = project.getId();

    globalUnitProject = new GlobalUnitProject();
    globalUnitProject.setActive(true);
    globalUnitProject.setActiveSince(new Date());
    globalUnitProject.setModifiedBy(this.getCurrentUser());
    globalUnitProject.setCreatedBy(this.getCurrentUser());
    globalUnitProject.setGlobalUnit(loggedCenter);
    globalUnitProject.setProject(project);
    globalUnitProject.setOrigin(false);
    globalUnitProjectManager.saveGlobalUnitProject(globalUnitProject);

  }

  /**
   * Add OCS project information in the center project Created.
   * 
   * @param centerProjectID
   */
  public void addOcsProjectInformation(long centerProjectID) {

    agreement = ocsClient.getagreement(syncCode);

    Phase phase = this.getCurrentCenterPhase();

    Project project = new Project();
    project.setCreatedBy(this.getCurrentUser());
    project.setModifiedBy(this.getCurrentUser());
    project.setActive(true);
    project.setActiveSince(new Date());
    project.setCreateDate(new Date());
    project = projectManager.saveProject(project);
    projectID = project.getId();

    CenterProject centerProject = this.createCenterProject(project, true);

    centerProject.setSync(true);
    centerProject.setSyncDate(new Date());
    centerProject.setAutoFill(true);
    centerProject.setOcsCode(syncCode.trim());

    GlobalUnitProject globalUnitProject = new GlobalUnitProject();
    globalUnitProject.setActive(true);
    globalUnitProject.setActiveSince(new Date());
    globalUnitProject.setModifiedBy(this.getCurrentUser());
    globalUnitProject.setCreatedBy(this.getCurrentUser());
    globalUnitProject.setGlobalUnit(loggedCenter);
    globalUnitProject.setProject(project);
    globalUnitProject.setOrigin(true);
    globalUnitProjectManager.saveGlobalUnitProject(globalUnitProject);


    project = projectManager.saveProject(project);

    ProjectInfo projectInfo = new ProjectInfo();
    projectInfo.setTitle("[ " + syncCode.trim() + " ]" + agreement.getDescription());
    projectInfo.setModificationJustification("New OCS Project created");
    projectInfo.setSummary(agreement.getDescription());
    projectInfo.setStartDate(agreement.getStartDate());
    projectInfo.setEndDate(agreement.getEndDate());
    projectInfo.setScale(0);
    projectInfo.setCofinancing(false);
    projectInfo.setProjectEditLeader(false);
    projectInfo.setPresetDate(new Date());
    projectInfo.setStatus(Long.parseLong(ProjectStatusEnum.Ongoing.getStatusId()));
    projectInfo.setAdministrative(new Boolean(false));
    projectInfo.setPhase(phase);
    projectInfo.setModifiedBy(this.getCurrentUser());
    projectInfo.setModificationJustification("");
    projectInfo.setProject(project);

    projectInfo = projectInfoManager.saveProjectInfo(projectInfo);


    // CenterProjectFundingSource fundingSource = new CenterProjectFundingSource();
    //
    // fundingSource.setCenterProject(centerProject);
    // fundingSource.setCode(syncCode);
    // fundingSource.setSync(true);
    // fundingSource.setSyncDate(new Date());
    // fundingSource.setAutoFill(true);
    //
    // fundingSource.setTitle(agreement.getDescription());
    // fundingSource.setDescription(agreement.getDescription());
    //
    // try {
    // fundingSource.setStartDate(agreement.getStartDate());
    // } catch (Exception e) {
    // // OCS sends a bad Date format
    // fundingSource.setStartDate(null);
    // }
    // try {
    // fundingSource.setEndDate(agreement.getEndDate());
    // } catch (Exception e) {
    // // OCS sends a bad Date format
    // fundingSource.setEndDate(null);
    // }
    // try {
    //
    // if (agreement.getExtensionDate().after(agreement.getEndDate())) {
    // fundingSource.setExtensionDate(agreement.getExtensionDate());
    // } else {
    // fundingSource.setExtensionDate(null);
    // }
    // } catch (Exception e) {
    // // OCS sends a bad Date format
    // fundingSource.setExtensionDate(null);
    // }
    //
    // if (agreement.getOriginalDonor() != null) {
    // fundingSource.setOriginalDonor(agreement.getOriginalDonor().getName());
    // }
    // if (agreement.getDirectDonor() != null) {
    // fundingSource.setDirectDonor(agreement.getDirectDonor().getName());
    // }
    // fundingSource.setTotalAmount(Double.parseDouble(agreement.getGrantAmount()));
    //
    // // Setting the sync type (1 = OCS CIAT)
    // CenterFundingSyncType fundingSyncType = fundingSyncTypeManager.getCenterFundingSyncTypeById(1);
    // fundingSource.setCenterFundingSyncType(fundingSyncType);
    //
    // // Setting the budget Type
    // String fundingType = agreement.getFundingType();
    // long fundingtypeID = -1L;
    // if (fundingType != null) {
    // switch (fundingType) {
    // case "BLR":
    // fundingtypeID = 3;
    // break;
    // case "W1/W2":
    // fundingtypeID = 1;
    // break;
    // case "W3R":
    // fundingtypeID = 2;
    // break;
    // case "W3U":
    // fundingtypeID = 2;
    // break;
    // default:
    // fundingtypeID = -1L;
    // break;
    // }
    // }
    //
    // CenterFundingSourceType fundingSourceType = centerFundingTypeManager.getFundingSourceTypeById(fundingtypeID);
    // fundingSource.setCenterFundingSourceType(fundingSourceType);
    //
    // fundingSource.setActive(true);
    // fundingSource.setCreatedBy(this.getCurrentUser());
    // fundingSource.setModifiedBy(this.getCurrentUser());
    // fundingSource.setActiveSince(new Date());
    //
    // centerProjectFudingSourceManager.saveProjectFundingSource(fundingSource);

  }

  public CenterProject createCenterProject(Project project, boolean autofill) {
    CenterProject centerProject = new CenterProject();
    centerProject.setActive(true);
    centerProject.setActiveSince(new Date());
    centerProject.setCreatedBy(this.getCurrentUser());
    centerProject.setModifiedBy(this.getCurrentUser());
    centerProject.setStartDate(new Date());
    centerProject.setDateCreated(new Date());
    centerProject.setResearchProgram(selectedProgram);
    centerProject.setProjectStatus(new CenterProjectStatus(new Long(2), true));
    centerProject.setAutoFill(autofill);
    centerProject.setSync(autofill);
    centerProject.setSyncDate(new Date());


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

    centerProject.setProjectCrosscutingTheme(projectCrosscutingTheme);
    projectCrosscutingTheme.setProject(centerProject);

    project.setCenterProject(centerProject);
    centerProject.setProject(project);

    project = projectManager.saveProject(project);

    return centerProject;
  }

  /**
   * Create a No Sync Project
   */
  public void createEmptyProject() {

    Phase phase = this.getCurrentCenterPhase();

    Project project = new Project();
    project.setCreatedBy(this.getCurrentUser());
    project.setModifiedBy(this.getCurrentUser());
    project.setActive(true);
    project.setActiveSince(new Date());

    CenterProject centerProject = new CenterProject();
    centerProject.setActive(true);
    centerProject.setActiveSince(new Date());
    centerProject.setCreatedBy(this.getCurrentUser());
    centerProject.setModifiedBy(this.getCurrentUser());
    centerProject.setStartDate(new Date());
    centerProject.setDateCreated(new Date());
    centerProject.setResearchProgram(selectedProgram);
    centerProject.setProjectStatus(new CenterProjectStatus(new Long(2), true));
    centerProject.setAutoFill(false);
    centerProject.setSync(false);
    centerProject.setSyncDate(new Date());

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

    centerProject.setProjectCrosscutingTheme(projectCrosscutingTheme);
    projectCrosscutingTheme.setProject(centerProject);

    project.setCenterProject(centerProject);
    centerProject.setProject(project);

    project = projectManager.saveProject(project);
    projectID = project.getId();


    GlobalUnitProject globalUnitProject = new GlobalUnitProject();
    globalUnitProject.setActive(true);
    globalUnitProject.setActiveSince(new Date());
    globalUnitProject.setModifiedBy(this.getCurrentUser());
    globalUnitProject.setCreatedBy(this.getCurrentUser());
    globalUnitProject.setGlobalUnit(loggedCenter);
    globalUnitProject.setProject(project);
    globalUnitProject.setOrigin(true);
    globalUnitProjectManager.saveGlobalUnitProject(globalUnitProject);

    project = projectManager.saveProject(project);

    ProjectInfo projectInfo = new ProjectInfo();
    projectInfo.setModificationJustification("New expected Project created");
    projectInfo.setStartDate(new Date());
    projectInfo.setScale(0);
    projectInfo.setCofinancing(false);
    projectInfo.setProjectEditLeader(false);
    projectInfo.setPresetDate(new Date());
    projectInfo.setStatus(Long.parseLong(ProjectStatusEnum.Ongoing.getStatusId()));
    projectInfo.setAdministrative(new Boolean(false));
    projectInfo.setPhase(phase);
    projectInfo.setModifiedBy(this.getCurrentUser());
    projectInfo.setModificationJustification("");
    projectInfo.setProject(project);

    projectInfo = projectInfoManager.saveProjectInfo(projectInfo);

  }

  /**
   * Add CRP project Cross-cutting information in the center project Created.
   * 
   * @param project
   * @param centerProject
   */

  public void crpCrossCuttingInformation(Project project, CenterProject centerProject, Phase phase) {

    boolean hasChanges = false;

    CenterProjectCrosscutingTheme crosscutingThemeSave = centerProject.getProjectCrosscutingTheme();

    if (project.getProjecInfoPhase(phase).getCrossCuttingGender() != null
      && project.getProjecInfoPhase(phase).getCrossCuttingGender()) {
      hasChanges = true;
      crosscutingThemeSave.setGender(true);
    }

    if (project.getProjecInfoPhase(phase).getCrossCuttingYouth() != null
      && project.getProjecInfoPhase(phase).getCrossCuttingYouth()) {
      hasChanges = true;
      crosscutingThemeSave.setYouth(true);
    }

    if (project.getProjecInfoPhase(phase).getCrossCuttingCapacity() != null
      && project.getProjecInfoPhase(phase).getCrossCuttingCapacity()) {
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

  public void crpProjectLocation(Project project, CenterProject centerProject, Phase phase) {

    List<ProjectLocation> projectLocations = new ArrayList<>(project.getProjectLocations().stream()
      .filter(pl -> pl.isActive() && pl.getPhase().getId() == phase.getId()
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
  public void crpProjectPartners(Project project, CenterProject centerProject, Phase phase) {


    List<ProjectPartner> projectPartners = new ArrayList<>(project.getProjectPartners().stream()
      .filter(pp -> pp.isActive() && pp.getPhase().getId() == phase.getId()).collect(Collectors.toList()));

    for (ProjectPartner projectPartner : projectPartners) {

      // TODO fix the filter to work whit all centers (in the future)
      List<ProjectPartnerContribution> contributions = new ArrayList<>(projectPartner.getProjectPartnerContributions()
        .stream().filter(pc -> pc.isActive() && (pc.getProjectPartnerContributor().getInstitution().getId() == 46))
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
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();
    // projectID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_ID))[0]));
    projectID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PROJECT_ID).getMultipleValues()[0]));

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


  public long getProgramID() {
    return programID;
  }

  public long getProjectID() {
    return projectID;
  }


  public List<Project> getProjects() {
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

    loggedCenter = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCenter = centerService.getGlobalUnitById(loggedCenter.getId());

    researchAreas =
      new ArrayList<>(loggedCenter.getCenterAreas().stream().filter(ra -> ra.isActive()).collect(Collectors.toList()));

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
          List<CenterLeader> userAreaLeads = new ArrayList<>(user.getResearchLeaders().stream()
            .filter(rl -> rl.isActive()
              && (rl.getType().getId() == CenterLeaderTypeEnum.RESEARCH_AREA_LEADER_TYPE.getValue()))
            .collect(Collectors.toList()));
          if (!userAreaLeads.isEmpty()) {
            areaID = userAreaLeads.get(0).getResearchArea().getId();
          } else {
            // Check if the User is a Program Leader
            List<CenterLeader> userProgramLeads = new ArrayList<>(user.getResearchLeaders().stream()
              .filter(rl -> rl.isActive()
                && (rl.getType().getId() == CenterLeaderTypeEnum.RESEARCH_PROGRAM_LEADER_TYPE.getValue()))
              .collect(Collectors.toList()));
            if (!userProgramLeads.isEmpty()) {
              programID = userProgramLeads.get(0).getResearchProgram().getId();
            } else {
              // Check if the User is a Scientist Leader
              List<CenterLeader> userScientistLeader = new ArrayList<>(user.getResearchLeaders().stream()
                .filter(rl -> rl.isActive()
                  && (rl.getType().getId() == CenterLeaderTypeEnum.PROGRAM_SCIENTIST_LEADER_TYPE.getValue()))
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

      if ((areaID != -1) && (programID == -1)) {
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
                && (rl.getType().getId() == CenterLeaderTypeEnum.RESEARCH_PROGRAM_LEADER_TYPE.getValue()))
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

      List<CenterProject> centerProjects =
        new ArrayList<>(selectedProgram.getProjects().stream().filter(p -> p.isActive()).collect(Collectors.toList()));

      List<Project> pList = new ArrayList<>();
      projects = new ArrayList<>();

      for (CenterProject centerProject : centerProjects) {
        pList.add(centerProject.getProject());
      }


      for (Project project : pList) {
        /* Get The Crp/Center/Platform where the project was created */
        GlobalUnitProject globalUnitProject =
          globalUnitProjectManager.findByProjectIdOutOrigin(project.getId(), loggedCenter.getId());
        Phase phase = new Phase();
        if (globalUnitProject.isOrigin()) {
          phase = this.getCurrentCenterPhase();
        } else {
          globalUnitProject = globalUnitProjectManager.findByProjectId(project.getId());
          phase = this.getCenterCrpPhase(globalUnitProject.getGlobalUnit());
        }

        ProjectInfo projectInfo = project.getProjecInfoPhase(phase);

        project.setProjectInfo(projectInfo);

        projects.add(project);

      }

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

  public void setProgramID(long programID) {
    this.programID = programID;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setProjects(List<Project> projects) {
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