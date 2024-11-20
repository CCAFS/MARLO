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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationReferenceComplementarySolutionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationReferenceComplementarySolutionManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReferenceComplementarySolution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationReferenceComplementarySolutionManagerImpl implements ProjectInnovationReferenceComplementarySolutionManager {


  private ProjectInnovationReferenceComplementarySolutionDAO projectInnovationReferenceComplementarySolutionDAO;
  // Managers


  @Inject
  public ProjectInnovationReferenceComplementarySolutionManagerImpl(ProjectInnovationReferenceComplementarySolutionDAO projectInnovationReferenceComplementarySolutionDAO) {
    this.projectInnovationReferenceComplementarySolutionDAO = projectInnovationReferenceComplementarySolutionDAO;


  }

  @Override
  public void deleteProjectInnovationReferenceComplementarySolution(long projectInnovationReferenceComplementarySolutionId) {

    projectInnovationReferenceComplementarySolutionDAO.deleteProjectInnovationReferenceComplementarySolution(projectInnovationReferenceComplementarySolutionId);
  }

  @Override
  public boolean existProjectInnovationReferenceComplementarySolution(long projectInnovationReferenceComplementarySolutionID) {

    return projectInnovationReferenceComplementarySolutionDAO.existProjectInnovationReferenceComplementarySolution(projectInnovationReferenceComplementarySolutionID);
  }

  @Override
  public List<ProjectInnovationReferenceComplementarySolution> findAll() {

    return projectInnovationReferenceComplementarySolutionDAO.findAll();

  }

  @Override
  public ProjectInnovationReferenceComplementarySolution getProjectInnovationReferenceComplementarySolutionById(long projectInnovationReferenceComplementarySolutionID) {

    return projectInnovationReferenceComplementarySolutionDAO.find(projectInnovationReferenceComplementarySolutionID);
  }

  @Override
  public ProjectInnovationReferenceComplementarySolution saveProjectInnovationReferenceComplementarySolution(ProjectInnovationReferenceComplementarySolution projectInnovationReferenceComplementarySolution) {

    return projectInnovationReferenceComplementarySolutionDAO.save(projectInnovationReferenceComplementarySolution);
  }


}
