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


import org.cgiar.ccafs.marlo.data.dao.CrpParameterDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpParameterManager;
import org.cgiar.ccafs.marlo.data.model.CrpParameter;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpParameterManagerImpl implements CrpParameterManager {


  private CrpParameterDAO crpParameterDAO;
  // Managers


  @Inject
  public CrpParameterManagerImpl(CrpParameterDAO crpParameterDAO) {
    this.crpParameterDAO = crpParameterDAO;


  }

  @Override
  public boolean deleteCrpParameter(long crpParameterId) {

    return crpParameterDAO.deleteCrpParameter(crpParameterId);
  }

  @Override
  public boolean existCrpParameter(long crpParameterID) {

    return crpParameterDAO.existCrpParameter(crpParameterID);
  }

  @Override
  public List<CrpParameter> findAll() {

    return crpParameterDAO.findAll();

  }

  @Override
  public CrpParameter getCrpParameterById(long crpParameterID) {

    return crpParameterDAO.find(crpParameterID);
  }

  @Override
  public long saveCrpParameter(CrpParameter crpParameter) {

    return crpParameterDAO.save(crpParameter);
  }


}
