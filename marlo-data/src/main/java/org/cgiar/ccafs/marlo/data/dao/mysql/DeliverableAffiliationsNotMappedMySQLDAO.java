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

import org.cgiar.ccafs.marlo.data.dao.DeliverableAffiliationsNotMappedDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableAffiliationsNotMapped;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@Named
public class DeliverableAffiliationsNotMappedMySQLDAO extends AbstractMarloDAO<DeliverableAffiliationsNotMapped, Long>
  implements DeliverableAffiliationsNotMappedDAO {

  @Inject
  public DeliverableAffiliationsNotMappedMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableAffiliationsNotMapped(long deliverableAffiliationsNotMappedId) {
    DeliverableAffiliationsNotMapped deliverableAffiliationsNotMapped = this.find(deliverableAffiliationsNotMappedId);
    deliverableAffiliationsNotMapped.setActive(false);
    this.save(deliverableAffiliationsNotMapped);
  }

  @Override
  public boolean existDeliverableAffiliationsNotMapped(long deliverableAffiliationsNotMappedID) {
    DeliverableAffiliationsNotMapped deliverableAffiliationsNotMapped = this.find(deliverableAffiliationsNotMappedID);
    if (deliverableAffiliationsNotMapped == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableAffiliationsNotMapped find(long id) {
    return super.find(DeliverableAffiliationsNotMapped.class, id);

  }

  @Override
  public List<DeliverableAffiliationsNotMapped> findAll() {
    String query = "from " + DeliverableAffiliationsNotMapped.class.getName();
    List<DeliverableAffiliationsNotMapped> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<DeliverableAffiliationsNotMapped> findBydeliverableMetadataExternalSourcesId(long externalSourceId) {
    String query = "from " + DeliverableAffiliationsNotMapped.class.getName()
      + " where deliverable_metadata_external_sources_id =" + externalSourceId;
    List<DeliverableAffiliationsNotMapped> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableAffiliationsNotMapped save(DeliverableAffiliationsNotMapped deliverableAffiliationsNotMapped) {
    if (deliverableAffiliationsNotMapped.getId() == null) {
      super.saveEntity(deliverableAffiliationsNotMapped);
    } else {
      deliverableAffiliationsNotMapped = super.update(deliverableAffiliationsNotMapped);
    }

    return deliverableAffiliationsNotMapped;
  }
}