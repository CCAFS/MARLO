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

import org.cgiar.ccafs.marlo.data.dao.ProjectBilateralCofinancingDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectBilateralCofinancing;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.ibm.icu.text.SimpleDateFormat;

public class ProjectBilateralCofinancingMySQLDAO implements ProjectBilateralCofinancingDAO {

  private StandardDAO dao;

  @Inject
  public ProjectBilateralCofinancingMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteProjectBilateralCofinancing(long projectBilateralCofinancingId) {
    ProjectBilateralCofinancing projectBilateralCofinancing = this.find(projectBilateralCofinancingId);
    projectBilateralCofinancing.setActive(false);
    return this.save(projectBilateralCofinancing) > 0;
  }

  @Override
  public boolean existProjectBilateralCofinancing(long projectBilateralCofinancingID) {
    ProjectBilateralCofinancing projectBilateralCofinancing = this.find(projectBilateralCofinancingID);
    if (projectBilateralCofinancing == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectBilateralCofinancing find(long id) {
    return dao.find(ProjectBilateralCofinancing.class, id);

  }

  @Override
  public List<ProjectBilateralCofinancing> findAll() {
    String query = "from " + ProjectBilateralCofinancing.class.getName() + " where is_active=1";
    List<ProjectBilateralCofinancing> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectBilateralCofinancing projectBilateralCofinancing) {
    if (projectBilateralCofinancing.getId() == null) {
      dao.save(projectBilateralCofinancing);
    } else {
      dao.update(projectBilateralCofinancing);
    }


    return projectBilateralCofinancing.getId();
  }

  @Override
  public List<ProjectBilateralCofinancing> searchProject(String searchValue, long institutionID, int year) {
    StringBuilder query = new StringBuilder();
    query.append("from " + ProjectBilateralCofinancing.class.getName());
    query.append(" where title like '%" + searchValue + "%' ");
    query.append("OR id like '%" + searchValue + "%' ");
    List<ProjectBilateralCofinancing> listProjects = dao.findAll(query.toString());
    SimpleDateFormat df = new SimpleDateFormat("yyyy");
    return listProjects.stream()
      .filter(c -> c.isActive() && c.getLiaisonInstitution().getInstitution().getId().longValue() == institutionID
        && year <= Integer.parseInt(df.format(c.getEndDate())))
      .collect(Collectors.toList());
  }
}