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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationProjectOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationProjectOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationProjectOutcomeMySQLDAO extends AbstractMarloDAO<ProjectInnovationProjectOutcome, Long>
  implements ProjectInnovationProjectOutcomeDAO {


  @Inject
  public ProjectInnovationProjectOutcomeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationProjectOutcome(long projectInnovationProjectOutcomeId) {
    ProjectInnovationProjectOutcome projectInnovationProjectOutcome = this.find(projectInnovationProjectOutcomeId);
    this.delete(projectInnovationProjectOutcome);
  }

  @Override
  public boolean existProjectInnovationProjectOutcome(long projectInnovationProjectOutcomeID) {
    ProjectInnovationProjectOutcome projectInnovationProjectOutcome = this.find(projectInnovationProjectOutcomeID);
    if (projectInnovationProjectOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationProjectOutcome find(long id) {
    return super.find(ProjectInnovationProjectOutcome.class, id);

  }

  @Override
  public List<ProjectInnovationProjectOutcome> findAll() {
    String query = "from " + ProjectInnovationProjectOutcome.class.getName();
    List<ProjectInnovationProjectOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInnovationProjectOutcome save(ProjectInnovationProjectOutcome projectInnovationProjectOutcome) {
    if (projectInnovationProjectOutcome.getId() == null) {
      super.saveEntity(projectInnovationProjectOutcome);
    } else {
      projectInnovationProjectOutcome = super.update(projectInnovationProjectOutcome);
    }


    return projectInnovationProjectOutcome;
  }


}