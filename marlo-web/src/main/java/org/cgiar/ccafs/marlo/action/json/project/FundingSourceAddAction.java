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
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.text.SimpleDateFormat;
import java.util.Map;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class FundingSourceAddAction extends BaseAction {

  private static final long serialVersionUID = -2726882911129987628L;

  private static String DESCRIPTION = "description";
  private static String START_DATE = "startDate";
  private static String END_DATE = "endDate";
  private static String FINANCE_CODE = "financeCode";
  private static String CONTACT_NAME = "contactName";
  private static String CONTACT_EMAIL = "contactEmail";
  private static String DONOR = "institution";
  private static String CENTER_TYPE = "centerType";
  private static String TYPE = "type";
  private static String BUDGETS = "budgets";


  private String crpID;
  private FundingSourceManager fundingSourceManager;
  private InstitutionManager institutionManager;
  private BudgetTypeManager budgetTypeManager;


  @Inject
  public FundingSourceAddAction(APConfig config, FundingSourceManager fundingSourceManager,
    InstitutionManager institutionManager, BudgetTypeManager budgetTypeManager) {
    super(config);
    this.fundingSourceManager = fundingSourceManager;
    this.institutionManager = institutionManager;
    this.budgetTypeManager = budgetTypeManager;
  }

  @Override
  public String execute() throws Exception {

    Map<String, Object> parameters = ActionContext.getContext().getParameters();

    SimpleDateFormat dateFormat = new SimpleDateFormat(APConstants.DATE_FORMAT);

    FundingSource fundingSource = new FundingSource();

    String budgets = StringUtils.trim(((String[]) parameters.get(BUDGETS))[0]);


    return SUCCESS;
  }

}
