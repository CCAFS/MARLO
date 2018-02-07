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
import org.cgiar.ccafs.marlo.data.manager.FundingSourceBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;
import org.cgiar.ccafs.marlo.data.model.FundingStatusEnum;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
  private ProjectBudgetManager projectBudgetManager;
  private FundingSourceBudgetManager fundingSourceBudgetManager;
  private Phase phase;


  @Inject
  public FundingSourceListAction(APConfig config, FundingSourceManager fundingSourceManager,
    InstitutionManager institutionManager, ProjectBudgetManager projectBudgetManager,
    FundingSourceBudgetManager fundingSourceBudgetManager, GlobalUnitManager crpManager) {
    super(config);
    this.fundingSourceManager = fundingSourceManager;
    this.institutionManager = institutionManager;
    this.projectBudgetManager = projectBudgetManager;
    this.crpManager = crpManager;
    this.fundingSourceBudgetManager = fundingSourceBudgetManager;
  }


  @Override
  public String execute() throws Exception {
    sources = new ArrayList<>();
    List<FundingSource> fundingSources;
    GlobalUnit loggedCrp = crpManager.getGlobalUnitById(this.getCrpID());
    Institution institution = institutionManager.getInstitutionById(institutionID);

    Map<String, Object> source;
    if (institution == null) {
      fundingSources =
        fundingSourceManager.searchFundingSources(queryParameter, year, this.getCrpID().longValue(), phase.getId());
    } else {
      fundingSources = fundingSourceManager.searchFundingSourcesByInstitution(queryParameter, institution.getId(), year,
        this.getCrpID(), phase.getId());


    }

    if (fundingSources != null) {
      fundingSources =
        fundingSources.stream().filter(c -> c.getFundingSourceInfo().getTitle() != null).collect(Collectors.toList());
      fundingSources
        .sort((p1, p2) -> p1.getFundingSourceInfo().getTitle().compareTo(p2.getFundingSourceInfo().getTitle()));
    }
    // add elements to al, including duplicates
    Set<FundingSource> hs = new HashSet<>();
    hs.addAll(fundingSources);
    fundingSources.clear();
    fundingSources.addAll(hs.stream()
      .filter(c -> c.getFundingSourceInfo(this.getActualPhase()).getStatus() != null
        && (c.getFundingSourceInfo(this.getActualPhase()).getStatus() == Integer
          .parseInt(FundingStatusEnum.Ongoing.getStatusId())
        || c.getFundingSourceInfo(this.getActualPhase()).getStatus() == Integer
          .parseInt(FundingStatusEnum.Pipeline.getStatusId())
        || c.getFundingSourceInfo(this.getActualPhase()).getStatus() == Integer
          .parseInt(FundingStatusEnum.Informally.getStatusId())

          || c.getFundingSourceInfo(this.getActualPhase()).getStatus() == Integer
            .parseInt(FundingStatusEnum.Extended.getStatusId()))


    ).collect(Collectors.toList()));
    fundingSources.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));

    for (FundingSource fundingSource : fundingSources) {
      if (fundingSource.isActive()) {
        source = new HashMap<>();
        source.put("id", fundingSource.getId());
        source.put("name", fundingSource.getFundingSourceInfo().getTitle());
        source.put("type", fundingSource.getFundingSourceInfo().getBudgetType().getName());
        source.put("typeId", fundingSource.getFundingSourceInfo().getBudgetType().getId());

        if ((fundingSource.getFundingSourceInfo().getW1w2() != null)
          && (this.hasSpecificities(APConstants.CRP_FS_W1W2_COFINANCING))) {
          source.put("w1w2", fundingSource.getFundingSourceInfo().getW1w2().booleanValue());
        }

        if (fundingSource.getFundingSourceInfo().getBudgetType().getId().intValue() == 1) {

          String permission =
            this.generatePermission(Permission.PROJECT_FUNDING_W1_BASE_PERMISSION, loggedCrp.getAcronym());

          boolean hasPermission = this.hasPermissionNoBase(permission);
          source.put("canSelect", hasPermission);
        } else {

          source.put("canSelect", true);

        }


        FundingSourceBudget fundingSourceBudget =
          fundingSourceBudgetManager.getByFundingSourceAndYear(fundingSource.getId(), year);
        double remainingAmount = 0;
        if (fundingSourceBudget != null && fundingSourceBudget.getBudget() != null) {
          remainingAmount = projectBudgetManager.getReaminingAmount(fundingSource.getId(), year,
            fundingSourceBudget.getBudget(), this.getActualPhase().getId());
        }

        source.put("amount", remainingAmount);

        sources.add(source);
      }

    }
    return SUCCESS;
  }


  public List<Map<String, Object>> getSources() {
    return sources;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();

    if (parameters.get(APConstants.INSTITUTION_REQUEST_ID).isDefined()) {
      institutionID =
        // Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.INSTITUTION_REQUEST_ID))[0]));
        Long.parseLong(StringUtils.trim(parameters.get(APConstants.INSTITUTION_REQUEST_ID).getMultipleValues()[0]));
    }
    // queryParameter = StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]);
    queryParameter = StringUtils.trim(parameters.get(APConstants.QUERY_PARAMETER).getMultipleValues()[0]);
    // year = Integer.parseInt(StringUtils.trim(((String[]) parameters.get(APConstants.YEAR_REQUEST))[0]));
    year = Integer.parseInt(StringUtils.trim(parameters.get(APConstants.YEAR_REQUEST).getMultipleValues()[0]));
    phase = this.getActualPhase();
  }


  public void setSources(List<Map<String, Object>> sources) {
    this.sources = sources;
  }

}
