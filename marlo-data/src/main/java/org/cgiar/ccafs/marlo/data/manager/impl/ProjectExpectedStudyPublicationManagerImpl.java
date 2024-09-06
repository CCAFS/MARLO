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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPublicationDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPublicationManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPublication;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyPublicationManagerImpl implements ProjectExpectedStudyPublicationManager {


  private ProjectExpectedStudyPublicationDAO projectExpectedStudyPublicationDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyPublicationManagerImpl(ProjectExpectedStudyPublicationDAO projectExpectedStudyPublicationDAO) {
    this.projectExpectedStudyPublicationDAO = projectExpectedStudyPublicationDAO;


  }

  @Override
  public void deleteProjectExpectedStudyPublication(long projectExpectedStudyPublicationId) {

    projectExpectedStudyPublicationDAO.deleteProjectExpectedStudyPublication(projectExpectedStudyPublicationId);
  }

  @Override
  public boolean existProjectExpectedStudyPublication(long projectExpectedStudyPublicationID) {

    return projectExpectedStudyPublicationDAO.existProjectExpectedStudyPublication(projectExpectedStudyPublicationID);
  }

  @Override
  public List<ProjectExpectedStudyPublication> findAll() {

    return projectExpectedStudyPublicationDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyPublication getProjectExpectedStudyPublicationById(long projectExpectedStudyPublicationID) {

    return projectExpectedStudyPublicationDAO.find(projectExpectedStudyPublicationID);
  }

  @Override
  public ProjectExpectedStudyPublication saveProjectExpectedStudyPublication(ProjectExpectedStudyPublication projectExpectedStudyPublication) {

    return projectExpectedStudyPublicationDAO.save(projectExpectedStudyPublication);
  }


}
