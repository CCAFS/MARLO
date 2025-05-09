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


import org.cgiar.ccafs.marlo.data.dao.OrganizationRoleDAO;
import org.cgiar.ccafs.marlo.data.manager.OrganizationRoleManager;
import org.cgiar.ccafs.marlo.data.model.OrganizationRole;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class OrganizationRoleManagerImpl implements OrganizationRoleManager {


  private OrganizationRoleDAO organizationRoleDAO;
  // Managers


  @Inject
  public OrganizationRoleManagerImpl(OrganizationRoleDAO organizationRoleDAO) {
    this.organizationRoleDAO = organizationRoleDAO;


  }

  @Override
  public void deleteOrganizationRole(long organizationRoleId) {

    organizationRoleDAO.deleteOrganizationRole(organizationRoleId);
  }

  @Override
  public boolean existOrganizationRole(long organizationRoleID) {

    return organizationRoleDAO.existOrganizationRole(organizationRoleID);
  }

  @Override
  public List<OrganizationRole> findAll() {

    return organizationRoleDAO.findAll();

  }

  @Override
  public OrganizationRole getOrganizationRoleById(long organizationRoleID) {

    return organizationRoleDAO.find(organizationRoleID);
  }

  @Override
  public OrganizationRole saveOrganizationRole(OrganizationRole organizationRole) {

    return organizationRoleDAO.save(organizationRole);
  }


}
