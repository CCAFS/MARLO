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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudySrfTargetDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudySrfTargetMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudySrfTarget, Long>
  implements ProjectExpectedStudySrfTargetDAO {


  @Inject
  public ProjectExpectedStudySrfTargetMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudySrfTarget(long projectExpectedStudySrfTargetId) {
    ProjectExpectedStudySrfTarget projectExpectedStudySrfTarget = this.find(projectExpectedStudySrfTargetId);
    this.delete(projectExpectedStudySrfTarget);
  }

  @Override
  public boolean existProjectExpectedStudySrfTarget(long projectExpectedStudySrfTargetID) {
    ProjectExpectedStudySrfTarget projectExpectedStudySrfTarget = this.find(projectExpectedStudySrfTargetID);
    if (projectExpectedStudySrfTarget == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudySrfTarget find(long id) {
    return super.find(ProjectExpectedStudySrfTarget.class, id);

  }

  @Override
  public List<ProjectExpectedStudySrfTarget> findAll() {
    String query = "from " + ProjectExpectedStudySrfTarget.class.getName();
    List<ProjectExpectedStudySrfTarget> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudySrfTarget save(ProjectExpectedStudySrfTarget projectExpectedStudySrfTarget) {
    if (projectExpectedStudySrfTarget.getId() == null) {
      super.saveEntity(projectExpectedStudySrfTarget);
    } else {
      projectExpectedStudySrfTarget = super.update(projectExpectedStudySrfTarget);
    }


    return projectExpectedStudySrfTarget;
  }


}