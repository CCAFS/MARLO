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

import org.cgiar.ccafs.marlo.data.dao.DeliverableCrossCuttingMarkerDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrossCuttingMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableCrossCuttingMarkerMySQLDAO extends AbstractMarloDAO<DeliverableCrossCuttingMarker, Long>
  implements DeliverableCrossCuttingMarkerDAO {


  @Inject
  public DeliverableCrossCuttingMarkerMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableCrossCuttingMarker(long deliverableCrossCuttingMarkerId) {
    DeliverableCrossCuttingMarker deliverableCrossCuttingMarker = this.find(deliverableCrossCuttingMarkerId);
    this.update(deliverableCrossCuttingMarker);
  }

  @Override
  public boolean existDeliverableCrossCuttingMarker(long deliverableCrossCuttingMarkerID) {
    DeliverableCrossCuttingMarker deliverableCrossCuttingMarker = this.find(deliverableCrossCuttingMarkerID);
    if (deliverableCrossCuttingMarker == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableCrossCuttingMarker find(long id) {
    return super.find(DeliverableCrossCuttingMarker.class, id);

  }

  @Override
  public List<DeliverableCrossCuttingMarker> findAll() {
    String query = "from " + DeliverableCrossCuttingMarker.class.getName() + " where is_active=1";
    List<DeliverableCrossCuttingMarker> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableCrossCuttingMarker getDeliverableCrossCountryMarkerId(long deliverableID,
    long cgiarCrossCuttingMarkerID, long phaseID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT id as markerId FROM deliverable_cross_cutting_markers ");
    query.append("WHERE deliverable_id = ");
    query.append(deliverableID);
    query.append(" AND cgiar_cross_cutting_marker_id = ");
    query.append(cgiarCrossCuttingMarkerID);
    query.append(" AND id_phase = ");
    query.append(phaseID);
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<DeliverableCrossCuttingMarker> deliverableCrossCuttingMarkers = new ArrayList<DeliverableCrossCuttingMarker>();
    for (Map<String, Object> map : list) {
      String markerId = map.get("markerId").toString();
      long longMarkerId = Long.parseLong(markerId);
      DeliverableCrossCuttingMarker deliverableCrossCuttingMarker = this.find(longMarkerId);
      deliverableCrossCuttingMarkers.add(deliverableCrossCuttingMarker);
    }
    if (deliverableCrossCuttingMarkers.size() > 0) {
      return deliverableCrossCuttingMarkers.get(0);
    } else {
      return null;
    }

  }


  @Override
  public DeliverableCrossCuttingMarker save(DeliverableCrossCuttingMarker deliverableCrossCuttingMarker) {
    if (deliverableCrossCuttingMarker.getId() == null) {
      super.saveEntity(deliverableCrossCuttingMarker);
    } else {
      deliverableCrossCuttingMarker = super.update(deliverableCrossCuttingMarker);
    }


    return deliverableCrossCuttingMarker;
  }


}