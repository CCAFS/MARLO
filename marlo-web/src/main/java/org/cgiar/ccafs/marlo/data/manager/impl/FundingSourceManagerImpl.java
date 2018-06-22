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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.action.funding.dto.FundingSourceSearchSummary;
import org.cgiar.ccafs.marlo.action.funding.dto.FundingSourceSummary;
import org.cgiar.ccafs.marlo.data.dao.FundingSourceBudgetDAO;
import org.cgiar.ccafs.marlo.data.dao.FundingSourceDAO;
import org.cgiar.ccafs.marlo.data.dao.FundingSourceLocationsDAO;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.mapper.FundingSourceSummaryMapper;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingStatusEnum;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class FundingSourceManagerImpl implements FundingSourceManager {

  private final FundingSourceDAO fundingSourceDAO;

  private final FundingSourceBudgetDAO fundingSourceBudgetDAO;

  private final FundingSourceLocationsDAO fundingSourceLocationsDAO;

  private final FundingSourceSummaryMapper fundingSourceSummaryMapper;
  // Managers


  @Inject
  public FundingSourceManagerImpl(FundingSourceDAO fundingSourceDAO, FundingSourceBudgetDAO fundingSourceBudgetDAO,
    FundingSourceLocationsDAO fundingSourceLocationsDAO, FundingSourceSummaryMapper fundingSourceSummaryMapper) {
    this.fundingSourceDAO = fundingSourceDAO;
    this.fundingSourceBudgetDAO = fundingSourceBudgetDAO;
    this.fundingSourceLocationsDAO = fundingSourceLocationsDAO;
    this.fundingSourceSummaryMapper = fundingSourceSummaryMapper;
  }

  @Override
  public void deleteFundingSource(long fundingSourceId) {

    fundingSourceDAO.deleteFundingSource(fundingSourceId);

    /**
     * Because we are not using cascade delete and we need to set the inactive flag on the
     * entities, we need to do this for all child entities.
     * At this point in time I am not going to delete many to many relationships e.g. DeliverableFundingSource
     * and instead look to prevent FundingSources being deleted when there are still deliverables linked
     * to the funding source.
     * Note there is a more elegant way of doing soft deletes, see this article for instructions:
     * https://vladmihalcea.com/the-best-way-to-soft-delete-with-hibernate/
     */
    fundingSourceBudgetDAO.deleteAllFundingSourceBudgetForFundingSource(fundingSourceId);

    fundingSourceLocationsDAO.deleteAllFundingSourceLocationsForFundingSource(fundingSourceId);
  }

  @Override
  public boolean existFundingSource(long fundingSourceID) {

    return fundingSourceDAO.existFundingSource(fundingSourceID);
  }

  @Override
  public List<FundingSource> findAll() {

    return fundingSourceDAO.findAll();

  }

  @Override
  public List<FundingSourceSummary> getClosedFundingSourceSummaries(GlobalUnit globalUnit, Phase phase) {

    Set<Integer> statusTypes = new HashSet<>();
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Complete.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Cancelled.getStatusId()));

    List<FundingSource> fundingSources = fundingSourceDAO.getFundingSourceSummaries(globalUnit, phase, statusTypes);

    List<FundingSourceSummary> fundingSourceSummaries =
      fundingSourceSummaryMapper.fundingSourcesToFundingSourceSummaries(fundingSources);

    return fundingSourceSummaries;
  }

  @Override
  public List<FundingSource> getFundingSource(long userId, String crp) {
    List<FundingSource> projects = new ArrayList<>();

    List<Map<String, Object>> view = fundingSourceDAO.getFundingSource(userId, crp);

    if (view != null) {
      for (Map<String, Object> map : view) {
        FundingSource fs = this.getFundingSourceById((Long.parseLong(map.get("project_id").toString())));
        if (fs != null) {
          projects.add(fs);
        }

      }
    }
    return projects;
  }

  @Override
  public FundingSource getFundingSourceById(long fundingSourceID) {

    return fundingSourceDAO.find(fundingSourceID);
  }

  @Override
  public List<FundingSourceSummary> getOngoingFundingSourceSummaries(GlobalUnit globalUnit, Phase phase) {

    Set<Integer> statusTypes = new HashSet<>();
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Ongoing.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Extended.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Pipeline.getStatusId()));
    statusTypes.add(Integer.parseInt(FundingStatusEnum.Informally.getStatusId()));

    List<FundingSource> fundingSources = fundingSourceDAO.getFundingSourceSummaries(globalUnit, phase, statusTypes);

    List<FundingSourceSummary> fundingSourceSummaries =
      fundingSourceSummaryMapper.fundingSourcesToFundingSourceSummaries(fundingSources);

    return fundingSourceSummaries;

  }

  @Override
  public FundingSource saveFundingSource(FundingSource fundingSource) {

    return fundingSourceDAO.save(fundingSource);
  }

  @Override
  public FundingSource saveFundingSource(FundingSource fundingSource, String sectionName, List<String> relationsName,
    Phase phase) {

    return fundingSourceDAO.save(fundingSource, sectionName, relationsName, phase);
  }

  @Override
  public List<FundingSourceSearchSummary> searchFundingSources(String query, int year, long crpID, long phaseID) {
    return fundingSourceDAO.searchFundingSources(query, year, crpID, phaseID);
  }

  @Override
  public List<FundingSource> searchFundingSourcesByFinanceCode(String ocsCode) {
    return fundingSourceDAO.searchFundingSourcesByFinanceCode(ocsCode);
  }

  @Override
  public List<FundingSourceSearchSummary> searchFundingSourcesByInstitution(String userInput, Long institutionID,
    int year, long crpID, long phaseID) {
    return fundingSourceDAO.searchFundingSourcesByInstitution(userInput, institutionID, year, crpID, phaseID);
  }

  @Override
  public List<FundingSource> searchFundingSourcesByLocElement(long projectId, long locElementId, int year, long crpID,
    long phaseID) {
    return fundingSourceDAO.searchFundingSourcesByLocElement(projectId, locElementId, year, crpID, phaseID);
  }

  @Override
  public List<FundingSource> searchFundingSourcesByLocElementType(long projectId, long locElementTypeId, int year,
    long crpID) {
    return fundingSourceDAO.searchFundingSourcesByLocElementType(projectId, locElementTypeId, year, crpID);
  }


}
