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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyProjectOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyProjectOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyProjectOutcomeMySQLDAO
  extends AbstractMarloDAO<ProjectExpectedStudyProjectOutcome, Long> implements ProjectExpectedStudyProjectOutcomeDAO {


  @Inject
  public ProjectExpectedStudyProjectOutcomeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyProjectOutcome(long projectExpectedStudyProjectOutcomeId) {
    ProjectExpectedStudyProjectOutcome projectExpectedStudyProjectOutcome =
      this.find(projectExpectedStudyProjectOutcomeId);
    this.delete(projectExpectedStudyProjectOutcome);
  }

  @Override
  public boolean existProjectExpectedStudyProjectOutcome(long projectExpectedStudyProjectOutcomeID) {
    ProjectExpectedStudyProjectOutcome projectExpectedStudyProjectOutcome =
      this.find(projectExpectedStudyProjectOutcomeID);
    if (projectExpectedStudyProjectOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyProjectOutcome find(long id) {
    return super.find(ProjectExpectedStudyProjectOutcome.class, id);

  }

  @Override
  public List<ProjectExpectedStudyProjectOutcome> findAll() {
    String query = "from " + ProjectExpectedStudyProjectOutcome.class.getName();
    List<ProjectExpectedStudyProjectOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyProjectOutcome
    save(ProjectExpectedStudyProjectOutcome projectExpectedStudyProjectOutcome) {
    if (projectExpectedStudyProjectOutcome.getId() == null) {
      super.saveEntity(projectExpectedStudyProjectOutcome);
    } else {
      projectExpectedStudyProjectOutcome = super.update(projectExpectedStudyProjectOutcome);
    }


    return projectExpectedStudyProjectOutcome;
  }


}