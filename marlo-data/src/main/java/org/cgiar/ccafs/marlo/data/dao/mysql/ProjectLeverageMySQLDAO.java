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

import org.cgiar.ccafs.marlo.data.dao.ProjectLeverageDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectLeverage;

import java.util.List;

import com.google.inject.Inject;

public class ProjectLeverageMySQLDAO implements ProjectLeverageDAO {

  private StandardDAO dao;

  @Inject
  public ProjectLeverageMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteProjectLeverage(long projectLeverageId) {
    ProjectLeverage projectLeverage = this.find(projectLeverageId);
    projectLeverage.setActive(false);
    return this.save(projectLeverage) > 0;
  }

  @Override
  public boolean existProjectLeverage(long projectLeverageID) {
    ProjectLeverage projectLeverage = this.find(projectLeverageID);
    if (projectLeverage == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectLeverage find(long id) {
    return dao.find(ProjectLeverage.class, id);

  }

  @Override
  public List<ProjectLeverage> findAll() {
    String query = "from " + ProjectLeverage.class.getName() + " where is_active=1";
    List<ProjectLeverage> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectLeverage projectLeverage) {
    if (projectLeverage.getId() == null) {
      dao.save(projectLeverage);
    } else {
      dao.update(projectLeverage);
    }


    return projectLeverage.getId();
  }


}