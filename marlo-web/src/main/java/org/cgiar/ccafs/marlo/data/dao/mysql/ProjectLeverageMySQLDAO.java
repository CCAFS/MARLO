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

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class ProjectLeverageMySQLDAO extends AbstractMarloDAO<ProjectLeverage, Long> implements ProjectLeverageDAO {


  @Inject
  public ProjectLeverageMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectLeverage(long projectLeverageId) {
    ProjectLeverage projectLeverage = this.find(projectLeverageId);
    projectLeverage.setActive(false);
    this.save(projectLeverage);
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
    return super.find(ProjectLeverage.class, id);

  }

  @Override
  public List<ProjectLeverage> findAll() {
    String query = "from " + ProjectLeverage.class.getName() + " where is_active=1";
    List<ProjectLeverage> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectLeverage save(ProjectLeverage projectLeverage) {
    if (projectLeverage.getId() == null) {
      super.saveEntity(projectLeverage);
    } else {
      projectLeverage = super.update(projectLeverage);
    }


    return projectLeverage;
  }


}