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


import org.cgiar.ccafs.marlo.data.dao.ICenterProjectLocationDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectLocationManager;
import org.cgiar.ccafs.marlo.data.model.CenterProjectLocation;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class CenterProjectLocationManager implements ICenterProjectLocationManager {


  private ICenterProjectLocationDAO projectLocationDAO;

  // Managers


  @Inject
  public CenterProjectLocationManager(ICenterProjectLocationDAO projectLocationDAO) {
    this.projectLocationDAO = projectLocationDAO;


  }

  @Override
  public void deleteProjectLocation(long projectLocationId) {

    projectLocationDAO.deleteProjectLocation(projectLocationId);
  }

  @Override
  public boolean existProjectLocation(long projectLocationID) {

    return projectLocationDAO.existProjectLocation(projectLocationID);
  }

  @Override
  public List<CenterProjectLocation> findAll() {

    return projectLocationDAO.findAll();

  }

  @Override
  public CenterProjectLocation getProjectLocationById(long projectLocationID) {

    return projectLocationDAO.find(projectLocationID);
  }

  @Override
  public List<CenterProjectLocation> getProjectLocationsByUserId(Long userId) {
    return projectLocationDAO.getProjectLocationsByUserId(userId);
  }

  @Override
  public CenterProjectLocation saveProjectLocation(CenterProjectLocation projectLocation) {

    return projectLocationDAO.save(projectLocation);
  }

  @Override
  public CenterProjectLocation saveProjectLocation(CenterProjectLocation projectLocation, String actionName,
    List<String> relationsName) {
    return projectLocationDAO.save(projectLocation, actionName, relationsName);
  }


}
