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
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressMilestone;
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
public class FlagshipProgressAction extends BaseAction {


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


  private CrpMilestoneManager crpMilestoneManager;


  private PhaseManager phaseManager;


  // variables
  private String transaction;


  private ReportSynthesis reportSynthesis;


  private Long liaisonInstitutionID;

  private Long synthesisID;
  private LiaisonInstitution liaisonInstitution;
  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<CrpProgramOutcome> outcomes;

  @Inject
  public FlagshipProgressAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, CrpProgramManager crpProgramManager,
    FlagshipProgressValidator validator, ProjectFocusManager projectFocusManager, ProjectManager projectManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ReportSynthesisFlagshipProgressMilestoneManager reportSynthesisFlagshipProgressMilestoneManager,
    CrpMilestoneManager crpMilestoneManager, PhaseManager phaseManager) {
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

  public int getIndex(Long crpMilestoneID) {
    if (reportSynthesis.getReportSynthesisFlagshipProgress().getMilestones() != null) {
      int i = 0;
      for (ReportSynthesisFlagshipProgressMilestone flagshipProgressMilestone : reportSynthesis
        .getReportSynthesisFlagshipProgress().getMilestones()) {
        if (flagshipProgressMilestone != null && flagshipProgressMilestone.getCrpMilestone() != null
          && flagshipProgressMilestone.getCrpMilestone().getId() != null) {
          if (flagshipProgressMilestone.getCrpMilestone().getId().longValue() == crpMilestoneID.longValue()) {
            return i;
          }
        }
        i++;
      }

    } else {
      reportSynthesis.getReportSynthesisFlagshipProgress().setMilestones(new ArrayList<>());
    }

    ReportSynthesisFlagshipProgressMilestone flagshipProgressMilestone = new ReportSynthesisFlagshipProgressMilestone();
    flagshipProgressMilestone.setCrpMilestone(crpMilestoneManager.getCrpMilestoneById(crpMilestoneID));
    flagshipProgressMilestone.setReportSynthesisFlagshipProgress(reportSynthesis.getReportSynthesisFlagshipProgress());
    reportSynthesis.getReportSynthesisFlagshipProgress().getMilestones().add(flagshipProgressMilestone);

    return this.getIndex(crpMilestoneID);

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

  public List<CrpProgramOutcome> getOutcomes() {
    return outcomes;
  }

  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }

  public ReportSynthesisFlagshipProgressMilestone getReportSynthesisFlagshipProgressMilestone(Long crpMilestoneID) {
    return reportSynthesis.getReportSynthesisFlagshipProgress().getMilestones().get(this.getIndex(crpMilestoneID));
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

            reportSynthesis.getReportSynthesisFlagshipProgress().setMilestones(
              reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressMilestones()
                .stream().filter(c -> c.isActive() && c.getCrpMilestone() != null).collect(Collectors.toList()));

            reportSynthesis.getReportSynthesisFlagshipProgress().getMilestones()
              .sort((p1, p2) -> p1.getCrpMilestone().getId().compareTo(p2.getCrpMilestone().getId()));
          }

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

    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));


    if (this.isPMU()) {

      // TODO

    }

    // ADD PMU as liasion Institution too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));


    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_FLAGSHIP_PROGRESS_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisFlagshipProgress().getMilestones() != null) {
        reportSynthesis.getReportSynthesisFlagshipProgress().getMilestones().clear();
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
      }

      flagshipProgressDB.setSummary(reportSynthesis.getReportSynthesisFlagshipProgress().getSummary());

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


  public void saveFlagshipProgressNewData(ReportSynthesisFlagshipProgress flagshipProgressDB) {

    if (reportSynthesis.getReportSynthesisFlagshipProgress().getMilestones() == null) {
      reportSynthesis.getReportSynthesisFlagshipProgress().setMilestones(new ArrayList<>());
    }

    for (ReportSynthesisFlagshipProgressMilestone flagshipProgressMilestone : reportSynthesis
      .getReportSynthesisFlagshipProgress().getMilestones()) {
      ReportSynthesisFlagshipProgressMilestone flagshipProgressMilestoneNew = null;

      if (flagshipProgressMilestone != null) {

        if (flagshipProgressMilestone.getCrpMilestone() != null
          && flagshipProgressMilestone.getCrpMilestone().getId() > 0) {
          flagshipProgressMilestone.setCrpMilestone(
            crpMilestoneManager.getCrpMilestoneById(flagshipProgressMilestone.getCrpMilestone().getId()));
        }

        if (flagshipProgressMilestone.getId() == null) {
          flagshipProgressMilestoneNew = new ReportSynthesisFlagshipProgressMilestone();
          flagshipProgressMilestoneNew.setReportSynthesisFlagshipProgress(flagshipProgressDB);
        } else {
          flagshipProgressMilestoneNew = reportSynthesisFlagshipProgressMilestoneManager
            .getReportSynthesisFlagshipProgressMilestoneById(flagshipProgressMilestone.getId());
        }

        flagshipProgressMilestoneNew.setEvidence(flagshipProgressMilestone.getEvidence());
        flagshipProgressMilestoneNew.setMilestonesStatus(flagshipProgressMilestone.getMilestonesStatus());
        flagshipProgressMilestoneNew.setCrpMilestone(flagshipProgressMilestone.getCrpMilestone());

        flagshipProgressMilestoneNew = reportSynthesisFlagshipProgressMilestoneManager
          .saveReportSynthesisFlagshipProgressMilestone(flagshipProgressMilestoneNew);


      }

    }

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
