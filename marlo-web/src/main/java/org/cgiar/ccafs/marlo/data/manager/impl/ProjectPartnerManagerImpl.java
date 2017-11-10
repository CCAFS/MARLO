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


import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectPartnerManagerImpl implements ProjectPartnerManager {


  private ProjectPartnerDAO projectPartnerDAO;
  // Managers


  @Inject
  public ProjectPartnerManagerImpl(ProjectPartnerDAO projectPartnerDAO) {
    this.projectPartnerDAO = projectPartnerDAO;


  }

  @Override
  public void deleteProjectPartner(long projectPartnerId) {

    projectPartnerDAO.deleteProjectPartner(projectPartnerId);
  }

  @Override
  public boolean existProjectPartner(long projectPartnerID) {

    return projectPartnerDAO.existProjectPartner(projectPartnerID);
  }

  @Override
  public List<ProjectPartner> findAll() {

    return projectPartnerDAO.findAll();

  }

  @Override
  public ProjectPartner getProjectPartnerById(long projectPartnerID) {

    return projectPartnerDAO.find(projectPartnerID);
  }

  @Override
  public ProjectPartner getProjectPartnerByIdAndEagerFetchLocations(long projectPartnerID) {
    return projectPartnerDAO.getProjectPartnerByIdAndEagerFetchLocations(projectPartnerID);
  }

  @Override
  public List<ProjectPartner> getProjectPartnersForProjectWithActiveProjectPartnerPersons(long projectId) {
    return projectPartnerDAO.getProjectPartnersForProjectWithActiveProjectPartnerPersons(projectId);
  }

  @Override
  public ProjectPartner saveProjectPartner(ProjectPartner projectPartner) {

    return projectPartnerDAO.save(projectPartner);
  }


}
