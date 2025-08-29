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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationPRMSDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPRMS;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationPRMSMySQLDAO extends AbstractMarloDAO<ProjectInnovationPRMS, Long>
  implements ProjectInnovationPRMSDAO {


  @Inject
  public ProjectInnovationPRMSMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationPRMS(long ProjectInnovationPRMSId) {
    ProjectInnovationPRMS ProjectInnovationPRMS = this.find(ProjectInnovationPRMSId);
    ProjectInnovationPRMS.setActive(false);
    this.update(ProjectInnovationPRMS);
  }

  @Override
  public boolean existProjectInnovationPRMS(long ProjectInnovationPRMSID) {
    ProjectInnovationPRMS ProjectInnovationPRMS = this.find(ProjectInnovationPRMSID);
    if (ProjectInnovationPRMS == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationPRMS find(long id) {
    return super.find(ProjectInnovationPRMS.class, id);

  }

  @Override
  public List<ProjectInnovationPRMS> findAll() {
    String query = "from " + ProjectInnovationPRMS.class.getName() + " where is_active=1";
    List<ProjectInnovationPRMS> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;
  }

  @Override
  public List<ProjectInnovationPRMS> findByInnovationIDAndPhaseID(long projectInnovationID, long phaseID) {
    String query = "from " + ProjectInnovationPRMS.class.getName() + " where is_active=1 and project_innovation_id = "
      + projectInnovationID + " and id_phase = " + phaseID;
    List<ProjectInnovationPRMS> list = super.findAll(query);
    if (list == null || list.isEmpty()) {
      return Collections.emptyList();
    }
    return list;
  }

  @Override
  public ProjectInnovationPRMS save(ProjectInnovationPRMS ProjectInnovationPRMS) {
    if (ProjectInnovationPRMS.getId() == null) {
      super.saveEntity(ProjectInnovationPRMS);
    } else {
      ProjectInnovationPRMS = super.update(ProjectInnovationPRMS);
    }
    return ProjectInnovationPRMS;
  }

}