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

import org.cgiar.ccafs.marlo.data.dao.DisaggregatedTargetsBusinessRuleDAO;
import org.cgiar.ccafs.marlo.data.model.DisaggregatedTargetsBusinessRule;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DisaggregatedTargetsBusinessRuleMySQLDAO extends AbstractMarloDAO<DisaggregatedTargetsBusinessRule, Long>
  implements DisaggregatedTargetsBusinessRuleDAO {


  @Inject
  public DisaggregatedTargetsBusinessRuleMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDisaggregatedTargetsBusinessRule(long disaggregatedTargetsBusinessRuleId) {
    DisaggregatedTargetsBusinessRule disaggregatedTargetsBusinessRule = this.find(disaggregatedTargetsBusinessRuleId);
    disaggregatedTargetsBusinessRule.setActive(false);
    this.update(disaggregatedTargetsBusinessRule);
  }

  @Override
  public boolean existDisaggregatedTargetsBusinessRule(long disaggregatedTargetsBusinessRuleID) {
    DisaggregatedTargetsBusinessRule disaggregatedTargetsBusinessRule = this.find(disaggregatedTargetsBusinessRuleID);
    if (disaggregatedTargetsBusinessRule == null) {
      return false;
    }
    return true;

  }

  @Override
  public DisaggregatedTargetsBusinessRule find(long id) {
    return super.find(DisaggregatedTargetsBusinessRule.class, id);

  }

  @Override
  public List<DisaggregatedTargetsBusinessRule> findAll() {
    String query = "from " + DisaggregatedTargetsBusinessRule.class.getName() + " where is_active=1";
    List<DisaggregatedTargetsBusinessRule> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DisaggregatedTargetsBusinessRule save(DisaggregatedTargetsBusinessRule disaggregatedTargetsBusinessRule) {
    if (disaggregatedTargetsBusinessRule.getId() == null) {
      super.saveEntity(disaggregatedTargetsBusinessRule);
    } else {
      disaggregatedTargetsBusinessRule = super.update(disaggregatedTargetsBusinessRule);
    }


    return disaggregatedTargetsBusinessRule;
  }


}