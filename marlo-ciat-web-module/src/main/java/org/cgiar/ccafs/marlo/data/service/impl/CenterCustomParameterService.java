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
package org.cgiar.ccafs.marlo.data.service.impl;


import org.cgiar.ccafs.marlo.data.dao.ICenterCustomParameterDAO;
import org.cgiar.ccafs.marlo.data.model.CenterCustomParameter;
import org.cgiar.ccafs.marlo.data.service.ICenterCustomParameterService;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterCustomParameterService implements ICenterCustomParameterService {


  private ICenterCustomParameterDAO centerCustomParameterDAO;

  // Managers


  @Inject
  public CenterCustomParameterService(ICenterCustomParameterDAO centerCustomParameterDAO) {
    this.centerCustomParameterDAO = centerCustomParameterDAO;


  }

  @Override
  public boolean deleteCenterCustomParameter(long centerCustomParameterId) {

    return centerCustomParameterDAO.deleteCenterCustomParameter(centerCustomParameterId);
  }

  @Override
  public boolean existCenterCustomParameter(long centerCustomParameterID) {

    return centerCustomParameterDAO.existCenterCustomParameter(centerCustomParameterID);
  }

  @Override
  public List<CenterCustomParameter> findAll() {

    return centerCustomParameterDAO.findAll();

  }

  @Override
  public CenterCustomParameter getCenterCustomParameterById(long centerCustomParameterID) {

    return centerCustomParameterDAO.find(centerCustomParameterID);
  }

  @Override
  public List<CenterCustomParameter> getCenterCustomParametersByUserId(Long userId) {
    return centerCustomParameterDAO.getCenterCustomParametersByUserId(userId);
  }

  @Override
  public long saveCenterCustomParameter(CenterCustomParameter centerCustomParameter) {

    return centerCustomParameterDAO.save(centerCustomParameter);
  }

  @Override
  public long saveCenterCustomParameter(CenterCustomParameter centerCustomParameter, String actionName, List<String> relationsName) {
    return centerCustomParameterDAO.save(centerCustomParameter, actionName, relationsName);
  }


}
