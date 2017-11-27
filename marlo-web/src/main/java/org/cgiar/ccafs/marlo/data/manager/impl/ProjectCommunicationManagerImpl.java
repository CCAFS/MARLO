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


import org.cgiar.ccafs.marlo.data.dao.ProjectCommunicationDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectCommunicationManager;
import org.cgiar.ccafs.marlo.data.model.ProjectCommunication;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectCommunicationManagerImpl implements ProjectCommunicationManager {


  private ProjectCommunicationDAO projectCommunicationDAO;
  // Managers


  @Inject
  public ProjectCommunicationManagerImpl(ProjectCommunicationDAO projectCommunicationDAO) {
    this.projectCommunicationDAO = projectCommunicationDAO;


  }

  @Override
  public void deleteProjectCommunication(long projectCommunicationId) {

    projectCommunicationDAO.deleteProjectCommunication(projectCommunicationId);
  }

  @Override
  public boolean existProjectCommunication(long projectCommunicationID) {

    return projectCommunicationDAO.existProjectCommunication(projectCommunicationID);
  }

  @Override
  public List<ProjectCommunication> findAll() {

    return projectCommunicationDAO.findAll();

  }

  @Override
  public ProjectCommunication getProjectCommunicationById(long projectCommunicationID) {

    return projectCommunicationDAO.find(projectCommunicationID);
  }

  @Override
  public ProjectCommunication saveProjectCommunication(ProjectCommunication projectCommunication) {

    return projectCommunicationDAO.save(projectCommunication);
  }


}
