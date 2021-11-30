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
import org.cgiar.ccafs.marlo.data.manager.GeneralStatusManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndMilestoneReasonManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeMilestoneLinkManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CgiarCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.DeliverableStatusEnum;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
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
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcome;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestone;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestoneLink;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.y2018.OutcomeMilestonesValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class OutcomesMilestonesAction extends BaseAction {

  private static Logger LOG = LoggerFactory.getLogger(OutcomesMilestonesAction.class);

  private static final long serialVersionUID = -6827326398431411479L;

  // Managers
  private GlobalUnitManager crpManager;
  private SectionStatusManager sectionStatusManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private AuditLogManager auditLogManager;
  private UserManager userManager;
  private CrpProgramManager crpProgramManager;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private ReportSynthesisFlagshipProgressCrossCuttingMarkerManager reportSynthesisFlagshipProgressCrossCuttingMarkerManager;
  private CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager;
  private RepIndGenderYouthFocusLevelManager focusLevelManager;
  private RepIndMilestoneReasonManager repIndMilestoneReasonManager;
  private CrpMilestoneManager crpMilestoneManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private ReportSynthesisFlagshipProgressOutcomeManager reportSynthesisFlagshipProgressOutcomeManager;
  private ReportSynthesisFlagshipProgressOutcomeMilestoneManager reportSynthesisFlagshipProgressOutcomeMilestoneManager;
  private ReportSynthesisFlagshipProgressOutcomeMilestoneLinkManager reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager;
  private GeneralStatusManager generalStatusManager;
  // private ReportSynthesisFlagshipProgressMilestoneManager reportSynthesisFlagshipProgressMilestoneManager;
  // private PhaseManager phaseManager;
  // private ProjectFocusManager projectFocusManager;
  // private ProjectManager projectManager;

  // validator
  private OutcomeMilestonesValidator validator;

  // class variables
  private String transaction;
  private String flagshipsIncomplete;
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
  private List<String> listOfFlagships;

  @Inject
  public OutcomesMilestonesAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, CrpProgramManager crpProgramManager,
    OutcomeMilestonesValidator validator, // ProjectFocusManager projectFocusManager, ProjectManager projectManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    // ReportSynthesisFlagshipProgressMilestoneManager reportSynthesisFlagshipProgressMilestoneManager,
    CrpMilestoneManager crpMilestoneManager, // PhaseManager phaseManager,
    ReportSynthesisFlagshipProgressCrossCuttingMarkerManager reportSynthesisFlagshipProgressCrossCuttingMarkerManager,
    CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager, RepIndGenderYouthFocusLevelManager focusLevelManager,
    RepIndMilestoneReasonManager repIndMilestoneReasonManager,
    ReportSynthesisFlagshipProgressOutcomeManager reportSynthesisFlagshipProgressOutcomeManager,
    ReportSynthesisFlagshipProgressOutcomeMilestoneManager reportSynthesisFlagshipProgressOutcomeMilestoneManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager, SectionStatusManager sectionStatusManager,
    GeneralStatusManager generalStatusManager,
    ReportSynthesisFlagshipProgressOutcomeMilestoneLinkManager reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.validator = validator;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.crpMilestoneManager = crpMilestoneManager;
    this.reportSynthesisFlagshipProgressCrossCuttingMarkerManager =
      reportSynthesisFlagshipProgressCrossCuttingMarkerManager;
    this.cgiarCrossCuttingMarkerManager = cgiarCrossCuttingMarkerManager;
    this.focusLevelManager = focusLevelManager;
    this.repIndMilestoneReasonManager = repIndMilestoneReasonManager;
    this.reportSynthesisFlagshipProgressOutcomeManager = reportSynthesisFlagshipProgressOutcomeManager;
    this.reportSynthesisFlagshipProgressOutcomeMilestoneManager =
      reportSynthesisFlagshipProgressOutcomeMilestoneManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.sectionStatusManager = sectionStatusManager;
    this.generalStatusManager = generalStatusManager;
    this.reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager =
      reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager;
    // this.reportSynthesisFlagshipProgressMilestoneManager = reportSynthesisFlagshipProgressMilestoneManager;
    // this.phaseManager = phaseManager;
    // this.projectFocusManager = projectFocusManager;
    // this.projectManager = projectManager;
  }


  public void fillMilestoneReason() {

    List<ReportSynthesisFlagshipProgressOutcome> outcomeList =
      reportSynthesis.getReportSynthesisFlagshipProgress().getOutcomeList();

    // Set rep ind milestone reason value for each milestone object of report synthesis flagships progress outcomes
    // milestones list
    if (outcomeList != null && !outcomeList.isEmpty()) {
      for (ReportSynthesisFlagshipProgressOutcome outcome : outcomeList) {

        // get milestones list
        if (outcome.getMilestones() != null && !outcome.getMilestones().isEmpty()) {
          for (ReportSynthesisFlagshipProgressOutcomeMilestone milestone : outcome.getMilestones()) {
            if (milestone != null && milestone.getId() != null && milestone.getReason() == null) {
              ReportSynthesisFlagshipProgressOutcomeMilestone progressMilestone =
                reportSynthesisFlagshipProgressOutcomeMilestoneManager
                  .getReportSynthesisFlagshipProgressOutcomeMilestoneById(milestone.getId());

              if (progressMilestone != null && progressMilestone.getReason() != null) {

                // Assign 'rep ind milestone reason' value to each milestone
                milestone.setReason(progressMilestone.getReason());
              }
            }
          }
        }
      }
      reportSynthesis.getReportSynthesisFlagshipProgress().setOutcomeList(outcomeList);

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
              .parseLong(ProjectStatusEnum.Extended.getStatusId())
            || project.getProjecInfoPhase(this.getActualPhase()).getStatus().longValue() == Long
              .parseLong(ProjectStatusEnum.Complete.getStatusId())) {
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
        // String markerInfo = "";
        // String milestoneInfo = "";
        return crossCuttingMarker;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  /*
   * public GeneralStatus getCurrentMilestoneStatus(Long flagshipProgressOutcomeMilestoneId) {
   * GeneralStatus milestoneStatus = null;
   * if (flagshipProgressOutcomeMilestoneId != null && flagshipProgressOutcomeMilestoneId != -1) {
   * ReportSynthesisFlagshipProgressOutcomeMilestone fpom = reportSynthesisFlagshipProgressOutcomeMilestoneManager
   * .getReportSynthesisFlagshipProgressOutcomeMilestoneById(flagshipProgressOutcomeMilestoneId);
   * Long milestoneID = null;
   * CrpMilestone milestone = crpMilestoneManager.getCrpMilestoneById(milestoneID);
   * milestone = crpMilestoneManager.getCrpMilestoneByPhase(milestone.getComposeID(),
   * milestone.getCrpProgramOutcome().getPhase().getNext().getNext().getId());
   * if (milestone != null && milestone.getMilestonesStatus() != null
   * && milestone.getMilestonesStatus().getId() != null) {
   * milestoneStatus = generalStatusManager.getGeneralStatusById(milestone.getMilestonesStatus().getId());
   * }
   * }
   * return milestoneStatus;
   * }
   */

  public List<CrpProgram> getFlagships() {
    return flagships;
  }

  public void getFlagshipsWithMissingFields() {
    listOfFlagships = new ArrayList<>();
    SectionStatus sectionStatus = this.sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(),
      "Reporting", this.getActualPhase().getYear(), false, "outomesMilestones");

    if (sectionStatus != null && sectionStatus.getMissingFields() != null && !sectionStatus.getMissingFields().isEmpty()
      && sectionStatus.getMissingFields().length() != 0 && sectionStatus.getSynthesisFlagships() != null
      && !sectionStatus.getSynthesisFlagships().isEmpty()
      && sectionStatus.getMissingFields().contains("synthesis.AR2019Table5")) {
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

    /*
     * List<String> arraylist = new ArrayList<>();
     * String textToSeparate = "Go,PHP,JavaScript,Python";
     * String separator = ";";
     * String[] arrayText = textToSeparate.split(separator);
     * for (String element : arrayText) {
     * arraylist.add(element);
     * }
     */
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

  public List<String> getListOfFlagships() {
    return listOfFlagships;
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
    ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisMilestone = null;
    CrpMilestone annualReportMilestone = crpMilestoneManager.getCrpMilestoneById(milestoneID);
    if (outcomeID != null && outcomeID != -1) {
      reportSynthesisMilestone =
        reportSynthesisFlagshipProgressOutcomeMilestoneManager.getMilestoneId(outcomeID, milestoneID);
      if (reportSynthesisMilestone == null) {
        ReportSynthesisFlagshipProgressOutcome reportSynthesisOutcome =
          reportSynthesisFlagshipProgressOutcomeManager.getReportSynthesisFlagshipProgressOutcomeById(outcomeID);
        reportSynthesisMilestone = new ReportSynthesisFlagshipProgressOutcomeMilestone();
        if (annualReportMilestone != null) {
          reportSynthesisMilestone.setReportSynthesisFlagshipProgressOutcome(reportSynthesisOutcome);
          reportSynthesisMilestone.setCrpMilestone(annualReportMilestone);

          reportSynthesisMilestone.setMilestonesStatus(annualReportMilestone.getMilestonesStatus());

          reportSynthesisMilestone.setExtendedYear(
            annualReportMilestone.getExtendedYear() != null && annualReportMilestone.getExtendedYear() != -1
              ? annualReportMilestone.getExtendedYear() : null);

          reportSynthesisMilestone = reportSynthesisFlagshipProgressOutcomeMilestoneManager
            .saveReportSynthesisFlagshipProgressOutcomeMilestone(reportSynthesisMilestone);

          List<ReportSynthesisFlagshipProgressCrossCuttingMarker> markers = new ArrayList<>();
          List<CgiarCrossCuttingMarker> markerTypes = cgiarCrossCuttingMarkerManager.findAll();
          for (CgiarCrossCuttingMarker markerType : markerTypes) {
            ReportSynthesisFlagshipProgressCrossCuttingMarker newMarker =
              new ReportSynthesisFlagshipProgressCrossCuttingMarker();
            switch (markerType.getName()) {
              case "Gender":
                newMarker.setFocus(annualReportMilestone.getGenderFocusLevel());
                break;

              case "Youth":
                newMarker.setFocus(annualReportMilestone.getYouthFocusLevel());
                break;

              case "CapDev":
                newMarker.setFocus(annualReportMilestone.getCapdevFocusLevel());
                break;

              case "Climate Change":
                newMarker.setFocus(annualReportMilestone.getClimateFocusLevel());
                break;

              default:
                newMarker.setFocus(null);
                break;
            }

            newMarker.setMarker(markerType);
            newMarker.setReportSynthesisFlagshipProgressOutcomeMilestone(reportSynthesisMilestone);

            newMarker = reportSynthesisFlagshipProgressCrossCuttingMarkerManager
              .saveReportSynthesisFlagshipProgressCrossCuttingMarker(newMarker);

            markers.add(newMarker);
          }
          reportSynthesisMilestone.setMarkers(markers);
        }
      } else {
        if (annualReportMilestone != null) {
          if (reportSynthesisMilestone.getMilestonesStatus() == null
            || reportSynthesisMilestone.getMilestonesStatus().getId() == -1) {
            reportSynthesisMilestone.setMilestonesStatus(annualReportMilestone.getMilestonesStatus());
          }

          if (reportSynthesisMilestone.getExtendedYear() == null || reportSynthesisMilestone.getExtendedYear() == -1) {
            reportSynthesisMilestone.setExtendedYear(annualReportMilestone.getExtendedYear());
          }
        }
      }
    }

    return reportSynthesisMilestone;
  }

  public String getMilestoneExtendedYear(Long milestoneID) {
    String extendedYear = null;
    if (milestoneID != null && milestoneID != -1) {
      CrpMilestone milestone = crpMilestoneManager.getCrpMilestoneById(milestoneID);
      milestone = crpMilestoneManager.getCrpMilestoneByPhase(milestone.getComposeID(),
        milestone.getCrpProgramOutcome().getPhase().getNext().getNext().getId());
      if (milestone != null && milestone.getMilestonesStatus() != null
        && ProjectStatusEnum.Extended.getStatusId().equals(String.valueOf(milestone.getMilestonesStatus().getId()))) {
        extendedYear = String.valueOf(milestone.getExtendedYear());
      }
    }

    return extendedYear;
  }


  public CrpMilestone getNextPOWBMilestone(final String milestoneComposedId) {
    CrpMilestone nextYearMilestone = null;
    String stripped = StringUtils.stripToNull(milestoneComposedId);
    Phase current = this.getActualPhase();
    if (current != null) {
      while (current.getName() != null && !StringUtils.containsIgnoreCase(current.getName(), "POWB")
        && current.getYear() != this.getCurrentCycleYear() + 1) {
        current = current.getNext();
      }
    }

    if (stripped != null && current != null) {
      nextYearMilestone = crpMilestoneManager.getCrpMilestoneByPhase(milestoneComposedId, current.getId());
    }

    return nextYearMilestone;
  }


  /**
   * Get the information for the Outcomes in the form
   * 
   * @param markerID
   * @return
   */
  public ReportSynthesisFlagshipProgressOutcome getOutcome(long progressID, long outcomeID) {
    ReportSynthesisFlagshipProgressOutcome reportSynthesisOutcome =
      reportSynthesisFlagshipProgressOutcomeManager.getOutcomeId(progressID, outcomeID);
    if (reportSynthesisOutcome == null) {
      CrpProgramOutcome annualReportOutcome = crpProgramOutcomeManager.getCrpProgramOutcomeById(outcomeID);
      ReportSynthesisFlagshipProgress flagshipProgress =
        reportSynthesisFlagshipProgressManager.getReportSynthesisFlagshipProgressById(progressID);
      if (annualReportOutcome != null && flagshipProgress != null) {
        reportSynthesisOutcome = new ReportSynthesisFlagshipProgressOutcome();
        reportSynthesisOutcome.setCrpProgramOutcome(annualReportOutcome);
        reportSynthesisOutcome.setReportSynthesisFlagshipProgress(flagshipProgress);
        reportSynthesisOutcome = reportSynthesisFlagshipProgressOutcomeManager
          .saveReportSynthesisFlagshipProgressOutcome(reportSynthesisOutcome);
      }
    }

    return reportSynthesisOutcome;
  }

  public List<CrpProgramOutcome> getOutcomes() {
    return outcomes;
  }

  /**
   * Get the information for the Outcomes in the form
   * 
   * @param markerID
   * @return
   */
  public ReportSynthesisFlagshipProgressOutcome getOutcomeToPmu(Long programID, long outcomeID) {
    ReportSynthesisFlagshipProgressOutcome outcome = new ReportSynthesisFlagshipProgressOutcome();
    CrpProgramOutcome crpProgramOutcome = crpProgramOutcomeManager.getCrpProgramOutcomeById(outcomeID);

    LiaisonInstitution inst = crpProgramOutcome.getCrpProgram().getLiaisonInstitutions().stream()
      .filter(c -> c.isActive() && c.getCrpProgram().getId().equals(programID)).collect(Collectors.toList()).get(0);

    // ReportSynthesisSrfProgress crpProgress = new ReportSynthesisSrfProgress();
    ReportSynthesis reportSynthesisFP =
      reportSynthesisManager.findSynthesis(this.getActualPhase().getId(), inst.getId());

    ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();
    if (reportSynthesisFP != null) {
      if (reportSynthesisFP.getReportSynthesisFlagshipProgress() != null) {
        flagshipProgress = reportSynthesisFP.getReportSynthesisFlagshipProgress();
        outcome = reportSynthesisFlagshipProgressOutcomeManager.getOutcomeId(flagshipProgress.getId(), outcomeID);
        if (outcome != null) {
          return outcome;
        } else {
          return null;
        }
      }
    }

    return null;
  }

  public List<RepIndMilestoneReason> getReasons() {
    return reasons;
  }

  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }

  /*
   * public ReportSynthesisFlagshipProgressMilestone getReportSynthesisFlagshipProgressProgram(Long crpMilestoneID,
   * Long crpProgramID) {
   * List<ReportSynthesisFlagshipProgressMilestone> flagshipProgressMilestonesPrev =
   * reportSynthesisFlagshipProgressMilestoneManager.findByProgram(crpProgramID);
   * List<ReportSynthesisFlagshipProgressMilestone> flagshipProgressMilestones = flagshipProgressMilestonesPrev.stream()
   * .filter(c -> c.getCrpMilestone().getId().longValue() == crpMilestoneID.longValue() && c.isActive())
   * .collect(Collectors.toList());
   * if (!flagshipProgressMilestones.isEmpty()) {
   * return flagshipProgressMilestones.get(0);
   * }
   * return new ReportSynthesisFlagshipProgressMilestone();
   * }
   */

  public ReportSynthesisFlagshipProgressOutcomeMilestone getReportSynthesisMilestone(CrpProgram crpProgram,
    long outcomeId, long milestoneId) {
    ReportSynthesisFlagshipProgressOutcomeMilestone flagshipProgressOutcomeMilestone = null;

    List<ReportSynthesisFlagshipProgress> progress =
      this.reportSynthesisFlagshipProgressManager.getFlagshipsReportSynthesisFlagshipProgress(
        Collections.singletonList(
          this.liaisonInstitutionManager.findByAcronymAndCrp(crpProgram.getAcronym(), crpProgram.getCrp().getId())),
        this.getPhaseID());
    if (this.isNotEmpty(progress) && progress.get(0) != null && progress.get(0).getId() != null) {
      flagshipProgressOutcomeMilestone = this.reportSynthesisFlagshipProgressOutcomeMilestoneManager
        .getReportSynthesisMilestoneFromOutcomeIdAndMilestoneId(progress.get(0).getId(), outcomeId, milestoneId);
    }

    return flagshipProgressOutcomeMilestone;
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
    Set<Project> myProjects = new HashSet<Project>();
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
          .filter(c -> c.isActive() && c.getYear().intValue() == this.getActualPhase().getYear()
            || (c.getExtendedYear() != null && c.getExtendedYear().intValue() == this.getActualPhase().getYear()))
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
      transaction = StringUtils.stripToEmpty(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
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
        liaisonInstitutionID = Long.parseLong(
          StringUtils.stripToEmpty(this.getRequest().getParameter(APConstants.LIAISON_INSTITUTION_REQUEST_ID)));
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
        synthesisID =
          Long.parseLong(StringUtils.stripToEmpty(this.getRequest().getParameter(APConstants.REPORT_SYNTHESIS_ID)));
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
        BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();
        AutoSaveReader autoSaveReader = new AutoSaveReader();
        reportSynthesis = (ReportSynthesis) autoSaveReader.readFromJson(jReader);
        synthesisID = reportSynthesis.getId();
        this.setDraft(true);
      } else {
        this.setDraft(false);
        // Check if relation is null. if so, create it.
        if (reportSynthesis.getReportSynthesisFlagshipProgress() == null) {
          ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();
          // create one to one relation
          reportSynthesis.setReportSynthesisFlagshipProgress(flagshipProgress);
          flagshipProgress.setReportSynthesis(reportSynthesis);
          // save the changes
          reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }

        this.getFlagshipsWithMissingFields();

        if (this.isFlagship()) {
          // Setu up Milestones Flagship Table
          if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {
            List<ReportSynthesisFlagshipProgressOutcome> reportOutcomes = new ArrayList<>(
              reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressOutcomes().stream()
                .filter(c -> c.isActive() && c.getCrpProgramOutcome() != null).collect(Collectors.toList()));

            reportSynthesis.getReportSynthesisFlagshipProgress().setOutcomeList(reportOutcomes);

            for (ReportSynthesisFlagshipProgressOutcome reportSynthesisFlagshipProgressOutcome : reportOutcomes) {
              List<ReportSynthesisFlagshipProgressOutcomeMilestone> milestones =
                reportSynthesisFlagshipProgressOutcome.getReportSynthesisFlagshipProgressOutcomeMilestones().stream()
                  .filter(c -> c.isActive()).collect(Collectors.toList());

              // Setting the Milestone Status: Selecting the loaded one if not null, else taken from the impact pathways
              for (ReportSynthesisFlagshipProgressOutcomeMilestone milestone : milestones) {
                if (milestone != null && milestone.getId() != null && milestone.getCrpMilestone() != null
                  && milestone.getCrpMilestone().getId() != null) {
                  List<ReportSynthesisFlagshipProgressOutcomeMilestoneLink> progressOutcomeMilestoneLinks =
                    this.reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager
                      .getLinksByProgressOutcomeMilestone(milestone.getId());
                  CrpMilestone crpMilestone =
                    crpMilestoneManager.getCrpMilestoneById(milestone.getCrpMilestone().getId());
                  if (milestone.getMilestonesStatus() == null) {
                    if (crpMilestone.getMilestonesStatus() != null
                      && crpMilestone.getMilestonesStatus().getId() != -1) {
                      if (crpMilestone.getMilestonesStatus().getId() != 1
                        || crpMilestone.getMilestonesStatus().getId() != 2) {
                        milestone.setMilestonesStatus(crpMilestone.getMilestonesStatus());
                      }
                    }
                  }

                  if (milestone.getExtendedYear() == null || milestone.getExtendedYear() == -1) {
                    if (crpMilestone.getExtendedYear() != null && crpMilestone.getExtendedYear() != -1) {
                      milestone.setExtendedYear(crpMilestone.getExtendedYear());
                    }
                  }

                  milestone.setLinks(progressOutcomeMilestoneLinks);
                }
              }

              reportSynthesisFlagshipProgressOutcome.setMilestones(milestones);

              if (reportSynthesisFlagshipProgressOutcome.getMilestones() != null) {
                reportSynthesisFlagshipProgressOutcome.getMilestones()
                  .sort((p1, p2) -> p1.getCrpMilestone().getId().compareTo(p2.getCrpMilestone().getId()));
              }
            }

            reportOutcomes
              .sort((p1, p2) -> p1.getCrpProgramOutcome().getId().compareTo(p2.getCrpProgramOutcome().getId()));
          }
        } else {
          List<ReportSynthesisFlagshipProgressOutcome> reportOutcomes =
            reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressOutcomes().stream()
              .filter(c -> c.isActive()).collect(Collectors.toList());

          reportSynthesis.getReportSynthesisFlagshipProgress().setOutcomeList(reportOutcomes);
        }
      }
    }

    List<Long> ids = new ArrayList<>();
    List<ReportSynthesisFlagshipProgressOutcome> outcomeList = new ArrayList<>();
    List<ReportSynthesisFlagshipProgressOutcome> outcomeList2 = new ArrayList<>();
    List<ReportSynthesisFlagshipProgressOutcome> toRemoveOutcomeList = new ArrayList<>();

    if (reportSynthesis != null && reportSynthesis.getReportSynthesisFlagshipProgress() != null
      && reportSynthesis.getReportSynthesisFlagshipProgress().getOutcomeList() != null
      && reportSynthesis.getReportSynthesisFlagshipProgress().getOutcomeList().size() > 0) {
      outcomeList = reportSynthesis.getReportSynthesisFlagshipProgress().getOutcomeList();
    }

    try {
      if (reportSynthesis != null && reportSynthesis.getReportSynthesisFlagshipProgress() != null
        && reportSynthesis.getReportSynthesisFlagshipProgress().getOutcomeList() != null
        && reportSynthesis.getReportSynthesisFlagshipProgress().getOutcomeList().size() > 0) {
        for (ReportSynthesisFlagshipProgressOutcome outcome : reportSynthesis.getReportSynthesisFlagshipProgress()
          .getOutcomeList()) {

          /*
           * if (reportSynthesis.getReportSynthesisFlagshipProgress().getOutcomeList() != null) {
           * for (ReportSynthesisFlagshipProgressOutcome outcome : reportSynthesis.getReportSynthesisFlagshipProgress()
           * .getOutcomeList()) {
           * // setting milestones
           * outcome.getCrpProgramOutcome()
           * .setMilestones(outcome.getCrpProgramOutcome().getCrpMilestones().stream()
           * .filter(c -> c.isActive() && (c.getYear().intValue() == this.getActualPhase().getYear()
           * || (c.getExtendedYear() != null && c.getExtendedYear().intValue() == this.getActualPhase().getYear())))
           * .collect(Collectors.toList()));
           * if (outcome.getCrpProgramOutcome().getMilestones() != null) {
           * for (CrpMilestone milestone : outcome.getCrpProgramOutcome().getMilestones()) {
           * if (milestone.getYear() == this.getActualPhase().getYear()) {
           * count++;
           * }
           * }
           * if (count > 0 && outcomeList != null) {
           * try {
           * outcomeList.remove(outcome);
           * } catch (Exception e) {
           * }
           * }
           * }
           * }
           * }
           */
          List<ReportSynthesisFlagshipProgressOutcomeMilestone> progressMilestones = outcome.getMilestones();
          if (progressMilestones != null) {
            for (ReportSynthesisFlagshipProgressOutcomeMilestone milestone : progressMilestones) {
              if (milestone.getCrpMilestone() != null
                && milestone.getCrpMilestone().getYear() != this.getActualPhase().getYear()) {
                boolean delete = false;
                if (milestone.getCrpMilestone().getExtendedYear() != null
                  && milestone.getCrpMilestone().getExtendedYear().longValue() != -1) {
                  if (milestone.getCrpMilestone().getExtendedYear() != this.getActualPhase().getYear()) {
                    delete = true;
                  }
                }

                if (delete) {
                  ids.add(outcome.getId());

                  if (outcomeList != null) {
                    toRemoveOutcomeList.add(outcome);
                  }
                }
              }
            }
          }
        }

        if (toRemoveOutcomeList != null) {
          outcomeList.removeAll(toRemoveOutcomeList);
        }
      }
    } catch (Exception e) {
      LOG.error("Error getting outcome list: " + e.getMessage());
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
      // setting milestones
      outcome.setMilestones(outcome.getCrpMilestones().stream()
        .filter(c -> c.isActive() && (c.getYear() != null && c.getYear().intValue() == this.getActualPhase().getYear()
          || (c.getExtendedYear() != null && c.getExtendedYear().intValue() == this.getActualPhase().getYear())))
        .collect(Collectors.toList()));
      // setting subidos
      outcome
        .setSubIdos(outcome.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      if (outcome.getMilestones() != null && !outcome.getMilestones().isEmpty()) {
        outcomesSet.add(outcome);
      }
    }

    outcomes.addAll(outcomesSet);
    outcomes.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));

    if (outcomesList != null && !outcomesList.isEmpty() && outcomesList.size() > 0) {
      outcomesList = outcomesList.stream()
        .filter(
          o -> o != null && o.getMilestones() != null && !o.getMilestones().isEmpty() && o.getMilestones().size() > 0)
        .collect(Collectors.toList());
    }

    if (outcomesList != null && !outcomesList.isEmpty() && outcomesList.size() > 0 && outcomeList != null) {
      for (CrpProgramOutcome outcomeCrp : outcomesList) {
        for (ReportSynthesisFlagshipProgressOutcome outcome : outcomeList) {
          if (outcome != null && outcome.getCrpProgramOutcome() != null
            && outcome.getCrpProgramOutcome().getId() != null
            && outcome.getCrpProgramOutcome().getId().equals(outcomeCrp.getId())) {
            outcomeList2.add(outcome);
            // outcome.getMilestones().forEach(m -> LOG.debug(m.getMilestonesStatus() == null
            // ? "no status for " + m.getId() : m.getMilestonesStatus().getName() + " for milestone " + m.getId()));
          }
        }
      }
    }


    outcomeList = outcomeList2;

    if (outcomeList != null) {
      outcomeList = outcomeList.stream().filter(o -> o.getCrpProgramOutcome() != null).collect(Collectors.toList());
      reportSynthesis.getReportSynthesisFlagshipProgress().setOutcomeList(outcomeList);
    }
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

    /*
     * outcomeList.forEach(o -> {
     * o.getMilestones().forEach(m -> LOG
     * .debug(m.getMilestonesStatus() == null ? "no status for " + m.getId() : m.getMilestonesStatus().getName()));
     * });
     */

    if (this.isPMU()) {
      this.loadTablePMU();

      reportSynthesis.getReportSynthesisFlagshipProgress().setCrpMilestones(new ArrayList<>());
      List<CrpProgramOutcome> selectedOutcomes = flagships.stream().filter(fp -> fp != null && fp.getId() != null)
        .flatMap(fp -> fp.getOutcomes().stream()).collect(Collectors.toList());

      if (this.isNotEmpty(selectedOutcomes)) {
        for (ReportSynthesisFlagshipProgressOutcomeMilestone outcomeMilestone : CollectionUtils
          .emptyIfNull(reportSynthesisFlagshipProgressOutcomeMilestoneManager
            .getAllFlagshipProgressOutcomeMilestones(this.getActualPhase().getId()))) {
          if (outcomeMilestone.getIsQAIncluded() != null && outcomeMilestone.getIsQAIncluded() == true) {
            reportSynthesis.getReportSynthesisFlagshipProgress().getCrpMilestones()
              .add(outcomeMilestone.getCrpMilestone());
          }
        }
      }
    }

    this.fillMilestoneReason();

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

      if (this.isPMU()) {
        this.updateQAInclusionList(reportSynthesis.getReportSynthesisFlagshipProgress());
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
        if (crossCuttingOwner.getJust() != null && !crossCuttingOwner.getJust().isEmpty()) {
          crossCuttingOwner.setJust(crossCuttingOwner.getJust().replaceAll("\"", "'"));
        }

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

  private void saveLinks(ReportSynthesisFlagshipProgressOutcomeMilestone flagshipProgressMilestoneNew,
    ReportSynthesisFlagshipProgressOutcomeMilestone flagshipProgressMilestoneOld) {
    // Search and deleted form Information
    if (this.isNotEmpty(flagshipProgressMilestoneNew.getReportSynthesisFlagshipProgressOutcomeMilestoneLinks())) {
      List<ReportSynthesisFlagshipProgressOutcomeMilestoneLink> linkPrev =
        new ArrayList<>(flagshipProgressMilestoneNew.getReportSynthesisFlagshipProgressOutcomeMilestoneLinks());

      for (ReportSynthesisFlagshipProgressOutcomeMilestoneLink milestoneLink : linkPrev) {
        if (flagshipProgressMilestoneOld.getLinks() == null
          || !flagshipProgressMilestoneOld.getLinks().contains(milestoneLink)) {
          this.reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager
            .deleteReportSynthesisFlagshipProgressOutcomeMilestoneLink(milestoneLink.getId());
        }
      }
    }

    // Save form Information
    if (flagshipProgressMilestoneOld.getLinks() != null) {
      for (ReportSynthesisFlagshipProgressOutcomeMilestoneLink milestoneLinkIncoming : flagshipProgressMilestoneOld
        .getLinks()) {
        if (milestoneLinkIncoming.getId() == null) {
          ReportSynthesisFlagshipProgressOutcomeMilestoneLink milestoneLinkSave =
            new ReportSynthesisFlagshipProgressOutcomeMilestoneLink();
          milestoneLinkSave.setReportSynthesisFlagshipProgressOutcomeMilestone(flagshipProgressMilestoneNew);
          milestoneLinkSave.setLink(milestoneLinkIncoming.getLink());

          this.reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager
            .saveReportSynthesisFlagshipProgressOutcomeMilestoneLink(milestoneLinkSave);
          // This is to add innovationLinkSave to generate correct
          // auditlog.
          flagshipProgressMilestoneNew.getReportSynthesisFlagshipProgressOutcomeMilestoneLinks().add(milestoneLinkSave);
        } else {
          ReportSynthesisFlagshipProgressOutcomeMilestoneLink milestoneLinkSave =
            this.reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager
              .getReportSynthesisFlagshipProgressOutcomeMilestoneLinkById(milestoneLinkIncoming.getId());
          milestoneLinkSave.setReportSynthesisFlagshipProgressOutcomeMilestone(flagshipProgressMilestoneNew);
          milestoneLinkSave.setLink(milestoneLinkIncoming.getLink());

          this.reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager
            .saveReportSynthesisFlagshipProgressOutcomeMilestoneLink(milestoneLinkSave);
          // This is to add innovationLinkSave to generate correct
          // auditlog.
          flagshipProgressMilestoneNew.getReportSynthesisFlagshipProgressOutcomeMilestoneLinks().add(milestoneLinkSave);
        }
      }
    }
  }

  public void saveMilestones(ReportSynthesisFlagshipProgressOutcome OutcomeDB,
    ReportSynthesisFlagshipProgressOutcome outcome) {
    if (!this.isPMU()) {
      List<ReportSynthesisFlagshipProgressOutcomeMilestone> progressMilestonesListOld = outcome.getMilestones();
      List<ReportSynthesisFlagshipProgressOutcomeMilestone> progressMilestonesListNew = outcome.getMilestones();

      if (progressMilestonesListOld == null) {
        progressMilestonesListOld = new ArrayList<>();
        outcome.setMilestones(progressMilestonesListOld);
      }

      // in theory, both old an new should have the same milestones, so the size should be the same
      for (int i = 0; i < progressMilestonesListOld.size(); i++) {
        ReportSynthesisFlagshipProgressOutcomeMilestone flagshipProgressMilestoneOld = progressMilestonesListOld.get(i);
        ReportSynthesisFlagshipProgressOutcomeMilestone flagshipProgressMilestoneIncoming =
          progressMilestonesListNew.get(i);
        ReportSynthesisFlagshipProgressOutcomeMilestone flagshipProgressMilestoneNew = null;

        if (flagshipProgressMilestoneOld != null) {
          if (flagshipProgressMilestoneOld.getCrpMilestone() != null
            && flagshipProgressMilestoneOld.getCrpMilestone().getId() > 0) {
            flagshipProgressMilestoneOld.setCrpMilestone(
              crpMilestoneManager.getCrpMilestoneById(flagshipProgressMilestoneOld.getCrpMilestone().getId()));
          }

          if (flagshipProgressMilestoneOld.getId() == null) {
            flagshipProgressMilestoneNew = new ReportSynthesisFlagshipProgressOutcomeMilestone();
            flagshipProgressMilestoneNew.setReportSynthesisFlagshipProgressOutcome(OutcomeDB);
          } else {
            flagshipProgressMilestoneNew = reportSynthesisFlagshipProgressOutcomeMilestoneManager
              .getReportSynthesisFlagshipProgressOutcomeMilestoneById(flagshipProgressMilestoneOld.getId());
          }

          // LOG.debug(flagshipProgressMilestoneIncoming.getReason() == null
          // ? "no reason for " + flagshipProgressMilestoneIncoming.getId()
          // : flagshipProgressMilestoneIncoming.getReason().getName() + " for milestone "
          // + flagshipProgressMilestoneIncoming.getId());
          GeneralStatus status =
            flagshipProgressMilestoneIncoming.getMilestonesStatus() == null ? null : generalStatusManager
              .getGeneralStatusById(flagshipProgressMilestoneIncoming.getMilestonesStatus().getId());
          flagshipProgressMilestoneNew.setMilestonesStatus(status);

          if (status != null && status.getId() != null) {
            // LOG.debug(status.getName() + " for milestone " + flagshipProgressMilestoneIncoming.getId());
            // LOG.debug(StringUtils.containsIgnoreCase(status.getName(), "omplete")
            // ? "Complete for " + flagshipProgressMilestoneIncoming.getId()
            // : "Other than complete for " + flagshipProgressMilestoneIncoming.getId());
            if (StringUtils.containsIgnoreCase(status.getName(), "xtended")) {
              flagshipProgressMilestoneNew.setExtendedYear(flagshipProgressMilestoneIncoming.getExtendedYear());
            } else {
              flagshipProgressMilestoneNew.setExtendedYear(null);
            }

            if (DeliverableStatusEnum.COMPLETE.equals(DeliverableStatusEnum.getValue(status.getId().intValue()))) {
              flagshipProgressMilestoneNew.setReason(null);
              flagshipProgressMilestoneNew.setOtherReason(null);
            } else {
              if (flagshipProgressMilestoneIncoming.getReason() != null) {
                if (flagshipProgressMilestoneIncoming.getReason().getId() != null
                  && flagshipProgressMilestoneIncoming.getReason().getId() != -1) {
                  RepIndMilestoneReason milestoneReason = repIndMilestoneReasonManager
                    .getRepIndMilestoneReasonById(flagshipProgressMilestoneIncoming.getReason().getId());

                  flagshipProgressMilestoneNew.setReason(milestoneReason);
                  if (StringUtils.containsIgnoreCase(milestoneReason.getName(), "ther")) {
                    flagshipProgressMilestoneNew.setOtherReason(flagshipProgressMilestoneOld.getOtherReason());
                  } else {
                    flagshipProgressMilestoneNew.setOtherReason(null);
                  }
                }
              }
            }
          }

          flagshipProgressMilestoneNew.setEvidence(flagshipProgressMilestoneIncoming.getEvidence());
          flagshipProgressMilestoneNew.setEvidenceLink(flagshipProgressMilestoneIncoming.getEvidenceLink());
          flagshipProgressMilestoneNew.setCrpMilestone(flagshipProgressMilestoneOld.getCrpMilestone());
          flagshipProgressMilestoneNew.setIsQAIncluded(flagshipProgressMilestoneOld.getIsQAIncluded());
          flagshipProgressMilestoneNew = reportSynthesisFlagshipProgressOutcomeMilestoneManager
            .saveReportSynthesisFlagshipProgressOutcomeMilestone(flagshipProgressMilestoneNew);

          this.saveCrossCutting(flagshipProgressMilestoneNew, flagshipProgressMilestoneOld);
          this.saveLinks(flagshipProgressMilestoneNew, flagshipProgressMilestoneOld);
        }
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

  public void setListOfFlagships(List<String> listOfFlagships) {
    this.listOfFlagships = listOfFlagships;
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

  private Long stringToLongNoException(String longString) {
    try {
      return Long.valueOf(StringUtils.stripToNull(longString));
    } catch (NumberFormatException nfe) {
      return -1L;
    }
  }

  private void updateQAInclusionList(ReportSynthesisFlagshipProgress flagshipProgressNew) {
    List<Long> selectedMilestones =
      Stream.of(StringUtils.split(StringUtils.trimToEmpty(flagshipProgressNew.getMilestonesValue()), ','))
        .map(this::stringToLongNoException).collect(Collectors.toList());

    for (ReportSynthesisFlagshipProgressOutcomeMilestone flagshipProgressOutcomeMilestone : CollectionUtils
      .emptyIfNull(reportSynthesisFlagshipProgressOutcomeMilestoneManager
        .getAllFlagshipProgressOutcomeMilestones(this.getActualPhase().getId()))) {
      Long milestoneId = flagshipProgressOutcomeMilestone.getCrpMilestone().getId();

      boolean newQAExclusionStatus = !selectedMilestones.contains(milestoneId);

      flagshipProgressOutcomeMilestone.setIsQAIncluded(newQAExclusionStatus);

      this.reportSynthesisFlagshipProgressOutcomeMilestoneManager
        .saveReportSynthesisFlagshipProgressOutcomeMilestone(flagshipProgressOutcomeMilestone);
    }
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, reportSynthesis, true);
    }
  }
}