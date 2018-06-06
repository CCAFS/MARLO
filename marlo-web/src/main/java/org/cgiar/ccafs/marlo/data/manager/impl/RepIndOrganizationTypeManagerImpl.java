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


import org.cgiar.ccafs.marlo.data.dao.RepIndOrganizationTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class RepIndOrganizationTypeManagerImpl implements RepIndOrganizationTypeManager {


  private RepIndOrganizationTypeDAO repIndOrganizationTypeDAO;
  // Managers


  @Inject
  public RepIndOrganizationTypeManagerImpl(RepIndOrganizationTypeDAO repIndOrganizationTypeDAO) {
    this.repIndOrganizationTypeDAO = repIndOrganizationTypeDAO;


  }

  @Override
  public void deleteRepIndOrganizationType(long repIndOrganizationTypeId) {

    repIndOrganizationTypeDAO.deleteRepIndOrganizationType(repIndOrganizationTypeId);
  }

  @Override
  public boolean existRepIndOrganizationType(long repIndOrganizationTypeID) {

    return repIndOrganizationTypeDAO.existRepIndOrganizationType(repIndOrganizationTypeID);
  }

  @Override
  public List<RepIndOrganizationType> findAll() {

    return repIndOrganizationTypeDAO.findAll();

  }

  @Override
  public RepIndOrganizationType getRepIndOrganizationTypeById(long repIndOrganizationTypeID) {

    return repIndOrganizationTypeDAO.find(repIndOrganizationTypeID);
  }

  @Override
  public RepIndOrganizationType saveRepIndOrganizationType(RepIndOrganizationType repIndOrganizationType) {

    return repIndOrganizationTypeDAO.save(repIndOrganizationType);
  }


}
