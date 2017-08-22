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


import org.cgiar.ccafs.marlo.data.dao.ICenterParameterDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterParameterManager;
import org.cgiar.ccafs.marlo.data.model.CenterParameter;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterParameterManager implements ICenterParameterManager {


  private ICenterParameterDAO centerParameterDAO;

  // Managers


  @Inject
  public CenterParameterManager(ICenterParameterDAO centerParameterDAO) {
    this.centerParameterDAO = centerParameterDAO;


  }

  @Override
  public void deleteCenterParameter(long centerParameterId) {

    centerParameterDAO.deleteCenterParameter(centerParameterId);
  }

  @Override
  public boolean existCenterParameter(long centerParameterID) {

    return centerParameterDAO.existCenterParameter(centerParameterID);
  }

  @Override
  public List<CenterParameter> findAll() {

    return centerParameterDAO.findAll();

  }

  @Override
  public CenterParameter getCenterParameterById(long centerParameterID) {

    return centerParameterDAO.find(centerParameterID);
  }

  @Override
  public List<CenterParameter> getCenterParametersByUserId(Long userId) {
    return centerParameterDAO.getCenterParametersByUserId(userId);
  }

  @Override
  public CenterParameter saveCenterParameter(CenterParameter centerParameter) {

    return centerParameterDAO.save(centerParameter);
  }

  @Override
  public CenterParameter saveCenterParameter(CenterParameter centerParameter, String actionName, List<String> relationsName) {
    return centerParameterDAO.save(centerParameter, actionName, relationsName);
  }


}
