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


import org.cgiar.ccafs.marlo.data.dao.IpProgramElementDAO;
import org.cgiar.ccafs.marlo.data.manager.IpProgramElementManager;
import org.cgiar.ccafs.marlo.data.model.IpProgramElement;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class IpProgramElementManagerImpl implements IpProgramElementManager {


  private IpProgramElementDAO ipProgramElementDAO;
  // Managers


  @Inject
  public IpProgramElementManagerImpl(IpProgramElementDAO ipProgramElementDAO) {
    this.ipProgramElementDAO = ipProgramElementDAO;


  }

  @Override
  public void deleteIpProgramElement(long ipProgramElementId) {

    ipProgramElementDAO.deleteIpProgramElement(ipProgramElementId);
  }

  @Override
  public boolean existIpProgramElement(long ipProgramElementID) {

    return ipProgramElementDAO.existIpProgramElement(ipProgramElementID);
  }

  @Override
  public List<IpProgramElement> findAll() {

    return ipProgramElementDAO.findAll();

  }

  @Override
  public IpProgramElement getIpProgramElementById(long ipProgramElementID) {

    return ipProgramElementDAO.find(ipProgramElementID);
  }

  @Override
  public IpProgramElement saveIpProgramElement(IpProgramElement ipProgramElement) {

    return ipProgramElementDAO.save(ipProgramElement);
  }


}
