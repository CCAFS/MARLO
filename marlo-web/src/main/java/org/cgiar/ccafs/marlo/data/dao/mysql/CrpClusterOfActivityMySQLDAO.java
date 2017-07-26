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

import org.cgiar.ccafs.marlo.data.dao.CrpClusterOfActivityDAO;
import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class CrpClusterOfActivityMySQLDAO implements CrpClusterOfActivityDAO {

  private StandardDAO dao;

  @Inject
  public CrpClusterOfActivityMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  /**
   * clone or update the outcome for next phases
   * 
   * @param next the next phase to clone
   * @param crpProgramID the program id we are working
   * @param crpCluster the CrpClusterOfActivity to clone
   */
  private void addCrpClusterPhase(Phase next, long crpProgramID, CrpClusterOfActivity crpCluster) {
    Phase phase = dao.find(Phase.class, next.getId());
    List<CrpClusterOfActivity> clusters =
      phase.getClusters().stream().filter(c -> c.isActive() && c.getCrpProgram().getId().longValue() == crpProgramID
        && c.getIdentifier().equals(crpCluster.getIdentifier())).collect(Collectors.toList());
    if (phase.getEditable() != null && phase.getEditable() && clusters.isEmpty()) {
      CrpClusterOfActivity clusterAdd = new CrpClusterOfActivity();
      clusterAdd.setActive(true);
      clusterAdd.setActiveSince(crpCluster.getActiveSince());
      clusterAdd.setCreatedBy(crpCluster.getCreatedBy());
      clusterAdd.setModificationJustification(crpCluster.getModificationJustification());
      clusterAdd.setModifiedBy(crpCluster.getModifiedBy());
      clusterAdd.setPhase(phase);
      clusterAdd.setCrpProgram(crpCluster.getCrpProgram());
      clusterAdd.setDescription(crpCluster.getDescription());
      clusterAdd.setIdentifier(crpCluster.getIdentifier());
      dao.save(clusterAdd);
      this.addLeaders(crpCluster, clusterAdd);
      this.addCrpKeyOutputs(crpCluster, clusterAdd);
    } else {
      if (phase.getEditable() != null && phase.getEditable()) {
        for (CrpClusterOfActivity clusterPrev : clusters) {
          clusterPrev.setDescription(crpCluster.getDescription());
          clusterPrev.setIdentifier(crpCluster.getIdentifier());
          dao.update(clusterPrev);
          this.updateCrpSubIdos(outcomePrev, outcome);
          this.updateMilestones(outcomePrev, outcome);
        }
      }
    }

    if (phase.getNext() != null) {
      this.addCrpClusterPhase(phase.getNext(), crpProgramID, crpCluster);
    }


  }

  /**
   * clone the key ouputs
   * 
   * @param crpClusterOfActivity clusterActivity original
   * @param crpClusterOfActivityAdd clusterActivity new
   */


  private void addCrpKeyOutputs(CrpClusterOfActivity crpClusterOfActivity,
    CrpClusterOfActivity crpClusterOfActivityAdd) {

    if (crpClusterOfActivity.getKeyOutputs() != null) {
      for (CrpClusterKeyOutput crpClusteKeyOutput : crpClusterOfActivity.getKeyOutputs()) {
        CrpClusterKeyOutput crpClusteKeyOutputAdd = new CrpClusterKeyOutput();
        crpClusteKeyOutputAdd.setActive(true);
        crpClusteKeyOutputAdd.setActiveSince(crpClusterOfActivity.getActiveSince());
        crpClusteKeyOutputAdd.setCreatedBy(crpClusterOfActivity.getCreatedBy());
        crpClusteKeyOutputAdd.setModificationJustification("");
        crpClusteKeyOutputAdd.setModifiedBy(crpClusterOfActivity.getCreatedBy());
        crpClusteKeyOutputAdd.setContribution(crpClusteKeyOutput.getContribution());
        crpClusteKeyOutputAdd.setKeyOutput(crpClusteKeyOutput.getKeyOutput());
        crpClusteKeyOutputAdd.setComposeID(crpClusteKeyOutput.getComposeID());
        dao.save(crpClusteKeyOutputAdd);
        if (crpClusteKeyOutput.getComposeID() == null) {
          crpClusteKeyOutput.setComposeID(crpClusterOfActivity.getIdentifier() + "-" + crpClusteKeyOutputAdd.getId());
          crpClusteKeyOutputAdd
            .setComposeID(crpClusterOfActivity.getIdentifier() + "-" + crpClusteKeyOutputAdd.getId());
          dao.update(crpClusteKeyOutputAdd);
        }
        if (crpClusteKeyOutput.getKeyOutputOutcomes() != null) {
          for (CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome : crpClusteKeyOutput.getKeyOutputOutcomes()) {
            CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcomeAdd = new CrpClusterKeyOutputOutcome();
            crpClusterKeyOutputOutcomeAdd.setActive(true);
            crpClusterKeyOutputOutcomeAdd.setActiveSince(crpClusterOfActivity.getActiveSince());
            crpClusterKeyOutputOutcomeAdd.setCreatedBy(crpClusterOfActivity.getCreatedBy());
            crpClusterKeyOutputOutcomeAdd.setModificationJustification("");
            crpClusterKeyOutputOutcomeAdd.setModifiedBy(crpClusterOfActivity.getCreatedBy());
            crpClusterKeyOutputOutcomeAdd.setCrpClusterKeyOutput(crpClusteKeyOutputAdd);
            crpClusterKeyOutputOutcomeAdd.setCrpProgramOutcome(crpClusterKeyOutputOutcome.getCrpProgramOutcome());
            dao.save(crpClusterKeyOutputOutcomeAdd);
          }
        }
      }
    }
  }


  /**
   * clone the cluster leaders
   * 
   * @param crpClusterOfActivity clusterActivity original
   * @param crpClusterOfActivityAdd clusterActivity new
   */

  private void addLeaders(CrpClusterOfActivity crpClusterOfActivity, CrpClusterOfActivity crpClusterOfActivityAdd) {

    if (crpClusterOfActivity.getLeaders() != null) {
      for (CrpClusterActivityLeader crpClusterActivityLeader : crpClusterOfActivity.getLeaders()) {
        CrpClusterActivityLeader crpClusterActivityLeaderAdd = new CrpClusterActivityLeader();
        crpClusterActivityLeaderAdd.setActive(true);
        crpClusterActivityLeaderAdd.setActiveSince(crpClusterOfActivity.getActiveSince());
        crpClusterActivityLeaderAdd.setCreatedBy(crpClusterOfActivity.getCreatedBy());
        crpClusterActivityLeaderAdd.setModificationJustification("");
        crpClusterActivityLeaderAdd.setUser(crpClusterActivityLeader.getUser());
        crpClusterActivityLeaderAdd.setCrpClusterOfActivity(crpClusterOfActivityAdd);
        dao.save(crpClusterActivityLeaderAdd);
      }
    }
  }

  @Override
  public boolean deleteCrpClusterOfActivity(long crpClusterOfActivityId) {
    CrpClusterOfActivity crpClusterOfActivity = this.find(crpClusterOfActivityId);
    crpClusterOfActivity.setActive(false);
    return this.save(crpClusterOfActivity) > 0;
  }

  @Override
  public boolean existCrpClusterOfActivity(long crpClusterOfActivityID) {
    CrpClusterOfActivity crpClusterOfActivity = this.find(crpClusterOfActivityID);
    if (crpClusterOfActivity == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpClusterOfActivity find(long id) {
    return dao.find(CrpClusterOfActivity.class, id);

  }

  @Override
  public List<CrpClusterOfActivity> findAll() {
    String query = "from " + CrpClusterOfActivity.class.getName() + " where is_active=1";
    List<CrpClusterOfActivity> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public Long save(CrpClusterOfActivity crpClusterOfActivity) {
    if (crpClusterOfActivity.getId() == null) {
      dao.save(crpClusterOfActivity);
    } else {
      dao.update(crpClusterOfActivity);
    }
    return crpClusterOfActivity.getId();
  }

  /**
   * check the sub-idos and updated
   * 
   * @param crpClusterOfActivityPrev outcome to update
   * @param crpClusterOfActivity outcome modified
   */
  private void updateClusterLeaders(CrpClusterOfActivity crpClusterOfActivityPrev,
    CrpClusterOfActivity crpClusterOfActivity) {
    for (CrpOutcomeSubIdo outcomeSubIdo : programOutcomePrev.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {
      if (programOutcome.getSubIdos() == null || programOutcome.getSubIdos().stream()
        .filter(c -> c.getSrfSubIdo() != null && c.getSrfSubIdo().equals(outcomeSubIdo.getSrfSubIdo()))
        .collect(Collectors.toList()).isEmpty()) {
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

          outcomeSubIdoAdd.setCrpProgramOutcome(programOutcomePrev);
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