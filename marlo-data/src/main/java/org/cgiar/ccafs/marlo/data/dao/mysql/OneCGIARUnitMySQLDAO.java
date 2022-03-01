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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.OneCGIARUnitDAO;
import org.cgiar.ccafs.marlo.data.model.OneCGIARUnit;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class OneCGIARUnitMySQLDAO extends AbstractMarloDAO<OneCGIARUnit, Long> implements OneCGIARUnitDAO {

  @Inject
  public OneCGIARUnitMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteOneCGIARUnit(long oneCGIARUnitId) {
    /*
     * OneCGIARUnit oneCGIARUnit = this.getUnitById(oneCGIARUnitId);
     * oneCGIARUnit.setActive(false);
     * this.save(oneCGIARUnit);
     */
    OneCGIARUnit oneCGIARUnit = this.getUnitById(oneCGIARUnitId);
    this.delete(oneCGIARUnit);
  }

  @Override
  public boolean existOneCGIARUnit(long oneCGIARUnitID) {
    OneCGIARUnit oneCGIARUnit = this.getUnitById(oneCGIARUnitID);
    if (oneCGIARUnit == null) {
      return false;
    }
    return true;

  }

  @Override
  public List<OneCGIARUnit> getAll() {
    String query = "from " + OneCGIARUnit.class.getName();
    List<OneCGIARUnit> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public OneCGIARUnit getUnitByFinancialCode(String financialCode) {
    String query = "select ocu from OneCGIARUnit ocu where financialCode = :financialCode";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("financialCode", financialCode);

    List<OneCGIARUnit> results = super.findAll(createQuery);

    OneCGIARUnit oneCGIARUnit = (results != null && !results.isEmpty()) ? results.get(0) : null;

    return oneCGIARUnit;
  }

  @Override
  public OneCGIARUnit getUnitById(long id) {
    return super.find(OneCGIARUnit.class, id);
  }

  @Override
  public List<OneCGIARUnit> getUnitsByParent(long parentId) {
    String query = "select oca from OneCGIARUnit oca where parentUnit.id = :parentId";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("parentId", parentId);

    List<OneCGIARUnit> results = super.findAll(createQuery);

    return results;
  }

  @Override
  public List<OneCGIARUnit> getUnitsByScienceGroup(long scienceGroupId) {
    String query = "select oca from OneCGIARUnit oca where scienceGroup.id = :scienceGroupId";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("scienceGroupId", scienceGroupId);

    List<OneCGIARUnit> results = super.findAll(createQuery);

    return results;
  }

  @Override
  public OneCGIARUnit save(OneCGIARUnit oneCGIARUnit) {
    if (oneCGIARUnit.getId() == null) {
      super.saveEntity(oneCGIARUnit);
    } else {
      oneCGIARUnit = super.update(oneCGIARUnit);
    }
    return oneCGIARUnit;
  }
}
