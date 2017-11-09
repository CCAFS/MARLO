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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ProjectLocationElementTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectLocationElementTypeManager;
import org.cgiar.ccafs.marlo.data.model.ProjectLocationElementType;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectLocationElementTypeManagerImpl implements ProjectLocationElementTypeManager {


  private ProjectLocationElementTypeDAO projectLocationElementTypeDAO;
  // Managers


  @Inject
  public ProjectLocationElementTypeManagerImpl(ProjectLocationElementTypeDAO projectLocationElementTypeDAO) {
    this.projectLocationElementTypeDAO = projectLocationElementTypeDAO;


  }

  @Override
  public void deleteProjectLocationElementType(long projectLocationElementTypeId) {

    projectLocationElementTypeDAO.deleteProjectLocationElementType(projectLocationElementTypeId);
  }

  @Override
  public boolean existProjectLocationElementType(long projectLocationElementTypeID) {

    return projectLocationElementTypeDAO.existProjectLocationElementType(projectLocationElementTypeID);
  }

  @Override
  public List<ProjectLocationElementType> findAll() {

    return projectLocationElementTypeDAO.findAll();

  }

  @Override
  public ProjectLocationElementType getByProjectAndElementType(long projectId, long elementTypeId) {
    return projectLocationElementTypeDAO.getByProjectAndElementType(projectId, elementTypeId);
  }

  @Override
  public ProjectLocationElementType getProjectLocationElementTypeById(long projectLocationElementTypeID) {

    return projectLocationElementTypeDAO.find(projectLocationElementTypeID);
  }

  @Override
  public ProjectLocationElementType saveProjectLocationElementType(ProjectLocationElementType projectLocationElementType) {

    return projectLocationElementTypeDAO.save(projectLocationElementType);
  }


}
