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

import org.cgiar.ccafs.marlo.data.dao.ProjectHighligthTypeDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectHighligthType;

import java.util.List;

import com.google.inject.Inject;

public class ProjectHighligthTypeMySQLDAO implements ProjectHighligthTypeDAO {

  private StandardDAO dao;

  @Inject
  public ProjectHighligthTypeMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteProjectHighligthType(long projectHighligthTypeId) {
    ProjectHighligthType projectHighligthType = this.find(projectHighligthTypeId);
    projectHighligthType.setActive(false);
    return this.save(projectHighligthType) > 0;
  }

  @Override
  public boolean existProjectHighligthType(long projectHighligthTypeID) {
    ProjectHighligthType projectHighligthType = this.find(projectHighligthTypeID);
    if (projectHighligthType == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectHighligthType find(long id) {
    return dao.find(ProjectHighligthType.class, id);

  }

  @Override
  public List<ProjectHighligthType> findAll() {
    String query = "from " + ProjectHighligthType.class.getName() + " where is_active=1";
    List<ProjectHighligthType> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectHighligthType projectHighligthType) {
    if (projectHighligthType.getId() == null) {
      dao.save(projectHighligthType);
    } else {
      dao.update(projectHighligthType);
    }


    return projectHighligthType.getId();
  }


}