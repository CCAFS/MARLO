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
import org.cgiar.ccafs.marlo.data.manager.DeliverableParticipantManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCuttingDimensionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipant;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimension;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.y2018.CCDimension2018Validator;

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
public class CrossCuttingDimensionAction extends BaseAction {

  private static final long serialVersionUID = -4977149795769459191L;

  // Managers
  private GlobalUnitManager crpManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private AuditLogManager auditLogManager;
  private CrpProgramManager crpProgramManager;
  private UserManager userManager;
  private ReportSynthesisCrossCuttingDimensionManager reportSynthesisCrossCuttingDimensionManager;
  private DeliverableParticipantManager deliverableParticipantManager;
  private CCDimension2018Validator validator;

  // Variables
  private String transaction;
  private ReportSynthesis reportSynthesis;
  private Long liaisonInstitutionID;
  private Long synthesisID;
  private LiaisonInstitution liaisonInstitution;
  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<ReportSynthesisCrossCuttingDimension> flagshipCCDimensions;
  private Double totalParticipants = new Double(0);
  private Double totalParticipantFormalTraining = new Double(0);
  private Double totalParticipantFormalTrainingShortMale = new Double(0);
  private Double totalParticipantFormalTrainingLongMale = new Double(0);
  private Double totalParticipantFormalTrainingShortFemale = new Double(0);
  private Double totalParticipantFormalTrainingLongFemale = new Double(0);
  private List<DeliverableParticipant> deliverableParticipants;


  @Inject
  public CrossCuttingDimensionAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager,
    ReportSynthesisCrossCuttingDimensionManager reportSynthesisCrossCuttingDimensionManager,
    CCDimension2018Validator validator, CrpProgramManager crpProgramManager,
    DeliverableParticipantManager deliverableParticipantManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.reportSynthesisCrossCuttingDimensionManager = reportSynthesisCrossCuttingDimensionManager;
    this.validator = validator;
    this.crpProgramManager = crpProgramManager;
    this.deliverableParticipantManager = deliverableParticipantManager;
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


  public List<DeliverableParticipant> getDeliverableParticipants() {
    return deliverableParticipants;
  }

