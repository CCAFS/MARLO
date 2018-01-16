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

import org.cgiar.ccafs.marlo.data.dao.CrpSubIdosContributionDAO;
import org.cgiar.ccafs.marlo.data.model.CrpSubIdosContribution;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CrpSubIdosContributionMySQLDAO extends AbstractMarloDAO<CrpSubIdosContribution, Long> implements CrpSubIdosContributionDAO {


  @Inject
  public CrpSubIdosContributionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCrpSubIdosContribution(long crpSubIdosContributionId) {
    CrpSubIdosContribution crpSubIdosContribution = this.find(crpSubIdosContributionId);
    crpSubIdosContribution.setActive(false);
    this.save(crpSubIdosContribution);
  }

  @Override
  public boolean existCrpSubIdosContribution(long crpSubIdosContributionID) {
    CrpSubIdosContribution crpSubIdosContribution = this.find(crpSubIdosContributionID);
    if (crpSubIdosContribution == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpSubIdosContribution find(long id) {
    return super.find(CrpSubIdosContribution.class, id);

  }

  @Override
  public List<CrpSubIdosContribution> findAll() {
    String query = "from " + CrpSubIdosContribution.class.getName() + " where is_active=1";
    List<CrpSubIdosContribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CrpSubIdosContribution save(CrpSubIdosContribution crpSubIdosContribution) {
    if (crpSubIdosContribution.getId() == null) {
      super.saveEntity(crpSubIdosContribution);
    } else {
      crpSubIdosContribution = super.update(crpSubIdosContribution);
    }
    return crpSubIdosContribution;
  }


}