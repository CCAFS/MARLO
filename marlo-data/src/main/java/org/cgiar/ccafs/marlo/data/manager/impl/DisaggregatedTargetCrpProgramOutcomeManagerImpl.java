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


import org.cgiar.ccafs.marlo.data.dao.DisaggregatedTargetCrpProgramOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.DisaggregatedTargetCrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.DisaggregatedTargetCrpProgramOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DisaggregatedTargetCrpProgramOutcomeManagerImpl implements DisaggregatedTargetCrpProgramOutcomeManager {


  private DisaggregatedTargetCrpProgramOutcomeDAO disaggregatedTargetCrpProgramOutcomeDAO;
  // Managers


  @Inject
  public DisaggregatedTargetCrpProgramOutcomeManagerImpl(DisaggregatedTargetCrpProgramOutcomeDAO disaggregatedTargetCrpProgramOutcomeDAO) {
    this.disaggregatedTargetCrpProgramOutcomeDAO = disaggregatedTargetCrpProgramOutcomeDAO;


  }

  @Override
  public void deleteDisaggregatedTargetCrpProgramOutcome(long disaggregatedTargetCrpProgramOutcomeId) {

    disaggregatedTargetCrpProgramOutcomeDAO.deleteDisaggregatedTargetCrpProgramOutcome(disaggregatedTargetCrpProgramOutcomeId);
  }

  @Override
  public boolean existDisaggregatedTargetCrpProgramOutcome(long disaggregatedTargetCrpProgramOutcomeID) {

    return disaggregatedTargetCrpProgramOutcomeDAO.existDisaggregatedTargetCrpProgramOutcome(disaggregatedTargetCrpProgramOutcomeID);
  }

  @Override
  public List<DisaggregatedTargetCrpProgramOutcome> findAll() {

    return disaggregatedTargetCrpProgramOutcomeDAO.findAll();

  }

  @Override
  public DisaggregatedTargetCrpProgramOutcome getDisaggregatedTargetCrpProgramOutcomeById(long disaggregatedTargetCrpProgramOutcomeID) {

    return disaggregatedTargetCrpProgramOutcomeDAO.find(disaggregatedTargetCrpProgramOutcomeID);
  }

  @Override
  public DisaggregatedTargetCrpProgramOutcome saveDisaggregatedTargetCrpProgramOutcome(DisaggregatedTargetCrpProgramOutcome disaggregatedTargetCrpProgramOutcome) {

    return disaggregatedTargetCrpProgramOutcomeDAO.save(disaggregatedTargetCrpProgramOutcome);
  }


}
