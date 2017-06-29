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

import com.google.inject.Inject;

public class ProjectOtherContributionMySQLDAO implements ProjectOtherContributionDAO {

  private StandardDAO dao;

  @Inject
  public ProjectOtherContributionMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteProjectOtherContribution(long projectOtherContributionId) {
    ProjectOtherContribution projectOtherContribution = this.find(projectOtherContributionId);
    projectOtherContribution.setActive(false);
    return this.save(projectOtherContribution) > 0;
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
    return dao.find(ProjectOtherContribution.class, id);

  }

  @Override
  public List<ProjectOtherContribution> findAll() {
    String query = "from " + ProjectOtherContribution.class.getName() + " where is_active=1";
    List<ProjectOtherContribution> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectOtherContribution projectOtherContribution) {
    if (projectOtherContribution.getId() == null) {
      dao.save(projectOtherContribution);
    } else {
      dao.update(projectOtherContribution);
    }


    return projectOtherContribution.getId();
  }


}