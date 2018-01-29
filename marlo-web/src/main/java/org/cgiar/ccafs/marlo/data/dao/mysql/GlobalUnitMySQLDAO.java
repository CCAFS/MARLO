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

import org.cgiar.ccafs.marlo.data.dao.GlobalUnitDAO;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class GlobalUnitMySQLDAO extends AbstractMarloDAO<GlobalUnit, Long> implements GlobalUnitDAO {


  @Inject
  public GlobalUnitMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public List<GlobalUnit> crpUsers(String emai) {

    String query = "select distinct cp from GlobalUnit cp inner join fetch cp.crpUsers cpUser   "
      + "where cpUser.user.email = :emai and cpUser.active=1 ";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("emai", emai);
    List<GlobalUnit> crps = super.findAll(createQuery);
    return crps;
  }

  @Override
  public void deleteGlobalUnit(long globalUnitId) {
    GlobalUnit globalUnit = this.find(globalUnitId);
    globalUnit.setActive(false);
    this.save(globalUnit);
  }

  @Override
  public boolean existGlobalUnit(long globalUnitID) {
    GlobalUnit globalUnit = this.find(globalUnitID);
    if (globalUnit == null) {
      return false;
    }
    return true;

  }

  @Override
  public GlobalUnit find(long id) {
    return super.find(GlobalUnit.class, id);

  }


  @Override
  public List<GlobalUnit> findAll() {
    String query = "from " + GlobalUnit.class.getName() + " where is_active=1";
    List<GlobalUnit> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public GlobalUnit findGlobalUnitByAcronym(String acronym) {
    String query = "from " + GlobalUnit.class.getName() + " where acronym='" + acronym + "'";
    List<GlobalUnit> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public GlobalUnit save(GlobalUnit globalUnit) {
    if (globalUnit.getId() == null) {
      super.saveEntity(globalUnit);
    } else {
      globalUnit = super.update(globalUnit);
    }


    return globalUnit;
  }


}