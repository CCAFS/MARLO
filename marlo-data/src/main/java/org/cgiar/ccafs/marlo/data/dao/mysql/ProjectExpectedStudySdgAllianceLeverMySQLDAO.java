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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudySdgAllianceLeverDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySdgAllianceLever;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudySdgAllianceLeverMySQLDAO extends
  AbstractMarloDAO<ProjectExpectedStudySdgAllianceLever, Long> implements ProjectExpectedStudySdgAllianceLeverDAO {


  @Inject
  public ProjectExpectedStudySdgAllianceLeverMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudySdgAllianceLever(long projectExpectedStudySdgAllianceLeverId) {
    ProjectExpectedStudySdgAllianceLever projectExpectedStudySdgAllianceLever =
      this.find(projectExpectedStudySdgAllianceLeverId);
    projectExpectedStudySdgAllianceLever.setActive(false);
    this.update(projectExpectedStudySdgAllianceLever);
  }

  @Override
  public boolean existProjectExpectedStudySdgAllianceLever(long projectExpectedStudySdgAllianceLeverID) {
    ProjectExpectedStudySdgAllianceLever projectExpectedStudySdgAllianceLever =
      this.find(projectExpectedStudySdgAllianceLeverID);
    if (projectExpectedStudySdgAllianceLever == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudySdgAllianceLever find(long id) {
    return super.find(ProjectExpectedStudySdgAllianceLever.class, id);

  }

  @Override
  public List<ProjectExpectedStudySdgAllianceLever> findAll() {
    String query = "from " + ProjectExpectedStudySdgAllianceLever.class.getName() + " where is_active=1";
    List<ProjectExpectedStudySdgAllianceLever> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }


  @Override
  public ProjectExpectedStudySdgAllianceLever findAllByPhaseExpectedAndLever(long phaseId, long expectedId,
    long leverId) {
    String query = "from " + ProjectExpectedStudySdgAllianceLever.class.getName() + " where is_active=1 and id_phase="
      + phaseId + " and expected_id=" + expectedId + " and alliance_lever_id=" + leverId + " and is_primary=0";
    List<ProjectExpectedStudySdgAllianceLever> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;

  }


  @Override
  public ProjectExpectedStudySdgAllianceLever findByPhaseExpectedAndLever(long phaseId, long expectedId, long leverId,
    long sdg, int isPrimary) {
    String query = "from " + ProjectExpectedStudySdgAllianceLever.class.getName() + " where is_active=1 and id_phase="
      + phaseId + " and expected_id=" + expectedId + " and alliance_lever_id=" + leverId + " and sdg_contribution_id="
      + sdg + " and is_primary=" + isPrimary;
    List<ProjectExpectedStudySdgAllianceLever> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;

  }

  @Override
  public ProjectExpectedStudySdgAllianceLever findByPhaseExpectedAndLeverSingle(long phaseId, long expectedId,
    long leverId, long sdg, int isPrimary) {
    String query = "from " + ProjectExpectedStudySdgAllianceLever.class.getName() + " where is_active=1 and id_phase="
      + phaseId + " and expected_id=" + expectedId + " and alliance_lever_id=" + leverId + " and sdg_contribution_id="
      + null + " and is_primary=" + isPrimary;
    List<ProjectExpectedStudySdgAllianceLever> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;

  }

  @Override
  public ProjectExpectedStudySdgAllianceLever
    save(ProjectExpectedStudySdgAllianceLever projectExpectedStudySdgAllianceLever) {
    if (projectExpectedStudySdgAllianceLever.getId() == null) {
      super.saveEntity(projectExpectedStudySdgAllianceLever);
    } else {
      projectExpectedStudySdgAllianceLever = super.update(projectExpectedStudySdgAllianceLever);
    }


    return projectExpectedStudySdgAllianceLever;
  }


}