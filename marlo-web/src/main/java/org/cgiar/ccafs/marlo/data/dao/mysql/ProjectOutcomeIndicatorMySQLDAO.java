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

import org.cgiar.ccafs.marlo.data.dao.ProjectOutcomeIndicatorDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcomeIndicator;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class ProjectOutcomeIndicatorMySQLDAO extends AbstractMarloDAO<ProjectOutcomeIndicator, Long> implements ProjectOutcomeIndicatorDAO {


  @Inject
  public ProjectOutcomeIndicatorMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectOutcomeIndicator(long projectOutcomeIndicatorId) {
    ProjectOutcomeIndicator projectOutcomeIndicator = this.find(projectOutcomeIndicatorId);
    projectOutcomeIndicator.setActive(false);
    this.save(projectOutcomeIndicator);
  }

  @Override
  public boolean existProjectOutcomeIndicator(long projectOutcomeIndicatorID) {
    ProjectOutcomeIndicator projectOutcomeIndicator = this.find(projectOutcomeIndicatorID);
    if (projectOutcomeIndicator == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectOutcomeIndicator find(long id) {
    return super.find(ProjectOutcomeIndicator.class, id);

  }

  @Override
  public List<ProjectOutcomeIndicator> findAll() {
    String query = "from " + ProjectOutcomeIndicator.class.getName() + " where is_active=1";
    List<ProjectOutcomeIndicator> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectOutcomeIndicator save(ProjectOutcomeIndicator projectOutcomeIndicator) {
    if (projectOutcomeIndicator.getId() == null) {
      super.saveEntity(projectOutcomeIndicator);
    } else {
      projectOutcomeIndicator = super.update(projectOutcomeIndicator);
    }


    return projectOutcomeIndicator;
  }


}