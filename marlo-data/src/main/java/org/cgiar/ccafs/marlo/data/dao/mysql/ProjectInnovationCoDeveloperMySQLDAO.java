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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationCoDeveloperDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCoDeveloper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationCoDeveloperMySQLDAO extends AbstractMarloDAO<ProjectInnovationCoDeveloper, Long>
  implements ProjectInnovationCoDeveloperDAO {

  @Inject
  public ProjectInnovationCoDeveloperMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationCoDeveloper(long projectInnovationCoDeveloperId) {
    ProjectInnovationCoDeveloper projectInnovationCoDeveloper = this.find(projectInnovationCoDeveloperId);
    projectInnovationCoDeveloper.setActive(false);
    this.update(projectInnovationCoDeveloper);
  }

  @Override
  public ProjectInnovationCoDeveloper find(long id) {
    return super.find(ProjectInnovationCoDeveloper.class, id);
  }

  @Override
  public List<ProjectInnovationCoDeveloper> getByInnovationAndPhase(long innovationID, long phaseID) {
    String query = "from " + ProjectInnovationCoDeveloper.class.getName() + " where is_active=1 and innovation_id="
      + innovationID + " and id_phase=" + phaseID;
    return super.findAll(query);
  }

  @Override
  public ProjectInnovationCoDeveloper save(ProjectInnovationCoDeveloper projectInnovationCoDeveloper) {
    if (projectInnovationCoDeveloper.getId() == null) {
      super.saveEntity(projectInnovationCoDeveloper);
    } else {
      projectInnovationCoDeveloper = super.update(projectInnovationCoDeveloper);
    }
    return projectInnovationCoDeveloper;
  }
}
