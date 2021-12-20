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
import org.cgiar.ccafs.marlo.data.manager.PowbExpenditureAreasManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrpFinancialReportManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFinancialSummaryBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFinancialSummaryManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbExpenditureAreas;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpFinancialReport;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummary;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummaryBudget;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.y2018.FinancialSummary2018Validator;

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
 * @author Andrés Valencia - CIAT/CCAFS
 */
public class FinancialSummaryAction extends BaseAction {


  private static final long serialVersionUID = -8306463804965610803L;


  // Managers
  private GlobalUnitManager crpManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private AuditLogManager auditLogManager;
  private UserManager userManager;
  private CrpProgramManager crpProgramManager;
  private FinancialSummary2018Validator validator;
  private ReportSynthesisFinancialSummaryManager reportSynthesisFinancialSummaryManager;
  private ReportSynthesisCrpFinancialReportManager reportSynthesisCrpFinancialReportManager;
  private ReportSynthesisFinancialSummaryBudgetManager reportSynthesisFinancialSummaryBudgetManager;
  private PowbExpenditureAreasManager powbExpenditureAreasManager;

  // Variables
  private String transaction;
  private ReportSynthesis reportSynthesis;
  private Long liaisonInstitutionID;
  private Long synthesisID;
  private LiaisonInstitution liaisonInstitution;
  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private String pmuText;


