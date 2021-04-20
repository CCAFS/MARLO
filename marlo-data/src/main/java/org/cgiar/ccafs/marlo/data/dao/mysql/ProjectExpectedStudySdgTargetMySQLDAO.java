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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudySdgTargetDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySdgTarget;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudySdgTargetMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudySdgTarget, Long>
  implements ProjectExpectedStudySdgTargetDAO {


  @Inject
  public ProjectExpectedStudySdgTargetMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudySdgTarget(long projectExpectedStudySdgTargetId) {
    ProjectExpectedStudySdgTarget projectExpectedStudySdgTarget = this.find(projectExpectedStudySdgTargetId);
    this.delete(projectExpectedStudySdgTarget);
  }

  @Override
  public boolean existProjectExpectedStudySdgTarget(long projectExpectedStudySdgTargetID) {
    ProjectExpectedStudySdgTarget projectExpectedStudySdgTarget = this.find(projectExpectedStudySdgTargetID);
    if (projectExpectedStudySdgTarget == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudySdgTarget find(long id) {
    return super.find(ProjectExpectedStudySdgTarget.class, id);

  }

  @Override
  public List<ProjectExpectedStudySdgTarget> findAll() {
    String query = "from " + ProjectExpectedStudySdgTarget.class.getName() + " where is_active=1";
    List<ProjectExpectedStudySdgTarget> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudySdgTarget save(ProjectExpectedStudySdgTarget projectExpectedStudySdgTarget) {
    if (projectExpectedStudySdgTarget.getId() == null) {
      super.saveEntity(projectExpectedStudySdgTarget);
    } else {
      projectExpectedStudySdgTarget = super.update(projectExpectedStudySdgTarget);
    }


    return projectExpectedStudySdgTarget;
  }


}