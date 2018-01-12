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

import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface CrpClusterActivityLeaderManager {


  /**
   * This method removes a specific crpClusterActivityLeader value from the database.
   * 
   * @param crpClusterActivityLeaderId is the crpClusterActivityLeader identifier.
   * @return true if the crpClusterActivityLeader was successfully deleted, false otherwise.
   */
  public void deleteCrpClusterActivityLeader(long crpClusterActivityLeaderId);


  /**
   * This method validate if the crpClusterActivityLeader identify with the given id exists in the system.
   * 
   * @param crpClusterActivityLeaderID is a crpClusterActivityLeader identifier.
   * @return true if the crpClusterActivityLeader exists, false otherwise.
   */
  public boolean existCrpClusterActivityLeader(long crpClusterActivityLeaderID);


  /**
   * This method gets a list of crpClusterActivityLeader that are active
   * 
   * @return a list from CrpClusterActivityLeader null if no exist records
   */
  public List<CrpClusterActivityLeader> findAll();


  /**
   * This method gets a crpClusterActivityLeader object by a given crpClusterActivityLeader identifier.
   * 
   * @param crpClusterActivityLeaderID is the crpClusterActivityLeader identifier.
   * @return a CrpClusterActivityLeader object.
   */
  public CrpClusterActivityLeader getCrpClusterActivityLeaderById(long crpClusterActivityLeaderID);

  /**
   * This method saves the information of the given crpClusterActivityLeader
   * 
   * @param crpClusterActivityLeader - is the crpClusterActivityLeader object with the
   *        new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database,
   *         0 if the crpClusterActivityLeader was updated
   *         or -1 is some error occurred.
   */
  public CrpClusterActivityLeader saveCrpClusterActivityLeader(CrpClusterActivityLeader crpClusterActivityLeader);


}
