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
package org.cgiar.ccafs.marlo.data.service;

import org.cgiar.ccafs.marlo.data.model.CenterMonitoringOutcomeEvidence;
import org.cgiar.ccafs.marlo.data.service.impl.CenterMonitoringOutcomeEvidenceService;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CenterMonitoringOutcomeEvidenceService.class)
public interface ICenterMonitoringOutcomeEvidenceService {


  /**
   * This method removes a specific monitorignOutcomeEvidence value from the database.
   * 
   * @param monitorignOutcomeEvidenceId is the monitorignOutcomeEvidence identifier.
   * @return true if the monitorignOutcomeEvidence was successfully deleted, false otherwise.
   */
  public boolean deleteMonitorignOutcomeEvidence(long monitorignOutcomeEvidenceId);


  /**
   * This method validate if the monitorignOutcomeEvidence identify with the given id exists in the system.
   * 
   * @param monitorignOutcomeEvidenceID is a monitorignOutcomeEvidence identifier.
   * @return true if the monitorignOutcomeEvidence exists, false otherwise.
   */
  public boolean existMonitorignOutcomeEvidence(long monitorignOutcomeEvidenceID);


  /**
   * This method gets a list of monitorignOutcomeEvidence that are active
   * 
   * @return a list from CenterMonitoringOutcomeEvidence null if no exist records
   */
  public List<CenterMonitoringOutcomeEvidence> findAll();


  /**
   * This method gets a monitorignOutcomeEvidence object by a given monitorignOutcomeEvidence identifier.
   * 
   * @param monitorignOutcomeEvidenceID is the monitorignOutcomeEvidence identifier.
   * @return a CenterMonitoringOutcomeEvidence object.
   */
  public CenterMonitoringOutcomeEvidence getMonitorignOutcomeEvidenceById(long monitorignOutcomeEvidenceID);

  /**
   * This method gets a list of monitorignOutcomeEvidences belongs of the user
   * 
   * @param userId - the user id
   * @return List of MonitorignOutcomeEvidences or null if the user is invalid or not have roles.
   */
  public List<CenterMonitoringOutcomeEvidence> getMonitorignOutcomeEvidencesByUserId(Long userId);

  /**
   * This method saves the information of the given monitorignOutcomeEvidence
   * 
   * @param monitorignOutcomeEvidence - is the monitorignOutcomeEvidence object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the monitorignOutcomeEvidence was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveMonitorignOutcomeEvidence(CenterMonitoringOutcomeEvidence monitorignOutcomeEvidence);


}
