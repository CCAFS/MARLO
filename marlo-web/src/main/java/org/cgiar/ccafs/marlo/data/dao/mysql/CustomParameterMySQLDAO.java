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

import org.cgiar.ccafs.marlo.data.dao.CustomParameterDAO;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class CustomParameterMySQLDAO extends AbstractMarloDAO<CustomParameter, Long> implements CustomParameterDAO {


  @Inject
  public CustomParameterMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCustomParameter(long customParameterId) {
    CustomParameter customParameter = this.find(customParameterId);
    customParameter.setActive(false);
    this.save(customParameter);
  }

  @Override
  public boolean existCustomParameter(long customParameterID) {
    CustomParameter customParameter = this.find(customParameterID);
    if (customParameter == null) {
      return false;
    }
    return true;

  }

  @Override
  public CustomParameter find(long id) {
    return super.find(CustomParameter.class, id);

  }

  @Override
  public List<CustomParameter> findAll() {
    String query = "from " + CustomParameter.class.getName() + " where is_active=1";
    List<CustomParameter> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CustomParameter> getAllCustomParametersByGlobalUnitId(long globalUnitId) {
    String queryString = "SELECT cp FROM CustomParameter cp INNER JOIN FETCH cp.parameter p "
      + "WHERE cp.crp.id =:globalUnitId " + "AND cp.active = TRUE";

    List<CustomParameter> customParameters = this.getSessionFactory().getCurrentSession().createQuery(queryString)
      .setParameter("globalUnitId", globalUnitId).list();

    return customParameters;
  }

  @Override
  public CustomParameter getCustomParematerByParameterKeyAndGlobalUnitId(String paramaterKey, long globalUnitId) {

    String queryString = "SELECT cp FROM CustomParameter cp INNER JOIN cp.parameter p "
      + "WHERE p.key =:key AND cp.crp.id =:globalUnitId " + "AND cp.active = TRUE";

    Object customParameter = this.getSessionFactory().getCurrentSession().createQuery(queryString)
      .setParameter("key", paramaterKey).setParameter("globalUnitId", globalUnitId).setCacheable(true).uniqueResult();

    if (customParameter != null) {
      return (CustomParameter) customParameter;
    }
    return null;
  }

  @Override
  public CustomParameter save(CustomParameter customParameter) {
    if (customParameter.getId() == null) {
      super.saveEntity(customParameter);
    } else {
      customParameter = super.update(customParameter);
    }


    return customParameter;
  }


}