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

package org.cgiar.ccafs.marlo.action.powb;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PowbCollaborationGlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PowbCollaborationManager;
import org.cgiar.ccafs.marlo.data.manager.PowbCollaborationRegionManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceLocation;
import org.cgiar.ccafs.marlo.data.model.FundingStatusEnum;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbCollaboration;
import org.cgiar.ccafs.marlo.data.model.PowbCollaborationGlobalUnit;
import org.cgiar.ccafs.marlo.data.model.PowbCollaborationRegion;
import org.cgiar.ccafs.marlo.data.model.PowbMonitoringEvaluationLearningExercise;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.powb.PowbCollaborationValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Christian Garcia - CIAT/CCAFS
 */
public class PowbCollaborationAction extends BaseAction {


  private static final long serialVersionUID = 5112563288393590088L;


  // Managers
  private GlobalUnitManager crpManager;
  private GlobalUnitProjectManager globalUnitProjectManager;
  private PowbSynthesisManager powbSynthesisManager;
  private AuditLogManager auditLogManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;

  private UserManager userManager;


  private CrpProgramManager crpProgramManager;

  private PowbCollaborationManager powbCollaborationManager;
  private PowbCollaborationGlobalUnitManager powbCollaborationGlobalUnitManager;
  private PowbCollaborationRegionManager powbCollaborationRegionManager;

  private LocElementManager locElementManager;
  private FundingSourceManager fundingSourceManager;

  private PowbCollaborationValidator validator;
  private List<LocElement> locElements;
  private List<LiaisonInstitution> regions;

  private List<CrpProgram> crpPrograms;

  // Variables
  private String transaction;

  private PowbSynthesis powbSynthesis;

  private Long liaisonInstitutionID;
  private Long powbSynthesisID;
  private PowbSynthesis powbSynthesisBD;


  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private LiaisonInstitution liaisonInstitution;
  private List<PowbMonitoringEvaluationLearningExercise> flagshipExercises;
  private List<GlobalUnit> globalUnits;

  @Inject
  public PowbCollaborationAction(APConfig config, GlobalUnitManager crpManager,
    PowbSynthesisManager powbSynthesisManager, AuditLogManager auditLogManager,
    LiaisonInstitutionManager liaisonInstitutionManager, UserManager userManager, CrpProgramManager crpProgramManager,
    PowbCollaborationManager powbCollaborationManager, PowbCollaborationValidator validator,
    GlobalUnitProjectManager globalUnitProjectManager,
    PowbCollaborationGlobalUnitManager powbCollaborationGlobalUnitManager, LocElementManager locElementManager,
    PowbCollaborationRegionManager powbCollaborationRegionManager, FundingSourceManager fundingSourceManager) {
    super(config);
    this.crpManager = crpManager;
    this.powbSynthesisManager = powbSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.powbCollaborationManager = powbCollaborationManager;
    this.globalUnitProjectManager = globalUnitProjectManager;
    this.validator = validator;
    this.powbCollaborationGlobalUnitManager = powbCollaborationGlobalUnitManager;
    this.locElementManager = locElementManager;
    this.powbCollaborationRegionManager = powbCollaborationRegionManager;
    this.fundingSourceManager = fundingSourceManager;
  }

