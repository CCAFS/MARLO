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
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.manager.impl.CapacityDevelopmentTypeService;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopmentType;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CapacityDevelopmentTypeService.class)
public interface ICapacityDevelopmentTypeService {


  /**
   * This method removes a specific capacityDevelopmentType value from the database.
   * 
   * @param capacityDevelopmentTypeId is the capacityDevelopmentType identifier.
   */
  public void deleteCapacityDevelopmentType(long capacityDevelopmentTypeId);


  /**
   * This method validate if the capacityDevelopmentType identify with the given id exists in the system.
   * 
   * @param capacityDevelopmentTypeID is a capacityDevelopmentType identifier.
   * @return true if the capacityDevelopmentType exists, false otherwise.
   */
  public boolean existCapacityDevelopmentType(long capacityDevelopmentTypeID);


  /**
   * This method gets a list of capacityDevelopmentType that are active
   * 
   * @return a list from CapacityDevelopmentType null if no exist records
   */
  public List<CapacityDevelopmentType> findAll();


  /**
   * This method gets a capacityDevelopmentType object by a given capacityDevelopmentType identifier.
   * 
   * @param capacityDevelopmentTypeID is the capacityDevelopmentType identifier.
   * @return a CapacityDevelopmentType object.
   */
  public CapacityDevelopmentType getCapacityDevelopmentTypeById(long capacityDevelopmentTypeID);

  /**
   * This method gets a list of capacityDevelopmentTypes belongs of the user
   * 
   * @param userId - the user id
   * @return List of CapacityDevelopmentTypes or null if the user is invalid or not have roles.
   */
  public List<CapacityDevelopmentType> getCapacityDevelopmentTypesByUserId(Long userId);

  /**
   * This method saves the information of the given capacityDevelopmentType
   * 
   * @param capacityDevelopmentType - is the capacityDevelopmentType object with the new information to be
   *        added/updated.
   * @return a object.
   */
  public CapacityDevelopmentType saveCapacityDevelopmentType(CapacityDevelopmentType capacityDevelopmentType);

  /**
   * This method saves the information of the given capacityDevelopmentType
   * 
   * @param capacityDevelopmentType - is the capacityDevelopmentType object with the new information to be
   *        added/updated.
   * @return a object.
   */
  public CapacityDevelopmentType saveCapacityDevelopmentType(CapacityDevelopmentType capacityDevelopmentType,
    String actionName, List<String> relationsName);


}
