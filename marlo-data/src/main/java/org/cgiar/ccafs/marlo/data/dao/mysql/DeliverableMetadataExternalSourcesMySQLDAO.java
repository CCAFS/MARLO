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

import org.cgiar.ccafs.marlo.data.dao.DeliverableMetadataExternalSourcesDAO;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataExternalSources;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@Named
public class DeliverableMetadataExternalSourcesMySQLDAO
  extends AbstractMarloDAO<DeliverableMetadataExternalSources, Long> implements DeliverableMetadataExternalSourcesDAO {

  @Inject
  public DeliverableMetadataExternalSourcesMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableMetadataExternalSources(long deliverableMetadataExternalSourceId) {
    DeliverableMetadataExternalSources deliverableMetadataExternalSource =
      this.find(deliverableMetadataExternalSourceId);
    deliverableMetadataExternalSource.setActive(false);
    this.save(deliverableMetadataExternalSource);
  }

  @Override
  public boolean existDeliverableMetadataExternalSources(long deliverableMetadataExternalSourceID) {
    DeliverableMetadataExternalSources deliverableMetadataExternalSource =
      this.find(deliverableMetadataExternalSourceID);
    if (deliverableMetadataExternalSource == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableMetadataExternalSources find(long id) {
    return super.find(DeliverableMetadataExternalSources.class, id);

  }

  @Override
  public List<DeliverableMetadataExternalSources> findAll() {
    String query = "from " + DeliverableMetadataExternalSources.class.getName();
    List<DeliverableMetadataExternalSources> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableMetadataExternalSources findByPhaseAndDeliverable(Phase phase, Deliverable deliverable) {
    String query = "select distinct dm from DeliverableMetadataExternalSources dm where phase.id = :phaseId "
      + "and deliverable.id= :deliverableId";
    Query<DeliverableMetadataExternalSources> createQuery =
      this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("phaseId", phase.getId());
    createQuery.setParameter("deliverableId", deliverable.getId());

    Object findSingleResult = super.findSingleResult(DeliverableMetadataExternalSources.class, createQuery);
    DeliverableMetadataExternalSources deliverableMetadataExternalSourceResult =
      (DeliverableMetadataExternalSources) findSingleResult;
    return deliverableMetadataExternalSourceResult;
  }

  @Override
  public DeliverableMetadataExternalSources save(DeliverableMetadataExternalSources deliverableMetadataExternalSource) {
    if (deliverableMetadataExternalSource.getId() == null) {
      super.saveEntity(deliverableMetadataExternalSource);
    } else {
      deliverableMetadataExternalSource = super.update(deliverableMetadataExternalSource);
    }

    return deliverableMetadataExternalSource;
  }
}