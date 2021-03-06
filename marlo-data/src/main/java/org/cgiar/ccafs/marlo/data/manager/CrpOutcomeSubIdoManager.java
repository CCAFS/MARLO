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

import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface CrpOutcomeSubIdoManager {


  /**
   * This method removes a specific crpOutcomeSubIdo value from the database.
   * 
   * @param crpOutcomeSubIdoId is the crpOutcomeSubIdo identifier.
   * @return true if the crpOutcomeSubIdo was successfully deleted, false otherwise.
   */
  public void deleteCrpOutcomeSubIdo(long crpOutcomeSubIdoId);

  /**
   * This method validate if the crpOutcomeSubIdo identify with the given id exists in the system.
   * 
   * @param crpOutcomeSubIdoID is a crpOutcomeSubIdo identifier.
   * @return true if the crpOutcomeSubIdo exists, false otherwise.
   */
  public boolean existCrpOutcomeSubIdo(long crpOutcomeSubIdoID);

  /**
   * This method gets a list of crpOutcomeSubIdo that are active
   * 
   * @return a list from CrpOutcomeSubIdo null if no exist records
   */
  public List<CrpOutcomeSubIdo> findAll();


  /**
   * This method gets a crpOutcomeSubIdo object by a given crpOutcomeSubIdo identifier.
   * 
   * @param crpOutcomeSubIdoID is the crpOutcomeSubIdo identifier.
   * @return a CrpOutcomeSubIdo object.
   */
  public CrpOutcomeSubIdo getCrpOutcomeSubIdoById(long crpOutcomeSubIdoID);

  /**
   * This method finds a CrpOutcomeSubIdo in the given phase by an outcome composeId
   * 
   * @param composedId the composed id of the original outcome
   * @param phaseId the id of the phase
   * @return a CrpOutcomeSubIdo, null if not found.
   */
  public CrpOutcomeSubIdo getCrpOutcomeSubIdoByOutcomeComposedIdAndPhase(String outcomeComposedId, long phaseId);

  /**
   * This method finds a CrpOutcomeSubIdo in the given phase by an outcome composeId and a Srf SubIdo ID
   * 
   * @param composedId the composed id of the original outcome
   * @param phaseId the id of the phase
   * @param subIdoId the Srf SubIdo ID
   * @return a CrpOutcomeSubIdo, null if not found.
   */
  public CrpOutcomeSubIdo getCrpOutcomeSubIdoByOutcomeComposedIdPhaseAndSubIdo(String outcomeComposedId, long phaseId,
    long subIdoId);

  /**
   * Replicates an outcome sub-ido, starting from the given phase
   * 
   * @param originalCrpOutcomeSubIdo outcome sub-ido to be replicated
   * @param initialPhase initial replication phase
   */
  public void replicate(CrpOutcomeSubIdo originalCrpOutcomeSubIdo, Phase initialPhase);

  /**
   * This method saves the information of the given crpOutcomeSubIdo
   * 
   * @param crpOutcomeSubIdo - is the crpOutcomeSubIdo object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpOutcomeSubIdo was
   *         updated
   *         or -1 is some error occurred.
   */
  public CrpOutcomeSubIdo saveCrpOutcomeSubIdo(CrpOutcomeSubIdo crpOutcomeSubIdo);


}
