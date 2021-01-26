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


import org.cgiar.ccafs.marlo.data.dao.DeliverableAffiliationDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableAffiliationManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableAffiliation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableAffiliationManagerImpl implements DeliverableAffiliationManager {


  private DeliverableAffiliationDAO deliverableAffiliationDAO;
  // Managers


  @Inject
  public DeliverableAffiliationManagerImpl(DeliverableAffiliationDAO deliverableAffiliationDAO) {
    this.deliverableAffiliationDAO = deliverableAffiliationDAO;


  }

  @Override
  public void deleteDeliverableAffiliation(long deliverableAffiliationId) {

    deliverableAffiliationDAO.deleteDeliverableAffiliation(deliverableAffiliationId);
  }

  @Override
  public boolean existDeliverableAffiliation(long deliverableAffiliationID) {

    return deliverableAffiliationDAO.existDeliverableAffiliation(deliverableAffiliationID);
  }

  @Override
  public List<DeliverableAffiliation> findAll() {

    return deliverableAffiliationDAO.findAll();

  }

  @Override
  public DeliverableAffiliation getDeliverableAffiliationById(long deliverableAffiliationID) {

    return deliverableAffiliationDAO.find(deliverableAffiliationID);
  }

  @Override
  public DeliverableAffiliation saveDeliverableAffiliation(DeliverableAffiliation deliverableAffiliation) {

    return deliverableAffiliationDAO.save(deliverableAffiliation);
  }


}
