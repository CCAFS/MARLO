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


import org.cgiar.ccafs.marlo.data.dao.ProjectOutcomePandrDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomePandrManager;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcomePandr;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectOutcomePandrManagerImpl implements ProjectOutcomePandrManager {


  private ProjectOutcomePandrDAO projectOutcomePandrDAO;
  // Managers


  @Inject
  public ProjectOutcomePandrManagerImpl(ProjectOutcomePandrDAO projectOutcomePandrDAO) {
    this.projectOutcomePandrDAO = projectOutcomePandrDAO;


  }

  @Override
  public boolean deleteProjectOutcomePandr(long projectOutcomePandrId) {

    return projectOutcomePandrDAO.deleteProjectOutcomePandr(projectOutcomePandrId);
  }

  @Override
  public boolean existProjectOutcomePandr(long projectOutcomePandrID) {

    return projectOutcomePandrDAO.existProjectOutcomePandr(projectOutcomePandrID);
  }

  @Override
  public List<ProjectOutcomePandr> findAll() {

    return projectOutcomePandrDAO.findAll();

  }

  @Override
  public ProjectOutcomePandr getProjectOutcomePandrById(long projectOutcomePandrID) {

    return projectOutcomePandrDAO.find(projectOutcomePandrID);
  }

  @Override
  public long saveProjectOutcomePandr(ProjectOutcomePandr projectOutcomePandr) {

    return projectOutcomePandrDAO.save(projectOutcomePandr);
  }


}
