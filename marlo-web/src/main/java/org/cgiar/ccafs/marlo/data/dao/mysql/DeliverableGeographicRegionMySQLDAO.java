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

import org.cgiar.ccafs.marlo.data.dao.DeliverableGeographicRegionDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableGeographicRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableGeographicRegionMySQLDAO extends AbstractMarloDAO<DeliverableGeographicRegion, Long>
  implements DeliverableGeographicRegionDAO {


  @Inject
  public DeliverableGeographicRegionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableGeographicRegion(long deliverableGeographicRegionId) {
    DeliverableGeographicRegion deliverableGeographicRegion = this.find(deliverableGeographicRegionId);

    this.delete(deliverableGeographicRegion);
  }

  @Override
  public boolean existDeliverableGeographicRegion(long deliverableGeographicRegionID) {
    DeliverableGeographicRegion deliverableGeographicRegion = this.find(deliverableGeographicRegionID);
    if (deliverableGeographicRegion == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableGeographicRegion find(long id) {
    return super.find(DeliverableGeographicRegion.class, id);

  }

  @Override
  public List<DeliverableGeographicRegion> findAll() {
    String query = "from " + DeliverableGeographicRegion.class.getName();
    List<DeliverableGeographicRegion> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<DeliverableGeographicRegion> getDeliverableGeographicRegionbyPhase(long deliverableID, long phaseID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT deliverable_geographic_regions.id as deliverableLocationID FROM deliverables ");
    query.append(
      "INNER JOIN deliverable_geographic_regions ON deliverable_geographic_regions.deliverable_id = deliverables.id ");
    query.append("INNER JOIN phases ON deliverable_geographic_regions.id_phase = phases.id ");
    query.append("WHERE deliverables.id = ");
    query.append(deliverableID);
    query.append(" AND phases.id = ");
    query.append(phaseID);
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<DeliverableGeographicRegion> deliverableLocations = new ArrayList<DeliverableGeographicRegion>();
    for (Map<String, Object> map : list) {
      String deliverableLocationID = map.get("deliverableLocationID").toString();
      long longDeliverableLocationID = Long.parseLong(deliverableLocationID);
      DeliverableGeographicRegion deliverableLocation = this.find(longDeliverableLocationID);
      deliverableLocations.add(deliverableLocation);
    }
    return deliverableLocations;
  }

  @Override
  public DeliverableGeographicRegion save(DeliverableGeographicRegion deliverableGeographicRegion) {
    if (deliverableGeographicRegion.getId() == null) {
      super.saveEntity(deliverableGeographicRegion);
    } else {
      deliverableGeographicRegion = super.update(deliverableGeographicRegion);
    }


    return deliverableGeographicRegion;
  }


}