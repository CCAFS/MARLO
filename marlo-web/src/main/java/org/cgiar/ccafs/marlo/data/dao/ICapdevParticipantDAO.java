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


import org.cgiar.ccafs.marlo.data.model.CapdevParticipant;

import java.util.List;

public interface ICapdevParticipantDAO {

  /**
   * This method removes a specific capdevParticipant value from the database.
   * 
   * @param capdevParticipantId is the capdevParticipant identifier..
   */
  public void deleteCapdevParticipant(long capdevParticipantId);

  /**
   * This method validate if the capdevParticipant identify with the given id exists in the system.
   * 
   * @param capdevParticipantID is a capdevParticipant identifier.
   * @return true if the capdevParticipant exists, false otherwise.
   */
  public boolean existCapdevParticipant(long capdevParticipantID);

  /**
   * This method gets a capdevParticipant object by a given capdevParticipant identifier.
   * 
   * @param capdevParticipantID is the capdevParticipant identifier.
   * @return a CapdevParticipant object.
   */
  public CapdevParticipant find(long id);

  /**
   * This method gets a list of capdevParticipant that are active
   * 
   * @return a list from CapdevParticipant null if no exist records
   */
  public List<CapdevParticipant> findAll();


  /**
   * This method gets a list of capdevParticipants belongs of the user
   * 
   * @param userId - the user id
   * @return List of CapdevParticipants or null if the user is invalid or not have roles.
   */
  public List<CapdevParticipant> getCapdevParticipantsByUserId(long userId);

  /**
   * This method saves the information of the given capdevParticipant
   * 
   * @param capdevParticipant - is the capdevParticipant object with the new information to be added/updated.
   * @return a object.
   */
  public CapdevParticipant save(CapdevParticipant capdevParticipant);

  /**
   * This method saves the information of the given capdevParticipant
   * 
   * @param capdevParticipant - is the capdevParticipant object with the new information to be added/updated.
   * @return a object.
   */
  public CapdevParticipant save(CapdevParticipant capdevParticipant, String actionName, List<String> relationsName);
}
