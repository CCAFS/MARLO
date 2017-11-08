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

import org.cgiar.ccafs.marlo.data.manager.impl.ParticipantService;
import org.cgiar.ccafs.marlo.data.model.Participant;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(ParticipantService.class)
public interface IParticipantService {


  /**
   * This method removes a specific participant value from the database.
   * 
   * @param participantId is the participant identifier.
   */
  public void deleteParticipant(long participantId);


  /**
   * This method validate if the participant identify with the given id exists in the system.
   * 
   * @param participantID is a participant identifier.
   * @return true if the participant exists, false otherwise.
   */
  public boolean existParticipant(long participantID);


  /**
   * This method gets a list of participant that are active
   * 
   * @return a list from Participant null if no exist records
   */
  public List<Participant> findAll();


  /**
   * This method gets a participant object by a given participant identifier.
   * 
   * @param participantID is the participant identifier.
   * @return a Participant object.
   */
  public Participant getParticipantById(long participantID);

  /**
   * This method gets a list of participants belongs of the user
   * 
   * @param userId - the user id
   * @return List of Participants or null if the user is invalid or not have roles.
   */
  public List<Participant> getParticipantsByUserId(Long userId);

  /**
   * This method saves the information of the given participant
   * 
   * @param participant - is the participant object with the new information to be added/updated.
   * @return a object.
   */
  public Participant saveParticipant(Participant participant);

  /**
   * This method saves the information of the given participant
   * 
   * @param participant - is the participant object with the new information to be added/updated.
   * @return a object
   */
  public Participant saveParticipant(Participant participant, String actionName, List<String> relationsName);


}
