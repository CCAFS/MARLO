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


import org.cgiar.ccafs.marlo.data.dao.mysql.CapdevDisciplineDAO;
import org.cgiar.ccafs.marlo.data.model.CapdevDiscipline;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CapdevDisciplineDAO.class)
public interface ICapdevDisciplineDAO {

  /**
   * This method removes a specific capdevDiscipline value from the database.
   * 
   * @param capdevDisciplineId is the capdevDiscipline identifier.
   * @return true if the capdevDiscipline was successfully deleted, false otherwise.
   */
  public void deleteCapdevDiscipline(long capdevDisciplineId);

  /**
   * This method validate if the capdevDiscipline identify with the given id exists in the system.
   * 
   * @param capdevDisciplineID is a capdevDiscipline identifier.
   */
  public boolean existCapdevDiscipline(long capdevDisciplineID);

  /**
   * This method gets a capdevDiscipline object by a given capdevDiscipline identifier.
   * 
   * @param capdevDisciplineID is the capdevDiscipline identifier.
   * @return a CapdevDiscipline object.
   */
  public CapdevDiscipline find(long id);

  /**
   * This method gets a list of capdevDiscipline that are active
   * 
   * @return a list from CapdevDiscipline null if no exist records
   */
  public List<CapdevDiscipline> findAll();


  /**
   * This method gets a list of capdevDisciplines belongs of the user
   * 
   * @param userId - the user id
   * @return List of CapdevDisciplines or null if the user is invalid or not have roles.
   */
  public List<CapdevDiscipline> getCapdevDisciplinesByUserId(long userId);

  /**
   * This method saves the information of the given capdevDiscipline
   * 
   * @param capdevDiscipline - is the capdevDiscipline object with the new information to be added/updated.
   * @return a object.
   */
  public CapdevDiscipline save(CapdevDiscipline capdevDiscipline);

  /**
   * This method saves the information of the given capdevDiscipline
   * 
   * @param capdevDiscipline - is the capdevDiscipline object with the new information to be added/updated.
   * @return a object.
   */
  public CapdevDiscipline save(CapdevDiscipline capdevDiscipline, String actionName, List<String> relationsName);
}
