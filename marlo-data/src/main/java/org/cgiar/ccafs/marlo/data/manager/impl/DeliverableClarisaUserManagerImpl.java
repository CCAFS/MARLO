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


import org.cgiar.ccafs.marlo.data.dao.DeliverableClarisaUserDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableClarisaUserManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableClarisaUser;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableClarisaUserManagerImpl implements DeliverableClarisaUserManager {


  private DeliverableClarisaUserDAO deliverableClarisaUserDAO;
  // Managers


  @Inject
  public DeliverableClarisaUserManagerImpl(DeliverableClarisaUserDAO deliverableClarisaUserDAO) {
    this.deliverableClarisaUserDAO = deliverableClarisaUserDAO;


  }

  @Override
  public void deleteDeliverableClarisaUser(long deliverableClarisaUserId) {

    deliverableClarisaUserDAO.deleteDeliverableClarisaUser(deliverableClarisaUserId);
  }

  @Override
  public boolean existDeliverableClarisaUser(long deliverableClarisaUserID) {

    return deliverableClarisaUserDAO.existDeliverableClarisaUser(deliverableClarisaUserID);
  }

  @Override
  public List<DeliverableClarisaUser> findAll() {

    return deliverableClarisaUserDAO.findAll();

  }

  @Override
  public DeliverableClarisaUser getDeliverableClarisaUserById(long deliverableClarisaUserID) {

    return deliverableClarisaUserDAO.find(deliverableClarisaUserID);
  }

  @Override
  public DeliverableClarisaUser saveDeliverableClarisaUser(DeliverableClarisaUser deliverableClarisaUser) {

    return deliverableClarisaUserDAO.save(deliverableClarisaUser);
  }


}
