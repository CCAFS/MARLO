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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationActorDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationActorManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationActor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationActorManagerImpl implements ProjectInnovationActorManager {


  private ProjectInnovationActorDAO projectInnovationActorDAO;
  // Managers


  @Inject
  public ProjectInnovationActorManagerImpl(ProjectInnovationActorDAO projectInnovationActorDAO) {
    this.projectInnovationActorDAO = projectInnovationActorDAO;


  }

  @Override
  public void deleteProjectInnovationActor(long projectInnovationActorId) {

    projectInnovationActorDAO.deleteProjectInnovationActor(projectInnovationActorId);
  }

  @Override
  public boolean existProjectInnovationActor(long projectInnovationActorID) {

    return projectInnovationActorDAO.existProjectInnovationActor(projectInnovationActorID);
  }

  @Override
  public List<ProjectInnovationActor> findAll() {

    return projectInnovationActorDAO.findAll();

  }

  @Override
  public ProjectInnovationActor getProjectInnovationActorById(long projectInnovationActorID) {

    return projectInnovationActorDAO.find(projectInnovationActorID);
  }

  @Override
  public ProjectInnovationActor saveProjectInnovationActor(ProjectInnovationActor projectInnovationActor) {

    return projectInnovationActorDAO.save(projectInnovationActor);
  }


}
