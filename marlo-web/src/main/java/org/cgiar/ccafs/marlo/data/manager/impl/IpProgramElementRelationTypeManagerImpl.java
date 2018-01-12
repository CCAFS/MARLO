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


import org.cgiar.ccafs.marlo.data.dao.IpProgramElementRelationTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.IpProgramElementRelationTypeManager;
import org.cgiar.ccafs.marlo.data.model.IpProgramElementRelationType;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class IpProgramElementRelationTypeManagerImpl implements IpProgramElementRelationTypeManager {


  private IpProgramElementRelationTypeDAO ipProgramElementRelationTypeDAO;
  // Managers


  @Inject
  public IpProgramElementRelationTypeManagerImpl(IpProgramElementRelationTypeDAO ipProgramElementRelationTypeDAO) {
    this.ipProgramElementRelationTypeDAO = ipProgramElementRelationTypeDAO;


  }

  @Override
  public void deleteIpProgramElementRelationType(long ipProgramElementRelationTypeId) {

    ipProgramElementRelationTypeDAO.deleteIpProgramElementRelationType(ipProgramElementRelationTypeId);
  }

  @Override
  public boolean existIpProgramElementRelationType(long ipProgramElementRelationTypeID) {

    return ipProgramElementRelationTypeDAO.existIpProgramElementRelationType(ipProgramElementRelationTypeID);
  }

  @Override
  public List<IpProgramElementRelationType> findAll() {

    return ipProgramElementRelationTypeDAO.findAll();

  }

  @Override
  public IpProgramElementRelationType getIpProgramElementRelationTypeById(long ipProgramElementRelationTypeID) {

    return ipProgramElementRelationTypeDAO.find(ipProgramElementRelationTypeID);
  }

  @Override
  public IpProgramElementRelationType saveIpProgramElementRelationType(IpProgramElementRelationType ipProgramElementRelationType) {

    return ipProgramElementRelationTypeDAO.save(ipProgramElementRelationType);
  }


}
