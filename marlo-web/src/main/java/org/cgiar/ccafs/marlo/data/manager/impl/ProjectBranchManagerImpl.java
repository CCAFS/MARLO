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


import org.cgiar.ccafs.marlo.data.dao.ProjectBranchDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectBranchManager;
import org.cgiar.ccafs.marlo.data.model.ProjectBranch;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectBranchManagerImpl implements ProjectBranchManager {


  private ProjectBranchDAO projectBranchDAO;
  // Managers


  @Inject
  public ProjectBranchManagerImpl(ProjectBranchDAO projectBranchDAO) {
    this.projectBranchDAO = projectBranchDAO;


  }

  @Override
  public boolean deleteProjectBranch(long projectBranchId) {

    return projectBranchDAO.deleteProjectBranch(projectBranchId);
  }

  @Override
  public boolean existProjectBranch(long projectBranchID) {

    return projectBranchDAO.existProjectBranch(projectBranchID);
  }

  @Override
  public List<ProjectBranch> findAll() {

    return projectBranchDAO.findAll();

  }

  @Override
  public ProjectBranch getProjectBranchById(long projectBranchID) {

    return projectBranchDAO.find(projectBranchID);
  }

  @Override
  public long saveProjectBranch(ProjectBranch projectBranch) {

    return projectBranchDAO.save(projectBranch);
  }


}
