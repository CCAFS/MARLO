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

import org.cgiar.ccafs.marlo.data.dao.OtherContributionDAO;
import org.cgiar.ccafs.marlo.data.model.OtherContribution;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class OtherContributionMySQLDAO extends AbstractMarloDAO<OtherContribution, Long> implements OtherContributionDAO {


  @Inject
  public OtherContributionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteOtherContribution(long otherContributionId) {
    OtherContribution otherContribution = this.find(otherContributionId);
    otherContribution.setActive(false);
    this.save(otherContribution);
  }

  @Override
  public boolean existOtherContribution(long otherContributionID) {
    OtherContribution otherContribution = this.find(otherContributionID);
    if (otherContribution == null) {
      return false;
    }
    return true;

  }

  @Override
  public OtherContribution find(long id) {
    return super.find(OtherContribution.class, id);

  }

  @Override
  public List<OtherContribution> findAll() {
    String query = "from " + OtherContribution.class.getName() + " where is_active=1";
    List<OtherContribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public OtherContribution save(OtherContribution otherContribution) {
    if (otherContribution.getId() == null) {
      super.saveEntity(otherContribution);
    } else {
      otherContribution = super.update(otherContribution);
    }


    return otherContribution;
  }


}