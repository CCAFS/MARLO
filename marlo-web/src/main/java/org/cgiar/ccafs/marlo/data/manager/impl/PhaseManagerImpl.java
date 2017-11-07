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


import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class PhaseManagerImpl implements PhaseManager {


  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public PhaseManagerImpl(PhaseDAO phaseDAO) {
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deletePhase(long phaseId) {

    phaseDAO.deletePhase(phaseId);
  }

  @Override
  public boolean existPhase(long phaseID) {

    return phaseDAO.existPhase(phaseID);
  }

  @Override
  public List<Phase> findAll() {

    return phaseDAO.findAll();

  }

  @Override
  public Phase findCycle(String cylce, int year, long crpId) {

    return phaseDAO.findCycle(cylce, year, crpId);
  }

  @Override
  public Phase getPhaseById(long phaseID) {

    return phaseDAO.find(phaseID);
  }

  @Override
  public Phase savePhase(Phase phase) {

    return phaseDAO.save(phase);
  }


}
