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
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class FundingSourceListAction extends BaseAction {


  private static final long serialVersionUID = 6304226585314276677L;


  List<Map<String, Object>> sources;

  private long institutionID;

  private int year;
  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  private String queryParameter;
  private FundingSourceManager fundingSourceManager;
  private InstitutionManager institutionManager;
  private Phase phase;


  @Inject
  public FundingSourceListAction(APConfig config, FundingSourceManager fundingSourceManager,
    InstitutionManager institutionManager, GlobalUnitManager crpManager) {
    super(config);
    this.fundingSourceManager = fundingSourceManager;
    this.institutionManager = institutionManager;
    this.crpManager = crpManager;
  }


  @Override
  public String execute() throws Exception {
    sources = new ArrayList<>();

    GlobalUnit loggedCrp = crpManager.getGlobalUnitById(this.getCrpID());
    Institution institution = institutionManager.getInstitutionById(institutionID);

    /**
     * Read only summary objects (not hibernate entities)
     */
    List<FundingSourceSearchSummary> summaries;

    if (institution == null) {
      summaries =
        fundingSourceManager.searchFundingSources(queryParameter, year, this.getCrpID().longValue(), phase.getId());
    } else {
      summaries = fundingSourceManager.searchFundingSourcesByInstitution(queryParameter, institution.getId(), year,
        this.getCrpID(), phase.getId());

    }

    for (FundingSourceSearchSummary summary : summaries) {

      if (summary.getTypeId().intValue() == 1) {

        String permission =
          this.generatePermission(Permission.PROJECT_FUNDING_W1_BASE_PERMISSION, loggedCrp.getAcronym());

        boolean hasPermission = this.hasPermissionNoBase(permission);
        summary.setCanSelect(hasPermission);
      }
      sources.add(summary.convertToMap());

    }
    return SUCCESS;
  }


  public List<Map<String, Object>> getSources() {
    return sources;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();

    if (parameters.get(APConstants.INSTITUTION_REQUEST_ID).isDefined()) {
      institutionID =
        Long.parseLong(StringUtils.trim(parameters.get(APConstants.INSTITUTION_REQUEST_ID).getMultipleValues()[0]));
    }
    queryParameter = StringUtils.trim(parameters.get(APConstants.QUERY_PARAMETER).getMultipleValues()[0]);
    year = Integer.parseInt(StringUtils.trim(parameters.get(APConstants.YEAR_REQUEST).getMultipleValues()[0]));
    phase = this.getActualPhase();
  }


  public void setSources(List<Map<String, Object>> sources) {
    this.sources = sources;
  }

}
