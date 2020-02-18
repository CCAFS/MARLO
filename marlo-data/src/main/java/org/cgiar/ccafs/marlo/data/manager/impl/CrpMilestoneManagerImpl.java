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


import org.cgiar.ccafs.marlo.data.dao.CrpMilestoneDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class CrpMilestoneManagerImpl implements CrpMilestoneManager {


  private CrpMilestoneDAO crpAssumptionDAO;
  private PhaseDAO phaseDAO;

  // Managers


  @Inject
  public CrpMilestoneManagerImpl(CrpMilestoneDAO crpAssumptionDAO, PhaseDAO phaseDAO) {
    this.crpAssumptionDAO = crpAssumptionDAO;
    this.phaseDAO = phaseDAO;

  }

  @Override
  public void deleteCrpMilestone(long crpAssumptionId) {

    crpAssumptionDAO.deleteCrpMilestone(crpAssumptionId);
  }

  @Override
  public boolean existCrpMilestone(long crpAssumptionID) {

    return crpAssumptionDAO.existCrpMilestone(crpAssumptionID);
  }

  @Override
  public List<CrpMilestone> findAll() {

    return crpAssumptionDAO.findAll();

  }

  @Override
  public CrpMilestone getCrpMilestoneById(long crpAssumptionID) {

    return crpAssumptionDAO.find(crpAssumptionID);
  }

  @Override
  public CrpMilestone saveCrpMilestone(CrpMilestone crpAssumption) {

    return crpAssumptionDAO.save(crpAssumption);
  }

  /*
   * public void saveInnovationCenterPhase(Phase next, long innovationid, CrpMilestone crpMilestone) {
   * Phase phase = phaseDAO.find(next.getId());
   * List<ProjectInnovationCenter> projectInnovatioCenters =
   * crpAssumptionDAO.findAll().stream()
   * .filter(c -> c.getcr().getId().longValue() == innovationid
   * && c.getPhase().getId().equals(phase.getId())
   * && c.getInstitution().getId().equals(projectInnovationCenter.getInstitution().getId()))
   * .collect(Collectors.toList());
   * if (projectInnovatioCenters.isEmpty()) {
   * ProjectInnovationCenter projectInnovationCenterAdd = new ProjectInnovationCenter();
   * projectInnovationCenterAdd.setProjectInnovation(projectInnovationCenter.getProjectInnovation());
   * projectInnovationCenterAdd.setPhase(phase);
   * projectInnovationCenterAdd.setInstitution(projectInnovationCenter.getInstitution());
   * crpAssumptionDAO.save(projectInnovationCenterAdd);
   * }
   * if (phase.getNext() != null) {
   * this.saveInnovationCenterPhase(phase.getNext(), innovationid, projectInnovationCenter);
   * }
   * }
   */

}
