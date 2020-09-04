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


import org.cgiar.ccafs.marlo.data.dao.CrpOutcomeSubIdoDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpOutcomeSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class CrpOutcomeSubIdoManagerImpl implements CrpOutcomeSubIdoManager {

  // DAO
  private CrpOutcomeSubIdoDAO crpOutcomeSubIdoDAO;

  // Managers
  private CrpProgramOutcomeManager crpProgramOutcomeManager;

  @Inject
  public CrpOutcomeSubIdoManagerImpl(CrpOutcomeSubIdoDAO crpOutcomeSubIdoDAO,
    CrpProgramOutcomeManager crpProgramOutcomeManager) {
    this.crpOutcomeSubIdoDAO = crpOutcomeSubIdoDAO;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
  }

  @Override
  public void deleteCrpOutcomeSubIdo(long crpOutcomeSubIdoId) {
    crpOutcomeSubIdoDAO.deleteCrpOutcomeSubIdo(crpOutcomeSubIdoId);
  }

  @Override
  public boolean existCrpOutcomeSubIdo(long crpOutcomeSubIdoID) {
    return crpOutcomeSubIdoDAO.existCrpOutcomeSubIdo(crpOutcomeSubIdoID);
  }

  @Override
  public List<CrpOutcomeSubIdo> findAll() {
    return crpOutcomeSubIdoDAO.findAll();
  }

  @Override
  public CrpOutcomeSubIdo getCrpOutcomeSubIdoById(long crpOutcomeSubIdoID) {
    return crpOutcomeSubIdoDAO.find(crpOutcomeSubIdoID);
  }

  @Override
  public CrpOutcomeSubIdo getCrpOutcomeSubIdoByOutcomeComposedIdAndPhase(String outcomeComposedId, long phaseId) {
    return crpOutcomeSubIdoDAO.getCrpOutcomeSubIdoByOutcomeComposedIdAndPhase(outcomeComposedId, phaseId);
  }

  @Override
  public CrpOutcomeSubIdo getCrpOutcomeSubIdoByOutcomeComposedIdPhaseAndSubIdo(String outcomeComposedId, long phaseId,
    long subIdoId) {
    return crpOutcomeSubIdoDAO.getCrpOutcomeSubIdoByOutcomeComposedIdPhaseAndSubIdo(outcomeComposedId, phaseId,
      subIdoId);
  }

  @Override
  public void replicate(CrpOutcomeSubIdo originalCrpOutcomeSubIdo, Phase initialPhase) {
    Phase current = initialPhase;
    String outcomeComposedId = originalCrpOutcomeSubIdo.getCrpProgramOutcome().getComposeID();
    SrfSubIdo subIdo = originalCrpOutcomeSubIdo.getSrfSubIdo();

    while (current != null) {
      CrpOutcomeSubIdo outcomeSubIdo = subIdo != null
        ? this.getCrpOutcomeSubIdoByOutcomeComposedIdPhaseAndSubIdo(outcomeComposedId, current.getId(), subIdo.getId())
        : this.getCrpOutcomeSubIdoByOutcomeComposedIdAndPhase(outcomeComposedId, current.getId());
      CrpProgramOutcome crpProgramOutcome = crpProgramOutcomeManager.getCrpProgramOutcome(outcomeComposedId, current);
      if (outcomeSubIdo == null) {
        outcomeSubIdo = new CrpOutcomeSubIdo();
      }

      outcomeSubIdo.copyFields(originalCrpOutcomeSubIdo);
      outcomeSubIdo.setCrpProgramOutcome(crpProgramOutcome);

      this.saveCrpOutcomeSubIdo(outcomeSubIdo);

      current = current.getNext();
    }
  }

  @Override
  public CrpOutcomeSubIdo saveCrpOutcomeSubIdo(CrpOutcomeSubIdo crpOutcomeSubIdo) {
    return crpOutcomeSubIdoDAO.save(crpOutcomeSubIdo);
  }
}