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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.IParticipantDAO;
import org.cgiar.ccafs.marlo.data.model.Participant;

import java.util.List;

import com.google.inject.Inject;

public class ParticipantDAO implements IParticipantDAO {

  private StandardDAO dao;

  @Inject
  public ParticipantDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteParticipant(long participantId) {
    Participant participant = this.find(participantId);
    participant.setActive(false);
    return this.save(participant) > 0;
  }

  @Override
  public boolean existParticipant(long participantID) {
    Participant participant = this.find(participantID);
    if (participant == null) {
      return false;
    }
    return true;

  }

  @Override
  public Participant find(long id) {
    return dao.find(Participant.class, id);

  }

  @Override
  public List<Participant> findAll() {
    String query = "from " + Participant.class.getName();
    List<Participant> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<Participant> getParticipantsByUserId(long userId) {
    String query = "from " + Participant.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(Participant participant) {
    if (participant.getId() == null) {
      dao.save(participant);
    } else {
      dao.update(participant);
    }
    return participant.getId();
  }

  @Override
  public long save(Participant participant, String actionName, List<String> relationsName) {
    if (participant.getId() == null) {
      dao.save(participant, actionName, relationsName);
    } else {
      dao.update(participant, actionName, relationsName);
    }
    return participant.getId();
  }


}