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
import org.cgiar.ccafs.marlo.data.model.BudgetType;
import org.cgiar.ccafs.marlo.data.model.FundingSource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class FundingSourceMySQLDAO extends AbstractMarloDAO<FundingSource, Long> implements FundingSourceDAO {


  @Inject
  public FundingSourceMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteFundingSource(long fundingSourceId) {
    FundingSource fundingSource = this.find(fundingSourceId);
    fundingSource.setActive(false);
    this.save(fundingSource);
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
    return super.find(FundingSource.class, id);

  }

  @Override
  public List<FundingSource> findAll() {
    String query = "from " + FundingSource.class.getName() + " where is_active=1";
    List<FundingSource> list = super.findAll(query);
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
      super.excuteStoreProcedure(" call getPermissions(" + userId + ")", builder.toString());

    return list;
  }

  @Override
  public FundingSource save(FundingSource fundingSource) {
    if (fundingSource.getId() == null) {
      super.saveEntity(fundingSource);
    } else {
      fundingSource = super.update(fundingSource);
    }


    return fundingSource;
  }

  @Override
  public FundingSource save(FundingSource fundingSource, String sectionName, List<String> relationsName) {
    if (fundingSource.getId() == null) {
      super.saveEntity(fundingSource, sectionName, relationsName);
    } else {
      fundingSource = super.update(fundingSource, sectionName, relationsName);
    }


    return fundingSource;
  }

  @Override
  public List<FundingSource> searchFundingSources(String query, int year, long crpID) {
    StringBuilder q = new StringBuilder();
    q.append("from " + FundingSource.class.getName());
    q.append(" where global_unit_id=" + crpID + " and (title like '%" + query + "%' ");
    q.append("OR id like '%" + query + "%' or concat('FS',id) like '%" + query + "%' or (select name from "
      + BudgetType.class.getName() + " where id=type) like '%" + query + "%') and is_active=1 and global_unit_id="
      + crpID + " and ( type=1)");

    List<FundingSource> fundingSources = super.findAll(q.toString());
    SimpleDateFormat df = new SimpleDateFormat("yyyy");
    return fundingSources.stream()
      .filter(c -> c.getEndDate() != null && year <= Integer.parseInt(df.format(c.getEndDate())))
      .collect(Collectors.toList());
  }

  @Override
  public List<FundingSource> searchFundingSourcesByFinanceCode(String ocsCode) {
    StringBuilder q = new StringBuilder();
    q.append("from " + FundingSource.class.getName());
    q.append(" where finance_code=" + ocsCode);


    List<FundingSource> fundingSources = super.findAll(q.toString());
    return fundingSources;
  }


  @Override
  public List<FundingSource> searchFundingSourcesByInstitution(String query, long institutionID, int year, long crpID) {
    StringBuilder q = new StringBuilder();
    q.append("from " + FundingSource.class.getName());
    q.append(" where is_active=1 and (title like '%" + query + "%' ");
    q.append("OR id like '%" + query + "%' or concat('FS',id) like '%" + query + "%' or (select name from "
      + BudgetType.class.getName() + " where id=type) like '%" + query + "%') and global_unit_id=" + crpID);


    List<FundingSource> fundingSources = super.findAll(q.toString());
    List<FundingSource> fundingSourcesReturn = new ArrayList<>();
    SimpleDateFormat df = new SimpleDateFormat("yyyy");
    for (FundingSource fundingSource : fundingSources) {
      try {
        if (fundingSource.getEndDate() != null) {
          if (year <= Integer.parseInt(df.format(fundingSource.getEndDate()))) {
            if (fundingSource.getBudgetType().getId().intValue() != 1) {
              if (fundingSource.hasInstitution(institutionID)) {
                fundingSourcesReturn.add(fundingSource);
              }
            } else {
              fundingSourcesReturn.add(fundingSource);
            }
          }
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
    query.append("funding_source_locations.loc_element_id =" + locElementId + "   AND funding_sources.global_unit_id="
      + crpID + " AND project_budgets.project_id=" + projectId + "  AND  funding_source_locations.is_active=1 and ");
    query.append("project_budgets.is_active = 1 AND  ");
    query.append("project_budgets.`year` =" + year);

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());

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
    query.append(
      "funding_source_locations.loc_element_type_id =" + locElementTypeId + " AND funding_sources.global_unit_id="
        + crpID + " AND project_budgets.project_id=" + projectId + "  AND  funding_source_locations.is_active=1 and ");
    query.append("project_budgets.is_active = 1 AND  ");
    query.append("project_budgets.`year` =" + year);

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());

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