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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationPartnershipDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationPartnershipManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPartnership;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationPartnershipManagerImpl implements ProjectInnovationPartnershipManager {


  private ProjectInnovationPartnershipDAO projectInnovationPartnershipDAO;
  // Managers


  @Inject
  public ProjectInnovationPartnershipManagerImpl(ProjectInnovationPartnershipDAO projectInnovationPartnershipDAO) {
    this.projectInnovationPartnershipDAO = projectInnovationPartnershipDAO;


  }

  @Override
  public void deleteProjectInnovationPartnership(long projectInnovationPartnershipId) {

    projectInnovationPartnershipDAO.deleteProjectInnovationPartnership(projectInnovationPartnershipId);
  }

  @Override
  public boolean existProjectInnovationPartnership(long projectInnovationPartnershipID) {

    return projectInnovationPartnershipDAO.existProjectInnovationPartnership(projectInnovationPartnershipID);
  }

  @Override
  public List<ProjectInnovationPartnership> findAll() {

    return projectInnovationPartnershipDAO.findAll();

  }

  @Override
  public ProjectInnovationPartnership getProjectInnovationPartnershipById(long projectInnovationPartnershipID) {

    return projectInnovationPartnershipDAO.find(projectInnovationPartnershipID);
  }

  @Override
  public ProjectInnovationPartnership saveProjectInnovationPartnership(ProjectInnovationPartnership projectInnovationPartnership) {

    return projectInnovationPartnershipDAO.save(projectInnovationPartnership);
  }


}
