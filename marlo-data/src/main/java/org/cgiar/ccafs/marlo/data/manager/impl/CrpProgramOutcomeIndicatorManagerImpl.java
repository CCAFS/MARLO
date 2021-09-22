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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;


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

  @Override
  public List<CrpProgramOutcomeIndicator> getCrpProgramOutcomeIndicatorByOutcome(CrpProgramOutcome crpProgramOutcome) {
    return crpProgramOutcomeIndicatorDAO.getCrpProgramOutcomeIndicatorByOutcome(crpProgramOutcome);
  }

  @Override
  public List<CrpProgramOutcomeIndicator> getCrpProgramOutcomeIndicatorByOutcomeAndIndicator(String indicator,
    CrpProgramOutcome crpProgramOutcome) {
    return crpProgramOutcomeIndicatorDAO.getCrpProgramOutcomeIndicatorByOutcomeAndIndicator(indicator,
      crpProgramOutcome);

  }


  public CrpProgramOutcomeIndicator getCrpProgramOutcomeIndicatorByPhase(String composedID, long phaseID) {
    return crpProgramOutcomeIndicatorDAO.getCrpProgramOutcomeIndicatorByPhase(composedID, phaseID);
  }

  @Override
  public void remove(CrpProgramOutcomeIndicator originalCrpOutcomeIndicator, Phase initialPhase) {
    if (originalCrpOutcomeIndicator != null && originalCrpOutcomeIndicator.getCrpProgramOutcome() != null
      && originalCrpOutcomeIndicator.getCrpProgramOutcome().getComposeID() != null) {

      List<CrpProgramOutcome> outcomes = crpProgramOutcomeManager.getAllCrpProgramOutcomesByComposedIdFromPhase(
        originalCrpOutcomeIndicator.getCrpProgramOutcome().getComposeID(), initialPhase.getId());

      if (outcomes != null) {
        for (CrpProgramOutcome crpProgramOutcome : outcomes) {

          if (this.getCrpProgramOutcomeIndicatorByOutcomeAndIndicator(originalCrpOutcomeIndicator.getIndicator(),
            crpProgramOutcome) != null
            || (!this.getCrpProgramOutcomeIndicatorByOutcomeAndIndicator(originalCrpOutcomeIndicator.getIndicator(),
              crpProgramOutcome).isEmpty())) {
            List<CrpProgramOutcomeIndicator> indicators =
              new ArrayList<>(this.getCrpProgramOutcomeIndicatorByOutcomeAndIndicator(
                originalCrpOutcomeIndicator.getIndicator(), crpProgramOutcome));

            for (CrpProgramOutcomeIndicator indicator : indicators) {
              if (indicator != null && indicator.getId() != null) {
                this.deleteCrpProgramOutcomeIndicator(indicator.getId());
              }
            }
          }
        }
      }
    }
  }

  @Override
  public void replicate(CrpProgramOutcomeIndicator originalCrpOutcomeIndicator, Phase initialPhase) {
    if (originalCrpOutcomeIndicator.getCrpProgramOutcome() != null
      && originalCrpOutcomeIndicator.getCrpProgramOutcome().getComposeID() != null) {

      List<CrpProgramOutcome> outcomes = crpProgramOutcomeManager.getAllCrpProgramOutcomesByComposedIdFromPhase(
        originalCrpOutcomeIndicator.getCrpProgramOutcome().getComposeID(), initialPhase.getId());

      for (CrpProgramOutcome crpProgramOutcome : outcomes) {
        CrpProgramOutcomeIndicator outcomeIndicator = new CrpProgramOutcomeIndicator();
        if (originalCrpOutcomeIndicator.getId() != null) {
          if (this.getCrpProgramOutcomeIndicatorById(originalCrpOutcomeIndicator.getId()) != null) {
            originalCrpOutcomeIndicator = this.getCrpProgramOutcomeIndicatorById(originalCrpOutcomeIndicator.getId());
          } else {
            outcomeIndicator = new CrpProgramOutcomeIndicator();
          }
        }

        if (this.getCrpProgramOutcomeIndicatorByOutcomeAndIndicator(originalCrpOutcomeIndicator.getIndicator(),
          crpProgramOutcome) == null
          || (this.getCrpProgramOutcomeIndicatorByOutcomeAndIndicator(originalCrpOutcomeIndicator.getIndicator(),
            crpProgramOutcome).isEmpty())) {
          outcomeIndicator.copyFields(originalCrpOutcomeIndicator);
          outcomeIndicator.setCrpProgramOutcome(crpProgramOutcome);
          if (crpProgramOutcome.getId() != null
            && crpProgramOutcomeManager.getCrpProgramOutcomeById(crpProgramOutcome.getId()) != null) {
            crpProgramOutcome = crpProgramOutcomeManager.getCrpProgramOutcomeById(crpProgramOutcome.getId());
          }
          outcomeIndicator.setPhase(crpProgramOutcome.getPhase());
          this.saveCrpProgramOutcomeIndicator(outcomeIndicator);
        }
      }
    }
  }

  @Override
  public CrpProgramOutcomeIndicator
    saveCrpProgramOutcomeIndicator(CrpProgramOutcomeIndicator crpProgramOutcomeIndicator) {

    return crpProgramOutcomeIndicatorDAO.save(crpProgramOutcomeIndicator);
  }

}
