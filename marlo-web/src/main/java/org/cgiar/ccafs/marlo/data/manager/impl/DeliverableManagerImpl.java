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
import org.cgiar.ccafs.marlo.data.dao.CrpClusterKeyOutputDAO;
import org.cgiar.ccafs.marlo.data.dao.CrpClusterOfActivityDAO;
import org.cgiar.ccafs.marlo.data.dao.DeliverableDAO;
import org.cgiar.ccafs.marlo.data.dao.DeliverableInfoDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverableManagerImpl implements DeliverableManager {

  private PhaseDAO phaseDAO;

  private DeliverableDAO deliverableDAO;
  private DeliverableInfoDAO deliverableInfoDAO;
  private CrpClusterOfActivityDAO crpClusterOfActivityDAO;
  private CrpClusterKeyOutputDAO crpClusterKeyOutputDAO;

  // Managers


  @Inject
  public DeliverableManagerImpl(DeliverableDAO deliverableDAO, PhaseDAO phaseDAO, DeliverableInfoDAO deliverableInfoDAO,
    CrpClusterOfActivityDAO crpClusterOfActivityDAO, CrpClusterKeyOutputDAO crpClusterKeyOutputDAO) {
    this.deliverableDAO = deliverableDAO;
    this.phaseDAO = phaseDAO;
    this.deliverableInfoDAO = deliverableInfoDAO;
    this.crpClusterOfActivityDAO = crpClusterOfActivityDAO;
    this.crpClusterKeyOutputDAO = crpClusterKeyOutputDAO;

  }

  @Override
  public Deliverable copyDeliverable(Deliverable deliverable, Phase phase) {


    DeliverableInfo deliverableInfo = new DeliverableInfo();
    deliverableInfo.updateDeliverableInfo(deliverable.getDeliverableInfo());

    if (deliverableInfo.getCrpClusterKeyOutput() != null) {
      CrpClusterKeyOutput crpClusterKeyOutput =
        crpClusterKeyOutputDAO.find(deliverableInfo.getCrpClusterKeyOutput().getId());
      CrpClusterOfActivity crpClusterOfActivity = crpClusterOfActivityDAO
        .getCrpClusterOfActivityByIdentifierPhase(crpClusterKeyOutput.getCrpClusterOfActivity().getIdentifier(), phase);
      List<CrpClusterKeyOutput> clusterKeyOutputs = crpClusterOfActivity.getCrpClusterKeyOutputs().stream()
        .filter(c -> c.isActive() && c.getComposeID().equals(deliverableInfo.getCrpClusterKeyOutput().getComposeID()))
        .collect(Collectors.toList());
      if (!clusterKeyOutputs.isEmpty()) {
        deliverableInfo.setCrpClusterKeyOutput(clusterKeyOutputs.get(0));
      }
    }

    deliverableInfo.setPhase(phase);
    deliverableInfoDAO.save(deliverableInfo);
    return deliverableInfo.getDeliverable();
  }

  @Override
  public void deleteDeliverable(long deliverableId) {

    deliverableDAO.deleteDeliverable(deliverableId);
  }

  @Override
  public boolean existDeliverable(long deliverableID) {

    return deliverableDAO.existDeliverable(deliverableID);
  }

  @Override
  public List<Deliverable> findAll() {

    return deliverableDAO.findAll();

  }

  @Override
  public Deliverable getDeliverableById(long deliverableID) {

    return deliverableDAO.find(deliverableID);
  }

  @Override
  public Deliverable saveDeliverable(Deliverable deliverable) {

    return deliverableDAO.save(deliverable);
  }

  @Override
  public Deliverable saveDeliverable(Deliverable deliverable, String section, List<String> relationsName, Phase phase) {
    Deliverable resultDeliverable = deliverableDAO.save(deliverable, section, relationsName, phase);

    Phase currentPhase = phaseDAO.find(deliverable.getDeliverableInfo().getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (deliverable.getDeliverableInfo().getPhase().getNext() != null) {
        this.saveDeliverablePhase(deliverable.getDeliverableInfo().getPhase().getNext(), deliverable.getId(),
          deliverable);
      }
    }
    return resultDeliverable;
  }

  public void saveDeliverablePhase(Phase next, long deliverableID, Deliverable deliverable) {
    Phase phase = phaseDAO.find(next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<DeliverableInfo> deliverablesInfo = phase.getDeliverableInfos().stream()
        .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == deliverableID)
        .collect(Collectors.toList());
      for (DeliverableInfo deliverableInfo : deliverablesInfo) {
        deliverableInfo.updateDeliverableInfo(deliverable.getDeliverableInfo());
        if (deliverableInfo.getCrpClusterKeyOutput() != null) {
          CrpClusterKeyOutput crpClusterKeyOutput =
            crpClusterKeyOutputDAO.find(deliverableInfo.getCrpClusterKeyOutput().getId());
          CrpClusterOfActivity crpClusterOfActivity = crpClusterOfActivityDAO.getCrpClusterOfActivityByIdentifierPhase(
            crpClusterKeyOutput.getCrpClusterOfActivity().getIdentifier(), phase);
          List<CrpClusterKeyOutput> clusterKeyOutputs = crpClusterOfActivity.getCrpClusterKeyOutputs().stream()
            .filter(
              c -> c.isActive() && c.getComposeID().equals(deliverableInfo.getCrpClusterKeyOutput().getComposeID()))
            .collect(Collectors.toList());
          if (!clusterKeyOutputs.isEmpty()) {
            deliverableInfo.setCrpClusterKeyOutput(clusterKeyOutputs.get(0));
          }
        }

        deliverableInfo.setPhase(phase);
        deliverableInfoDAO.save(deliverableInfo);
      }


    }
    if (phase.getNext() != null) {
      this.saveDeliverablePhase(phase.getNext(), deliverableID, deliverable);
    }
  }
}
