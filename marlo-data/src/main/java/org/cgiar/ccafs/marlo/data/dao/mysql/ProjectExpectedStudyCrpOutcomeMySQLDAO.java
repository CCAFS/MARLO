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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyCrpOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrpOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyCrpOutcomeMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyCrpOutcome, Long>
  implements ProjectExpectedStudyCrpOutcomeDAO {


  @Inject
  public ProjectExpectedStudyCrpOutcomeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyCrpOutcome(long projectExpectedStudyCrpOutcomeId) {
    ProjectExpectedStudyCrpOutcome projectExpectedStudyCrpOutcome = this.find(projectExpectedStudyCrpOutcomeId);
    this.delete(projectExpectedStudyCrpOutcome);
  }

  @Override
  public void deleteProjectExpectedStudyCrpOutcome(long projectExpectedStudyCrpOutcomeId, long phaseId) {
    ProjectExpectedStudyCrpOutcome projectExpectedStudyCrpOutcome = this.find(projectExpectedStudyCrpOutcomeId);
    this.delete(projectExpectedStudyCrpOutcome);
  }

  @Override
  public boolean existProjectExpectedStudyCrpOutcome(long projectExpectedStudyCrpOutcomeID) {
    ProjectExpectedStudyCrpOutcome projectExpectedStudyCrpOutcome = this.find(projectExpectedStudyCrpOutcomeID);
    if (projectExpectedStudyCrpOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyCrpOutcome find(long id) {
    return super.find(ProjectExpectedStudyCrpOutcome.class, id);

  }

  @Override
  public List<ProjectExpectedStudyCrpOutcome> findAll() {
    String query = "from " + ProjectExpectedStudyCrpOutcome.class.getName();
    List<ProjectExpectedStudyCrpOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyCrpOutcome save(ProjectExpectedStudyCrpOutcome projectExpectedStudyCrpOutcome) {
    if (projectExpectedStudyCrpOutcome.getId() == null) {
      super.saveEntity(projectExpectedStudyCrpOutcome);
    } else {
      projectExpectedStudyCrpOutcome = super.update(projectExpectedStudyCrpOutcome);
    }


    return projectExpectedStudyCrpOutcome;
  }


}