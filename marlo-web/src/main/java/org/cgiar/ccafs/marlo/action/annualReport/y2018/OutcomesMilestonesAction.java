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
import org.cgiar.ccafs.marlo.data.manager.CgiarCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndMilestoneReasonManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CgiarCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.RepIndMilestoneReason;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressMilestone;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcome;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestone;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.FlagshipProgressValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
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
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class OutcomesMilestonesAction extends BaseAction {


  private static final long serialVersionUID = -6827326398431411479L;


  // Managers
  private GlobalUnitManager crpManager;


  private LiaisonInstitutionManager liaisonInstitutionManager;


  private ReportSynthesisManager reportSynthesisManager;


  private AuditLogManager auditLogManager;


  private UserManager userManager;


  private CrpProgramManager crpProgramManager;


  private FlagshipProgressValidator validator;


  private ProjectFocusManager projectFocusManager;


  private ProjectManager projectManager;


  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;


  private ReportSynthesisFlagshipProgressMilestoneManager reportSynthesisFlagshipProgressMilestoneManager;

  private ReportSynthesisFlagshipProgressCrossCuttingMarkerManager reportSynthesisFlagshipProgressCrossCuttingMarkerManager;

  private CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager;

  private RepIndGenderYouthFocusLevelManager focusLevelManager;

  private RepIndMilestoneReasonManager repIndMilestoneReasonManager;

  private CrpMilestoneManager crpMilestoneManager;

  private CrpProgramOutcomeManager crpProgramOutcomeManager;


  private PhaseManager phaseManager;

  private ReportSynthesisFlagshipProgressOutcomeManager reportSynthesisFlagshipProgressOutcomeManager;

  private ReportSynthesisFlagshipProgressOutcomeMilestoneManager reportSynthesisFlagshipProgressOutcomeMilestoneManager;


  // variables
  private String transaction;


  private ReportSynthesis reportSynthesis;


  private Long liaisonInstitutionID;


  private Long synthesisID;


  private LiaisonInstitution liaisonInstitution;

  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<CrpProgramOutcome> outcomes;
  private List<CrpProgram> flagships;

  private List<CgiarCrossCuttingMarker> cgiarCrossCuttingMarkers;
  private List<RepIndGenderYouthFocusLevel> focusLevels;
  private List<RepIndMilestoneReason> reasons;


  @Inject
  public OutcomesMilestonesAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, CrpProgramManager crpProgramManager,
    FlagshipProgressValidator validator, ProjectFocusManager projectFocusManager, ProjectManager projectManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ReportSynthesisFlagshipProgressMilestoneManager reportSynthesisFlagshipProgressMilestoneManager,
    CrpMilestoneManager crpMilestoneManager, PhaseManager phaseManager,
    ReportSynthesisFlagshipProgressCrossCuttingMarkerManager reportSynthesisFlagshipProgressCrossCuttingMarkerManager,
    CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager, RepIndGenderYouthFocusLevelManager focusLevelManager,
    RepIndMilestoneReasonManager repIndMilestoneReasonManager,
    ReportSynthesisFlagshipProgressOutcomeManager reportSynthesisFlagshipProgressOutcomeManager,
    ReportSynthesisFlagshipProgressOutcomeMilestoneManager reportSynthesisFlagshipProgressOutcomeMilestoneManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.validator = validator;
    this.projectFocusManager = projectFocusManager;
    this.projectManager = projectManager;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.reportSynthesisFlagshipProgressMilestoneManager = reportSynthesisFlagshipProgressMilestoneManager;
    this.crpMilestoneManager = crpMilestoneManager;
    this.phaseManager = phaseManager;
    this.reportSynthesisFlagshipProgressCrossCuttingMarkerManager =
      reportSynthesisFlagshipProgressCrossCuttingMarkerManager;
    this.cgiarCrossCuttingMarkerManager = cgiarCrossCuttingMarkerManager;
    this.focusLevelManager = focusLevelManager;
    this.repIndMilestoneReasonManager = repIndMilestoneReasonManager;
    this.reportSynthesisFlagshipProgressOutcomeManager = reportSynthesisFlagshipProgressOutcomeManager;
    this.reportSynthesisFlagshipProgressOutcomeMilestoneManager =
      reportSynthesisFlagshipProgressOutcomeMilestoneManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
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


  public List<CgiarCrossCuttingMarker> getCgiarCrossCuttingMarkers() {
    return cgiarCrossCuttingMarkers;
  }


  public List<ProjectMilestone> getContributions(long milestoneID) {
    List<ProjectMilestone> milestones = new ArrayList<>();
    Set<ProjectMilestone> milestonesSet = new HashSet<>();

    CrpMilestone crpMilestone = crpMilestoneManager.getCrpMilestoneById(milestoneID);
    List<ProjectMilestone> projectMilestones =
      crpMilestone.getProjectMilestones().stream().filter(c -> c.isActive() && c.getProjectOutcome().getPhase() != null
        && c.getProjectOutcome().getPhase().equals(this.getActualPhase())).collect(Collectors.toList());

    for (ProjectMilestone projectMilestone : projectMilestones) {
      projectMilestone.getProjectOutcome().getProject().getProjecInfoPhase(this.getActualPhase());
      if (projectMilestone.getProjectOutcome().isActive()) {
        Project project = projectMilestone.getProjectOutcome().getProject();
        if (project.getProjecInfoPhase(this.getActualPhase()) != null) {
          if (project.getProjecInfoPhase(this.getActualPhase()).getStatus().longValue() == Long
            .parseLong(ProjectStatusEnum.Ongoing.getStatusId())
            || project.getProjecInfoPhase(this.getActualPhase()).getStatus().longValue() == Long
              .parseLong(ProjectStatusEnum.Extended.getStatusId())) {
            if (project.getProjecInfoPhase(this.getActualPhase()).getEndDate() != null) {
              Calendar cal = Calendar.getInstance();
              cal.setTime(project.getProjecInfoPhase(this.getActualPhase()).getEndDate());
              if (cal.get(Calendar.YEAR) >= this.getActualPhase().getYear()) {
                milestonesSet.add(projectMilestone);
              }
            }
          }
        }
      }


    }
    milestones.addAll(milestonesSet);
    return milestones;
  }


  /**
   * Get the information for the Cross Cutting marker in the form
   * 
   * @param markerID
   * @return
   */
  public ReportSynthesisFlagshipProgressCrossCuttingMarker getCrossCuttingMarker(long milestoneID, long markerID) {
    if (milestoneID != -1) {
      ReportSynthesisFlagshipProgressCrossCuttingMarker crossCuttingMarker =
        new ReportSynthesisFlagshipProgressCrossCuttingMarker();

      crossCuttingMarker = reportSynthesisFlagshipProgressCrossCuttingMarkerManager.getMarkerId(milestoneID, markerID,
        this.getActualPhase().getId());

      if (crossCuttingMarker != null) {
        return crossCuttingMarker;
      } else {
        return null;
      }
    } else {
      return null;
    }

  }

  public List<CrpProgram> getFlagships() {
    return flagships;
  }

  public List<RepIndGenderYouthFocusLevel> getFocusLevels() {
    return focusLevels;
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

  /**
   * Get the information for the Milestones in the form
   * 
   * @param markerID
   * @return
   */
  public ReportSynthesisFlagshipProgressOutcomeMilestone getMilestone(Long outcomeID, long milestoneID) {
    if (outcomeID != -1) {
      ReportSynthesisFlagshipProgressOutcomeMilestone milestone = new ReportSynthesisFlagshipProgressOutcomeMilestone();

      milestone = reportSynthesisFlagshipProgressOutcomeMilestoneManager.getMilestoneId(outcomeID, milestoneID);

      if (milestone != null) {
        return milestone;
      } else {
        return null;
      }
    } else {
      return null;
    }

  }

  // public int getIndex(Long crpMilestoneID) {
  // if (reportSynthesis.getReportSynthesisFlagshipProgress().getMilestones() != null) {
  // int i = 0;
  // for (ReportSynthesisFlagshipProgressMilestone flagshipProgressMilestone : reportSynthesis
  // .getReportSynthesisFlagshipProgress().getMilestones()) {
  // if (flagshipProgressMilestone != null && flagshipProgressMilestone.getCrpMilestone() != null
  // && flagshipProgressMilestone.getCrpMilestone().getId() != null) {
  // if (flagshipProgressMilestone.getCrpMilestone().getId().longValue() == crpMilestoneID.longValue()) {
  // return i;
  // }
  // }
  // i++;
  // }
  //
  // } else {
  // reportSynthesis.getReportSynthesisFlagshipProgress().setMilestones(new ArrayList<>());
  // }
  //
  // ReportSynthesisFlagshipProgressMilestone flagshipProgressMilestone = new
  // ReportSynthesisFlagshipProgressMilestone();
  // flagshipProgressMilestone.setCrpMilestone(crpMilestoneManager.getCrpMilestoneById(crpMilestoneID));
  // flagshipProgressMilestone.setReportSynthesisFlagshipProgress(reportSynthesis.getReportSynthesisFlagshipProgress());
  // reportSynthesis.getReportSynthesisFlagshipProgress().getMilestones().add(flagshipProgressMilestone);
  //
  // return this.getIndex(crpMilestoneID);
  //
  // }


  /**
   * Get the information for the Outcomes in the form
   * 
   * @param markerID
   * @return
   */
  public ReportSynthesisFlagshipProgressOutcome getOutcome(long progressID, long outcomeID) {
    ReportSynthesisFlagshipProgressOutcome outcome = new ReportSynthesisFlagshipProgressOutcome();

    outcome = reportSynthesisFlagshipProgressOutcomeManager.getOutcomeId(progressID, outcomeID);

    if (outcome != null) {
      return outcome;
    } else {
      return null;
    }
  }

  public List<CrpProgramOutcome> getOutcomes() {
    return outcomes;
  }

  public List<RepIndMilestoneReason> getReasons() {
    return reasons;
  }

  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }

  public ReportSynthesisFlagshipProgressMilestone getReportSynthesisFlagshipProgressProgram(Long crpMilestoneID,
    Long crpProgramID) {
    List<ReportSynthesisFlagshipProgressMilestone> flagshipProgressMilestonesPrev =
      reportSynthesisFlagshipProgressMilestoneManager.findByProgram(crpProgramID);
    List<ReportSynthesisFlagshipProgressMilestone> flagshipProgressMilestones = flagshipProgressMilestonesPrev.stream()
      .filter(c -> c.getCrpMilestone().getId().longValue() == crpMilestoneID.longValue() && c.isActive())
      .collect(Collectors.toList());
    if (!flagshipProgressMilestones.isEmpty()) {
      return flagshipProgressMilestones.get(0);
    }
    return new ReportSynthesisFlagshipProgressMilestone();
  }

  public Long getSynthesisID() {
    return synthesisID;
  }


  public String getTransaction() {
    return transaction;
  }

  // public ReportSynthesisFlagshipProgressMilestone getReportSynthesisFlagshipProgressMilestone(Long crpMilestoneID) {
  // return reportSynthesis.getReportSynthesisFlagshipProgress().getMilestones().get(this.getIndex(crpMilestoneID));
  // }

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

  public void loadFlagShipBudgetInfo(CrpProgram crpProgram) {
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


      double w1 = project.getCoreBudget(this.getActualPhase().getYear(), this.getActualPhase());
      double w3 = project.getW3Budget(this.getActualPhase().getYear(), this.getActualPhase());
      double bilateral = project.getBilateralBudget(this.getActualPhase().getYear(), this.getActualPhase());
      List<ProjectBudgetsFlagship> budgetsFlagships = project.getProjectBudgetsFlagships().stream()
        .filter(c -> c.isActive() && c.getCrpProgram().getId().longValue() == crpProgram.getId().longValue()
          && c.getPhase().equals(this.getActualPhase()) && c.getYear() == this.getActualPhase().getYear())
        .collect(Collectors.toList());
      double percentageW1 = 0;
      double percentageW3 = 0;
      double percentageB = 0;

      if (!this.getCountProjectFlagships(project.getId())) {
        percentageW1 = 100;
        percentageW3 = 100;
        percentageB = 100;

      }
      for (ProjectBudgetsFlagship projectBudgetsFlagship : budgetsFlagships) {
        switch (projectBudgetsFlagship.getBudgetType().getId().intValue()) {
          case 1:
            percentageW1 = percentageW1 + projectBudgetsFlagship.getAmount();
            break;
          case 2:
            percentageW3 = percentageW3 + projectBudgetsFlagship.getAmount();
            break;
          case 3:
            percentageB = percentageB + projectBudgetsFlagship.getAmount();
            break;
          default:
            break;
        }
      }
      w1 = w1 * (percentageW1) / 100;
      w3 = w3 * (percentageW3) / 100;
      bilateral = bilateral * (percentageB) / 100;
      crpProgram.setW1(crpProgram.getW1() + w1);
      crpProgram.setW3(crpProgram.getW3() + w3 + bilateral);


    }
  }

  public void loadTablePMU() {
    flagships = loggedCrp.getCrpPrograms().stream()
      .filter(c -> c.isActive() && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    flagships.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));

    for (CrpProgram crpProgram : flagships) {
      crpProgram.setMilestones(new ArrayList<>());
      crpProgram.setW1(new Double(0));
      crpProgram.setW3(new Double(0));

      crpProgram.setOutcomes(crpProgram.getCrpProgramOutcomes().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
      List<CrpProgramOutcome> validOutcomes = new ArrayList<>();
      for (CrpProgramOutcome crpProgramOutcome : crpProgram.getOutcomes()) {

        crpProgramOutcome.setMilestones(crpProgramOutcome.getCrpMilestones().stream()
          .filter(c -> c.isActive() && c.getYear().intValue() == this.getActualPhase().getYear())
          .collect(Collectors.toList()));
        crpProgramOutcome.setSubIdos(
          crpProgramOutcome.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        crpProgram.getMilestones().addAll(crpProgramOutcome.getMilestones());
        if (!crpProgram.getMilestones().isEmpty()) {
          validOutcomes.add(crpProgramOutcome);
        }
      }
      crpProgram.setOutcomes(validOutcomes);
      this.loadFlagShipBudgetInfo(crpProgram);

    }
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
        this.setDraft(true);
      } else {

        this.setDraft(false);
        // Check if relation is null -create it
        if (reportSynthesis.getReportSynthesisFlagshipProgress() == null) {
          ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();
          // create one to one relation
          reportSynthesis.setReportSynthesisFlagshipProgress(flagshipProgress);
          flagshipProgress.setReportSynthesis(reportSynthesis);
          // save the changes
          reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }


        if (this.isFlagship()) {
          // Setu up Milestones Flagship Table
          if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {


            List<ReportSynthesisFlagshipProgressOutcome> reportOutcomes = new ArrayList<>(
              reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressOutcomes().stream()
                .filter(c -> c.isActive() && c.getCrpProgramOutcome() != null).collect(Collectors.toList()));

            reportSynthesis.getReportSynthesisFlagshipProgress().setOutcomeList(reportOutcomes);

            for (ReportSynthesisFlagshipProgressOutcome reportSynthesisFlagshipProgressOutcome : reportOutcomes) {

              List<ReportSynthesisFlagshipProgressOutcomeMilestone> milestones = new ArrayList<>(
                reportSynthesisFlagshipProgressOutcome.getReportSynthesisFlagshipProgressOutcomeMilestones().stream()
                  .filter(c -> c.isActive()).collect(Collectors.toList()));

              reportSynthesisFlagshipProgressOutcome.setMilestones(milestones);

              reportSynthesisFlagshipProgressOutcome.getMilestones()
                .sort((p1, p2) -> p1.getCrpMilestone().getId().compareTo(p2.getCrpMilestone().getId()));
            }

            reportOutcomes
              .sort((p1, p2) -> p1.getCrpProgramOutcome().getId().compareTo(p2.getCrpProgramOutcome().getId()));
          }

        } else {

          List<ReportSynthesisFlagshipProgressOutcome> reportOutcomes = new ArrayList<>(
            reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressOutcomes().stream()
              .filter(c -> c.isActive()).collect(Collectors.toList()));

          reportSynthesis.getReportSynthesisFlagshipProgress().setOutcomeList(reportOutcomes);

        }
      }
    }


    // Get the Outcome milestones
    outcomes = new ArrayList<>();
    List<CrpProgramOutcome> outcomesList = new ArrayList<>();
    Set<CrpProgramOutcome> outcomesSet = new HashSet<>();

    for (CrpProgram crpProgram : loggedCrp.getCrpPrograms().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {
      outcomesList
        .addAll(crpProgram.getCrpProgramOutcomes().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())
            && liaisonInstitution.getCrpProgram() != null
            && liaisonInstitution.getCrpProgram().getId().equals(c.getCrpProgram().getId()))
          .collect(Collectors.toList()));
    }
    for (CrpProgramOutcome outcome : outcomesList) {
      outcome.setMilestones(outcome.getCrpMilestones().stream()
        .filter(c -> c.isActive() && c.getYear().intValue() == this.getActualPhase().getYear())
        .collect(Collectors.toList()));
      if (!outcome.getMilestones().isEmpty()) {
        outcomesSet.add(outcome);
      }
    }
    outcomes.addAll(outcomesSet);
    outcomes.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));


    // Cross Cutting Markers
    cgiarCrossCuttingMarkers = cgiarCrossCuttingMarkerManager.findAll();

    // Cross Cutting Values List
    focusLevels = focusLevelManager.findAll();

    // Milestone reasons
    reasons = repIndMilestoneReasonManager.findAll();

    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));


    if (this.isPMU()) {
      this.loadTablePMU();
    }

    // ADD PMU as liasion Institution too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym() != null && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));


    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_FLAGSHIP_PROGRESS_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisFlagshipProgress().getOutcomeList() != null) {
        reportSynthesis.getReportSynthesisFlagshipProgress().getOutcomeList().clear();
      }
    }
  }


  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {


      ReportSynthesisFlagshipProgress flagshipProgressDB =
        reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisFlagshipProgress();

      if (this.isFlagship()) {
        this.saveFlagshipProgressNewData(flagshipProgressDB);
        flagshipProgressDB.setSummary(reportSynthesis.getReportSynthesisFlagshipProgress().getSummary());
      }

      flagshipProgressDB =
        reportSynthesisFlagshipProgressManager.saveReportSynthesisFlagshipProgress(flagshipProgressDB);

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
   * Save CrossCutting Information
   * 
   * @param projectPolicy
   * @param phase
   */
  public void saveCrossCutting(ReportSynthesisFlagshipProgressOutcomeMilestone milestoneDB,
    ReportSynthesisFlagshipProgressOutcomeMilestone milestone) {

    // Save form Information
    if (milestone.getMarkers() != null) {
      for (ReportSynthesisFlagshipProgressCrossCuttingMarker crossCuttingOwner : milestone.getMarkers()) {
        if (crossCuttingOwner.getId() == null) {
          ReportSynthesisFlagshipProgressCrossCuttingMarker crossCuttingOwnerSave =
            new ReportSynthesisFlagshipProgressCrossCuttingMarker();
          crossCuttingOwnerSave.setReportSynthesisFlagshipProgressOutcomeMilestone(milestoneDB);


          CgiarCrossCuttingMarker cgiarCrossCuttingMarker =
            cgiarCrossCuttingMarkerManager.getCgiarCrossCuttingMarkerById(crossCuttingOwner.getMarker().getId());

          crossCuttingOwnerSave.setMarker(cgiarCrossCuttingMarker);

          if (crossCuttingOwner.getFocus() != null) {
            if (crossCuttingOwner.getFocus().getId() != null && crossCuttingOwner.getFocus().getId() != -1) {
              RepIndGenderYouthFocusLevel focusLevel =
                focusLevelManager.getRepIndGenderYouthFocusLevelById(crossCuttingOwner.getFocus().getId());
              crossCuttingOwnerSave.setFocus(focusLevel);
            } else {
              crossCuttingOwnerSave.setFocus(null);
            }
          } else {
            crossCuttingOwnerSave.setFocus(null);
          }

          crossCuttingOwnerSave.setJust(crossCuttingOwner.getJust());

          reportSynthesisFlagshipProgressCrossCuttingMarkerManager
            .saveReportSynthesisFlagshipProgressCrossCuttingMarker(crossCuttingOwnerSave);

        } else {
          boolean hasChanges = false;
          ReportSynthesisFlagshipProgressCrossCuttingMarker crossCuttingOwnerSave =
            reportSynthesisFlagshipProgressCrossCuttingMarkerManager
              .getReportSynthesisFlagshipProgressCrossCuttingMarkerById(crossCuttingOwner.getId());

          if (crossCuttingOwner.getFocus() != null) {
            if (crossCuttingOwner.getFocus().getId() != null && crossCuttingOwner.getFocus().getId() != -1) {

              if (crossCuttingOwnerSave.getFocus() != null) {
                if (crossCuttingOwner.getFocus().getId() != crossCuttingOwnerSave.getFocus().getId()) {
                  RepIndGenderYouthFocusLevel focusLevel =
                    focusLevelManager.getRepIndGenderYouthFocusLevelById(crossCuttingOwner.getFocus().getId());
                  crossCuttingOwnerSave.setFocus(focusLevel);
                  hasChanges = true;
                }
              } else {
                RepIndGenderYouthFocusLevel focusLevel =
                  focusLevelManager.getRepIndGenderYouthFocusLevelById(crossCuttingOwner.getFocus().getId());
                crossCuttingOwnerSave.setFocus(focusLevel);
                hasChanges = true;
              }

            } else {
              crossCuttingOwnerSave.setFocus(null);
              hasChanges = true;
            }
          } else {
            crossCuttingOwnerSave.setFocus(null);
            hasChanges = true;
          }

          if (crossCuttingOwner.getJust() != null) {
            if (!crossCuttingOwner.getJust().equals(crossCuttingOwnerSave.getJust())) {
              crossCuttingOwnerSave.setJust(crossCuttingOwner.getJust());
              hasChanges = true;
            }
          } else {
            crossCuttingOwnerSave.setJust(null);
            hasChanges = true;
          }


          if (hasChanges) {
            reportSynthesisFlagshipProgressCrossCuttingMarkerManager
              .saveReportSynthesisFlagshipProgressCrossCuttingMarker(crossCuttingOwnerSave);
          }

        }
      }
    }
  }

  public void saveFlagshipProgressNewData(ReportSynthesisFlagshipProgress flagshipProgressDB) {

    if (reportSynthesis.getReportSynthesisFlagshipProgress().getOutcomeList() == null) {
      reportSynthesis.getReportSynthesisFlagshipProgress().setOutcomeList(new ArrayList<>());
    }

    for (ReportSynthesisFlagshipProgressOutcome flagshipProgressOutcome : reportSynthesis
      .getReportSynthesisFlagshipProgress().getOutcomeList()) {
      ReportSynthesisFlagshipProgressOutcome flagshipProgressOutcomeNew = null;

      if (flagshipProgressOutcome != null) {

        if (flagshipProgressOutcome.getCrpProgramOutcome() != null
          && flagshipProgressOutcome.getCrpProgramOutcome().getId() > 0) {
          flagshipProgressOutcome.setCrpProgramOutcome(
            crpProgramOutcomeManager.getCrpProgramOutcomeById(flagshipProgressOutcome.getCrpProgramOutcome().getId()));
        }

        if (flagshipProgressOutcome.getId() == null) {
          flagshipProgressOutcomeNew = new ReportSynthesisFlagshipProgressOutcome();
          flagshipProgressOutcomeNew.setReportSynthesisFlagshipProgress(flagshipProgressDB);
        } else {
          flagshipProgressOutcomeNew = reportSynthesisFlagshipProgressOutcomeManager
            .getReportSynthesisFlagshipProgressOutcomeById(flagshipProgressOutcome.getId());
        }

        flagshipProgressOutcomeNew.setSummary(flagshipProgressOutcome.getSummary());
        flagshipProgressOutcomeNew.setCrpProgramOutcome(flagshipProgressOutcome.getCrpProgramOutcome());

        flagshipProgressOutcomeNew = reportSynthesisFlagshipProgressOutcomeManager
          .saveReportSynthesisFlagshipProgressOutcome(flagshipProgressOutcomeNew);

        this.saveMilestones(flagshipProgressOutcomeNew, flagshipProgressOutcome);

      }

    }

  }

  public void saveMilestones(ReportSynthesisFlagshipProgressOutcome OutcomeDB,
    ReportSynthesisFlagshipProgressOutcome outcome) {

    if (outcome.getMilestones() == null) {
      outcome.setMilestones(new ArrayList<>());
    }

    for (ReportSynthesisFlagshipProgressOutcomeMilestone flagshipProgressMilestone : outcome.getMilestones()) {
      ReportSynthesisFlagshipProgressOutcomeMilestone flagshipProgressMilestoneNew = null;

      if (flagshipProgressMilestone != null) {

        if (flagshipProgressMilestone.getCrpMilestone() != null
          && flagshipProgressMilestone.getCrpMilestone().getId() > 0) {
          flagshipProgressMilestone.setCrpMilestone(
            crpMilestoneManager.getCrpMilestoneById(flagshipProgressMilestone.getCrpMilestone().getId()));
        }

        if (flagshipProgressMilestone.getId() == null) {
          flagshipProgressMilestoneNew = new ReportSynthesisFlagshipProgressOutcomeMilestone();
          flagshipProgressMilestoneNew.setReportSynthesisFlagshipProgressOutcome(OutcomeDB);
        } else {
          flagshipProgressMilestoneNew = reportSynthesisFlagshipProgressOutcomeMilestoneManager
            .getReportSynthesisFlagshipProgressOutcomeMilestoneById(flagshipProgressMilestone.getId());
        }

        if (flagshipProgressMilestone.getReason() != null) {
          if (flagshipProgressMilestone.getReason().getId() != null
            && flagshipProgressMilestone.getReason().getId() != -1) {

            RepIndMilestoneReason milestoneReason =
              repIndMilestoneReasonManager.getRepIndMilestoneReasonById(flagshipProgressMilestone.getReason().getId());

            flagshipProgressMilestoneNew.setReason(milestoneReason);
            flagshipProgressMilestoneNew.setOtherReason(flagshipProgressMilestone.getOtherReason());
          }
        }

        flagshipProgressMilestoneNew.setEvidence(flagshipProgressMilestone.getEvidence());
        flagshipProgressMilestoneNew.setMilestonesStatus(flagshipProgressMilestone.getMilestonesStatus());
        flagshipProgressMilestoneNew.setCrpMilestone(flagshipProgressMilestone.getCrpMilestone());

        flagshipProgressMilestoneNew = reportSynthesisFlagshipProgressOutcomeMilestoneManager
          .saveReportSynthesisFlagshipProgressOutcomeMilestone(flagshipProgressMilestoneNew);

        this.saveCrossCutting(flagshipProgressMilestoneNew, flagshipProgressMilestone);


      }

    }

  }


  public void setCgiarCrossCuttingMarkers(List<CgiarCrossCuttingMarker> cgiarCrossCuttingMarkers) {
    this.cgiarCrossCuttingMarkers = cgiarCrossCuttingMarkers;
  }

  public void setFlagships(List<CrpProgram> flagships) {
    this.flagships = flagships;
  }

  public void setFocusLevels(List<RepIndGenderYouthFocusLevel> focusLevels) {
    this.focusLevels = focusLevels;
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

  public void setOutcomes(List<CrpProgramOutcome> outcomes) {
    this.outcomes = outcomes;
  }


  public void setReasons(List<RepIndMilestoneReason> reasons) {
    this.reasons = reasons;
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
