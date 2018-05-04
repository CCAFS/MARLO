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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationMySQLDAO extends AbstractMarloDAO<ProjectInnovation, Long>
  implements ProjectInnovationDAO {


  @Inject
  public ProjectInnovationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovation(long projectInnovationId) {
    ProjectInnovation projectInnovation = this.find(projectInnovationId);
    projectInnovation.setActive(false);
    this.update(projectInnovation);
  }

  @Override
  public boolean existProjectInnovation(long projectInnovationID) {
    ProjectInnovation projectInnovation = this.find(projectInnovationID);
    if (projectInnovation == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovation find(long id) {
    return super.find(ProjectInnovation.class, id);

  }

  @Override
  public List<ProjectInnovation> findAll() {
    String query = "from " + ProjectInnovation.class.getName() + " where is_active=1";
    List<ProjectInnovation> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInnovation save(ProjectInnovation projectInnovation) {
    if (projectInnovation.getId() == null) {
      super.saveEntity(projectInnovation);
    } else {
      projectInnovation = super.update(projectInnovation);
    }


    return projectInnovation;
  }

  @Override
  public ProjectInnovation save(ProjectInnovation projectInnovation, String section, List<String> relationsName,
    Phase phase) {
    if (projectInnovation.getId() == null) {
      super.saveEntity(projectInnovation, section, relationsName, phase);
    } else {
      projectInnovation = super.update(projectInnovation, section, relationsName, phase);
    }
    return projectInnovation;
  }


}