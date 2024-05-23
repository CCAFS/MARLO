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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationCenterDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCenter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationCenterMySQLDAO extends AbstractMarloDAO<ProjectInnovationCenter, Long>
  implements ProjectInnovationCenterDAO {


  @Inject
  public ProjectInnovationCenterMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationCenter(long projectInnovationCenterId) {
    ProjectInnovationCenter projectInnovationCenter = this.find(projectInnovationCenterId);
    this.delete(projectInnovationCenter);

  }

  @Override
  public boolean existProjectInnovationCenter(long projectInnovationCenterID) {
    ProjectInnovationCenter projectInnovationCenter = this.find(projectInnovationCenterID);
    if (projectInnovationCenter == null) {
      return false;
    }
    return true;
  }

  @Override
  public ProjectInnovationCenter find(long id) {
    return super.find(ProjectInnovationCenter.class, id);
  }

  @Override
  public List<ProjectInnovationCenter> findAll() {
    String query = "from " + ProjectInnovationCenter.class.getName();
    List<ProjectInnovationCenter> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public List<ProjectInnovationCenter> findAllByInsitutionAndPhase(long institutionId, long phaseId) {
    String query = "from " + ProjectInnovationCenter.class.getName() + " where id_phase =" + phaseId
      + " and institution_id =" + institutionId;
    List<ProjectInnovationCenter> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }


  @Override
  public ProjectInnovationCenter getProjectInnovationCenterById(long innovationid, long globalUnitID, long phaseID) {
    String query = "from " + ProjectInnovationCenter.class.getName() + " where project_innovation_id='" + innovationid
      + "' AND global_unit_id='" + globalUnitID + "' AND id_phase='" + phaseID + "'";
    List<ProjectInnovationCenter> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public ProjectInnovationCenter save(ProjectInnovationCenter projectInnovationCenter) {
    if (projectInnovationCenter.getId() == null) {
      super.saveEntity(projectInnovationCenter);
    } else {
      projectInnovationCenter = super.update(projectInnovationCenter);
    }
    return projectInnovationCenter;
  }
}
