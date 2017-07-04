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

import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.service.impl.CenterOutcomeManager;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CenterOutcomeManager.class)
public interface ICenterOutcomeManager {


  /**
   * This method removes a specific researchOutcome value from the database.
   * 
   * @param researchOutcomeId is the researchOutcome identifier.
   * @return true if the researchOutcome was successfully deleted, false otherwise.
   */
  public boolean deleteResearchOutcome(long researchOutcomeId);


  /**
   * This method validate if the researchOutcome identify with the given id exists in the system.
   * 
   * @param researchOutcomeID is a researchOutcome identifier.
   * @return true if the researchOutcome exists, false otherwise.
   */
  public boolean existResearchOutcome(long researchOutcomeID);


  /**
   * This method gets a list of researchOutcome that are active
   * 
   * @return a list from CenterOutcome null if no exist records
   */
  public List<CenterOutcome> findAll();


  /**
   * This method gets a researchOutcome object by a given researchOutcome identifier.
   * 
   * @param researchOutcomeID is the researchOutcome identifier.
   * @return a CenterOutcome object.
   */
  public CenterOutcome getResearchOutcomeById(long researchOutcomeID);

  /**
   * This method gets a list of researchOutcomes belongs of the user
   * 
   * @param userId - the user id
   * @return List of ResearchOutcomes or null if the user is invalid or not have roles.
   */
  public List<CenterOutcome> getResearchOutcomesByUserId(Long userId);

  /**
   * This method saves the information of the given researchOutcome
   * 
   * @param researchOutcome - is the researchOutcome object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the researchOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveResearchOutcome(CenterOutcome researchOutcome);

  /**
   * This method saves the information of the given researchOutcome
   * 
   * @param outcome - is the researchOutcome object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the researchOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveResearchOutcome(CenterOutcome outcome, String actionName, List<String> relationsName);


}
