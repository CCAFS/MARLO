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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

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
  public List<Deliverable> getDeliverablesByPhase(long phase) {
    return deliverableDAO.getDeliverablesByPhase(phase);
  }

  @Override
  public Deliverable saveDeliverable(Deliverable deliverable) {

    return deliverableDAO.save(deliverable);
  }

  @Override
  public Deliverable saveDeliverable(Deliverable deliverable, String section, List<String> relationsName, Phase phase) {
    Deliverable resultDeliverable = deliverableDAO.save(deliverable, section, relationsName, phase);

    boolean isPublication = resultDeliverable.getIsPublication() != null && resultDeliverable.getIsPublication();
    if (deliverable.getDeliverableInfo().getPhase().getDescription().equals(APConstants.PLANNING)
      && deliverable.getDeliverableInfo().getPhase().getNext() != null && !isPublication) {
      this.saveDeliverablePhase(deliverable.getDeliverableInfo().getPhase().getNext(), deliverable.getId(),
        deliverable);
    }
    if (deliverable.getDeliverableInfo().getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (deliverable.getDeliverableInfo().getPhase().getNext() != null
        && deliverable.getDeliverableInfo().getPhase().getNext().getNext() != null && !isPublication) {
        Phase upkeepPhase = deliverable.getDeliverableInfo().getPhase().getNext().getNext();
        if (upkeepPhase != null) {
          this.saveDeliverablePhase(upkeepPhase, deliverable.getId(), deliverable);
        }
      }
    }

    return resultDeliverable;
  }

  public void saveDeliverablePhase(Phase next, long deliverableID, Deliverable deliverable) {
    Phase phase = phaseDAO.find(next.getId());

    List<DeliverableInfo> deliverablesInfo = phase.getDeliverableInfos().stream()
      .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == deliverableID)
      .collect(Collectors.toList());

    if (deliverablesInfo == null || deliverablesInfo.isEmpty()) {
      deliverablesInfo = new ArrayList<>();
      deliverablesInfo.add(new DeliverableInfo());
    }

    for (DeliverableInfo deliverableInfo : deliverablesInfo) {
      deliverableInfo.updateDeliverableInfo(deliverable.getDeliverableInfo());

      if (deliverableInfo.getCrpClusterKeyOutput() != null
        && deliverableInfo.getCrpClusterKeyOutput().getId() != null) {

        CrpClusterKeyOutput crpClusterKeyOutput =
          crpClusterKeyOutputDAO.find(deliverableInfo.getCrpClusterKeyOutput().getId());

        CrpClusterOfActivity crpClusterOfActivity = crpClusterOfActivityDAO.getCrpClusterOfActivityByIdentifierPhase(
          crpClusterKeyOutput.getCrpClusterOfActivity().getIdentifier(), phase);
        if (crpClusterOfActivity != null) {
          List<CrpClusterKeyOutput> clusterKeyOutputs = crpClusterOfActivity.getCrpClusterKeyOutputs().stream()
            .filter(c -> c.isActive() && c.getComposeID().equals(crpClusterKeyOutput.getComposeID()))
            .collect(Collectors.toList());
          if (!clusterKeyOutputs.isEmpty()) {
            deliverableInfo.setCrpClusterKeyOutput(clusterKeyOutputs.get(0));
          } else {
            deliverableInfo.setCrpClusterKeyOutput(null);
          }
        } else {
          deliverableInfo.setCrpClusterKeyOutput(null);
        }

      }

      deliverableInfo.setPhase(phase);
      deliverableInfoDAO.save(deliverableInfo);
    }


    if (phase.getNext() != null) {
      this.saveDeliverablePhase(phase.getNext(), deliverableID, deliverable);
    }
  }
}
