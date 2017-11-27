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


import org.cgiar.ccafs.marlo.data.dao.ProjectHighligthTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectHighligthTypeManager;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightType;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectHighligthTypeManagerImpl implements ProjectHighligthTypeManager {


  private ProjectHighligthTypeDAO projectHighligthTypeDAO;
  // Managers


  @Inject
  public ProjectHighligthTypeManagerImpl(ProjectHighligthTypeDAO projectHighligthTypeDAO) {
    this.projectHighligthTypeDAO = projectHighligthTypeDAO;


  }

  @Override
  public void deleteProjectHighligthType(long projectHighligthTypeId) {

    projectHighligthTypeDAO.deleteProjectHighligthType(projectHighligthTypeId);
  }

  @Override
  public boolean existProjectHighligthType(long projectHighligthTypeID) {

    return projectHighligthTypeDAO.existProjectHighligthType(projectHighligthTypeID);
  }

  @Override
  public List<ProjectHighlightType> findAll() {

    return projectHighligthTypeDAO.findAll();

  }

  @Override
  public ProjectHighlightType getProjectHighligthTypeById(long projectHighligthTypeID) {

    return projectHighligthTypeDAO.find(projectHighligthTypeID);
  }

  @Override
  public ProjectHighlightType saveProjectHighligthType(ProjectHighlightType projectHighlightType) {

    return projectHighligthTypeDAO.save(projectHighlightType);
  }


}
