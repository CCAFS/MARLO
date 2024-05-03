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
import org.cgiar.ccafs.marlo.data.dao.DeliverableInfoDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterKeyOutputManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Garcia
 */

@Named
public class DeliverableInfoManagerImpl implements DeliverableInfoManager {

  private final Logger logger = LoggerFactory.getLogger(DeliverableInfoManagerImpl.class);

  private PhaseDAO phaseDAO;
  private DeliverableInfoDAO deliverableInfoDAO;
  private CrpClusterKeyOutputManager crpClusterKeyOutputManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  // Managers


  @Inject
  public DeliverableInfoManagerImpl(DeliverableInfoDAO deliverableInfoDAO, PhaseDAO phaseDAO,
    CrpClusterKeyOutputManager crpClusterKeyOutputManager, CrpProgramOutcomeManager crpProgramOutcomeManager) {
    this.deliverableInfoDAO = deliverableInfoDAO;
    this.phaseDAO = phaseDAO;
    this.crpClusterKeyOutputManager = crpClusterKeyOutputManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
  }

  @Override
  public void deleteDeliverableInfo(long deliverableInfoId) {

    deliverableInfoDAO.deleteDeliverableInfo(deliverableInfoId);
  }

  @Override
  public boolean existDeliverableInfo(long deliverableInfoID) {

    return deliverableInfoDAO.existDeliverableInfo(deliverableInfoID);
  }

  @Override
  public List<DeliverableInfo> findAll() {

    return deliverableInfoDAO.findAll();

  }

  @Override
  public DeliverableInfo getDeliverableInfoById(long deliverableInfoID) {

    return deliverableInfoDAO.find(deliverableInfoID);
  }

  @Override
  public List<DeliverableInfo> getDeliverablesInfoByDeliverableId(long deliverableId) {
    return deliverableInfoDAO.getDeliverablesInfoByDeliverableId(deliverableId);
  }

  @Override
  public List<DeliverableInfo> getDeliverablesInfoByPhase(Phase phase) {
    return deliverableInfoDAO.getDeliverablesInfoByPhase(phase);
  }

  @Override
  public List<DeliverableInfo> getDeliverablesInfoByProjectAndPhase(Phase phase, Project project) {
    return deliverableInfoDAO.getDeliverablesInfoByProjectAndPhase(phase, project);
  }

  @Override
  public List<DeliverableInfo> getDeliverablesInfoByType(Phase phase, DeliverableType deliverableType) {
    return deliverableInfoDAO.getDeliverablesInfoByType(phase, deliverableType);
  }


  @Override
  public boolean isDeliverableSubcategoryIncludedWebsite(long deliverableID, Phase phase) {

    return deliverableInfoDAO.isDeliverableSubcategoryIncludedWebsite(deliverableID, phase);
  }

  @Override
  public DeliverableInfo saveDeliverableInfo(DeliverableInfo deliverableInfo) {
    DeliverableInfo resultDeliverableInfo = deliverableInfoDAO.save(deliverableInfo);
    boolean isPublication = deliverableInfo.getDeliverable().getIsPublication() != null
      && deliverableInfo.getDeliverable().getIsPublication();

    List<DeliverableInfo> deliverableInfosTemp = new ArrayList<>();
    try {
      deliverableInfosTemp =
        deliverableInfoDAO.getDeliverablesInfoByDeliverableId(deliverableInfo.getDeliverable().getId());
      logger.info("DeliverableInfoManagerImpl linea 127 " + deliverableInfosTemp.size());
    } catch (Exception e) {
      logger.error("unable to get deliverables info");
    }

    if (deliverableInfo.getPhase().getDescription().equals(APConstants.PLANNING)
      && deliverableInfo.getPhase().getNext() != null && !isPublication) {
      this.saveInfoPhase(deliverableInfo.getPhase().getNext(), deliverableInfo.getDeliverable().getId(),
        deliverableInfo, deliverableInfosTemp);
    }
    if (deliverableInfo.getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (deliverableInfo.getPhase().getNext() != null && deliverableInfo.getPhase().getNext().getNext() != null
        && !isPublication) {
        Phase upkeepPhase = deliverableInfo.getPhase().getNext().getNext();
        if (upkeepPhase != null) {
          this.saveInfoPhase(upkeepPhase, deliverableInfo.getDeliverable().getId(), deliverableInfo,
            deliverableInfosTemp);
        }
      }
    }
    return resultDeliverableInfo;
  }

