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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyCenterDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCenter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyCenterMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyCenter, Long>
  implements ProjectExpectedStudyCenterDAO {

  @Inject
  public ProjectExpectedStudyCenterMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyCenter(long projectExpectedStudyCenterId) {
    ProjectExpectedStudyCenter projectExpectedStudyCenter = this.find(projectExpectedStudyCenterId);
    this.delete(projectExpectedStudyCenter);
  }

  @Override
  public boolean existProjectExpectedStudyCenter(long projectExpectedStudyCenterID) {
    ProjectExpectedStudyCenter projectExpectedStudyCenter = this.find(projectExpectedStudyCenterID);
    if (projectExpectedStudyCenter == null) {
      return false;
    }
    return true;
  }

  @Override
  public ProjectExpectedStudyCenter find(long id) {
    return super.find(ProjectExpectedStudyCenter.class, id);
  }

  @Override
  public List<ProjectExpectedStudyCenter> findAll() {
    String query = "from " + ProjectExpectedStudyCenter.class.getName();
    List<ProjectExpectedStudyCenter> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public List<ProjectExpectedStudyCenter> findAllByInsituttionAndPhase(long institutionId, long phaseId) {
    String query = "from " + ProjectExpectedStudyCenter.class.getName() + " where id_phase =" + phaseId
      + " and institution_id =" + institutionId;
    List<ProjectExpectedStudyCenter> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public ProjectExpectedStudyCenter save(ProjectExpectedStudyCenter projectExpectedStudyCenter) {
    if (projectExpectedStudyCenter.getId() == null) {
      super.saveEntity(projectExpectedStudyCenter);
    } else {
      projectExpectedStudyCenter = super.update(projectExpectedStudyCenter);
    }
    return projectExpectedStudyCenter;
  }

}
