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


import org.cgiar.ccafs.marlo.data.dao.CrpProgramOutcomeIndicatorDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcomeIndicator;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Christian Garcia
 */
@Named
public class CrpProgramOutcomeIndicatorManagerImpl implements CrpProgramOutcomeIndicatorManager {


  private CrpProgramOutcomeIndicatorDAO crpProgramOutcomeIndicatorDAO;
  // Managers
  private CrpProgramOutcomeManager crpProgramOutcomeManager;

  @Inject
  public CrpProgramOutcomeIndicatorManagerImpl(CrpProgramOutcomeIndicatorDAO crpProgramOutcomeIndicatorDAO,
    CrpProgramOutcomeManager crpProgramOutcomeManager) {
    this.crpProgramOutcomeIndicatorDAO = crpProgramOutcomeIndicatorDAO;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;


  }

  @Override
  public void deleteCrpProgramOutcomeIndicator(long crpProgramOutcomeIndicatorId) {

    crpProgramOutcomeIndicatorDAO.deleteCrpProgramOutcomeIndicator(crpProgramOutcomeIndicatorId);
  }

  @Override
  public boolean existCrpProgramOutcomeIndicator(long crpProgramOutcomeIndicatorID) {

    return crpProgramOutcomeIndicatorDAO.existCrpProgramOutcomeIndicator(crpProgramOutcomeIndicatorID);
  }

  @Override
  public List<CrpProgramOutcomeIndicator> findAll() {

    return crpProgramOutcomeIndicatorDAO.findAll();

  }

  @Override
  public CrpProgramOutcomeIndicator getCrpProgramOutcomeIndicatorById(long crpProgramOutcomeIndicatorID) {

    return crpProgramOutcomeIndicatorDAO.find(crpProgramOutcomeIndicatorID);
  }


  public CrpProgramOutcomeIndicator getCrpProgramOutcomeIndicatorByPhase(String composedID, long phaseID) {
    return crpProgramOutcomeIndicatorDAO.getCrpProgramOutcomeIndicatorByPhase(composedID, phaseID);
  }

  @Override
  public void replicate(CrpProgramOutcomeIndicator originalCrpOutcomeIndicator, Phase initialPhase) {
    String outcomeIndicatorComposedId = originalCrpOutcomeIndicator.getComposeID();
    String outcomeComposedId = StringUtils.substringBeforeLast(outcomeIndicatorComposedId, "-");

    List<CrpProgramOutcome> outcomes =
      crpProgramOutcomeManager.getAllCrpProgramOutcomesByComposedIdFromPhase(outcomeComposedId, initialPhase.getId());

    for (CrpProgramOutcome crpProgramOutcome : outcomes) {
      CrpProgramOutcomeIndicator outcomeIndicator =
        this.getCrpProgramOutcomeIndicatorByPhase(outcomeIndicatorComposedId, crpProgramOutcome.getPhase().getId());
      if (outcomeIndicator == null) {
        outcomeIndicator = new CrpProgramOutcomeIndicator();
      }

      outcomeIndicator.copyFields(originalCrpOutcomeIndicator);
      outcomeIndicator.setCrpProgramOutcome(crpProgramOutcome);
      this.saveCrpProgramOutcomeIndicator(outcomeIndicator);
    }
  }

  @Override
  public CrpProgramOutcomeIndicator
    saveCrpProgramOutcomeIndicator(CrpProgramOutcomeIndicator crpProgramOutcomeIndicator) {

    return crpProgramOutcomeIndicatorDAO.save(crpProgramOutcomeIndicator);
  }

}
