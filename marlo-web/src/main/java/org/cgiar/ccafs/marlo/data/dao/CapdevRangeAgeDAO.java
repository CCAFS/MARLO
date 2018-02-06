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

import org.cgiar.ccafs.marlo.data.model.CapdevRangeAge;

import java.util.List;


public interface CapdevRangeAgeDAO {

  /**
   * This method removes a specific capdevRangeAge value from the database.
   * 
   * @param capdevRangeAgeId is the capdevRangeAge identifier.
   * @return true if the capdevRangeAge was successfully deleted, false otherwise.
   */
  public void deleteCapdevRangeAge(long capdevRangeAgeId);

  /**
   * This method validate if the capdevRangeAge identify with the given id exists in the system.
   * 
   * @param capdevRangeAgeID is a capdevRangeAge identifier.
   * @return true if the capdevRangeAge exists, false otherwise.
   */
  public boolean existCapdevRangeAge(long capdevRangeAgeID);

  /**
   * This method gets a capdevRangeAge object by a given capdevRangeAge identifier.
   * 
   * @param capdevRangeAgeID is the capdevRangeAge identifier.
   * @return a CapdevRangeAge object.
   */
  public CapdevRangeAge find(long id);

  /**
   * This method gets a list of capdevRangeAge that are active
   * 
   * @return a list from CapdevRangeAge null if no exist records
   */
  public List<CapdevRangeAge> findAll();


  /**
   * This method saves the information of the given capdevRangeAge
   * 
   * @param capdevRangeAge - is the capdevRangeAge object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the capdevRangeAge was
   *         updated
   *         or -1 is some error occurred.
   */
  public CapdevRangeAge save(CapdevRangeAge capdevRangeAge);
}
