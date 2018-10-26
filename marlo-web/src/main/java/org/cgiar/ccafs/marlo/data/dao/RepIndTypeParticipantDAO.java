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

import org.cgiar.ccafs.marlo.data.model.RepIndTypeParticipant;

import java.util.List;


public interface RepIndTypeParticipantDAO {

  /**
   * This method removes a specific repIndTypeParticipant value from the database.
   * 
   * @param repIndTypeParticipantId is the repIndTypeParticipant identifier.
   * @return true if the repIndTypeParticipant was successfully deleted, false otherwise.
   */
  public void deleteRepIndTypeParticipant(long repIndTypeParticipantId);

  /**
   * This method validate if the repIndTypeParticipant identify with the given id exists in the system.
   * 
   * @param repIndTypeParticipantID is a repIndTypeParticipant identifier.
   * @return true if the repIndTypeParticipant exists, false otherwise.
   */
  public boolean existRepIndTypeParticipant(long repIndTypeParticipantID);

  /**
   * This method gets a repIndTypeParticipant object by a given repIndTypeParticipant identifier.
   * 
   * @param repIndTypeParticipantID is the repIndTypeParticipant identifier.
   * @return a RepIndTypeParticipant object.
   */
  public RepIndTypeParticipant find(long id);

  /**
   * This method gets a list of repIndTypeParticipant that are active
   * 
   * @return a list from RepIndTypeParticipant null if no exist records
   */
  public List<RepIndTypeParticipant> findAll();


  /**
   * This method saves the information of the given repIndTypeParticipant
   * 
   * @param repIndTypeParticipant - is the repIndTypeParticipant object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndTypeParticipant was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndTypeParticipant save(RepIndTypeParticipant repIndTypeParticipant);
}
