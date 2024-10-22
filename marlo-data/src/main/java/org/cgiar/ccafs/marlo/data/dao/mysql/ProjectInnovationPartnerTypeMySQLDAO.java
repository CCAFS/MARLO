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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationPartnerTypeDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPartnerType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationPartnerTypeMySQLDAO extends AbstractMarloDAO<ProjectInnovationPartnerType, Long>
  implements ProjectInnovationPartnerTypeDAO {


  @Inject
  public ProjectInnovationPartnerTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationPartnerType(long projectInnovationPartnerTypeId) {
    ProjectInnovationPartnerType projectInnovationPartnerType = this.find(projectInnovationPartnerTypeId);
    this.delete(projectInnovationPartnerType);
  }

  @Override
  public boolean existProjectInnovationPartnerType(long projectInnovationPartnerTypeID) {
    ProjectInnovationPartnerType projectInnovationPartnerType = this.find(projectInnovationPartnerTypeID);
    if (projectInnovationPartnerType == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationPartnerType find(long id) {
    return super.find(ProjectInnovationPartnerType.class, id);

  }

  @Override
  public List<ProjectInnovationPartnerType> findAll() {
    String query = "from " + ProjectInnovationPartnerType.class.getName();
    List<ProjectInnovationPartnerType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInnovationPartnerType save(ProjectInnovationPartnerType projectInnovationPartnerType) {
    if (projectInnovationPartnerType.getId() == null) {
      super.saveEntity(projectInnovationPartnerType);
    } else {
      projectInnovationPartnerType = super.update(projectInnovationPartnerType);
    }


    return projectInnovationPartnerType;
  }


}