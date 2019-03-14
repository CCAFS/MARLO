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

import org.cgiar.ccafs.marlo.data.model.RepIndMilestoneReason;

import java.util.List;


public interface RepIndMilestoneReasonDAO {

  /**
   * This method removes a specific repIndMilestoneReason value from the database.
   * 
   * @param repIndMilestoneReasonId is the repIndMilestoneReason identifier.
   * @return true if the repIndMilestoneReason was successfully deleted, false otherwise.
   */
  public void deleteRepIndMilestoneReason(long repIndMilestoneReasonId);

  /**
   * This method validate if the repIndMilestoneReason identify with the given id exists in the system.
   * 
   * @param repIndMilestoneReasonID is a repIndMilestoneReason identifier.
   * @return true if the repIndMilestoneReason exists, false otherwise.
   */
  public boolean existRepIndMilestoneReason(long repIndMilestoneReasonID);

  /**
   * This method gets a repIndMilestoneReason object by a given repIndMilestoneReason identifier.
   * 
   * @param repIndMilestoneReasonID is the repIndMilestoneReason identifier.
   * @return a RepIndMilestoneReason object.
   */
  public RepIndMilestoneReason find(long id);

  /**
   * This method gets a list of repIndMilestoneReason that are active
   * 
   * @return a list from RepIndMilestoneReason null if no exist records
   */
  public List<RepIndMilestoneReason> findAll();


  /**
   * This method saves the information of the given repIndMilestoneReason
   * 
   * @param repIndMilestoneReason - is the repIndMilestoneReason object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndMilestoneReason was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndMilestoneReason save(RepIndMilestoneReason repIndMilestoneReason);
}
