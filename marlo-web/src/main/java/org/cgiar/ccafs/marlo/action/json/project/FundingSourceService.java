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
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;


public class FundingSourceService extends BaseAction {


  private static final long serialVersionUID = 6304226585314276677L;


  List<Map<String, Object>> sources;

  private String financeCode;
  private String leadCenter;


  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  private FundingSourceManager fundingSourceManager;

  @Inject
  public FundingSourceService(APConfig config, FundingSourceManager fundingSourceManager, GlobalUnitManager crpManager,
    PhaseManager phaseManager) {
    super(config);
    this.fundingSourceManager = fundingSourceManager;
    this.crpManager = crpManager;
  }


  @Override
  public String execute() throws Exception {
    sources = new ArrayList<>();

    GlobalUnit loggedCrp = crpManager.getGlobalUnitById(this.getCrpID());

    /**
     * Read only summary objects (not hibernate entities)
     */
    List<FundingSourceSearchSummary> summaries = new ArrayList<>();
    summaries = null;
    if (financeCode.isEmpty()) {
      financeCode = "0";
    }

    if (financeCode != null && leadCenter != null) {
      summaries = fundingSourceManager
        .searchFundingSourcesInSpecificCRPByfinancecode("", 0, this.getCrpID().longValue(),
          this.getActualPhase().getId(), financeCode, Long.parseLong(leadCenter))
        .stream().filter(f -> f.getFinanceCode() != null && f.getFinanceCode().equals(financeCode))
        .collect(Collectors.toList());

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
        summaries = new ArrayList<>();
        if (summariesTemp != null && !summariesTemp.isEmpty()) {
          summaries.addAll(summariesTemp);
        }
      }

    }
    if (summaries != null) {
      for (FundingSourceSearchSummary summary : summaries) {

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
    financeCode = StringUtils.trim(parameters.get(APConstants.FINANCE_CODE).getMultipleValues()[0]);
    leadCenter = StringUtils.trim(parameters.get(APConstants.INSTITUTION_REQUEST_ID).getMultipleValues()[0]);
  }


  public void setSources(List<Map<String, Object>> sources) {
    this.sources = sources;
  }

}
