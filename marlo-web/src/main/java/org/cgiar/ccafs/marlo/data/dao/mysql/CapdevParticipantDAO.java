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

import org.cgiar.ccafs.marlo.data.dao.ICapdevParticipantDAO;
import org.cgiar.ccafs.marlo.data.model.CapdevParticipant;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CapdevParticipantDAO extends AbstractMarloDAO<CapdevParticipant, Long> implements ICapdevParticipantDAO {


  @Inject
  public CapdevParticipantDAO(SessionFactory sessionFactory) {
    super(sessionFactory);

  }

  @Override
  public void deleteCapdevParticipant(long capdevParticipantId) {
    CapdevParticipant capdevParticipant = this.find(capdevParticipantId);
    capdevParticipant.setActive(false);
    this.save(capdevParticipant);
  }

  @Override
  public boolean existCapdevParticipant(long capdevParticipantID) {
    CapdevParticipant capdevParticipant = this.find(capdevParticipantID);
    if (capdevParticipant == null) {
      return false;
    }
    return true;

  }

  @Override
  public CapdevParticipant find(long id) {
    return super.find(CapdevParticipant.class, id);

  }

  @Override
  public List<CapdevParticipant> findAll() {
    String query = "from " + CapdevParticipant.class.getName();
    List<CapdevParticipant> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CapdevParticipant> getCapdevParticipantsByUserId(long userId) {
    String query = "from " + CapdevParticipant.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CapdevParticipant save(CapdevParticipant capdevParticipant) {
    if (capdevParticipant.getId() == null) {
      super.saveEntity(capdevParticipant);
    } else {
      super.update(capdevParticipant);
    }
    return capdevParticipant;
  }

  @Override
  public CapdevParticipant save(CapdevParticipant capdevParticipant, String actionName, List<String> relationsName) {
    if (capdevParticipant.getId() == null) {
      super.saveEntity(capdevParticipant, actionName, relationsName);
    } else {
      super.update(capdevParticipant, actionName, relationsName);
    }
    return capdevParticipant;
  }


}