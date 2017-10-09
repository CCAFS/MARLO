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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.FundingSourceDAO;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInfo;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;


public class FundingSourceMySQLDAO implements FundingSourceDAO {

  private StandardDAO dao;

  @Inject
  public FundingSourceMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteFundingSource(long fundingSourceId) {
    FundingSource fundingSource = this.find(fundingSourceId);
    fundingSource.setActive(false);
    return this.save(fundingSource) > 0;
  }

  @Override
  public boolean existFundingSource(long fundingSourceID) {
    FundingSource fundingSource = this.find(fundingSourceID);
    if (fundingSource == null) {
      return false;
    }
    return true;

  }

  @Override
  public FundingSource find(long id) {
    return dao.find(FundingSource.class, id);

  }


  @Override
  public List<FundingSource> findAll() {
    String query = "from " + FundingSource.class.getName() + " where is_active=1";
    List<FundingSource> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<Map<String, Object>> getFundingSource(long userId, String crp) {
    StringBuilder builder = new StringBuilder();
    builder.append("select DISTINCT project_id from user_permission where  crp_acronym='" + crp
      + "' and permission_id = 438 and project_id is not null");
    List<Map<String, Object>> list =
      dao.excuteStoreProccedure(" call getPermissions(" + userId + ")", builder.toString());

    return list;
  }

  @Override
  public long save(FundingSource fundingSource) {
    if (fundingSource.getId() == null) {
      dao.save(fundingSource);
    } else {
      dao.update(fundingSource);
    }


    return fundingSource.getId();
  }

  @Override
  public long save(FundingSource fundingSource, String sectionName, List<String> relationsName, Phase phase) {
    if (fundingSource.getId() == null) {
      dao.save(fundingSource, sectionName, relationsName, phase);
    } else {
      dao.update(fundingSource, sectionName, relationsName, phase);
    }


    return fundingSource.getId();
  }

  @Override
  public List<FundingSource> searchFundingSources(String query, int year, long crpID, long phaseID) {
    StringBuilder q = new StringBuilder();
    q.append("SELECT fsi.id AS id ");
    q.append("FROM  funding_sources_info fsi ");
    q.append("INNER JOIN funding_sources fs ON fs.id = fsi.funding_source_id ");
    q.append("AND fs.is_active ");
    q.append("AND fs.crp_id = " + crpID);
    q.append(" WHERE ");
    q.append("(fsi.title LIKE '%" + query + "%' ");
    q.append("OR fsi.funding_source_id LIKE '%" + query + "%' ");
    q.append("OR CONCAT('FS', fsi.funding_source_id) LIKE '%" + query + "%' ");
    q.append("OR (SELECT NAME FROM budget_types bt WHERE bt.id = fsi.type) LIKE '%" + query + "%' )");
    q.append("AND fsi.type = 1 ");
    q.append("AND fsi.id_phase = " + phaseID);
    q.append(" AND fsi.end_date IS NOT NULL");
    q.append(" AND " + year + " <= YEAR(fsi.end_date) ");

    List<Map<String, Object>> rList = dao.findCustomQuery(q.toString());

    List<FundingSource> fundingSources = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        FundingSourceInfo fundingSourceInfo =
          dao.find(FundingSourceInfo.class, Long.parseLong(map.get("id").toString()));
        fundingSourceInfo.getFundingSource().setFundingSourceInfo(fundingSourceInfo);
        fundingSources.add(fundingSourceInfo.getFundingSource());
      }
    }

