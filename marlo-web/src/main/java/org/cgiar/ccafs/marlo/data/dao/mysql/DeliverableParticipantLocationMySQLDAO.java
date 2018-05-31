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

import org.cgiar.ccafs.marlo.data.dao.DeliverableParticipantLocationDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipantLocation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableParticipantLocationMySQLDAO extends AbstractMarloDAO<DeliverableParticipantLocation, Long>
  implements DeliverableParticipantLocationDAO {


  @Inject
  public DeliverableParticipantLocationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableParticipantLocation(long deliverableParticipantLocationId) {
    DeliverableParticipantLocation deliverableParticipantLocation = this.find(deliverableParticipantLocationId);
    deliverableParticipantLocation.setActive(false);
    this.update(deliverableParticipantLocation);
  }

  @Override
  public boolean existDeliverableParticipantLocation(long deliverableParticipantLocationID) {
    DeliverableParticipantLocation deliverableParticipantLocation = this.find(deliverableParticipantLocationID);
    if (deliverableParticipantLocation == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableParticipantLocation find(long id) {
    return super.find(DeliverableParticipantLocation.class, id);

  }

  @Override
  public List<DeliverableParticipantLocation> findAll() {
    String query = "from " + DeliverableParticipantLocation.class.getName() + " where is_active=1";
    List<DeliverableParticipantLocation> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<DeliverableParticipantLocation> findParticipantLocationsByParticipant(long deliverableParticipantId) {
    String query = "from " + DeliverableParticipantLocation.class.getName() + " where deliverable_participant_id="
      + deliverableParticipantId + " and is_active=1";
    List<DeliverableParticipantLocation> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public DeliverableParticipantLocation save(DeliverableParticipantLocation deliverableParticipantLocation) {
    if (deliverableParticipantLocation.getId() == null) {
      super.saveEntity(deliverableParticipantLocation);
    } else {
      deliverableParticipantLocation = super.update(deliverableParticipantLocation);
    }


    return deliverableParticipantLocation;
  }


}