  @Inject
  public FinancialSummaryAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, CrpProgramManager crpProgramManager,
    FinancialSummary2018Validator validator,
    ReportSynthesisCrpFinancialReportManager reportSynthesisCrpFinancialReportManager,
    ReportSynthesisFinancialSummaryManager reportSynthesisFinancialSummaryManager,
    ReportSynthesisFinancialSummaryBudgetManager reportSynthesisFinancialSummaryBudgetManager,
    PowbExpenditureAreasManager powbExpenditureAreasManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.validator = validator;
    this.reportSynthesisFinancialSummaryManager = reportSynthesisFinancialSummaryManager;
    this.reportSynthesisFinancialSummaryBudgetManager = reportSynthesisFinancialSummaryBudgetManager;
    this.powbExpenditureAreasManager = powbExpenditureAreasManager;
    this.reportSynthesisCrpFinancialReportManager = reportSynthesisCrpFinancialReportManager;
  }


  @Override
  public String cancel() {
    Path path = this.getAutoSaveFilePath();
    if (path.toFile().exists()) {
      path.toFile().delete();
    }
    this.setDraft(false);
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      messages.iterator().next();
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

  public String getPmuText() {
    return pmuText;
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

        if (this.isSelectedPhaseAR2021()) {
          if (this.isPMU()) {
            // Check if relation is null -create it
            if (reportSynthesis.getReportSynthesisCrpFinancialReport() == null) {
              ReportSynthesisCrpFinancialReport reportSynthesisCrpFinancialReport =
                new ReportSynthesisCrpFinancialReport();
              // create one to one relation
              reportSynthesis.setReportSynthesisCrpFinancialReport(reportSynthesisCrpFinancialReport);;
              reportSynthesisCrpFinancialReport.setReportSynthesis(reportSynthesis);
              // save the changes
              reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
            }

            // nothing else for now
          }
        } else {
          // Check if relation is null -create it
          if (reportSynthesis.getReportSynthesisFinancialSummary() == null) {
            ReportSynthesisFinancialSummary financialSummary = new ReportSynthesisFinancialSummary();
            // create one to one relation
            reportSynthesis.setReportSynthesisFinancialSummary(financialSummary);;
            financialSummary.setReportSynthesis(reportSynthesis);
            // save the changes
            reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
          }
          reportSynthesis.getReportSynthesisFinancialSummary().setBudgets(new ArrayList<>());
          if (this.isPMU()) {
            // Flagships Financial Budgets
            if (reportSynthesis.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets() != null
              && !reportSynthesis.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets()
                .isEmpty()) {
              reportSynthesis.getReportSynthesisFinancialSummary()
                .setBudgets(new ArrayList<>(
                  reportSynthesis.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets()
                    .stream().filter(t -> t.isActive()).collect(Collectors.toList())));
            } else {

              List<LiaisonInstitution> flagshipList = loggedCrp.getLiaisonInstitutions().stream()
                .filter(c -> c.getCrpProgram() != null && c.isActive()
                  && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
                .collect(Collectors.toList());
              flagshipList.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
              reportSynthesis.getReportSynthesisFinancialSummary().setBudgets(new ArrayList<>());
              for (LiaisonInstitution liInstitution : flagshipList) {
                ReportSynthesisFinancialSummaryBudget financialSummaryBudget =
                  new ReportSynthesisFinancialSummaryBudget();
                financialSummaryBudget.setLiaisonInstitution(liInstitution);
                reportSynthesis.getReportSynthesisFinancialSummary().getBudgets().add(financialSummaryBudget);
              }

              List<PowbExpenditureAreas> expAreas = new ArrayList<>(powbExpenditureAreasManager.findAll().stream()
                .filter(x -> x.isActive() && !x.getIsExpenditure()).collect(Collectors.toList()));
              for (PowbExpenditureAreas powbExpenditureAreas : expAreas) {
                ReportSynthesisFinancialSummaryBudget financialSummaryBudget =
                  new ReportSynthesisFinancialSummaryBudget();
                financialSummaryBudget.setExpenditureArea(powbExpenditureAreas);
                reportSynthesis.getReportSynthesisFinancialSummary().getBudgets().add(financialSummaryBudget);
              }


            }
          }
        }
      }
    }


    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

    // ADD PMU as liasion Institution too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym() != null && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));

    // Informative table to Flagships
    if (!this.isSelectedPhaseAR2021() && this.isFlagship()) {

      LiaisonInstitution pmuInstitution = loggedCrp.getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() == null && c.getAcronym() != null && c.getAcronym().equals("PMU"))
        .collect(Collectors.toList()).get(0);
      ReportSynthesis reportSynthesisDB = reportSynthesisManager.findSynthesis(phase.getId(), pmuInstitution.getId());


      if (reportSynthesisDB != null) {
        if (reportSynthesisDB.getReportSynthesisFinancialSummary() != null) {
          pmuText = reportSynthesisDB.getReportSynthesisFinancialSummary().getNarrative();
          if (reportSynthesisDB.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets() != null
            && !reportSynthesisDB.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets()
              .isEmpty()) {
            reportSynthesis.getReportSynthesisFinancialSummary()
              .setBudgets(new ArrayList<>(
                reportSynthesisDB.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets()
                  .stream().filter(t -> t.isActive()).collect(Collectors.toList())));
          }
        }
      }
    }

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_CRP_PROGRESS_BASE_PERMISSION, params));

    if (!this.isSelectedPhaseAR2021() && this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisFinancialSummary().getBudgets() != null) {
        reportSynthesis.getReportSynthesisFinancialSummary().getBudgets().clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      if (this.isSelectedPhaseAR2021()) {
        ReportSynthesisCrpFinancialReport reportSynthesisCrpFinancialReportDB =
          this.reportSynthesisCrpFinancialReportManager.findByReportSynthesis(synthesisID);

        if (this.isPMU()) {
          this.saveCrpFinancialReport(reportSynthesisCrpFinancialReportDB);
        }
      } else {
        ReportSynthesisFinancialSummary financialSummaryDB =
          reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisFinancialSummary();

        if (this.isPMU()) {
          this.saveFinancialSummaryBudgets(financialSummaryDB);
          financialSummaryDB.setNarrative(reportSynthesis.getReportSynthesisFinancialSummary().getNarrative());

          financialSummaryDB =
            reportSynthesisFinancialSummaryManager.saveReportSynthesisFinancialSummary(financialSummaryDB);
        }
      }

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


  private void saveCrpFinancialReport(ReportSynthesisCrpFinancialReport reportSynthesisCrpFinancialReportDB) {
    ReportSynthesisCrpFinancialReport crpFinancialReport = reportSynthesis.getReportSynthesisCrpFinancialReport();
    if (crpFinancialReport != null) {
      reportSynthesisCrpFinancialReportDB.setFinancialStatusNarrative(crpFinancialReport.getFinancialStatusNarrative());

      reportSynthesisCrpFinancialReportDB
        .setCapitalEquipment2020Forecast(crpFinancialReport.getCapitalEquipment2020Forecast());
      reportSynthesisCrpFinancialReportDB
        .setCapitalEquipment2021Budget(crpFinancialReport.getCapitalEquipment2021Budget());
      reportSynthesisCrpFinancialReportDB.setCapitalEquipmentComments(crpFinancialReport.getCapitalEquipmentComments());
      reportSynthesisCrpFinancialReportDB.setCloseout2020Forecast(crpFinancialReport.getCloseout2020Forecast());
      reportSynthesisCrpFinancialReportDB.setCloseout2021Budget(crpFinancialReport.getCloseout2021Budget());
      reportSynthesisCrpFinancialReportDB.setCloseoutComments(crpFinancialReport.getCloseoutComments());
      reportSynthesisCrpFinancialReportDB
        .setCollaboratorPartnerships2020Forecast(crpFinancialReport.getCollaboratorPartnerships2020Forecast());
      reportSynthesisCrpFinancialReportDB
        .setCollaboratorPartnerships2021Budget(crpFinancialReport.getCollaboratorPartnerships2021Budget());
      reportSynthesisCrpFinancialReportDB
        .setCollaboratorPartnershipsComments(crpFinancialReport.getCollaboratorPartnershipsComments());
      reportSynthesisCrpFinancialReportDB.setConsultancy2020Forecast(crpFinancialReport.getConsultancy2020Forecast());
      reportSynthesisCrpFinancialReportDB.setConsultancy2021Budget(crpFinancialReport.getConsultancy2021Budget());
      reportSynthesisCrpFinancialReportDB.setConsultancyComments(crpFinancialReport.getConsultancyComments());
      reportSynthesisCrpFinancialReportDB.setOperation2020Forecast(crpFinancialReport.getOperation2020Forecast());
      reportSynthesisCrpFinancialReportDB.setOperation2021Budget(crpFinancialReport.getOperation2021Budget());
      reportSynthesisCrpFinancialReportDB.setOperationComments(crpFinancialReport.getOperationComments());
      reportSynthesisCrpFinancialReportDB.setPersonnel2020Forecast(crpFinancialReport.getPersonnel2020Forecast());
      reportSynthesisCrpFinancialReportDB.setPersonnel2021Budget(crpFinancialReport.getPersonnel2020Forecast());
      reportSynthesisCrpFinancialReportDB.setPersonnelComments(crpFinancialReport.getPersonnelComments());
      reportSynthesisCrpFinancialReportDB.setTravel2020Forecast(crpFinancialReport.getTravel2020Forecast());
      reportSynthesisCrpFinancialReportDB.setTravel2021Budget(crpFinancialReport.getTravel2021Budget());
      reportSynthesisCrpFinancialReportDB.setTravelComments(crpFinancialReport.getTravelComments());

      // Total
      reportSynthesisCrpFinancialReportDB.setCrpTotal2020Forecast(crpFinancialReport.getCrpTotal2020Forecast());
      reportSynthesisCrpFinancialReportDB.setCrpTotal2021Budget(crpFinancialReport.getCrpTotal2021Budget());
      reportSynthesisCrpFinancialReportDB.setCrpTotalComments(crpFinancialReport.getCrpTotalComments());

      reportSynthesisCrpFinancialReportDB = this.reportSynthesisCrpFinancialReportManager
        .saveReportSynthesisCrpFinancialReport(reportSynthesisCrpFinancialReportDB);
    }
  }


  /**
   * Save Financial Summary budget by Flagship Information
   * 
   * @param financialSummaryBudgetDB
   */
  public void saveFinancialSummaryBudgets(ReportSynthesisFinancialSummary financialSummaryDB) {

    // Save form Information
    if (reportSynthesis.getReportSynthesisFinancialSummary().getBudgets() != null) {
      for (ReportSynthesisFinancialSummaryBudget budget : reportSynthesis.getReportSynthesisFinancialSummary()
        .getBudgets()) {
        if (budget.getId() == null) {

          ReportSynthesisFinancialSummaryBudget budgetSave = new ReportSynthesisFinancialSummaryBudget();

          budgetSave.setReportSynthesisFinancialSummary(financialSummaryDB);

          if (budget.getLiaisonInstitution() != null && budget.getLiaisonInstitution().getId() != null) {
            LiaisonInstitution liaisonInstitution =
              liaisonInstitutionManager.getLiaisonInstitutionById(budget.getLiaisonInstitution().getId());

            budgetSave.setLiaisonInstitution(liaisonInstitution);
          } else {
            PowbExpenditureAreas expenditureAreas =
              powbExpenditureAreasManager.getPowbExpenditureAreasById(budget.getExpenditureArea().getId());

            budgetSave.setExpenditureArea(expenditureAreas);
          }

          budgetSave.setW1Planned(budget.getW1Planned());
          budgetSave.setW3Planned(budget.getW3Planned());
          budgetSave.setBilateralPlanned(budget.getBilateralPlanned());

          budgetSave.setW1Actual(budget.getW1Actual());
          budgetSave.setW3Actual(budget.getW3Actual());
          budgetSave.setBilateralActual(budget.getBilateralActual());
          budgetSave.setComments(budget.getComments());

          reportSynthesisFinancialSummaryBudgetManager.saveReportSynthesisFinancialSummaryBudget(budgetSave);

        } else {


          boolean hasChanges = false;
          ReportSynthesisFinancialSummaryBudget budgetPrev =
            reportSynthesisFinancialSummaryBudgetManager.getReportSynthesisFinancialSummaryBudgetById(budget.getId());

          if (budgetPrev.getW1Planned() != budget.getW1Planned()) {
            hasChanges = true;
            budgetPrev.setW1Planned(budget.getW1Planned());
          }

          if (budgetPrev.getW3Planned() != budget.getW3Planned()) {
            hasChanges = true;
            budgetPrev.setW3Planned(budget.getW3Planned());
          }

          if (budgetPrev.getBilateralPlanned() != budget.getBilateralPlanned()) {
            hasChanges = true;
            budgetPrev.setBilateralPlanned(budget.getBilateralPlanned());
          }

          if (budgetPrev.getW1Actual() != budget.getW1Actual()) {
            hasChanges = true;
            budgetPrev.setW1Actual(budget.getW1Actual());
          }

          if (budgetPrev.getW3Actual() != budget.getW3Actual()) {
            hasChanges = true;
            budgetPrev.setW3Actual(budget.getW3Actual());
          }

          if (budgetPrev.getBilateralActual() != budget.getBilateralActual()) {
            hasChanges = true;
            budgetPrev.setBilateralActual(budget.getBilateralActual());
          }

          if (budgetPrev.getComments() != budget.getComments()) {
            hasChanges = true;
            budgetPrev.setComments(budget.getComments());
          }


          if (hasChanges) {
            reportSynthesisFinancialSummaryBudgetManager.saveReportSynthesisFinancialSummaryBudget(budgetPrev);
          }


        }
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


  public void setPmuText(String pmuText) {
    this.pmuText = pmuText;
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
