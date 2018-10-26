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


import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerPartnershipLocationDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPartnershipLocationManager;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnershipLocation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectPartnerPartnershipLocationManagerImpl implements ProjectPartnerPartnershipLocationManager {


  private ProjectPartnerPartnershipLocationDAO projectPartnerPartnershipLocationDAO;
  // Managers


  @Inject
  public ProjectPartnerPartnershipLocationManagerImpl(
    ProjectPartnerPartnershipLocationDAO projectPartnerPartnershipLocationDAO) {
    this.projectPartnerPartnershipLocationDAO = projectPartnerPartnershipLocationDAO;


  }

  @Override
  public void deleteProjectPartnerPartnershipLocation(long projectPartnerPartnershipLocationId) {

    projectPartnerPartnershipLocationDAO.deleteProjectPartnerPartnershipLocation(projectPartnerPartnershipLocationId);
  }

  @Override
  public boolean existProjectPartnerPartnershipLocation(long projectPartnerPartnershipLocationID) {

    return projectPartnerPartnershipLocationDAO
      .existProjectPartnerPartnershipLocation(projectPartnerPartnershipLocationID);
  }

  @Override
  public List<ProjectPartnerPartnershipLocation> findAll() {

    return projectPartnerPartnershipLocationDAO.findAll();

  }

  @Override
  public List<ProjectPartnerPartnershipLocation>
    findParnershipLocationByPartnership(long projectPartnerPartnershipnId) {
    return projectPartnerPartnershipLocationDAO.findParnershipLocationByPartnership(projectPartnerPartnershipnId);
  }

  @Override
  public ProjectPartnerPartnershipLocation
    getProjectPartnerPartnershipLocationById(long projectPartnerPartnershipLocationID) {

    return projectPartnerPartnershipLocationDAO.find(projectPartnerPartnershipLocationID);
  }

  @Override
  public ProjectPartnerPartnershipLocation
    saveProjectPartnerPartnershipLocation(ProjectPartnerPartnershipLocation projectPartnerPartnershipLocation) {

    return projectPartnerPartnershipLocationDAO.save(projectPartnerPartnershipLocation);
  }


}
