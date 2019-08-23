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


import org.cgiar.ccafs.marlo.data.dao.DeliverableUserPartnershipPersonDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserPartnershipPersonManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnershipPerson;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableUserPartnershipPersonManagerImpl implements DeliverableUserPartnershipPersonManager {


  private DeliverableUserPartnershipPersonDAO deliverableUserPartnershipPersonDAO;
  // Managers


  @Inject
  public DeliverableUserPartnershipPersonManagerImpl(
    DeliverableUserPartnershipPersonDAO deliverableUserPartnershipPersonDAO) {
    this.deliverableUserPartnershipPersonDAO = deliverableUserPartnershipPersonDAO;
  }

  @Override
  public void deleteDeliverableUserPartnershipPerson(long deliverableUserPartnershipPersonId) {

    deliverableUserPartnershipPersonDAO.deleteDeliverableUserPartnershipPerson(deliverableUserPartnershipPersonId);
  }

  @Override
  public boolean existDeliverableUserPartnershipPerson(long deliverableUserPartnershipPersonID) {

    return deliverableUserPartnershipPersonDAO
      .existDeliverableUserPartnershipPerson(deliverableUserPartnershipPersonID);
  }

  @Override
  public List<DeliverableUserPartnershipPerson> findAll() {

    return deliverableUserPartnershipPersonDAO.findAll();

  }

  @Override
  public DeliverableUserPartnershipPerson
    getDeliverableUserPartnershipPersonById(long deliverableUserPartnershipPersonID) {

    return deliverableUserPartnershipPersonDAO.find(deliverableUserPartnershipPersonID);
  }

  @Override
  public DeliverableUserPartnershipPerson
    saveDeliverableUserPartnershipPerson(DeliverableUserPartnershipPerson deliverableUserPartnershipPerson) {

    return deliverableUserPartnershipPersonDAO.save(deliverableUserPartnershipPerson);
  }


}
