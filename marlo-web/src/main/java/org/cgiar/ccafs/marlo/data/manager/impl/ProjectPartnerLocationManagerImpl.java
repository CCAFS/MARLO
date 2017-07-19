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


import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerLocationDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerLocationManager;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerLocation;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectPartnerLocationManagerImpl implements ProjectPartnerLocationManager {


  private ProjectPartnerLocationDAO projectPartnerLocationDAO;
  // Managers


  @Inject
  public ProjectPartnerLocationManagerImpl(ProjectPartnerLocationDAO projectPartnerLocationDAO) {
    this.projectPartnerLocationDAO = projectPartnerLocationDAO;


  }

  @Override
  public boolean deleteProjectPartnerLocation(long projectPartnerLocationId) {

    return projectPartnerLocationDAO.deleteProjectPartnerLocation(projectPartnerLocationId);
  }

  @Override
  public boolean existProjectPartnerLocation(long projectPartnerLocationID) {

    return projectPartnerLocationDAO.existProjectPartnerLocation(projectPartnerLocationID);
  }

  @Override
  public List<ProjectPartnerLocation> findAll() {

    return projectPartnerLocationDAO.findAll();

  }

  @Override
  public ProjectPartnerLocation getProjectPartnerLocationById(long projectPartnerLocationID) {

    return projectPartnerLocationDAO.find(projectPartnerLocationID);
  }

  @Override
  public long saveProjectPartnerLocation(ProjectPartnerLocation projectPartnerLocation) {

    return projectPartnerLocationDAO.save(projectPartnerLocation);
  }


}
