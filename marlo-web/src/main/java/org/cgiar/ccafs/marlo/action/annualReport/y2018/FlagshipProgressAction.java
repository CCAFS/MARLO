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
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.y2018.FlagshipProgress2018Validator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Andrés Valencia - CIAT/CCAFS
 */
public class FlagshipProgressAction extends BaseAction {

  private static final long serialVersionUID = -7779248645237507262L;


  // Managers
  private GlobalUnitManager crpManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private AuditLogManager auditLogManager;
  private UserManager userManager;
  private CrpProgramManager crpProgramManager;
  private FlagshipProgress2018Validator validator;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private SectionStatusManager sectionStatusManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ProjectFocusManager projectFocusManager;

  // variables
  private String transaction;
  private ReportSynthesis reportSynthesis;
  private Long liaisonInstitutionID;
  private Long synthesisID;
  private LiaisonInstitution liaisonInstitution;
  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<ReportSynthesisFlagshipProgress> flagshipsReportSynthesisFlagshipProgress;
  private boolean hasFlagshipProgress;
  private List<String> listOfFlagships;
  private List<ProjectExpectedStudy> covidAnalysisStudies;


  @Inject
  public FlagshipProgressAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, CrpProgramManager crpProgramManager,
    FlagshipProgress2018Validator validator,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    SectionStatusManager sectionStatusManager, ProjectExpectedStudyManager projectExpectedStudyManager,
    ProjectFocusManager projectFocusManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.validator = validator;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.sectionStatusManager = sectionStatusManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.projectFocusManager = projectFocusManager;
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

  public List<ProjectExpectedStudy> getCovidAnalysisStudies() {
    return covidAnalysisStudies;
  }

  public List<ReportSynthesisFlagshipProgress> getFlagshipsReportSynthesisFlagshipProgress() {
    return flagshipsReportSynthesisFlagshipProgress;
  }


  public void getFlagshipsWithMissingFields() {
    String flagshipsIncomplete = "";
    listOfFlagships = new ArrayList<>();
    SectionStatus sectionStatus = this.sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(),
      "Reporting", this.getActualPhase().getYear(), false, "flagshipProgress");

