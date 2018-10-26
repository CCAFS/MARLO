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

import org.cgiar.ccafs.marlo.data.dao.DeliverableTypeRuleDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableTypeRule;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class DeliverableTypeRuleMySQLDAO extends AbstractMarloDAO<DeliverableTypeRule, Long>
  implements DeliverableTypeRuleDAO {


  @Inject
  public DeliverableTypeRuleMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableTypeRule(long deliverableTypeRuleId) {
    DeliverableTypeRule deliverableTypeRule = this.find(deliverableTypeRuleId);
    this.update(deliverableTypeRule);
  }

  @Override
  public boolean existDeliverableTypeRule(long deliverableTypeRuleID) {
    DeliverableTypeRule deliverableTypeRule = this.find(deliverableTypeRuleID);
    if (deliverableTypeRule == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableTypeRule find(long id) {
    return super.find(DeliverableTypeRule.class, id);

  }

  @Override
  public List<DeliverableTypeRule> findAll() {
    String query = "from " + DeliverableTypeRule.class.getName() + " where is_active=1";
    List<DeliverableTypeRule> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<DeliverableTypeRule> findDeliverableTypeRuleByRule(String rule) {
    String query =
      "select distinct dtr from DeliverableTypeRule as dtr inner join dtr.deliverableRule as deliverableRule "
        + "where deliverableRule.name = :rule ";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("rule", rule);
    List<DeliverableTypeRule> deliverableTypeRules = createQuery.list();
    return deliverableTypeRules;
  }

  @Override
  public DeliverableTypeRule save(DeliverableTypeRule deliverableTypeRule) {
    if (deliverableTypeRule.getId() == null) {
      super.saveEntity(deliverableTypeRule);
    } else {
      deliverableTypeRule = super.update(deliverableTypeRule);
    }


    return deliverableTypeRule;
  }


}