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


import org.cgiar.ccafs.marlo.data.dao.CrpMilestoneDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Garcia
 */
@Named
public class CrpMilestoneManagerImpl implements CrpMilestoneManager {

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(CrpMilestoneManagerImpl.class);

  // DAO
  private CrpMilestoneDAO crpMilestoneDAO;

  // Managers
  private CrpProgramOutcomeManager crpProgramOutcomeManager;

  @Inject
  public CrpMilestoneManagerImpl(CrpMilestoneDAO crpMilestoneDAO, CrpProgramOutcomeManager crpProgramOutcomeManager) {
    this.crpMilestoneDAO = crpMilestoneDAO;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
  }

  @Override
  public void deleteCrpMilestone(long crpAssumptionId) {
    crpMilestoneDAO.deleteCrpMilestone(crpAssumptionId);
  }

  @Override
  public boolean existCrpMilestone(long crpAssumptionID) {
    return crpMilestoneDAO.existCrpMilestone(crpAssumptionID);
  }

  @Override
  public List<CrpMilestone> findAll() {
    return crpMilestoneDAO.findAll();
  }

  @Override
  public CrpMilestone getCrpMilestoneById(long crpAssumptionID) {
    return crpMilestoneDAO.find(crpAssumptionID);
  }

  @Override
  public CrpMilestone getCrpMilestoneByPhase(String composedID, long phaseID) {
    return crpMilestoneDAO.getCrpMilestoneByPhase(composedID, phaseID);
  }

  @Override
  public void replicate(CrpMilestone originalCrpMilestone, Phase initialPhase) {
    String milestoneComposedId = originalCrpMilestone.getComposeID();
    String outcomeComposedId = StringUtils.substringBeforeLast(milestoneComposedId, "-");

    List<CrpProgramOutcome> outcomes =
      crpProgramOutcomeManager.getAllCrpProgramOutcomesByComposedIdFromPhase(outcomeComposedId, initialPhase.getId());

    for (CrpProgramOutcome crpProgramOutcome : outcomes) {
      CrpMilestone milestone = this.getCrpMilestoneByPhase(milestoneComposedId, crpProgramOutcome.getPhase().getId());
      if (milestone == null) {
        milestone = new CrpMilestone();
      }

      milestone.copyFields(originalCrpMilestone);
      milestone.setCrpProgramOutcome(crpProgramOutcome);
      this.saveCrpMilestone(milestone);
    }
  }

  @Override
  public CrpMilestone saveCrpMilestone(CrpMilestone crpAssumption) {
    return crpMilestoneDAO.save(crpAssumption);
  }
}