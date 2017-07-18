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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.CrpProgramOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.CrpAssumption;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class CrpProgramOutcomeMySQLDAO implements CrpProgramOutcomeDAO {

  private StandardDAO dao;

  @Inject
  public CrpProgramOutcomeMySQLDAO(StandardDAO dao) {
    this.dao = dao;
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
        dao.save(crpMilestoneAdd);
      }
    }
  }

  /**
   * clone or update the otucome for next phases
   * 
   * @param next the next phase to clone
   * @param crpProgramID the program id we are working
   * @param otucome the outcome to clone
   */
  private void addCrpPorgramOutcomePhase(Phase next, long crpProgramID, CrpProgramOutcome outcome,
    String previousTitle) {
    Phase phase = dao.find(Phase.class, next.getId());
    List<CrpProgramOutcome> outcomes =
      phase.getOutcomes().stream().filter(c -> c.isActive() && c.getCrpProgram().getId().longValue() == crpProgramID
        && c.getDescription().equals(previousTitle)).collect(Collectors.toList());
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
      dao.save(outcomeAdd);

      this.addCrpSubIdos(outcome, outcomeAdd);
      this.addCrpMilestones(outcome, outcomeAdd);
    } else {
      if (phase.getEditable() != null && phase.getEditable()) {
        for (CrpProgramOutcome outcomePrev : outcomes) {
          outcomePrev.setSrfTargetUnit(outcome.getSrfTargetUnit());
          outcomePrev.setValue(outcome.getValue());
          outcomePrev.setYear(outcome.getYear());
          outcomePrev.setDescription(outcome.getDescription());
          dao.update(outcomePrev);
          this.updateCrpSubIdos(outcomePrev, outcome);
        }
      }
    }

    if (phase.getNext() != null) {
      this.addCrpPorgramOutcomePhase(phase.getNext(), crpProgramID, outcome, previousTitle);
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
        dao.update(crpOutcomeSubIdoAdd);
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
          dao.save(crpAssumptionAdd);

        }
      }
    }
  }

  @Override
  public boolean deleteCrpProgramOutcome(long crpProgramOutcomeId) {
    CrpProgramOutcome crpProgramOutcome = this.find(crpProgramOutcomeId);
    crpProgramOutcome.setActive(false);
    return this.save(crpProgramOutcome) > 0;
  }

  @Override
  public boolean existCrpProgramOutcome(long crpProgramOutcomeID) {
    CrpProgramOutcome crpProgramOutcome = this.find(crpProgramOutcomeID);
    if (crpProgramOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpProgramOutcome find(long id) {
    return dao.find(CrpProgramOutcome.class, id);

  }

  @Override
  public List<CrpProgramOutcome> findAll() {
    String query = "from " + CrpProgramOutcome.class.getName() + " where is_active=1";
    List<CrpProgramOutcome> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }


  @Override
  public long save(CrpProgramOutcome crpProgramOutcome) {
    String title = "";
    if (crpProgramOutcome.getId() != null) {
      title = this.find(crpProgramOutcome.getId()).getDescription();
    } else {
      title = crpProgramOutcome.getDescription();
    }
    if (crpProgramOutcome.getId() == null) {
      dao.save(crpProgramOutcome);
    } else {
      dao.update(crpProgramOutcome);
    }
    if (crpProgramOutcome.getPhase().getNext() != null) {
      this.addCrpPorgramOutcomePhase(crpProgramOutcome.getPhase().getNext(), crpProgramOutcome.getCrpProgram().getId(),
        crpProgramOutcome, title);
    }
    return crpProgramOutcome.getId();
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
        .filter(c -> c.getSrfSubIdo().equals(outcomeSubIdo.getSrfSubIdo())).collect(Collectors.toList()).isEmpty()) {
        outcomeSubIdo.setActive(false);
        dao.update(outcomeSubIdo);
      }
    }
    if (programOutcome.getSubIdos() != null) {
      for (CrpOutcomeSubIdo outcomeSubIdo : programOutcome.getSubIdos()) {
        if (programOutcomePrev.getCrpOutcomeSubIdos().stream()
          .filter(c -> c.isActive() && c.getSrfSubIdo().equals(outcomeSubIdo.getSrfSubIdo()))
          .collect(Collectors.toList()).isEmpty()) {

          CrpOutcomeSubIdo outcomeSubIdoAdd = new CrpOutcomeSubIdo();

          outcomeSubIdoAdd.setCrpProgramOutcome(programOutcome);
          outcomeSubIdoAdd.setModifiedBy(programOutcomePrev.getModifiedBy());
          outcomeSubIdoAdd.setActive(true);
          outcomeSubIdoAdd.setActiveSince(programOutcomePrev.getActiveSince());
          outcomeSubIdoAdd.setModificationJustification(programOutcomePrev.getModificationJustification());
          outcomeSubIdoAdd.setCreatedBy(programOutcomePrev.getCreatedBy());
          outcomeSubIdoAdd.setContribution(outcomeSubIdo.getContribution());
          outcomeSubIdoAdd.setSrfSubIdo(outcomeSubIdo.getSrfSubIdo());
          dao.update(outcomeSubIdoAdd);

        }
      }
    }
  }


}