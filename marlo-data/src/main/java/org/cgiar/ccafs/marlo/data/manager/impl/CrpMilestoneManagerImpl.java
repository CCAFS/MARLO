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
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Garcia
 */
@Named
public class CrpMilestoneManagerImpl implements CrpMilestoneManager {

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(CrpMilestoneManagerImpl.class);

  // DAO
  private CrpMilestoneDAO crpMilestoneDAO;

  // Managers
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  // private PowbIndFollowingMilestoneManager powbIndFollowingMilestoneManager;
  // private PowbIndAssesmentRiskManager powbIndAssesmentRiskManager;
  // private PowbIndMilestoneRiskManager powbIndMilestoneRiskManager;
  // private RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager;
  // private PhaseManager phaseManager;


  @Inject
  public CrpMilestoneManagerImpl(CrpMilestoneDAO crpMilestoneDAO, CrpProgramOutcomeManager crpProgramOutcomeManager
  /*
   * PowbIndFollowingMilestoneManager powbIndFollowingMilestoneManager,
   * , PowbIndAssesmentRiskManager powbIndAssesmentRiskManager, PowbIndMilestoneRiskManager powbIndMilestoneRiskManager,
   * RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager
   */) {
    this.crpMilestoneDAO = crpMilestoneDAO;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    // this.powbIndAssesmentRiskManager = powbIndAssesmentRiskManager;
    // this.powbIndMilestoneRiskManager = powbIndMilestoneRiskManager;
    // this.powbIndFollowingMilestoneManager = powbIndFollowingMilestoneManager;
    // this.repIndGenderYouthFocusLevelManager = repIndGenderYouthFocusLevelManager;
    // this.phaseManager = phaseManager;
  }

  /**
   * clone the milestones
   * 
   * @param crpProgramOutcome outcome original
   * @param crpProgramOutcomeAdd outcome new
   */
  // @Override
  // public void addCrpMilestones(CrpProgramOutcome crpProgramOutcome, CrpProgramOutcome crpProgramOutcomeAdd) {
  // if (crpProgramOutcome.getMilestones() != null) {
  // for (CrpMilestone crpMilestone : crpProgramOutcome.getMilestones()) {
  // CrpMilestone crpMilestoneAdd = new CrpMilestone();
  // crpMilestoneAdd.setCrpProgramOutcome(crpProgramOutcomeAdd);
  // crpMilestoneAdd.setSrfTargetUnit(crpMilestone.getSrfTargetUnit());
  // crpMilestoneAdd.setTitle(crpMilestone.getTitle());
  // crpMilestoneAdd.setValue(crpMilestone.getValue());
  // crpMilestoneAdd.setYear(crpMilestone.getYear());
  //
  // if (crpMilestone.getExtendedYear() != null) {
  // if (crpMilestone.getExtendedYear() != -1) {
  // crpMilestoneAdd.setExtendedYear(crpMilestone.getExtendedYear());
  // }
  // }
  //
  // crpMilestoneAdd.setMilestonesStatus(crpMilestone.getMilestonesStatus());
  //
  // crpMilestoneAdd.setComposeID(crpMilestone.getComposeID());
  // crpMilestoneAdd = crpMilestoneDAO.save(crpMilestoneAdd);
  // if (crpMilestone.getComposeID() == null || crpMilestone.getComposeID().trim().length() == 0) {
  // crpMilestone.setComposeID(crpProgramOutcome.getComposeID() + "-" + crpMilestoneAdd.getId());
  // crpMilestoneAdd.setComposeID(crpProgramOutcome.getComposeID() + "-" + crpMilestoneAdd.getId());
  // crpMilestoneDAO.save(crpMilestoneAdd);
  // }
  // }
  // }
  // }

  @Override
  public void deleteCrpMilestone(long crpAssumptionId) {
    crpMilestoneDAO.deleteCrpMilestone(crpAssumptionId);
  }

  @Override
  public boolean existCrpMilestone(long crpAssumptionID) {
    return crpMilestoneDAO.existCrpMilestone(crpAssumptionID);
  }

  @Override
  public List<CrpMilestone> findAll() {
    return crpMilestoneDAO.findAll();
  }

  @Override
  public CrpMilestone getCrpMilestoneById(long crpAssumptionID) {
    return crpMilestoneDAO.find(crpAssumptionID);
  }

