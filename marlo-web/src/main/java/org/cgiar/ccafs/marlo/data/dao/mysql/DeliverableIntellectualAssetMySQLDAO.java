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

import org.cgiar.ccafs.marlo.data.dao.DeliverableIntellectualAssetDAO;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAsset;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class DeliverableIntellectualAssetMySQLDAO extends AbstractMarloDAO<DeliverableIntellectualAsset, Long>
  implements DeliverableIntellectualAssetDAO {


  @Inject
  public DeliverableIntellectualAssetMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableIntellectualAsset(long deliverableIntellectualAssetId) {
    DeliverableIntellectualAsset deliverableIntellectualAsset = this.find(deliverableIntellectualAssetId);
    super.delete(deliverableIntellectualAsset);
  }

  @Override
  public boolean existDeliverableIntellectualAsset(long deliverableIntellectualAssetID) {
    DeliverableIntellectualAsset deliverableIntellectualAsset = this.find(deliverableIntellectualAssetID);
    if (deliverableIntellectualAsset == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableIntellectualAsset find(long id) {
    return super.find(DeliverableIntellectualAsset.class, id);

  }

  @Override
  public List<DeliverableIntellectualAsset> findAll() {
    String query = "from " + DeliverableIntellectualAsset.class.getName();
    List<DeliverableIntellectualAsset> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableIntellectualAsset findIntellectualAssetByPhaseAndDeliverable(Phase phase, Deliverable deliverable) {
    String query = "select distinct di from DeliverableIntellectualAsset di "
      + " where phase.id = :phaseId and deliverable.id= :deliverableId";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("phaseId", phase.getId());
    createQuery.setParameter("deliverableId", deliverable.getId());

    Object findSingleResult = super.findSingleResult(DeliverableIntellectualAsset.class, createQuery);

    DeliverableIntellectualAsset deliverableIntellectualAssetResult = (DeliverableIntellectualAsset) findSingleResult;


    return deliverableIntellectualAssetResult;
  }

  @Override
  public DeliverableIntellectualAsset save(DeliverableIntellectualAsset deliverableIntellectualAsset) {
    if (deliverableIntellectualAsset.getId() == null) {
      super.saveEntity(deliverableIntellectualAsset);
    } else {
      deliverableIntellectualAsset = super.update(deliverableIntellectualAsset);
    }


    return deliverableIntellectualAsset;
  }


}