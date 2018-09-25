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
import org.cgiar.ccafs.marlo.data.dao.CrpProgramOutcomeIndicatorDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.PowbIndAssesmentRiskDAO;
import org.cgiar.ccafs.marlo.data.dao.PowbIndFollowingMilestoneDAO;
import org.cgiar.ccafs.marlo.data.dao.PowbIndMilestoneRiskDAO;
import org.cgiar.ccafs.marlo.data.dao.RepIndGenderYouthFocusLevelDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CrpAssumption;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcomeIndicator;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbIndAssesmentRisk;
import org.cgiar.ccafs.marlo.data.model.PowbIndFollowingMilestone;
import org.cgiar.ccafs.marlo.data.model.PowbIndMilestoneRisk;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class CrpProgramOutcomeManagerImpl implements CrpProgramOutcomeManager {


  private CrpProgramOutcomeDAO crpProgramOutcomeDAO;
  private CrpOutcomeSubIdoDAO crpOutcomeSubIdoDAO;
  private CrpAssumptionDAO crpAssumptionDAO;
  private CrpMilestoneDAO crpMilestoneDAO;
  private CrpProgramOutcomeIndicatorDAO crpProgramOutcomeIndicatorDAO;
  private PowbIndFollowingMilestoneDAO powbIndFollowingMilestoneDAO;
  private PowbIndAssesmentRiskDAO powbIndAssesmentRiskDAO;
  private PowbIndMilestoneRiskDAO powbIndMilestoneRiskDAO;
  private RepIndGenderYouthFocusLevelDAO repIndGenderYouthFocusLevelDAO;

  private PhaseDAO phaseMySQLDAO;

  // Managers


  @Inject
  public CrpProgramOutcomeManagerImpl(CrpProgramOutcomeDAO crpProgramOutcomeDAO,
    CrpOutcomeSubIdoDAO crpOutcomeSubIdoDAO, CrpAssumptionDAO crpAssumptionDAO, PhaseDAO phaseMySQLDAO,
    CrpMilestoneDAO crpMilestoneDAO, CrpProgramOutcomeIndicatorDAO crpProgramOutcomeIndicatorDAO,
    PowbIndFollowingMilestoneDAO powbIndFollowingMilestoneDAO, PowbIndAssesmentRiskDAO powbIndAssesmentRiskDAO,
    PowbIndMilestoneRiskDAO powbIndMilestoneRiskDAO, RepIndGenderYouthFocusLevelDAO repIndGenderYouthFocusLevelDAO) {
    this.crpProgramOutcomeDAO = crpProgramOutcomeDAO;
    this.crpOutcomeSubIdoDAO = crpOutcomeSubIdoDAO;
    this.crpAssumptionDAO = crpAssumptionDAO;
    this.crpProgramOutcomeIndicatorDAO = crpProgramOutcomeIndicatorDAO;
    this.crpMilestoneDAO = crpMilestoneDAO;
    this.phaseMySQLDAO = phaseMySQLDAO;
    this.powbIndFollowingMilestoneDAO = powbIndFollowingMilestoneDAO;
    this.powbIndAssesmentRiskDAO = powbIndAssesmentRiskDAO;
    this.powbIndMilestoneRiskDAO = powbIndMilestoneRiskDAO;
    this.repIndGenderYouthFocusLevelDAO = repIndGenderYouthFocusLevelDAO;

  }

  /**
   * clone the milestones
   * 
   * @param crpProgramOutcome outcome original
   * @param crpProgramOutcomeAdd outcome new
   */


  private void addCrpIndicators(CrpProgramOutcome crpProgramOutcome, CrpProgramOutcome crpProgramOutcomeAdd) {

    if (crpProgramOutcome.getIndicators() != null) {
      for (CrpProgramOutcomeIndicator crpProgramOutcomeIndicator : crpProgramOutcome.getIndicators()) {
        CrpProgramOutcomeIndicator crpIndicatorAdd = new CrpProgramOutcomeIndicator();
        crpIndicatorAdd.setCrpProgramOutcome(crpProgramOutcomeAdd);
        crpIndicatorAdd.setIndicator(crpProgramOutcomeIndicator.getIndicator());
        crpIndicatorAdd.setComposeID(crpProgramOutcomeIndicator.getComposeID());
        crpIndicatorAdd = crpProgramOutcomeIndicatorDAO.save(crpIndicatorAdd);
        if (crpProgramOutcomeIndicator.getComposeID() == null) {
          crpProgramOutcomeIndicator.setComposeID(crpProgramOutcomeAdd.getComposeID() + "-" + crpIndicatorAdd.getId());
          crpIndicatorAdd.setComposeID(crpProgramOutcomeAdd.getComposeID() + "-" + crpIndicatorAdd.getId());
          crpProgramOutcomeIndicatorDAO.save(crpIndicatorAdd);
        }
      }
    }
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
    if (outcomes.isEmpty()) {
      CrpProgramOutcome outcomeAdd = new CrpProgramOutcome();
      outcomeAdd.setPhase(phase);
      outcomeAdd.setCrpProgram(outcome.getCrpProgram());
      outcomeAdd.setSrfTargetUnit(outcome.getSrfTargetUnit());
      outcomeAdd.setValue(outcome.getValue());
      outcomeAdd.setYear(outcome.getYear());
      outcomeAdd.setDescription(outcome.getDescription());
      outcomeAdd.setComposeID(outcome.getComposeID());
      outcomeAdd.setIndicator(outcome.getIndicator());
      if (outcome.getFile() != null) {
        if (outcome.getFile().getId() == null || outcome.getFile().getId().longValue() == -1) {
          outcome.setFile(null);
        }
      }
      outcomeAdd.setFile(outcome.getFile());
      crpProgramOutcomeDAO.save(outcomeAdd);
      this.addCrpSubIdos(outcome, outcomeAdd);
      this.addCrpMilestones(outcome, outcomeAdd);
      this.addCrpIndicators(outcome, outcomeAdd);

    } else {

      for (CrpProgramOutcome outcomePrev : outcomes) {
        outcomePrev.setSrfTargetUnit(outcome.getSrfTargetUnit());
        outcomePrev.setValue(outcome.getValue());
        outcomePrev.setYear(outcome.getYear());
        outcomePrev.setDescription(outcome.getDescription());
        outcomePrev.setIndicator(outcome.getIndicator());
        if (outcome.getFile() != null) {
          if (outcome.getFile().getId() == null || outcome.getFile().getId().longValue() == -1) {
            outcome.setFile(null);
          }
        }
        outcomePrev.setFile(outcome.getFile());
        outcomePrev = crpProgramOutcomeDAO.save(outcomePrev);
        this.updateCrpSubIdos(outcomePrev, outcome);
        this.updateMilestones(outcomePrev, outcome);
        this.updateIndicators(outcomePrev, outcome);
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
        crpOutcomeSubIdoAdd.setContribution(crpOutcomeSubIdo.getContribution());
        crpOutcomeSubIdoAdd.setSrfSubIdo(crpOutcomeSubIdo.getSrfSubIdo());
        crpOutcomeSubIdoAdd.setCrpProgramOutcome(crpProgramOutcomeAdd);
        crpOutcomeSubIdoDAO.save(crpOutcomeSubIdoAdd);
        for (CrpAssumption crpAssumption : crpOutcomeSubIdo.getCrpAssumptions().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {
          CrpAssumption crpAssumptionAdd = new CrpAssumption();
          crpAssumptionAdd.setCrpOutcomeSubIdo(crpOutcomeSubIdoAdd);
          crpAssumptionAdd.setDescription(crpAssumption.getDescription());
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
        crpOutcomeSubIdoDAO.deleteCrpOutcomeSubIdo(outcomeSubIdo.getId());
      }
    }
    if (programOutcome.getSubIdos() != null) {
      for (CrpOutcomeSubIdo outcomeSubIdo : programOutcome.getSubIdos()) {
        if (programOutcomePrev.getCrpOutcomeSubIdos().stream()
          .filter(c -> c.isActive() && c.getSrfSubIdo().equals(outcomeSubIdo.getSrfSubIdo()))
          .collect(Collectors.toList()).isEmpty()) {

          CrpOutcomeSubIdo outcomeSubIdoAdd = new CrpOutcomeSubIdo();

          outcomeSubIdoAdd.setCrpProgramOutcome(programOutcomePrev);
          outcomeSubIdoAdd.setContribution(outcomeSubIdo.getContribution());
          outcomeSubIdoAdd.setSrfSubIdo(outcomeSubIdo.getSrfSubIdo());
          crpOutcomeSubIdoDAO.save(outcomeSubIdoAdd);

        }
      }
    }
  }

  /**
   * check the indicators and updated
   * 
   * @param programOutcomePrev outcome to update
   * @param programOutcome outcome modified
   */
  private void updateIndicators(CrpProgramOutcome programOutcomePrev, CrpProgramOutcome programOutcome) {
    for (CrpProgramOutcomeIndicator crpProgramOutcomeIndicator : programOutcomePrev.getCrpProgramOutcomeIndicators()
      .stream().filter(c -> c.isActive()).collect(Collectors.toList())) {
      if (programOutcome.getIndicators() == null || programOutcome.getIndicators().stream()
        .filter(c -> c.getComposeID() != null && c.getComposeID().equals(crpProgramOutcomeIndicator.getComposeID()))
        .collect(Collectors.toList()).isEmpty()) {
        crpProgramOutcomeIndicatorDAO.deleteCrpProgramOutcomeIndicator(crpProgramOutcomeIndicator.getId());
      }
    }
    if (programOutcome.getIndicators() != null) {
      for (CrpProgramOutcomeIndicator crpProgramOutcomeIndicator : programOutcome.getIndicators()) {

        if (programOutcomePrev.getCrpProgramOutcomeIndicators().stream()
          .filter(c -> c.isActive() && c.getComposeID().equals(crpProgramOutcomeIndicator.getComposeID()))
          .collect(Collectors.toList()).isEmpty()) {


          CrpProgramOutcomeIndicator crpIndicatorAdd = new CrpProgramOutcomeIndicator();
          crpIndicatorAdd.setCrpProgramOutcome(programOutcomePrev);
          crpIndicatorAdd.setIndicator(crpProgramOutcomeIndicator.getIndicator());
          crpIndicatorAdd.setComposeID(crpProgramOutcomeIndicator.getComposeID());
          crpIndicatorAdd = crpProgramOutcomeIndicatorDAO.save(crpIndicatorAdd);
          if (crpProgramOutcomeIndicator.getComposeID() == null
            || crpProgramOutcomeIndicator.getComposeID().length() == 0) {
            crpProgramOutcomeIndicator.setComposeID(programOutcomePrev.getComposeID() + "-" + crpIndicatorAdd.getId());
            crpIndicatorAdd.setComposeID(programOutcomePrev.getComposeID() + "-" + crpIndicatorAdd.getId());
            crpProgramOutcomeIndicatorDAO.save(crpIndicatorAdd);

          }


        } else {
          CrpProgramOutcomeIndicator crpIndicatortoUpdate = programOutcomePrev.getCrpProgramOutcomeIndicators().stream()
            .filter(c -> c.isActive() && c.getComposeID().equals(crpProgramOutcomeIndicator.getComposeID()))
            .collect(Collectors.toList()).get(0);
          crpIndicatortoUpdate.setIndicator(crpProgramOutcomeIndicator.getIndicator());

          crpProgramOutcomeIndicatorDAO.save(crpIndicatortoUpdate);
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
        .filter(c -> c.getComposeID() != null && c.getComposeID().equals(crpMilestone.getComposeID()))
        .collect(Collectors.toList()).isEmpty()) {
        crpMilestoneDAO.deleteCrpMilestone(crpMilestone.getId());
      }
    }
    if (programOutcome.getMilestones() != null) {
      for (CrpMilestone crpMilestone : programOutcome.getMilestones()) {
        if (programOutcomePrev.getCrpMilestones().stream()
          .filter(c -> c.isActive() && c.getComposeID().equals(crpMilestone.getComposeID()))
          .collect(Collectors.toList()).isEmpty()) {

          CrpMilestone crpMilestoneAdd = new CrpMilestone();

          crpMilestoneAdd.setCrpProgramOutcome(programOutcomePrev);
          crpMilestoneAdd.setComposeID(crpMilestone.getComposeID());
          crpMilestoneAdd.setSrfTargetUnit(crpMilestone.getSrfTargetUnit());
          crpMilestoneAdd.setTitle(crpMilestone.getTitle());
          crpMilestoneAdd.setValue(crpMilestone.getValue());
          crpMilestoneAdd.setYear(crpMilestone.getYear());


          /* POWB 2019 */


          crpMilestoneAdd.setPowbMilestoneOtherRisk(crpMilestone.getPowbMilestoneOtherRisk());
          crpMilestoneAdd.setPowbMilestoneVerification(crpMilestone.getPowbMilestoneVerification());


          if (crpMilestone.getPowbIndAssesmentRisk().getId() != null) {
            PowbIndAssesmentRisk powbIndAssesmentRisk =
              powbIndAssesmentRiskDAO.find(crpMilestone.getPowbIndAssesmentRisk().getId());
            crpMilestoneAdd.setPowbIndAssesmentRisk(powbIndAssesmentRisk);
          }

          if (crpMilestone.getPowbIndMilestoneRisk().getId() != null) {
            PowbIndMilestoneRisk powbIndMilestoneRisk =
              powbIndMilestoneRiskDAO.find(crpMilestone.getPowbIndMilestoneRisk().getId());
            crpMilestoneAdd.setPowbIndMilestoneRisk(powbIndMilestoneRisk);
          }

          if (crpMilestone.getPowbIndFollowingMilestone().getId() != null) {
            PowbIndFollowingMilestone powbIndFollowingMilestone =
              powbIndFollowingMilestoneDAO.find(crpMilestone.getPowbIndFollowingMilestone().getId());
            crpMilestoneAdd.setPowbIndFollowingMilestone(powbIndFollowingMilestone);
          }

          if (crpMilestone.getYouthFocusLevel().getId() != null) {
            RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
              repIndGenderYouthFocusLevelDAO.find(crpMilestone.getYouthFocusLevel().getId());
            crpMilestoneAdd.setYouthFocusLevel(repIndGenderYouthFocusLevel);
          }

          if (crpMilestone.getClimateFocusLevel().getId() != null) {
            RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
              repIndGenderYouthFocusLevelDAO.find(crpMilestone.getClimateFocusLevel().getId());
            crpMilestoneAdd.setClimateFocusLevel(repIndGenderYouthFocusLevel);
          }

          if (crpMilestone.getCapdevFocusLevel().getId() != null) {
            RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
              repIndGenderYouthFocusLevelDAO.find(crpMilestone.getCapdevFocusLevel().getId());
            crpMilestoneAdd.setCapdevFocusLevel(repIndGenderYouthFocusLevel);
          }

          if (crpMilestone.getGenderFocusLevel().getId() != null) {
            RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
              repIndGenderYouthFocusLevelDAO.find(crpMilestone.getGenderFocusLevel().getId());
            crpMilestoneAdd.setGenderFocusLevel(repIndGenderYouthFocusLevel);
          }

          /* */

          crpMilestoneAdd.setComposeID(crpMilestone.getComposeID());
          crpMilestoneAdd = crpMilestoneDAO.save(crpMilestoneAdd);
          if (crpMilestone.getComposeID() == null || crpMilestone.getComposeID().length() == 0) {
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
          /* POWB 2019 */

          milestonetoUpdate.setPowbMilestoneOtherRisk(crpMilestone.getPowbMilestoneOtherRisk());
          milestonetoUpdate.setPowbMilestoneVerification(crpMilestone.getPowbMilestoneVerification());

          if (crpMilestone.getPowbIndAssesmentRisk().getId() != null) {
            PowbIndAssesmentRisk powbIndAssesmentRisk =
              powbIndAssesmentRiskDAO.find(crpMilestone.getPowbIndAssesmentRisk().getId());
            milestonetoUpdate.setPowbIndAssesmentRisk(powbIndAssesmentRisk);
          }

          if (crpMilestone.getPowbIndMilestoneRisk().getId() != null) {
            PowbIndMilestoneRisk powbIndMilestoneRisk =
              powbIndMilestoneRiskDAO.find(crpMilestone.getPowbIndMilestoneRisk().getId());
            milestonetoUpdate.setPowbIndMilestoneRisk(powbIndMilestoneRisk);
          }

          if (crpMilestone.getPowbIndFollowingMilestone().getId() != null) {
            PowbIndFollowingMilestone powbIndFollowingMilestone =
              powbIndFollowingMilestoneDAO.find(crpMilestone.getPowbIndFollowingMilestone().getId());
            milestonetoUpdate.setPowbIndFollowingMilestone(powbIndFollowingMilestone);
          }

          if (crpMilestone.getYouthFocusLevel().getId() != null) {
            RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
              repIndGenderYouthFocusLevelDAO.find(crpMilestone.getYouthFocusLevel().getId());
            milestonetoUpdate.setYouthFocusLevel(repIndGenderYouthFocusLevel);
          }

          if (crpMilestone.getClimateFocusLevel().getId() != null) {
            RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
              repIndGenderYouthFocusLevelDAO.find(crpMilestone.getClimateFocusLevel().getId());
            milestonetoUpdate.setClimateFocusLevel(repIndGenderYouthFocusLevel);
          }

          if (crpMilestone.getCapdevFocusLevel().getId() != null) {
            RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
              repIndGenderYouthFocusLevelDAO.find(crpMilestone.getCapdevFocusLevel().getId());
            milestonetoUpdate.setCapdevFocusLevel(repIndGenderYouthFocusLevel);
          }

          if (crpMilestone.getGenderFocusLevel().getId() != null) {
            RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
              repIndGenderYouthFocusLevelDAO.find(crpMilestone.getGenderFocusLevel().getId());
            milestonetoUpdate.setGenderFocusLevel(repIndGenderYouthFocusLevel);
          }

          /*  */
          crpMilestoneDAO.save(milestonetoUpdate);
        }
      }
    }
  }
}
