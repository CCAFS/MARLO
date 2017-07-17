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


package org.cgiar.ccafs.marlo.data.dao;


import org.cgiar.ccafs.marlo.data.dao.mysql.CapacityDevelopmentDAO;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CapacityDevelopmentDAO.class)
public interface ICapacityDevelopmentDAO {

  /**
   * This method removes a specific capacityDevelopment value from the database.
   * 
   * @param capacityDevelopmentId is the capacityDevelopment identifier.
   * @return true if the capacityDevelopment was successfully deleted, false otherwise.
   */
  public boolean deleteCapacityDevelopment(long capacityDevelopmentId);

  /**
   * This method validate if the capacityDevelopment identify with the given id exists in the system.
   * 
   * @param capacityDevelopmentID is a capacityDevelopment identifier.
   * @return true if the capacityDevelopment exists, false otherwise.
   */
  public boolean existCapacityDevelopment(long capacityDevelopmentID);

  /**
   * This method gets a capacityDevelopment object by a given capacityDevelopment identifier.
   * 
   * @param capacityDevelopmentID is the capacityDevelopment identifier.
   * @return a CapacityDevelopment object.
   */
  public CapacityDevelopment find(long id);

  /**
   * This method gets a list of capacityDevelopment that are active
   * 
   * @return a list from CapacityDevelopment null if no exist records
   */
  public List<CapacityDevelopment> findAll();


  /**
   * This method gets a list of capacityDevelopments belongs of the user
   * 
   * @param userId - the user id
   * @return List of CapacityDevelopments or null if the user is invalid or not have roles.
   */
  public List<CapacityDevelopment> getCapacityDevelopmentsByUserId(long userId);

  /**
   * This method saves the information of the given capacityDevelopment
   * 
   * @param capacityDevelopment - is the capacityDevelopment object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the capacityDevelopment was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CapacityDevelopment capacityDevelopment);

  /**
   * This method saves the information of the given capacityDevelopment
   * 
   * @param capacityDevelopment - is the capacityDevelopment object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the capacityDevelopment was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CapacityDevelopment capacityDevelopment, String actionName, List<String> relationsName);
}
