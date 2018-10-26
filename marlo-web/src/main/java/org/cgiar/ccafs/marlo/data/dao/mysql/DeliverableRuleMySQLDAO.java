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

import org.cgiar.ccafs.marlo.data.dao.DeliverableRuleDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableRule;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableRuleMySQLDAO extends AbstractMarloDAO<DeliverableRule, Long> implements DeliverableRuleDAO {


  @Inject
  public DeliverableRuleMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableRule(long deliverableRuleId) {
    DeliverableRule deliverableRule = this.find(deliverableRuleId);
    this.update(deliverableRule);
  }

  @Override
  public boolean existDeliverableRule(long deliverableRuleID) {
    DeliverableRule deliverableRule = this.find(deliverableRuleID);
    if (deliverableRule == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableRule find(long id) {
    return super.find(DeliverableRule.class, id);

  }

  @Override
  public List<DeliverableRule> findAll() {
    String query = "from " + DeliverableRule.class.getName() + " where is_active=1";
    List<DeliverableRule> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableRule save(DeliverableRule deliverableRule) {
    if (deliverableRule.getId() == null) {
      super.saveEntity(deliverableRule);
    } else {
      deliverableRule = super.update(deliverableRule);
    }


    return deliverableRule;
  }


}