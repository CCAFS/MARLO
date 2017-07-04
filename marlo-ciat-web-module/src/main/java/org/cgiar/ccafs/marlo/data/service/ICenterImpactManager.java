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

import org.cgiar.ccafs.marlo.data.model.CenterImpact;
import org.cgiar.ccafs.marlo.data.service.impl.CenterImpactManager;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CenterImpactManager.class)
public interface ICenterImpactManager {


  /**
   * This method removes a specific researchImpact value from the database.
   * 
   * @param researchImpactId is the researchImpact identifier.
   * @return true if the researchImpact was successfully deleted, false otherwise.
   */
  public boolean deleteResearchImpact(long researchImpactId);


  /**
   * This method validate if the researchImpact identify with the given id exists in the system.
   * 
   * @param researchImpactID is a researchImpact identifier.
   * @return true if the researchImpact exists, false otherwise.
   */
  public boolean existResearchImpact(long researchImpactID);


  /**
   * This method gets a list of researchImpact that are active
   * 
   * @return a list from CenterImpact null if no exist records
   */
  public List<CenterImpact> findAll();


  /**
   * This method gets a researchImpact object by a given researchImpact identifier.
   * 
   * @param researchImpactID is the researchImpact identifier.
   * @return a CenterImpact object.
   */
  public CenterImpact getResearchImpactById(long researchImpactID);

  /**
   * This method gets a list of researchImpacts belongs of the user
   * 
   * @param userId - the user id
   * @return List of ResearchImpacts or null if the user is invalid or not have roles.
   */
  public List<CenterImpact> getResearchImpactsByUserId(Long userId);

  /**
   * This method saves the information of the given researchImpact
   * 
   * @param researchImpact - is the researchImpact object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the researchImpact was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveResearchImpact(CenterImpact researchImpact);


}