    if (sectionStatus != null && sectionStatus.getMissingFields() != null && !sectionStatus.getMissingFields().isEmpty()
      && sectionStatus.getMissingFields().length() != 0 && sectionStatus.getSynthesisFlagships() != null
      && !sectionStatus.getSynthesisFlagships().isEmpty()
      && sectionStatus.getMissingFields().contains("flagshipProgress1")) {
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

  public void getInfoCovidAnalisysStudies() {

    covidAnalysisStudies = projectExpectedStudyManager.findAll().stream()
      .filter(s -> s.getProjectExpectedStudyInfo(this.getActualPhase()) != null
        && s.getProjectExpectedStudyInfo(this.getActualPhase()).getHasCovidAnalysis() != null
        && s.getProjectExpectedStudyInfo(this.getActualPhase()).getHasCovidAnalysis())
      .collect(Collectors.toList());

    // Fill flagships information from Project Focus
    if (covidAnalysisStudies != null && !covidAnalysisStudies.isEmpty()) {
      for (ProjectExpectedStudy study : covidAnalysisStudies) {

        // Get the project flagships for each study
        if (study.getProject() != null && study.getProject().getId() != null) {
          List<ProjectFocus> focusList = projectFocusManager.findByProjectId(study.getProject().getId());
          if (focusList != null && !focusList.isEmpty()) {

            // Filter project Focuses for actual phase
            focusList = focusList.stream().filter(f -> f.getPhase() != null && this.getActualPhase() != null
              && f.getPhase().getId().equals(this.getActualPhase().getId())).collect(Collectors.toList());

            List<CrpProgram> programs = new ArrayList<>();
            for (ProjectFocus focus : focusList) {

              // Get CRP Program ID for each Project Focus
              // Filter the crpPrograms without SmoCode
              CrpProgram program = new CrpProgram();
              if (focus.getCrpProgram() != null && focus.getCrpProgram().getId() != null
                && focus.getCrpProgram().getProgramType() == 1 && focus.getCrpProgram().getSmoCode() != null
                && !focus.getCrpProgram().getSmoCode().isEmpty()) {
                program = crpProgramManager.getCrpProgramById(focus.getCrpProgram().getId());
              }

              if (program != null) {
                programs.add(program);
              }
            }

            if (programs != null && !programs.isEmpty()) {
              List<ProjectExpectedStudyFlagship> studyFlagships = new ArrayList<>();
              for (CrpProgram program : programs) {
                ProjectExpectedStudyFlagship studyFlagship = new ProjectExpectedStudyFlagship();
                studyFlagship.setPhase(this.getActualPhase());
                studyFlagship.setCrpProgram(program);
                studyFlagship.setProjectExpectedStudy(study);
                studyFlagships.add(studyFlagship);
              }

              // Assign flagship information to each study
              study.setFlagships(studyFlagships);
            }

          }
        }
      }

      // Remove Covid Analisys Studies without this CRP Program (only for Synthesis Flagships)
      if (!this.isPMU()) {
        CrpProgram actualCrpProgram =
          crpProgramManager.getCrpProgramById(liaisonInstitution.getCrpProgram().getId().longValue());

        List<ProjectExpectedStudy> studies = new ArrayList<>();
        if (actualCrpProgram != null && actualCrpProgram.getId() != null) {
          for (ProjectExpectedStudy study : covidAnalysisStudies) {
            if (study.getFlagships() != null && !study.getFlagships().isEmpty()) {
              for (ProjectExpectedStudyFlagship studyFlagship : study.getFlagships()) {
                if (studyFlagship != null && studyFlagship.getCrpProgram() != null
                  && studyFlagship.getCrpProgram().getId() != null
                  && studyFlagship.getCrpProgram().getId().equals(actualCrpProgram.getId())) {
                  studies.add(study);
                }
              }
            }
          }

          if (studies != null && !studies.isEmpty()) {
            covidAnalysisStudies = studies;
          }
        }
      }
    }
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

  public boolean isHasFlagshipProgress() {
    return hasFlagshipProgress;
  }

  @Override
  public boolean isPMU() {
    boolean isPMU = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() == null) {
        isPMU = true;
      }
    }
    return isPMU;

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
      }
    }

    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

    // Flagship - Synthesis
    if (this.isPMU()) {
      flagshipsReportSynthesisFlagshipProgress = reportSynthesisFlagshipProgressManager
        .getFlagshipsReportSynthesisFlagshipProgress(liaisonInstitutions, phase.getId());
      if (flagshipsReportSynthesisFlagshipProgress != null) {
        int count = 0;
        hasFlagshipProgress = false;
        if (flagshipsReportSynthesisFlagshipProgress.stream()
          .filter(f -> f != null && f.getProgressByFlagships() != null && !f.getProgressByFlagships().isEmpty())
          .collect(Collectors.toList()) != null
          && flagshipsReportSynthesisFlagshipProgress.stream()
            .filter(f -> f != null && f.getProgressByFlagships() != null && !f.getProgressByFlagships().isEmpty())
            .collect(Collectors.toList()).size() > 0) {
          count++;
        }
        if (flagshipsReportSynthesisFlagshipProgress.stream()
          .filter(f -> f != null && f.getDetailedAnnex() != null && !f.getDetailedAnnex().isEmpty())
          .collect(Collectors.toList()) != null
          && flagshipsReportSynthesisFlagshipProgress.stream()
            .filter(f -> f != null && f.getDetailedAnnex() != null && !f.getDetailedAnnex().isEmpty())
            .collect(Collectors.toList()).size() > 0) {
          count++;
        }

        if (count > 0) {
          hasFlagshipProgress = true;
        }
      }
    }

    // ADD PMU as liasion Institution too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym() != null && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));

    this.getInfoCovidAnalisysStudies();

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_FLAGSHIP_PROGRESS_BASE_PERMISSION, params));
  }


  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {


      ReportSynthesisFlagshipProgress flagshipProgressDB =
        reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisFlagshipProgress();

      if (this.isFlagship()) {
        flagshipProgressDB
          .setProgressByFlagships(reportSynthesis.getReportSynthesisFlagshipProgress().getProgressByFlagships());
        flagshipProgressDB.setDetailedAnnex(reportSynthesis.getReportSynthesisFlagshipProgress().getDetailedAnnex());
      } else {
        flagshipProgressDB
          .setOverallProgress(reportSynthesis.getReportSynthesisFlagshipProgress().getOverallProgress());
      }

      flagshipProgressDB
        .setExpandedResearchAreas(reportSynthesis.getReportSynthesisFlagshipProgress().getExpandedResearchAreas());
      flagshipProgressDB
        .setDroppedResearchLines(reportSynthesis.getReportSynthesisFlagshipProgress().getDroppedResearchLines());
      flagshipProgressDB
        .setChangedDirection(reportSynthesis.getReportSynthesisFlagshipProgress().getChangedDirection());
      flagshipProgressDB.setAltmetricScore(reportSynthesis.getReportSynthesisFlagshipProgress().getAltmetricScore());

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
        List<String> keys = new ArrayList<>(this.getInvalidFields().keySet());
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

  public void setCovidAnalysisStudies(List<ProjectExpectedStudy> covidAnalysisStudies) {
    this.covidAnalysisStudies = covidAnalysisStudies;
  }

  public void setFlagshipsReportSynthesisFlagshipProgress(
    List<ReportSynthesisFlagshipProgress> flagshipsReportSynthesisFlagshipProgress) {
    this.flagshipsReportSynthesisFlagshipProgress = flagshipsReportSynthesisFlagshipProgress;
  }


  public void setHasFlagshipProgress(boolean hasFlagshipProgress) {
    this.hasFlagshipProgress = hasFlagshipProgress;
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
      if (this.isPMU()) {
        validator.validateCheckButton(this, reportSynthesis, true, hasFlagshipProgress);
      } else {
        validator.validate(this, reportSynthesis, true, hasFlagshipProgress);
      }
    }
  }

}
