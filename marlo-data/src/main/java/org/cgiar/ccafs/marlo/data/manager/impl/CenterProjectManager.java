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


import org.cgiar.ccafs.marlo.data.dao.ICenterProjectDAO;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.service.ICenterProjectManager;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterProjectManager implements ICenterProjectManager {


  private ICenterProjectDAO projectDAO;

  // Managers


  @Inject
  public CenterProjectManager(ICenterProjectDAO projectDAO) {
    this.projectDAO = projectDAO;


  }

  @Override
  public boolean deleteCenterProject(long projectId) {

    return projectDAO.deleteCenterProject(projectId);
  }

  @Override
  public boolean existCenterProject(long projectID) {

    return projectDAO.existCenterProject(projectID);
  }

  @Override
  public List<CenterProject> findAll() {

    return projectDAO.findAll();

  }

  @Override
  public CenterProject getCenterProjectById(long projectID) {

    return projectDAO.find(projectID);
  }

  @Override
  public List<CenterProject> getCenterProjectsByUserId(Long userId) {
    return projectDAO.getCenterProjectsByUserId(userId);
  }

  @Override
  public long saveCenterProject(CenterProject project) {

    return projectDAO.save(project);
  }

  @Override
  public long saveCenterProject(CenterProject project, String actionName, List<String> relationsName) {
    return projectDAO.save(project, actionName, relationsName);
  }


}
