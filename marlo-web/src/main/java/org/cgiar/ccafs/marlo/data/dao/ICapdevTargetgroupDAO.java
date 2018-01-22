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


import org.cgiar.ccafs.marlo.data.model.CapdevTargetgroup;

import java.util.List;


public interface ICapdevTargetgroupDAO {

  /**
   * This method removes a specific capdevTargetgroup value from the database.
   * 
   * @param capdevTargetgroupId is the capdevTargetgroup identifier.
   */
  public void deleteCapdevTargetgroup(long capdevTargetgroupId);

  /**
   * This method validate if the capdevTargetgroup identify with the given id exists in the system.
   * 
   * @param capdevTargetgroupID is a capdevTargetgroup identifier.
   * @return true if the capdevTargetgroup exists, false otherwise.
   */
  public boolean existCapdevTargetgroup(long capdevTargetgroupID);

  /**
   * This method gets a capdevTargetgroup object by a given capdevTargetgroup identifier.
   * 
   * @param capdevTargetgroupID is the capdevTargetgroup identifier.
   * @return a CapdevTargetgroup object.
   */
  public CapdevTargetgroup find(long id);

  /**
   * This method gets a list of capdevTargetgroup that are active
   * 
   * @return a list from CapdevTargetgroup null if no exist records
   */
  public List<CapdevTargetgroup> findAll();


  /**
   * This method gets a list of capdevTargetgroups belongs of the user
   * 
   * @param userId - the user id
   * @return List of CapdevTargetgroups or null if the user is invalid or not have roles.
   */
  public List<CapdevTargetgroup> getCapdevTargetgroupsByUserId(long userId);

  /**
   * This method saves the information of the given capdevTargetgroup
   * 
   * @param capdevTargetgroup - is the capdevTargetgroup object with the new information to be added/updated.
   */
  public CapdevTargetgroup save(CapdevTargetgroup capdevTargetgroup);

  /**
   * This method saves the information of the given capdevTargetgroup
   * 
   * @param capdevTargetgroup - is the capdevTargetgroup object with the new information to be added/updated.
   * @return a object.
   */
  public CapdevTargetgroup save(CapdevTargetgroup capdevTargetgroup, String actionName, List<String> relationsName);
}
