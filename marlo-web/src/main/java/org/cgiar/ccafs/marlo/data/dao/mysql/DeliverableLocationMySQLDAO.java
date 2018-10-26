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

import org.cgiar.ccafs.marlo.data.dao.DeliverableLocationDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableLocationMySQLDAO extends AbstractMarloDAO<DeliverableLocation, Long>
  implements DeliverableLocationDAO {


  @Inject
  public DeliverableLocationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableLocation(long deliverableLocationId) {
    DeliverableLocation deliverableLocation = this.find(deliverableLocationId);
    this.delete(deliverableLocation);
  }

  @Override
  public boolean existDeliverableLocation(long deliverableLocationID) {
    DeliverableLocation deliverableLocation = this.find(deliverableLocationID);
    if (deliverableLocation == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableLocation find(long id) {
    return super.find(DeliverableLocation.class, id);

  }

  @Override
  public List<DeliverableLocation> findAll() {
    String query = "from " + DeliverableLocation.class.getName() + " where is_active=1";
    List<DeliverableLocation> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<DeliverableLocation> getDeliverableLocationbyPhase(long deliverableID, long phaseID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT deliverable_locations.id as deliverableLocationID FROM deliverables ");
    query.append("INNER JOIN deliverable_locations ON deliverable_locations.deliverable_id = deliverables.id ");
    query.append("INNER JOIN phases ON deliverable_locations.id_phase = phases.id ");
    query.append("WHERE deliverables.id = ");
    query.append(deliverableID);
    query.append(" AND phases.id = ");
    query.append(phaseID);
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<DeliverableLocation> deliverableLocations = new ArrayList<DeliverableLocation>();
    for (Map<String, Object> map : list) {
      String deliverableLocationID = map.get("deliverableLocationID").toString();
      long longDeliverableLocationID = Long.parseLong(deliverableLocationID);
      DeliverableLocation deliverableLocation = this.find(longDeliverableLocationID);
      deliverableLocations.add(deliverableLocation);
    }
    return deliverableLocations;
  }

  @Override
  public DeliverableLocation save(DeliverableLocation deliverableLocation) {
    if (deliverableLocation.getId() == null) {
      super.saveEntity(deliverableLocation);
    } else {
      deliverableLocation = super.update(deliverableLocation);
    }


    return deliverableLocation;
  }

}