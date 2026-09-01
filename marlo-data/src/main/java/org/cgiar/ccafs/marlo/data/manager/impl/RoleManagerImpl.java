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


import org.cgiar.ccafs.marlo.data.dao.RoleDAO;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.model.Role;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author Christian Garcia
 */
@Named
public class RoleManagerImpl implements RoleManager {


  private RoleDAO roleDAO;
  // Managers


  @Inject
  public RoleManagerImpl(RoleDAO roleDAO) {
    this.roleDAO = roleDAO;


  }

  @Override
  @Transactional
  public void deleteRole(long roleId) {

    roleDAO.deleteRole(roleId);
  }

  @Override
  public boolean existRole(long roleID) {

    return roleDAO.existRole(roleID);
  }

  @Override
  public boolean existsPermissionsByNames(List<String> permissionNames) {
    return roleDAO.existsPermissionsByNames(permissionNames);
  }

  @Override
  public List<Role> findAll() {

    return roleDAO.findAll();

  }

  @Override
  public List<Role> findAllByGlobalUnit(long globalUnitId) {

    return roleDAO.findAllByGlobalUnit(globalUnitId);

  }

  @Override
  @Transactional
  public int cloneRolePermissionsByAcronym(long templateGlobalUnitId, long targetGlobalUnitId) {
    return roleDAO.cloneRolePermissionsByAcronym(templateGlobalUnitId, targetGlobalUnitId);
  }

  @Override
  @Transactional
  public int ensureSuperAdminRoleAndPermissions(long targetGlobalUnitId, long templateGlobalUnitId) {
    return roleDAO.ensureSuperAdminRoleAndPermissions(targetGlobalUnitId, templateGlobalUnitId);
  }

  @Override
  public Role getRoleById(long roleID) {

    return roleDAO.find(roleID);
  }

  @Override
  @Transactional
  public Role saveRole(Role role) {

    return roleDAO.save(role);
  }


}
