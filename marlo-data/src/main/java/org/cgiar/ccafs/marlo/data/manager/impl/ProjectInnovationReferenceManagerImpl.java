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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationReferenceDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationReferenceManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReference;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationReferenceManagerImpl implements ProjectInnovationReferenceManager {


  private ProjectInnovationReferenceDAO projectInnovationReferenceDAO;
  // Managers


  @Inject
  public ProjectInnovationReferenceManagerImpl(ProjectInnovationReferenceDAO projectInnovationReferenceDAO) {
    this.projectInnovationReferenceDAO = projectInnovationReferenceDAO;


  }

  @Override
  public void deleteProjectInnovationReference(long projectInnovationReferenceId) {

    projectInnovationReferenceDAO.deleteProjectInnovationReference(projectInnovationReferenceId);
  }

  @Override
  public boolean existProjectInnovationReference(long projectInnovationReferenceID) {

    return projectInnovationReferenceDAO.existProjectInnovationReference(projectInnovationReferenceID);
  }

  @Override
  public List<ProjectInnovationReference> findAll() {

    return projectInnovationReferenceDAO.findAll();

  }

  @Override
  public ProjectInnovationReference getProjectInnovationReferenceById(long projectInnovationReferenceID) {

    return projectInnovationReferenceDAO.find(projectInnovationReferenceID);
  }

  @Override
  public ProjectInnovationReference saveProjectInnovationReference(ProjectInnovationReference projectInnovationReference) {

    return projectInnovationReferenceDAO.save(projectInnovationReference);
  }


}
