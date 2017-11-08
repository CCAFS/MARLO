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
import org.hibernate.SessionFactory;

public class ParticipantDAO extends AbstractMarloDAO<Participant, Long> implements IParticipantDAO {


  @Inject
  public ParticipantDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteParticipant(long participantId) {
    Participant participant = this.find(participantId);
    participant.setActive(false);
    this.save(participant);
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
    return super.find(Participant.class, id);

  }

  @Override
  public List<Participant> findAll() {
    String query = "from " + Participant.class.getName();
    List<Participant> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<Participant> getParticipantsByUserId(long userId) {
    String query = "from " + Participant.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public Participant save(Participant participant) {
    if (participant.getId() == null) {
      super.saveEntity(participant);
    } else {
      super.update(participant);
    }
    return participant;
  }

  @Override
  public Participant save(Participant participant, String actionName, List<String> relationsName) {
    if (participant.getId() == null) {
      super.saveEntity(participant, actionName, relationsName);
    } else {
      super.update(participant, actionName, relationsName);
    }
    return participant;
  }


}