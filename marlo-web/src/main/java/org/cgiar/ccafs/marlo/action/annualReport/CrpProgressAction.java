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
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrpProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrpProgressStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrpProgressTargetManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorTargetManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudyDTO;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgressStudy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgressTarget;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.CrpProgressValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
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
public class CrpProgressAction extends BaseAction {


  private static final long serialVersionUID = -3785412578513649561L;


  // Managers
  private GlobalUnitManager crpManager;

  private LiaisonInstitutionManager liaisonInstitutionManager;


  private ReportSynthesisManager reportSynthesisManager;

  private AuditLogManager auditLogManager;


  private UserManager userManager;

  private CrpProgramManager crpProgramManager;


  private ReportSynthesisCrpProgressManager reportSynthesisCrpProgressManager;

  private CrpProgressValidator validator;


  private ProjectFocusManager projectFocusManager;


  private ProjectManager projectManager;


  private ProjectExpectedStudyManager projectExpectedStudyManager;


  private ReportSynthesisCrpProgressStudyManager reportSynthesisCrpProgressStudyManager;


  private ReportSynthesisCrpProgressTargetManager reportSynthesisCrpProgressTargetManager;

  private SrfSloIndicatorTargetManager srfSloIndicatorTargetManager;


  private PhaseManager phaseManager;

  // Variables
  private String transaction;

  private ReportSynthesis reportSynthesis;

  private Long liaisonInstitutionID;

  private Long synthesisID;

  private LiaisonInstitution liaisonInstitution;

  private GlobalUnit loggedCrp;

  private List<LiaisonInstitution> liaisonInstitutions;

  private List<ProjectExpectedStudy> studiesList;

  private List<SrfSloIndicatorTarget> sloTargets;

  private List<PowbEvidencePlannedStudyDTO> flagshipPlannedList;

  private List<ReportSynthesisCrpProgressTarget> fpSynthesisTable;

  private List<ReportSynthesisCrpProgress> flagshipCrpProgress;


  @Inject
  public CrpProgressAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager,
    ReportSynthesisCrpProgressManager reportSynthesisCrpProgressManager, AuditLogManager auditLogManager,
    UserManager userManager, CrpProgramManager crpProgramManager, ReportSynthesisManager reportSynthesisManager,
    CrpProgressValidator validator, ProjectFocusManager projectFocusManager, ProjectManager projectManager,
    ProjectExpectedStudyManager projectExpectedStudyManager,
    ReportSynthesisCrpProgressStudyManager reportSynthesisCrpProgressStudyManager,
    ReportSynthesisCrpProgressTargetManager reportSynthesisCrpProgressTargetManager,
    SrfSloIndicatorTargetManager srfSloIndicatorTargetManager, PhaseManager phaseManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.validator = validator;
    this.projectFocusManager = projectFocusManager;
    this.projectManager = projectManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.reportSynthesisCrpProgressTargetManager = reportSynthesisCrpProgressTargetManager;
    this.reportSynthesisCrpProgressStudyManager = reportSynthesisCrpProgressStudyManager;
    this.srfSloIndicatorTargetManager = srfSloIndicatorTargetManager;
    this.reportSynthesisCrpProgressManager = reportSynthesisCrpProgressManager;
    this.phaseManager = phaseManager;
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

