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


import org.cgiar.ccafs.marlo.data.dao.AllianceLeverOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.AllianceLeverOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.AllianceLeverOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class AllianceLeverOutcomeManagerImpl implements AllianceLeverOutcomeManager {


  private AllianceLeverOutcomeDAO allianceLeverOutcomeDAO;
  // Managers


  @Inject
  public AllianceLeverOutcomeManagerImpl(AllianceLeverOutcomeDAO allianceLeverOutcomeDAO) {
    this.allianceLeverOutcomeDAO = allianceLeverOutcomeDAO;


  }

  @Override
  public void deleteAllianceLeverOutcome(long allianceLeverOutcomeId) {

    allianceLeverOutcomeDAO.deleteAllianceLeverOutcome(allianceLeverOutcomeId);
  }

  @Override
  public boolean existAllianceLeverOutcome(long allianceLeverOutcomeID) {

    return allianceLeverOutcomeDAO.existAllianceLeverOutcome(allianceLeverOutcomeID);
  }

  @Override
  public List<AllianceLeverOutcome> findAll() {

    return allianceLeverOutcomeDAO.findAll();

  }

  @Override
  public List<AllianceLeverOutcome> findAllianceLeverOutcomeByExpectedPhaseAndLever(long phase, long expectedId,
    long lever) {

    return allianceLeverOutcomeDAO.findAllianceLeverOutcomeByExpectedPhaseAndLever(phase, expectedId, lever);

  }

  @Override
  public AllianceLeverOutcome getAllianceLeverOutcomeById(long allianceLeverOutcomeID) {

    return allianceLeverOutcomeDAO.find(allianceLeverOutcomeID);
  }

  @Override
  public AllianceLeverOutcome saveAllianceLeverOutcome(AllianceLeverOutcome allianceLeverOutcome) {

    return allianceLeverOutcomeDAO.save(allianceLeverOutcome);
  }


}
