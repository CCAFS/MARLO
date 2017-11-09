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


import org.cgiar.ccafs.marlo.data.dao.CrpAssumptionDAO;
import org.cgiar.ccafs.marlo.data.dao.CrpMilestoneDAO;
import org.cgiar.ccafs.marlo.data.dao.CrpOutcomeSubIdoDAO;
import org.cgiar.ccafs.marlo.data.dao.CrpProgramOutcomeDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CrpAssumption;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpProgramOutcomeManagerImpl implements CrpProgramOutcomeManager {


  private CrpProgramOutcomeDAO crpProgramOutcomeDAO;
  private CrpOutcomeSubIdoDAO crpOutcomeSubIdoDAO;
  private CrpAssumptionDAO crpAssumptionDAO;
  private CrpMilestoneDAO crpMilestoneDAO;
  private PhaseDAO phaseMySQLDAO;

  // Managers


  @Inject
  public CrpProgramOutcomeManagerImpl(CrpProgramOutcomeDAO crpProgramOutcomeDAO,
    CrpOutcomeSubIdoDAO crpOutcomeSubIdoDAO, CrpAssumptionDAO crpAssumptionDAO, PhaseDAO phaseMySQLDAO,
    CrpMilestoneDAO crpMilestoneDAO) {
    this.crpProgramOutcomeDAO = crpProgramOutcomeDAO;
    this.crpOutcomeSubIdoDAO = crpOutcomeSubIdoDAO;
    this.crpAssumptionDAO = crpAssumptionDAO;
    this.crpMilestoneDAO = crpMilestoneDAO;
    this.phaseMySQLDAO = phaseMySQLDAO;


  }

  /**
   * clone the milestones
   * 
   * @param crpProgramOutcome outcome original
   * @param crpProgramOutcomeAdd outcome new
   */


  private void addCrpMilestones(CrpProgramOutcome crpProgramOutcome, CrpProgramOutcome crpProgramOutcomeAdd) {

    if (crpProgramOutcome.getMilestones() != null) {
      for (CrpMilestone crpMilestone : crpProgramOutcome.getMilestones()) {
        CrpMilestone crpMilestoneAdd = new CrpMilestone();
        crpMilestoneAdd.setActive(true);
        crpMilestoneAdd.setActiveSince(crpProgramOutcome.getActiveSince());
        crpMilestoneAdd.setCreatedBy(crpProgramOutcome.getCreatedBy());
        crpMilestoneAdd.setModificationJustification("");
        crpMilestoneAdd.setModifiedBy(crpProgramOutcome.getCreatedBy());
        crpMilestoneAdd.setCrpProgramOutcome(crpProgramOutcomeAdd);
        crpMilestoneAdd.setSrfTargetUnit(crpMilestone.getSrfTargetUnit());
        crpMilestoneAdd.setTitle(crpMilestone.getTitle());
        crpMilestoneAdd.setValue(crpMilestone.getValue());
        crpMilestoneAdd.setYear(crpMilestone.getYear());
        crpMilestoneAdd.setComposeID(crpMilestone.getComposeID());
        crpMilestoneDAO.save(crpMilestoneAdd);
        if (crpMilestone.getComposeID() == null) {
          crpMilestone.setComposeID(crpProgramOutcomeAdd.getComposeID() + "-" + crpMilestoneAdd.getId());
          crpMilestoneAdd.setComposeID(crpProgramOutcomeAdd.getComposeID() + "-" + crpMilestoneAdd.getId());
          crpMilestoneDAO.save(crpMilestoneAdd);
        }
      }
    }
  }


  /**
   * clone or update the outcome for next phases
   * 
   * @param next the next phase to clone
   * @param crpProgramID the program id we are working
   * @param otucome the outcome to clone
   */
  private void addCrpPorgramOutcomePhase(Phase next, long crpProgramID, CrpProgramOutcome outcome) {
    Phase phase = phaseMySQLDAO.find(next.getId());
    List<CrpProgramOutcome> outcomes =
      phase.getOutcomes().stream().filter(c -> c.isActive() && c.getCrpProgram().getId().longValue() == crpProgramID
        && c.getComposeID().equals(outcome.getComposeID())).collect(Collectors.toList());
    if (phase.getEditable() != null && phase.getEditable() && outcomes.isEmpty()) {
      CrpProgramOutcome outcomeAdd = new CrpProgramOutcome();
      outcomeAdd.setActive(true);
      outcomeAdd.setActiveSince(outcome.getActiveSince());
      outcomeAdd.setCreatedBy(outcome.getCreatedBy());
      outcomeAdd.setModificationJustification(outcome.getModificationJustification());
      outcomeAdd.setModifiedBy(outcome.getModifiedBy());
      outcomeAdd.setPhase(phase);
      outcomeAdd.setCrpProgram(outcome.getCrpProgram());
      outcomeAdd.setSrfTargetUnit(outcome.getSrfTargetUnit());
      outcomeAdd.setValue(outcome.getValue());
      outcomeAdd.setYear(outcome.getYear());
      outcomeAdd.setDescription(outcome.getDescription());
      outcomeAdd.setComposeID(outcome.getComposeID());
      crpProgramOutcomeDAO.save(outcomeAdd);
      this.addCrpSubIdos(outcome, outcomeAdd);
      this.addCrpMilestones(outcome, outcomeAdd);

    } else {
      if (phase.getEditable() != null && phase.getEditable()) {
        for (CrpProgramOutcome outcomePrev : outcomes) {
          outcomePrev.setSrfTargetUnit(outcome.getSrfTargetUnit());
          outcomePrev.setValue(outcome.getValue());
          outcomePrev.setYear(outcome.getYear());
          outcomePrev.setDescription(outcome.getDescription());
          crpProgramOutcomeDAO.save(outcomePrev);
          this.updateCrpSubIdos(outcomePrev, outcome);
          this.updateMilestones(outcomePrev, outcome);
        }
      }
    }

    if (phase.getNext() != null) {
      this.addCrpPorgramOutcomePhase(phase.getNext(), crpProgramID, outcome);
    }


  }

  /**
   * clone the outcomes Sub idos
   * 
   * @param crpProgramOutcome outcome original
   * @param crpProgramOutcomeAdd outcome new
   */

  private void addCrpSubIdos(CrpProgramOutcome crpProgramOutcome, CrpProgramOutcome crpProgramOutcomeAdd) {

    if (crpProgramOutcome.getSubIdos() != null) {
      for (CrpOutcomeSubIdo crpOutcomeSubIdo : crpProgramOutcome.getSubIdos()) {
        CrpOutcomeSubIdo crpOutcomeSubIdoAdd = new CrpOutcomeSubIdo();
        crpOutcomeSubIdoAdd.setActive(true);
        crpOutcomeSubIdoAdd.setActiveSince(crpProgramOutcome.getActiveSince());
        crpOutcomeSubIdoAdd.setCreatedBy(crpProgramOutcome.getCreatedBy());
        crpOutcomeSubIdoAdd.setModificationJustification("");
        crpOutcomeSubIdoAdd.setModifiedBy(crpProgramOutcome.getCreatedBy());
        crpOutcomeSubIdoAdd.setContribution(crpOutcomeSubIdo.getContribution());
        crpOutcomeSubIdoAdd.setSrfSubIdo(crpOutcomeSubIdo.getSrfSubIdo());
        crpOutcomeSubIdoAdd.setCrpProgramOutcome(crpProgramOutcomeAdd);
        crpOutcomeSubIdoDAO.save(crpOutcomeSubIdoAdd);
        for (CrpAssumption crpAssumption : crpOutcomeSubIdo.getCrpAssumptions().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {
          CrpAssumption crpAssumptionAdd = new CrpAssumption();
          crpAssumptionAdd.setActive(true);
          crpAssumptionAdd.setActiveSince(crpProgramOutcome.getActiveSince());
          crpAssumptionAdd.setCreatedBy(crpProgramOutcome.getCreatedBy());
          crpAssumptionAdd.setCrpOutcomeSubIdo(crpOutcomeSubIdoAdd);
          crpAssumptionAdd.setDescription(crpAssumption.getDescription());
          crpAssumptionAdd.setModificationJustification("");
          crpAssumptionAdd.setModifiedBy(crpProgramOutcome.getModifiedBy());
          crpAssumptionDAO.save(crpAssumptionAdd);

        }
      }
    }
  }


  @Override
  public void deleteCrpProgramOutcome(long crpProgramOutcomeId) {

    crpProgramOutcomeDAO.deleteCrpProgramOutcome(crpProgramOutcomeId);
  }

  @Override
  public boolean existCrpProgramOutcome(long crpProgramOutcomeID) {

    return crpProgramOutcomeDAO.existCrpProgramOutcome(crpProgramOutcomeID);
  }

  @Override
  public List<CrpProgramOutcome> findAll() {

    return crpProgramOutcomeDAO.findAll();

  }

  @Override
  public CrpProgramOutcome getCrpProgramOutcomeById(long crpProgramOutcomeID) {

    return crpProgramOutcomeDAO.find(crpProgramOutcomeID);
  }

  @Override
  public CrpProgramOutcome saveCrpProgramOutcome(CrpProgramOutcome crpProgramOutcome) {

    CrpProgramOutcome resultDao = crpProgramOutcomeDAO.save(crpProgramOutcome);
    if (crpProgramOutcome.getPhase().getNext() != null) {
      this.addCrpPorgramOutcomePhase(crpProgramOutcome.getPhase().getNext(), crpProgramOutcome.getCrpProgram().getId(),
        crpProgramOutcome);
    }
    return resultDao;
  }

  /**
   * check the sub-idos and updated
   * 
   * @param programOutcomePrev outcome to update
   * @param programOutcome outcome modified
   */
  private void updateCrpSubIdos(CrpProgramOutcome programOutcomePrev, CrpProgramOutcome programOutcome) {
    for (CrpOutcomeSubIdo outcomeSubIdo : programOutcomePrev.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {
      if (programOutcome.getSubIdos() == null || programOutcome.getSubIdos().stream()
        .filter(c -> c.getSrfSubIdo() != null && c.getSrfSubIdo().equals(outcomeSubIdo.getSrfSubIdo()))
        .collect(Collectors.toList()).isEmpty()) {
        outcomeSubIdo.setActive(false);
        crpOutcomeSubIdoDAO.save(outcomeSubIdo);
      }
    }
    if (programOutcome.getSubIdos() != null) {
      for (CrpOutcomeSubIdo outcomeSubIdo : programOutcome.getSubIdos()) {
        if (programOutcomePrev.getCrpOutcomeSubIdos().stream()
          .filter(c -> c.isActive() && c.getSrfSubIdo().equals(outcomeSubIdo.getSrfSubIdo()))
          .collect(Collectors.toList()).isEmpty()) {

          CrpOutcomeSubIdo outcomeSubIdoAdd = new CrpOutcomeSubIdo();

          outcomeSubIdoAdd.setCrpProgramOutcome(programOutcomePrev);
          outcomeSubIdoAdd.setModifiedBy(programOutcomePrev.getModifiedBy());
          outcomeSubIdoAdd.setActive(true);
          outcomeSubIdoAdd.setActiveSince(programOutcomePrev.getActiveSince());
          outcomeSubIdoAdd.setModificationJustification(programOutcomePrev.getModificationJustification());
          outcomeSubIdoAdd.setCreatedBy(programOutcomePrev.getCreatedBy());
          outcomeSubIdoAdd.setContribution(outcomeSubIdo.getContribution());
          outcomeSubIdoAdd.setSrfSubIdo(outcomeSubIdo.getSrfSubIdo());
          crpOutcomeSubIdoDAO.save(outcomeSubIdoAdd);

        }
      }
    }
  }

  /**
   * check the milesotnes and updated
   * 
   * @param programOutcomePrev outcome to update
   * @param programOutcome outcome modified
   */
  private void updateMilestones(CrpProgramOutcome programOutcomePrev, CrpProgramOutcome programOutcome) {
    for (CrpMilestone crpMilestone : programOutcomePrev.getCrpMilestones().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {
      if (programOutcome.getMilestones() == null || programOutcome.getMilestones().stream()
        .filter(c -> c.getComposeID().equals(crpMilestone.getComposeID())).collect(Collectors.toList()).isEmpty()) {
        crpMilestone.setActive(false);
        crpMilestoneDAO.save(crpMilestone);
      }
    }
    if (programOutcome.getMilestones() != null) {
      for (CrpMilestone crpMilestone : programOutcome.getMilestones()) {
        if (programOutcomePrev.getCrpMilestones().stream()
          .filter(c -> c.isActive() && c.getComposeID().equals(crpMilestone.getComposeID()))
          .collect(Collectors.toList()).isEmpty()) {

          CrpMilestone crpMilestoneAdd = new CrpMilestone();

          crpMilestoneAdd.setCrpProgramOutcome(programOutcomePrev);
          crpMilestoneAdd.setModifiedBy(programOutcomePrev.getModifiedBy());
          crpMilestoneAdd.setActive(true);
          crpMilestoneAdd.setActiveSince(programOutcomePrev.getActiveSince());
          crpMilestoneAdd.setModificationJustification(programOutcomePrev.getModificationJustification());
          crpMilestoneAdd.setCreatedBy(programOutcomePrev.getCreatedBy());
          crpMilestoneAdd.setComposeID(crpMilestone.getComposeID());
          crpMilestoneAdd.setSrfTargetUnit(crpMilestone.getSrfTargetUnit());
          crpMilestoneAdd.setTitle(crpMilestone.getTitle());
          crpMilestoneAdd.setValue(crpMilestone.getValue());
          crpMilestoneAdd.setYear(crpMilestone.getYear());
          crpMilestoneAdd.setComposeID(crpMilestone.getComposeID());
          crpMilestoneDAO.save(crpMilestoneAdd);
          if (crpMilestone.getComposeID() == null) {
            crpMilestone.setComposeID(programOutcomePrev.getComposeID() + "-" + crpMilestoneAdd.getId());
            crpMilestoneAdd.setComposeID(programOutcomePrev.getComposeID() + "-" + crpMilestoneAdd.getId());
            crpMilestoneDAO.save(crpMilestoneAdd);
          }

        } else {
          CrpMilestone milestonetoUpdate = programOutcomePrev.getCrpMilestones().stream()
            .filter(c -> c.isActive() && c.getComposeID().equals(crpMilestone.getComposeID()))
            .collect(Collectors.toList()).get(0);
          milestonetoUpdate.setTitle(crpMilestone.getTitle());
          milestonetoUpdate.setSrfTargetUnit(crpMilestone.getSrfTargetUnit());
          milestonetoUpdate.setYear(crpMilestone.getYear());
          milestonetoUpdate.setValue(crpMilestone.getValue());
          crpMilestoneDAO.save(milestonetoUpdate);
        }
      }
    }
  }
}
