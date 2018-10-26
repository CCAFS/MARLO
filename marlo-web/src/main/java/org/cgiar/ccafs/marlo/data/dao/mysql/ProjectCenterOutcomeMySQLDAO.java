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

import org.cgiar.ccafs.marlo.data.dao.ProjectCenterOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectCenterOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectCenterOutcomeMySQLDAO extends AbstractMarloDAO<ProjectCenterOutcome, Long> implements ProjectCenterOutcomeDAO {


  @Inject
  public ProjectCenterOutcomeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectCenterOutcome(long projectCenterOutcomeId) {
    ProjectCenterOutcome projectCenterOutcome = this.find(projectCenterOutcomeId);
    projectCenterOutcome.setActive(false);
    this.update(projectCenterOutcome);
  }

  @Override
  public boolean existProjectCenterOutcome(long projectCenterOutcomeID) {
    ProjectCenterOutcome projectCenterOutcome = this.find(projectCenterOutcomeID);
    if (projectCenterOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectCenterOutcome find(long id) {
    return super.find(ProjectCenterOutcome.class, id);

  }

  @Override
  public List<ProjectCenterOutcome> findAll() {
    String query = "from " + ProjectCenterOutcome.class.getName() + " where is_active=1";
    List<ProjectCenterOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectCenterOutcome save(ProjectCenterOutcome projectCenterOutcome) {
    if (projectCenterOutcome.getId() == null) {
      super.saveEntity(projectCenterOutcome);
    } else {
      projectCenterOutcome = super.update(projectCenterOutcome);
    }


    return projectCenterOutcome;
  }


}