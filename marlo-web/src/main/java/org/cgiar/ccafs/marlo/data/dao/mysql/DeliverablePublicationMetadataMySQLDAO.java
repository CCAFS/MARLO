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
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class DeliverablePublicationMetadataMySQLDAO extends AbstractMarloDAO
  implements DeliverablePublicationMetadataDAO {


  @Inject
  public DeliverablePublicationMetadataMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteDeliverablePublicationMetadata(long deliverablePublicationMetadataId) {
    DeliverablePublicationMetadata deliverablePublicationMetadata = this.find(deliverablePublicationMetadataId);
    return super.delete(deliverablePublicationMetadata);
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
  public long save(DeliverablePublicationMetadata deliverablePublicationMetadata) {
    if (deliverablePublicationMetadata.getId() == null) {
      super.save(deliverablePublicationMetadata);
    } else {
      super.update(deliverablePublicationMetadata);
    }


    return deliverablePublicationMetadata.getId();
  }


}