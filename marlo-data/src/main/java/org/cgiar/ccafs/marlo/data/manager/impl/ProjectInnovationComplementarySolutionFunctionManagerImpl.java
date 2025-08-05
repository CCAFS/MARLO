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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationComplementarySolutionFunctionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationComplementarySolutionFunctionManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationComplementarySolutionFunction;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationComplementarySolutionFunctionManagerImpl
  implements ProjectInnovationComplementarySolutionFunctionManager {


  private ProjectInnovationComplementarySolutionFunctionDAO projectInnovationComplementarySolutionFunctionDAO;
  // Managers


  @Inject
  public ProjectInnovationComplementarySolutionFunctionManagerImpl(
    ProjectInnovationComplementarySolutionFunctionDAO projectInnovationComplementarySolutionFunctionDAO) {
    this.projectInnovationComplementarySolutionFunctionDAO = projectInnovationComplementarySolutionFunctionDAO;


  }

  @Override
  public void
    deleteProjectInnovationComplementarySolutionFunction(long projectInnovationComplementarySolutionFunctionId) {

    projectInnovationComplementarySolutionFunctionDAO
      .deleteProjectInnovationComplementarySolutionFunction(projectInnovationComplementarySolutionFunctionId);
  }

  @Override
  public boolean
    existProjectInnovationComplementarySolutionFunction(long projectInnovationComplementarySolutionFunctionID) {

    return projectInnovationComplementarySolutionFunctionDAO
      .existProjectInnovationComplementarySolutionFunction(projectInnovationComplementarySolutionFunctionID);
  }

  @Override
  public List<ProjectInnovationComplementarySolutionFunction> findAll() {

    return projectInnovationComplementarySolutionFunctionDAO.findAll();

  }

  @Override
  public List<ProjectInnovationComplementarySolutionFunction>
    getProjectInnovationComplementarySolutionFunctionByComplementarySolutionId(long complementarySolutionID) {
    return projectInnovationComplementarySolutionFunctionDAO
      .getProjectInnovationComplementarySolutionFunctionByComplementarySolutionId(complementarySolutionID);
  }

  @Override
  public ProjectInnovationComplementarySolutionFunction
    getProjectInnovationComplementarySolutionFunctionById(long projectInnovationComplementarySolutionFunctionID) {

    return projectInnovationComplementarySolutionFunctionDAO.find(projectInnovationComplementarySolutionFunctionID);
  }

  @Override
  public ProjectInnovationComplementarySolutionFunction saveProjectInnovationComplementarySolutionFunction(
    ProjectInnovationComplementarySolutionFunction projectInnovationComplementarySolutionFunction) {

    return projectInnovationComplementarySolutionFunctionDAO.save(projectInnovationComplementarySolutionFunction);
  }


}
