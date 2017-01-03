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


import org.cgiar.ccafs.marlo.data.dao.IpElementDAO;
import org.cgiar.ccafs.marlo.data.manager.IpElementManager;
import org.cgiar.ccafs.marlo.data.model.IpElement;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class IpElementManagerImpl implements IpElementManager {


  private IpElementDAO ipElementDAO;
  // Managers


  @Inject
  public IpElementManagerImpl(IpElementDAO ipElementDAO) {
    this.ipElementDAO = ipElementDAO;


  }

  @Override
  public boolean deleteIpElement(long ipElementId) {

    return ipElementDAO.deleteIpElement(ipElementId);
  }

  @Override
  public boolean existIpElement(long ipElementID) {

    return ipElementDAO.existIpElement(ipElementID);
  }

  @Override
  public List<IpElement> findAll() {

    return ipElementDAO.findAll();

  }

  @Override
  public IpElement getIpElementById(long ipElementID) {

    return ipElementDAO.find(ipElementID);
  }

  @Override
  public long saveIpElement(IpElement ipElement) {

    return ipElementDAO.save(ipElement);
  }


}
