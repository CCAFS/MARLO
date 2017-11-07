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

import org.cgiar.ccafs.marlo.data.dao.ProjectBudgetsCluserActvityDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsCluserActvity;

import java.util.List;

import com.google.inject.Inject;

public class ProjectBudgetsCluserActvityMySQLDAO implements ProjectBudgetsCluserActvityDAO {

  private StandardDAO dao;

  @Inject
  public ProjectBudgetsCluserActvityMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }


  @Override
  public boolean deleteProjectBudgetsCluserActvity(long projectBudgetsCluserActvityId) {
    ProjectBudgetsCluserActvity projectBudgetsCluserActvity = this.find(projectBudgetsCluserActvityId);
    projectBudgetsCluserActvity.setActive(false);
    boolean result = dao.update(projectBudgetsCluserActvity);


    return result;
  }

  @Override
  public boolean existProjectBudgetsCluserActvity(long projectBudgetsCluserActvityID) {
    ProjectBudgetsCluserActvity projectBudgetsCluserActvity = this.find(projectBudgetsCluserActvityID);
    if (projectBudgetsCluserActvity == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectBudgetsCluserActvity find(long id) {
    return dao.find(ProjectBudgetsCluserActvity.class, id);

  }

  @Override
  public List<ProjectBudgetsCluserActvity> findAll() {
    String query = "from " + ProjectBudgetsCluserActvity.class.getName() + " where is_active=1";
    List<ProjectBudgetsCluserActvity> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectBudgetsCluserActvity projectBudgetsCluserActvity) {
    if (projectBudgetsCluserActvity.getId() == null) {
      dao.save(projectBudgetsCluserActvity);
    } else {
      dao.update(projectBudgetsCluserActvity);
    }


    return projectBudgetsCluserActvity.getId();
  }


}