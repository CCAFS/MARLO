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

import org.cgiar.ccafs.marlo.data.dao.DeliverableParticipantDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipant;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableParticipantMySQLDAO extends AbstractMarloDAO<DeliverableParticipant, Long> implements DeliverableParticipantDAO {


  @Inject
  public DeliverableParticipantMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableParticipant(long deliverableParticipantId) {
    DeliverableParticipant deliverableParticipant = this.find(deliverableParticipantId);
    deliverableParticipant.setActive(false);
    this.update(deliverableParticipant);
  }

  @Override
  public boolean existDeliverableParticipant(long deliverableParticipantID) {
    DeliverableParticipant deliverableParticipant = this.find(deliverableParticipantID);
    if (deliverableParticipant == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableParticipant find(long id) {
    return super.find(DeliverableParticipant.class, id);

  }

  @Override
  public List<DeliverableParticipant> findAll() {
    String query = "from " + DeliverableParticipant.class.getName() + " where is_active=1";
    List<DeliverableParticipant> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableParticipant save(DeliverableParticipant deliverableParticipant) {
    if (deliverableParticipant.getId() == null) {
      super.saveEntity(deliverableParticipant);
    } else {
      deliverableParticipant = super.update(deliverableParticipant);
    }


    return deliverableParticipant;
  }


}