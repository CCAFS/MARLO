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


import org.cgiar.ccafs.marlo.data.dao.IpProgramTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.IpProgramTypeManager;
import org.cgiar.ccafs.marlo.data.model.IpProgramType;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class IpProgramTypeManagerImpl implements IpProgramTypeManager {


  private IpProgramTypeDAO ipProgramTypeDAO;
  // Managers


  @Inject
  public IpProgramTypeManagerImpl(IpProgramTypeDAO ipProgramTypeDAO) {
    this.ipProgramTypeDAO = ipProgramTypeDAO;


  }

  @Override
  public void deleteIpProgramType(long ipProgramTypeId) {

    ipProgramTypeDAO.deleteIpProgramType(ipProgramTypeId);
  }

  @Override
  public boolean existIpProgramType(long ipProgramTypeID) {

    return ipProgramTypeDAO.existIpProgramType(ipProgramTypeID);
  }

  @Override
  public List<IpProgramType> findAll() {

    return ipProgramTypeDAO.findAll();

  }

  @Override
  public IpProgramType getIpProgramTypeById(long ipProgramTypeID) {

    return ipProgramTypeDAO.find(ipProgramTypeID);
  }

  @Override
  public IpProgramType saveIpProgramType(IpProgramType ipProgramType) {

    return ipProgramTypeDAO.save(ipProgramType);
  }


}
