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


import org.cgiar.ccafs.marlo.data.dao.DeliverableTraineesIndicatorDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTraineesIndicatorManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableTraineesIndicator;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableTraineesIndicatorManagerImpl implements DeliverableTraineesIndicatorManager {


  private DeliverableTraineesIndicatorDAO deliverableTraineesIndicatorDAO;
  // Managers


  @Inject
  public DeliverableTraineesIndicatorManagerImpl(DeliverableTraineesIndicatorDAO deliverableTraineesIndicatorDAO) {
    this.deliverableTraineesIndicatorDAO = deliverableTraineesIndicatorDAO;


  }

  @Override
  public void deleteDeliverableTraineesIndicator(long deliverableTraineesIndicatorId) {

    deliverableTraineesIndicatorDAO.deleteDeliverableTraineesIndicator(deliverableTraineesIndicatorId);
  }

  @Override
  public boolean existDeliverableTraineesIndicator(long deliverableTraineesIndicatorID) {

    return deliverableTraineesIndicatorDAO.existDeliverableTraineesIndicator(deliverableTraineesIndicatorID);
  }

  @Override
  public List<DeliverableTraineesIndicator> findAll() {

    return deliverableTraineesIndicatorDAO.findAll();

  }

  @Override
  public DeliverableTraineesIndicator getDeliverableTraineesIndicatorById(long deliverableTraineesIndicatorID) {

    return deliverableTraineesIndicatorDAO.find(deliverableTraineesIndicatorID);
  }

  @Override
  public DeliverableTraineesIndicator saveDeliverableTraineesIndicator(DeliverableTraineesIndicator deliverableTraineesIndicator) {

    return deliverableTraineesIndicatorDAO.save(deliverableTraineesIndicator);
  }


}
