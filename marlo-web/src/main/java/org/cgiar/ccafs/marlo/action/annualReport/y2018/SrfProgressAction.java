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

package org.cgiar.ccafs.marlo.action.annualReport.y2018;

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
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressTargetManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorTargetManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgressTarget;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTarget;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.y2018.SrfProgressValidator;

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
public class SrfProgressAction extends BaseAction {


  private static final long serialVersionUID = -3785412578513649561L;


  // Managers
  private GlobalUnitManager crpManager;

  private LiaisonInstitutionManager liaisonInstitutionManager;


  private ReportSynthesisManager reportSynthesisManager;

  private AuditLogManager auditLogManager;


  private UserManager userManager;

  private CrpProgramManager crpProgramManager;


  private ReportSynthesisSrfProgressManager reportSynthesisSrfProgressManager;

  private SrfProgressValidator validator;

  private ReportSynthesisSrfProgressTargetManager reportSynthesisSrfProgressTargetManager;


  private SrfSloIndicatorTargetManager srfSloIndicatorTargetManager;

  private ProjectManager projectManager;


  private PhaseManager phaseManager;

  private ProjectFocusManager projectFocusManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;

  // Variables
  private String transaction;

  private ReportSynthesis reportSynthesis;

  private Long liaisonInstitutionID;

  private Long synthesisID;

  private LiaisonInstitution liaisonInstitution;

  private GlobalUnit loggedCrp;

  private List<LiaisonInstitution> liaisonInstitutions;

  private List<SrfSloIndicatorTarget> sloTargets;

  private List<ReportSynthesisCrpProgressTarget> fpSynthesisTable;

  private List<ReportSynthesisSrfProgress> flagshipSrfProgress;

  private List<ProjectExpectedStudy> studiesList;


  @Inject
  public SrfProgressAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager,
    ReportSynthesisCrpProgressManager reportSynthesisCrpProgressManager, AuditLogManager auditLogManager,
    UserManager userManager, CrpProgramManager crpProgramManager, ReportSynthesisManager reportSynthesisManager,
    SrfProgressValidator validator, ProjectFocusManager projectFocusManager, ProjectManager projectManager,
    ProjectExpectedStudyManager projectExpectedStudyManager,
    ReportSynthesisCrpProgressStudyManager reportSynthesisCrpProgressStudyManager,
    ReportSynthesisCrpProgressTargetManager reportSynthesisCrpProgressTargetManager,
    SrfSloIndicatorTargetManager srfSloIndicatorTargetManager, PhaseManager phaseManager,
    ReportSynthesisSrfProgressTargetManager reportSynthesisSrfProgressTargetManager,
    ReportSynthesisSrfProgressManager reportSynthesisSrfProgressManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.validator = validator;
    this.srfSloIndicatorTargetManager = srfSloIndicatorTargetManager;
    this.phaseManager = phaseManager;
    this.reportSynthesisSrfProgressTargetManager = reportSynthesisSrfProgressTargetManager;
    this.reportSynthesisSrfProgressManager = reportSynthesisSrfProgressManager;
    this.projectFocusManager = projectFocusManager;
    this.projectManager = projectManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
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

  public List<ReportSynthesisSrfProgress> getFlagshipSrfProgress() {
    return flagshipSrfProgress;
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

  /**
   * Get the information list for the Flagships Slo Targets Information in the form
   *
   * @param markerID
   * @return
   */
  public List<ReportSynthesisSrfProgressTarget> getTargetsFlagshipInfo(long targetID) {

    List<ReportSynthesisSrfProgressTarget> targets = new ArrayList<ReportSynthesisSrfProgressTarget>();

    ReportSynthesisSrfProgressTarget target = new ReportSynthesisSrfProgressTarget();

    // Get the list of liaison institutions Flagships and PMU.
    List<LiaisonInstitution> liaisonInstitutionsFg = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutionsFg.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

    for (LiaisonInstitution liaisonInstitution : liaisonInstitutionsFg) {
      target = reportSynthesisSrfProgressTargetManager.getSrfProgressTargetInfo(liaisonInstitution,
        this.getActualPhase().getId(), targetID);
      targets.add(target);
    }
    return targets;
  }


  /**
   * Get the information for the Slo targets in the form
   * 
   * @param markerID
   * @return
   */
  public ReportSynthesisSrfProgressTarget getTargetsInfo(long targetID) {
    ReportSynthesisSrfProgressTarget target = new ReportSynthesisSrfProgressTarget();
    if (this.isDraft()) {
      // Cgiar Cross Cutting Markers Autosave
      if (reportSynthesis.getReportSynthesisSrfProgress().getSloTargets() != null) {
        for (ReportSynthesisSrfProgressTarget reportSynthesisSrfProgressTargets : reportSynthesis
          .getReportSynthesisSrfProgress().getSloTargets()) {
          if (reportSynthesisSrfProgressTargets.getSrfSloIndicatorTarget().getId() == targetID) {
            target = reportSynthesisSrfProgressTargets;
          }
        }
      }
    } else {
      target = reportSynthesisSrfProgressTargetManager.getReportSynthesisSrfProgressId(synthesisID, targetID);
    }
    if (target != null) {
      return target;
    } else {
      return null;
    }
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

        // TODO autosave List

        this.setDraft(true);
      } else {

        this.setDraft(false);

        // Check if relation is null -create it
        if (reportSynthesis.getReportSynthesisSrfProgress() == null) {
          ReportSynthesisSrfProgress srfProgress = new ReportSynthesisSrfProgress();
          // create one to one relation
          reportSynthesis.setReportSynthesisSrfProgress(srfProgress);
          srfProgress.setReportSynthesis(reportSynthesis);
          // save the changes
          reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }


        // Srf Targets List
        if (reportSynthesis.getReportSynthesisSrfProgress().getReportSynthesisSrfProgressTargets() != null) {
          reportSynthesis.getReportSynthesisSrfProgress()
            .setSloTargets(new ArrayList<>(reportSynthesis.getReportSynthesisSrfProgress()
              .getReportSynthesisSrfProgressTargets().stream().filter(t -> t.isActive()).collect(Collectors.toList())));
        }


      }
    }


