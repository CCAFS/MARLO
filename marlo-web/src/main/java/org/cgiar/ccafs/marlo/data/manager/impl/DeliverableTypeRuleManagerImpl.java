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


import org.cgiar.ccafs.marlo.data.dao.DeliverableTypeRuleDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeRuleManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableTypeRule;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverableTypeRuleManagerImpl implements DeliverableTypeRuleManager {


  private DeliverableTypeRuleDAO deliverableTypeRuleDAO;
  // Managers


  @Inject
  public DeliverableTypeRuleManagerImpl(DeliverableTypeRuleDAO deliverableTypeRuleDAO) {
    this.deliverableTypeRuleDAO = deliverableTypeRuleDAO;


  }

  @Override
  public void deleteDeliverableTypeRule(long deliverableTypeRuleId) {

    deliverableTypeRuleDAO.deleteDeliverableTypeRule(deliverableTypeRuleId);
  }

  @Override
  public boolean existDeliverableTypeRule(long deliverableTypeRuleID) {

    return deliverableTypeRuleDAO.existDeliverableTypeRule(deliverableTypeRuleID);
  }

  @Override
  public List<DeliverableTypeRule> findAll() {

    return deliverableTypeRuleDAO.findAll();

  }

  @Override
  public List<DeliverableTypeRule> findDeliverableTypeRuleByRule(String rule) {
    return deliverableTypeRuleDAO.findDeliverableTypeRuleByRule(rule);
  }

  @Override
  public DeliverableTypeRule getDeliverableTypeRuleById(long deliverableTypeRuleID) {

    return deliverableTypeRuleDAO.find(deliverableTypeRuleID);
  }

  @Override
  public DeliverableTypeRule saveDeliverableTypeRule(DeliverableTypeRule deliverableTypeRule) {

    return deliverableTypeRuleDAO.save(deliverableTypeRule);
  }


}
