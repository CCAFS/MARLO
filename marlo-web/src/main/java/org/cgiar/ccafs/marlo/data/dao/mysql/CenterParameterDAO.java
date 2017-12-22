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

import org.cgiar.ccafs.marlo.data.dao.ICenterParameterDAO;
import org.cgiar.ccafs.marlo.data.model.CenterParameter;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CenterParameterDAO extends AbstractMarloDAO<CenterParameter, Long> implements ICenterParameterDAO {


  @Inject
  public CenterParameterDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCenterParameter(long centerParameterId) {
    CenterParameter centerParameter = this.find(centerParameterId);
    super.delete(centerParameter);
  }

  @Override
  public boolean existCenterParameter(long centerParameterID) {
    CenterParameter centerParameter = this.find(centerParameterID);
    if (centerParameter == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterParameter find(long id) {
    return super.find(CenterParameter.class, id);

  }

  @Override
  public List<CenterParameter> findAll() {
    String query = "from " + CenterParameter.class.getName();
    List<CenterParameter> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterParameter> getCenterParametersByUserId(long userId) {
    String query = "from " + CenterParameter.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CenterParameter save(CenterParameter centerParameter) {
    if (centerParameter.getId() == null) {
      super.saveEntity(centerParameter);
    } else {
      centerParameter = super.update(centerParameter);
    }
    return centerParameter;
  }

  @Override
  public CenterParameter save(CenterParameter centerParameter, String actionName, List<String> relationsName) {
    if (centerParameter.getId() == null) {
      super.saveEntity(centerParameter, actionName, relationsName);
    } else {
      centerParameter = super.update(centerParameter, actionName, relationsName);
    }
    return centerParameter;
  }


}