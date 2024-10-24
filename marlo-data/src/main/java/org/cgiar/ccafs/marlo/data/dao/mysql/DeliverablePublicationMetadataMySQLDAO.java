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

import org.cgiar.ccafs.marlo.data.dao.DeliverablePublicationMetadataDAO;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

@Named
public class DeliverablePublicationMetadataMySQLDAO extends AbstractMarloDAO<DeliverablePublicationMetadata, Long>
  implements DeliverablePublicationMetadataDAO {


  @Inject
  public DeliverablePublicationMetadataMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverablePublicationMetadata(long deliverablePublicationMetadataId) {
    DeliverablePublicationMetadata deliverablePublicationMetadata = this.find(deliverablePublicationMetadataId);
    super.delete(deliverablePublicationMetadata);
  }

  @Override
  public boolean existDeliverablePublicationMetadata(long deliverablePublicationMetadataID) {
    DeliverablePublicationMetadata deliverablePublicationMetadata = this.find(deliverablePublicationMetadataID);
    if (deliverablePublicationMetadata == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverablePublicationMetadata find(long id) {
    return super.find(DeliverablePublicationMetadata.class, id);

  }

  @Override
  public List<DeliverablePublicationMetadata> findAll() {
    String query = "from " + DeliverablePublicationMetadata.class.getName() + " where is_active=1";
    List<DeliverablePublicationMetadata> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverablePublicationMetadata findPublicationMetadataByPhaseAndDeliverable(Phase phase,
    Deliverable deliverable) {
    String query = "select distinct dp from DeliverablePublicationMetadata dp "
      + " where phase.id = :phaseId and deliverable.id= :deliverableId";
    Query<DeliverablePublicationMetadata> createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("phaseId", phase.getId());
    createQuery.setParameter("deliverableId", deliverable.getId());

    Object findSingleResult = super.findSingleResult(DeliverablePublicationMetadata.class, createQuery);
    DeliverablePublicationMetadata deliverablePublicationMetadataResult =
      (DeliverablePublicationMetadata) findSingleResult;


    return deliverablePublicationMetadataResult;
  }

  @Override
  public DeliverablePublicationMetadata save(DeliverablePublicationMetadata deliverablePublicationMetadata) {
    if (deliverablePublicationMetadata.getId() == null) {
      super.saveEntity(deliverablePublicationMetadata);
    } else {
      deliverablePublicationMetadata = super.update(deliverablePublicationMetadata);
    }


    return deliverablePublicationMetadata;
  }


}