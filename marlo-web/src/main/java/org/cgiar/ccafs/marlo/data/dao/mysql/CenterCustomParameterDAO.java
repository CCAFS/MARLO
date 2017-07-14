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

import org.cgiar.ccafs.marlo.data.dao.ICenterCustomParameterDAO;
import org.cgiar.ccafs.marlo.data.model.CenterCustomParameter;

import java.util.List;

import com.google.inject.Inject;

public class CenterCustomParameterDAO implements ICenterCustomParameterDAO {

  private StandardDAO dao;

  @Inject
  public CenterCustomParameterDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCenterCustomParameter(long centerCustomParameterId) {
    CenterCustomParameter centerCustomParameter = this.find(centerCustomParameterId);
    centerCustomParameter.setActive(false);
    return this.save(centerCustomParameter) > 0;
  }

  @Override
  public boolean existCenterCustomParameter(long centerCustomParameterID) {
    CenterCustomParameter centerCustomParameter = this.find(centerCustomParameterID);
    if (centerCustomParameter == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterCustomParameter find(long id) {
    return dao.find(CenterCustomParameter.class, id);

  }

  @Override
  public List<CenterCustomParameter> findAll() {
    String query = "from " + CenterCustomParameter.class.getName();
    List<CenterCustomParameter> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterCustomParameter> getCenterCustomParametersByUserId(long userId) {
    String query = "from " + CenterCustomParameter.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterCustomParameter centerCustomParameter) {
    if (centerCustomParameter.getId() == null) {
      dao.save(centerCustomParameter);
    } else {
      dao.update(centerCustomParameter);
    }
    return centerCustomParameter.getId();
  }

  @Override
  public long save(CenterCustomParameter centerCustomParameter, String actionName, List<String> relationsName) {
    if (centerCustomParameter.getId() == null) {
      dao.save(centerCustomParameter, actionName, relationsName);
    } else {
      dao.update(centerCustomParameter, actionName, relationsName);
    }
    return centerCustomParameter.getId();
  }


}