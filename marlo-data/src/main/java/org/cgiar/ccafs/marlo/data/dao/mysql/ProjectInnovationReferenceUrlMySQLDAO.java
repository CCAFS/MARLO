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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationReferenceUrlDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReferenceUrl;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationReferenceUrlMySQLDAO extends AbstractMarloDAO<ProjectInnovationReferenceUrl, Long>
  implements ProjectInnovationReferenceUrlDAO {


  @Inject
  public ProjectInnovationReferenceUrlMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationReferenceUrl(long projectInnovationReferenceUrlId) {
    ProjectInnovationReferenceUrl projectInnovationReferenceUrl = this.find(projectInnovationReferenceUrlId);
    projectInnovationReferenceUrl.setActive(false);
    this.update(projectInnovationReferenceUrl);
  }

  @Override
  public boolean existProjectInnovationReferenceUrl(long projectInnovationReferenceUrlID) {
    ProjectInnovationReferenceUrl projectInnovationReferenceUrl = this.find(projectInnovationReferenceUrlID);
    if (projectInnovationReferenceUrl == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationReferenceUrl find(long id) {
    return super.find(ProjectInnovationReferenceUrl.class, id);

  }

  @Override
  public List<ProjectInnovationReferenceUrl> findAll() {
    String query = "from " + ProjectInnovationReferenceUrl.class.getName() + " where is_active=1";
    List<ProjectInnovationReferenceUrl> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectInnovationReferenceUrl> getProjectInnovationReferenceUrlByPhaseAndInnovation(long phaseID,
    long innovationID) {
    String query = "from " + ProjectInnovationReferenceUrl.class.getName()
      + " where is_active=1 and project_innovation_id=" + innovationID + " and id_phase=" + phaseID;
    List<ProjectInnovationReferenceUrl> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return Collections.emptyList();
  }

  @Override
  public ProjectInnovationReferenceUrl save(ProjectInnovationReferenceUrl projectInnovationReferenceUrl) {
    if (projectInnovationReferenceUrl.getId() == null) {
      super.saveEntity(projectInnovationReferenceUrl);
    } else {
      projectInnovationReferenceUrl = super.update(projectInnovationReferenceUrl);
    }


    return projectInnovationReferenceUrl;
  }
}