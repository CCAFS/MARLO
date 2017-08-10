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

import org.cgiar.ccafs.marlo.data.manager.impl.CapdevHighestDegreeManagerImpl;
import org.cgiar.ccafs.marlo.data.model.CapdevHighestDegree;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CapdevHighestDegreeManagerImpl.class)
public interface CapdevHighestDegreeManager {


  /**
   * This method removes a specific capdevHighestDegree value from the database.
   * 
   * @param capdevHighestDegreeId is the capdevHighestDegree identifier.
   * @return true if the capdevHighestDegree was successfully deleted, false otherwise.
   */
  public boolean deleteCapdevHighestDegree(long capdevHighestDegreeId);


  /**
   * This method validate if the capdevHighestDegree identify with the given id exists in the system.
   * 
   * @param capdevHighestDegreeID is a capdevHighestDegree identifier.
   * @return true if the capdevHighestDegree exists, false otherwise.
   */
  public boolean existCapdevHighestDegree(long capdevHighestDegreeID);


  /**
   * This method gets a list of capdevHighestDegree that are active
   * 
   * @return a list from CapdevHighestDegree null if no exist records
   */
  public List<CapdevHighestDegree> findAll();


  /**
   * This method gets a capdevHighestDegree object by a given capdevHighestDegree identifier.
   * 
   * @param capdevHighestDegreeID is the capdevHighestDegree identifier.
   * @return a CapdevHighestDegree object.
   */
  public CapdevHighestDegree getCapdevHighestDegreeById(long capdevHighestDegreeID);

  /**
   * This method saves the information of the given capdevHighestDegree
   * 
   * @param capdevHighestDegree - is the capdevHighestDegree object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the capdevHighestDegree was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveCapdevHighestDegree(CapdevHighestDegree capdevHighestDegree);


}
