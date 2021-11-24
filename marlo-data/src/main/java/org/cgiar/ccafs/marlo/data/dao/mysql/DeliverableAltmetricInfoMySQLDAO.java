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

import org.cgiar.ccafs.marlo.data.dao.DeliverableAltmetricInfoDAO;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableAltmetricInfo;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.ListUtils;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@Named
public class DeliverableAltmetricInfoMySQLDAO extends AbstractMarloDAO<DeliverableAltmetricInfo, Long>
  implements DeliverableAltmetricInfoDAO {

  @Inject
  public DeliverableAltmetricInfoMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableAltmetricInfo(long deliverableAltmetricInfoId) {
    DeliverableAltmetricInfo deliverableAltmetricInfo = this.find(deliverableAltmetricInfoId);
    deliverableAltmetricInfo.setActive(false);
    this.save(deliverableAltmetricInfo);
  }

  @Override
  public boolean existDeliverableAltmetricInfo(long deliverableAltmetricInfoID) {
    DeliverableAltmetricInfo deliverableAltmetricInfo = this.find(deliverableAltmetricInfoID);
    if (deliverableAltmetricInfo == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableAltmetricInfo find(long id) {
    return super.find(DeliverableAltmetricInfo.class, id);

  }

  @Override
  public List<DeliverableAltmetricInfo> findAll() {
    String query = "select distinct dai from DeliverableAltmetricInfo dai";
    List<DeliverableAltmetricInfo> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableAltmetricInfo findByPhaseAndDeliverable(Phase phase, Deliverable deliverable) {
    String query = "select distinct dai from DeliverableAltmetricInfo dai where phase.id = :phaseId "
      + "and deliverable.id= :deliverableId order by lastUpdated desc";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("phaseId", phase.getId());
    createQuery.setParameter("deliverableId", deliverable.getId());

    List<DeliverableAltmetricInfo> findSingleResult = ListUtils.emptyIfNull(super.findAll(createQuery));
    DeliverableAltmetricInfo deliverableAltmetricInfoResult =
      (findSingleResult.isEmpty()) ? null : findSingleResult.get(0);
    return deliverableAltmetricInfoResult;
  }

  @Override
  public DeliverableAltmetricInfo save(DeliverableAltmetricInfo deliverableAltmetricInfo) {
    if (deliverableAltmetricInfo.getId() == null) {
      super.saveEntity(deliverableAltmetricInfo);
    } else {
      deliverableAltmetricInfo = super.update(deliverableAltmetricInfo);
    }

    return deliverableAltmetricInfo;
  }
}