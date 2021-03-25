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
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaActionStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaEvaluationActionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaEvaluationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaStudyManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
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
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressStudyDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMelia;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaActionStudy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluationAction;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaStudy;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.y2018.MonitoringEvaluationValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class MonitoringEvaluationAction extends BaseAction {


  private static final long serialVersionUID = -4468796840831686456L;


  // Managers
  private GlobalUnitManager crpManager;

  private LiaisonInstitutionManager liaisonInstitutionManager;


  private ReportSynthesisManager reportSynthesisManager;
  private AuditLogManager auditLogManager;
  private UserManager userManager;
  private CrpProgramManager crpProgramManager;
  private ReportSynthesisMeliaManager reportSynthesisMeliaManager;
  private MonitoringEvaluationValidator validator;
  private ProjectFocusManager projectFocusManager;
  private ProjectManager projectManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ReportSynthesisMeliaStudyManager reportSynthesisMeliaStudyManager;
  private ReportSynthesisMeliaActionStudyManager reportSynthesisMeliaActionStudyManager;
  private ReportSynthesisMeliaEvaluationManager reportSynthesisMeliaEvaluationManager;
  private ReportSynthesisMeliaEvaluationActionManager reportSynthesisMeliaEvaluationActionManager;
  private PhaseManager phaseManager;
  private SectionStatusManager sectionStatusManager;

  // Variables
  private String transaction;
  private ReportSynthesis reportSynthesis;

  private Long liaisonInstitutionID;
  private Long synthesisID;
  private LiaisonInstitution liaisonInstitution;
  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<ProjectExpectedStudy> studiesList;
  private List<PowbEvidencePlannedStudyDTO> flagshipPlannedList;
  private List<ReportSynthesisMeliaEvaluation> fpSynthesisTable;
  private List<ReportSynthesisMelia> reportSynthesisMeliaList;
  private List<ReportSynthesisMelia> flagshipMeliaProgress;
  private List<ProjectExpectedStudy> projectExpectedStudies;
  private List<ReportSynthesisMeliaActionStudy> projectExpectedStudyConverted;
  private List<ReportSynthesisMeliaActionStudy> selectedExpectedStudies;
  private List<ProjectExpectedStudy> selectedExpectedSt;
  private Map<Integer, String> statuses;
  private boolean tableComplete;
  private List<String> listOfFlagships;

  @Inject
  public MonitoringEvaluationAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, CrpProgramManager crpProgramManager,
    ReportSynthesisMeliaManager reportSynthesisMeliaManager, MonitoringEvaluationValidator validator,
    ProjectFocusManager projectFocusManager, ProjectManager projectManager,
    ProjectExpectedStudyManager projectExpectedStudyManager,
    ReportSynthesisMeliaStudyManager reportSynthesisMeliaStudyManager,
    ReportSynthesisMeliaActionStudyManager reportSynthesisMeliaActionStudyManager,
    ReportSynthesisMeliaEvaluationManager reportSynthesisMeliaEvaluationManager, PhaseManager phaseManager,
    ReportSynthesisMeliaEvaluationActionManager reportSynthesisMeliaEvaluationActionManager,
    SectionStatusManager sectionStatusManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.reportSynthesisMeliaManager = reportSynthesisMeliaManager;
    this.validator = validator;
    this.projectFocusManager = projectFocusManager;
    this.projectManager = projectManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.reportSynthesisMeliaStudyManager = reportSynthesisMeliaStudyManager;
    this.reportSynthesisMeliaActionStudyManager = reportSynthesisMeliaActionStudyManager;
    this.reportSynthesisMeliaEvaluationManager = reportSynthesisMeliaEvaluationManager;
    this.reportSynthesisMeliaEvaluationActionManager = reportSynthesisMeliaEvaluationActionManager;
    this.phaseManager = phaseManager;
    this.sectionStatusManager = sectionStatusManager;
  }

  /**
   * Method to fill the list of studies selected by flagships
   * 
   * @param flagshipsLiaisonInstitutions
   * @param phaseID
   * @return
   */
  public List<ReportSynthesisFlagshipProgressStudyDTO>
    fillFpPlannedList(List<LiaisonInstitution> flagshipsLiaisonInstitutions, long phaseID) {
    List<ReportSynthesisFlagshipProgressStudyDTO> flagshipPlannedList = new ArrayList<>();

    if (projectExpectedStudyManager.findAll() != null) {

      // Get global unit studies
      List<ProjectExpectedStudy> projectExpectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
        .filter(ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(this.getActualPhase()) != null
          && ps.getProject() != null
          && ps.getProject().getGlobalUnitProjects().stream()
            .filter(gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(loggedCrp.getId()))
            .collect(Collectors.toList()).size() > 0)
        .collect(Collectors.toList()));

      // Fill all project studies of the global unit
      for (ProjectExpectedStudy projectExpectedStudy : projectExpectedStudies) {

        if (projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()).getStudyType() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()).getStudyType().getId() != 1
          && projectExpectedStudy.getProjectExpectedStudyInfo().getStatus() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo().getYear().equals(this.getCurrentCycleYear())) {

          ReportSynthesisFlagshipProgressStudyDTO dto = new ReportSynthesisFlagshipProgressStudyDTO();
          projectExpectedStudy.getProject()
            .setProjectInfo(projectExpectedStudy.getProject().getProjecInfoPhase(this.getActualPhase()));
          dto.setProjectExpectedStudy(projectExpectedStudy);
          if (projectExpectedStudy.getProject().getProjectInfo() != null) {
            if (projectExpectedStudy.getProject().getProjectInfo().getAdministrative() != null
              && projectExpectedStudy.getProject().getProjectInfo().getAdministrative()) {
              dto.setLiaisonInstitutions(new ArrayList<>());
              dto.getLiaisonInstitutions().add(this.liaisonInstitution);
            } else {
              List<ProjectFocus> projectFocuses =
                new ArrayList<>(projectExpectedStudy.getProject().getProjectFocuses().stream()
                  .filter(pf -> pf.isActive() && pf.getPhase().getId().equals(phaseID)).collect(Collectors.toList()));
              List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
              for (ProjectFocus projectFocus : projectFocuses) {
                liaisonInstitutions.addAll(projectFocus.getCrpProgram().getLiaisonInstitutions().stream()
                  .filter(li -> li.isActive() && li.getCrpProgram() != null
                    && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
                    && li.getCrp() != null && li.getCrp().equals(this.getLoggedCrp()))
                  .collect(Collectors.toList()));
              }
              dto.setLiaisonInstitutions(liaisonInstitutions);
            }
            flagshipPlannedList.add(dto);
          }


        }
      }


      List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
        .filter(es -> es.isActive() && es.getProjectExpectedStudyInfo(this.getActualPhase()) != null
          && es.getProjectExpectedStudyInfo(this.getActualPhase()).getYear().equals(this.getCurrentCycleYear())
          && es.getProject() == null)
        .collect(Collectors.toList()));

      for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
        if (projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()) != null) {
          if (projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()).getStudyType() != null
            && projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()).getStudyType().getId() != 1
            && projectExpectedStudy.getProjectExpectedStudyInfo().getStatus() != null
            && projectExpectedStudy.getProjectExpectedStudyInfo().getYear().equals(this.getCurrentCycleYear())) {
            ReportSynthesisFlagshipProgressStudyDTO dto = new ReportSynthesisFlagshipProgressStudyDTO();

            dto.setProjectExpectedStudy(projectExpectedStudy);
            List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
            liaisonInstitutions.add(this.liaisonInstitution);
            dto.setLiaisonInstitutions(liaisonInstitutions);
            flagshipPlannedList.add(dto);
            // break;
          }
        }

      }

      // Get deleted studies
      List<ReportSynthesisMeliaStudy> flagshipProgressStudies = new ArrayList<>();
      for (LiaisonInstitution liaisonInstitution : flagshipsLiaisonInstitutions) {
        ReportSynthesis reportSynthesis = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
        if (reportSynthesis != null) {
          if (reportSynthesis.getReportSynthesisMelia() != null) {
            if (reportSynthesis.getReportSynthesisMelia().getReportSynthesisMeliaStudies() != null) {
              List<ReportSynthesisMeliaStudy> studies = new ArrayList<>(reportSynthesis.getReportSynthesisMelia()
                .getReportSynthesisMeliaStudies().stream().filter(s -> s.isActive()).collect(Collectors.toList()));
              if (studies != null || !studies.isEmpty()) {
                for (ReportSynthesisMeliaStudy reportSynthesisFlagshipProgressStudy : studies) {
                  flagshipProgressStudies.add(reportSynthesisFlagshipProgressStudy);
                }
              }
            }
          }
        }
      }

      // Get list of studies to remove
      List<ReportSynthesisFlagshipProgressStudyDTO> removeList = new ArrayList<>();
      for (ReportSynthesisFlagshipProgressStudyDTO dto : flagshipPlannedList) {

        List<LiaisonInstitution> removeLiaison = new ArrayList<>();
        for (LiaisonInstitution liaisonInstitution : dto.getLiaisonInstitutions()) {
          ReportSynthesis reportSynthesis = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
          if (reportSynthesis != null) {
            if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {

              ReportSynthesisMeliaStudy flagshipProgressStudyNew = new ReportSynthesisMeliaStudy();
              flagshipProgressStudyNew.setProjectExpectedStudy(dto.getProjectExpectedStudy());
              flagshipProgressStudyNew.setReportSynthesisMelia(reportSynthesis.getReportSynthesisMelia());

              if (flagshipProgressStudies.contains(flagshipProgressStudyNew)) {
                removeLiaison.add(liaisonInstitution);
              }
            }
          }
        }

        for (LiaisonInstitution li : removeLiaison) {
          dto.getLiaisonInstitutions().remove(li);
        }

        if (dto.getLiaisonInstitutions().isEmpty()) {
          removeList.add(dto);
        }
      }

      // Remove studies unselected by flagships
      for (ReportSynthesisFlagshipProgressStudyDTO i : removeList) {
        flagshipPlannedList.remove(i);
      }

    }
    return flagshipPlannedList;
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

  public void getAllProjectExpectedStudies() {
    projectExpectedStudies = new ArrayList<>();
    if (projectExpectedStudyManager.findAll() != null) {

      // Get global unit studies
      projectExpectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
        .filter(es -> es.isActive() && es.getProjectExpectedStudyInfo(this.getActualPhase()) != null
          && es.getProjectExpectedStudyInfo(this.getActualPhase()).getYear().equals(this.getCurrentCycleYear()))
        .collect(Collectors.toList()));
    }

    projectExpectedStudyConverted = new ArrayList<>();
    if (projectExpectedStudies != null) {
      Long i = (long) 1;
      for (ProjectExpectedStudy study : projectExpectedStudies) {
        ReportSynthesisMeliaActionStudy meliaActionStudy = new ReportSynthesisMeliaActionStudy();
        if (study != null && study.getId() != null) {
          meliaActionStudy
            .setProjectExpectedStudy(projectExpectedStudyManager.getProjectExpectedStudyById(study.getId()));
          meliaActionStudy.setId(i);
          projectExpectedStudyConverted.add(meliaActionStudy);
        }
        i++;
      }
    }
  }

  private Path getAutoSaveFilePath() {
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = reportSynthesis.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<ReportSynthesisMelia> getFlagshipMeliaProgress() {
    return flagshipMeliaProgress;
  }

  public List<PowbEvidencePlannedStudyDTO> getFlagshipPlannedList() {
    return flagshipPlannedList;
  }


  public void getFlagshipsWithMissingFields() {
    String flagshipsIncomplete = "";
    listOfFlagships = new ArrayList<>();
    SectionStatus sectionStatus = this.sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(),
      "Reporting", this.getActualPhase().getYear(), false, "melia");

    if (sectionStatus != null && sectionStatus.getMissingFields() != null && !sectionStatus.getMissingFields().isEmpty()
      && sectionStatus.getMissingFields().length() != 0 && sectionStatus.getSynthesisFlagships() != null
      && !sectionStatus.getSynthesisFlagships().isEmpty()
      && sectionStatus.getMissingFields().contains("synthesis.AR2019Table9/10")) {
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

  public List<ReportSynthesisMeliaEvaluation> getFpSynthesisTable() {
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

  public PhaseManager getPhaseManager() {
    return phaseManager;
  }

  public List<ProjectExpectedStudy> getProjectExpectedStudies() {
    return projectExpectedStudies;
  }

  public List<ReportSynthesisMeliaActionStudy> getProjectExpectedStudyConverted() {
    return projectExpectedStudyConverted;
  }

  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }

  public List<ReportSynthesisMelia> getReportSynthesisMeliaList() {
    return reportSynthesisMeliaList;
  }

  public List<ProjectExpectedStudy> getSelectedExpectedSt() {
    return selectedExpectedSt;
  }

  public List<ReportSynthesisMeliaActionStudy> getSelectedExpectedStudies() {
    return selectedExpectedStudies;
  }

  public Map<Integer, String> getStatuses() {
    return statuses;
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

  /**
   * This method get the status of an specific study depending of the
   * sectionStatuses
   *
   * @param expectedID is the study ID to be identified.
   * @return Boolean object with the status of the study
   */
  public Boolean isStudyComplete(long expectedID, long phaseID) {

    SectionStatus sectionStatus = this.sectionStatusManager.getSectionStatusByProjectExpectedStudy(expectedID,
      "Reporting", this.getActualPhase().getYear(), false, "studies");

    if (sectionStatus == null) {
      tableComplete = true;
      return true;
    }

    if (sectionStatus.getMissingFields().length() != 0) {
      tableComplete = false;
      return false;
    }

    tableComplete = true;
    return true;

  }

  @Override
  public void prepare() throws Exception {
    tableComplete = false;
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
      // Able to PMU Also
      this.studiesList(phase.getId(), liaisonInstitution);


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
        if (reportSynthesis.getReportSynthesisMelia() == null) {
          ReportSynthesisMelia melia = new ReportSynthesisMelia();
          // create one to one relation
          reportSynthesis.setReportSynthesisMelia(melia);;
          melia.setReportSynthesis(reportSynthesis);
          // save the changes
          reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }


        // Crp Progress Studies
        reportSynthesis.getReportSynthesisMelia().setExpectedStudies(new ArrayList<>());
        if (reportSynthesis.getReportSynthesisMelia().getReportSynthesisMeliaStudies() != null
          && !reportSynthesis.getReportSynthesisMelia().getReportSynthesisMeliaStudies().isEmpty()) {
          for (ReportSynthesisMeliaStudy plannedStudy : reportSynthesis.getReportSynthesisMelia()
            .getReportSynthesisMeliaStudies().stream().filter(ro -> ro.isActive()).collect(Collectors.toList())) {
            reportSynthesis.getReportSynthesisMelia().getExpectedStudies().add(plannedStudy.getProjectExpectedStudy());
          }
        }

        if (this.isPMU()) {
          if (reportSynthesis.getReportSynthesisMelia().getReportSynthesisMeliaEvaluations() != null
            && !reportSynthesis.getReportSynthesisMelia().getReportSynthesisMeliaEvaluations().isEmpty()) {
            reportSynthesis.getReportSynthesisMelia()
              .setEvaluations(new ArrayList<>(reportSynthesis.getReportSynthesisMelia()
                .getReportSynthesisMeliaEvaluations().stream().filter(e -> e.isActive()).collect(Collectors.toList())));
            reportSynthesis.getReportSynthesisMelia().getEvaluations()
              .sort(Comparator.comparing(ReportSynthesisMeliaEvaluation::getId));

            // load evaluation actions
            if (reportSynthesis.getReportSynthesisMelia().getEvaluations() != null
              && !reportSynthesis.getReportSynthesisMelia().getEvaluations().isEmpty()) {
              for (ReportSynthesisMeliaEvaluation reportSynthesisMeliaEvaluation : reportSynthesis
                .getReportSynthesisMelia().getEvaluations()) {

                if (reportSynthesisMeliaEvaluation.getReportSynthesisMeliaEvaluationActions() != null
                  && !reportSynthesisMeliaEvaluation.getReportSynthesisMeliaEvaluationActions().isEmpty()) {
                  reportSynthesisMeliaEvaluation.setMeliaEvaluationActions(
                    new ArrayList<>(reportSynthesisMeliaEvaluation.getReportSynthesisMeliaEvaluationActions().stream()
                      .filter(e -> e.isActive()).collect(Collectors.toList())));
                }
                /*
                 * if (reportSynthesisMeliaEvaluation.getReportSynthesisMeliaActionStudies() != null
                 * && !reportSynthesisMeliaEvaluation.getReportSynthesisMeliaActionStudies().isEmpty()) {
                 * reportSynthesisMeliaEvaluation.setMeliaActionsStudy(
                 * new ArrayList<>(reportSynthesisMeliaEvaluation.getReportSynthesisMeliaActionStudies().stream()
                 * .filter(e -> e.isActive()).collect(Collectors.toList())));
                 * }
                 */
              }
            }
          }

          // Flagship - Synthesis
          reportSynthesisMeliaList = reportSynthesisMeliaManager.getFlagshipMelia(liaisonInstitutions, phase.getId());
        }
      }
    }

    // Getting The list
    statuses = new HashMap<>();
    List<ProjectStatusEnum> listStatus = Arrays.asList(ProjectStatusEnum.values());
    for (ProjectStatusEnum globalStatusEnum : listStatus) {
      if (globalStatusEnum.getStatusId().equals("2") || globalStatusEnum.getStatusId().equals("3")) {
        statuses.put(Integer.parseInt(globalStatusEnum.getStatusId()), globalStatusEnum.getStatus());
      }
    }

    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

    // Get all expected studies
    this.getAllProjectExpectedStudies();

    if (this.isPMU()) {
      // Table I-1 PMU Information
      flagshipPlannedList = reportSynthesisMeliaManager.getMeliaPlannedList(liaisonInstitutions, phase.getId(),
        loggedCrp, this.liaisonInstitution);

      // Table I-2 Evaluations
      fpSynthesisTable = reportSynthesisMeliaManager.flagshipSynthesisEvaluation(liaisonInstitutions, phase.getId());

      // Flagships Synthesis Progress
      flagshipMeliaProgress = reportSynthesisMeliaManager.getFlagshipMelia(liaisonInstitutions, phase.getId());
    }

    // ADD PMU as liasion Institution too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym() != null && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));


    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_MELIA_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisMelia().getEvaluations() != null) {
        reportSynthesis.getReportSynthesisMelia().getEvaluations().clear();
      }

      if (reportSynthesis.getReportSynthesisMelia().getPlannedStudies() != null) {
        reportSynthesis.getReportSynthesisMelia().getPlannedStudies().clear();
      }

    }
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      ReportSynthesisMelia meliaDB =
        reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisMelia();

      // Dont save records (check marks in exclusion table) for Flagships
      if (this.isPMU()) {
        if (reportSynthesis.getReportSynthesisMelia().getPlannedStudies() == null) {
          reportSynthesis.getReportSynthesisMelia().setPlannedStudies(new ArrayList<>());
        }
        this.saveStudies(meliaDB);
      }

      if (this.isPMU()) {
        this.saveEvaluations(meliaDB);
      }

      meliaDB.setSummary(reportSynthesis.getReportSynthesisMelia().getSummary());

      meliaDB = reportSynthesisMeliaManager.saveReportSynthesisMelia(meliaDB);


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
   * Save Evaluations Information
   * 
   * @param crossCgiarDB
   */
  public void saveEvaluations(ReportSynthesisMelia meliaDB) {

    // Search and deleted form Information
    if (meliaDB.getReportSynthesisMeliaEvaluations() != null
      && meliaDB.getReportSynthesisMeliaEvaluations().size() > 0) {

      List<ReportSynthesisMeliaEvaluation> evaluationPrev = new ArrayList<>(
        meliaDB.getReportSynthesisMeliaEvaluations().stream().filter(nu -> nu.isActive()).collect(Collectors.toList()));

      for (ReportSynthesisMeliaEvaluation evaluation : evaluationPrev) {
        if (!reportSynthesis.getReportSynthesisMelia().getEvaluations().contains(evaluation)) {
          reportSynthesisMeliaEvaluationManager.deleteReportSynthesisMeliaEvaluation(evaluation.getId());
          // Delete evaluationActions
          if (evaluation.getReportSynthesisMeliaEvaluationActions() != null
            && !evaluation.getReportSynthesisMeliaEvaluationActions().isEmpty()) {
            for (ReportSynthesisMeliaEvaluationAction reportSynthesisMeliaEvaluationAction : evaluation
              .getReportSynthesisMeliaEvaluationActions()) {
              reportSynthesisMeliaEvaluationActionManager
                .deleteReportSynthesisMeliaEvaluationAction(reportSynthesisMeliaEvaluationAction.getId());
            }
          }
          // Delete actions studies
          if (evaluation.getReportSynthesisMeliaActionStudies() != null
            && !evaluation.getReportSynthesisMeliaActionStudies().isEmpty()) {
            for (ReportSynthesisMeliaActionStudy reportSynthesisMeliaEvaluationAction : evaluation
              .getMeliaActionsStudy()) {
              reportSynthesisMeliaActionStudyManager
                .deleteReportSynthesisMeliaActionStudy(reportSynthesisMeliaEvaluationAction.getId());
            }
          }
        }
      }
    }

    // Save form Information
    if (reportSynthesis.getReportSynthesisMelia().getEvaluations() != null) {
      for (ReportSynthesisMeliaEvaluation evaluation : reportSynthesis.getReportSynthesisMelia().getEvaluations()) {
        if (evaluation.getId() == null) {
          ReportSynthesisMeliaEvaluation evaluationSave = new ReportSynthesisMeliaEvaluation();

          evaluationSave.setReportSynthesisMelia(meliaDB);

          evaluationSave.setStatus(evaluation.getStatus());
          evaluationSave.setNameEvaluation(evaluation.getNameEvaluation());
          evaluationSave.setRecommendation(evaluation.getRecommendation());
          evaluationSave.setManagementResponse(evaluation.getManagementResponse());
          evaluationSave.setComments(evaluation.getComments());
          evaluationSave.setEvidences(evaluation.getEvidences());
          evaluationSave = reportSynthesisMeliaEvaluationManager.saveReportSynthesisMeliaEvaluation(evaluationSave);

          // Save evaluationActions
          for (ReportSynthesisMeliaEvaluationAction reportSynthesisMeliaEvaluationAction : evaluation
            .getMeliaEvaluationActions()) {
            ReportSynthesisMeliaEvaluationAction meliaEvaluationActionSave = new ReportSynthesisMeliaEvaluationAction();
            meliaEvaluationActionSave.setActions(reportSynthesisMeliaEvaluationAction.getActions());
            meliaEvaluationActionSave.setTextWhom(reportSynthesisMeliaEvaluationAction.getTextWhom());
            meliaEvaluationActionSave.setTextWhen(reportSynthesisMeliaEvaluationAction.getTextWhen());
            meliaEvaluationActionSave.setReportSynthesisMeliaEvaluation(evaluationSave);
            reportSynthesisMeliaEvaluationActionManager
              .saveReportSynthesisMeliaEvaluationAction(meliaEvaluationActionSave);
          }

          // Save evaluation Actions studies
          if (evaluation.getMeliaActionsStudy() != null) {
            for (ReportSynthesisMeliaActionStudy reportSynthesisMeliaActionStudy : evaluation.getMeliaActionsStudy()) {
              ReportSynthesisMeliaActionStudy meliaEvaluationActionSave = new ReportSynthesisMeliaActionStudy();
              meliaEvaluationActionSave
                .setProjectExpectedStudy(reportSynthesisMeliaActionStudy.getProjectExpectedStudy());
              meliaEvaluationActionSave.setReportSynthesisMeliaEvaluation(evaluationSave);
              reportSynthesisMeliaActionStudyManager
                .saveReportSynthesisMeliaActionStudy(reportSynthesisMeliaActionStudy);
            }
          }
        } else {

          ReportSynthesisMeliaEvaluation evaluationPrev =
            reportSynthesisMeliaEvaluationManager.getReportSynthesisMeliaEvaluationById(evaluation.getId());

          evaluationPrev.setStatus(evaluation.getStatus());
          evaluationPrev.setNameEvaluation(evaluation.getNameEvaluation());
          evaluationPrev.setRecommendation(evaluation.getRecommendation());
          evaluationPrev.setManagementResponse(evaluation.getManagementResponse());
          evaluationPrev.setComments(evaluation.getComments());
          evaluationPrev.setEvidences(evaluation.getEvidences());

          List<ReportSynthesisMeliaEvaluationAction> evaluationActionsPrev =
            new ArrayList<>(evaluationPrev.getReportSynthesisMeliaEvaluationActions().stream()
              .filter(nu -> nu.isActive()).collect(Collectors.toList()));

          for (ReportSynthesisMeliaEvaluationAction evaluationAction : evaluationActionsPrev) {
            if (evaluation.getMeliaEvaluationActions() == null || evaluation.getMeliaEvaluationActions().isEmpty()
              || !evaluation.getMeliaEvaluationActions().contains(evaluationAction)) {
              reportSynthesisMeliaEvaluationActionManager
                .deleteReportSynthesisMeliaEvaluationAction(evaluationAction.getId());
            }
          }


          // Save evaluation actions
          if (evaluation.getMeliaEvaluationActions() != null && !evaluation.getMeliaEvaluationActions().isEmpty()) {
            for (ReportSynthesisMeliaEvaluationAction reportSynthesisMeliaEvaluationAction : evaluation
              .getMeliaEvaluationActions()) {

              if (reportSynthesisMeliaEvaluationAction.getId() == null) {
                ReportSynthesisMeliaEvaluationAction meliaEvaluationActionSave =
                  new ReportSynthesisMeliaEvaluationAction();
                meliaEvaluationActionSave.setActions(reportSynthesisMeliaEvaluationAction.getActions());
                meliaEvaluationActionSave.setTextWhom(reportSynthesisMeliaEvaluationAction.getTextWhom());
                meliaEvaluationActionSave.setTextWhen(reportSynthesisMeliaEvaluationAction.getTextWhen());
                meliaEvaluationActionSave.setReportSynthesisMeliaEvaluation(evaluationPrev);
                reportSynthesisMeliaEvaluationActionManager
                  .saveReportSynthesisMeliaEvaluationAction(meliaEvaluationActionSave);
              } else {
                ReportSynthesisMeliaEvaluationAction evaluationActionUpdate =
                  reportSynthesisMeliaEvaluationActionManager
                    .getReportSynthesisMeliaEvaluationActionById(reportSynthesisMeliaEvaluationAction.getId());

                evaluationActionUpdate.setActions(reportSynthesisMeliaEvaluationAction.getActions());
                evaluationActionUpdate.setTextWhom(reportSynthesisMeliaEvaluationAction.getTextWhom());
                evaluationActionUpdate.setTextWhen(reportSynthesisMeliaEvaluationAction.getTextWhen());
                reportSynthesisMeliaEvaluationActionManager
                  .saveReportSynthesisMeliaEvaluationAction(evaluationActionUpdate);
              }

            }

          }

          // Save evaluation actions studies test
          if (selectedExpectedSt != null) {
            for (ProjectExpectedStudy reportSynthesisMeliaActionStudy : selectedExpectedSt) {

              if (reportSynthesisMeliaActionStudy.getId() == null) {
                ReportSynthesisMeliaActionStudy meliaEvaluationActionSave = new ReportSynthesisMeliaActionStudy();
                meliaEvaluationActionSave.setProjectExpectedStudy(reportSynthesisMeliaActionStudy);
                meliaEvaluationActionSave.setReportSynthesisMeliaEvaluation(evaluationPrev);
                reportSynthesisMeliaActionStudyManager.saveReportSynthesisMeliaActionStudy(meliaEvaluationActionSave);
              } else {
                /*
                 * ReportSynthesisMeliaActionStudy evaluationActionUpdate = reportSynthesisMeliaActionStudyManager
                 * .getReportSynthesisMeliaActionStudyById(reportSynthesisMeliaActionStudy.getId());
                 * evaluationActionUpdate
                 * .setProjectExpectedStudy(reportSynthesisMeliaActionStudy);
                 * evaluationActionUpdate.setReportSynthesisMeliaEvaluation(evaluationPrev);
                 * reportSynthesisMeliaActionStudyManager.saveReportSynthesisMeliaActionStudy(evaluationActionUpdate);
                 * reportSynthesisMeliaEvaluationManager.saveReportSynthesisMeliaEvaluation(evaluationPrev);
                 */
              }

            }

          }

          // Save evaluation actions studies
          if (evaluation.getMeliaActionsStudy() != null && !evaluation.getMeliaActionsStudy().isEmpty()) {
            for (ReportSynthesisMeliaActionStudy reportSynthesisMeliaActionStudy : evaluation.getMeliaActionsStudy()) {

              if (reportSynthesisMeliaActionStudy.getId() == null) {
                ReportSynthesisMeliaActionStudy meliaEvaluationActionSave = new ReportSynthesisMeliaActionStudy();
                meliaEvaluationActionSave
                  .setProjectExpectedStudy(reportSynthesisMeliaActionStudy.getProjectExpectedStudy());
                meliaEvaluationActionSave.setReportSynthesisMeliaEvaluation(evaluationPrev);
                reportSynthesisMeliaActionStudyManager.saveReportSynthesisMeliaActionStudy(meliaEvaluationActionSave);
              } else {
                ReportSynthesisMeliaActionStudy evaluationActionUpdate = reportSynthesisMeliaActionStudyManager
                  .getReportSynthesisMeliaActionStudyById(reportSynthesisMeliaActionStudy.getId());

                evaluationActionUpdate
                  .setProjectExpectedStudy(reportSynthesisMeliaActionStudy.getProjectExpectedStudy());
                evaluationActionUpdate.setReportSynthesisMeliaEvaluation(evaluationPrev);
                reportSynthesisMeliaActionStudyManager.saveReportSynthesisMeliaActionStudy(evaluationActionUpdate);
                reportSynthesisMeliaEvaluationManager.saveReportSynthesisMeliaEvaluation(evaluationPrev);
              }

            }

          }
          // Save evaluation actions studies test 3
          if (selectedExpectedStudies != null) {
            for (ReportSynthesisMeliaActionStudy reportSynthesisMeliaActionStudy : selectedExpectedStudies) {

              if (reportSynthesisMeliaActionStudy.getId() == null
                && reportSynthesisMeliaActionStudy.getProjectExpectedStudy() != null
                && reportSynthesisMeliaActionStudy.getProjectExpectedStudy().getId() != null) {
                ReportSynthesisMeliaActionStudy meliaEvaluationActionSave = new ReportSynthesisMeliaActionStudy();
                meliaEvaluationActionSave.setProjectExpectedStudy(projectExpectedStudyManager
                  .getProjectExpectedStudyById(reportSynthesisMeliaActionStudy.getProjectExpectedStudy().getId()));
                meliaEvaluationActionSave.setReportSynthesisMeliaEvaluation(evaluationPrev);
                reportSynthesisMeliaActionStudyManager.saveReportSynthesisMeliaActionStudy(meliaEvaluationActionSave);
              } else {
                ReportSynthesisMeliaActionStudy evaluationActionUpdate = reportSynthesisMeliaActionStudyManager
                  .getReportSynthesisMeliaActionStudyById(reportSynthesisMeliaActionStudy.getId());

                evaluationActionUpdate
                  .setProjectExpectedStudy(reportSynthesisMeliaActionStudy.getProjectExpectedStudy());
                evaluationActionUpdate.setReportSynthesisMeliaEvaluation(evaluationPrev);
                reportSynthesisMeliaActionStudyManager.saveReportSynthesisMeliaActionStudy(evaluationActionUpdate);
                reportSynthesisMeliaEvaluationManager.saveReportSynthesisMeliaEvaluation(evaluationPrev);
              }

            }

          }


        }
      }
    }
  }

  public void saveStudies(ReportSynthesisMelia meliaDB) {

    List<Long> selectedPs = new ArrayList<>();
    List<Long> studiesIds = new ArrayList<>();

    for (ProjectExpectedStudy std : studiesList) {
      studiesIds.add(std.getId());
    }

    if (reportSynthesis.getReportSynthesisMelia().getPlannedStudiesValue() != null
      && reportSynthesis.getReportSynthesisMelia().getPlannedStudiesValue().length() > 0) {
      List<Long> stList = new ArrayList<>();
      for (String string : reportSynthesis.getReportSynthesisMelia().getPlannedStudiesValue().trim().split(",")) {
        stList.add(Long.parseLong(string.trim()));
      }


      for (Long studyId : studiesIds) {
        int index = stList.indexOf(studyId);
        if (index < 0) {
          selectedPs.add(studyId);
        }


      }

      for (ReportSynthesisMeliaStudy reportStudy : meliaDB.getReportSynthesisMeliaStudies().stream()
        .filter(rio -> rio.isActive()).collect(Collectors.toList())) {
        if (!selectedPs.contains(reportStudy.getProjectExpectedStudy().getId())) {
          reportSynthesisMeliaStudyManager.deleteReportSynthesisMeliaStudy(reportStudy.getId());
        }
      }

      for (Long studyId : selectedPs) {
        ProjectExpectedStudy expectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(studyId);

        ReportSynthesisMeliaStudy meliaStudyNew = new ReportSynthesisMeliaStudy();
        meliaStudyNew = new ReportSynthesisMeliaStudy();
        meliaStudyNew.setProjectExpectedStudy(expectedStudy);
        meliaStudyNew.setReportSynthesisMelia(meliaDB);

        List<ReportSynthesisMeliaStudy> meliaStudies =
          meliaDB.getReportSynthesisMeliaStudies().stream().filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!meliaStudies.contains(meliaStudyNew)) {
          meliaStudyNew = reportSynthesisMeliaStudyManager.saveReportSynthesisMeliaStudy(meliaStudyNew);
        }
      }
    } else {

      for (Long studyId : studiesIds) {
        ProjectExpectedStudy expectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(studyId);

        ReportSynthesisMeliaStudy meliaStudyNew = new ReportSynthesisMeliaStudy();
        meliaStudyNew = new ReportSynthesisMeliaStudy();
        meliaStudyNew.setProjectExpectedStudy(expectedStudy);
        meliaStudyNew.setReportSynthesisMelia(meliaDB);

        List<ReportSynthesisMeliaStudy> meliaStudies =
          meliaDB.getReportSynthesisMeliaStudies().stream().filter(rio -> rio.isActive()).collect(Collectors.toList());


        if (!meliaStudies.contains(meliaStudyNew)) {
          meliaStudyNew = reportSynthesisMeliaStudyManager.saveReportSynthesisMeliaStudy(meliaStudyNew);
        }
      }

    }
  }

  public void setFlagshipMeliaProgress(List<ReportSynthesisMelia> flagshipMeliaProgress) {
    this.flagshipMeliaProgress = flagshipMeliaProgress;
  }

  public void setFlagshipPlannedList(List<PowbEvidencePlannedStudyDTO> flagshipPlannedList) {
    this.flagshipPlannedList = flagshipPlannedList;
  }

  public void setFpSynthesisTable(List<ReportSynthesisMeliaEvaluation> fpSynthesisTable) {
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

  public void setPhaseManager(PhaseManager phaseManager) {
    this.phaseManager = phaseManager;
  }

  public void setProjectExpectedStudies(List<ProjectExpectedStudy> projectExpectedStudies) {
    this.projectExpectedStudies = projectExpectedStudies;
  }

  public void setProjectExpectedStudyConverted(List<ReportSynthesisMeliaActionStudy> projectExpectedStudyConverted) {
    this.projectExpectedStudyConverted = projectExpectedStudyConverted;
  }

  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }


  public void setReportSynthesisMeliaList(List<ReportSynthesisMelia> reportSynthesisMeliaList) {
    this.reportSynthesisMeliaList = reportSynthesisMeliaList;
  }


  public void setSelectedExpectedSt(List<ProjectExpectedStudy> selectedExpectedSt) {
    this.selectedExpectedSt = selectedExpectedSt;
  }

  public void setSelectedExpectedStudies(List<ReportSynthesisMeliaActionStudy> selectedExpectedStudies) {
    this.selectedExpectedStudies = selectedExpectedStudies;
  }

  public void setStatuses(Map<Integer, String> statuses) {
    this.statuses = statuses;
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
    if (this.isFlagship()) {
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
          for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
            if (projectExpectedStudy.getProjectExpectedStudyInfo(phase) != null) {
              if (projectExpectedStudy.getProjectExpectedStudyInfo(phase).getStudyType() != null
                && projectExpectedStudy.getProjectExpectedStudyInfo(phase).getStudyType().getId() != 1) {
                studiesList.add(projectExpectedStudy);
              }
            }
          }
        }

        List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
          .filter(es -> es.isActive() && es.getProjectExpectedStudyInfo(phase) != null
            && es.getProjectExpectedStudyInfo(phase).getYear().equals(this.getCurrentCycleYear())
            && es.getProject() == null)
          .collect(Collectors.toList()));

        for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
          if (projectExpectedStudy.getProjectExpectedStudyInfo(phase) != null) {
            List<ProjectExpectedStudyFlagship> studiesPrograms =
              new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyFlagships().stream()
                .filter(s -> s.isActive() && s.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
            for (ProjectExpectedStudyFlagship projectExpectedStudyFlagship : studiesPrograms) {
              CrpProgram crpProgram = liaisonInstitution.getCrpProgram();
              if (crpProgram.equals(projectExpectedStudyFlagship.getCrpProgram())) {
                if (projectExpectedStudy.getProjectExpectedStudyInfo(phase) != null) {
                  if (projectExpectedStudy.getProjectExpectedStudyInfo(phase).getStudyType() != null
                    && projectExpectedStudy.getProjectExpectedStudyInfo(phase).getStudyType().getId() != 1
                    && projectExpectedStudy.getProjectExpectedStudyInfo().getStatus() != null && projectExpectedStudy
                      .getProjectExpectedStudyInfo().getYear().equals(this.getCurrentCycleYear())) {
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
            projectExpectedStudy.setSubIdos(new ArrayList<>(projectExpectedStudy.getProjectExpectedStudySubIdos()
              .stream().filter(s -> s.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
          }
        }

      }
    } else {
      // Fill Project Expected Studies of the PMU, removing flagship deletions
      liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() != null && c.isActive()
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());
      liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

      List<ReportSynthesisFlagshipProgressStudyDTO> flagshipPlannedList =
        this.fillFpPlannedList(liaisonInstitutions, phase.getId());

      for (ReportSynthesisFlagshipProgressStudyDTO reportSynthesisFlagshipProgressStudyDTO : flagshipPlannedList) {

        ProjectExpectedStudy projectExpectedStudy = reportSynthesisFlagshipProgressStudyDTO.getProjectExpectedStudy();
        projectExpectedStudy.getProjectExpectedStudyInfo(phase);
        projectExpectedStudy.setSelectedFlahsgips(new ArrayList<>());
        // sort selected flagships
        if (reportSynthesisFlagshipProgressStudyDTO.getLiaisonInstitutions() != null
          && !reportSynthesisFlagshipProgressStudyDTO.getLiaisonInstitutions().isEmpty()) {
          reportSynthesisFlagshipProgressStudyDTO.getLiaisonInstitutions()
            .sort((l1, l2) -> l1.getCrpProgram().getAcronym().compareTo(l2.getCrpProgram().getAcronym()));
        }
        projectExpectedStudy.getSelectedFlahsgips()
          .addAll(reportSynthesisFlagshipProgressStudyDTO.getLiaisonInstitutions());
        studiesList.add(projectExpectedStudy);

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

