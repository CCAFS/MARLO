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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationFunctionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationFunctionManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationFunction;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationFunctionManagerImpl implements ProjectInnovationFunctionManager {


  private ProjectInnovationFunctionDAO projectInnovationFunctionDAO;
  // Managers


  @Inject
  public ProjectInnovationFunctionManagerImpl(ProjectInnovationFunctionDAO projectInnovationFunctionDAO) {
    this.projectInnovationFunctionDAO = projectInnovationFunctionDAO;


  }

  @Override
  public void deleteProjectInnovationFunction(long projectInnovationFunctionId) {

    projectInnovationFunctionDAO.deleteProjectInnovationFunction(projectInnovationFunctionId);
  }

  @Override
  public boolean existProjectInnovationFunction(long projectInnovationFunctionID) {

    return projectInnovationFunctionDAO.existProjectInnovationFunction(projectInnovationFunctionID);
  }

  @Override
  public List<ProjectInnovationFunction> findAll() {

    return projectInnovationFunctionDAO.findAll();

  }

  @Override
  public ProjectInnovationFunction getProjectInnovationFunctionById(long projectInnovationFunctionID) {

    return projectInnovationFunctionDAO.find(projectInnovationFunctionID);
  }

  @Override
  public ProjectInnovationFunction saveProjectInnovationFunction(ProjectInnovationFunction projectInnovationFunction) {

    return projectInnovationFunctionDAO.save(projectInnovationFunction);
  }


}
