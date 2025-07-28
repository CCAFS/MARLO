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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationComplementarySolutionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationComplementarySolutionManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationComplementarySolution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationComplementarySolutionManagerImpl
  implements ProjectInnovationComplementarySolutionManager {


  private ProjectInnovationComplementarySolutionDAO projectInnovationComplementarySolutionDAO;
  // Managers


  @Inject
  public ProjectInnovationComplementarySolutionManagerImpl(
    ProjectInnovationComplementarySolutionDAO projectInnovationComplementarySolutionDAO) {
    this.projectInnovationComplementarySolutionDAO = projectInnovationComplementarySolutionDAO;


  }

  @Override
  public void deleteProjectInnovationComplementarySolution(long projectInnovationComplementarySolutionId) {

    projectInnovationComplementarySolutionDAO
      .deleteProjectInnovationComplementarySolution(projectInnovationComplementarySolutionId);
  }

  @Override
  public boolean existProjectInnovationComplementarySolution(long projectInnovationComplementarySolutionID) {

    return projectInnovationComplementarySolutionDAO
      .existProjectInnovationComplementarySolution(projectInnovationComplementarySolutionID);
  }

  @Override
  public List<ProjectInnovationComplementarySolution> findAll() {

    return projectInnovationComplementarySolutionDAO.findAll();

  }

  @Override
  public ProjectInnovationComplementarySolution
    getProjectInnovationComplementarySolutionById(long projectInnovationComplementarySolutionID) {

    return projectInnovationComplementarySolutionDAO.find(projectInnovationComplementarySolutionID);
  }

  @Override
  public List<ProjectInnovationComplementarySolution>
    getProjectInnovationComplementarySolutionByInnovationAndPhase(long innovationID, long phaseID) {
    return projectInnovationComplementarySolutionDAO
      .getProjectInnovationComplementarySolutionByInnovationAndPhase(innovationID, phaseID);
  }

  @Override
  public ProjectInnovationComplementarySolution saveProjectInnovationComplementarySolution(
    ProjectInnovationComplementarySolution projectInnovationComplementarySolution) {

    return projectInnovationComplementarySolutionDAO.save(projectInnovationComplementarySolution);
  }


}
