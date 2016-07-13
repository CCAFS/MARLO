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


import org.cgiar.ccafs.marlo.data.dao.ProjectFocusDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectFocusManagerImpl implements ProjectFocusManager {


  private ProjectFocusDAO projectFocusDAO;
  // Managers


  @Inject
  public ProjectFocusManagerImpl(ProjectFocusDAO projectFocusDAO) {
    this.projectFocusDAO = projectFocusDAO;


  }

  @Override
  public boolean deleteProjectFocus(long projectFocusId) {

    return projectFocusDAO.deleteProjectFocus(projectFocusId);
  }

  @Override
  public boolean existProjectFocus(long projectFocusID) {

    return projectFocusDAO.existProjectFocus(projectFocusID);
  }

  @Override
  public List<ProjectFocus> findAll() {

    return projectFocusDAO.findAll();

  }

  @Override
  public ProjectFocus getProjectFocusById(long projectFocusID) {

    return projectFocusDAO.find(projectFocusID);
  }

  @Override
  public long saveProjectFocus(ProjectFocus projectFocus) {

    return projectFocusDAO.save(projectFocus);
  }


}
