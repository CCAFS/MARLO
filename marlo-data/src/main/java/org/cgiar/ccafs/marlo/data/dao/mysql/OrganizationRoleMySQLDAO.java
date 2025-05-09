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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.OrganizationRoleDAO;
import org.cgiar.ccafs.marlo.data.model.OrganizationRole;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class OrganizationRoleMySQLDAO extends AbstractMarloDAO<OrganizationRole, Long> implements OrganizationRoleDAO {


  @Inject
  public OrganizationRoleMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteOrganizationRole(long organizationRoleId) {
    OrganizationRole organizationRole = this.find(organizationRoleId);
    organizationRole.setActive(false);
    this.update(organizationRole);
  }

  @Override
  public boolean existOrganizationRole(long organizationRoleID) {
    OrganizationRole organizationRole = this.find(organizationRoleID);
    if (organizationRole == null) {
      return false;
    }
    return true;

  }

  @Override
  public OrganizationRole find(long id) {
    return super.find(OrganizationRole.class, id);

  }

  @Override
  public List<OrganizationRole> findAll() {
    String query = "from " + OrganizationRole.class.getName() + " where is_active=1";
    List<OrganizationRole> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public OrganizationRole save(OrganizationRole organizationRole) {
    if (organizationRole.getId() == null) {
      super.saveEntity(organizationRole);
    } else {
      organizationRole = super.update(organizationRole);
    }


    return organizationRole;
  }


}