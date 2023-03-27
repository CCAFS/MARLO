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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationCrpOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrpOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationCrpOutcomeMySQLDAO extends AbstractMarloDAO<ProjectInnovationCrpOutcome, Long>
  implements ProjectInnovationCrpOutcomeDAO {


  @Inject
  public ProjectInnovationCrpOutcomeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationCrpOutcome(long projectInnovationCrpOutcomeId) {
    ProjectInnovationCrpOutcome projectInnovationCrpOutcome = this.find(projectInnovationCrpOutcomeId);
    this.delete(projectInnovationCrpOutcome);
  }

  @Override
  public boolean existProjectInnovationCrpOutcome(long projectInnovationCrpOutcomeID) {
    ProjectInnovationCrpOutcome projectInnovationCrpOutcome = this.find(projectInnovationCrpOutcomeID);
    if (projectInnovationCrpOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationCrpOutcome find(long id) {
    return super.find(ProjectInnovationCrpOutcome.class, id);

  }

  @Override
  public List<ProjectInnovationCrpOutcome> findAll() {
    String query = "from " + ProjectInnovationCrpOutcome.class.getName();
    List<ProjectInnovationCrpOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInnovationCrpOutcome save(ProjectInnovationCrpOutcome projectInnovationCrpOutcome) {
    if (projectInnovationCrpOutcome.getId() == null) {
      super.saveEntity(projectInnovationCrpOutcome);
    } else {
      projectInnovationCrpOutcome = super.update(projectInnovationCrpOutcome);
    }


    return projectInnovationCrpOutcome;
  }


}