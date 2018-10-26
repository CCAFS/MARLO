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


import org.cgiar.ccafs.marlo.data.dao.DeliverableRuleDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableRuleManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableRule;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverableRuleManagerImpl implements DeliverableRuleManager {


  private DeliverableRuleDAO deliverableRuleDAO;
  // Managers


  @Inject
  public DeliverableRuleManagerImpl(DeliverableRuleDAO deliverableRuleDAO) {
    this.deliverableRuleDAO = deliverableRuleDAO;


  }

  @Override
  public void deleteDeliverableRule(long deliverableRuleId) {

    deliverableRuleDAO.deleteDeliverableRule(deliverableRuleId);
  }

  @Override
  public boolean existDeliverableRule(long deliverableRuleID) {

    return deliverableRuleDAO.existDeliverableRule(deliverableRuleID);
  }

  @Override
  public List<DeliverableRule> findAll() {

    return deliverableRuleDAO.findAll();

  }

  @Override
  public DeliverableRule getDeliverableRuleById(long deliverableRuleID) {

    return deliverableRuleDAO.find(deliverableRuleID);
  }

  @Override
  public DeliverableRule saveDeliverableRule(DeliverableRule deliverableRule) {

    return deliverableRuleDAO.save(deliverableRule);
  }


}
