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

package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.BudgetTypeManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInfoManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.BudgetType;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInfo;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class FundingSourceAddAction extends BaseAction {


  private static final long serialVersionUID = -2726882911129987628L;


  private static String DESCRIPTION = "description";
  private static String TITLE = "title";
  private static String START_DATE = "startDate";

  private static String SELECTED_YEAR = "selectedYear";
  private static String END_DATE = "endDate";

  private static String FINANCE_CODE = "financeCode";

  private static String CONTACT_NAME = "contactName";
  private static String CONTACT_EMAIL = "contactEmail";
  private static String DONOR = "institution";
  private static String LEADER = "liaisonInstitution";

  private static String W1W2 = "w1w2";

  private static String TYPE = "budgetType";
  private static String BUDGETS = "budgets";
  private static String STATUS = "status";
  private static String FILE = "fileID";
  private Crp loggedCrp;
  private FundingSourceManager fundingSourceManager;
  private FundingSourceInfoManager fundingSourceInfoManager;
  private FundingSourceInstitutionManager fundingSourceInstitutionManager;
  private InstitutionManager institutionManager;
  private BudgetTypeManager budgetTypeManager;
  private FundingSourceBudgetManager fundingSourceBudgetManager;
  private CrpManager crpManager;
  private FileDBManager fileDBManager;
  private Map<String, Object> fsProp = new HashMap<>();


  @Inject
  public FundingSourceAddAction(APConfig config, FundingSourceManager fundingSourceManager,
    InstitutionManager institutionManager, BudgetTypeManager budgetTypeManager, CrpManager crpManager,
    FundingSourceBudgetManager fundingSourceBudgetManager, FileDBManager fileDBManager,
    FundingSourceInstitutionManager fundingSourceInstitutionManager,
    FundingSourceInfoManager fundingSourceInfoManager) {
    super(config);
    this.fundingSourceManager = fundingSourceManager;
    this.institutionManager = institutionManager;
    this.budgetTypeManager = budgetTypeManager;
    this.crpManager = crpManager;
    this.fileDBManager = fileDBManager;
    this.fundingSourceInstitutionManager = fundingSourceInstitutionManager;
    this.fundingSourceBudgetManager = fundingSourceBudgetManager;
    this.fundingSourceInfoManager = fundingSourceInfoManager;
  }


  @Override
  public String execute() throws Exception {

    // Map<String, Object> parameters = ActionContext.getContext().getParameters();

    Map<String, Parameter> parameters = ActionContext.getContext().getParameters();


    // String budgets = StringUtils.trim(((String[]) parameters.get(BUDGETS))[0]);

    String budgets = StringUtils.trim((parameters.get(BUDGETS).getMultipleValues())[0]);


    budgets = budgets.replace("\"", "");
    budgets = budgets.replace("[", "");
    budgets = budgets.replace("{", "");
    budgets = budgets.replace("]", "");
    budgets = budgets.replace("}", "");

    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    // int selectedYear = Integer.parseInt(((String[]) parameters.get(SELECTED_YEAR))[0]);

    int selectedYear = Integer.parseInt((parameters.get(SELECTED_YEAR).getMultipleValues())[0]);
    SimpleDateFormat dateFormat = new SimpleDateFormat(APConstants.DATE_FORMAT);

    FundingSource fundingSource = new FundingSource();

    FundingSourceInfo fundingSourceInfo = new FundingSourceInfo();

    fundingSource.setCrp(loggedCrp);

    fundingSourceInfo
      .setStartDate(dateFormat.parse(StringUtils.trim((parameters.get(START_DATE)).getMultipleValues()[0])));
    fundingSourceInfo.setEndDate(dateFormat.parse(StringUtils.trim((parameters.get(END_DATE)).getMultipleValues()[0])));
    fundingSourceInfo.setTitle(StringUtils.trim((parameters.get(TITLE)).getMultipleValues()[0]));
    fundingSourceInfo.setDescription(StringUtils.trim((parameters.get(DESCRIPTION)).getMultipleValues()[0]));
    fundingSourceInfo.setFinanceCode(StringUtils.trim((parameters.get(FINANCE_CODE)).getMultipleValues()[0]));
    fundingSourceInfo.setContactPersonEmail(StringUtils.trim((parameters.get(CONTACT_EMAIL)).getMultipleValues()[0]));
    fundingSourceInfo.setContactPersonName(StringUtils.trim((parameters.get(CONTACT_NAME)).getMultipleValues()[0]));
    fundingSourceInfo.setStatus(Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()));

    try {
      fundingSourceInfo.setW1w2(Boolean.parseBoolean(StringUtils.trim((parameters.get(W1W2)).getMultipleValues()[0])));
    } catch (Exception e2) {
      fundingSourceInfo.setW1w2(null);
    }


    fundingSourceInfo.setStatus(Integer.parseInt(StringUtils.trim((parameters.get(STATUS)).getMultipleValues()[0])));
    try {
      fundingSourceInfo.setFile(
        fileDBManager.getFileDBById(Long.parseLong(StringUtils.trim((parameters.get(FILE)).getMultipleValues()[0]))));
    } catch (Exception e1) {
      fundingSourceInfo.setFile(null);
    }

    Institution institutionDonor =
      // institutionManager.getInstitutionById(Long.parseLong(StringUtils.trim(((String[]) parameters.get(DONOR))[0])));
      institutionManager
        .getInstitutionById(Long.parseLong(StringUtils.trim(parameters.get(DONOR).getMultipleValues()[0])));
    fundingSourceInfo.setDirectDonor(institutionDonor);

    BudgetType budgetType =
      // budgetTypeManager.getBudgetTypeById(Long.parseLong(StringUtils.trim(((String[]) parameters.get(TYPE))[0])));
      budgetTypeManager
        .getBudgetTypeById(Long.parseLong(StringUtils.trim(parameters.get(TYPE).getMultipleValues()[0])));
    fundingSourceInfo.setBudgetType(budgetType);


    fundingSource.setActive(true);
    fundingSource.setActiveSince(new Date());
    fundingSource.setCreatedBy(this.getCurrentUser());
    fundingSourceInfo.setModificationJustification("");
    fundingSource.setModifiedBy(this.getCurrentUser());
    fundingSourceInfo.setModifiedBy(this.getCurrentUser());
    fundingSourceInfo.setPhase(this.getActualPhase());


    fundingSource = fundingSourceManager.saveFundingSource(fundingSource);

    fundingSourceInfo.setFundingSource(fundingSourceManager.getFundingSourceById(fundingSource.getId()));
    long fundingSourceInfoID = fundingSourceInfoManager.saveFundingSourceInfo(fundingSourceInfo).getId();

    /*
     * LiaisonUser user = liaisonUserManager.getLiaisonUserByUserId(this.getCurrentUser().getId(), loggedCrp.getId());
     * if (user != null) {
     * LiaisonInstitution liaisonInstitution = user.getLiaisonInstitution();
     * try {
     * if (liaisonInstitution != null && liaisonInstitution.getInstitution() != null) {
     * Institution institution = institutionManager.getInstitutionById(liaisonInstitution.getInstitution().getId());
     * FundingSourceInstitution fundingSourceInstitution = new FundingSourceInstitution();
     * fundingSourceInstitution.setFundingSource(fundingSource);
     * fundingSourceInstitution.setInstitution(institution);
     * fundingSourceInstitutionManager.saveFundingSourceInstitution(fundingSourceInstitution);
     * }
     * } catch (Exception e) {
     * }
     * }
     */


    Institution institution =
      // institutionManager.getInstitutionById(Long.parseLong(StringUtils.trim(((String[])
      // parameters.get(LEADER))[0])));
      institutionManager
        .getInstitutionById(Long.parseLong(StringUtils.trim(parameters.get(LEADER).getMultipleValues()[0])));

    FundingSourceInstitution fundingSourceInstitution = new FundingSourceInstitution();
    fundingSourceInstitution.setFundingSource(fundingSource);

    fundingSourceInstitution.setInstitution(institution);
    fundingSourceInstitutionManager.saveFundingSourceInstitution(fundingSourceInstitution);

    double remaining = 0;
    boolean hasYear = false;
    boolean hasAmount = false;
    FundingSourceBudget fundingSourceBudget = null;
    for (String comaString : budgets.split(","))

    {

      String[] value = comaString.split(":");

      for (int i = 0; i < value.length; i++) {

        if (!hasYear && !hasAmount) {
          fundingSourceBudget = new FundingSourceBudget();
        }

        if (value[i].equals("year") && !hasYear) {
          fundingSourceBudget.setYear(Integer.parseInt(value[i + 1]));

          hasYear = true;
        }

        if (value[i].equals("budget") && !hasAmount) {
          fundingSourceBudget.setBudget(Double.parseDouble(value[i + 1]));

          hasAmount = true;
          if (hasYear && fundingSourceBudget.getYear().intValue() == selectedYear) {
            remaining = fundingSourceBudget.getBudget().doubleValue();
          }
        }

        if (hasYear && hasAmount) {

          fundingSourceBudget.setFundingSource(fundingSource);
          fundingSourceBudget.setActive(true);
          fundingSourceBudget.setActiveSince(new Date());
          fundingSourceBudget.setCreatedBy(this.getCurrentUser());
          fundingSourceBudget.setModificationJustification("");
          fundingSourceBudget.setModifiedBy(this.getCurrentUser());

          fundingSourceBudgetManager.saveFundingSourceBudget(fundingSourceBudget);

          hasYear = false;
          hasAmount = false;

        }
      }
      this.clearPermissionsCache();
    }


    if (fundingSource.getId() > 0)

    {
      fsProp.put("id", fundingSource.getId());
      fsProp.put("title", fundingSourceInfo.getDescription());
      fsProp.put("ammount", remaining);
      fsProp.put("type", budgetType.getName());
      fsProp.put("typeID", budgetType.getId());
      fsProp.put("status", "OK");
    } else

    {
      fsProp.put("status", "FAIL");
      fsProp.put("message", this.getText("manageUsers.email.notAdded"));
    }

    return SUCCESS;

  }


  public Map<String, Object> getFsProp() {
    return fsProp;
  }


  public void setFsProp(Map<String, Object> fsProp) {
    this.fsProp = fsProp;
  }

}
