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


import org.cgiar.ccafs.marlo.data.dao.DeliverableCrpOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableCrpOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrpOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableCrpOutcomeManagerImpl implements DeliverableCrpOutcomeManager {


  private DeliverableCrpOutcomeDAO deliverableCrpOutcomeDAO;
  // Managers


  @Inject
  public DeliverableCrpOutcomeManagerImpl(DeliverableCrpOutcomeDAO deliverableCrpOutcomeDAO) {
    this.deliverableCrpOutcomeDAO = deliverableCrpOutcomeDAO;


  }

  @Override
  public void deleteDeliverableCrpOutcome(long deliverableCrpOutcomeId) {

    deliverableCrpOutcomeDAO.deleteDeliverableCrpOutcome(deliverableCrpOutcomeId);
  }

  @Override
  public boolean existDeliverableCrpOutcome(long deliverableCrpOutcomeID) {

    return deliverableCrpOutcomeDAO.existDeliverableCrpOutcome(deliverableCrpOutcomeID);
  }

  @Override
  public List<DeliverableCrpOutcome> findAll() {

    return deliverableCrpOutcomeDAO.findAll();

  }

  @Override
  public DeliverableCrpOutcome getDeliverableCrpOutcomeById(long deliverableCrpOutcomeID) {

    return deliverableCrpOutcomeDAO.find(deliverableCrpOutcomeID);
  }

  @Override
  public DeliverableCrpOutcome saveDeliverableCrpOutcome(DeliverableCrpOutcome deliverableCrpOutcome) {

    return deliverableCrpOutcomeDAO.save(deliverableCrpOutcome);
  }


}
