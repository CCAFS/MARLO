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

import org.cgiar.ccafs.marlo.data.dao.DeliverableDAO;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableMySQLDAO extends AbstractMarloDAO<Deliverable, Long> implements DeliverableDAO {


  @Inject
  public DeliverableMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverable(long deliverableId) {
    Deliverable deliverable = this.find(deliverableId);
    deliverable.setActive(false);
    this.save(deliverable);
  }

  @Override
  public boolean existDeliverable(long deliverableID) {
    Deliverable deliverable = this.find(deliverableID);
    if (deliverable == null) {
      return false;
    }
    return true;

  }

  @Override
  public Deliverable find(long id) {
    return super.find(Deliverable.class, id);

  }

  @Override
  public List<Deliverable> findAll() {
    String query = "from " + Deliverable.class.getName() + " where is_active=1";
    List<Deliverable> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<Deliverable> getDeliverablesByPhase(long phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("di.id as id ");
    query.append("FROM ");
    query.append("deliverables AS d ");
    query.append("INNER JOIN deliverables_info AS di ON d.id = di.deliverable_id ");
    query.append("WHERE d.is_active = 1 AND (d.is_publication IS NULL OR d.is_publication = 0) AND ");
    query.append("di.is_active = 1 AND ");
    query.append("di.`id_phase` =" + phase);

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<Deliverable> deliverables = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        Deliverable deliverable = this.find(Long.parseLong(map.get("id").toString()));
        deliverables.add(deliverable);
      }
    }

    return deliverables;
  }

  @Override
  public Deliverable save(Deliverable deliverable) {
    if (deliverable.getId() == null) {
      super.saveEntity(deliverable);
    } else {
      deliverable = super.update(deliverable);
    }


    return deliverable;
  }

  @Override
  public Deliverable save(Deliverable deliverable, String section, List<String> relationsName, Phase phase) {
    if (deliverable.getId() == null) {
      deliverable = super.saveEntity(deliverable, section, relationsName, phase);
    } else {
      deliverable = super.update(deliverable, section, relationsName, phase);
    }


    return deliverable;
  }


}