  @Override
  public String cancel() {
    Path path = this.getAutoSaveFilePath();
    if (path.toFile().exists()) {
      boolean fileDeleted = path.toFile().delete();
    }
    this.setDraft(false);
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      String validationMessage = messages.iterator().next();
      this.setActionMessages(null);
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    } else {
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    }
    messages = this.getActionMessages();
    return SUCCESS;
  }


  public boolean canEditRegion(long regionId) {
    String permission = this.generatePermission(Permission.POWB_SYNTHESIS_RPL_EFFORT, this.getCrpSession(),
      powbSynthesis.getId().toString(), regionId + "");
    String permissionTotal = this.generatePermission(Permission.POWB_SYNTHESIS_COLLABORATION_BASE_PERMISSION + ":*",
      this.getCrpSession(), powbSynthesis.getId().toString());


    boolean permissionBoolean = this.hasPermissionNoBase(permission) || this.hasPermissionNoBase(permissionTotal);
    return permissionBoolean;

  }


  public Long firstFlagship() {
    List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList()));
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
    long liaisonInstitutionId = liaisonInstitutions.get(0).getId();
    return liaisonInstitutionId;
  }


  private Path getAutoSaveFilePath() {
    String composedClassName = powbSynthesis.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = powbSynthesis.getId() + "_" + composedClassName + "_" + this.getActualPhase().getDescription()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public List<CrpProgram> getCrpPrograms() {
    return crpPrograms;
  }

  public PowbCollaborationRegion getElemnentRegion(long regionId) {
    int index = this.getIndexRegion(regionId);
    return powbSynthesis.getRegions().get(index);
  }

  public List<PowbMonitoringEvaluationLearningExercise> getFlagshipExercises() {
    return flagshipExercises;
  }


  public List<GlobalUnit> getGlobalUnits() {
    return globalUnits;
  }


  public int getIndexRegion(long regionId) {
    if (powbSynthesis.getRegions() == null) {
      powbSynthesis.setRegions(new ArrayList<>());
    }
    int i = 0;
    for (PowbCollaborationRegion powbCollaborationRegion : powbSynthesis.getRegions()) {
      if (powbCollaborationRegion.getLiaisonInstitution() != null
        && powbCollaborationRegion.getLiaisonInstitution().getId().longValue() == regionId) {
        return i;
      }

      i++;
    }

    PowbCollaborationRegion powbCollaborationRegion = new PowbCollaborationRegion();
    powbCollaborationRegion.setLiaisonInstitution(liaisonInstitutionManager.getLiaisonInstitutionById(regionId));
    powbSynthesis.getRegions().add(powbCollaborationRegion);
    return this.getIndexRegion(regionId);
  }

  public LiaisonInstitution getLiaisonInstitution() {
    return liaisonInstitution;
  }

  public Long getLiaisonInstitutionID() {
    return liaisonInstitutionID;
  }


  public List<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }


  public List<LocElement> getLocElements() {
    return locElements;
  }

  public List<LocElement> getLocElementsByPMU() {
    List<LocElement> locElements = new ArrayList<>();
    List<Project> projects = this.loadProjectsPMU();
    this.loadLocationsProjectRelated(locElements, projects);
    return locElements;

  }

  public List<LocElement> getLocElementsByRegion(long regionId) {
    LiaisonInstitution liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(regionId);
    List<LocElement> locElements = new ArrayList<>();
    List<Project> projects = this.loadProjects(liaisonInstitution.getCrpProgram().getId());
    this.loadLocationsProjectRelated(locElements, projects);
    return locElements;

  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public PowbSynthesis getPowbSynthesis() {
    return powbSynthesis;
  }

  public Long getPowbSynthesisID() {
    return powbSynthesisID;
  }


  public List<LiaisonInstitution> getRegions() {
    return regions;
  }


  public String getTransaction() {
    return transaction;
  }


  public void globalUnitNewData() {

    for (PowbCollaborationGlobalUnit powbCollaborationGlobalUnit : powbSynthesis
      .getPowbCollaborationGlobalUnitsList()) {
      PowbCollaborationGlobalUnit powbCollaborationGlobalUnitNew = null;
      if (powbCollaborationGlobalUnit != null) {


        if (powbCollaborationGlobalUnit.getGlobalUnit() != null
          && powbCollaborationGlobalUnit.getGlobalUnit().getId() > 0) {
          powbCollaborationGlobalUnit
            .setGlobalUnit(crpManager.getGlobalUnitById(powbCollaborationGlobalUnit.getGlobalUnit().getId()));
        } else {
          powbCollaborationGlobalUnit.setGlobalUnit(null);

        }


        if (powbCollaborationGlobalUnit.getId() == null) {

          powbCollaborationGlobalUnitNew = new PowbCollaborationGlobalUnit();
          powbCollaborationGlobalUnitNew.setActive(true);
          powbCollaborationGlobalUnitNew.setCreatedBy(this.getCurrentUser());
          powbCollaborationGlobalUnitNew.setModifiedBy(this.getCurrentUser());
          powbCollaborationGlobalUnitNew.setModificationJustification("");
          powbCollaborationGlobalUnitNew.setActiveSince(new Date());
          powbCollaborationGlobalUnitNew.setPowbSynthesis(powbSynthesis);

        } else {

          powbCollaborationGlobalUnitNew =
            powbCollaborationGlobalUnitManager.getPowbCollaborationGlobalUnitById(powbCollaborationGlobalUnit.getId());
          powbCollaborationGlobalUnitNew.setModifiedBy(this.getCurrentUser());


        }
        powbCollaborationGlobalUnitNew.setFlagship(powbCollaborationGlobalUnit.getFlagship());
        powbCollaborationGlobalUnitNew.setGlobalUnit(powbCollaborationGlobalUnit.getGlobalUnit());
        powbCollaborationGlobalUnitNew.setCollaborationType(powbCollaborationGlobalUnit.getCollaborationType());
        powbCollaborationGlobalUnitNew.setBrief(powbCollaborationGlobalUnit.getBrief());


        powbCollaborationGlobalUnitNew =
          powbCollaborationGlobalUnitManager.savePowbCollaborationGlobalUnit(powbCollaborationGlobalUnitNew);


      }

    }

  }

  public void globaUnitsPreviousData(List<PowbCollaborationGlobalUnit> powbCollaborationGlobalUnits) {

    List<PowbCollaborationGlobalUnit> globlalUnitsPrev;


    globlalUnitsPrev =
      powbSynthesisBD.getPowbCollaborationGlobalUnits().stream().filter(a -> a.isActive()).collect(Collectors.toList());


    for (PowbCollaborationGlobalUnit powbCollaborationGlobalUnit : globlalUnitsPrev) {
      if (!powbCollaborationGlobalUnits.contains(powbCollaborationGlobalUnit)) {

        powbCollaborationGlobalUnitManager.deletePowbCollaborationGlobalUnit(powbCollaborationGlobalUnit.getId());
      }
    }

  }

  public boolean isFlagship() {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() != null) {
        CrpProgram crpProgram =
          crpProgramManager.getCrpProgramById(liaisonInstitution.getCrpProgram().getId().longValue());
        if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
          isFP = true;
        }
      }
    }
    return isFP;
  }

  @Override
  public boolean isPMU() {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() == null) {
        isFP = true;
      }
    }
    return isFP;

  }

  public List<LocElement> loadLocations() {
    List<LocElement> locElements = locElementManager.findAll().stream()
      .filter(c -> c.getLocElementType().getId().longValue() == 2).collect(Collectors.toList());
    locElements.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
    for (LocElement locElement : locElements) {
      this.loadLocElementsRelations(locElement);
    }

    List<LocElement> locElementsToRet = new ArrayList<>();

    for (LocElement locElement : locElements) {
      if (!locElement.getProjects().isEmpty()) {
        locElementsToRet.add(locElement);
      }
    }
    return locElementsToRet;
  }


  public void loadLocationsProjectRelated(List<LocElement> locElements, List<Project> projects) {
    for (LocElement locElement : locElementManager.findAll().stream()
      .filter(c -> c.getLocElementType().getId().longValue() == 2).collect(Collectors.toList())) {
      this.loadLocElementsRelations(locElement);
      Set<Project> liaisonProjects = new HashSet();
      Set<FundingSource> liasionsFundings = new HashSet();

      for (Project project : locElement.getProjects()) {
        if (projects.contains(project)) {
          if (project.getProjecInfoPhase(this.getActualPhase()) != null) {
            liaisonProjects.add(project);

            List<ProjectBudget> projectBudgets = project.getProjectBudgets().stream().filter(c -> c.isActive()
              && c.getPhase().equals(this.getActualPhase()) && c.getYear() == this.getActualPhase().getYear())
              .collect(Collectors.toList());
            for (ProjectBudget projectBudget : projectBudgets) {
              projectBudget.getFundingSource().getFundingSourceInfo(this.getActualPhase());
              if (projectBudget.getFundingSource().getFundingSourceInfo() != null) {
                FundingSource fs = fundingSourceManager.getFundingSourceById(projectBudget.getFundingSource().getId());

                if (locElement.getFundingSources().contains(fs) && !liasionsFundings.contains(fs)) {
                  liasionsFundings.add(fs);

                }

              }

            }
          }


        }
      }
      locElement.getProjects().clear();
      locElement.getFundingSources().clear();
      locElement.getProjects().addAll(liaisonProjects.stream().distinct().collect(Collectors.toList()));
      locElement.getFundingSources().addAll(liasionsFundings.stream().distinct().collect(Collectors.toList()));
      locElement.getProjects().sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
      locElement.getFundingSources().sort((p1, p2) -> p1.getId().compareTo(p2.getId()));


      if (!locElement.getProjects().isEmpty()) {
        locElements.add(locElement);
      }

    }
  }

  public void loadLocElementsRelations(LocElement locElement) {

    HashSet<Project> project = new HashSet<>();
    HashSet<FundingSource> fundingSources = new HashSet<>();

    List<ProjectLocation> locations =
      locElement.getProjectLocations().stream().filter(c -> c.isActive() && c.getPhase() != null
        && c.getPhase().equals(this.getActualPhase()) && c.getProject().isActive()).collect(Collectors.toList());
    locations.sort((p1, p2) -> p1.getProject().getId().compareTo(p2.getProject().getId()));
    for (ProjectLocation projectLocation : locations) {
      projectLocation.getProject()
        .setProjectInfo(projectLocation.getProject().getProjecInfoPhase(this.getActualPhase()));
      if (globalUnitProjectManager.findByProjectId(projectLocation.getProject().getId()).getGlobalUnit()
        .equals(loggedCrp)) {
        if (projectLocation.getProject().getProjectInfo().getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
          || projectLocation.getProject().getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
          if (projectLocation.getProject().isActive() && projectLocation.getProject().getProjectInfo() != null) {
            project.add(projectLocation.getProject());
          }


        }
      }

    }


    List<LocElement> locElementsParent = locElementManager.findAll().stream()
      .filter(c -> c.getLocElement() != null && c.getLocElement().getId().equals(locElement.getId()))
      .collect(Collectors.toList());

    for (LocElement locElementParent : locElementsParent) {


      locations = locElementParent.getProjectLocations().stream().filter(c -> c.isActive() && c.getPhase() != null
        && c.getPhase().equals(this.getActualPhase()) && c.getProject().isActive()).collect(Collectors.toList());
      locations.sort((p1, p2) -> p1.getProject().getId().compareTo(p2.getProject().getId()));
      for (ProjectLocation projectLocation : locations) {
        projectLocation.getProject()
          .setProjectInfo(projectLocation.getProject().getProjecInfoPhase(this.getActualPhase()));
        if (globalUnitProjectManager.findByProjectId(projectLocation.getProject().getId()).getGlobalUnit()
          .equals(loggedCrp)) {
          if (projectLocation.getProject().getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || projectLocation.getProject().getProjectInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            if (projectLocation.getProject().isActive() && projectLocation.getProject().getProjectInfo() != null) {
              project.add(projectLocation.getProject());
            }

          }
        }

      }
    }


    List<FundingSourceLocation> locationsFunding = locElement.getFundingSourceLocations()
      .stream().filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())
        && c.getFundingSource().isActive() && c.getFundingSource().getCrp().equals(loggedCrp))
      .collect(Collectors.toList());
    locationsFunding.sort((p1, p2) -> p1.getFundingSource().getId().compareTo(p2.getFundingSource().getId()));

    for (FundingSourceLocation fundingSourceLocation : locationsFunding) {
      fundingSourceLocation.getFundingSource()
        .setFundingSourceInfo(fundingSourceLocation.getFundingSource().getFundingSourceInfo(this.getActualPhase()));
      if (fundingSourceLocation.getFundingSource().getFundingSourceInfo(this.getActualPhase()).getStatus() == Integer
        .parseInt(FundingStatusEnum.Ongoing.getStatusId())
        || fundingSourceLocation.getFundingSource().getFundingSourceInfo(this.getActualPhase()).getStatus() == Integer
          .parseInt(FundingStatusEnum.Pipeline.getStatusId())
        || fundingSourceLocation.getFundingSource().getFundingSourceInfo(this.getActualPhase()).getStatus() == Integer
          .parseInt(FundingStatusEnum.Informally.getStatusId())

        || fundingSourceLocation.getFundingSource().getFundingSourceInfo(this.getActualPhase()).getStatus() == Integer
          .parseInt(FundingStatusEnum.Extended.getStatusId())) {
        if (fundingSourceLocation.getFundingSource().getFundingSourceInfo() != null
          && fundingSourceLocation.getFundingSource().isActive()) {
          fundingSources.add(fundingSourceLocation.getFundingSource());
        }


      }
    }

    locElement.setProjects(new ArrayList<>());
    locElement.setFundingSources(new ArrayList<>());
    locElement.getProjects().addAll(project);
    locElement.getFundingSources().addAll(fundingSources);
  }

  public List<Project> loadProjects(long crpProgramID) {
    List<Project> projectsToRet = new ArrayList<>();
    CrpProgram crpProgram = crpProgramManager.getCrpProgramById(crpProgramID);
    List<ProjectFocus> projects = crpProgram.getProjectFocuses().stream()
      .filter(c -> c.getProject().isActive() && c.isActive()).collect(Collectors.toList());
    Set<Project> myProjects = new HashSet();
    for (ProjectFocus projectFocus : projects) {
      Project project = projectFocus.getProject();
      if (project.isActive()) {
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        if (project.getProjectInfo() != null && project.getProjectInfo().getStatus() != null) {
          if (project.getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || project.getProjectInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            myProjects.add(project);
          }
        }


      }
    }
    for (Project project : myProjects) {

      project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
      projectsToRet.add(project);
    }
    return projectsToRet;

  }


  public List<Project> loadProjectsPMU() {
    List<Project> projectsToRet = new ArrayList<>();
    GlobalUnit globalUnit = crpManager.getGlobalUnitById(loggedCrp.getId());
    List<GlobalUnitProject> globalUnitProjects = globalUnit.getGlobalUnitProjects().stream()
      .filter(c -> c.isActive() && c.isOrigin()).collect(Collectors.toList());
    Set<Project> myProjects = new HashSet();
    for (GlobalUnitProject globalUnitProject : globalUnitProjects) {
      Project project = globalUnitProject.getProject();
      if (project.isActive()) {
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));

        if (project.getProjectInfo() != null && project.getProjectInfo().getStatus() != null) {
          if (project.getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || project.getProjectInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())) {

            if ((project.getProjectInfo().getAdministrative() != null
              && project.getProjectInfo().getAdministrative().booleanValue())
              || (project.getProjectInfo().getNoRegional() != null
                && project.getProjectInfo().getNoRegional().booleanValue())) {
              myProjects.add(project);
            }

          }
        }


      }
    }
    for (Project project : myProjects) {

      project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
      projectsToRet.add(project);
    }
    return projectsToRet;

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
    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    Phase phase = this.getActualPhase();

    // If there is a history version being loaded
    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {
      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      PowbSynthesis history = (PowbSynthesis) auditLogManager.getHistory(transaction);
      if (history != null) {
        powbSynthesis = history;
        powbSynthesisID = powbSynthesis.getId();
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }
    } else {
      // Get Liaison institution ID Parameter
      try {
        liaisonInstitutionID =
          Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.LIAISON_INSTITUTION_REQUEST_ID)));
      } catch (NumberFormatException e) {
        User user = userManager.getUser(this.getCurrentUser().getId());
        if (user.getLiasonsUsers() != null || !user.getLiasonsUsers().isEmpty()) {
          List<LiaisonUser> liaisonUsers = new ArrayList<>(
            user.getLiasonsUsers().stream().filter(lu -> lu.isActive() && lu.getLiaisonInstitution().isActive()
              && lu.getLiaisonInstitution().getCrp().getId() == loggedCrp.getId()).collect(Collectors.toList()));
          if (!liaisonUsers.isEmpty()) {
            boolean isLeader = false;
            for (LiaisonUser liaisonUser : liaisonUsers) {
              LiaisonInstitution institution = liaisonUser.getLiaisonInstitution();
              if (institution.isActive()) {
                if (institution.getCrpProgram() != null) {
                  if (institution.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
                    liaisonInstitutionID = institution.getId();
                    isLeader = true;
                    break;
                  }
                } else {
                  if (institution.getAcronym().equals("PMU")) {
                    liaisonInstitutionID = institution.getId();
                    isLeader = true;
                    break;
                  }
                }
              }
            }
            if (!isLeader) {
              liaisonInstitutionID = this.firstFlagship();
            }
          } else {
            liaisonInstitutionID = this.firstFlagship();
          }
        } else {
          liaisonInstitutionID = this.firstFlagship();
        }
      }


      try {
        powbSynthesisID =
          Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.POWB_SYNTHESIS_ID)));
        powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);

        if (!powbSynthesis.getPhase().equals(phase)) {
          powbSynthesis = powbSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);
          if (powbSynthesis == null) {
            powbSynthesis = this.createPowbSynthesis(phase.getId(), liaisonInstitutionID);
          }
          powbSynthesisID = powbSynthesis.getId();
        }
      } catch (Exception e) {

        powbSynthesis = powbSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);
        if (powbSynthesis == null) {
          powbSynthesis = this.createPowbSynthesis(phase.getId(), liaisonInstitutionID);
        }
        powbSynthesisID = powbSynthesis.getId();

      }
    }


    if (powbSynthesis != null) {

      PowbSynthesis powbSynthesisDB = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
      powbSynthesisID = powbSynthesisDB.getId();
      liaisonInstitutionID = powbSynthesisDB.getLiaisonInstitution().getId();
      liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);

      Path path = this.getAutoSaveFilePath();
      // Verify if there is a Draft file
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        AutoSaveReader autoSaveReader = new AutoSaveReader();
        powbSynthesis = (PowbSynthesis) autoSaveReader.readFromJson(jReader);
        powbSynthesisID = powbSynthesis.getId();
        if (powbSynthesis.getPowbCollaborationGlobalUnitsList() != null) {
          for (PowbCollaborationGlobalUnit powbCollaborationGlobalUnit : powbSynthesis
            .getPowbCollaborationGlobalUnitsList()) {
            if (powbCollaborationGlobalUnit.getGlobalUnit() != null
              && powbCollaborationGlobalUnit.getGlobalUnit().getId() != -1) {
              powbCollaborationGlobalUnit
                .setGlobalUnit(crpManager.getGlobalUnitById(powbCollaborationGlobalUnit.getGlobalUnit().getId()));
            }
          }
        }
        if (powbSynthesis.getRegions() != null) {
          for (PowbCollaborationRegion powbCollaborationRegion : powbSynthesis.getRegions()) {
            if (powbCollaborationRegion.getLiaisonInstitution() != null
              && powbCollaborationRegion.getLiaisonInstitution().getId() != -1) {
              powbCollaborationRegion.setLiaisonInstitution(liaisonInstitutionManager
                .getLiaisonInstitutionById(powbCollaborationRegion.getLiaisonInstitution().getId()));
            }
          }
          powbSynthesis.getRegions().sort(
            (p1, p2) -> p1.getLiaisonInstitution().getAcronym().compareTo(p2.getLiaisonInstitution().getAcronym()));
        }
        this.setDraft(true);
        reader.close();
      } else {
        this.setDraft(false);
        // Check if ToC relation is null -create it
        if (powbSynthesis.getCollaboration() == null) {
          PowbCollaboration powbCollaboration = new PowbCollaboration();
          powbCollaboration.setActive(true);
          powbCollaboration.setActiveSince(new Date());
          powbCollaboration.setCreatedBy(this.getCurrentUser());
          powbCollaboration.setModifiedBy(this.getCurrentUser());
          powbCollaboration.setModificationJustification("");
          // create one to one relation
          powbSynthesis.setCollaboration(powbCollaboration);
          powbCollaboration.setPowbSynthesis(powbSynthesis);
          // save the changes
          powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
        }
        powbSynthesis.setPowbCollaborationGlobalUnitsList(powbSynthesis.getPowbCollaborationGlobalUnits().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList()));

        powbSynthesis.setRegions(
          powbSynthesis.getPowbCollaborationRegions().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

        powbSynthesis.getRegions()
          .sort((p1, p2) -> p1.getLiaisonInstitution().getAcronym().compareTo(p2.getLiaisonInstitution().getAcronym()));
      }
    }

    if (this.isPMU()) {
      // this.loadLocations();
      crpPrograms = loggedCrp.getCrpPrograms().stream()
        .filter(c -> c.isActive() && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());
      for (CrpProgram crpProgram : crpPrograms) {
        List<LiaisonInstitution> liaisonInstitutions =
          crpProgram.getLiaisonInstitutions().stream().filter(c -> c.isActive()).collect(Collectors.toList());
        if (!liaisonInstitutions.isEmpty()) {
          PowbSynthesis powbSynthesisProgram =
            powbSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutions.get(0).getId());
          if (powbSynthesisProgram != null) {
            powbSynthesisProgram.setPowbCollaborationGlobalUnitsList(powbSynthesisProgram
              .getPowbCollaborationGlobalUnits().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

            crpProgram.setCollaboration(powbSynthesisProgram.getCollaboration());
            crpProgram.setSynthesis(powbSynthesisProgram);


          }
        }
        if (crpProgram.getSynthesis() == null) {
          crpProgram.setSynthesis(new PowbSynthesis());
        }
        if (crpProgram.getCollaboration() == null) {
          crpProgram.setCollaboration(new PowbCollaboration());

        }

      }
      crpPrograms.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));
    }
    if (this.hasSpecificities(APConstants.CRP_HAS_REGIONS)) {
      regions = liaisonInstitutionManager.findAll().stream()
        .filter(c -> c.isActive() && c.getCrpProgram() != null
          && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());
      regions.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));
    } else {
      regions = new ArrayList<>();
    }
    globalUnits = new ArrayList<>();
    List<GlobalUnit> globalUnitsList = crpManager.findAll().stream()
      .filter(c -> c.isActive() && c.getGlobalUnitType().getId() != 2).collect(Collectors.toList());

    for (GlobalUnit globalUnit : globalUnitsList) {
      if (!globalUnit.getId().equals(loggedCrp.getId())) {
        if (globalUnit.getAcronym() != null && globalUnit.getAcronym().trim().length() == 0) {
          globalUnit.setAcronymValid(globalUnit.getName());
        } else {
          globalUnit.setAcronymValid(globalUnit.getAcronym());

        }
        globalUnits.add(globalUnit);
      }

    }
    globalUnits.sort((p1, p2) -> p1.getAcronymValid().compareTo(p2.getAcronymValid()));
    globalUnits.sort((p1, p2) -> {
      if (p1.getGlobalUnitType().getId().compareTo(p2.getGlobalUnitType().getId()) == 0) {
        return p1.getAcronymValid().compareTo(p2.getAcronymValid());

      } else {
        return p1.getGlobalUnitType().getId().compareTo(p2.getGlobalUnitType().getId());
      }
    });

    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));


    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));

    powbSynthesisBD = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), powbSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.POWB_SYNTHESIS_COLLABORATION_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (powbSynthesis.getPowbCollaborationGlobalUnitsList() != null) {
        powbSynthesis.getPowbCollaborationGlobalUnitsList().clear();
      }
      if (powbSynthesis.getRegions() != null) {
        powbSynthesis.getRegions().clear();
      }
    }
  }


  public List<Project> projectsbyProgram(CrpProgram crpProgram) {
    List<ProjectFocus> projects = crpProgram.getProjectFocuses().stream()
      .filter(c -> c.getProject().isActive() && c.isActive()).collect(Collectors.toList());
    Set<Project> myProjects = new HashSet();
    for (ProjectFocus projectFocus : projects) {
      Project project = projectFocus.getProject();
      if (project.isActive()) {
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        if (project.getProjectInfo() != null && project.getProjectInfo().getStatus() != null) {
          if (project.getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || project.getProjectInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            myProjects.add(project);
          }
        }


      }
    }
    List<Project> projectsToRet = new ArrayList<>();
    projectsToRet.addAll(myProjects);
    return projectsToRet;

  }

  public void regionsNewData() {

    for (PowbCollaborationRegion powbCollaborationRegion : powbSynthesis.getRegions()) {
      PowbCollaborationRegion powbCollaborationRegionNew = null;
      if (powbCollaborationRegion != null) {


        if (powbCollaborationRegion.getLiaisonInstitution() != null
          && powbCollaborationRegion.getLiaisonInstitution().getId() > 0) {
          powbCollaborationRegion.setLiaisonInstitution(liaisonInstitutionManager
            .getLiaisonInstitutionById(powbCollaborationRegion.getLiaisonInstitution().getId()));
        } else {
          powbCollaborationRegion.setLiaisonInstitution(null);

        }


        if (powbCollaborationRegion.getId() == null) {

          powbCollaborationRegionNew = new PowbCollaborationRegion();
          powbCollaborationRegionNew.setActive(true);
          powbCollaborationRegionNew.setCreatedBy(this.getCurrentUser());
          powbCollaborationRegionNew.setModifiedBy(this.getCurrentUser());
          powbCollaborationRegionNew.setModificationJustification("");
          powbCollaborationRegionNew.setActiveSince(new Date());
          powbCollaborationRegionNew.setPowbSynthesis(powbSynthesis);

        } else {

          powbCollaborationRegionNew =
            powbCollaborationRegionManager.getPowbCollaborationRegionById(powbCollaborationRegion.getId());
          powbCollaborationRegionNew.setModifiedBy(this.getCurrentUser());


        }
        powbCollaborationRegionNew.setLiaisonInstitution(powbCollaborationRegion.getLiaisonInstitution());
        powbCollaborationRegionNew.setEffostornCountry(powbCollaborationRegion.getEffostornCountry());


        powbCollaborationRegionNew =
          powbCollaborationRegionManager.savePowbCollaborationRegion(powbCollaborationRegionNew);


      }

    }

  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      PowbCollaboration powCollabrotionDB =
        powbSynthesisManager.getPowbSynthesisById(powbSynthesisID).getCollaboration();

      if (powCollabrotionDB == null) {
        powCollabrotionDB = new PowbCollaboration();
        powCollabrotionDB.setActive(true);
        powCollabrotionDB.setActiveSince(new Date());
        powCollabrotionDB.setCreatedBy(this.getCurrentUser());
        powCollabrotionDB.setModifiedBy(this.getCurrentUser());
        powCollabrotionDB.setModificationJustification("");
        // create one to one relation
        powCollabrotionDB.setPowbSynthesis(powbSynthesis);
        // save the changes

      }
      powCollabrotionDB.setCotributionsPlatafforms(powbSynthesis.getCollaboration().getCotributionsPlatafforms());
      powCollabrotionDB.setCrossCrp(powbSynthesis.getCollaboration().getCrossCrp());
      powCollabrotionDB.setEffostornCountry(powbSynthesis.getCollaboration().getEffostornCountry());
      powCollabrotionDB.setKeyExternalPartners(powbSynthesis.getCollaboration().getKeyExternalPartners());

      powCollabrotionDB = powbCollaborationManager.savePowbCollaboration(powCollabrotionDB);
      if (powbSynthesis.getPowbCollaborationGlobalUnitsList() == null) {
        powbSynthesis.setPowbCollaborationGlobalUnitsList(new ArrayList<>());
      }
      if (powbSynthesis.getRegions() == null) {
        powbSynthesis.setRegions(new ArrayList<>());
      }
      this.globaUnitsPreviousData(powbSynthesis.getPowbCollaborationGlobalUnitsList());
      this.globalUnitNewData();
      this.regionsNewData();
      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.SYNTHESIS_GLOBAL_UNTIS_RELATION);
      relationsName.add(APConstants.SYNTHESIS_REGIONS_RELATION);


      powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
      powbSynthesis.setModifiedBy(this.getCurrentUser());
      powbSynthesis.setActiveSince(new Date());

      powbSynthesisManager.save(powbSynthesis, this.getActionName(), relationsName, this.getActualPhase());


      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      Collection<String> messages = this.getActionMessages();
      // this.setInvalidFields(new HashMap<>());
      if (!this.getInvalidFields().isEmpty()) {
        this.setActionMessages(null);
        // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
        List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
        for (String key : keys) {
          this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
        }

      } else {
        this.addActionMessage("message:" + this.getText("saving.saved"));
      }

      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setCrpPrograms(List<CrpProgram> crpPrograms) {
    this.crpPrograms = crpPrograms;
  }

  public void setFlagshipExercises(List<PowbMonitoringEvaluationLearningExercise> flagshipExercises) {
    this.flagshipExercises = flagshipExercises;
  }


  public void setGlobalUnits(List<GlobalUnit> globalUnits) {
    this.globalUnits = globalUnits;
  }


  public void setLiaisonInstitution(LiaisonInstitution liaisonInstitution) {
    this.liaisonInstitution = liaisonInstitution;
  }

  public void setLiaisonInstitutionID(Long liaisonInstitutionID) {
    this.liaisonInstitutionID = liaisonInstitutionID;
  }

  public void setLiaisonInstitutions(List<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }

  public void setLocElements(List<LocElement> locElements) {
    this.locElements = locElements;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setPowbSynthesis(PowbSynthesis powbSynthesis) {
    this.powbSynthesis = powbSynthesis;
  }

  public void setPowbSynthesisID(Long powbSynthesisID) {
    this.powbSynthesisID = powbSynthesisID;
  }


  public void setRegions(List<LiaisonInstitution> regions) {
    this.regions = regions;
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, powbSynthesis, true);
    }
  }
}
