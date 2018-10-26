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

import org.cgiar.ccafs.marlo.data.dao.RepIndTypeParticipantDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndTypeParticipant;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndTypeParticipantMySQLDAO extends AbstractMarloDAO<RepIndTypeParticipant, Long>
  implements RepIndTypeParticipantDAO {


  @Inject
  public RepIndTypeParticipantMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndTypeParticipant(long repIndTypeParticipantId) {
    RepIndTypeParticipant repIndTypeParticipant = this.find(repIndTypeParticipantId);
    this.delete(repIndTypeParticipant);
  }

  @Override
  public boolean existRepIndTypeParticipant(long repIndTypeParticipantID) {
    RepIndTypeParticipant repIndTypeParticipant = this.find(repIndTypeParticipantID);
    if (repIndTypeParticipant == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndTypeParticipant find(long id) {
    return super.find(RepIndTypeParticipant.class, id);

  }

  @Override
  public List<RepIndTypeParticipant> findAll() {
    String query = "from " + RepIndTypeParticipant.class.getName();
    List<RepIndTypeParticipant> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndTypeParticipant save(RepIndTypeParticipant repIndTypeParticipant) {
    if (repIndTypeParticipant.getId() == null) {
      super.saveEntity(repIndTypeParticipant);
    } else {
      repIndTypeParticipant = super.update(repIndTypeParticipant);
    }


    return repIndTypeParticipant;
  }


}