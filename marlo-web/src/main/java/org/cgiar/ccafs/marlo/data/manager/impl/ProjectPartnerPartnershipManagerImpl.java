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


import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerPartnershipDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPartnershipManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnership;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectPartnerPartnershipManagerImpl implements ProjectPartnerPartnershipManager {


  private ProjectPartnerPartnershipDAO projectPartnerPartnershipDAO;
  // Managers


  @Inject
  public ProjectPartnerPartnershipManagerImpl(ProjectPartnerPartnershipDAO projectPartnerPartnershipDAO) {
    this.projectPartnerPartnershipDAO = projectPartnerPartnershipDAO;


  }

  @Override
  public void deleteProjectPartnerPartnership(long projectPartnerPartnershipId) {

    projectPartnerPartnershipDAO.deleteProjectPartnerPartnership(projectPartnerPartnershipId);
  }

  @Override
  public boolean existProjectPartnerPartnership(long projectPartnerPartnershipID) {

    return projectPartnerPartnershipDAO.existProjectPartnerPartnership(projectPartnerPartnershipID);
  }

  @Override
  public List<ProjectPartnerPartnership> findAll() {

    return projectPartnerPartnershipDAO.findAll();

  }

  @Override
  public ProjectPartnerPartnership getProjectPartnerPartnershipById(long projectPartnerPartnershipID) {

    return projectPartnerPartnershipDAO.find(projectPartnerPartnershipID);
  }

  @Override
  public List<ProjectPartnerPartnership> getProjectPartnerPartnershipByPhase(Phase phase) {
    return projectPartnerPartnershipDAO.getProjectPartnerPartnershipByPhase(phase);
  }

  @Override
  public ProjectPartnerPartnership saveProjectPartnerPartnership(ProjectPartnerPartnership projectPartnerPartnership) {

    return projectPartnerPartnershipDAO.save(projectPartnerPartnership);
  }

}
