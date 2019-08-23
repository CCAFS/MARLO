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
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */

@Named
public class DeliverableInfoManagerImpl implements DeliverableInfoManager {

  private PhaseDAO phaseDAO;

  private DeliverableInfoDAO deliverableInfoDAO;
  // Managers


  @Inject
  public DeliverableInfoManagerImpl(DeliverableInfoDAO deliverableInfoDAO, PhaseDAO phaseDAO) {
    this.deliverableInfoDAO = deliverableInfoDAO;
    this.phaseDAO = phaseDAO;

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
  public DeliverableInfo saveDeliverableInfo(DeliverableInfo deliverableInfo) {
    // changes dperez
    DeliverableInfo resultDeliverableInfo = deliverableInfoDAO.save(deliverableInfo);
    boolean isPublication = deliverableInfo.getDeliverable().getIsPublication() != null
      && deliverableInfo.getDeliverable().getIsPublication();
    if (deliverableInfo.getPhase().getDescription().equals(APConstants.PLANNING)
      && deliverableInfo.getPhase().getNext() != null && !isPublication) {
      this.saveInfoPhase(deliverableInfo.getPhase().getNext(), deliverableInfo.getDeliverable().getId(),
        deliverableInfo);
    }
    if (deliverableInfo.getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (deliverableInfo.getPhase().getNext() != null && deliverableInfo.getPhase().getNext().getNext() != null
        && !isPublication) {
        Phase upkeepPhase = deliverableInfo.getPhase().getNext().getNext();
        if (upkeepPhase != null) {
          this.saveInfoPhase(upkeepPhase, deliverableInfo.getDeliverable().getId(), deliverableInfo);
        }
      }
    }
    return resultDeliverableInfo;
    // END changes dperez
    // return deliverableInfoDAO.save(deliverableInfo);
  }

  public void saveInfoPhase(Phase next, Long deliverableId, DeliverableInfo deliverableInfo) {
    Phase phase = phaseDAO.find(next.getId());
    List<DeliverableInfo> deliverableInfos = phase.getDeliverableInfos().stream()
      .filter(c -> c.getDeliverable().getId().equals(deliverableId)).collect(Collectors.toList());
    if (!deliverableInfos.isEmpty()) {
      for (DeliverableInfo deliverableInfoPhase : deliverableInfos) {
        deliverableInfoPhase.updateDeliverableInfo(deliverableInfo);
        deliverableInfoDAO.save(deliverableInfoPhase);
      }
    } else {
      DeliverableInfo deliverableInfoAdd = new DeliverableInfo();
      deliverableInfoAdd.setDeliverable(deliverableInfo.getDeliverable());
      deliverableInfoAdd.updateDeliverableInfo(deliverableInfo);
      deliverableInfoAdd.setDeliverableType(deliverableInfo.getDeliverableType());
      deliverableInfoAdd.setPhase(phase);
      deliverableInfoDAO.save(deliverableInfoAdd);
    }
    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), deliverableId, deliverableInfo);
    }
  }


}
