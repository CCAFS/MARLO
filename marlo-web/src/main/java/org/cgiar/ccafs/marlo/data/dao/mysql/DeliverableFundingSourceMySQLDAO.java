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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.DeliverableFundingSourceDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class DeliverableFundingSourceMySQLDAO implements DeliverableFundingSourceDAO {

  private StandardDAO dao;

  @Inject
  public DeliverableFundingSourceMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  /**
   * clone or update the deliverable funding sources for next phases
   * 
   * @param next the next phase to clone
   * @param deliverableID the deliverable id we are working
   * @param deliverableFundingSource the deliverable funding source to clone
   */
  private void addDeliverableFundingSourcePhase(Phase next, long deliverableID,
    DeliverableFundingSource deliverableFundingSource) {
    Phase phase = dao.find(Phase.class, next.getId());

    List<DeliverableFundingSource> deliverableFundingSources = phase.getDeliverableFundingSources().stream()
      .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == deliverableID
        && deliverableFundingSource.getFundingSource().getId().equals(c.getFundingSource().getId()))
      .collect(Collectors.toList());
    if (phase.getEditable() != null && phase.getEditable() && deliverableFundingSources.isEmpty()) {
      DeliverableFundingSource deliverableFundingSourceAdd = new DeliverableFundingSource();
      deliverableFundingSourceAdd.setActive(true);
      deliverableFundingSourceAdd.setActiveSince(deliverableFundingSource.getActiveSince());
      deliverableFundingSourceAdd.setCreatedBy(deliverableFundingSource.getCreatedBy());
      deliverableFundingSourceAdd.setModificationJustification(deliverableFundingSource.getModificationJustification());
      deliverableFundingSourceAdd.setModifiedBy(deliverableFundingSource.getModifiedBy());
      deliverableFundingSourceAdd.setPhase(phase);
      deliverableFundingSourceAdd.setDeliverable(deliverableFundingSource.getDeliverable());
      deliverableFundingSourceAdd.setFundingSource(deliverableFundingSource.getFundingSource());
      dao.save(deliverableFundingSourceAdd);
    }

    if (phase.getNext() != null) {
      this.addDeliverableFundingSourcePhase(phase.getNext(), deliverableID, deliverableFundingSource);
    }


  }


  @Override
  public boolean deleteDeliverableFundingSource(long deliverableFundingSourceId) {
    DeliverableFundingSource deliverableFundingSource = this.find(deliverableFundingSourceId);
    deliverableFundingSource.setActive(false);

    boolean result = dao.update(deliverableFundingSource);
    Phase currentPhase = dao.find(Phase.class, deliverableFundingSource.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (deliverableFundingSource.getPhase().getNext() != null) {
        this.deleteDeliverableFundingSource(deliverableFundingSource.getPhase().getNext(),
          deliverableFundingSource.getDeliverable().getId(), deliverableFundingSource);
      }
    }

    return result;


  }


  public void deleteDeliverableFundingSource(Phase next, long deliverableID,
    DeliverableFundingSource deliverableFundingSource) {
    Phase phase = dao.find(Phase.class, next.getId());

    if (phase.getEditable() != null && phase.getEditable()) {

      List<DeliverableFundingSource> fundingSources = phase.getDeliverableFundingSources().stream()
        .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == deliverableID
          && deliverableFundingSource.getFundingSource().getId().equals(c.getFundingSource().getId()))
        .collect(Collectors.toList());

      for (DeliverableFundingSource deFundingSource : fundingSources) {
        deFundingSource.setActive(false);
        dao.update(deFundingSource);

      }
    }
    if (phase.getNext() != null) {
      this.deleteDeliverableFundingSource(phase.getNext(), deliverableID, deliverableFundingSource);
    }


  }

  @Override
  public boolean existDeliverableFundingSource(long deliverableFundingSourceID) {
    DeliverableFundingSource deliverableFundingSource = this.find(deliverableFundingSourceID);
    if (deliverableFundingSource == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableFundingSource find(long id) {
    return dao.find(DeliverableFundingSource.class, id);

  }

  @Override
  public List<DeliverableFundingSource> findAll() {
    String query = "from " + DeliverableFundingSource.class.getName() + " where is_active=1";
    List<DeliverableFundingSource> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(DeliverableFundingSource deliverableFundingSource) {
    if (deliverableFundingSource.getId() == null) {
      dao.save(deliverableFundingSource);
    } else {
      dao.update(deliverableFundingSource);
    }
    Phase currentPhase = dao.find(Phase.class, deliverableFundingSource.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (deliverableFundingSource.getPhase().getNext() != null) {
        this.addDeliverableFundingSourcePhase(deliverableFundingSource.getPhase().getNext(),
          deliverableFundingSource.getDeliverable().getId(), deliverableFundingSource);
      }
    }
    return deliverableFundingSource.getId();
  }


}