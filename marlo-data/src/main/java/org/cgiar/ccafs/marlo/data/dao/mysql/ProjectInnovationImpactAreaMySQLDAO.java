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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationImpactAreaDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationImpactArea;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationImpactAreaMySQLDAO extends AbstractMarloDAO<ProjectInnovationImpactArea, Long>
  implements ProjectInnovationImpactAreaDAO {

  @Inject
  public ProjectInnovationImpactAreaMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationImpactArea(long projectInnovationImpactAreaId) {
    ProjectInnovationImpactArea projectInnovationImpactArea = this.find(projectInnovationImpactAreaId);
    projectInnovationImpactArea.setActive(false);
    this.update(projectInnovationImpactArea);
  }

  @Override
  public boolean existProjectInnovationImpactArea(long projectInnovationImpactAreaID) {
    ProjectInnovationImpactArea projectInnovationImpactArea = this.find(projectInnovationImpactAreaID);
    if (projectInnovationImpactArea == null) {
      return false;
    }
    return true;
  }

  @Override
  public ProjectInnovationImpactArea find(long id) {
    return super.find(ProjectInnovationImpactArea.class, id);
  }

  @Override
  public List<ProjectInnovationImpactArea> findAll() {
    String query = "from " + ProjectInnovationImpactArea.class.getName() + " where is_active=1";
    List<ProjectInnovationImpactArea> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return Collections.emptyList();
  }

  @Override
  public List<ProjectInnovationImpactArea> getProjectInnovationImpactAreaByInnovationAndPhase(long innovationID,
    long phaseID) {
    String query = "from " + ProjectInnovationImpactArea.class.getName() + " where is_active=1 and innovation_id="
      + innovationID + " and id_phase=" + phaseID;
    List<ProjectInnovationImpactArea> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return Collections.emptyList();
  }

  @Override
  public ProjectInnovationImpactArea save(ProjectInnovationImpactArea projectInnovationImpactArea) {
    if (projectInnovationImpactArea.getId() == null) {
      super.saveEntity(projectInnovationImpactArea);
    } else {
      projectInnovationImpactArea = super.update(projectInnovationImpactArea);
    }


    return projectInnovationImpactArea;
  }


}