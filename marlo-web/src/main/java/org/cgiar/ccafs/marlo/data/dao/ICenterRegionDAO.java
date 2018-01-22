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

import org.cgiar.ccafs.marlo.data.model.CenterRegion;

import java.util.List;


public interface ICenterRegionDAO {

  /**
   * This method removes a specific researchRegion value from the database.
   * 
   * @param researchRegionId is the researchRegion identifier.
   * @return true if the researchRegion was successfully deleted, false otherwise.
   */
  public void deleteResearchRegion(long researchRegionId);

  /**
   * This method validate if the researchRegion identify with the given id exists in the system.
   * 
   * @param researchRegionID is a researchRegion identifier.
   * @return true if the researchRegion exists, false otherwise.
   */
  public boolean existResearchRegion(long researchRegionID);

  /**
   * This method gets a researchRegion object by a given researchRegion identifier.
   * 
   * @param researchRegionID is the researchRegion identifier.
   * @return a CenterRegion object.
   */
  public CenterRegion find(long id);

  /**
   * This method gets a list of researchRegion that are active
   * 
   * @return a list from CenterRegion null if no exist records
   */
  public List<CenterRegion> findAll();


  /**
   * This method gets a list of researchRegions belongs of the user
   * 
   * @param userId - the user id
   * @return List of ResearchRegions or null if the user is invalid or not have roles.
   */
  public List<CenterRegion> getResearchRegionsByUserId(long userId);

  /**
   * This method saves the information of the given researchRegion
   * 
   * @param researchRegion - is the researchRegion object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the researchRegion was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterRegion save(CenterRegion researchRegion);
}
