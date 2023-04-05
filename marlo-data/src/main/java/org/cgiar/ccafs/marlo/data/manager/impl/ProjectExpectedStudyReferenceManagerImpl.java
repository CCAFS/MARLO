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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyReferenceDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyReferenceManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyReference;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyReferenceManagerImpl implements ProjectExpectedStudyReferenceManager {


  private ProjectExpectedStudyReferenceDAO projectExpectedStudyReferenceDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyReferenceManagerImpl(ProjectExpectedStudyReferenceDAO projectExpectedStudyReferenceDAO) {
    this.projectExpectedStudyReferenceDAO = projectExpectedStudyReferenceDAO;


  }

  @Override
  public void deleteProjectExpectedStudyReference(long projectExpectedStudyReferenceId) {

    projectExpectedStudyReferenceDAO.deleteProjectExpectedStudyReference(projectExpectedStudyReferenceId);
  }

  @Override
  public boolean existProjectExpectedStudyReference(long projectExpectedStudyReferenceID) {

    return projectExpectedStudyReferenceDAO.existProjectExpectedStudyReference(projectExpectedStudyReferenceID);
  }

  @Override
  public List<ProjectExpectedStudyReference> findAll() {

    return projectExpectedStudyReferenceDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyReference getProjectExpectedStudyReferenceById(long projectExpectedStudyReferenceID) {

    return projectExpectedStudyReferenceDAO.find(projectExpectedStudyReferenceID);
  }

  @Override
  public ProjectExpectedStudyReference saveProjectExpectedStudyReference(ProjectExpectedStudyReference projectExpectedStudyReference) {

    return projectExpectedStudyReferenceDAO.save(projectExpectedStudyReference);
  }


}
