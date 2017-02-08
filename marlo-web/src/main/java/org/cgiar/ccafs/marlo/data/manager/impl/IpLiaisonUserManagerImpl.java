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


import org.cgiar.ccafs.marlo.data.dao.IpLiaisonUserDAO;
import org.cgiar.ccafs.marlo.data.manager.IpLiaisonUserManager;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonUser;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class IpLiaisonUserManagerImpl implements IpLiaisonUserManager {


  private IpLiaisonUserDAO ipLiaisonUserDAO;
  // Managers


  @Inject
  public IpLiaisonUserManagerImpl(IpLiaisonUserDAO ipLiaisonUserDAO) {
    this.ipLiaisonUserDAO = ipLiaisonUserDAO;


  }

  @Override
  public boolean deleteIpLiaisonUser(long ipLiaisonUserId) {

    return ipLiaisonUserDAO.deleteIpLiaisonUser(ipLiaisonUserId);
  }

  @Override
  public boolean existIpLiaisonUser(long ipLiaisonUserID) {

    return ipLiaisonUserDAO.existIpLiaisonUser(ipLiaisonUserID);
  }

  @Override
  public List<IpLiaisonUser> findAll() {

    return ipLiaisonUserDAO.findAll();

  }

  @Override
  public IpLiaisonUser getIpLiaisonUserById(long ipLiaisonUserID) {

    return ipLiaisonUserDAO.find(ipLiaisonUserID);
  }

  @Override
  public long saveIpLiaisonUser(IpLiaisonUser ipLiaisonUser) {

    return ipLiaisonUserDAO.save(ipLiaisonUser);
  }


}