  @Override
  public CrpMilestone getCrpMilestoneByPhase(String composedID, long phaseID) {
    return crpMilestoneDAO.getCrpMilestoneByPhase(composedID, phaseID);
  }

  @Override
  public void replicate(CrpMilestone originalCrpMilestone, Phase initialPhase) {
    String milestoneComposedId = originalCrpMilestone.getComposeID();
    String outcomeComposedId = StringUtils.substringBeforeLast(milestoneComposedId, "-");

    List<CrpProgramOutcome> outcomes =
      crpProgramOutcomeManager.getAllCrpProgramOutcomesByComposedIdFromPhase(outcomeComposedId, initialPhase.getId());

    for (CrpProgramOutcome crpProgramOutcome : outcomes) {
      CrpMilestone milestone = this.getCrpMilestoneByPhase(milestoneComposedId, crpProgramOutcome.getPhase().getId());
      if (milestone == null) {
        milestone = new CrpMilestone();
      }

      milestone.copyFields(originalCrpMilestone);
      milestone.setCrpProgramOutcome(crpProgramOutcome);
      this.saveCrpMilestone(milestone);
    }
  }

  @Override
  public CrpMilestone saveCrpMilestone(CrpMilestone crpAssumption) {
    return crpMilestoneDAO.save(crpAssumption);
  }

  /**
   * Detect and make changes of milestones, comparing the previous (DB) outcome with an incoming outcome (front-end)
   * 
   * @param programOutcomeOld outcome to be updated
   * @param programOutcomeIncoming incoming outcome (modified)
   */
  // @Override
  // public void updateMilestones(CrpProgramOutcome programOutcomeOld, CrpProgramOutcome programOutcomeIncoming) {
  // List<CrpMilestone> oldMilestones = programOutcomeOld.getCrpMilestones() != null
  // ? programOutcomeOld.getCrpMilestones().stream().filter(c -> c.isActive()).collect(Collectors.toList())
  // : Collections.emptyList();
  // List<CrpMilestone> incomingMilestones = null;
  //
  // for (CrpMilestone crpMilestone : oldMilestones) {
  // incomingMilestones = programOutcomeIncoming.getMilestones() != null
  // ? programOutcomeIncoming.getMilestones().stream()
  // .filter(c -> c.getComposeID().equals(crpMilestone.getComposeID())).collect(Collectors.toList())
  // : Collections.emptyList();
  //
  // if (incomingMilestones.isEmpty()) {
  // CrpMilestone milestoneDb = this.getCrpMilestoneById(crpMilestone.getId());
  //
  // this.deleteCrpMilestone(milestoneDb.getId());
  // this.replicate(milestoneDb, programOutcomeOld.getPhase());
  // }
  // }
  //
  // incomingMilestones = programOutcomeIncoming.getMilestones();
  // if (incomingMilestones != null) {
  // for (CrpMilestone crpMilestone : incomingMilestones) {
  // CrpMilestone oldMilestone =
  // this.getCrpMilestoneByPhase(crpMilestone.getComposeID(), programOutcomeOld.getPhase().getId());
  //
  // if (oldMilestone == null) {
  // CrpMilestone newCrpMilestone = new CrpMilestone();
  // newCrpMilestone.copyFields(crpMilestone);
  //
  // newCrpMilestone.setComposeID(crpMilestone.getComposeID());
  // newCrpMilestone = this.saveCrpMilestone(newCrpMilestone);
  //
  // newCrpMilestone.setComposeID(programOutcomeIncoming.getComposeID() + "-" + newCrpMilestone.getId());
  // newCrpMilestone = this.saveCrpMilestone(newCrpMilestone);
  // this.replicate(newCrpMilestone, programOutcomeIncoming.getPhase());
  // } else {
  // if (StringUtils.stripToNull(oldMilestone.getComposeID()) == null) {
  // LOG.debug(programOutcomeOld.toString() + programOutcomeOld.getPhase());
  // /// crpMilestone.setComposeID(programOutcomeOld.getComposeID() + "-" + oldMilestone.getId());
  // oldMilestone.setComposeID(programOutcomeOld.getComposeID() + "-" + oldMilestone.getId());
  // }
  //
  // oldMilestone.copyFields(crpMilestone);
  // oldMilestone.setId(oldMilestone.getId());
  // oldMilestone = this.saveCrpMilestone(oldMilestone);
  // this.replicate(oldMilestone, programOutcomeOld.getPhase());
  // }
  // }
  // }
  // }

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
