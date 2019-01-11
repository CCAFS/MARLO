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


import org.cgiar.ccafs.marlo.data.dao.ProjectPolicySubIdoDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicySubIdoManager;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicySubIdo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectPolicySubIdoManagerImpl implements ProjectPolicySubIdoManager {


  private ProjectPolicySubIdoDAO projectPolicySubIdoDAO;
  // Managers


  @Inject
  public ProjectPolicySubIdoManagerImpl(ProjectPolicySubIdoDAO projectPolicySubIdoDAO) {
    this.projectPolicySubIdoDAO = projectPolicySubIdoDAO;


  }

  @Override
  public void deleteProjectPolicySubIdo(long projectPolicySubIdoId) {

    projectPolicySubIdoDAO.deleteProjectPolicySubIdo(projectPolicySubIdoId);
  }

  @Override
  public boolean existProjectPolicySubIdo(long projectPolicySubIdoID) {

    return projectPolicySubIdoDAO.existProjectPolicySubIdo(projectPolicySubIdoID);
  }

  @Override
  public List<ProjectPolicySubIdo> findAll() {

    return projectPolicySubIdoDAO.findAll();

  }

  @Override
  public ProjectPolicySubIdo getProjectPolicySubIdoById(long projectPolicySubIdoID) {

    return projectPolicySubIdoDAO.find(projectPolicySubIdoID);
  }

  @Override
  public ProjectPolicySubIdo saveProjectPolicySubIdo(ProjectPolicySubIdo projectPolicySubIdo) {

    return projectPolicySubIdoDAO.save(projectPolicySubIdo);
  }


}
