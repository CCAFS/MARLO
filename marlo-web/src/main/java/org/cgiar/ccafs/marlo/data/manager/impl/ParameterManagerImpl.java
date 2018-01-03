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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ParameterDAO;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.model.Parameter;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class ParameterManagerImpl implements ParameterManager {


  private ParameterDAO parameterDAO;
  // Managers


  @Inject
  public ParameterManagerImpl(ParameterDAO parameterDAO) {
    this.parameterDAO = parameterDAO;


  }

  @Override
  public void deleteParameter(long parameterId) {

    parameterDAO.deleteParameter(parameterId);
  }

  @Override
  public boolean existParameter(long parameterID) {

    return parameterDAO.existParameter(parameterID);
  }

  @Override
  public List<Parameter> findAll() {

    return parameterDAO.findAll();

  }

  @Override
  public Parameter getParameterById(long parameterID) {

    return parameterDAO.find(parameterID);
  }

  @Override
  public Parameter getParameterByKey(String key) {
    return parameterDAO.getParameterByKey(key);
  }

  @Override
  public Parameter saveParameter(Parameter parameter) {

    return parameterDAO.save(parameter);
  }


}
