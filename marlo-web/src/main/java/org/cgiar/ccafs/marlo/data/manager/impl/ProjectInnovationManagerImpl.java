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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectInnovationManagerImpl implements ProjectInnovationManager {


  private ProjectInnovationDAO projectInnovationDAO;
  // Managers


  @Inject
  public ProjectInnovationManagerImpl(ProjectInnovationDAO projectInnovationDAO) {
    this.projectInnovationDAO = projectInnovationDAO;


  }

  @Override
  public void deleteProjectInnovation(long projectInnovationId) {

    projectInnovationDAO.deleteProjectInnovation(projectInnovationId);
  }

  @Override
  public boolean existProjectInnovation(long projectInnovationID) {

    return projectInnovationDAO.existProjectInnovation(projectInnovationID);
  }

  @Override
  public List<ProjectInnovation> findAll() {

    return projectInnovationDAO.findAll();

  }

  @Override
  public ProjectInnovation getProjectInnovationById(long projectInnovationID) {

    return projectInnovationDAO.find(projectInnovationID);
  }

  @Override
  public ProjectInnovation saveProjectInnovation(ProjectInnovation projectInnovation) {
    return projectInnovationDAO.save(projectInnovation);
  }

  @Override
  public ProjectInnovation saveProjectInnovation(ProjectInnovation projectInnovation, String section,
    List<String> relationsName, Phase phase) {
    return projectInnovationDAO.save(projectInnovation, section, relationsName, phase);
  }


}
