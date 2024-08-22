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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPrimaryStrategicOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPrimaryStrategicOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyPrimaryStrategicOutcomeMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyPrimaryStrategicOutcome, Long> implements ProjectExpectedStudyPrimaryStrategicOutcomeDAO {


  @Inject
  public ProjectExpectedStudyPrimaryStrategicOutcomeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyPrimaryStrategicOutcome(long projectExpectedStudyPrimaryStrategicOutcomeId) {
    ProjectExpectedStudyPrimaryStrategicOutcome projectExpectedStudyPrimaryStrategicOutcome = this.find(projectExpectedStudyPrimaryStrategicOutcomeId);
    projectExpectedStudyPrimaryStrategicOutcome.setActive(false);
    this.update(projectExpectedStudyPrimaryStrategicOutcome);
  }

  @Override
  public boolean existProjectExpectedStudyPrimaryStrategicOutcome(long projectExpectedStudyPrimaryStrategicOutcomeID) {
    ProjectExpectedStudyPrimaryStrategicOutcome projectExpectedStudyPrimaryStrategicOutcome = this.find(projectExpectedStudyPrimaryStrategicOutcomeID);
    if (projectExpectedStudyPrimaryStrategicOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyPrimaryStrategicOutcome find(long id) {
    return super.find(ProjectExpectedStudyPrimaryStrategicOutcome.class, id);

  }

  @Override
  public List<ProjectExpectedStudyPrimaryStrategicOutcome> findAll() {
    String query = "from " + ProjectExpectedStudyPrimaryStrategicOutcome.class.getName() + " where is_active=1";
    List<ProjectExpectedStudyPrimaryStrategicOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyPrimaryStrategicOutcome save(ProjectExpectedStudyPrimaryStrategicOutcome projectExpectedStudyPrimaryStrategicOutcome) {
    if (projectExpectedStudyPrimaryStrategicOutcome.getId() == null) {
      super.saveEntity(projectExpectedStudyPrimaryStrategicOutcome);
    } else {
      projectExpectedStudyPrimaryStrategicOutcome = super.update(projectExpectedStudyPrimaryStrategicOutcome);
    }


    return projectExpectedStudyPrimaryStrategicOutcome;
  }


}