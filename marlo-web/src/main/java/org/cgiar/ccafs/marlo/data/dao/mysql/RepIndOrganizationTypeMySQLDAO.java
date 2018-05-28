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

import org.cgiar.ccafs.marlo.data.dao.RepIndOrganizationTypeDAO;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RepIndOrganizationTypeMySQLDAO extends AbstractMarloDAO<RepIndOrganizationType, Long>
  implements RepIndOrganizationTypeDAO {


  @Inject
  public RepIndOrganizationTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepIndOrganizationType(long repIndOrganizationTypeId) {
    RepIndOrganizationType repIndOrganizationType = this.find(repIndOrganizationTypeId);
    this.delete(repIndOrganizationType);
  }

  @Override
  public boolean existRepIndOrganizationType(long repIndOrganizationTypeID) {
    RepIndOrganizationType repIndOrganizationType = this.find(repIndOrganizationTypeID);
    if (repIndOrganizationType == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepIndOrganizationType find(long id) {
    return super.find(RepIndOrganizationType.class, id);

  }

  @Override
  public List<RepIndOrganizationType> findAll() {
    String query = "from " + RepIndOrganizationType.class.getName();
    List<RepIndOrganizationType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepIndOrganizationType save(RepIndOrganizationType repIndOrganizationType) {
    if (repIndOrganizationType.getId() == null) {
      super.saveEntity(repIndOrganizationType);
    } else {
      repIndOrganizationType = super.update(repIndOrganizationType);
    }


    return repIndOrganizationType;
  }


}