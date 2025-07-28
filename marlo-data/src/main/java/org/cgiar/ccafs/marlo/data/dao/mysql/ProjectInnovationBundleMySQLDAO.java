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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationBundleDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationBundle;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationBundleMySQLDAO extends AbstractMarloDAO<ProjectInnovationBundle, Long>
  implements ProjectInnovationBundleDAO {


  @Inject
  public ProjectInnovationBundleMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationBundle(long projectInnovationBundleId) {
    ProjectInnovationBundle projectInnovationBundle = this.find(projectInnovationBundleId);
    projectInnovationBundle.setActive(false);
    this.update(projectInnovationBundle);
  }

  @Override
  public boolean existProjectInnovationBundle(long projectInnovationBundleID) {
    ProjectInnovationBundle projectInnovationBundle = this.find(projectInnovationBundleID);
    if (projectInnovationBundle == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationBundle find(long id) {
    return super.find(ProjectInnovationBundle.class, id);

  }

  @Override
  public List<ProjectInnovationBundle> findAll() {
    String query = "from " + ProjectInnovationBundle.class.getName() + " where is_active=1";
    List<ProjectInnovationBundle> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectInnovationBundle> getProjectInnovationBundleByInnovationAndPhase(long innovationID, long phaseID) {
    String query = "from " + ProjectInnovationBundle.class.getName() + " where is_active=1 and project_innovation_id = "
      + innovationID + " and id_phase = " + phaseID;
    List<ProjectInnovationBundle> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInnovationBundle save(ProjectInnovationBundle projectInnovationBundle) {
    if (projectInnovationBundle.getId() == null) {
      super.saveEntity(projectInnovationBundle);
    } else {
      projectInnovationBundle = super.update(projectInnovationBundle);
    }


    return projectInnovationBundle;
  }


}