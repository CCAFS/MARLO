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

import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;


public interface CrpProgramOutcomeDAO {

  /**
   * This method removes a specific crpProgramOutcome value from the database.
   * 
   * @param crpProgramOutcomeId is the crpProgramOutcome identifier.
   * @return true if the crpProgramOutcome was successfully deleted, false otherwise.
   */
  public void deleteCrpProgramOutcome(long crpProgramOutcomeId);

  /**
   * This method validate if the crpProgramOutcome identify with the given id exists in the system.
   * 
   * @param crpProgramOutcomeID is a crpProgramOutcome identifier.
   * @return true if the crpProgramOutcome exists, false otherwise.
   */
  public boolean existCrpProgramOutcome(long crpProgramOutcomeID);

  /**
   * This method gets a crpProgramOutcome object by a given crpProgramOutcome identifier.
   * 
   * @param crpProgramOutcomeID is the crpProgramOutcome identifier.
   * @return a CrpProgramOutcome object.
   */
  public CrpProgramOutcome find(long id);

  /**
   * This method gets a list of crpProgramOutcome that are active
   * 
   * @return a list from CrpProgramOutcome null if no exist records
   */
  public List<CrpProgramOutcome> findAll();

  /**
   * This method finds all CrpProgramOutcomes from the given phase by composeId
   * 
   * @param composedId the composed id of the original outcome
   * @param phaseId the id of the phase
   * @return a CrpProgramOutcome list.
   */
  public List<CrpProgramOutcome> getAllCrpProgramOutcomesByComposedIdFromPhase(String composedId, long phaseId);

  /**
   * This method finds all CrpProgramOutcomes from the given phase
   * 
   * @param composedId the composed id of the original outcome
   * @param phaseId the id of the phase
   * @return a CrpProgramOutcome list.
   */
  public List<CrpProgramOutcome> getAllCrpProgramOutcomesByPhase(long phaseId);

  /**
   * This method finds a CrpProgramOutcome by composeId and phase
   * 
   * @param composedId
   * @param phase
   * @return a CrpProgramOutcome object.
   */
  public CrpProgramOutcome getCrpProgramOutcome(String composedId, Phase phase);

  /**
   * This method saves the information of the given crpProgramOutcome
   * 
   * @param crpProgramOutcome - is the crpProgramOutcome object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpProgramOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public CrpProgramOutcome save(CrpProgramOutcome crpProgramOutcome);

}