  public List<ReportSynthesisCrossCuttingDimension> getFlagshipCCDimensions() {
    return flagshipCCDimensions;
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


  public Long getSynthesisID() {
    return synthesisID;
  }


  public Double getTotalParticipantFormalTraining() {
    return totalParticipantFormalTraining;
  }


  public Double getTotalParticipantFormalTrainingLongFemale() {
    return totalParticipantFormalTrainingLongFemale;
  }


  public Double getTotalParticipantFormalTrainingLongMale() {
    return totalParticipantFormalTrainingLongMale;
  }

  public Double getTotalParticipantFormalTrainingShortFemale() {
    return totalParticipantFormalTrainingShortFemale;
  }

  public Double getTotalParticipantFormalTrainingShortMale() {
    return totalParticipantFormalTrainingShortMale;
  }


  public Double getTotalParticipants() {
    return totalParticipants;
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
        this.setDraft(true);
      } else {

        this.setDraft(false);
        // Check if relation is null -create it
        if (reportSynthesis.getReportSynthesisCrossCuttingDimension() == null) {
          ReportSynthesisCrossCuttingDimension cuttingDimension = new ReportSynthesisCrossCuttingDimension();
          // create one to one relation
          reportSynthesis.setReportSynthesisCrossCuttingDimension(cuttingDimension);
          cuttingDimension.setReportSynthesis(reportSynthesis);
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

    // Flagship Cross Cutting Dimensions Synthesis progress
    if (this.isPMU()) {
      flagshipCCDimensions =
        reportSynthesisCrossCuttingDimensionManager.getFlagshipCCDimensions(liaisonInstitutions, phase.getId());
    }

    // ADD PMU as liasion Institution too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym() != null && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));

    /** Graphs and Tables */
    // Deliverables Participants
    deliverableParticipants = deliverableParticipantManager.getDeliverableParticipantByPhase(phase);
    if (deliverableParticipants != null && !deliverableParticipants.isEmpty()) {
      for (DeliverableParticipant deliverableParticipant : deliverableParticipants) {
        if (deliverableParticipant.getDeliverable().getDeliverableInfo(phase) != null
          && deliverableParticipant.getDeliverable().getDeliverableInfo(phase).isRequired()) {
          // Total Participants
          Double numberParticipant = 0.0;
          if (deliverableParticipant.getParticipants() != null) {
            numberParticipant = deliverableParticipant.getParticipants();
          }

          totalParticipants += numberParticipant;

          // Total Formal Training
          if (deliverableParticipant.getRepIndTypeActivity() != null
            && deliverableParticipant.getRepIndTypeActivity().getIsFormal()) {
            totalParticipantFormalTraining += numberParticipant;

            // Total Female and Male per terms
            if (deliverableParticipant.getRepIndTrainingTerm() != null) {
              Double numberFemales = 0.0;
              if (deliverableParticipant.getFemales() != null) {
                numberFemales = deliverableParticipant.getFemales();
              }
              if (deliverableParticipant.getRepIndTrainingTerm().getId()
                .equals(APConstants.REP_IND_TRAINING_TERMS_SHORT)) {
                totalParticipantFormalTrainingShortFemale += numberFemales;
                totalParticipantFormalTrainingShortMale += (numberParticipant - numberFemales);
              }
              if (deliverableParticipant.getRepIndTrainingTerm().getId()
                .equals(APConstants.REP_IND_TRAINING_TERMS_LONG)) {
                totalParticipantFormalTrainingLongFemale += numberFemales;
                totalParticipantFormalTrainingLongMale += (numberParticipant - numberFemales);
              }
            }
          }
        }
      }
    }

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_CROSS_CUTTING_BASE_PERMISSION, params));
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      ReportSynthesisCrossCuttingDimension crossCuttingDimensionDB =
        reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisCrossCuttingDimension();

      crossCuttingDimensionDB.setGenderResearchFindings(
        reportSynthesis.getReportSynthesisCrossCuttingDimension().getGenderResearchFindings());
      crossCuttingDimensionDB
        .setGenderLearned(reportSynthesis.getReportSynthesisCrossCuttingDimension().getGenderLearned());
      crossCuttingDimensionDB
        .setGenderProblemsArisen(reportSynthesis.getReportSynthesisCrossCuttingDimension().getGenderProblemsArisen());
      crossCuttingDimensionDB
        .setYouthContribution(reportSynthesis.getReportSynthesisCrossCuttingDimension().getYouthContribution());
      crossCuttingDimensionDB
        .setYouthResearchFindings(reportSynthesis.getReportSynthesisCrossCuttingDimension().getYouthResearchFindings());
      crossCuttingDimensionDB
        .setYouthLearned(reportSynthesis.getReportSynthesisCrossCuttingDimension().getYouthLearned());
      crossCuttingDimensionDB
        .setYouthProblemsArisen(reportSynthesis.getReportSynthesisCrossCuttingDimension().getYouthProblemsArisen());
      crossCuttingDimensionDB
        .setCapDevKeyAchievements(reportSynthesis.getReportSynthesisCrossCuttingDimension().getCapDevKeyAchievements());
      crossCuttingDimensionDB.setClimateChangeKeyAchievements(
        reportSynthesis.getReportSynthesisCrossCuttingDimension().getClimateChangeKeyAchievements());

      // Save Number of Trainees
      crossCuttingDimensionDB.setTraineesLongTermFemale(
        reportSynthesis.getReportSynthesisCrossCuttingDimension().getTraineesLongTermFemale());
      crossCuttingDimensionDB
        .setTraineesLongTermMale(reportSynthesis.getReportSynthesisCrossCuttingDimension().getTraineesLongTermMale());
      crossCuttingDimensionDB.setTraineesShortTermFemale(
        reportSynthesis.getReportSynthesisCrossCuttingDimension().getTraineesShortTermFemale());
      crossCuttingDimensionDB
        .setTraineesShortTermMale(reportSynthesis.getReportSynthesisCrossCuttingDimension().getTraineesShortTermMale());

      crossCuttingDimensionDB =
        reportSynthesisCrossCuttingDimensionManager.saveReportSynthesisCrossCuttingDimension(crossCuttingDimensionDB);

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

  public void setDeliverableParticipants(List<DeliverableParticipant> deliverableParticipants) {
    this.deliverableParticipants = deliverableParticipants;
  }

  public void setFlagshipCCDimensions(List<ReportSynthesisCrossCuttingDimension> flagshipCCDimensions) {
    this.flagshipCCDimensions = flagshipCCDimensions;
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

  public void setSynthesisID(Long synthesisID) {
    this.synthesisID = synthesisID;
  }

  public void setTotalParticipantFormalTraining(Double totalParticipantFormalTraining) {
    this.totalParticipantFormalTraining = totalParticipantFormalTraining;
  }

  public void setTotalParticipantFormalTrainingLongFemale(Double totalParticipantFormalTrainingLongFemale) {
    this.totalParticipantFormalTrainingLongFemale = totalParticipantFormalTrainingLongFemale;
  }

  public void setTotalParticipantFormalTrainingLongMale(Double totalParticipantFormalTrainingLongMale) {
    this.totalParticipantFormalTrainingLongMale = totalParticipantFormalTrainingLongMale;
  }

  public void setTotalParticipantFormalTrainingShortFemale(Double totalParticipantFormalTrainingShortFemale) {
    this.totalParticipantFormalTrainingShortFemale = totalParticipantFormalTrainingShortFemale;
  }

  public void setTotalParticipantFormalTrainingShortMale(Double totalParticipantFormalTrainingShortMale) {
    this.totalParticipantFormalTrainingShortMale = totalParticipantFormalTrainingShortMale;
  }

  public void setTotalParticipants(Double totalParticipants) {
    this.totalParticipants = totalParticipants;
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