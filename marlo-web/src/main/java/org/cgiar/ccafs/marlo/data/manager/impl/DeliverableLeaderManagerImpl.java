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


import org.cgiar.ccafs.marlo.data.dao.DeliverableLeaderDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableLeaderManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableLeader;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class DeliverableLeaderManagerImpl implements DeliverableLeaderManager {


  private DeliverableLeaderDAO deliverableLeaderDAO;
  // Managers


  @Inject
  public DeliverableLeaderManagerImpl(DeliverableLeaderDAO deliverableLeaderDAO) {
    this.deliverableLeaderDAO = deliverableLeaderDAO;


  }

  @Override
  public void deleteDeliverableLeader(long deliverableLeaderId) {

    deliverableLeaderDAO.deleteDeliverableLeader(deliverableLeaderId);
  }

  @Override
  public boolean existDeliverableLeader(long deliverableLeaderID) {

    return deliverableLeaderDAO.existDeliverableLeader(deliverableLeaderID);
  }

  @Override
  public List<DeliverableLeader> findAll() {

    return deliverableLeaderDAO.findAll();

  }

  @Override
  public DeliverableLeader getDeliverableLeaderById(long deliverableLeaderID) {

    return deliverableLeaderDAO.find(deliverableLeaderID);
  }

  @Override
  public DeliverableLeader saveDeliverableLeader(DeliverableLeader deliverableLeader) {

    return deliverableLeaderDAO.save(deliverableLeader);
  }


}
