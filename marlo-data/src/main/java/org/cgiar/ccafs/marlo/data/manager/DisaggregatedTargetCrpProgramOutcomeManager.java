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
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.model.DisaggregatedTargetCrpProgramOutcome;

import java.util.List;


/**
 * @author CCAFS
 */

public interface DisaggregatedTargetCrpProgramOutcomeManager {


  /**
   * This method removes a specific disaggregatedTargetCrpProgramOutcome value from the database.
   * 
   * @param disaggregatedTargetCrpProgramOutcomeId is the disaggregatedTargetCrpProgramOutcome identifier.
   * @return true if the disaggregatedTargetCrpProgramOutcome was successfully deleted, false otherwise.
   */
  public void deleteDisaggregatedTargetCrpProgramOutcome(long disaggregatedTargetCrpProgramOutcomeId);


  /**
   * This method validate if the disaggregatedTargetCrpProgramOutcome identify with the given id exists in the system.
   * 
   * @param disaggregatedTargetCrpProgramOutcomeID is a disaggregatedTargetCrpProgramOutcome identifier.
   * @return true if the disaggregatedTargetCrpProgramOutcome exists, false otherwise.
   */
  public boolean existDisaggregatedTargetCrpProgramOutcome(long disaggregatedTargetCrpProgramOutcomeID);


  /**
   * This method gets a list of disaggregatedTargetCrpProgramOutcome that are active
   * 
   * @return a list from DisaggregatedTargetCrpProgramOutcome null if no exist records
   */
  public List<DisaggregatedTargetCrpProgramOutcome> findAll();


  /**
   * This method gets a disaggregatedTargetCrpProgramOutcome object by a given disaggregatedTargetCrpProgramOutcome
   * identifier.
   * 
   * @param disaggregatedTargetCrpProgramOutcomeID is the disaggregatedTargetCrpProgramOutcome identifier.
   * @return a DisaggregatedTargetCrpProgramOutcome object.
   */
  public DisaggregatedTargetCrpProgramOutcome
    getDisaggregatedTargetCrpProgramOutcomeById(long disaggregatedTargetCrpProgramOutcomeID);

  /**
   * This method gets a disaggregatedTargetCrpProgramOutcome object by a given disaggregatedTargetCrpProgramOutcome
   * identifier.
   * 
   * @param crpProgramOutcomeID is the crpProgramOutcome identifier.
   * @return a DisaggregatedTargetCrpProgramOutcome List.
   */
  public List<DisaggregatedTargetCrpProgramOutcome>
    getDisaggregatedTargetCrpProgramOutcomeByOutcome(long crpProgramOutcomeID);

  /**
   * This method gets a disaggregatedTargetCrpProgramOutcome object by a given disaggregatedTargetCrpProgramOutcome
   * identifier.
   * 
   * @param phaseId is the phase identifier.
   * @return a DisaggregatedTargetCrpProgramOutcome List.
   */
  public List<DisaggregatedTargetCrpProgramOutcome> getDisaggregatedTargetCrpProgramOutcomeByPhase(long phaseId);

  /**
   * This method saves the information of the given disaggregatedTargetCrpProgramOutcome
   * 
   * @param disaggregatedTargetCrpProgramOutcome - is the disaggregatedTargetCrpProgramOutcome object with the new
   *        information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         disaggregatedTargetCrpProgramOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public DisaggregatedTargetCrpProgramOutcome
    saveDisaggregatedTargetCrpProgramOutcome(DisaggregatedTargetCrpProgramOutcome disaggregatedTargetCrpProgramOutcome);


}
