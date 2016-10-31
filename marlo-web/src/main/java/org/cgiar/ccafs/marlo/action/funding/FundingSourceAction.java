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


package org.cgiar.ccafs.marlo.action.funding;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.BudgetTypeManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.model.AgreementStatusEnum;
import org.cgiar.ccafs.marlo.data.model.BudgetType;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class FundingSourceAction extends BaseAction {

  private static final long serialVersionUID = -3919022306156272887L;


  private CrpManager crpManager;

  private FundingSourceManager fundingSourceManager;
  private FundingSourceBudgetManager fundingSourceBudgetManager;

  private InstitutionManager institutionManager;
  private BudgetTypeManager budgetTypeManager;


  private LiaisonInstitutionManager liaisonInstitutionManager;

  private AuditLogManager auditLogManager;


  private Crp loggedCrp;


  private long projectID;


  private FundingSource fundingSource;


  private Map<String, String> status;

  private Map<String, String> budgetTypes;


  private List<LiaisonInstitution> liaisonInstitutions;


  private List<Institution> institutions;

  private String transaction;


  @Inject
  public FundingSourceAction(APConfig config, CrpManager crpManager, FundingSourceManager fundingSourceManager,
    InstitutionManager institutionManager, LiaisonInstitutionManager liaisonInstitutionManager,
    AuditLogManager auditLogManager, FundingSourceBudgetManager fundingSourceBudgetManager,
    BudgetTypeManager budgetTypeManager) {
    super(config);
    this.crpManager = crpManager;
    this.fundingSourceManager = fundingSourceManager;
    this.budgetTypeManager = budgetTypeManager;
    this.institutionManager = institutionManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.auditLogManager = auditLogManager;
    this.fundingSourceBudgetManager = fundingSourceBudgetManager;
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

  private Path getAutoSaveFilePath() {
    String composedClassName = fundingSource.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = fundingSource.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public Map<String, String> getBudgetTypes() {
    return budgetTypes;
  }

  public FundingSource getFundingSource() {
    return fundingSource;
  }

  public List<Institution> getInstitutions() {
    return institutions;
  }

  public List<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  public long getProjectID() {
    return projectID;
  }

  public Map<String, String> getStatus() {
    return status;
  }

  public String getTransaction() {
    return transaction;
  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.FUNDING_SOURCE_REQUEST_ID)));


    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      FundingSource history = (FundingSource) auditLogManager.getHistory(transaction);

      if (history != null) {
        fundingSource = history;
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }

    } else {
      fundingSource = fundingSourceManager.getFundingSourceById(projectID);
    }


    if (fundingSource != null) {


      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);

        AutoSaveReader autoSaveReader = new AutoSaveReader();

        fundingSource = (FundingSource) autoSaveReader.readFromJson(jReader);
        FundingSource projectDb = fundingSourceManager.getFundingSourceById(fundingSource.getId());
        reader.close();

        this.setDraft(true);
      } else {
        this.setDraft(false);
      }

      status = new HashMap<>();
      List<AgreementStatusEnum> list = Arrays.asList(AgreementStatusEnum.values());
      for (AgreementStatusEnum agreementStatusEnum : list) {
        status.put(agreementStatusEnum.getStatusId(), agreementStatusEnum.getStatus());
      }

      institutions = institutionManager.findAll();

      liaisonInstitutions = new ArrayList<>();

      liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions());
      liaisonInstitutions.addAll(
        liaisonInstitutionManager.findAll().stream().filter(c -> c.getCrp() == null).collect(Collectors.toList()));


      fundingSource.setBudgets(new ArrayList<>(fundingSourceManager.getFundingSourceById(fundingSource.getId())
        .getFundingSourceBudgets().stream().filter(pb -> pb.isActive()).collect(Collectors.toList())));

    }

    budgetTypes = new HashMap<>();

    for (BudgetType budgetType : budgetTypeManager.findAll()) {
      budgetTypes.put(budgetType.getId().toString(), budgetType.getName());
    }
    String params[] = {loggedCrp.getAcronym(), fundingSource.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_W3_COFUNDED_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (institutions != null) {
        institutions.clear();
      }

      if (liaisonInstitutions != null) {
        liaisonInstitutions.clear();
      }

      if (status != null) {
        status.clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      FundingSource fundingSourceDB = fundingSourceManager.getFundingSourceById(fundingSource.getId());
      fundingSourceDB.setActive(true);
      fundingSourceDB.setCreatedBy(fundingSourceDB.getCreatedBy());
      fundingSourceDB.setModifiedBy(this.getCurrentUser());
      fundingSourceDB.setModificationJustification("");
      fundingSourceDB.setActiveSince(fundingSourceDB.getActiveSince());


      Institution institution = institutionManager.getInstitutionById(fundingSource.getInstitution().getId());
      fundingSourceDB.setInstitution(institution);

      fundingSourceDB.setStartDate(fundingSource.getStartDate());
      fundingSourceDB.setEndDate(fundingSource.getEndDate());

      fundingSourceDB.setFinanceCode(fundingSource.getFinanceCode());
      fundingSourceDB.setContactPersonEmail(fundingSource.getContactPersonEmail());
      fundingSourceDB.setContactPersonName(fundingSource.getContactPersonName());
      fundingSourceDB.setBudgets(fundingSource.getBudgets());
      fundingSourceDB.setBudgetType(fundingSource.getBudgetType());
      fundingSourceDB.setCenterType(fundingSource.getCenterType());
      fundingSourceDB.setDescription(fundingSource.getDescription());
      if (fundingSource.getBudgets() != null) {
        for (FundingSourceBudget fundingSourceBudget : fundingSource.getBudgets()) {
          fundingSourceBudgetManager.saveFundingSourceBudget(fundingSourceBudget);
        }
      }

      List<String> relationsName = new ArrayList<>();
      fundingSourceManager.saveFundingSource(fundingSourceDB, this.getActionName(), relationsName);

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists()) {
        path.toFile().delete();
      }


      Collection<String> messages = this.getActionMessages();
      if (!messages.isEmpty()) {
        String validationMessage = messages.iterator().next();
        this.setActionMessages(null);
        this.addActionWarning(this.getText("saving.saved") + validationMessage);
      } else {
        this.addActionMessage(this.getText("saving.saved"));
      }

      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setBudgetTypes(Map<String, String> budgetTypes) {
    this.budgetTypes = budgetTypes;
  }

  public void setFundingSource(FundingSource fundingSource) {
    this.fundingSource = fundingSource;
  }


  public void setInstitutions(List<Institution> institutions) {
    this.institutions = institutions;
  }

  public void setLiaisonInstitutions(List<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setStatus(Map<String, String> status) {
    this.status = status;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

}
