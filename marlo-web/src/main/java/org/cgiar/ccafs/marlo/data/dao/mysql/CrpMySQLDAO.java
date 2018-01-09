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

import org.cgiar.ccafs.marlo.data.dao.CrpDAO;
import org.cgiar.ccafs.marlo.data.model.Crp;

import java.util.List;

<<<<<<< HEAD
import com.google.inject.Inject;
import org.hibernate.Query;
=======
import javax.inject.Named;
import javax.inject.Inject;
>>>>>>> 526eac1563be62614621b474c5f4baed2a9e2a0d
import org.hibernate.SessionFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Christian Garcia - CIAT/CCAFS
 */
@Named
public class CrpMySQLDAO extends AbstractMarloDAO<Crp, Long> implements CrpDAO {

  @Inject
  public CrpMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }


  @Override
  public List<Crp> crpUsers(String emai) {

    String query = "select distinct cp from Crp cp inner join fetch cp.crpUsers cpUser   "
      + "where cpUser.user.email = :emai and cpUser.active=1 ";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("emai", emai);
    List<Crp> crps = super.findAll(createQuery);
    return crps;
  }

  @Override
  public void deleteCrp(long crpId) {
    Crp crp = this.find(crpId);
    crp.setActive(false);
    this.save(crp);
  }

  @Override
  public boolean existCrp(long crpID) {
    Crp crp = this.find(crpID);
    if (crp == null) {
      return false;
    }
    return true;

  }

  @Override
  public Crp find(long id) {
    return super.find(Crp.class, id);

  }

  @Override
  public List<Crp> findAll() {
    String query = "from " + Crp.class.getName() + " where is_active=1";
    List<Crp> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public Crp findCrpByAcronym(String acronym) {
    String query = "from " + Crp.class.getName() + " where acronym='" + acronym + "'";
    List<Crp> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public Crp save(Crp crp) {
    if (crp.getId() == null) {
      super.saveEntity(crp);
    } else {
      crp = super.update(crp);
    }
    return crp;
  }
}
