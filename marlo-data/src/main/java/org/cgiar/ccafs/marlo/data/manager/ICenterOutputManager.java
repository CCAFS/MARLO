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

import org.cgiar.ccafs.marlo.data.manager.impl.CenterOutputManager;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CenterOutputManager.class)
public interface ICenterOutputManager {


  /**
   * This method removes a specific researchOutput value from the database.
   * 
   * @param researchOutputId is the researchOutput identifier.
   * @return true if the researchOutput was successfully deleted, false otherwise.
   */
  public boolean deleteResearchOutput(long researchOutputId);


  /**
   * This method validate if the researchOutput identify with the given id exists in the system.
   * 
   * @param researchOutputID is a researchOutput identifier.
   * @return true if the researchOutput exists, false otherwise.
   */
  public boolean existResearchOutput(long researchOutputID);


  /**
   * This method gets a list of researchOutput that are active
   * 
   * @return a list from CenterOutput null if no exist records
   */
  public List<CenterOutput> findAll();


  /**
   * This method gets a researchOutput object by a given researchOutput identifier.
   * 
   * @param researchOutputID is the researchOutput identifier.
   * @return a CenterOutput object.
   */
  public CenterOutput getResearchOutputById(long researchOutputID);

  /**
   * This method gets a list of researchOutputs belongs of the user
   * 
   * @param userId - the user id
   * @return List of ResearchOutputs or null if the user is invalid or not have roles.
   */
  public List<CenterOutput> getResearchOutputsByUserId(Long userId);

  /**
   * This method saves the information of the given researchOutput
   * 
   * @param researchOutput - is the researchOutput object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the researchOutput was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveResearchOutput(CenterOutput researchOutput);

  /**
   * This method saves the information of the given researchOutput
   * 
   * @param outcome - is the researchOutput object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the researchOutput was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveResearchOutput(CenterOutput output, String actionName, List<String> relationsName);


}
