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


import org.cgiar.ccafs.marlo.data.dao.CrpProgramOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpProgramOutcomeManagerImpl implements CrpProgramOutcomeManager {


  private CrpProgramOutcomeDAO crpProgramOutcomeDAO;
  // Managers


  @Inject
  public CrpProgramOutcomeManagerImpl(CrpProgramOutcomeDAO crpProgramOutcomeDAO) {
    this.crpProgramOutcomeDAO = crpProgramOutcomeDAO;


  }

  @Override
  public void deleteCrpProgramOutcome(long crpProgramOutcomeId) {

    crpProgramOutcomeDAO.deleteCrpProgramOutcome(crpProgramOutcomeId);
  }

  @Override
  public boolean existCrpProgramOutcome(long crpProgramOutcomeID) {

    return crpProgramOutcomeDAO.existCrpProgramOutcome(crpProgramOutcomeID);
  }

  @Override
  public List<CrpProgramOutcome> findAll() {

    return crpProgramOutcomeDAO.findAll();

  }

  @Override
  public CrpProgramOutcome getCrpProgramOutcomeById(long crpProgramOutcomeID) {

    return crpProgramOutcomeDAO.find(crpProgramOutcomeID);
  }

  @Override
  public CrpProgramOutcome saveCrpProgramOutcome(CrpProgramOutcome crpProgramOutcome) {

    return crpProgramOutcomeDAO.save(crpProgramOutcome);
  }


}
