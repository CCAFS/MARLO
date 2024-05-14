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


import org.cgiar.ccafs.marlo.data.dao.DisaggregatedTargetsBusinessRuleDAO;
import org.cgiar.ccafs.marlo.data.manager.DisaggregatedTargetsBusinessRuleManager;
import org.cgiar.ccafs.marlo.data.model.DisaggregatedTargetsBusinessRule;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DisaggregatedTargetsBusinessRuleManagerImpl implements DisaggregatedTargetsBusinessRuleManager {


  private DisaggregatedTargetsBusinessRuleDAO disaggregatedTargetsBusinessRuleDAO;
  // Managers


  @Inject
  public DisaggregatedTargetsBusinessRuleManagerImpl(DisaggregatedTargetsBusinessRuleDAO disaggregatedTargetsBusinessRuleDAO) {
    this.disaggregatedTargetsBusinessRuleDAO = disaggregatedTargetsBusinessRuleDAO;


  }

  @Override
  public void deleteDisaggregatedTargetsBusinessRule(long disaggregatedTargetsBusinessRuleId) {

    disaggregatedTargetsBusinessRuleDAO.deleteDisaggregatedTargetsBusinessRule(disaggregatedTargetsBusinessRuleId);
  }

  @Override
  public boolean existDisaggregatedTargetsBusinessRule(long disaggregatedTargetsBusinessRuleID) {

    return disaggregatedTargetsBusinessRuleDAO.existDisaggregatedTargetsBusinessRule(disaggregatedTargetsBusinessRuleID);
  }

  @Override
  public List<DisaggregatedTargetsBusinessRule> findAll() {

    return disaggregatedTargetsBusinessRuleDAO.findAll();

  }

  @Override
  public DisaggregatedTargetsBusinessRule getDisaggregatedTargetsBusinessRuleById(long disaggregatedTargetsBusinessRuleID) {

    return disaggregatedTargetsBusinessRuleDAO.find(disaggregatedTargetsBusinessRuleID);
  }

  @Override
  public DisaggregatedTargetsBusinessRule saveDisaggregatedTargetsBusinessRule(DisaggregatedTargetsBusinessRule disaggregatedTargetsBusinessRule) {

    return disaggregatedTargetsBusinessRuleDAO.save(disaggregatedTargetsBusinessRule);
  }


}
