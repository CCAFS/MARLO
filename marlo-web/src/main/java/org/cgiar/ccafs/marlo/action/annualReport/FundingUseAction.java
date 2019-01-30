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
import org.cgiar.ccafs.marlo.data.manager.PowbExpenditureAreasManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFundingUseExpendituryAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFundingUseSummaryManager;
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
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseExpendituryArea;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseSummary;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.annualreport.FundingUseValidator;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
public class FundingUseAction extends BaseAction {

  private static final long serialVersionUID = -8306463804965610803L;

  private static Logger LOG = LoggerFactory.getLogger(FundingUseAction.class);

  // Managers
  private GlobalUnitManager crpManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private AuditLogManager auditLogManager;
  private UserManager userManager;
  private CrpProgramManager crpProgramManager;
  private ReportSynthesisFundingUseSummaryManager reportSynthesisFundingUseSummaryManager;
  private ReportSynthesisFundingUseExpendituryAreaManager reportSynthesisFundingUseExpendituryAreaManager;
  private PowbExpenditureAreasManager powbExpenditureAreasManager;
  // Variables
  private String transaction;
  private FundingUseValidator validator;
  private ReportSynthesis reportSynthesis;
  private Long liaisonInstitutionID;
  private Long synthesisID;
  private LiaisonInstitution liaisonInstitution;
  private GlobalUnit loggedCrp;
  private List<LiaisonInstitution> liaisonInstitutions;
  private ReportSynthesis reportSynthesisPMU;
  private String pmuText;


