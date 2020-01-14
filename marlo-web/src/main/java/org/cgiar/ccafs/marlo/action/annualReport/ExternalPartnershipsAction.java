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

package org.cgiar.ccafs.marlo.action.annualReport;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisExternalPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisExternalPartnershipProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnership;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnership;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnershipDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnershipProject;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.ExternalPartnershipsValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ExternalPartnershipsAction extends BaseAction {


  private static final long serialVersionUID = 575869881576979848L;


  // Managers
  private GlobalUnitManager crpManager;


  private LiaisonInstitutionManager liaisonInstitutionManager;


  private ReportSynthesisManager reportSynthesisManager;

  private AuditLogManager auditLogManager;


  private UserManager userManager;

  private CrpProgramManager crpProgramManager;


  private ReportSynthesisExternalPartnershipManager reportSynthesisExternalPartnershipManager;
  private ExternalPartnershipsValidator validator;
  private ProjectFocusManager projectFocusManager;
  private ProjectManager projectManager;
  private ProjectPartnerPartnershipManager projectPartnerPartnershipManager;
  private ReportSynthesisExternalPartnershipProjectManager reportSynthesisExternalPartnershipProjectManager;
  private PhaseManager phaseManager;
  // Variables
  private String transaction;
  private ReportSynthesis reportSynthesis;
  private Long liaisonInstitutionID;
  private Long synthesisID;
  private LiaisonInstitution liaisonInstitution;
  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<ProjectPartnerPartnership> partnerShipList;
  private List<ReportSynthesisExternalPartnership> flagshipExternalPartnerships;
  private List<ReportSynthesisExternalPartnershipDTO> flagshipPlannedList;

  @Inject
  public ExternalPartnershipsAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, CrpProgramManager crpProgramManager,
    ReportSynthesisExternalPartnershipManager reportSynthesisExternalPartnershipManager,
    ExternalPartnershipsValidator validator, ProjectFocusManager projectFocusManager, ProjectManager projectManager,
    ProjectPartnerPartnershipManager projectPartnerPartnershipManager,
    ReportSynthesisExternalPartnershipProjectManager reportSynthesisExternalPartnershipProjectManager,
    PhaseManager phaseManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.reportSynthesisExternalPartnershipManager = reportSynthesisExternalPartnershipManager;
    this.validator = validator;
    this.projectFocusManager = projectFocusManager;
    this.projectManager = projectManager;
    this.projectPartnerPartnershipManager = projectPartnerPartnershipManager;
    this.reportSynthesisExternalPartnershipProjectManager = reportSynthesisExternalPartnershipProjectManager;
    this.phaseManager = phaseManager;
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
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = reportSynthesis.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    String fl = config.getAutoSaveFolder();
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public GlobalUnitManager getCrpManager() {
    return crpManager;
  }

  public List<ReportSynthesisExternalPartnership> getFlagshipExternalPartnerships() {
    return flagshipExternalPartnerships;
  }

  public List<ReportSynthesisExternalPartnershipDTO> getFlagshipPlannedList() {
    return flagshipPlannedList;
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

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }


  public List<ProjectPartnerPartnership> getPartnerShipList() {
    return partnerShipList;
  }

  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }

  public Long getSynthesisID() {
    return synthesisID;
  }

  public String getTransaction() {
    return transaction;
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

  public void partnerShipList(long phaseID, LiaisonInstitution liaisonInstitution) {

    partnerShipList = new ArrayList<>();


    if (projectFocusManager.findAll() != null) {

      List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
        .filter(pf -> pf.isActive() && pf.getCrpProgram().getId() == liaisonInstitution.getCrpProgram().getId()
          && pf.getPhase() != null && pf.getPhase().getId() == phaseID)
        .collect(Collectors.toList()));

      for (ProjectFocus focus : projectFocus) {
        Project project = projectManager.getProjectById(focus.getProject().getId());

        List<ProjectPartner> projectPartners = new ArrayList<>(project.getProjectPartners().stream()
          .filter(pp -> pp.isActive() && pp.getPhase() != null && pp.getPhase().getId() == phaseID)
          .collect(Collectors.toList()));
        Collections.sort(projectPartners,
          (p1, p2) -> p1.getInstitution().getId().compareTo(p2.getInstitution().getId()));

        for (ProjectPartner projectPartner : projectPartners) {
          List<ProjectPartnerPartnership> projectPartnerPartnerships = new ArrayList<>(projectPartner
            .getProjectPartnerPartnerships().stream().filter(ppp -> ppp.isActive()).collect(Collectors.toList()));
          for (ProjectPartnerPartnership projectPartnerPartnership : projectPartnerPartnerships) {

            // Set up list
            projectPartnerPartnership.setPartnershipResearchPhases(new ArrayList<>());
            if (projectPartnerPartnership.getProjectPartnerPartnershipResearchPhases() != null
              || !projectPartnerPartnership.getProjectPartnerPartnershipResearchPhases().isEmpty()) {
              projectPartnerPartnership.getPartnershipResearchPhases()
                .addAll(projectPartnerPartnership.getProjectPartnerPartnershipResearchPhases().stream()
                  .filter(p -> p.isActive()).collect(Collectors.toList()));
            }

            projectPartnerPartnership.setPartnershipLocations(new ArrayList<>());
            if (projectPartnerPartnership.getProjectPartnerPartnershipLocations() != null
              || !projectPartnerPartnership.getProjectPartnerPartnershipLocations().isEmpty()) {
              projectPartnerPartnership.getPartnershipLocations()
                .addAll(projectPartnerPartnership.getProjectPartnerPartnershipLocations().stream()
                  .filter(p -> p.isActive()).collect(Collectors.toList()));
            }

            partnerShipList.add(projectPartnerPartnership);
          }
        }
      }
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
      ReportSynthesis history = (ReportSynthesis) auditLogManager.getHistory(transaction);
      if (history != null) {
        reportSynthesis = history;
        synthesisID = reportSynthesis.getId();
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
          List<LiaisonUser> liaisonUsers = new ArrayList<>(user.getLiasonsUsers().stream()
            .filter(lu -> lu.isActive() && lu.getLiaisonInstitution().isActive()
              && lu.getLiaisonInstitution().getCrp().getId() == loggedCrp.getId()
              && lu.getLiaisonInstitution().getInstitution() == null)
            .collect(Collectors.toList()));
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
                  if (institution.getAcronym() != null && institution.getAcronym().equals("PMU")) {
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
        synthesisID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.REPORT_SYNTHESIS_ID)));
        reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);

        if (!reportSynthesis.getPhase().equals(phase)) {
          reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);
          if (reportSynthesis == null) {
            reportSynthesis = this.createReportSynthesis(phase.getId(), liaisonInstitutionID);
          }
          synthesisID = reportSynthesis.getId();
        }
      } catch (Exception e) {
        reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);
        if (reportSynthesis == null) {
          reportSynthesis = this.createReportSynthesis(phase.getId(), liaisonInstitutionID);
        }
        synthesisID = reportSynthesis.getId();

      }
    }


    if (reportSynthesis != null) {

      ReportSynthesis reportSynthesisDB = reportSynthesisManager.getReportSynthesisById(reportSynthesis.getId());
      synthesisID = reportSynthesisDB.getId();
      liaisonInstitutionID = reportSynthesisDB.getLiaisonInstitution().getId();
      liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);

      // Fill Flagship Expected Studies
      if (this.isFlagship()) {
        this.partnerShipList(phase.getId(), liaisonInstitution);
      }


      Path path = this.getAutoSaveFilePath();
      // Verify if there is a Draft file
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();
        AutoSaveReader autoSaveReader = new AutoSaveReader();
        reportSynthesis = (ReportSynthesis) autoSaveReader.readFromJson(jReader);
        synthesisID = reportSynthesis.getId();
        this.setDraft(true);
      } else {

        this.setDraft(false);
        // Check if relation is null -create it
        if (reportSynthesis.getReportSynthesisExternalPartnership() == null) {
          ReportSynthesisExternalPartnership externalPartnership = new ReportSynthesisExternalPartnership();
          // create one to one relation
          reportSynthesis.setReportSynthesisExternalPartnership(externalPartnership);
          externalPartnership.setReportSynthesis(reportSynthesis);
          // save the changes
          reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }


        if (this.isFlagship()) {

          // External PartnerShips
          reportSynthesis.getReportSynthesisExternalPartnership().setPartnerPartnerships(new ArrayList<>());
          if (reportSynthesis.getReportSynthesisExternalPartnership()
            .getReportSynthesisExternalPartnershipProjects() != null
            && !reportSynthesis.getReportSynthesisExternalPartnership().getReportSynthesisExternalPartnershipProjects()
              .isEmpty()) {
            for (ReportSynthesisExternalPartnershipProject externalPartnership : reportSynthesis
              .getReportSynthesisExternalPartnership().getReportSynthesisExternalPartnershipProjects().stream()
              .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
              reportSynthesis.getReportSynthesisExternalPartnership().getPartnerPartnerships()
                .add(externalPartnership.getProjectPartnerPartnership());
            }
          }
        }
      }
    }


    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));


    if (this.isPMU()) {
      // Table G
      flagshipPlannedList = reportSynthesisExternalPartnershipManager.getPlannedPartnershipList(liaisonInstitutions,
        phase.getId(), loggedCrp, this.liaisonInstitution);

      // Flagship External Partnership Synthesis Progress
      flagshipExternalPartnerships =
        reportSynthesisExternalPartnershipManager.getFlagshipCExternalPartnership(liaisonInstitutions, phase.getId());
    }

    // ADD PMU as liasion Institution too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym() != null && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_EXTERNAL_PARTNERSHIP_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisExternalPartnership().getPartnerPartnerships() != null) {
        reportSynthesis.getReportSynthesisExternalPartnership().getPartnerPartnerships().clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {


      ReportSynthesisExternalPartnership externalPartnershipDB =
        reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisExternalPartnership();

      if (this.isFlagship()) {

        if (reportSynthesis.getReportSynthesisExternalPartnership().getPartnerPartnerships() == null) {
          reportSynthesis.getReportSynthesisExternalPartnership().setPartnerPartnerships(new ArrayList<>());
        }

        this.saveExternalPartnership(externalPartnershipDB);


      }

      externalPartnershipDB.setHighlights(reportSynthesis.getReportSynthesisExternalPartnership().getHighlights());


      externalPartnershipDB =
        reportSynthesisExternalPartnershipManager.saveReportSynthesisExternalPartnership(externalPartnershipDB);


      List<String> relationsName = new ArrayList<>();
      reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);

      /**
       * The following is required because we need to update something on the @ReportSynthesis if we want a row created
       * in the auditlog table.
       */
      this.setModificationJustification(reportSynthesis);

      reportSynthesisManager.save(reportSynthesis, this.getActionName(), relationsName, this.getActualPhase());


      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      Collection<String> messages = this.getActionMessages();
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

  public void saveExternalPartnership(ReportSynthesisExternalPartnership externalPartnershipDB) {

    List<Long> selectedPs = new ArrayList<>();
    List<Long> studiesIds = new ArrayList<>();

    for (ProjectPartnerPartnership std : partnerShipList) {
      studiesIds.add(std.getId());
    }

    if (reportSynthesis.getReportSynthesisExternalPartnership().getPartnershipsValue() != null
      && reportSynthesis.getReportSynthesisExternalPartnership().getPartnershipsValue().length() > 0) {
      List<Long> stList = new ArrayList<>();
      for (String string : reportSynthesis.getReportSynthesisExternalPartnership().getPartnershipsValue().trim()
        .split(",")) {
        stList.add(Long.parseLong(string.trim()));
      }

      for (Long studyId : studiesIds) {
        int index = stList.indexOf(studyId);
        if (index < 0) {
          selectedPs.add(studyId);
        }
      }

      for (ReportSynthesisExternalPartnershipProject reportStudy : externalPartnershipDB
        .getReportSynthesisExternalPartnershipProjects().stream().filter(rio -> rio.isActive())
        .collect(Collectors.toList())) {
        if (!selectedPs.contains(reportStudy.getProjectPartnerPartnership().getId())) {
          reportSynthesisExternalPartnershipProjectManager
            .deleteReportSynthesisExternalPartnershipProject(reportStudy.getId());
        }
      }

      for (Long studyId : selectedPs) {
        ProjectPartnerPartnership projectPartnerPartnership =
          projectPartnerPartnershipManager.getProjectPartnerPartnershipById(studyId);

        ReportSynthesisExternalPartnershipProject projectPartnerPartnershipNew =
          new ReportSynthesisExternalPartnershipProject();
        projectPartnerPartnershipNew = new ReportSynthesisExternalPartnershipProject();
        projectPartnerPartnershipNew.setProjectPartnerPartnership(projectPartnerPartnership);
        projectPartnerPartnershipNew.setReportSynthesisExternalPartnership(externalPartnershipDB);

        List<ReportSynthesisExternalPartnershipProject> externalPartnershipProjects =
          externalPartnershipDB.getReportSynthesisExternalPartnershipProjects().stream().filter(rio -> rio.isActive())
            .collect(Collectors.toList());


        if (!externalPartnershipProjects.contains(projectPartnerPartnershipNew)) {
          projectPartnerPartnershipNew = reportSynthesisExternalPartnershipProjectManager
            .saveReportSynthesisExternalPartnershipProject(projectPartnerPartnershipNew);
        }
      }
    } else {

      for (Long studyId : studiesIds) {
        ProjectPartnerPartnership projectPartnerPartnership =
          projectPartnerPartnershipManager.getProjectPartnerPartnershipById(studyId);

        ReportSynthesisExternalPartnershipProject projectPartnerPartnershipNew =
          new ReportSynthesisExternalPartnershipProject();
        projectPartnerPartnershipNew = new ReportSynthesisExternalPartnershipProject();
        projectPartnerPartnershipNew.setProjectPartnerPartnership(projectPartnerPartnership);
        projectPartnerPartnershipNew.setReportSynthesisExternalPartnership(externalPartnershipDB);

        List<ReportSynthesisExternalPartnershipProject> externalPartnershipProjects =
          externalPartnershipDB.getReportSynthesisExternalPartnershipProjects().stream().filter(rio -> rio.isActive())
            .collect(Collectors.toList());


        if (!externalPartnershipProjects.contains(projectPartnerPartnershipNew)) {
          projectPartnerPartnershipNew = reportSynthesisExternalPartnershipProjectManager
            .saveReportSynthesisExternalPartnershipProject(projectPartnerPartnershipNew);
        }
      }

    }
  }

  public void setCrpManager(GlobalUnitManager crpManager) {
    this.crpManager = crpManager;
  }


  public void setFlagshipExternalPartnerships(List<ReportSynthesisExternalPartnership> flagshipExternalPartnerships) {
    this.flagshipExternalPartnerships = flagshipExternalPartnerships;
  }

  public void setFlagshipPlannedList(List<ReportSynthesisExternalPartnershipDTO> flagshipPlannedList) {
    this.flagshipPlannedList = flagshipPlannedList;
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

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setPartnerShipList(List<ProjectPartnerPartnership> partnerShipList) {
    this.partnerShipList = partnerShipList;
  }

  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }

  public void setSynthesisID(Long synthesisID) {
    this.synthesisID = synthesisID;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }


  @Override
  public void validate() {
    if (save) {
      validator.validate(this, reportSynthesis, true);
    }
  }
}
