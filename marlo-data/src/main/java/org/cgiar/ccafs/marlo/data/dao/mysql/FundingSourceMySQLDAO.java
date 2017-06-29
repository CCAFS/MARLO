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
    String query = "select DISTINCT project_id from user_permissions where id=" + userId + " and crp_acronym='" + crp
      + "' and permission_id = 438 and project_id is not null";
    return dao.findCustomQuery(query);
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
  public long save(FundingSource fundingSource, String sectionName, List<String> relationsName) {
    if (fundingSource.getId() == null) {
      dao.save(fundingSource, sectionName, relationsName);
    } else {
      dao.update(fundingSource, sectionName, relationsName);
    }


    return fundingSource.getId();
  }

  @Override
  public List<FundingSource> searchFundingSources(String query, int year, long crpID) {
    StringBuilder q = new StringBuilder();
    q.append("from " + FundingSource.class.getName());
    q.append(" where crp_id=" + crpID + " and (title like '%" + query + "%' ");
    q.append("OR id like '%" + query + "%' or concat('FS',id) like '%" + query + "%' or (select name from "
      + BudgetType.class.getName() + " where id=type) like '%" + query + "%') and is_active=1 and crp_id=" + crpID
      + " and ( type=1)");

    List<FundingSource> fundingSources = dao.findAll(q.toString());
    SimpleDateFormat df = new SimpleDateFormat("yyyy");
    return fundingSources.stream()
      .filter(c -> c.getEndDate() != null && year <= Integer.parseInt(df.format(c.getEndDate())))
      .collect(Collectors.toList());
  }

  @Override
  public List<FundingSource> searchFundingSourcesByInstitution(String query, long institutionID, int year, long crpID) {
    StringBuilder q = new StringBuilder();
    q.append("from " + FundingSource.class.getName());
    q.append(" where is_active=1 and (title like '%" + query + "%' ");
    q.append("OR id like '%" + query + "%' or concat('FS',id) like '%" + query + "%' or (select name from "
      + BudgetType.class.getName() + " where id=type) like '%" + query + "%') and crp_id=" + crpID);


    List<FundingSource> fundingSources = dao.findAll(q.toString());
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


}