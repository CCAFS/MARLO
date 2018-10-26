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


import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerPartnershipResearchPhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPartnershipResearchPhaseManager;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnershipResearchPhase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectPartnerPartnershipResearchPhaseManagerImpl
  implements ProjectPartnerPartnershipResearchPhaseManager {


  private ProjectPartnerPartnershipResearchPhaseDAO projectPartnerPartnershipResearchPhaseDAO;
  // Managers


  @Inject
  public ProjectPartnerPartnershipResearchPhaseManagerImpl(
    ProjectPartnerPartnershipResearchPhaseDAO projectPartnerPartnershipResearchPhaseDAO) {
    this.projectPartnerPartnershipResearchPhaseDAO = projectPartnerPartnershipResearchPhaseDAO;


  }

  @Override
  public void deleteProjectPartnerPartnershipResearchPhase(long projectPartnerPartnershipResearchPhaseId) {

    projectPartnerPartnershipResearchPhaseDAO
      .deleteProjectPartnerPartnershipResearchPhase(projectPartnerPartnershipResearchPhaseId);
  }

  @Override
  public boolean existProjectPartnerPartnershipResearchPhase(long projectPartnerPartnershipResearchPhaseID) {

    return projectPartnerPartnershipResearchPhaseDAO
      .existProjectPartnerPartnershipResearchPhase(projectPartnerPartnershipResearchPhaseID);
  }

  @Override
  public List<ProjectPartnerPartnershipResearchPhase> findAll() {

    return projectPartnerPartnershipResearchPhaseDAO.findAll();

  }

  @Override
  public List<ProjectPartnerPartnershipResearchPhase>
    findParnershipResearchPhaseByPartnership(long projectPartnerPartnershipnId) {
    return projectPartnerPartnershipResearchPhaseDAO
      .findParnershipResearchPhaseByPartnership(projectPartnerPartnershipnId);
  }

  @Override
  public ProjectPartnerPartnershipResearchPhase
    getProjectPartnerPartnershipResearchPhaseById(long projectPartnerPartnershipResearchPhaseID) {
    return projectPartnerPartnershipResearchPhaseDAO.find(projectPartnerPartnershipResearchPhaseID);
  }

  @Override
  public ProjectPartnerPartnershipResearchPhase saveProjectPartnerPartnershipResearchPhase(
    ProjectPartnerPartnershipResearchPhase projectPartnerPartnershipResearchPhase) {

    return projectPartnerPartnershipResearchPhaseDAO.save(projectPartnerPartnershipResearchPhase);
  }

}
