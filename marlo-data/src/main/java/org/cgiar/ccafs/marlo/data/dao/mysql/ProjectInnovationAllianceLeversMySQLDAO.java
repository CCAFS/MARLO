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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationAllianceLeversDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationAllianceLevers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationAllianceLeversMySQLDAO extends AbstractMarloDAO<ProjectInnovationAllianceLevers, Long> implements ProjectInnovationAllianceLeversDAO {


  @Inject
  public ProjectInnovationAllianceLeversMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationAllianceLevers(long projectInnovationAllianceLeversId) {
    ProjectInnovationAllianceLevers projectInnovationAllianceLevers = this.find(projectInnovationAllianceLeversId);
    projectInnovationAllianceLevers.setActive(false);
    this.update(projectInnovationAllianceLevers);
  }

  @Override
  public boolean existProjectInnovationAllianceLevers(long projectInnovationAllianceLeversID) {
    ProjectInnovationAllianceLevers projectInnovationAllianceLevers = this.find(projectInnovationAllianceLeversID);
    if (projectInnovationAllianceLevers == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationAllianceLevers find(long id) {
    return super.find(ProjectInnovationAllianceLevers.class, id);

  }

  @Override
  public List<ProjectInnovationAllianceLevers> findAll() {
    String query = "from " + ProjectInnovationAllianceLevers.class.getName() + " where is_active=1";
    List<ProjectInnovationAllianceLevers> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInnovationAllianceLevers save(ProjectInnovationAllianceLevers projectInnovationAllianceLevers) {
    if (projectInnovationAllianceLevers.getId() == null) {
      super.saveEntity(projectInnovationAllianceLevers);
    } else {
      projectInnovationAllianceLevers = super.update(projectInnovationAllianceLevers);
    }


    return projectInnovationAllianceLevers;
  }


}