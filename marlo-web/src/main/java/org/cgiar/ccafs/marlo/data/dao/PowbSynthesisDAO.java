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

import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;

import java.util.List;


public interface PowbSynthesisDAO {

  /**
   * This method removes a specific powbSynthesis value from the database.
   * 
   * @param powbSynthesisId is the powbSynthesis identifier.
   * @return true if the powbSynthesis was successfully deleted, false otherwise.
   */
  public void deletePowbSynthesis(long powbSynthesisId);

  /**
   * This method validate if the powbSynthesis identify with the given id exists in the system.
   * 
   * @param powbSynthesisID is a powbSynthesis identifier.
   * @return true if the powbSynthesis exists, false otherwise.
   */
  public boolean existPowbSynthesis(long powbSynthesisID);

  /**
   * This method gets a powbSynthesis object by a given powbSynthesis identifier.
   * 
   * @param powbSynthesisID is the powbSynthesis identifier.
   * @return a PowbSynthesis object.
   */
  public PowbSynthesis find(long id);

  /**
   * This method gets a list of powbSynthesis that are active
   * 
   * @return a list from PowbSynthesis null if no exist records
   */
  public List<PowbSynthesis> findAll();


  /**
   * This method gets a powbSynthesis by phase and liaison Institution
   * 
   * @return a PowbSynthesis object or null if no exist records
   */
  public PowbSynthesis findSynthesis(long phaseID, long liaisonInstitutionID);

  /**
   * This method saves the information of the given powbSynthesis
   * 
   * @param powbSynthesis - is the powbSynthesis object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbSynthesis was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbSynthesis save(PowbSynthesis powbSynthesis);

  /**
   * This method saves the information of the given powbSynthesis
   * 
   * @param powbSynthesis - is the powbSynthesis object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbSynthesis was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbSynthesis save(PowbSynthesis powbSynthesis, String sectionName, List<String> relationsName, Phase phase);
}
