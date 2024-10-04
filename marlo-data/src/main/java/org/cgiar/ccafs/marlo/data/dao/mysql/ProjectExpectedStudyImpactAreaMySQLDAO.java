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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyImpactAreaDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyImpactArea;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyImpactAreaMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyImpactArea, Long>
  implements ProjectExpectedStudyImpactAreaDAO {


  @Inject
  public ProjectExpectedStudyImpactAreaMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyImpactArea(long projectExpectedStudyImpactAreaId) {
    ProjectExpectedStudyImpactArea projectExpectedStudyImpactArea = this.find(projectExpectedStudyImpactAreaId);
    projectExpectedStudyImpactArea.setActive(false);
    this.update(projectExpectedStudyImpactArea);
  }

  @Override
  public boolean existProjectExpectedStudyImpactArea(long projectExpectedStudyImpactAreaID) {
    ProjectExpectedStudyImpactArea projectExpectedStudyImpactArea = this.find(projectExpectedStudyImpactAreaID);
    if (projectExpectedStudyImpactArea == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyImpactArea find(long id) {
    return super.find(ProjectExpectedStudyImpactArea.class, id);

  }

  @Override
  public List<ProjectExpectedStudyImpactArea> findAll() {
    String query = "from " + ProjectExpectedStudyImpactArea.class.getName() + " where is_active=1";
    List<ProjectExpectedStudyImpactArea> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }


  @Override
  public ProjectExpectedStudyImpactArea findAllByStudyAndAreaAndPhase(long expectedId, long impactAreaId,
    long phaseId) {
    String query = "from " + ProjectExpectedStudyImpactArea.class.getName() + " where is_active=1 and expected_id="
      + expectedId + " and st_impact_area_id=" + impactAreaId + " and id_phase=" + phaseId;
    List<ProjectExpectedStudyImpactArea> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyImpactArea save(ProjectExpectedStudyImpactArea projectExpectedStudyImpactArea) {
    if (projectExpectedStudyImpactArea.getId() == null) {
      super.saveEntity(projectExpectedStudyImpactArea);
    } else {
      projectExpectedStudyImpactArea = super.update(projectExpectedStudyImpactArea);
    }


    return projectExpectedStudyImpactArea;
  }


}