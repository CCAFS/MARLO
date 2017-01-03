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


import org.cgiar.ccafs.marlo.data.dao.IpElementTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.IpElementTypeManager;
import org.cgiar.ccafs.marlo.data.model.IpElementType;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class IpElementTypeManagerImpl implements IpElementTypeManager {


  private IpElementTypeDAO ipElementTypeDAO;
  // Managers


  @Inject
  public IpElementTypeManagerImpl(IpElementTypeDAO ipElementTypeDAO) {
    this.ipElementTypeDAO = ipElementTypeDAO;


  }

  @Override
  public boolean deleteIpElementType(long ipElementTypeId) {

    return ipElementTypeDAO.deleteIpElementType(ipElementTypeId);
  }

  @Override
  public boolean existIpElementType(long ipElementTypeID) {

    return ipElementTypeDAO.existIpElementType(ipElementTypeID);
  }

  @Override
  public List<IpElementType> findAll() {

    return ipElementTypeDAO.findAll();

  }

  @Override
  public IpElementType getIpElementTypeById(long ipElementTypeID) {

    return ipElementTypeDAO.find(ipElementTypeID);
  }

  @Override
  public long saveIpElementType(IpElementType ipElementType) {

    return ipElementTypeDAO.save(ipElementType);
  }


}
