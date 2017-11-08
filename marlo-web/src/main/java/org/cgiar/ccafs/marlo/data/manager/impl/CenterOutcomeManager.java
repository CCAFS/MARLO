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


import org.cgiar.ccafs.marlo.data.dao.ICenterOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterOutcomeManager implements ICenterOutcomeManager {


  private ICenterOutcomeDAO researchOutcomeDAO;

  // Managers


  @Inject
  public CenterOutcomeManager(ICenterOutcomeDAO researchOutcomeDAO) {
    this.researchOutcomeDAO = researchOutcomeDAO;


  }

  @Override
  public void deleteResearchOutcome(long researchOutcomeId) {

    researchOutcomeDAO.deleteResearchOutcome(researchOutcomeId);
  }

  @Override
  public boolean existResearchOutcome(long researchOutcomeID) {

    return researchOutcomeDAO.existResearchOutcome(researchOutcomeID);
  }

  @Override
  public List<CenterOutcome> findAll() {

    return researchOutcomeDAO.findAll();

  }

  @Override
  public List<Map<String, Object>> getCountTargetUnit(long programID) {
    return researchOutcomeDAO.getCountTargetUnit(programID);
  }

  @Override
  public List<Map<String, Object>> getImpactPathwayOutcomes(long programID) {
    return researchOutcomeDAO.getImpactPathwayOutcomes(programID);
  }

  @Override
  public List<Map<String, Object>> getMonitoringOutcomes(long programID) {
    return researchOutcomeDAO.getMonitoringOutcomes(programID);
  }

  @Override
  public CenterOutcome getResearchOutcomeById(long researchOutcomeID) {

    return researchOutcomeDAO.find(researchOutcomeID);
  }

  @Override
  public List<CenterOutcome> getResearchOutcomesByUserId(Long userId) {
    return researchOutcomeDAO.getResearchOutcomesByUserId(userId);
  }

  @Override
  public CenterOutcome saveResearchOutcome(CenterOutcome researchOutcome) {

    return researchOutcomeDAO.save(researchOutcome);
  }

  @Override
  public CenterOutcome saveResearchOutcome(CenterOutcome outcome, String actionName, List<String> relationsName) {
    return researchOutcomeDAO.save(outcome, actionName, relationsName);
  }


}
