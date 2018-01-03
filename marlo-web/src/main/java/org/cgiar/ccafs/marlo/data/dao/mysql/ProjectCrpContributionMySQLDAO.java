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

import org.cgiar.ccafs.marlo.data.dao.ProjectCrpContributionDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectCrpContribution;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class ProjectCrpContributionMySQLDAO extends AbstractMarloDAO<ProjectCrpContribution, Long> implements ProjectCrpContributionDAO {


  @Inject
  public ProjectCrpContributionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectCrpContribution(long projectCrpContributionId) {
    ProjectCrpContribution projectCrpContribution = this.find(projectCrpContributionId);
    projectCrpContribution.setActive(false);
    this.save(projectCrpContribution);
  }

  @Override
  public boolean existProjectCrpContribution(long projectCrpContributionID) {
    ProjectCrpContribution projectCrpContribution = this.find(projectCrpContributionID);
    if (projectCrpContribution == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectCrpContribution find(long id) {
    return super.find(ProjectCrpContribution.class, id);

  }

  @Override
  public List<ProjectCrpContribution> findAll() {
    String query = "from " + ProjectCrpContribution.class.getName() + " where is_active=1";
    List<ProjectCrpContribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectCrpContribution save(ProjectCrpContribution projectCrpContribution) {
    if (projectCrpContribution.getId() == null) {
      super.saveEntity(projectCrpContribution);
    } else {
      projectCrpContribution = super.update(projectCrpContribution);
    }


    return projectCrpContribution;
  }


}