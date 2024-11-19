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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyGlobalTargetDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGlobalTarget;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyGlobalTargetMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyGlobalTarget, Long>
  implements ProjectExpectedStudyGlobalTargetDAO {


  @Inject
  public ProjectExpectedStudyGlobalTargetMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyGlobalTarget(long projectExpectedStudyGlobalTargetId) {
    ProjectExpectedStudyGlobalTarget projectExpectedStudyGlobalTarget = this.find(projectExpectedStudyGlobalTargetId);
    projectExpectedStudyGlobalTarget.setActive(false);
    this.update(projectExpectedStudyGlobalTarget);
  }

  @Override
  public boolean existProjectExpectedStudyGlobalTarget(long projectExpectedStudyGlobalTargetID) {
    ProjectExpectedStudyGlobalTarget projectExpectedStudyGlobalTarget = this.find(projectExpectedStudyGlobalTargetID);
    if (projectExpectedStudyGlobalTarget == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyGlobalTarget find(long id) {
    return super.find(ProjectExpectedStudyGlobalTarget.class, id);

  }

  @Override
  public List<ProjectExpectedStudyGlobalTarget> findAll() {
    String query = "from " + ProjectExpectedStudyGlobalTarget.class.getName() + " where is_active=1";
    List<ProjectExpectedStudyGlobalTarget> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyGlobalTarget findByExpectedAndGlobalAndPhase(long expectedId, long targetId,
    long phaseId) {
    String query = "from " + ProjectExpectedStudyGlobalTarget.class.getName() + " where is_active=1 and expected_id="
      + expectedId + " and global_target_id=" + targetId + " and id_phase=" + phaseId;
    List<ProjectExpectedStudyGlobalTarget> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyGlobalTarget save(ProjectExpectedStudyGlobalTarget projectExpectedStudyGlobalTarget) {
    if (projectExpectedStudyGlobalTarget.getId() == null) {
      super.saveEntity(projectExpectedStudyGlobalTarget);
    } else {
      projectExpectedStudyGlobalTarget = super.update(projectExpectedStudyGlobalTarget);
    }


    return projectExpectedStudyGlobalTarget;
  }


}