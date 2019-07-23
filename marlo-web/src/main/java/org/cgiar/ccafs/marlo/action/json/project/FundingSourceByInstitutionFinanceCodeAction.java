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
import org.cgiar.ccafs.marlo.action.funding.dto.FundingSourceSearchSummary;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.jfree.util.Log;


public class FundingSourceByInstitutionFinanceCodeAction extends BaseAction {


  private static final long serialVersionUID = 6304226585314276677L;


  List<Map<String, Object>> sources;

  private long phaseID;
  private String financeCode;
  private String leadCenter;
  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  private String queryParameter;
  private FundingSourceManager fundingSourceManager;
  private PhaseManager phaseManager;


  @Inject
  public FundingSourceByInstitutionFinanceCodeAction(APConfig config, FundingSourceManager fundingSourceManager,
    GlobalUnitManager crpManager, PhaseManager phaseManager) {
    super(config);
    this.fundingSourceManager = fundingSourceManager;
    this.crpManager = crpManager;
    this.phaseManager = phaseManager;
  }


  @Override
  public String execute() throws Exception {
    sources = new ArrayList<>();
    GlobalUnit loggedCrp = crpManager.getGlobalUnitById(this.getCrpID());

    /**
     * Read only summary objects (not hibernate entities)
     */
    List<FundingSourceSearchSummary> summaries = null;
    if (financeCode.isEmpty()) {
      financeCode = "0";
    }

    if (financeCode != null && leadCenter != null) {
      summaries =
        fundingSourceManager.searchFundingSourcesByInstitutionAndFinanceCode(Long.parseLong(leadCenter), financeCode);
    }

    if (summaries != null) {
      List<FundingSourceSearchSummary> summariesTemp = null;
      List<BigInteger> ids = null;
      for (FundingSourceSearchSummary summary : summaries) {
        if (ids == null) {
          ids = new ArrayList<>();
          summariesTemp = new ArrayList<>();
          ids.add(summary.getId());
          summariesTemp.add(summary);
        } else {
          if (!ids.contains(summary.getId())) {
            ids.add(summary.getId());
            summariesTemp.add(summary);
          }
        }
      }

      if (summariesTemp != null) {
        summaries = new ArrayList<>();
        summaries.addAll(summariesTemp);
      }
    }
    if (summaries != null) {
      String crpName = null;
      for (FundingSourceSearchSummary summary : summaries) {
        if (summary.getId() != null) {
          Long crpID = fundingSourceManager
            .getFundingSourceById(Long.parseLong(String.valueOf(summary.getId().intValue()))).getCrp().getId();
          if (crpID != null) {
            if (crpManager.getGlobalUnitById(crpID).getAcronym() != null) {
              crpName = crpManager.getGlobalUnitById(crpID).getAcronym();
            } else {
              crpName = crpManager.getGlobalUnitById(crpID).getName();
            }

            if (crpName != null) {
              summary.setCrpName(crpName);
            }
          }
        }

        if (summary.getTypeId().intValue() == 1) {
          String permission =
            this.generatePermission(Permission.PROJECT_FUNDING_W1_BASE_PERMISSION, loggedCrp.getAcronym());

          boolean hasPermission = this.hasPermissionNoBase(permission);
          summary.setCanSelect(hasPermission);
        }
        sources.add(summary.convertToMap());
      }
    }
    return SUCCESS;
  }


  public List<Map<String, Object>> getSources() {
    return sources;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    try {
      financeCode = StringUtils.trim(parameters.get(APConstants.FINANCE_CODE).getMultipleValues()[0]);
      leadCenter = StringUtils.trim(parameters.get(APConstants.INSTITUTION_LEAD).getMultipleValues()[0]);
    } catch (Exception e) {
      Log.info(e);
    }
  }

  public void setSources(List<Map<String, Object>> sources) {
    this.sources = sources;
  }

}
