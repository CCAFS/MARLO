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


import org.cgiar.ccafs.marlo.data.dao.ICapacityDevelopmentTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentTypeService;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopmentType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * @author Christian Garcia
 */
@Named
public class CapacityDevelopmentTypeService implements ICapacityDevelopmentTypeService {


  private ICapacityDevelopmentTypeDAO capacityDevelopmentTypeDAO;

  // Managers


  @Inject
  public CapacityDevelopmentTypeService(ICapacityDevelopmentTypeDAO capacityDevelopmentTypeDAO) {
    this.capacityDevelopmentTypeDAO = capacityDevelopmentTypeDAO;


  }

  @Override
  public void deleteCapacityDevelopmentType(long capacityDevelopmentTypeId) {

    capacityDevelopmentTypeDAO.deleteCapacityDevelopmentType(capacityDevelopmentTypeId);
  }

  @Override
  public boolean existCapacityDevelopmentType(long capacityDevelopmentTypeID) {

    return capacityDevelopmentTypeDAO.existCapacityDevelopmentType(capacityDevelopmentTypeID);
  }

  @Override
  public List<CapacityDevelopmentType> findAll() {

    return capacityDevelopmentTypeDAO.findAll();

  }

  @Override
  public CapacityDevelopmentType getCapacityDevelopmentTypeById(long capacityDevelopmentTypeID) {

    return capacityDevelopmentTypeDAO.find(capacityDevelopmentTypeID);
  }

  @Override
  public List<CapacityDevelopmentType> getCapacityDevelopmentTypesByUserId(Long userId) {
    return capacityDevelopmentTypeDAO.getCapacityDevelopmentTypesByUserId(userId);
  }

  @Override
  public CapacityDevelopmentType saveCapacityDevelopmentType(CapacityDevelopmentType capacityDevelopmentType) {

    return capacityDevelopmentTypeDAO.save(capacityDevelopmentType);
  }

  @Override
  public CapacityDevelopmentType saveCapacityDevelopmentType(CapacityDevelopmentType capacityDevelopmentType,
    String actionName, List<String> relationsName) {
    return capacityDevelopmentTypeDAO.save(capacityDevelopmentType, actionName, relationsName);
  }


}