  public void expectedStudiesNewData(ReportSynthesisCrpProgress crpProgressDB) {

    List<Long> selectedPs = new ArrayList<>();
    List<Long> studiesIds = new ArrayList<>();

    for (ProjectExpectedStudy std : studiesList) {
      studiesIds.add(std.getId());
    }

    if (reportSynthesis.getReportSynthesisCrpProgress().getPlannedStudiesValue() != null
      && reportSynthesis.getReportSynthesisCrpProgress().getPlannedStudiesValue().length() > 0) {
      List<Long> stList = new ArrayList<>();
      for (String string : reportSynthesis.getReportSynthesisCrpProgress().getPlannedStudiesValue().trim().split(",")) {
        stList.add(Long.parseLong(string.trim()));
      }


      for (Long studyId : studiesIds) {
        int index = stList.indexOf(studyId);
        if (index < 0) {
          selectedPs.add(studyId);
        }


      }

      for (ReportSynthesisCrpProgressStudy reportStudy : crpProgressDB.getReportSynthesisCrpProgressStudies().stream()
        .filter(rio -> rio.isActive()).collect(Collectors.toList())) {
        if (!selectedPs.contains(reportStudy.getProjectExpectedStudy().getId())) {
          reportSynthesisCrpProgressStudyManager.deleteReportSynthesisCrpProgressStudy(reportStudy.getId());
        }
      }

      for (Long studyId : selectedPs) {
        ProjectExpectedStudy expectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(studyId);

        ReportSynthesisCrpProgressStudy crpPlannedStudyNew = new ReportSynthesisCrpProgressStudy();
        crpPlannedStudyNew = new ReportSynthesisCrpProgressStudy();
        crpPlannedStudyNew.setProjectExpectedStudy(expectedStudy);
        crpPlannedStudyNew.setReportSynthesisCrpProgress(crpProgressDB);

        List<ReportSynthesisCrpProgressStudy> crpPlannedStudies = crpProgressDB.getReportSynthesisCrpProgressStudies()
          .stream().filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!crpPlannedStudies.contains(crpPlannedStudyNew)) {
          crpPlannedStudyNew =
            reportSynthesisCrpProgressStudyManager.saveReportSynthesisCrpProgressStudy(crpPlannedStudyNew);
        }
      }
    } else {

      for (Long studyId : studiesIds) {
        ProjectExpectedStudy expectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(studyId);

        ReportSynthesisCrpProgressStudy crpPlannedStudyNew = new ReportSynthesisCrpProgressStudy();
        crpPlannedStudyNew = new ReportSynthesisCrpProgressStudy();
        crpPlannedStudyNew.setProjectExpectedStudy(expectedStudy);
        crpPlannedStudyNew.setReportSynthesisCrpProgress(crpProgressDB);

        List<ReportSynthesisCrpProgressStudy> reportPlannedStudies = crpProgressDB
          .getReportSynthesisCrpProgressStudies().stream().filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!reportPlannedStudies.contains(crpPlannedStudyNew)) {
          crpPlannedStudyNew =
            reportSynthesisCrpProgressStudyManager.saveReportSynthesisCrpProgressStudy(crpPlannedStudyNew);
        }
      }

    }
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
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<ReportSynthesisCrpProgress> getFlagshipCrpProgress() {
    return flagshipCrpProgress;
  }


  public List<PowbEvidencePlannedStudyDTO> getFlagshipPlannedList() {
    return flagshipPlannedList;
  }


  public List<ReportSynthesisCrpProgressTarget> getFpSynthesisTable() {
    return fpSynthesisTable;
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


  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }

  public List<SrfSloIndicatorTarget> getSloTargets() {
    return sloTargets;
  }

  public List<ProjectExpectedStudy> getStudiesList() {
    return studiesList;
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
        this.studiesList(phase.getId(), liaisonInstitution);
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

        if (this.isFlagship()) {
          if (reportSynthesis.getReportSynthesisCrpProgress().getPlannedStudiesValue() != null) {
            String[] studyValues = reportSynthesis.getReportSynthesisCrpProgress().getPlannedStudiesValue().split(",");
            reportSynthesis.getReportSynthesisCrpProgress().setExpectedStudies(new ArrayList<>());


            for (int i = 0; i < studyValues.length; i++) {

              ProjectExpectedStudy study =
                projectExpectedStudyManager.getProjectExpectedStudyById(Long.parseLong(studyValues[i]));
              reportSynthesis.getReportSynthesisCrpProgress().getExpectedStudies().add(study);
            }
          }
        }

        this.setDraft(true);
      } else {

        this.setDraft(false);
        // Check if relation is null -create it
        if (reportSynthesis.getReportSynthesisCrpProgress() == null) {
          ReportSynthesisCrpProgress crpProgress = new ReportSynthesisCrpProgress();
          // create one to one relation
          reportSynthesis.setReportSynthesisCrpProgress(crpProgress);;
          crpProgress.setReportSynthesis(reportSynthesis);
          // save the changes
          reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }


        if (this.isFlagship()) {
          // Srf Targets List
          if (reportSynthesis.getReportSynthesisCrpProgress().getReportSynthesisCrpProgressTargets() != null) {
            reportSynthesis.getReportSynthesisCrpProgress().setSloTargets(
              new ArrayList<>(reportSynthesis.getReportSynthesisCrpProgress().getReportSynthesisCrpProgressTargets()
                .stream().filter(t -> t.isActive()).collect(Collectors.toList())));
          }
          // Crp Progress Studies
          reportSynthesis.getReportSynthesisCrpProgress().setExpectedStudies(new ArrayList<>());
          if (reportSynthesis.getReportSynthesisCrpProgress().getReportSynthesisCrpProgressStudies() != null
            && !reportSynthesis.getReportSynthesisCrpProgress().getReportSynthesisCrpProgressStudies().isEmpty()) {
            for (ReportSynthesisCrpProgressStudy plannedStudy : reportSynthesis.getReportSynthesisCrpProgress()
              .getReportSynthesisCrpProgressStudies().stream().filter(ro -> ro.isActive())
              .collect(Collectors.toList())) {
              reportSynthesis.getReportSynthesisCrpProgress().getExpectedStudies()
                .add(plannedStudy.getProjectExpectedStudy());
            }
          }
        }
      }
    }

    if (this.isFlagship()) {
      sloTargets = new ArrayList<>(srfSloIndicatorTargetManager.findAll().stream()
        .filter(sr -> sr.isActive() && sr.getYear() == 2022).collect(Collectors.toList()));
    }


    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));


    if (this.isPMU()) {

      // Table A-2 PMU Information
      flagshipPlannedList = reportSynthesisCrpProgressManager.getPlannedList(liaisonInstitutions, phase.getId(),
        loggedCrp, this.liaisonInstitution);
      // Table A-1 Evidence on Progress
      fpSynthesisTable = reportSynthesisCrpProgressTargetManager.flagshipSynthesis(liaisonInstitutions, phase.getId());

      // Flagships Synthesis Progress
      flagshipCrpProgress =
        reportSynthesisCrpProgressManager.getFlagshipCrpProgress(liaisonInstitutions, phase.getId());


    }

    // ADD PMU as liasion Institution too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));


    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_CRP_PROGRESS_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisCrpProgress().getSloTargets() != null) {
        reportSynthesis.getReportSynthesisCrpProgress().getSloTargets().clear();
      }

      if (reportSynthesis.getReportSynthesisCrpProgress().getPlannedStudies() != null) {
        reportSynthesis.getReportSynthesisCrpProgress().getPlannedStudies().clear();
      }
    }
  }


  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {


      ReportSynthesisCrpProgress crpProgressDB =
        reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisCrpProgress();
      if (this.isFlagship()) {

        if (reportSynthesis.getReportSynthesisCrpProgress().getPlannedStudies() == null) {
          reportSynthesis.getReportSynthesisCrpProgress().setPlannedStudies(new ArrayList<>());
        }

        this.expectedStudiesNewData(crpProgressDB);
        this.saveSrfTargets(crpProgressDB);

      }

      crpProgressDB.setOverallProgress(reportSynthesis.getReportSynthesisCrpProgress().getOverallProgress());
      crpProgressDB.setSummaries(reportSynthesis.getReportSynthesisCrpProgress().getSummaries());

      crpProgressDB = reportSynthesisCrpProgressManager.saveReportSynthesisCrpProgress(crpProgressDB);


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

  /**
   * Save Crp Progress Srf Targets Information
   * 
   * @param crpProgressDB
   */
  public void saveSrfTargets(ReportSynthesisCrpProgress crpProgressDB) {


    // Search and deleted form Information
    if (crpProgressDB.getReportSynthesisCrpProgressTargets() != null
      && crpProgressDB.getReportSynthesisCrpProgressTargets().size() > 0) {

      List<ReportSynthesisCrpProgressTarget> targetPrev = new ArrayList<>(crpProgressDB
        .getReportSynthesisCrpProgressTargets().stream().filter(nu -> nu.isActive()).collect(Collectors.toList()));

      for (ReportSynthesisCrpProgressTarget crpTarget : targetPrev) {
        if (!reportSynthesis.getReportSynthesisCrpProgress().getSloTargets().contains(crpTarget)) {
          reportSynthesisCrpProgressTargetManager.deleteReportSynthesisCrpProgressTarget(crpTarget.getId());
        }
      }
    }

    // Save form Information
    if (reportSynthesis.getReportSynthesisCrpProgress().getSloTargets() != null) {
      for (ReportSynthesisCrpProgressTarget crpTarget : reportSynthesis.getReportSynthesisCrpProgress()
        .getSloTargets()) {
        if (crpTarget.getId() == null) {
          ReportSynthesisCrpProgressTarget crpTargetSave = new ReportSynthesisCrpProgressTarget();

          crpTargetSave.setReportSynthesisCrpProgress(crpProgressDB);

          SrfSloIndicatorTarget sloIndicator =
            srfSloIndicatorTargetManager.getSrfSloIndicatorTargetById(crpTarget.getSrfSloIndicatorTarget().getId());

          crpTargetSave.setBirefSummary(crpTarget.getBirefSummary());
          crpTargetSave.setAdditionalContribution(crpTarget.getAdditionalContribution());

          crpTargetSave.setSrfSloIndicatorTarget(sloIndicator);

          reportSynthesisCrpProgressTargetManager.saveReportSynthesisCrpProgressTarget(crpTargetSave);
        } else {

          boolean hasChanges = false;
          ReportSynthesisCrpProgressTarget crpTargetPrev =
            reportSynthesisCrpProgressTargetManager.getReportSynthesisCrpProgressTargetById(crpTarget.getId());

          if (!crpTargetPrev.getBirefSummary().equals(crpTarget.getBirefSummary())) {
            hasChanges = true;
            crpTargetPrev.setBirefSummary(crpTarget.getBirefSummary());
          }

          if (!crpTargetPrev.getAdditionalContribution().equals(crpTarget.getAdditionalContribution())) {
            hasChanges = true;
            crpTargetPrev.setAdditionalContribution(crpTarget.getAdditionalContribution());
          }

          if (hasChanges) {
            reportSynthesisCrpProgressTargetManager.saveReportSynthesisCrpProgressTarget(crpTargetPrev);
          }
        }
      }
    }


  }

  public void setFlagshipCrpProgress(List<ReportSynthesisCrpProgress> flagshipCrpProgress) {
    this.flagshipCrpProgress = flagshipCrpProgress;
  }


  public void setFlagshipPlannedList(List<PowbEvidencePlannedStudyDTO> flagshipPlannedList) {
    this.flagshipPlannedList = flagshipPlannedList;
  }


  public void setFpSynthesisTable(List<ReportSynthesisCrpProgressTarget> fpSynthesisTable) {
    this.fpSynthesisTable = fpSynthesisTable;
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

  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }

  public void setSloTargets(List<SrfSloIndicatorTarget> sloTargets) {
    this.sloTargets = sloTargets;
  }

  public void setStudiesList(List<ProjectExpectedStudy> studiesList) {
    this.studiesList = studiesList;
  }

  public void setSynthesisID(Long synthesisID) {
    this.synthesisID = synthesisID;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  public void studiesList(long phaseID, LiaisonInstitution liaisonInstitution) {

    studiesList = new ArrayList<>();

    Phase phase = phaseManager.getPhaseById(phaseID);

    if (projectFocusManager.findAll() != null) {

      List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
        .filter(pf -> pf.isActive() && pf.getCrpProgram().getId() == liaisonInstitution.getCrpProgram().getId()
          && pf.getPhase() != null && pf.getPhase().getId() == phaseID)
        .collect(Collectors.toList()));

      for (ProjectFocus focus : projectFocus) {
        Project project = projectManager.getProjectById(focus.getProject().getId());
        List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(project.getProjectExpectedStudies().stream()
          .filter(es -> es.isActive() && es.getYear() == this.getCurrentCycleYear()).collect(Collectors.toList()));
        for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
          if (projectExpectedStudy.getProjectExpectedStudyInfo(phase) != null) {
            if (projectExpectedStudy.getProjectExpectedStudyInfo(phase).getStudyType() != null
              && projectExpectedStudy.getProjectExpectedStudyInfo(phase).getStudyType().getId() == 1) {
              studiesList.add(projectExpectedStudy);
            }
          }
        }
      }

      List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
        .filter(es -> es.isActive() && es.getYear() == this.getCurrentCycleYear() && es.getProject() == null)
        .collect(Collectors.toList()));

      for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
        if (projectExpectedStudy.getProjectExpectedStudyInfo(phase) != null) {
          List<ProjectExpectedStudyFlagship> studiesPrograms =
            new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyFlagships().stream()
              .filter(s -> s.isActive() && s.getPhase().getId() == phase.getId()).collect(Collectors.toList()));
          for (ProjectExpectedStudyFlagship projectExpectedStudyFlagship : studiesPrograms) {
            CrpProgram crpProgram = liaisonInstitution.getCrpProgram();
            if (crpProgram.equals(projectExpectedStudyFlagship.getCrpProgram())) {
              if (projectExpectedStudy.getProjectExpectedStudyInfo(phase) != null) {
                if (projectExpectedStudy.getProjectExpectedStudyInfo(phase).getStudyType() != null
                  && projectExpectedStudy.getProjectExpectedStudyInfo(phase).getStudyType().getId() == 1) {
                  studiesList.add(projectExpectedStudy);
                  break;
                }
              }
            }
          }
        }
      }

      for (ProjectExpectedStudy projectExpectedStudy : studiesList) {
        if (projectExpectedStudy.getProjectExpectedStudySubIdos() != null
          && !projectExpectedStudy.getProjectExpectedStudySubIdos().isEmpty()) {
          projectExpectedStudy.setSubIdos(new ArrayList<>(projectExpectedStudy.getProjectExpectedStudySubIdos().stream()
            .filter(s -> s.getPhase().getId() == phase.getId()).collect(Collectors.toList())));
        }
      }

    }
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, reportSynthesis, true);
    }
  }

}
