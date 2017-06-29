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

import org.cgiar.ccafs.marlo.data.dao.ParameterDAO;
import org.cgiar.ccafs.marlo.data.model.Parameter;

import java.util.List;

import com.google.inject.Inject;

public class ParameterMySQLDAO implements ParameterDAO {

  private StandardDAO dao;

  @Inject
  public ParameterMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteParameter(long parameterId) {
    Parameter parameter = this.find(parameterId);

    return dao.delete(parameter);
  }

  @Override
  public boolean existParameter(long parameterID) {
    Parameter parameter = this.find(parameterID);
    if (parameter == null) {
      return false;
    }
    return true;

  }

  @Override
  public Parameter find(long id) {
    return dao.find(Parameter.class, id);

  }

  @Override
  public List<Parameter> findAll() {
    String query = "from " + Parameter.class.getName() + "";
    List<Parameter> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public Parameter getParameterByKey(String key) {
    String query = "from " + Parameter.class.getName() + " where key='" + key + "'";
    List<Parameter> list = dao.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public long save(Parameter parameter) {
    if (parameter.getId() == null) {
      dao.save(parameter);
    } else {
      dao.update(parameter);
    }


    return parameter.getId();
  }


}