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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.DeliverableFundingSourceDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableFundingSourceManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverableFundingSourceManagerImpl implements DeliverableFundingSourceManager {


  private DeliverableFundingSourceDAO deliverableFundingSourceDAO;
  // Managers


  @Inject
  public DeliverableFundingSourceManagerImpl(DeliverableFundingSourceDAO deliverableFundingSourceDAO) {
    this.deliverableFundingSourceDAO = deliverableFundingSourceDAO;


  }

  @Override
  public void deleteDeliverableFundingSource(long deliverableFundingSourceId) {

    deliverableFundingSourceDAO.deleteDeliverableFundingSource(deliverableFundingSourceId);
  }

  @Override
  public boolean existDeliverableFundingSource(long deliverableFundingSourceID) {

    return deliverableFundingSourceDAO.existDeliverableFundingSource(deliverableFundingSourceID);
  }

  @Override
  public List<DeliverableFundingSource> findAll() {

    return deliverableFundingSourceDAO.findAll();

  }

  @Override
  public DeliverableFundingSource getDeliverableFundingSourceById(long deliverableFundingSourceID) {

    return deliverableFundingSourceDAO.find(deliverableFundingSourceID);
  }

  @Override
  public DeliverableFundingSource saveDeliverableFundingSource(DeliverableFundingSource deliverableFundingSource) {

    return deliverableFundingSourceDAO.save(deliverableFundingSource);
  }


}
