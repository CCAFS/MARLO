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

import org.cgiar.ccafs.marlo.data.dao.ProjectOtherContributionDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectOtherContribution;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class ProjectOtherContributionMySQLDAO extends AbstractMarloDAO<ProjectOtherContribution, Long> implements ProjectOtherContributionDAO {


  @Inject
  public ProjectOtherContributionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectOtherContribution(long projectOtherContributionId) {
    ProjectOtherContribution projectOtherContribution = this.find(projectOtherContributionId);
    projectOtherContribution.setActive(false);
    this.save(projectOtherContribution);
  }

  @Override
  public boolean existProjectOtherContribution(long projectOtherContributionID) {
    ProjectOtherContribution projectOtherContribution = this.find(projectOtherContributionID);
    if (projectOtherContribution == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectOtherContribution find(long id) {
    return super.find(ProjectOtherContribution.class, id);

  }

  @Override
  public List<ProjectOtherContribution> findAll() {
    String query = "from " + ProjectOtherContribution.class.getName() + " where is_active=1";
    List<ProjectOtherContribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectOtherContribution save(ProjectOtherContribution projectOtherContribution) {
    if (projectOtherContribution.getId() == null) {
      super.saveEntity(projectOtherContribution);
    } else {
      projectOtherContribution = super.update(projectOtherContribution);
    }


    return projectOtherContribution;
  }


}