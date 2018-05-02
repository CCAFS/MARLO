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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyInstitutionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInstitutionManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInstitution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyInstitutionManagerImpl implements ProjectExpectedStudyInstitutionManager {


  private ProjectExpectedStudyInstitutionDAO projectExpectedStudyInstitutionDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyInstitutionManagerImpl(ProjectExpectedStudyInstitutionDAO projectExpectedStudyInstitutionDAO) {
    this.projectExpectedStudyInstitutionDAO = projectExpectedStudyInstitutionDAO;


  }

  @Override
  public void deleteProjectExpectedStudyInstitution(long projectExpectedStudyInstitutionId) {

    projectExpectedStudyInstitutionDAO.deleteProjectExpectedStudyInstitution(projectExpectedStudyInstitutionId);
  }

  @Override
  public boolean existProjectExpectedStudyInstitution(long projectExpectedStudyInstitutionID) {

    return projectExpectedStudyInstitutionDAO.existProjectExpectedStudyInstitution(projectExpectedStudyInstitutionID);
  }

  @Override
  public List<ProjectExpectedStudyInstitution> findAll() {

    return projectExpectedStudyInstitutionDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyInstitution getProjectExpectedStudyInstitutionById(long projectExpectedStudyInstitutionID) {

    return projectExpectedStudyInstitutionDAO.find(projectExpectedStudyInstitutionID);
  }

  @Override
  public ProjectExpectedStudyInstitution saveProjectExpectedStudyInstitution(ProjectExpectedStudyInstitution projectExpectedStudyInstitution) {

    return projectExpectedStudyInstitutionDAO.save(projectExpectedStudyInstitution);
  }


}
