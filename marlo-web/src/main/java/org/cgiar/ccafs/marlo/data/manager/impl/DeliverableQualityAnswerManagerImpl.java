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


import org.cgiar.ccafs.marlo.data.dao.DeliverableQualityAnswerDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableQualityAnswerManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityAnswer;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class DeliverableQualityAnswerManagerImpl implements DeliverableQualityAnswerManager {


  private DeliverableQualityAnswerDAO deliverableQualityAnswerDAO;
  // Managers


  @Inject
  public DeliverableQualityAnswerManagerImpl(DeliverableQualityAnswerDAO deliverableQualityAnswerDAO) {
    this.deliverableQualityAnswerDAO = deliverableQualityAnswerDAO;


  }

  @Override
  public void deleteDeliverableQualityAnswer(long deliverableQualityAnswerId) {

    deliverableQualityAnswerDAO.deleteDeliverableQualityAnswer(deliverableQualityAnswerId);
  }

  @Override
  public boolean existDeliverableQualityAnswer(long deliverableQualityAnswerID) {

    return deliverableQualityAnswerDAO.existDeliverableQualityAnswer(deliverableQualityAnswerID);
  }

  @Override
  public List<DeliverableQualityAnswer> findAll() {

    return deliverableQualityAnswerDAO.findAll();

  }

  @Override
  public DeliverableQualityAnswer getDeliverableQualityAnswerById(long deliverableQualityAnswerID) {

    return deliverableQualityAnswerDAO.find(deliverableQualityAnswerID);
  }

  @Override
  public DeliverableQualityAnswer saveDeliverableQualityAnswer(DeliverableQualityAnswer deliverableQualityAnswer) {

    return deliverableQualityAnswerDAO.save(deliverableQualityAnswer);
  }


}
