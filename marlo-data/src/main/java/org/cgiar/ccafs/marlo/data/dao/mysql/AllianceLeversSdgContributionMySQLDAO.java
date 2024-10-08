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

import org.cgiar.ccafs.marlo.data.dao.AllianceLeversSdgContributionDAO;
import org.cgiar.ccafs.marlo.data.model.AllianceLeversSdgContribution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class AllianceLeversSdgContributionMySQLDAO extends AbstractMarloDAO<AllianceLeversSdgContribution, Long>
  implements AllianceLeversSdgContributionDAO {


  @Inject
  public AllianceLeversSdgContributionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteAllianceLeversSdgContribution(long allianceLeversSdgContributionId) {
    AllianceLeversSdgContribution allianceLeversSdgContribution = this.find(allianceLeversSdgContributionId);
    allianceLeversSdgContribution.setActive(false);
    this.update(allianceLeversSdgContribution);
  }

  @Override
  public boolean existAllianceLeversSdgContribution(long allianceLeversSdgContributionID) {
    AllianceLeversSdgContribution allianceLeversSdgContribution = this.find(allianceLeversSdgContributionID);
    if (allianceLeversSdgContribution == null) {
      return false;
    }
    return true;

  }

  @Override
  public AllianceLeversSdgContribution find(long id) {
    return super.find(AllianceLeversSdgContribution.class, id);

  }

  @Override
  public List<AllianceLeversSdgContribution> findAll() {
    String query = "from " + AllianceLeversSdgContribution.class.getName() + " where is_active=1";
    List<AllianceLeversSdgContribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<AllianceLeversSdgContribution> findAllByLeverId(long leverId) {
    String query =
      "from " + AllianceLeversSdgContribution.class.getName() + " where is_active=1 and alliance_lever_id =" + leverId;
    List<AllianceLeversSdgContribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public AllianceLeversSdgContribution save(AllianceLeversSdgContribution allianceLeversSdgContribution) {
    if (allianceLeversSdgContribution.getId() == null) {
      super.saveEntity(allianceLeversSdgContribution);
    } else {
      allianceLeversSdgContribution = super.update(allianceLeversSdgContribution);
    }


    return allianceLeversSdgContribution;
  }


}