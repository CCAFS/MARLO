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


import org.cgiar.ccafs.marlo.data.dao.ICenterProjectTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectTypeManager;
import org.cgiar.ccafs.marlo.data.model.CenterProjectType;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterProjectTypeManager implements ICenterProjectTypeManager {


  private ICenterProjectTypeDAO projectTypeDAO;

  // Managers


  @Inject
  public CenterProjectTypeManager(ICenterProjectTypeDAO projectTypeDAO) {
    this.projectTypeDAO = projectTypeDAO;


  }

  @Override
  public boolean deleteProjectType(long projectTypeId) {

    return projectTypeDAO.deleteProjectType(projectTypeId);
  }

  @Override
  public boolean existProjectType(long projectTypeID) {

    return projectTypeDAO.existProjectType(projectTypeID);
  }

  @Override
  public List<CenterProjectType> findAll() {

    return projectTypeDAO.findAll();

  }

  @Override
  public CenterProjectType getProjectTypeById(long projectTypeID) {

    return projectTypeDAO.find(projectTypeID);
  }

  @Override
  public List<CenterProjectType> getProjectTypesByUserId(Long userId) {
    return projectTypeDAO.getProjectTypesByUserId(userId);
  }

  @Override
  public long saveProjectType(CenterProjectType projectType) {

    return projectTypeDAO.save(projectType);
  }

  @Override
  public long saveProjectType(CenterProjectType projectType, String actionName, List<String> relationsName) {
    return projectTypeDAO.save(projectType, actionName, relationsName);
  }


}
