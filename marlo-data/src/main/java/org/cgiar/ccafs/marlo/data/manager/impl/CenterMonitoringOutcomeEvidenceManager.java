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


import org.cgiar.ccafs.marlo.data.dao.ICenterMonitoringtOutcomeEvidenceDAO;
import org.cgiar.ccafs.marlo.data.model.CenterMonitoringOutcomeEvidence;
import org.cgiar.ccafs.marlo.data.service.ICenterMonitoringOutcomeEvidenceManager;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterMonitoringOutcomeEvidenceManager implements ICenterMonitoringOutcomeEvidenceManager {


  private ICenterMonitoringtOutcomeEvidenceDAO monitorignOutcomeEvidenceDAO;

  // Managers


  @Inject
  public CenterMonitoringOutcomeEvidenceManager(ICenterMonitoringtOutcomeEvidenceDAO monitorignOutcomeEvidenceDAO) {
    this.monitorignOutcomeEvidenceDAO = monitorignOutcomeEvidenceDAO;


  }

  @Override
  public boolean deleteMonitorignOutcomeEvidence(long monitorignOutcomeEvidenceId) {

    return monitorignOutcomeEvidenceDAO.deleteMonitorignOutcomeEvidence(monitorignOutcomeEvidenceId);
  }

  @Override
  public boolean existMonitorignOutcomeEvidence(long monitorignOutcomeEvidenceID) {

    return monitorignOutcomeEvidenceDAO.existMonitorignOutcomeEvidence(monitorignOutcomeEvidenceID);
  }

  @Override
  public List<CenterMonitoringOutcomeEvidence> findAll() {

    return monitorignOutcomeEvidenceDAO.findAll();

  }

  @Override
  public CenterMonitoringOutcomeEvidence getMonitorignOutcomeEvidenceById(long monitorignOutcomeEvidenceID) {

    return monitorignOutcomeEvidenceDAO.find(monitorignOutcomeEvidenceID);
  }

  @Override
  public List<CenterMonitoringOutcomeEvidence> getMonitorignOutcomeEvidencesByUserId(Long userId) {
    return monitorignOutcomeEvidenceDAO.getMonitorignOutcomeEvidencesByUserId(userId);
  }

  @Override
  public long saveMonitorignOutcomeEvidence(CenterMonitoringOutcomeEvidence monitorignOutcomeEvidence) {

    return monitorignOutcomeEvidenceDAO.save(monitorignOutcomeEvidence);
  }


}
