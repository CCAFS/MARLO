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
import org.cgiar.ccafs.marlo.data.model.CenterFundingSourceType;
import org.cgiar.ccafs.marlo.data.model.CenterLeader;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterProjectCrosscutingTheme;
import org.cgiar.ccafs.marlo.data.model.CenterProjectFundingSource;
import org.cgiar.ccafs.marlo.data.model.CenterProjectLocation;
import org.cgiar.ccafs.marlo.data.model.CenterProjectOutput;
import org.cgiar.ccafs.marlo.data.model.CenterProjectType;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.OutcomeOutputs;
import org.cgiar.ccafs.marlo.data.model.TopicOutcomes;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.service.IAuditLogManager;
import org.cgiar.ccafs.marlo.data.service.ICenterFundingSourceTypeManager;
import org.cgiar.ccafs.marlo.data.service.ICenterOutputManager;
import org.cgiar.ccafs.marlo.data.service.ICenterProjectCrosscutingThemeManager;
import org.cgiar.ccafs.marlo.data.service.ICenterProjectFundingSourceManager;
import org.cgiar.ccafs.marlo.data.service.ICenterProjectLocationManager;
import org.cgiar.ccafs.marlo.data.service.ICenterProjectOutputManager;
import org.cgiar.ccafs.marlo.data.service.ICenterProjectManager;
import org.cgiar.ccafs.marlo.data.service.ICenterProjectTypeManager;
import org.cgiar.ccafs.marlo.data.service.ICenterManager;
import org.cgiar.ccafs.marlo.data.service.ICrpManager;
import org.cgiar.ccafs.marlo.data.service.ILocElementManager;
import org.cgiar.ccafs.marlo.data.service.IUserManager;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConstants;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.monitoring.project.ProjectDescriptionValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectDescriptionAction extends BaseAction {


  private static final long serialVersionUID = 3034101967516313023L;


  private ICenterManager centerService;

  private ICenterProjectManager projectService;


  private IUserManager userService;

  private ICenterOutputManager outputService;


  private ICenterFundingSourceTypeManager fundingSourceService;

  private ICenterProjectOutputManager projectOutputService;


  private ICenterProjectLocationManager projectLocationService;

  private ILocElementManager locElementService;


  private ICenterProjectFundingSourceManager projectFundingSourceService;

  private ICenterProjectCrosscutingThemeManager projectCrosscutingThemeService;


  private IAuditLogManager auditLogService;


  private ProjectDescriptionValidator validator;


  private ICrpManager crpService;

  private ICenterProjectTypeManager projectTypeService;


  private CenterArea selectedResearchArea;

  private CenterProgram selectedProgram;

  private Center loggedCenter;

  private List<CenterArea> researchAreas;

  private List<CenterProgram> researchPrograms;

  private List<CenterFundingSourceType> fundingSourceTypes;


  private List<OutcomeOutputs> outputs;
  private List<TopicOutcomes> topicOutcomes;
  private List<LocElement> regionLists;
  private List<LocElement> countryLists;
  private List<Crp> crps;
  private List<CenterProjectType> projectTypes;
  private boolean region;
  private long programID;
  private long areaID;
  private long projectID;
  private CenterProject project;
  private String principalInvestigator;
  private String transaction;

  @Inject
  public ProjectDescriptionAction(APConfig config, ICenterManager centerService, ICenterProjectManager projectService,
    IUserManager userService, ICenterFundingSourceTypeManager fundingSourceService,
    ProjectDescriptionValidator validator, ICenterOutputManager outputService,
    ICenterProjectOutputManager projectOutputService, ICenterProjectFundingSourceManager projectFundingSourceService,
    ICenterProjectCrosscutingThemeManager projectCrosscutingThemeService,
    ICenterProjectLocationManager projectLocationService, ILocElementManager locElementService,
    IAuditLogManager auditLogService, ICrpManager crpService, ICenterProjectTypeManager projectTypeService) {
    super(config);
    this.centerService = centerService;
    this.projectService = projectService;
    this.userService = userService;
    this.fundingSourceService = fundingSourceService;
    this.validator = validator;
    this.outputService = outputService;
    this.projectFundingSourceService = projectFundingSourceService;
    this.projectOutputService = projectOutputService;
    this.projectCrosscutingThemeService = projectCrosscutingThemeService;
    this.projectLocationService = projectLocationService;
    this.locElementService = locElementService;
    this.auditLogService = auditLogService;
    this.crpService = crpService;
    this.projectTypeService = projectTypeService;
  }

  public Boolean bolValue(String value) {
    if (value == null || value.isEmpty() || value.toLowerCase().equals("null")) {
      return null;
    }
    return Boolean.valueOf(value);
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

  public long getAreaID() {
    return areaID;
  }

  private Path getAutoSaveFilePath() {
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<LocElement> getCountryLists() {
    return countryLists;
  }

  public List<Crp> getCrps() {
    return crps;
  }

  public List<CenterFundingSourceType> getFundingSourceTypes() {
    return fundingSourceTypes;
  }

  public Center getLoggedCenter() {
    return loggedCenter;
  }

  public List<OutcomeOutputs> getOutputs() {
    return outputs;
  }

  private String getPI() {
    List<CenterLeader> leaders = new ArrayList<>(
      selectedProgram.getResearchLeaders().stream().filter(rl -> rl.isActive()).collect(Collectors.toList()));
    return leaders.get(0).getUser().getComposedCompleteName();
  }

  public String getPrincipalInvestigator() {
    return principalInvestigator;
  }

  public long getProgramID() {
    return programID;
  }

  public void getProgramOutputs() {

    outputs = new ArrayList<>();

    List<CenterTopic> researchTopics = new ArrayList<>(
      selectedProgram.getResearchTopics().stream().filter(rt -> rt.isActive()).collect(Collectors.toList()));
    principalInvestigator = this.getPI();
    for (CenterTopic researchTopic : researchTopics) {
      List<CenterOutcome> researchOutcomes = new ArrayList<>(
        researchTopic.getResearchOutcomes().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));
      for (CenterOutcome researchOutcome : researchOutcomes) {
        OutcomeOutputs outcomeOutputs = new OutcomeOutputs();
        outcomeOutputs.setOutcome(researchOutcome);
        outcomeOutputs.setOutputs(new ArrayList<>());
        List<CenterOutput> researchOutputs = new ArrayList<>(
          researchOutcome.getResearchOutputs().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));
        for (CenterOutput researchOutput : researchOutputs) {
          outcomeOutputs.getOutputs().add(researchOutput);
        }
        outputs.add(outcomeOutputs);
      }
    }
  }

  public CenterProject getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  public List<CenterProjectType> getProjectTypes() {
    return projectTypes;
  }

  public List<LocElement> getRegionLists() {
    return regionLists;
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

  public List<TopicOutcomes> getTopicOutcomes() {
    return topicOutcomes;
  }

  public String getTransaction() {
    return transaction;
  }

  public boolean isRegion() {
    return region;
  }


  @Override
  public void prepare() throws Exception {
    loggedCenter = (Center) this.getSession().get(APConstants.SESSION_CENTER);
    loggedCenter = centerService.getCrpById(loggedCenter.getId());

    researchAreas = new ArrayList<>(
      loggedCenter.getResearchAreas().stream().filter(ra -> ra.isActive()).collect(Collectors.toList()));
    region = false;
    // Regions List
    regionLists = new ArrayList<>(locElementService.findAll().stream()
      .filter(le -> le.isActive() && le.getLocElementType() != null && le.getLocElementType().getId() == 1)
      .collect(Collectors.toList()));
    Collections.sort(regionLists, (r1, r2) -> r1.getName().compareTo(r2.getName()));

    // Country List
    countryLists = new ArrayList<>(locElementService.findAll().stream()
      .filter(le -> le.isActive() && le.getLocElementType() != null && le.getLocElementType().getId() == 2)
      .collect(Collectors.toList()));
    Collections.sort(countryLists, (c1, c2) -> c1.getName().compareTo(c2.getName()));

    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_ID)));
    } catch (Exception e) {
      projectID = -1;
    }

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      CenterProject history = (CenterProject) auditLogService.getHistory(transaction);

      if (history != null) {
        project = history;
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }

    } else {
      project = projectService.getCenterProjectById(projectID);
    }


    if (project != null) {

      CenterProject ProjectDB = projectService.getCenterProjectById(projectID);
      selectedProgram = ProjectDB.getResearchProgram();
      programID = selectedProgram.getId();
      selectedResearchArea = selectedProgram.getResearchArea();
      areaID = selectedResearchArea.getId();
      researchPrograms = new ArrayList<>(
        selectedResearchArea.getResearchPrograms().stream().filter(rp -> rp.isActive()).collect(Collectors.toList()));

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave() && this.isEditable()) {
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        AutoSaveReader autoSaveReader = new AutoSaveReader();

        project = (CenterProject) autoSaveReader.readFromJson(jReader);
        CenterProject projectDB = projectService.getCenterProjectById(project.getId());

        if (project.getProjectLeader() != null) {
          if (project.getProjectLeader().getId() != null) {
            if (project.getProjectLeader().getId() != null || project.getProjectLeader().getId() != -1) {
              User user = userService.getUser(project.getProjectLeader().getId());
              project.setProjectLeader(user);
            }
          }
        }

        if (project.getOutputs() != null) {
          List<CenterProjectOutput> outputs = new ArrayList<>();
          for (CenterProjectOutput output : project.getOutputs()) {

            if (output.getId() != null) {
              CenterProjectOutput projectOutput = projectOutputService.getProjectOutputById(output.getId());
              outputs.add(projectOutput);


            } else {
              CenterOutput researchOutput = outputService.getResearchOutputById(output.getResearchOutput().getId());
              CenterProjectOutput projectOutput = new CenterProjectOutput();
              projectOutput.setResearchOutput(researchOutput);
              projectOutput.setProject(projectDB);
              outputs.add(projectOutput);
            }


          }

          project.setOutputs(new ArrayList<>(outputs));
        }

        if (project.getProjectCountries() != null) {
          for (CenterProjectLocation projectLocation : project.getProjectCountries()) {
            if (projectLocation != null) {
              projectLocation.setLocElement(
                locElementService.getLocElementByISOCode(projectLocation.getLocElement().getIsoAlpha2()));
            }
          }
        }

        if (project.getProjectRegions() != null) {
          for (CenterProjectLocation projectLocation : project.getProjectRegions()) {
            region = true;
            if (projectLocation != null) {
              projectLocation
                .setLocElement(locElementService.getLocElementById(projectLocation.getLocElement().getId()));
            }
          }
        }

        reader.close();
        this.setDraft(true);
      } else {
        this.setDraft(false);

        System.out.println(String.valueOf(project.getGlobal()));
        if (project.getGlobal() != null) {
          project.setsGlobal(String.valueOf(project.getGlobal()));
        }
        if (project.getRegion() != null) {
          project.setsRegion(String.valueOf(project.getRegion()));
        }

        CenterProjectCrosscutingTheme crosscutingTheme;
        if (this.isEditable()) {
          crosscutingTheme = projectCrosscutingThemeService.getProjectCrosscutingThemeById(project.getId());
        } else {
          crosscutingTheme = project.getProjectCrosscutingTheme();
        }
        project.setProjectCrosscutingTheme(crosscutingTheme);

        project.setOutputs(new ArrayList<>(
          project.getProjectOutputs().stream().filter(po -> po.isActive()).collect(Collectors.toList())));

        project.setFundingSources(new ArrayList<>(
          project.getProjectFundingSources().stream().filter(fs -> fs.isActive()).collect(Collectors.toList())));


        if (project.getProjectLocations() != null) {

          List<CenterProjectLocation> countries = new ArrayList<>(project.getProjectLocations().stream()
            .filter(fl -> fl.isActive() && fl.getLocElement().getLocElementType().getId() == 2)
            .collect(Collectors.toList()));

          project.setProjectCountries(new ArrayList<>(countries));

          List<CenterProjectLocation> regions = new ArrayList<>(project.getProjectLocations().stream()
            .filter(fl -> fl.isActive() && fl.getLocElement().getLocElementType().getId() == 1)
            .collect(Collectors.toList()));


          if (regions.size() > 0) {
            region = true;
          }

          project.setProjectRegions(regions);

        }


      }


      fundingSourceTypes = new ArrayList<>(
        fundingSourceService.findAll().stream().filter(fst -> fst.isActive()).collect(Collectors.toList()));

      crps = new ArrayList<>(crpService.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

      projectTypes =
        new ArrayList<>(projectTypeService.findAll().stream().filter(pt -> pt.isActive()).collect(Collectors.toList()));

      this.getProgramOutputs();

    }


    topicOutcomes = new ArrayList<>();

    List<CenterTopic> researchTopics = new ArrayList<>(
      selectedProgram.getResearchTopics().stream().filter(rt -> rt.isActive()).collect(Collectors.toList()));


    for (CenterTopic researchTopic : researchTopics) {
      TopicOutcomes outcome = new TopicOutcomes();
      outcome.setTopic(researchTopic);
      outcome.setOutcomes(new ArrayList<>());
      List<CenterOutcome> researchOutcomes = new ArrayList<>(
        researchTopic.getResearchOutcomes().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));
      for (CenterOutcome researchOutcome : researchOutcomes) {
        outcome.getOutcomes().add(researchOutcome);
      }

      topicOutcomes.add(outcome);
    }

    String params[] =
      {loggedCenter.getAcronym(), selectedResearchArea.getId() + "", selectedProgram.getId() + "", projectID + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_DESCRIPTION_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (outputs != null) {
        outputs.clear();
      }

      if (fundingSourceTypes != null) {
        fundingSourceTypes.clear();
      }

      if (crps != null) {
        crps.clear();
      }

      if (projectTypes != null) {
        projectTypes.clear();
      }

      if (project.getProjectCrosscutingTheme() != null) {
        project.getProjectCrosscutingTheme().setPoliciesInstitutions(null);
        project.getProjectCrosscutingTheme().setGender(null);
        project.getProjectCrosscutingTheme().setYouth(null);
        project.getProjectCrosscutingTheme().setClimateChange(null);
        project.getProjectCrosscutingTheme().setCapacityDevelopment(null);
        project.getProjectCrosscutingTheme().setNa(null);
        project.getProjectCrosscutingTheme().setBigData(null);
        project.getProjectCrosscutingTheme().setImpactAssessment(null);
      }

      if (project.getFundingSources() != null) {
        project.getFundingSources().clear();
      }

      if (project.getOutputs() != null) {
        project.getOutputs().clear();
      }

      if (project.getProjectRegions() != null) {
        project.getProjectRegions().clear();
      }

      if (project.getProjectCountries() != null) {
        project.getProjectCountries().clear();
      }

    }
  }


  @Override
  public String save() {
    if (this.hasPermission("*")) {

      CenterProject projectDB = projectService.getCenterProjectById(projectID);

      projectDB.setName(project.getName());
      projectDB.setOcsCode(project.getOcsCode());
      projectDB.setStartDate(project.getStartDate());
      projectDB.setEndDate(project.getEndDate());
      projectDB.setExtensionDate(project.getExtensionDate());
      projectDB.setDescription(project.getDescription());
      projectDB.setGlobal(this.bolValue(project.getsGlobal()));
      projectDB.setRegion(this.bolValue(project.getsRegion()));
      projectDB.setDirectDonor(project.getDirectDonor());
      projectDB.setOriginalDonor(project.getOriginalDonor());
      projectDB.setTotalAmount(project.getTotalAmount());
      projectDB.setSuggestedName(project.getSuggestedName());

      if (project.getProjectType().getId() != null) {
        CenterProjectType projectType = projectTypeService.getProjectTypeById(project.getProjectType().getId());
        projectDB.setProjectType(projectType);
      }

      if (project.getProjectLeader().getId() != null) {
        User projectLeader = userService.getUser(project.getProjectLeader().getId());
        projectDB.setProjectLeader(projectLeader);
      }

      long projectSaveID = projectService.saveCenterProject(projectDB);

      projectDB = projectService.getCenterProjectById(projectSaveID);

      if (project.getProjectCrosscutingTheme() != null) {
        this.saveCrossCuting(projectDB);
      }

      this.saveFundingSources(projectDB);
      this.saveOutputs(projectDB);
      this.saveLocations(projectDB);

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_FUNDING_SOURCE_RELATION);
      relationsName.add(APConstants.PROJECT_OUTPUT_RELATION);
      relationsName.add(APConstants.PROJECT_LOCATION_RELATION);
      project = projectService.getCenterProjectById(projectID);
      project.setActiveSince(new Date());
      project.setModifiedBy(this.getCurrentUser());
      projectService.saveCenterProject(project, this.getActionName(), relationsName);

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists()) {
        path.toFile().delete();
      }


      if (!this.getInvalidFields().isEmpty()) {
        this.setActionMessages(null);
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

  public void saveCrossCuting(CenterProject projectDB) {
    CenterProjectCrosscutingTheme crosscutingTheme = project.getProjectCrosscutingTheme();

    CenterProjectCrosscutingTheme crosscutingThemeSave =
      projectCrosscutingThemeService.getProjectCrosscutingThemeById(projectDB.getProjectCrosscutingTheme().getId());

    crosscutingThemeSave
      .setClimateChange(crosscutingTheme.getClimateChange() != null ? crosscutingTheme.getClimateChange() : false);
    crosscutingThemeSave.setGender(crosscutingTheme.getGender() != null ? crosscutingTheme.getGender() : false);
    crosscutingThemeSave.setYouth(crosscutingTheme.getYouth() != null ? crosscutingTheme.getYouth() : false);
    crosscutingThemeSave.setPoliciesInstitutions(
      crosscutingTheme.getPoliciesInstitutions() != null ? crosscutingTheme.getPoliciesInstitutions() : false);
    crosscutingThemeSave.setCapacityDevelopment(
      crosscutingTheme.getCapacityDevelopment() != null ? crosscutingTheme.getCapacityDevelopment() : false);
    crosscutingThemeSave.setBigData(crosscutingTheme.getBigData() != null ? crosscutingTheme.getBigData() : false);
    crosscutingThemeSave.setImpactAssessment(
      crosscutingTheme.getImpactAssessment() != null ? crosscutingTheme.getImpactAssessment() : false);
    crosscutingThemeSave.setNa(crosscutingTheme.getNa() != null ? crosscutingTheme.getNa() : false);

    crosscutingThemeSave.setProject(projectDB);

    projectCrosscutingThemeService.saveProjectCrosscutingTheme(crosscutingThemeSave);


  }


  public void saveFundingSources(CenterProject projectDB) {

    if (projectDB.getProjectFundingSources() != null && projectDB.getProjectFundingSources().size() > 0) {
      List<CenterProjectFundingSource> fundingSourcesPrew = new ArrayList<>(
        projectDB.getProjectFundingSources().stream().filter(pfs -> pfs.isActive()).collect(Collectors.toList()));

      for (CenterProjectFundingSource projectFundingSource : fundingSourcesPrew) {
        if (!project.getFundingSources().contains(projectFundingSource)) {
          projectFundingSourceService.deleteProjectFundingSource(projectFundingSource.getId());
        }
      }
    }

    if (project.getFundingSources() != null) {

      for (CenterProjectFundingSource projectFundingSource : project.getFundingSources()) {
        if (projectFundingSource.getId() == null || projectFundingSource.getId() == -1) {

          CenterProjectFundingSource fundingSourceSave = new CenterProjectFundingSource();

          CenterFundingSourceType fundingSourceType =
            fundingSourceService.getFundingSourceTypeById(projectFundingSource.getFundingSourceType().getId());
          CenterProject project = projectService.getCenterProjectById(projectID);
          Crp crp = crpService.getCrpById(projectFundingSource.getCrp().getId());

          fundingSourceSave.setProject(project);
          fundingSourceSave.setCrp(crp);
          fundingSourceSave.setFundingSourceType(fundingSourceType);
          fundingSourceSave.setTitle(projectFundingSource.getTitle());
          fundingSourceSave.setActive(true);
          fundingSourceSave.setActiveSince(new Date());
          fundingSourceSave.setCreatedBy(this.getCurrentUser());
          fundingSourceSave.setModifiedBy(this.getCurrentUser());
          fundingSourceSave.setModificationJustification("");

          projectFundingSourceService.saveProjectFundingSource(fundingSourceSave);

        } else {
          boolean hasChanges = false;
          CenterProjectFundingSource fundingSourcePrew =
            projectFundingSourceService.getProjectFundingSourceById(projectFundingSource.getId());

          if (!fundingSourcePrew.getFundingSourceType().equals(projectFundingSource.getFundingSourceType())) {
            hasChanges = true;
            CenterFundingSourceType fundingSourceType =
              fundingSourceService.getFundingSourceTypeById(projectFundingSource.getFundingSourceType().getId());
            fundingSourcePrew.setFundingSourceType(fundingSourceType);
          }

          if (hasChanges) {
            fundingSourcePrew.setModifiedBy(this.getCurrentUser());
            fundingSourcePrew.setActiveSince(new Date());
            projectFundingSourceService.saveProjectFundingSource(fundingSourcePrew);
          }

        }
      }


    }

  }


  public void saveLocations(CenterProject projectDB) {

    if (project.getProjectRegions() != null) {
      List<CenterProjectLocation> regions = new ArrayList<>(projectDB.getProjectLocations().stream()
        .filter(fl -> fl.isActive() && fl.getLocElement().getLocElementType().getId() == 1)
        .collect(Collectors.toList()));
      if (regions != null && regions.size() > 0) {
        if (!region) {
          for (CenterProjectLocation projectLocation : regions) {
            projectLocationService.deleteProjectLocation(projectLocation.getId());
          }
        } else {
          for (CenterProjectLocation projectLocation : regions) {
            if (!project.getProjectRegions().contains(projectLocation)) {
              projectLocationService.deleteProjectLocation(projectLocation.getId());
            }
          }
        }
      }

      for (CenterProjectLocation projectLocation : project.getProjectRegions()) {


        if (projectLocation.getId() == null || projectLocation.getId() == -1) {

          CenterProjectLocation projectLocationSave = new CenterProjectLocation();
          projectLocationSave.setActive(true);
          projectLocationSave.setActiveSince(new Date());
          projectLocationSave.setCreatedBy(this.getCurrentUser());
          projectLocationSave.setModifiedBy(this.getCurrentUser());
          projectLocationSave.setModificationJustification("");
          projectLocationSave.setProject(projectDB);

          LocElement element = locElementService.getLocElementById(projectLocation.getLocElement().getId());
          projectLocationSave.setLocElement(element);

          projectLocationService.saveProjectLocation(projectLocationSave);
        }
      }


    }

    if (project.getProjectCountries() != null)

    {

      List<CenterProjectLocation> countries = new ArrayList<>(projectDB.getProjectLocations().stream()
        .filter(fl -> fl.isActive() && fl.getLocElement().getLocElementType().getId() == 2)
        .collect(Collectors.toList()));

      if (countries != null && countries.size() > 0) {
        for (CenterProjectLocation projectLocation : countries) {
          if (!project.getProjectCountries().contains(projectLocation)) {
            projectLocationService.deleteProjectLocation(projectLocation.getId());
          }
        }
      }

      for (CenterProjectLocation projectLocation : project.getProjectCountries()) {


        if (projectLocation.getId() == null || projectLocation.getId() == -1) {

          CenterProjectLocation projectLocationSave = new CenterProjectLocation();
          projectLocationSave.setActive(true);
          projectLocationSave.setActiveSince(new Date());
          projectLocationSave.setCreatedBy(this.getCurrentUser());
          projectLocationSave.setModifiedBy(this.getCurrentUser());
          projectLocationSave.setModificationJustification("");
          projectLocationSave.setProject(projectDB);

          LocElement element = locElementService.getLocElementByISOCode(projectLocation.getLocElement().getIsoAlpha2());
          projectLocationSave.setLocElement(element);

          projectLocationService.saveProjectLocation(projectLocationSave);
        }
      }


    }

  }

  public void saveOutputs(CenterProject projectDB) {

    if (projectDB.getProjectOutputs() != null && projectDB.getProjectOutputs().size() > 0) {
      List<CenterProjectOutput> outputsPrew = new ArrayList<>(
        projectDB.getProjectOutputs().stream().filter(po -> po.isActive()).collect(Collectors.toList()));

      for (CenterProjectOutput output : outputsPrew) {
        if (!project.getOutputs().contains(output)) {
          projectOutputService.deleteProjectOutput(output.getId());
        }
      }
    }

    if (project.getOutputs() != null) {
      for (CenterProjectOutput output : project.getOutputs()) {
        if (output.getId() == null || output.getId() == -1) {
          CenterProjectOutput outputSave = new CenterProjectOutput();

          CenterOutput researchOutput = outputService.getResearchOutputById(output.getResearchOutput().getId());
          CenterProject project = projectService.getCenterProjectById(projectID);

          outputSave.setProject(project);
          outputSave.setResearchOutput(researchOutput);
          outputSave.setActive(true);
          outputSave.setCreatedBy(this.getCurrentUser());
          outputSave.setModifiedBy(this.getCurrentUser());
          outputSave.setActiveSince(new Date());
          outputSave.setModificationJustification("");

          projectOutputService.saveProjectOutput(outputSave);

        }
      }
    }


  }

  public void setAreaID(long areaID) {
    this.areaID = areaID;
  }

  public void setCountryLists(List<LocElement> countryLists) {
    this.countryLists = countryLists;
  }

  public void setCrps(List<Crp> crps) {
    this.crps = crps;
  }

  public void setFundingSourceTypes(List<CenterFundingSourceType> fundingSourceTypes) {
    this.fundingSourceTypes = fundingSourceTypes;
  }

  public void setLoggedCenter(Center loggedCenter) {
    this.loggedCenter = loggedCenter;
  }

  public void setOutputs(List<OutcomeOutputs> outputs) {
    this.outputs = outputs;
  }

  public void setPrincipalInvestigator(String principalInvestigator) {
    this.principalInvestigator = principalInvestigator;
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

  public void setProjectTypes(List<CenterProjectType> projectTypes) {
    this.projectTypes = projectTypes;
  }

  public void setRegion(boolean region) {
    this.region = region;
  }


  public void setRegionLists(List<LocElement> regionLists) {
    this.regionLists = regionLists;
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

  public void setTopicOutcomes(List<TopicOutcomes> topicOutcomes) {
    this.topicOutcomes = topicOutcomes;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, project, selectedProgram, true);
    }
  }

}
