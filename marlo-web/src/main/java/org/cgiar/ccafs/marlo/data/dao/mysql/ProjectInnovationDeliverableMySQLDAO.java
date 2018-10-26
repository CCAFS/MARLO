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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationDeliverableDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationDeliverable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationDeliverableMySQLDAO extends AbstractMarloDAO<ProjectInnovationDeliverable, Long>
  implements ProjectInnovationDeliverableDAO {


  @Inject
  public ProjectInnovationDeliverableMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationDeliverable(long projectInnovationDeliverableId) {
    ProjectInnovationDeliverable projectInnovationDeliverable = this.find(projectInnovationDeliverableId);
    this.delete(projectInnovationDeliverable);
  }

  @Override
  public boolean existProjectInnovationDeliverable(long projectInnovationDeliverableID) {
    ProjectInnovationDeliverable projectInnovationDeliverable = this.find(projectInnovationDeliverableID);
    if (projectInnovationDeliverable == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationDeliverable find(long id) {
    return super.find(ProjectInnovationDeliverable.class, id);

  }

  @Override
  public List<ProjectInnovationDeliverable> findAll() {
    String query = "from " + ProjectInnovationDeliverable.class.getName();
    List<ProjectInnovationDeliverable> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInnovationDeliverable save(ProjectInnovationDeliverable projectInnovationDeliverable) {
    if (projectInnovationDeliverable.getId() == null) {
      super.saveEntity(projectInnovationDeliverable);
    } else {
      projectInnovationDeliverable = super.update(projectInnovationDeliverable);
    }


    return projectInnovationDeliverable;
  }


}