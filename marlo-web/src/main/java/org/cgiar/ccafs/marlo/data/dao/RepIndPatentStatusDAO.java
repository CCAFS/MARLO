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

import org.cgiar.ccafs.marlo.data.model.RepIndPatentStatus;

import java.util.List;


public interface RepIndPatentStatusDAO {

  /**
   * This method removes a specific repIndPatentStatus value from the database.
   * 
   * @param repIndPatentStatusId is the repIndPatentStatus identifier.
   * @return true if the repIndPatentStatus was successfully deleted, false otherwise.
   */
  public void deleteRepIndPatentStatus(long repIndPatentStatusId);

  /**
   * This method validate if the repIndPatentStatus identify with the given id exists in the system.
   * 
   * @param repIndPatentStatusID is a repIndPatentStatus identifier.
   * @return true if the repIndPatentStatus exists, false otherwise.
   */
  public boolean existRepIndPatentStatus(long repIndPatentStatusID);

  /**
   * This method gets a repIndPatentStatus object by a given repIndPatentStatus identifier.
   * 
   * @param repIndPatentStatusID is the repIndPatentStatus identifier.
   * @return a RepIndPatentStatus object.
   */
  public RepIndPatentStatus find(long id);

  /**
   * This method gets a list of repIndPatentStatus that are active
   * 
   * @return a list from RepIndPatentStatus null if no exist records
   */
  public List<RepIndPatentStatus> findAll();


  /**
   * This method saves the information of the given repIndPatentStatus
   * 
   * @param repIndPatentStatus - is the repIndPatentStatus object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndPatentStatus was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndPatentStatus save(RepIndPatentStatus repIndPatentStatus);
}
