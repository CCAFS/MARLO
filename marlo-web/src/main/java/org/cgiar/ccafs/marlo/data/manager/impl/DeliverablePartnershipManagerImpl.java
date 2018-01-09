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


import org.cgiar.ccafs.marlo.data.dao.DeliverablePartnershipDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverablePartnershipManager;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverablePartnershipManagerImpl implements DeliverablePartnershipManager {


  private DeliverablePartnershipDAO deliverablePartnershipDAO;
  // Managers


  @Inject
  public DeliverablePartnershipManagerImpl(DeliverablePartnershipDAO deliverablePartnershipDAO) {
    this.deliverablePartnershipDAO = deliverablePartnershipDAO;


  }

  @Override
  public void deleteDeliverablePartnership(long deliverablePartnershipId) {

    deliverablePartnershipDAO.deleteDeliverablePartnership(deliverablePartnershipId);
  }

  @Override
  public boolean existDeliverablePartnership(long deliverablePartnershipID) {

    return deliverablePartnershipDAO.existDeliverablePartnership(deliverablePartnershipID);
  }

  @Override
  public List<DeliverablePartnership> findAll() {

    return deliverablePartnershipDAO.findAll();

  }

  @Override
  public List<DeliverablePartnership> findForDeliverableIdAndPartnerTypeOther(long deliverableId) {
    return deliverablePartnershipDAO.findForDeliverableIdAndPartnerTypeOther(deliverableId);
  }

  @Override
  public List<DeliverablePartnership> findForDeliverableIdAndProjectPersonIdPartnerTypeOther(long deliverableId,
    long projectPersonId) {
    return deliverablePartnershipDAO.findForDeliverableIdAndProjectPersonIdPartnerTypeOther(deliverableId,
      projectPersonId);
  }

  @Override
  public DeliverablePartnership getDeliverablePartnershipById(long deliverablePartnershipID) {

    return deliverablePartnershipDAO.find(deliverablePartnershipID);
  }

  @Override
  public DeliverablePartnership saveDeliverablePartnership(DeliverablePartnership deliverablePartnership) {

    return deliverablePartnershipDAO.save(deliverablePartnership);
  }


}
