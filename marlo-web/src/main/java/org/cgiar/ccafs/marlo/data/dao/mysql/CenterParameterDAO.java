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

import com.google.inject.Inject;

public class CenterParameterDAO implements ICenterParameterDAO {

  private StandardDAO dao;

  @Inject
  public CenterParameterDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCenterParameter(long centerParameterId) {
    CenterParameter centerParameter = this.find(centerParameterId);
    return dao.delete(centerParameter);
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
    return dao.find(CenterParameter.class, id);

  }

  @Override
  public List<CenterParameter> findAll() {
    String query = "from " + CenterParameter.class.getName();
    List<CenterParameter> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterParameter> getCenterParametersByUserId(long userId) {
    String query = "from " + CenterParameter.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterParameter centerParameter) {
    if (centerParameter.getId() == null) {
      dao.save(centerParameter);
    } else {
      dao.update(centerParameter);
    }
    return centerParameter.getId();
  }

  @Override
  public long save(CenterParameter centerParameter, String actionName, List<String> relationsName) {
    if (centerParameter.getId() == null) {
      dao.save(centerParameter, actionName, relationsName);
    } else {
      dao.update(centerParameter, actionName, relationsName);
    }
    return centerParameter.getId();
  }


}