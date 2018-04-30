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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudySubIdoDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySubIdoManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudySubIdoManagerImpl implements ProjectExpectedStudySubIdoManager {


  private ProjectExpectedStudySubIdoDAO projectExpectedStudySubIdoDAO;
  // Managers


  @Inject
  public ProjectExpectedStudySubIdoManagerImpl(ProjectExpectedStudySubIdoDAO projectExpectedStudySubIdoDAO) {
    this.projectExpectedStudySubIdoDAO = projectExpectedStudySubIdoDAO;


  }

  @Override
  public void deleteProjectExpectedStudySubIdo(long projectExpectedStudySubIdoId) {

    projectExpectedStudySubIdoDAO.deleteProjectExpectedStudySubIdo(projectExpectedStudySubIdoId);
  }

  @Override
  public boolean existProjectExpectedStudySubIdo(long projectExpectedStudySubIdoID) {

    return projectExpectedStudySubIdoDAO.existProjectExpectedStudySubIdo(projectExpectedStudySubIdoID);
  }

  @Override
  public List<ProjectExpectedStudySubIdo> findAll() {

    return projectExpectedStudySubIdoDAO.findAll();

  }

  @Override
  public ProjectExpectedStudySubIdo getProjectExpectedStudySubIdoById(long projectExpectedStudySubIdoID) {

    return projectExpectedStudySubIdoDAO.find(projectExpectedStudySubIdoID);
  }

  @Override
  public ProjectExpectedStudySubIdo saveProjectExpectedStudySubIdo(ProjectExpectedStudySubIdo projectExpectedStudySubIdo) {

    return projectExpectedStudySubIdoDAO.save(projectExpectedStudySubIdo);
  }


}
