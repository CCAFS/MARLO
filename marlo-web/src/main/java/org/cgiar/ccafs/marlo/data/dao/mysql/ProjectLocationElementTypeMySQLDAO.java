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

import org.cgiar.ccafs.marlo.data.dao.ProjectLocationElementTypeDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectLocationElementType;

import java.util.List;

import com.google.inject.Inject;

public class ProjectLocationElementTypeMySQLDAO implements ProjectLocationElementTypeDAO {

  private StandardDAO dao;

  @Inject
  public ProjectLocationElementTypeMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteProjectLocationElementType(long projectLocationElementTypeId) {
    ProjectLocationElementType projectLocationElementType = this.find(projectLocationElementTypeId);
    return dao.delete(projectLocationElementType);
  }

  @Override
  public boolean existProjectLocationElementType(long projectLocationElementTypeID) {
    ProjectLocationElementType projectLocationElementType = this.find(projectLocationElementTypeID);
    if (projectLocationElementType == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectLocationElementType find(long id) {
    return dao.find(ProjectLocationElementType.class, id);

  }

  @Override
  public List<ProjectLocationElementType> findAll() {
    String query = "from " + ProjectLocationElementType.class.getName();
    List<ProjectLocationElementType> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectLocationElementType projectLocationElementType) {
    if (projectLocationElementType.getId() == null) {
      dao.save(projectLocationElementType);
    } else {
      dao.update(projectLocationElementType);
    }


    return projectLocationElementType.getId();
  }


}