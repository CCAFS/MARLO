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


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.PrimaryAllianceStrategicOutcome;

import java.util.List;


public interface PrimaryAllianceStrategicOutcomeDAO {

  /**
   * This method removes a specific primaryAllianceStrategicOutcome value from the database.
   * 
   * @param primaryAllianceStrategicOutcomeId is the primaryAllianceStrategicOutcome identifier.
   * @return true if the primaryAllianceStrategicOutcome was successfully deleted, false otherwise.
   */
  public void deletePrimaryAllianceStrategicOutcome(long primaryAllianceStrategicOutcomeId);

  /**
   * This method validate if the primaryAllianceStrategicOutcome identify with the given id exists in the system.
   * 
   * @param primaryAllianceStrategicOutcomeID is a primaryAllianceStrategicOutcome identifier.
   * @return true if the primaryAllianceStrategicOutcome exists, false otherwise.
   */
  public boolean existPrimaryAllianceStrategicOutcome(long primaryAllianceStrategicOutcomeID);

  /**
   * This method gets a primaryAllianceStrategicOutcome object by a given primaryAllianceStrategicOutcome identifier.
   * 
   * @param primaryAllianceStrategicOutcomeID is the primaryAllianceStrategicOutcome identifier.
   * @return a PrimaryAllianceStrategicOutcome object.
   */
  public PrimaryAllianceStrategicOutcome find(long id);

  /**
   * This method gets a list of primaryAllianceStrategicOutcome that are active
   * 
   * @return a list from PrimaryAllianceStrategicOutcome null if no exist records
   */
  public List<PrimaryAllianceStrategicOutcome> findAll();


  List<PrimaryAllianceStrategicOutcome> getPrimaryAllianceStrategicOutcomeByStudyAndPhase(long projectExpectedStudyId,
    long phaseId);

  /**
   * This method saves the information of the given primaryAllianceStrategicOutcome
   * 
   * @param primaryAllianceStrategicOutcome - is the primaryAllianceStrategicOutcome object with the new information to
   *        be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         primaryAllianceStrategicOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public PrimaryAllianceStrategicOutcome save(PrimaryAllianceStrategicOutcome primaryAllianceStrategicOutcome);
}
