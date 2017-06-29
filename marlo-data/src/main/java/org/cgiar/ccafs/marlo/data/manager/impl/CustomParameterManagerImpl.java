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


import org.cgiar.ccafs.marlo.data.dao.CustomParameterDAO;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CustomParameterManagerImpl implements CustomParameterManager {


  private CustomParameterDAO customParameterDAO;
  // Managers


  @Inject
  public CustomParameterManagerImpl(CustomParameterDAO customParameterDAO) {
    this.customParameterDAO = customParameterDAO;


  }

  @Override
  public boolean deleteCustomParameter(long customParameterId) {

    return customParameterDAO.deleteCustomParameter(customParameterId);
  }

  @Override
  public boolean existCustomParameter(long customParameterID) {

    return customParameterDAO.existCustomParameter(customParameterID);
  }

  @Override
  public List<CustomParameter> findAll() {

    return customParameterDAO.findAll();

  }

  @Override
  public CustomParameter getCustomParameterById(long customParameterID) {

    return customParameterDAO.find(customParameterID);
  }

  @Override
  public long saveCustomParameter(CustomParameter customParameter) {

    return customParameterDAO.save(customParameter);
  }


}
