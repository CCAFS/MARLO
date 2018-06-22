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
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaEvaluationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaStudyManager;
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
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMelia;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaStudy;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.MeliaValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
public class MeliaAction extends BaseAction {


  private static final long serialVersionUID = -4468796840831686456L;


  // Managers
  private GlobalUnitManager crpManager;

  private LiaisonInstitutionManager liaisonInstitutionManager;


  private ReportSynthesisManager reportSynthesisManager;
  private AuditLogManager auditLogManager;
  private UserManager userManager;
  private CrpProgramManager crpProgramManager;
  private ReportSynthesisMeliaManager reportSynthesisMeliaManager;
  private MeliaValidator validator;
  private ProjectFocusManager projectFocusManager;
  private ProjectManager projectManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ReportSynthesisMeliaStudyManager reportSynthesisMeliaStudyManager;
  private ReportSynthesisMeliaEvaluationManager reportSynthesisMeliaEvaluationManager;
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
  private List<PowbEvidencePlannedStudyDTO> flagshipPlannedList;
  private List<ReportSynthesisMeliaEvaluation> fpSynthesisTable;
  private List<ReportSynthesisMelia> flagshipMeliaProgress;
  private Map<Integer, String> statuses;

  @Inject
  public MeliaAction(APConfig config, GlobalUnitManager crpManager, LiaisonInstitutionManager liaisonInstitutionManager,
    ReportSynthesisManager reportSynthesisManager, AuditLogManager auditLogManager, UserManager userManager,
    CrpProgramManager crpProgramManager, ReportSynthesisMeliaManager reportSynthesisMeliaManager,
    MeliaValidator validator, ProjectFocusManager projectFocusManager, ProjectManager projectManager,
    ProjectExpectedStudyManager projectExpectedStudyManager,
    ReportSynthesisMeliaStudyManager reportSynthesisMeliaStudyManager,
    ReportSynthesisMeliaEvaluationManager reportSynthesisMeliaEvaluationManager, PhaseManager phaseManager) {
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
    this.reportSynthesisMeliaEvaluationManager = reportSynthesisMeliaEvaluationManager;
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
    String autoSaveFile = reportSynthesis.getId() + "_" + composedClassName + "_"
      + this.getActualPhase().getDescription() + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<ReportSynthesisMelia> getFlagshipMeliaProgress() {
    return flagshipMeliaProgress;
  }

  public List<PowbEvidencePlannedStudyDTO> getFlagshipPlannedList() {
    return flagshipPlannedList;
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

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public PhaseManager getPhaseManager() {
    return phaseManager;
  }

  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
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


        if (this.isFlagship()) {
          // Crp Progress Studies
          reportSynthesis.getReportSynthesisMelia().setExpectedStudies(new ArrayList<>());
          if (reportSynthesis.getReportSynthesisMelia().getReportSynthesisMeliaStudies() != null
            && !reportSynthesis.getReportSynthesisMelia().getReportSynthesisMeliaStudies().isEmpty()) {
            for (ReportSynthesisMeliaStudy plannedStudy : reportSynthesis.getReportSynthesisMelia()
              .getReportSynthesisMeliaStudies().stream().filter(ro -> ro.isActive()).collect(Collectors.toList())) {
              reportSynthesis.getReportSynthesisMelia().getExpectedStudies()
                .add(plannedStudy.getProjectExpectedStudy());
            }
          }

          if (reportSynthesis.getReportSynthesisMelia().getReportSynthesisMeliaEvaluations() != null
            && !reportSynthesis.getReportSynthesisMelia().getReportSynthesisMeliaEvaluations().isEmpty()) {
            reportSynthesis.getReportSynthesisMelia()
              .setEvaluations(new ArrayList<>(reportSynthesis.getReportSynthesisMelia()
                .getReportSynthesisMeliaEvaluations().stream().filter(e -> e.isActive()).collect(Collectors.toList())));
          }
        }
      }
    }

    // Getting The list
    statuses = new HashMap<>();
    List<ProjectStatusEnum> listStatus = Arrays.asList(ProjectStatusEnum.values());
    for (ProjectStatusEnum globalStatusEnum : listStatus) {
      statuses.put(Integer.parseInt(globalStatusEnum.getStatusId()), globalStatusEnum.getStatus());
    }

    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));


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
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym().equals("PMU"))
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

      if (this.isFlagship()) {
        if (reportSynthesis.getReportSynthesisMelia().getPlannedStudies() == null) {
          reportSynthesis.getReportSynthesisMelia().setPlannedStudies(new ArrayList<>());
        }
        this.saveStudies(meliaDB);

      } else {
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
          evaluationSave.setWhom(evaluation.getWhom());
          evaluationSave.setWhen(evaluation.getWhen());


          reportSynthesisMeliaEvaluationManager.saveReportSynthesisMeliaEvaluation(evaluationSave);
        } else {

          ReportSynthesisMeliaEvaluation evaluationPrev =
            reportSynthesisMeliaEvaluationManager.getReportSynthesisMeliaEvaluationById(evaluation.getId());


          evaluationPrev.setStatus(evaluation.getStatus());
          evaluationPrev.setNameEvaluation(evaluation.getNameEvaluation());
          evaluationPrev.setRecommendation(evaluation.getRecommendation());
          evaluationPrev.setManagementResponse(evaluation.getManagementResponse());
          evaluationPrev.setWhom(evaluation.getWhom());
          evaluationPrev.setWhen(evaluation.getWhen());

          reportSynthesisMeliaEvaluationManager.saveReportSynthesisMeliaEvaluation(evaluationPrev);

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

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setPhaseManager(PhaseManager phaseManager) {
    this.phaseManager = phaseManager;
  }

  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
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
              && projectExpectedStudy.getProjectExpectedStudyInfo(phase).getStudyType().getId() != 1) {
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
                  && projectExpectedStudy.getProjectExpectedStudyInfo(phase).getStudyType().getId() != 1) {
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