    sloTargets = new ArrayList<>(srfSloIndicatorTargetManager.findAll().stream()
      .filter(sr -> sr.isActive() && sr.getYear() == 2022).collect(Collectors.toList()));


    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));


    if (this.isPMU()) {

      // // Table A-2 PMU Information
      // flagshipPlannedList = reportSynthesisCrpProgressManager.getPlannedList(liaisonInstitutions, phase.getId(),
      // loggedCrp, this.liaisonInstitution);
      // // Table A-1 Evidence on Progress
      // fpSynthesisTable = reportSynthesisCrpProgressTargetManager.flagshipSynthesis(liaisonInstitutions,
      // phase.getId());
      //
      // // Flagships Synthesis Progress
      // flagshipCrpProgress =
      // reportSynthesisCrpProgressManager.getFlagshipCrpProgress(liaisonInstitutions, phase.getId());

      // Flagships Synthesis Progress
      flagshipSrfProgress =
        reportSynthesisSrfProgressManager.getFlagshipSrfProgress(liaisonInstitutions, phase.getId());


    }

    // ADD PMU as liasion Institution too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym() != null && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));


    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_SRF_PROGRESS_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisSrfProgress().getSloTargets() != null) {
        reportSynthesis.getReportSynthesisSrfProgress().getSloTargets().clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {


      ReportSynthesisSrfProgress srfProgressDB =
        reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisSrfProgress();

      this.saveSrfTargets(srfProgressDB);


      srfProgressDB.setSummary(reportSynthesis.getReportSynthesisSrfProgress().getSummary());

      srfProgressDB = reportSynthesisSrfProgressManager.saveReportSynthesisSrfProgress(srfProgressDB);


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
  public void saveSrfTargets(ReportSynthesisSrfProgress srfProgressDB) {


    // Search and deleted form Information
    if (srfProgressDB.getReportSynthesisSrfProgressTargets() != null
      && srfProgressDB.getReportSynthesisSrfProgressTargets().size() > 0) {

      List<ReportSynthesisSrfProgressTarget> targetPrev = new ArrayList<>(srfProgressDB
        .getReportSynthesisSrfProgressTargets().stream().filter(nu -> nu.isActive()).collect(Collectors.toList()));

      for (ReportSynthesisSrfProgressTarget srfTarget : targetPrev) {
        if (!reportSynthesis.getReportSynthesisSrfProgress().getSloTargets().contains(srfTarget)) {
          reportSynthesisSrfProgressTargetManager.deleteReportSynthesisSrfProgressTarget(srfTarget.getId());
        }
      }
    }

    // Save form Information
    if (reportSynthesis.getReportSynthesisSrfProgress().getSloTargets() != null) {
      for (ReportSynthesisSrfProgressTarget srfTarget : reportSynthesis.getReportSynthesisSrfProgress()
        .getSloTargets()) {
        if (srfTarget.getId() == null) {
          ReportSynthesisSrfProgressTarget srfTargetSave = new ReportSynthesisSrfProgressTarget();

          srfTargetSave.setReportSynthesisSrfProgress(srfProgressDB);

          SrfSloIndicatorTarget sloIndicator =
            srfSloIndicatorTargetManager.getSrfSloIndicatorTargetById(srfTarget.getSrfSloIndicatorTarget().getId());

          srfTargetSave.setBirefSummary(srfTarget.getBirefSummary());
          srfTargetSave.setAdditionalContribution(srfTarget.getAdditionalContribution());

          srfTargetSave.setSrfSloIndicatorTarget(sloIndicator);

          reportSynthesisSrfProgressTargetManager.saveReportSynthesisSrfProgressTarget(srfTargetSave);
        } else {

          boolean hasChanges = false;
          ReportSynthesisSrfProgressTarget srfTargetPrev =
            reportSynthesisSrfProgressTargetManager.getReportSynthesisSrfProgressTargetById(srfTarget.getId());

          if (!srfTargetPrev.getBirefSummary().equals(srfTarget.getBirefSummary())) {
            hasChanges = true;
            srfTargetPrev.setBirefSummary(srfTarget.getBirefSummary());
          }

          if (!srfTargetPrev.getAdditionalContribution().equals(srfTarget.getAdditionalContribution())) {
            hasChanges = true;
            srfTargetPrev.setAdditionalContribution(srfTarget.getAdditionalContribution());
          }

          if (hasChanges) {
            reportSynthesisSrfProgressTargetManager.saveReportSynthesisSrfProgressTarget(srfTargetPrev);
          }
        }
      }
    }


  }


  public void setFlagshipSrfProgress(List<ReportSynthesisSrfProgress> flagshipSrfProgress) {
    this.flagshipSrfProgress = flagshipSrfProgress;
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

  /**
   * Get the information list for Evidences that belongs to Srf Target
   *
   * @param markerID
   * @return
   */
  // public List<ProjectExpectedStudy> getTargetsEvidenceInfo(long targetID) {
  //
  // List<ReportSynthesisSrfProgressTarget> targets = new ArrayList<ReportSynthesisSrfProgressTarget>();
  //
  // ReportSynthesisSrfProgressTarget target = new ReportSynthesisSrfProgressTarget();
  //
  // // Get the list of liaison institutions Flagships and PMU.
  // List<LiaisonInstitution> liaisonInstitutionsFg = loggedCrp.getLiaisonInstitutions().stream()
  // .filter(c -> c.getCrpProgram() != null && c.isActive()
  // && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
  // .collect(Collectors.toList());
  // liaisonInstitutionsFg.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
  //
  // for (LiaisonInstitution liaisonInstitution : liaisonInstitutionsFg) {
  // target = reportSynthesisSrfProgressTargetManager.getSrfProgressTargetInfo(liaisonInstitution,
  // this.getActualPhase().getId(), targetID);
  // targets.add(target);
  // }
  // return targets;
  // }


  public void studiesList(long phaseID, LiaisonInstitution liaisonInstitution, long tragetId) {

    studiesList = new ArrayList<>();

    Phase phase = phaseManager.getPhaseById(phaseID);

    SrfSloIndicatorTarget target = srfSloIndicatorTargetManager.getSrfSloIndicatorTargetById(tragetId);

    if (projectFocusManager.findAll() != null) {

      List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
        .filter(pf -> pf.isActive() && pf.getCrpProgram().getId() == liaisonInstitution.getCrpProgram().getId()
          && pf.getPhase() != null && pf.getPhase().getId() == phaseID)
        .collect(Collectors.toList()));

      for (ProjectFocus focus : projectFocus) {
        Project project = projectManager.getProjectById(focus.getProject().getId());
        List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(project.getProjectExpectedStudies().stream()
          .filter(es -> es.isActive() && es.getProjectExpectedStudyInfo(phase) != null
            && es.getProjectExpectedStudyInfo(phase).getYear() == this.getCurrentCycleYear())
          .collect(Collectors.toList()));
        for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
          if (projectExpectedStudy.getProjectExpectedStudyInfo(phase) != null) {
            if (projectExpectedStudy.getProjectExpectedStudyInfo(phase).getStudyType() != null
              && projectExpectedStudy.getProjectExpectedStudyInfo(phase).getStudyType().getId() == 1) {

              if (projectExpectedStudy.getProjectExpectedStudySrfTargets() != null
                && projectExpectedStudy.getProjectExpectedStudySrfTargets().size() > 0) {

                List<ProjectExpectedStudySrfTarget> targetPrev = new ArrayList<>(projectExpectedStudy
                  .getProjectExpectedStudySrfTargets().stream()
                  .filter(nu -> nu.isActive() && nu.getPhase().getId() == phase.getId()).collect(Collectors.toList()));

                for (ProjectExpectedStudySrfTarget studytarget : targetPrev) {

                }

              }


              studiesList.add(projectExpectedStudy);
            }
          }
        }
      }

      List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
        .filter(es -> es.isActive() && es.getProjectExpectedStudyInfo(phase) != null
          && es.getProjectExpectedStudyInfo(phase).getYear() == this.getCurrentCycleYear() && es.getProject() == null)
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