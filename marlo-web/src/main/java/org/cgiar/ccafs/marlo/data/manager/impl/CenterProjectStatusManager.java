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


import org.cgiar.ccafs.marlo.data.dao.ICenterProjectStatusDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectStatusManager;
import org.cgiar.ccafs.marlo.data.model.CenterProjectStatus;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class CenterProjectStatusManager implements ICenterProjectStatusManager {


  private ICenterProjectStatusDAO projectStatusDAO;

  // Managers


  @Inject
  public CenterProjectStatusManager(ICenterProjectStatusDAO projectStatusDAO) {
    this.projectStatusDAO = projectStatusDAO;


  }

  @Override
  public void deleteProjectStatus(long projectStatusId) {

    projectStatusDAO.deleteProjectStatus(projectStatusId);
  }

  @Override
  public boolean existProjectStatus(long projectStatusID) {

    return projectStatusDAO.existProjectStatus(projectStatusID);
  }

  @Override
  public List<CenterProjectStatus> findAll() {

    return projectStatusDAO.findAll();

  }

  @Override
  public CenterProjectStatus getProjectStatusById(long projectStatusID) {

    return projectStatusDAO.find(projectStatusID);
  }

  @Override
  public List<CenterProjectStatus> getProjectStatussByUserId(Long userId) {
    return projectStatusDAO.getProjectStatussByUserId(userId);
  }

  @Override
  public CenterProjectStatus saveProjectStatus(CenterProjectStatus projectStatus) {

    return projectStatusDAO.save(projectStatus);
  }


}