  @Inject
  public FundingUseAction(APConfig config, GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    AuditLogManager auditLogManager, UserManager userManager, CrpProgramManager crpProgramManager,
    FundingUseValidator validator, ReportSynthesisFundingUseSummaryManager reportSynthesisFundingUseSummaryManager,
    ReportSynthesisFundingUseExpendituryAreaManager reportSynthesisFundingUseExpendituryAreaManager,
    PowbExpenditureAreasManager powbExpenditureAreasManager) {
    super(config);
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.auditLogManager = auditLogManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.validator = validator;
    this.reportSynthesisFundingUseSummaryManager = reportSynthesisFundingUseSummaryManager;
    this.reportSynthesisFundingUseExpendituryAreaManager = reportSynthesisFundingUseExpendituryAreaManager;
    this.powbExpenditureAreasManager = powbExpenditureAreasManager;
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

  public double getTotalFunding() {
    try {
      return reportSynthesisFundingUseExpendituryAreaManager
        .getTotalEstimatedOfW1W2ActualExpenditure(reportSynthesisPMU.getId());
    } catch (Exception e) {
      LOG.error("Error getting total funding by percentage");
      return 0;
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
      // reportSynthesisPMU: Used to calculate FLagships values
      LiaisonInstitution pmuInstitution = loggedCrp.getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() == null && c.getAcronym() != null && c.getAcronym().equals("PMU"))
        .collect(Collectors.toList()).get(0);
      reportSynthesisPMU = reportSynthesisManager.findSynthesis(phase.getId(), pmuInstitution.getId());

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
        if (reportSynthesis.getReportSynthesisFundingUseSummary() == null) {
          ReportSynthesisFundingUseSummary fundingUseSummary = new ReportSynthesisFundingUseSummary();
          // create one to one relation
          reportSynthesis.setReportSynthesisFundingUseSummary(fundingUseSummary);
          fundingUseSummary.setReportSynthesis(reportSynthesis);
          // save the changes
          reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }
        if (this.isPMU()) {

          // Flagships Funding Expenditure Areas
          if (reportSynthesis.getReportSynthesisFundingUseSummary()
            .getReportSynthesisFundingUseExpendituryAreas() != null
            && !reportSynthesis.getReportSynthesisFundingUseSummary().getReportSynthesisFundingUseExpendituryAreas()
              .isEmpty()) {
            reportSynthesis.getReportSynthesisFundingUseSummary()
              .setExpenditureAreas(new ArrayList<>(reportSynthesis.getReportSynthesisFundingUseSummary()
                .getReportSynthesisFundingUseExpendituryAreas().stream().filter(t -> t.isActive())
                .sorted((f1, f2) -> f1.getId().compareTo(f2.getId())).collect(Collectors.toList())));
          } else {
            reportSynthesis.getReportSynthesisFundingUseSummary().setExpenditureAreas(new ArrayList<>());
            List<PowbExpenditureAreas> expAreas = new ArrayList<>(
              powbExpenditureAreasManager.findAll().stream().filter(x -> x.isActive() && x.getIsExpenditure())
                .sorted((f1, f2) -> f1.getId().compareTo(f2.getId())).collect(Collectors.toList()));
            for (PowbExpenditureAreas powbExpenditureAreas : expAreas) {
              ReportSynthesisFundingUseExpendituryArea fundingUseExpenditureArea =
                new ReportSynthesisFundingUseExpendituryArea();
              fundingUseExpenditureArea.setExpenditureArea(powbExpenditureAreas);
              reportSynthesis.getReportSynthesisFundingUseSummary().getExpenditureAreas()
                .add(fundingUseExpenditureArea);
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
    if (this.isFlagship()) {
      if (reportSynthesisPMU != null && reportSynthesisPMU.getReportSynthesisFundingUseSummary() != null
        && reportSynthesisPMU.getReportSynthesisFundingUseSummary()
          .getReportSynthesisFundingUseExpendituryAreas() != null
        && !reportSynthesisPMU.getReportSynthesisFundingUseSummary().getReportSynthesisFundingUseExpendituryAreas()
          .isEmpty()) {
        pmuText = reportSynthesisPMU.getReportSynthesisFundingUseSummary().getMainArea();
        reportSynthesis.getReportSynthesisFundingUseSummary()
          .setExpenditureAreas(new ArrayList<>(reportSynthesisPMU.getReportSynthesisFundingUseSummary()
            .getReportSynthesisFundingUseExpendituryAreas().stream().filter(t -> t.isActive())
            .sorted((f1, f2) -> f1.getId().compareTo(f2.getId())).collect(Collectors.toList())));
      } else {
        reportSynthesis.getReportSynthesisFundingUseSummary().setExpenditureAreas(new ArrayList<>());
        List<PowbExpenditureAreas> expAreas = new ArrayList<>(
          powbExpenditureAreasManager.findAll().stream().filter(x -> x.isActive() && x.getIsExpenditure())
            .sorted((f1, f2) -> f1.getId().compareTo(f2.getId())).collect(Collectors.toList()));
        for (PowbExpenditureAreas powbExpenditureAreas : expAreas) {
          ReportSynthesisFundingUseExpendituryArea fundingUseExpenditureArea =
            new ReportSynthesisFundingUseExpendituryArea();
          fundingUseExpenditureArea.setExpenditureArea(powbExpenditureAreas);
          reportSynthesis.getReportSynthesisFundingUseSummary().getExpenditureAreas().add(fundingUseExpenditureArea);
        }
      }
    }


    // Base Permission
    String params[] = {loggedCrp.getAcronym(), reportSynthesis.getId() + ""};
    this.setBasePermission(this.getText(Permission.REPORT_SYNTHESIS_FUNDING_USE_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (reportSynthesis.getReportSynthesisFundingUseSummary().getExpenditureAreas() != null) {
        reportSynthesis.getReportSynthesisFundingUseSummary().getExpenditureAreas().clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      ReportSynthesisFundingUseSummary fundingUseSummaryDB =
        reportSynthesisManager.getReportSynthesisById(synthesisID).getReportSynthesisFundingUseSummary();

      if (this.isPMU()) {
        this.saveFundingUseExpenditureAreas(fundingUseSummaryDB);
        fundingUseSummaryDB.setMainArea(reportSynthesis.getReportSynthesisFundingUseSummary().getMainArea());

        fundingUseSummaryDB =
          reportSynthesisFundingUseSummaryManager.saveReportSynthesisFundingUseSummary(fundingUseSummaryDB);
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
   * Save Funding Use Expenditure Areas
   * 
   * @param fundingUseSummaryDB
   */
  public void saveFundingUseExpenditureAreas(ReportSynthesisFundingUseSummary fundingUseSummaryDB) {

    // Save form Information
    if (reportSynthesis.getReportSynthesisFundingUseSummary().getExpenditureAreas() != null) {
      for (ReportSynthesisFundingUseExpendituryArea fundingUseExpenditureArea : reportSynthesis
        .getReportSynthesisFundingUseSummary().getExpenditureAreas()) {
        if (fundingUseExpenditureArea.getId() == null) {

          ReportSynthesisFundingUseExpendituryArea fundingUseExpenditureAreaSave =
            new ReportSynthesisFundingUseExpendituryArea();

          fundingUseExpenditureAreaSave.setReportSynthesisFundingUseSummary(fundingUseSummaryDB);

          PowbExpenditureAreas expenditureAreas = powbExpenditureAreasManager
            .getPowbExpenditureAreasById(fundingUseExpenditureArea.getExpenditureArea().getId());

          fundingUseExpenditureAreaSave.setExpenditureArea(expenditureAreas);

          fundingUseExpenditureAreaSave.setW1w2Percentage(fundingUseExpenditureArea.getW1w2Percentage());
          fundingUseExpenditureAreaSave.setComments(fundingUseExpenditureArea.getComments());
          reportSynthesisFundingUseExpendituryAreaManager
            .saveReportSynthesisFundingUseExpendituryArea(fundingUseExpenditureAreaSave);

        } else {
          boolean hasChanges = false;
          ReportSynthesisFundingUseExpendituryArea fundingUseExpenditureAreaPrev =
            reportSynthesisFundingUseExpendituryAreaManager
              .getReportSynthesisFundingUseExpendituryAreaById(fundingUseExpenditureArea.getId());

          if (fundingUseExpenditureAreaPrev.getW1w2Percentage() != fundingUseExpenditureArea.getW1w2Percentage()) {
            hasChanges = true;
            fundingUseExpenditureAreaPrev.setW1w2Percentage(fundingUseExpenditureArea.getW1w2Percentage());
          }

          if (fundingUseExpenditureAreaPrev.getComments() != fundingUseExpenditureArea.getComments()) {
            hasChanges = true;
            fundingUseExpenditureAreaPrev.setComments(fundingUseExpenditureArea.getComments());
          }


          if (hasChanges) {
            reportSynthesisFundingUseExpendituryAreaManager
              .saveReportSynthesisFundingUseExpendituryArea(fundingUseExpenditureAreaPrev);
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