    return fundingSources;
  }

  @Override
  public List<FundingSource> searchFundingSourcesByInstitution(String query, long institutionID, int year, long crpID,
    long phaseID) {
    StringBuilder q = new StringBuilder();
    q.append("SELECT fsi.id AS id ");
    q.append("FROM  funding_sources_info fsi ");
    q.append("INNER JOIN funding_sources fs ON fs.id = fsi.funding_source_id ");
    q.append("AND fs.is_active ");
    q.append("AND fs.crp_id = " + crpID);
    q.append(" WHERE ");
    q.append("(fsi.title LIKE '%" + query + "%' ");
    q.append("OR fsi.funding_source_id LIKE '%" + query + "%' ");
    q.append("OR CONCAT('FS', fsi.funding_source_id) LIKE '%" + query + "%' ");
    q.append("OR (SELECT NAME FROM budget_types bt WHERE bt.id = fsi.type) LIKE '%" + query + "%' )");

    q.append("AND fsi.id_phase = " + phaseID);
    q.append(" AND fsi.end_date IS NOT NULL ");
    q.append("AND " + year + " <= YEAR(fsi.end_date) ");

    List<Map<String, Object>> rList = dao.findCustomQuery(q.toString());

    List<FundingSource> fundingSources = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        FundingSourceInfo fundingSourceInfo =
          dao.find(FundingSourceInfo.class, Long.parseLong(map.get("id").toString()));
        fundingSourceInfo.getFundingSource().setFundingSourceInfo(fundingSourceInfo);
        fundingSources.add(fundingSourceInfo.getFundingSource());
      }
    }

    List<FundingSource> fundingSourcesReturn = new ArrayList<>();
    SimpleDateFormat df = new SimpleDateFormat("yyyy");
    for (FundingSource fundingSource : fundingSources) {
      try {
        if (fundingSource.getFundingSourceInfo().getBudgetType().getId().intValue() != 1) {
          if (fundingSource.hasInstitution(institutionID, phaseID)) {
            fundingSourcesReturn.add(fundingSource);
          }
        } else {
          fundingSourcesReturn.add(fundingSource);
        }

      } catch (Exception e) {

        e.printStackTrace();
      }
    }

    return fundingSourcesReturn;
  }

  @Override
  public List<FundingSource> searchFundingSourcesByLocElement(long projectId, long locElementId, int year, long crpID) {

    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("funding_sources.id as id ");
    query.append("FROM ");
    query.append("funding_source_locations ");
    query.append("INNER JOIN funding_sources ON funding_source_locations.funding_source_id = funding_sources.id ");
    query.append("INNER JOIN project_budgets ON project_budgets.funding_source_id = funding_sources.id ");
    query.append("WHERE ");
    query.append("funding_source_locations.loc_element_id =" + locElementId + "   AND funding_sources.crp_id=" + crpID
      + " AND project_budgets.project_id=" + projectId + "  AND  funding_source_locations.is_active=1 and ");
    query.append("project_budgets.is_active = 1 AND  ");
    query.append("project_budgets.`year` =" + year);

    List<Map<String, Object>> rList = dao.findCustomQuery(query.toString());

    List<FundingSource> fundingSources = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        FundingSource fundingSource = this.find(Long.parseLong(map.get("id").toString()));


        fundingSources.add(fundingSource);
      }
    }

    return fundingSources;

  }

  @Override
  public List<FundingSource> searchFundingSourcesByLocElementType(long projectId, long locElementTypeId, int year,
    long crpID) {

    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("funding_sources.id as id ");
    query.append("FROM ");
    query.append("funding_source_locations ");
    query.append("INNER JOIN funding_sources ON funding_source_locations.funding_source_id = funding_sources.id ");
    query.append("INNER JOIN project_budgets ON project_budgets.funding_source_id = funding_sources.id ");
    query.append("WHERE ");
    query.append("funding_source_locations.loc_element_type_id =" + locElementTypeId + " AND funding_sources.crp_id="
      + crpID + " AND project_budgets.project_id=" + projectId + "  AND  funding_source_locations.is_active=1 and ");
    query.append("project_budgets.is_active = 1 AND  ");
    query.append("project_budgets.`year` =" + year);

    List<Map<String, Object>> rList = dao.findCustomQuery(query.toString());

    List<FundingSource> fundingSources = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        FundingSource fundingSource = this.find(Long.parseLong(map.get("id").toString()));
        fundingSources.add(fundingSource);
      }
    }

    return fundingSources;

  }


}