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

import org.cgiar.ccafs.marlo.data.dao.DeliverableFundingSourceDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class DeliverableFundingSourceMySQLDAO extends AbstractMarloDAO<DeliverableFundingSource, Long> implements DeliverableFundingSourceDAO {


  @Inject
  public DeliverableFundingSourceMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableFundingSource(long deliverableFundingSourceId) {
    DeliverableFundingSource deliverableFundingSource = this.find(deliverableFundingSourceId);
    deliverableFundingSource.setActive(false);
    this.save(deliverableFundingSource);
  }

  @Override
  public boolean existDeliverableFundingSource(long deliverableFundingSourceID) {
    DeliverableFundingSource deliverableFundingSource = this.find(deliverableFundingSourceID);
    if (deliverableFundingSource == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableFundingSource find(long id) {
    return super.find(DeliverableFundingSource.class, id);

  }

  @Override
  public List<DeliverableFundingSource> findAll() {
    String query = "from " + DeliverableFundingSource.class.getName() + " where is_active=1";
    List<DeliverableFundingSource> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableFundingSource save(DeliverableFundingSource deliverableFundingSource) {
    if (deliverableFundingSource.getId() == null) {
      super.saveEntity(deliverableFundingSource);
    } else {
      deliverableFundingSource = super.update(deliverableFundingSource);
    }


    return deliverableFundingSource;
  }


}