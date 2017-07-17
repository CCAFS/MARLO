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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.IParticipantDAO;
import org.cgiar.ccafs.marlo.data.manager.IParticipantService;
import org.cgiar.ccafs.marlo.data.model.Participant;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ParticipantService implements IParticipantService {


  private IParticipantDAO participantDAO;

  // Managers


  @Inject
  public ParticipantService(IParticipantDAO participantDAO) {
    this.participantDAO = participantDAO;


  }

  @Override
  public boolean deleteParticipant(long participantId) {

    return participantDAO.deleteParticipant(participantId);
  }

  @Override
  public boolean existParticipant(long participantID) {

    return participantDAO.existParticipant(participantID);
  }

  @Override
  public List<Participant> findAll() {

    return participantDAO.findAll();

  }

  @Override
  public Participant getParticipantById(long participantID) {

    return participantDAO.find(participantID);
  }

  @Override
  public List<Participant> getParticipantsByUserId(Long userId) {
    return participantDAO.getParticipantsByUserId(userId);
  }

  @Override
  public long saveParticipant(Participant participant) {

    return participantDAO.save(participant);
  }

  @Override
  public long saveParticipant(Participant participant, String actionName, List<String> relationsName) {
    return participantDAO.save(participant, actionName, relationsName);
  }


}
