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


import org.cgiar.ccafs.marlo.data.dao.mysql.CapdevOutputsDAO;
import org.cgiar.ccafs.marlo.data.model.CapdevOutputs;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CapdevOutputsDAO.class)
public interface ICapdevOutputsDAO {

  /**
   * This method removes a specific capdevOutputs value from the database.
   * 
   * @param capdevOutputsId is the capdevOutputs identifier.
   * @return true if the capdevOutputs was successfully deleted, false otherwise.
   */
  public boolean deleteCapdevOutputs(long capdevOutputsId);

  /**
   * This method validate if the capdevOutputs identify with the given id exists in the system.
   * 
   * @param capdevOutputsID is a capdevOutputs identifier.
   * @return true if the capdevOutputs exists, false otherwise.
   */
  public boolean existCapdevOutputs(long capdevOutputsID);

  /**
   * This method gets a capdevOutputs object by a given capdevOutputs identifier.
   * 
   * @param capdevOutputsID is the capdevOutputs identifier.
   * @return a CapdevOutputs object.
   */
  public CapdevOutputs find(long id);

  /**
   * This method gets a list of capdevOutputs that are active
   * 
   * @return a list from CapdevOutputs null if no exist records
   */
  public List<CapdevOutputs> findAll();


  /**
   * This method gets a list of capdevOutputss belongs of the user
   * 
   * @param userId - the user id
   * @return List of CapdevOutputss or null if the user is invalid or not have roles.
   */
  public List<CapdevOutputs> getCapdevOutputssByUserId(long userId);

  /**
   * This method saves the information of the given capdevOutputs
   * 
   * @param capdevOutputs - is the capdevOutputs object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the capdevOutputs was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CapdevOutputs capdevOutputs);

  /**
   * This method saves the information of the given capdevOutputs
   * 
   * @param capdevOutputs - is the capdevOutputs object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the capdevOutputs was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CapdevOutputs capdevOutputs, String actionName, List<String> relationsName);
}
