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


import org.cgiar.ccafs.marlo.data.dao.RepIndLandUseTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndLandUseTypeManager;
import org.cgiar.ccafs.marlo.data.model.RepIndLandUseType;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class RepIndLandUseTypeManagerImpl implements RepIndLandUseTypeManager {


  private RepIndLandUseTypeDAO repIndLandUseTypeDAO;
  // Managers


  @Inject
  public RepIndLandUseTypeManagerImpl(RepIndLandUseTypeDAO repIndLandUseTypeDAO) {
    this.repIndLandUseTypeDAO = repIndLandUseTypeDAO;


  }

  @Override
  public void deleteRepIndLandUseType(long repIndLandUseTypeId) {

    repIndLandUseTypeDAO.deleteRepIndLandUseType(repIndLandUseTypeId);
  }

  @Override
  public boolean existRepIndLandUseType(long repIndLandUseTypeID) {

    return repIndLandUseTypeDAO.existRepIndLandUseType(repIndLandUseTypeID);
  }

  @Override
  public List<RepIndLandUseType> findAll() {

    return repIndLandUseTypeDAO.findAll();

  }

  @Override
  public RepIndLandUseType getRepIndLandUseTypeById(long repIndLandUseTypeID) {

    return repIndLandUseTypeDAO.find(repIndLandUseTypeID);
  }

  @Override
  public RepIndLandUseType saveRepIndLandUseType(RepIndLandUseType repIndLandUseType) {

    return repIndLandUseTypeDAO.save(repIndLandUseType);
  }


}
