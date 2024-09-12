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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyAllianceLeversOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyAllianceLeversOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyAllianceLeversOutcomeMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyAllianceLeversOutcome, Long> implements ProjectExpectedStudyAllianceLeversOutcomeDAO {


  @Inject
  public ProjectExpectedStudyAllianceLeversOutcomeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyAllianceLeversOutcome(long projectExpectedStudyAllianceLeversOutcomeId) {
    ProjectExpectedStudyAllianceLeversOutcome projectExpectedStudyAllianceLeversOutcome = this.find(projectExpectedStudyAllianceLeversOutcomeId);
    projectExpectedStudyAllianceLeversOutcome.setActive(false);
    this.update(projectExpectedStudyAllianceLeversOutcome);
  }

  @Override
  public boolean existProjectExpectedStudyAllianceLeversOutcome(long projectExpectedStudyAllianceLeversOutcomeID) {
    ProjectExpectedStudyAllianceLeversOutcome projectExpectedStudyAllianceLeversOutcome = this.find(projectExpectedStudyAllianceLeversOutcomeID);
    if (projectExpectedStudyAllianceLeversOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyAllianceLeversOutcome find(long id) {
    return super.find(ProjectExpectedStudyAllianceLeversOutcome.class, id);

  }

  @Override
  public List<ProjectExpectedStudyAllianceLeversOutcome> findAll() {
    String query = "from " + ProjectExpectedStudyAllianceLeversOutcome.class.getName() + " where is_active=1";
    List<ProjectExpectedStudyAllianceLeversOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyAllianceLeversOutcome save(ProjectExpectedStudyAllianceLeversOutcome projectExpectedStudyAllianceLeversOutcome) {
    if (projectExpectedStudyAllianceLeversOutcome.getId() == null) {
      super.saveEntity(projectExpectedStudyAllianceLeversOutcome);
    } else {
      projectExpectedStudyAllianceLeversOutcome = super.update(projectExpectedStudyAllianceLeversOutcome);
    }


    return projectExpectedStudyAllianceLeversOutcome;
  }


}