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

import org.cgiar.ccafs.marlo.data.dao.RepIndContributionOfCrpDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndContributionOfCrp;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndContributionOfCrpMySQLDAO extends AbstractMarloDAO<RepIndContributionOfCrp, Long>
  implements RepIndContributionOfCrpDAO {


  @Inject
  public RepIndContributionOfCrpMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndContributionOfCrp(long repIndContributionOfCrpId) {
    RepIndContributionOfCrp repIndContributionOfCrp = this.find(repIndContributionOfCrpId);
    this.delete(repIndContributionOfCrp);
  }

  @Override
  public boolean existRepIndContributionOfCrp(long repIndContributionOfCrpID) {
    RepIndContributionOfCrp repIndContributionOfCrp = this.find(repIndContributionOfCrpID);
    if (repIndContributionOfCrp == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndContributionOfCrp find(long id) {
    return super.find(RepIndContributionOfCrp.class, id);

  }

  @Override
  public List<RepIndContributionOfCrp> findAll() {
    String query = "from " + RepIndContributionOfCrp.class.getName();
    List<RepIndContributionOfCrp> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndContributionOfCrp save(RepIndContributionOfCrp repIndContributionOfCrp) {
    if (repIndContributionOfCrp.getId() == null) {
      super.saveEntity(repIndContributionOfCrp);
    } else {
      repIndContributionOfCrp = super.update(repIndContributionOfCrp);
    }


    return repIndContributionOfCrp;
  }


}