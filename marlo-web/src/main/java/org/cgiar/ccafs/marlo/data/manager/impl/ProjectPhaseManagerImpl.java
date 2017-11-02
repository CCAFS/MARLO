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


import org.cgiar.ccafs.marlo.data.dao.ProjectPhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPhaseManager;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectPhaseManagerImpl implements ProjectPhaseManager {


  private ProjectPhaseDAO projectPhaseDAO;
  // Managers


  @Inject
  public ProjectPhaseManagerImpl(ProjectPhaseDAO projectPhaseDAO) {
    this.projectPhaseDAO = projectPhaseDAO;


  }

  @Override
  public void deleteProjectPhase(long projectPhaseId) {

    projectPhaseDAO.deleteProjectPhase(projectPhaseId);
  }

  @Override
  public boolean existProjectPhase(long projectPhaseID) {

    return projectPhaseDAO.existProjectPhase(projectPhaseID);
  }

  @Override
  public List<ProjectPhase> findAll() {

    return projectPhaseDAO.findAll();

  }

  @Override
  public ProjectPhase getProjectPhaseById(long projectPhaseID) {

    return projectPhaseDAO.find(projectPhaseID);
  }

  @Override
  public ProjectPhase saveProjectPhase(ProjectPhase projectPhase) {

    return projectPhaseDAO.save(projectPhase);
  }


}
