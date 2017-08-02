/*****************************************************************
 * <<<<<<< HEAD
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * =======
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * >>>>>>> staging
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

import org.cgiar.ccafs.marlo.data.dao.ProjectFurtherContributionDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectFurtherContribution;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class ProjectFurtherContributionMySQLDAO extends AbstractMarloDAO implements ProjectFurtherContributionDAO {


  @Inject
  public ProjectFurtherContributionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteProjectFurtherContribution(long projectFurtherContributionId) {
    ProjectFurtherContribution projectFurtherContribution = this.find(projectFurtherContributionId);
    projectFurtherContribution.setActive(false);
    return this.save(projectFurtherContribution) > 0;
  }

  @Override
  public boolean existProjectFurtherContribution(long projectFurtherContributionID) {
    ProjectFurtherContribution projectFurtherContribution = this.find(projectFurtherContributionID);
    if (projectFurtherContribution == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectFurtherContribution find(long id) {
    return super.find(ProjectFurtherContribution.class, id);
  }

  @Override
  public List<ProjectFurtherContribution> findAll() {
    String query = "from " + ProjectFurtherContribution.class.getName() + " where is_active=1";
    List<ProjectFurtherContribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectFurtherContribution projectFurtherContribution) {
    if (projectFurtherContribution.getId() == null) {
      super.save(projectFurtherContribution);
    } else {
      super.update(projectFurtherContribution);
    }


    return projectFurtherContribution.getId();
  }


}