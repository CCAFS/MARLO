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
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProgressTargetCaseGeographicRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProgressTargetCaseGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrpProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrpProgressStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrpProgressTargetManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressTargetCasesManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressTargetManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorTargetManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicRegion;
import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicScope;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgressTarget;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTarget;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTargetCases;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;
import org.cgiar.ccafs.marlo.data.model.StudiesStatusPlanningEnum;
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
  private SectionStatusManager sectionStatusManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;


  private ReportSynthesisManager reportSynthesisManager;

  private AuditLogManager auditLogManager;


  private UserManager userManager;

  private CrpProgramManager crpProgramManager;


  private ReportSynthesisSrfProgressManager reportSynthesisSrfProgressManager;
  private String flagshipsIncomplete;
  private SrfProgressValidator validator;

  private ReportSynthesisSrfProgressTargetManager reportSynthesisSrfProgressTargetManager;
  private ReportSynthesisSrfProgressTargetCasesManager reportSynthesisSrfProgressTargetCasesManager;


  private SrfSloIndicatorTargetManager srfSloIndicatorTargetManager;

  private ProjectManager projectManager;


  private PhaseManager phaseManager;

  private ProjectFocusManager projectFocusManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private LocElementManager locElementManager;
  private ProgressTargetCaseGeographicRegionManager progressTargetCaseGeographicRegionManager;
  private ProgressTargetCaseGeographicScopeManager progressTargetCaseGeographicScopeManager;


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
  private List<String> listOfFlagships;
  private List<RepIndGeographicScope> repIndGeographicScopes;
  private List<LocElement> repIndRegions;
  private List<LocElement> countries;

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
    ReportSynthesisSrfProgressTargetCasesManager reportSynthesisSrfProgressTargetCasesManager,
    ReportSynthesisSrfProgressManager reportSynthesisSrfProgressManager, SectionStatusManager sectionStatusManager,
    RepIndGeographicScopeManager repIndGeographicScopeManager, LocElementManager locElementManager,
    ProgressTargetCaseGeographicRegionManager progressTargetCaseGeographicRegionManager,
    ProgressTargetCaseGeographicScopeManager progressTargetCaseGeographicScopeManager) {
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
    this.sectionStatusManager = sectionStatusManager;
    this.reportSynthesisSrfProgressTargetCasesManager = reportSynthesisSrfProgressTargetCasesManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.locElementManager = locElementManager;
    this.progressTargetCaseGeographicRegionManager = progressTargetCaseGeographicRegionManager;
    this.progressTargetCaseGeographicScopeManager = progressTargetCaseGeographicScopeManager;
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

  public List<LocElement> getCountries() {
    return countries;
  }

  /**
   * Get the information of evidences according to srf target
   *
   * @param markerID
   * @return
   */
  public List<ProjectExpectedStudy> getEvidenceInfo(long targetID) {


    List<ProjectExpectedStudy> studiesInfo = new ArrayList<>();

    if (this.isPMU()) {
      List<ProjectExpectedStudy> flagshipStudiesInfo = new ArrayList<>();
      // Get the list of liaison institutions Flagships and PMU.
      List<LiaisonInstitution> liaisonInstitutionsFg = loggedCrp.getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() != null && c.isActive()
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());
      liaisonInstitutionsFg.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

      for (LiaisonInstitution liaisonInstitution : liaisonInstitutionsFg) {
        flagshipStudiesInfo = this.studiesList(this.getActualPhase().getId(), liaisonInstitution, targetID);
        studiesInfo.addAll(flagshipStudiesInfo);
      }

    } else {
      studiesInfo = this.studiesList(this.getActualPhase().getId(), this.liaisonInstitution, targetID);
    }


    return studiesInfo;
  }


  public List<ReportSynthesisSrfProgress> getFlagshipSrfProgress() {
    return flagshipSrfProgress;
  }

  public void getFlagshipsWithMissingFields() {
    listOfFlagships = new ArrayList<>();
    SectionStatus sectionStatus = sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(),
      "Reporting", this.getActualPhase().getYear(), false, "crpProgress");

    if (sectionStatus != null && sectionStatus.getMissingFields() != null && !sectionStatus.getMissingFields().isEmpty()
      && sectionStatus.getMissingFields().length() != 0 && sectionStatus.getSynthesisFlagships() != null
      && !sectionStatus.getSynthesisFlagships().isEmpty()
      && sectionStatus.getMissingFields().contains("crpProgress1")) {
      flagshipsIncomplete = sectionStatus.getSynthesisFlagships();
    }

    if (flagshipsIncomplete != null && !flagshipsIncomplete.isEmpty()) {
      String textToSeparate = flagshipsIncomplete;
      String separator = ";";
      String[] arrayText = textToSeparate.split(separator);
      for (String element : arrayText) {
        listOfFlagships.add(element);
      }
    }
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

  public List<String> getListOfFlagships() {
    return listOfFlagships;
  }


  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public List<RepIndGeographicScope> getRepIndGeographicScopes() {
    return repIndGeographicScopes;
  }

  public List<LocElement> getRepIndRegions() {
    return repIndRegions;
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
  public List<ReportSynthesisSrfProgressTargetCases> getTargetsCasesFlagshipInfo(long targetID) {

    List<ReportSynthesisSrfProgressTargetCases> targets = new ArrayList<ReportSynthesisSrfProgressTargetCases>();

    ReportSynthesisSrfProgressTargetCases target = new ReportSynthesisSrfProgressTargetCases();

    // Get the list of liaison institutions Flagships and PMU.
    List<LiaisonInstitution> liaisonInstitutionsFg = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutionsFg.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

    for (LiaisonInstitution liaisonInstitution : liaisonInstitutionsFg) {
      target = reportSynthesisSrfProgressTargetCasesManager.getSrfProgressTargetInfo(liaisonInstitution,
        this.getActualPhase().getId(), targetID);
      // target = reportSynthesisSrfProgressTargetManager.getSrfProgressTargetInfo(liaisonInstitution,
      // this.getActualPhase().getId(), targetID);
      targets.add(target);
    }
    return targets;
  }

  /**
   * Get the information for the Slo targets cases in the form
   * 
   * @param markerID
   * @return
   */
  public List<ReportSynthesisSrfProgressTargetCases> getTargetsCasesInfo(long targetID) {
    List<ReportSynthesisSrfProgressTargetCases> targets = new ArrayList<>();
    if (this.isDraft()) {
      // Cgiar Cross Cutting Markers Autosave
      if (reportSynthesis.getReportSynthesisSrfProgress().getSloTargetsCases() != null) {
        for (ReportSynthesisSrfProgressTargetCases reportSynthesisSrfProgressTargetsCases : reportSynthesis
          .getReportSynthesisSrfProgress().getSloTargetsCases()) {
          if (reportSynthesisSrfProgressTargetsCases.getSrfSloIndicatorTarget().getId().equals(targetID)) {
            targets.add(reportSynthesisSrfProgressTargetsCases);
          }
        }
      }
    } else {
      targets = reportSynthesisSrfProgressTargetCasesManager.getReportSynthesisSrfProgressId(synthesisID, targetID);
    }
    if (targets != null) {
      return targets;
    } else {
      return null;
    }
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
      // target = reportSynthesisSrfProgressTargetManager.getSrfProgressTargetInfo(liaisonInstitution,
      // this.getActualPhase().getId(), targetID);
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
          if (reportSynthesisSrfProgressTargets.getSrfSloIndicatorTarget().getId().equals(targetID)) {
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
              && lu.getLiaisonInstitution().getCrp().getId().equals(loggedCrp.getId())
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
      this.getFlagshipsWithMissingFields();
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

        // Geographic scope
        this.setRepIndGeographicScopes(repIndGeographicScopeManager.findAll().stream()
          .sorted((g1, g2) -> g1.getName().compareTo(g2.getName())).collect(Collectors.toList()));
        repIndRegions = locElementManager.findAll().stream()
          .filter(c -> c.getLocElementType().getId().intValue() == 1 && c.isActive() && c.getIsoNumeric() != null)
          .collect(Collectors.toList());
        this.setCountries(locElementManager.findAll().stream()
          .filter(c -> c.isActive() && c.getLocElementType().getId() == 2).collect(Collectors.toList()));


        // Srf Targets List
        if (reportSynthesis.getReportSynthesisSrfProgress().getReportSynthesisSrfProgressTargets() != null) {
          reportSynthesis.getReportSynthesisSrfProgress()
            .setSloTargets(new ArrayList<>(reportSynthesis.getReportSynthesisSrfProgress()
              .getReportSynthesisSrfProgressTargets().stream().filter(t -> t.isActive()).collect(Collectors.toList())));
        }

        // Srf Targets cases List
        if (reportSynthesis.getReportSynthesisSrfProgress() != null
          && reportSynthesis.getReportSynthesisSrfProgress().getId() != null) {

          /*
           * Check the target cases received from the manager and get the object(reportSynthesisSrfProgressTargetCases)
           * search by ID
           * Then add the object to the list and finally assigns the list to ReportSynthesisSrfProgress
           */
          if (reportSynthesisSrfProgressTargetCasesManager.getReportSynthesisSrfProgressTargetCaseBySrfProgress(
            reportSynthesis.getReportSynthesisSrfProgress().getId()) != null) {

            List<ReportSynthesisSrfProgressTargetCases> targetCasesDB = new ArrayList<>();
            targetCasesDB =
              reportSynthesisSrfProgressTargetCasesManager.getReportSynthesisSrfProgressTargetCaseBySrfProgress(
                reportSynthesis.getReportSynthesisSrfProgress().getId());
            List<ReportSynthesisSrfProgressTargetCases> targetCasesSave = new ArrayList<>();

            for (ReportSynthesisSrfProgressTargetCases targetCase : targetCasesDB) {
              if (targetCase.getId() != null) {
                ReportSynthesisSrfProgressTargetCases targetCaseDB = reportSynthesisSrfProgressTargetCasesManager
                  .getReportSynthesisSrfProgressTargetCasesById(targetCase.getId());

                // Target case geographic scope
                List<ProgressTargetCaseGeographicRegion> targetCaseGeographicRegions;
                targetCaseGeographicRegions =
                  progressTargetCaseGeographicRegionManager.findGeographicRegionByTargetCase(targetCase.getId());
                List<ProgressTargetCaseGeographicScope> targetCaseGeographicScope;
                targetCaseGeographicScope =
                  progressTargetCaseGeographicScopeManager.findGeographicScopeByTargetCase(targetCase.getId());

                if (targetCaseDB != null) {
                  if (targetCaseGeographicScope != null) {
                    targetCaseDB.setGeographicScopes(targetCaseGeographicScope);
                  }
                  if (targetCaseGeographicRegions != null) {
                    targetCaseDB.setGeographicRegions(targetCaseGeographicRegions);
                  }
                }

                if (targetCaseDB != null) {
                  targetCasesSave.add(targetCaseDB);
                }
              }

              if (targetCasesSave != null) {
                reportSynthesis.getReportSynthesisSrfProgress().setSloTargetsCases(targetCasesSave);
              }
            }
          }
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

      // this.saveSrfTargets(srfProgressDB);
      this.saveSrfTargetsCases(srfProgressDB);

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

      this.getActionMessages();
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
    // if (srfProgressDB.getReportSynthesisSrfProgressTargets() != null
    // && srfProgressDB.getReportSynthesisSrfProgressTargets().size() > 0) {
    //
    // List<ReportSynthesisSrfProgressTarget> targetPrev = new ArrayList<>(srfProgressDB
    // .getReportSynthesisSrfProgressTargets().stream().filter(nu -> nu.isActive()).collect(Collectors.toList()));
    //
    // for (ReportSynthesisSrfProgressTarget srfTarget : targetPrev) {
    // if (!reportSynthesis.getReportSynthesisSrfProgress().getSloTargets().contains(srfTarget)) {
    // reportSynthesisSrfProgressTargetManager.deleteReportSynthesisSrfProgressTarget(srfTarget.getId());
    // }
    // }
    // }

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
          srfTargetSave.setActive(true);

          reportSynthesisSrfProgressTargetManager.saveReportSynthesisSrfProgressTarget(srfTargetSave);
        } else {

          boolean hasChanges = false;
          ReportSynthesisSrfProgressTarget srfTargetPrev =
            reportSynthesisSrfProgressTargetManager.getReportSynthesisSrfProgressTargetById(srfTarget.getId());

          if (srfTargetPrev != null) {
            if (srfTargetPrev.getBirefSummary() != null
              && !srfTargetPrev.getBirefSummary().equals(srfTarget.getBirefSummary())) {
              hasChanges = true;
              srfTargetPrev.setBirefSummary(srfTarget.getBirefSummary());
            }

            if (srfTargetPrev.getAdditionalContribution() != null
              && !srfTargetPrev.getAdditionalContribution().equals(srfTarget.getAdditionalContribution())) {
              hasChanges = true;
              srfTargetPrev.setAdditionalContribution(srfTarget.getAdditionalContribution());
            }
          }

          if (hasChanges) {
            srfTargetPrev.setActive(true);
            reportSynthesisSrfProgressTargetManager.saveReportSynthesisSrfProgressTarget(srfTargetPrev);
          }
        }
      }
    }


  }


  /**
   * Save Crp Progress Srf Targets Cases Information
   * 
   * @param crpProgressDB
   */
  public void saveSrfTargetsCases(ReportSynthesisSrfProgress srfProgressDB) {
    // Save form Information
    if (reportSynthesis.getReportSynthesisSrfProgress().getSloTargets() != null) {
      for (ReportSynthesisSrfProgressTargetCases srfTarget : reportSynthesis.getReportSynthesisSrfProgress()
        .getSloTargetsCases()) {
        if (srfTarget.getId() == null) {
          ReportSynthesisSrfProgressTargetCases srfTargetSave = new ReportSynthesisSrfProgressTargetCases();

          srfTargetSave.setReportSynthesisSrfProgress(srfProgressDB);

          SrfSloIndicatorTarget sloIndicator =
            srfSloIndicatorTargetManager.getSrfSloIndicatorTargetById(srfTarget.getSrfSloIndicatorTarget().getId());

          srfTargetSave.setBriefSummary(srfTarget.getBriefSummary());
          srfTargetSave.setAdditionalContribution(srfTarget.getAdditionalContribution());

          srfTargetSave.setSrfSloIndicatorTarget(sloIndicator);
          srfTargetSave.setActive(true);

          reportSynthesisSrfProgressTargetCasesManager.saveReportSynthesisSrfProgressTargetCases(srfTargetSave);

        } else {

          boolean hasChanges = false;
          ReportSynthesisSrfProgressTargetCases srfTargetPrev = reportSynthesisSrfProgressTargetCasesManager
            .getReportSynthesisSrfProgressTargetCasesById(srfTarget.getId());

          if (srfTargetPrev != null) {
            if (srfTargetPrev.getBriefSummary() != null
              && !srfTargetPrev.getBriefSummary().equals(srfTarget.getBriefSummary())) {
              hasChanges = true;
              srfTargetPrev.setBriefSummary(srfTarget.getBriefSummary());
            }

            if (srfTargetPrev.getAdditionalContribution() != null
              && !srfTargetPrev.getAdditionalContribution().equals(srfTarget.getAdditionalContribution())) {
              hasChanges = true;
              srfTargetPrev.setAdditionalContribution(srfTarget.getAdditionalContribution());
            }
          }

          if (hasChanges) {
            srfTargetPrev.setActive(true);
            reportSynthesisSrfProgressTargetCasesManager.saveReportSynthesisSrfProgressTargetCases(srfTargetPrev);
          }
        }
      }
    }


  }

  public void setCountries(List<LocElement> countries) {
    this.countries = countries;
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


  public void setListOfFlagships(List<String> listOfFlagships) {
    this.listOfFlagships = listOfFlagships;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setRepIndGeographicScopes(List<RepIndGeographicScope> repIndGeographicScopes) {
    this.repIndGeographicScopes = repIndGeographicScopes;
  }

  public void setRepIndRegions(List<LocElement> repIndRegions) {
    this.repIndRegions = repIndRegions;
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


  public List<ProjectExpectedStudy> studiesList(long phaseID, LiaisonInstitution liaisonInstitution, long tragetId) {

    List<ProjectExpectedStudy> studies = new ArrayList<>();

    Phase phase = phaseManager.getPhaseById(phaseID);

    SrfSloIndicatorTarget target = srfSloIndicatorTargetManager.getSrfSloIndicatorTargetById(tragetId);

    if (projectFocusManager.findAll() != null) {

      List<ProjectFocus> projectFocus = new ArrayList<>(projectFocusManager.findAll().stream()
        .filter(pf -> pf.isActive() && pf.getCrpProgram().getId().equals(liaisonInstitution.getCrpProgram().getId())
          && pf.getPhase() != null && pf.getPhase().getId().equals(phaseID))
        .collect(Collectors.toList()));

      for (ProjectFocus focus : projectFocus) {
        Project project = projectManager.getProjectById(focus.getProject().getId());


        List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(project.getProjectExpectedStudies().stream()
          .filter(es -> es.isActive() && es.getProjectExpectedStudyInfo(phase) != null
            && es.getProjectExpectedStudyInfo(phase).getYear().equals(this.getCurrentCycleYear()))
          .collect(Collectors.toList()));


        List<ProjectExpectedStudy> expectedStudiesFiltered = new ArrayList<>();

        expectedStudiesFiltered = expectedStudies.stream()
          .filter(ps -> ps.getProjectExpectedStudyInfo().getYear() != null
            && ps.getProjectExpectedStudyInfo().getStatus() != null
            && ps.getProjectExpectedStudyInfo().getYear().equals(this.getCurrentCycleYear())
            && ((ps.getProjectExpectedStudyInfo().getStatus().getId()
              .equals(Long.parseLong(StudiesStatusPlanningEnum.Ongoing.getStatusId()))
              || ps.getProjectExpectedStudyInfo().getStatus().getId()
                .equals(Long.parseLong(StudiesStatusPlanningEnum.Extended.getStatusId()))
              || ps.getProjectExpectedStudyInfo().getStatus().getId()
                .equals(Long.parseLong(StudiesStatusPlanningEnum.New.getStatusId()))
              || ((ps.getProjectExpectedStudyInfo().getStatus().getId()
                .equals(Long.parseLong(StudiesStatusPlanningEnum.Complete.getStatusId()))
                || ps.getProjectExpectedStudyInfo().getStatus().getId()
                  .equals(Long.parseLong(StudiesStatusPlanningEnum.Cancelled.getStatusId())))
                && ps.getProjectExpectedStudyInfo().getYear() >= this.getActualPhase().getYear()))))
          .collect(Collectors.toList());

        for (ProjectExpectedStudy projectExpectedStudy : expectedStudiesFiltered) {
          if (projectExpectedStudy.getProjectExpectedStudyInfo(phase) != null) {
            if (projectExpectedStudy.getProjectExpectedStudyInfo(phase).getStudyType() != null) {

              if (projectExpectedStudy.getProjectExpectedStudySrfTargets() != null
                && projectExpectedStudy.getProjectExpectedStudySrfTargets().size() > 0) {
                // AR Synthesis 2018 add Studies wiht Target
                List<ProjectExpectedStudySrfTarget> targetPrev =
                  new ArrayList<>(projectExpectedStudy.getProjectExpectedStudySrfTargets().stream()
                    .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId()))
                    .collect(Collectors.toList()));

                for (ProjectExpectedStudySrfTarget studytarget : targetPrev) {
                  if (studytarget.getSrfSloIndicator().getId().equals(target.getId())) {
                    projectExpectedStudy
                      .setSrfTargets(new ArrayList<>(projectExpectedStudy.getProjectExpectedStudySrfTargets().stream()
                        .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId()))
                        .collect(Collectors.toList())));
                    studies.add(projectExpectedStudy);
                  }

                }
              }
            }
          }
        }
      }

      List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
        .filter(es -> es.isActive() && es.getProjectExpectedStudyInfo(phase) != null
          && es.getProjectExpectedStudyInfo(phase).getYear().equals(this.getCurrentCycleYear())
          && es.getProject() == null)
        .collect(Collectors.toList()));


      List<ProjectExpectedStudy> expectedStudiesFiltered = new ArrayList<>();

      expectedStudiesFiltered = expectedStudies.stream()
        .filter(ps -> ps.getProjectExpectedStudyInfo().getYear() != null
          && ps.getProjectExpectedStudyInfo().getStatus() != null
          && ps.getProjectExpectedStudyInfo().getYear().equals(this.getCurrentCycleYear())
          && ((ps.getProjectExpectedStudyInfo().getStatus().getId()
            .equals(Long.parseLong(StudiesStatusPlanningEnum.Ongoing.getStatusId()))
            || ps.getProjectExpectedStudyInfo().getStatus().getId()
              .equals(Long.parseLong(StudiesStatusPlanningEnum.Extended.getStatusId()))
            || ps.getProjectExpectedStudyInfo().getStatus().getId()
              .equals(Long.parseLong(StudiesStatusPlanningEnum.New.getStatusId())))
            || ((ps.getProjectExpectedStudyInfo().getStatus().getId()
              .equals(Long.parseLong(StudiesStatusPlanningEnum.Complete.getStatusId()))
              || ps.getProjectExpectedStudyInfo().getStatus().getId()
                .equals(Long.parseLong(StudiesStatusPlanningEnum.Cancelled.getStatusId())))
              && ps.getProjectExpectedStudyInfo().getYear() >= this.getActualPhase().getYear())))
        .collect(Collectors.toList());

      for (ProjectExpectedStudy projectExpectedStudy : expectedStudiesFiltered) {
        if (projectExpectedStudy.getProjectExpectedStudyInfo(phase) != null) {
          List<ProjectExpectedStudyFlagship> studiesPrograms =
            new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyFlagships().stream()
              .filter(s -> s.isActive() && s.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
          for (ProjectExpectedStudyFlagship projectExpectedStudyFlagship : studiesPrograms) {
            CrpProgram crpProgram = liaisonInstitution.getCrpProgram();
            if (crpProgram.equals(projectExpectedStudyFlagship.getCrpProgram())) {
              if (projectExpectedStudy.getProjectExpectedStudyInfo(phase) != null) {
                if (projectExpectedStudy.getProjectExpectedStudyInfo(phase).getStudyType() != null) {

                  if (projectExpectedStudy.getProjectExpectedStudySrfTargets() != null
                    && projectExpectedStudy.getProjectExpectedStudySrfTargets().size() > 0) {
                    // AR Synthesis 2018 add Studies wiht Target
                    List<ProjectExpectedStudySrfTarget> targetPrev =
                      new ArrayList<>(projectExpectedStudy.getProjectExpectedStudySrfTargets().stream()
                        .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId()))
                        .collect(Collectors.toList()));

                    for (ProjectExpectedStudySrfTarget studytarget : targetPrev) {
                      if (studytarget.getSrfSloIndicator().getId().equals(target.getId())) {
                        projectExpectedStudy
                          .setSrfTargets(new ArrayList<>(projectExpectedStudy.getProjectExpectedStudySrfTargets()
                            .stream().filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId()))
                            .collect(Collectors.toList())));
                        studies.add(projectExpectedStudy);
                        break;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      for (ProjectExpectedStudy projectExpectedStudy : studies) {
        if (projectExpectedStudy.getProjectExpectedStudySubIdos() != null
          && !projectExpectedStudy.getProjectExpectedStudySubIdos().isEmpty()) {
          projectExpectedStudy.setSubIdos(new ArrayList<>(projectExpectedStudy.getProjectExpectedStudySubIdos().stream()
            .filter(s -> s.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }
      }

    }

    return studies;
  }

  @Override
  public void validate() {
    if (save) {
      if (this.isPMU()) {
        validator.validateCheckButton(this, reportSynthesis, true);
      } else {
        validator.validate(this, reportSynthesis, true);
      }
    }
  }

}