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

import org.cgiar.ccafs.marlo.data.dao.SDGContributionDAO;
import org.cgiar.ccafs.marlo.data.model.SDGContribution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class SDGContributionMySQLDAO extends AbstractMarloDAO<SDGContribution, Long> implements SDGContributionDAO {


  @Inject
  public SDGContributionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteSDGContribution(long sDGContributionId) {
    SDGContribution sDGContribution = this.find(sDGContributionId);
    sDGContribution.setActive(false);
    this.update(sDGContribution);
  }

  @Override
  public boolean existSDGContribution(long sDGContributionID) {
    SDGContribution sDGContribution = this.find(sDGContributionID);
    if (sDGContribution == null) {
      return false;
    }
    return true;

  }

  @Override
  public SDGContribution find(long id) {
    return super.find(SDGContribution.class, id);

  }

  @Override
  public List<SDGContribution> findAll() {
    String query = "from " + SDGContribution.class.getName() + " where is_active=1";
    List<SDGContribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<SDGContribution> findSDGcontributionByExpectedPhaseAndLever(long phase, long expectedId, long lever,
    int isPrimary) {
    StringBuilder query = new StringBuilder();
    query.append(" select distinct sc.id ");
    query.append(" from sdg_contributions as sc ");
    query.append(" join project_expected_study_sdg_alliance_levers as pessal ");
    query.append(" on sc.id = pessal.sdg_contribution_id ");
    query.append(" join alliance_levers_sdg_contributions as alsc on alsc.sdg_contribution_id =sc.id ");
    query.append(" where alsc.alliance_lever_id =pessal.alliance_lever_id ");
    query.append(" and pessal.id_phase=" + phase);
    query.append(" and pessal.expected_id=" + expectedId);
    query.append(" and pessal.alliance_lever_id=" + lever);
    query.append(" and pessal.is_primary=" + isPrimary);
    query.append(" and sc.is_active = pessal.is_active ");
    query.append(" and pessal.is_active =1 ");

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<SDGContribution> sDGContributions = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        SDGContribution sDGContribution = this.find(Long.parseLong(map.get("id").toString()));
        sDGContributions.add(sDGContribution);
      }
    }

    return sDGContributions;
  }


  @Override
  public List<SDGContribution> findSDGcontributionByLever(long lever) {
    StringBuilder query = new StringBuilder();
    query.append(" select distinct sc.id from sdg_contributions as sc ");
    query.append(" join alliance_levers_sdg_contributions as alsc on alsc.sdg_contribution_id =sc.id ");
    query.append(" where sc.id =alsc.sdg_contribution_id ");
    query.append(" and sc.is_active = alsc.is_active ");
    query.append(" and alsc.alliance_lever_id =" + lever);
    query.append(" and alsc.is_active =1 ");

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<SDGContribution> sDGContributions = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        SDGContribution sDGContribution = this.find(Long.parseLong(map.get("id").toString()));
        sDGContributions.add(sDGContribution);
      }
    }

    return sDGContributions;
  }

  @Override
  public SDGContribution save(SDGContribution sDGContribution) {
    if (sDGContribution.getId() == null) {
      super.saveEntity(sDGContribution);
    } else {
      sDGContribution = super.update(sDGContribution);
    }


    return sDGContribution;
  }


}