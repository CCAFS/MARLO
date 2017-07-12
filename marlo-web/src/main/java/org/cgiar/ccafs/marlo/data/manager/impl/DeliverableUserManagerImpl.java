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


import org.cgiar.ccafs.marlo.data.dao.DeliverableUserDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class DeliverableUserManagerImpl implements DeliverableUserManager {


  private DeliverableUserDAO deliverableUserDAO;
  // Managers


  @Inject
  public DeliverableUserManagerImpl(DeliverableUserDAO deliverableUserDAO) {
    this.deliverableUserDAO = deliverableUserDAO;


  }

  @Override
  public boolean deleteDeliverableUser(long deliverableUserId) {

    return deliverableUserDAO.deleteDeliverableUser(deliverableUserId);
  }

  @Override
  public boolean existDeliverableUser(long deliverableUserID) {

    return deliverableUserDAO.existDeliverableUser(deliverableUserID);
  }

  @Override
  public List<DeliverableUser> findAll() {

    return deliverableUserDAO.findAll();

  }

  @Override
  public DeliverableUser getDeliverableUserById(long deliverableUserID) {

    return deliverableUserDAO.find(deliverableUserID);
  }

  @Override
  public long saveDeliverableUser(DeliverableUser deliverableUser) {

    return deliverableUserDAO.save(deliverableUser);
  }


}
