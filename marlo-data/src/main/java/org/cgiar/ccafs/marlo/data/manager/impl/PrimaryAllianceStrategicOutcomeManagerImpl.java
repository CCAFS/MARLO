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


import org.cgiar.ccafs.marlo.data.dao.PrimaryAllianceStrategicOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.PrimaryAllianceStrategicOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.PrimaryAllianceStrategicOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class PrimaryAllianceStrategicOutcomeManagerImpl implements PrimaryAllianceStrategicOutcomeManager {


  private PrimaryAllianceStrategicOutcomeDAO primaryAllianceStrategicOutcomeDAO;
  // Managers


  @Inject
  public PrimaryAllianceStrategicOutcomeManagerImpl(PrimaryAllianceStrategicOutcomeDAO primaryAllianceStrategicOutcomeDAO) {
    this.primaryAllianceStrategicOutcomeDAO = primaryAllianceStrategicOutcomeDAO;


  }

  @Override
  public void deletePrimaryAllianceStrategicOutcome(long primaryAllianceStrategicOutcomeId) {

    primaryAllianceStrategicOutcomeDAO.deletePrimaryAllianceStrategicOutcome(primaryAllianceStrategicOutcomeId);
  }

  @Override
  public boolean existPrimaryAllianceStrategicOutcome(long primaryAllianceStrategicOutcomeID) {

    return primaryAllianceStrategicOutcomeDAO.existPrimaryAllianceStrategicOutcome(primaryAllianceStrategicOutcomeID);
  }

  @Override
  public List<PrimaryAllianceStrategicOutcome> findAll() {

    return primaryAllianceStrategicOutcomeDAO.findAll();

  }

  @Override
  public PrimaryAllianceStrategicOutcome getPrimaryAllianceStrategicOutcomeById(long primaryAllianceStrategicOutcomeID) {

    return primaryAllianceStrategicOutcomeDAO.find(primaryAllianceStrategicOutcomeID);
  }

  @Override
  public PrimaryAllianceStrategicOutcome savePrimaryAllianceStrategicOutcome(PrimaryAllianceStrategicOutcome primaryAllianceStrategicOutcome) {

    return primaryAllianceStrategicOutcomeDAO.save(primaryAllianceStrategicOutcome);
  }


}
