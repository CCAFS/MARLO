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


import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationCrpDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCrpManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrp;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationCrpManagerImpl implements ProjectInnovationCrpManager {


  private ProjectInnovationCrpDAO projectInnovationCrpDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectInnovationCrpManagerImpl(ProjectInnovationCrpDAO projectInnovationCrpDAO, PhaseDAO phaseDAO) {
    this.projectInnovationCrpDAO = projectInnovationCrpDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectInnovationCrp(long projectInnovationCrpId) {

    ProjectInnovationCrp projectInnovationCrp = this.getProjectInnovationCrpById(projectInnovationCrpId);

    if (projectInnovationCrp.getPhase().getNext() != null) {
      this.deleteProjectInnovationCrpPhase(projectInnovationCrp.getPhase().getNext(),
        projectInnovationCrp.getProjectInnovation().getId(), projectInnovationCrp);
    }

    projectInnovationCrpDAO.deleteProjectInnovationCrp(projectInnovationCrpId);
  }


  public void deleteProjectInnovationCrpPhase(Phase next, long innovationID,
    ProjectInnovationCrp projectInnovationCrp) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationCrp> projectInnovationCrps =
      phase.getProjectInnovationCrps().stream()
        .filter(c -> c.isActive() && c.getProjectInnovation().getId().longValue() == innovationID
          && c.getGlobalUnit().getId().equals(projectInnovationCrp.getGlobalUnit().getId()))
        .collect(Collectors.toList());

    for (ProjectInnovationCrp projectInnovationCrpDB : projectInnovationCrps) {
      projectInnovationCrpDAO.deleteProjectInnovationCrp(projectInnovationCrpDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationCrpPhase(phase.getNext(), innovationID, projectInnovationCrp);
    }
  }

  @Override
  public boolean existProjectInnovationCrp(long projectInnovationCrpID) {

    return projectInnovationCrpDAO.existProjectInnovationCrp(projectInnovationCrpID);
  }

  @Override
  public List<ProjectInnovationCrp> findAll() {

    return projectInnovationCrpDAO.findAll();

  }

  @Override
  public ProjectInnovationCrp getProjectInnovationCrpById(long projectInnovationCrpID) {

    return projectInnovationCrpDAO.find(projectInnovationCrpID);
  }

  public void saveInnovationCrpPhase(Phase next, long innovationid, ProjectInnovationCrp projectInnovationCrp) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationCrp> projectInnovatioCrps =
      phase.getProjectInnovationCrps().stream()
        .filter(c -> c.getProjectInnovation().getId().longValue() == innovationid
          && c.getGlobalUnit().getId().equals(projectInnovationCrp.getGlobalUnit().getId()))
        .collect(Collectors.toList());

    if (projectInnovatioCrps.isEmpty()) {
      ProjectInnovationCrp projectInnovationCrpAdd = new ProjectInnovationCrp();
      projectInnovationCrpAdd.setProjectInnovation(projectInnovationCrp.getProjectInnovation());
      projectInnovationCrpAdd.setPhase(phase);
      projectInnovationCrpAdd.setGlobalUnit(projectInnovationCrp.getGlobalUnit());
      projectInnovationCrpDAO.save(projectInnovationCrpAdd);
    }


    if (phase.getNext() != null) {
      this.saveInnovationCrpPhase(phase.getNext(), innovationid, projectInnovationCrp);
    }
  }

  @Override
  public ProjectInnovationCrp saveProjectInnovationCrp(ProjectInnovationCrp projectInnovationCrp) {

    ProjectInnovationCrp crp = projectInnovationCrpDAO.save(projectInnovationCrp);

    Phase phase = phaseDAO.find(crp.getPhase().getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (crp.getPhase().getNext() != null) {
        this.saveInnovationCrpPhase(crp.getPhase().getNext(), crp.getProjectInnovation().getId(), projectInnovationCrp);
      }
    }
    return crp;
  }


}
