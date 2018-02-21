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


import org.cgiar.ccafs.marlo.data.dao.CenterOutputsOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.CenterOutputsOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CenterOutputsOutcome;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class CenterOutputsOutcomeManagerImpl implements CenterOutputsOutcomeManager {


  private CenterOutputsOutcomeDAO centerOutputsOutcomeDAO;
  // Managers


  @Inject
  public CenterOutputsOutcomeManagerImpl(CenterOutputsOutcomeDAO centerOutputsOutcomeDAO) {
    this.centerOutputsOutcomeDAO = centerOutputsOutcomeDAO;


  }

  @Override
  public void deleteCenterOutputsOutcome(long centerOutputsOutcomeId) {

    centerOutputsOutcomeDAO.deleteCenterOutputsOutcome(centerOutputsOutcomeId);
  }

  @Override
  public boolean existCenterOutputsOutcome(long centerOutputsOutcomeID) {

    return centerOutputsOutcomeDAO.existCenterOutputsOutcome(centerOutputsOutcomeID);
  }

  @Override
  public List<CenterOutputsOutcome> findAll() {

    return centerOutputsOutcomeDAO.findAll();

  }

  @Override
  public CenterOutputsOutcome getCenterOutputsOutcomeById(long centerOutputsOutcomeID) {

    return centerOutputsOutcomeDAO.find(centerOutputsOutcomeID);
  }

  @Override
  public CenterOutputsOutcome saveCenterOutputsOutcome(CenterOutputsOutcome centerOutputsOutcome) {

    return centerOutputsOutcomeDAO.save(centerOutputsOutcome);
  }


}