  public void saveInfoPhase(Phase next, Long deliverableId, DeliverableInfo deliverableInfo,
    List<DeliverableInfo> deliverableInfosTemp) {
    Phase phase = phaseDAO.find(next.getId());
    logger.info("DeliverableInfoManagerImpl linea 138 ");
    // cgamboa 03/05/2024 function phase.getDeliverableInfos() has been changed by deliverableInfosTemp
    List<DeliverableInfo> deliverableInfos = deliverableInfosTemp.stream()
      .filter(c -> c.getDeliverable().getId().equals(deliverableId) && c.getPhase().getId().equals(phase.getId()))
      .collect(Collectors.toList());
    // logger.info("DeliverableInfoManagerImpl linea 141 " + phase.getDeliverableInfos().size());
    CrpClusterKeyOutput keyOutputPhase = null;
    logger.info("DeliverableInfoManagerImpl linea 142 " + deliverableInfos.size());
    logger.info("DeliverableInfoManagerImpl linea 154 id " + deliverableInfos.get(0).getId());
    if (deliverableInfo.getCrpClusterKeyOutput() != null && deliverableInfo.getCrpClusterKeyOutput().getId() != -1) {

      CrpClusterKeyOutput keyOutput =
        crpClusterKeyOutputManager.getCrpClusterKeyOutputById(deliverableInfo.getCrpClusterKeyOutput().getId());

      CrpClusterOfActivity crpCluster = keyOutput.getCrpClusterOfActivity();

      List<CrpClusterOfActivity> clusters = phase.getClusters().stream()
        .filter(c -> c.isActive() && c.getCrpProgram().getId().equals(crpCluster.getCrpProgram().getId())
          && c.getIdentifier().equals(crpCluster.getIdentifier()))
        .collect(Collectors.toList());

      if (!clusters.isEmpty()) {

        CrpClusterOfActivity crpClusterPhase = clusters.get(0);

        List<CrpClusterKeyOutput> keyOutputsPhases = crpClusterPhase.getCrpClusterKeyOutputs().stream()
          .filter(k -> k.isActive() && k.getComposeID().equals(keyOutput.getComposeID())).collect(Collectors.toList());

        if (!keyOutputsPhases.isEmpty()) {
          keyOutputPhase = keyOutputsPhases.get(0);
        }

      }
    }
    CrpProgramOutcome programOutcomePhase = null;

    if (deliverableInfo.getCrpProgramOutcome() != null && deliverableInfo.getCrpProgramOutcome().getId() != -1
      && deliverableInfo.getCrpProgramOutcome().getComposeID() != null) {
      List<CrpProgramOutcome> programOutcomes = new ArrayList<>();
      programOutcomes = crpProgramOutcomeManager.getAllCrpProgramOutcomesByComposedIdFromPhase(
        deliverableInfo.getCrpProgramOutcome().getComposeID(), phase.getId());
      if (programOutcomes != null && !programOutcomes.isEmpty() && programOutcomes.get(0) != null) {
        programOutcomePhase = programOutcomes.get(0);
      }
    }


    if (!deliverableInfos.isEmpty()) {
      for (DeliverableInfo deliverableInfoPhase : deliverableInfos) {
        deliverableInfoPhase.updateDeliverableInfo(deliverableInfo);
        deliverableInfoPhase.setCrpClusterKeyOutput(keyOutputPhase);
        deliverableInfoPhase.setCrpProgramOutcome(programOutcomePhase);
        deliverableInfoDAO.save(deliverableInfoPhase);
      }
    } else {
      DeliverableInfo deliverableInfoAdd = new DeliverableInfo();
      deliverableInfoAdd.setDeliverable(deliverableInfo.getDeliverable());
      deliverableInfoAdd.updateDeliverableInfo(deliverableInfo);
      deliverableInfoAdd.setCrpClusterKeyOutput(keyOutputPhase);
      deliverableInfoAdd.setCrpProgramOutcome(programOutcomePhase);
      deliverableInfoAdd.setDeliverableType(deliverableInfo.getDeliverableType());
      deliverableInfoAdd.setPhase(phase);
      deliverableInfoDAO.save(deliverableInfoAdd);
    }
    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), deliverableId, deliverableInfo, deliverableInfosTemp);
    }
    logger.info("DeliverableInfoManagerImpl linea 202 ");
  }


}
