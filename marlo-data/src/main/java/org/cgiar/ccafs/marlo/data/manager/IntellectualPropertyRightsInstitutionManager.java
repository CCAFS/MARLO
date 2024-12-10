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

import org.cgiar.ccafs.marlo.data.model.IntellectualPropertyRightsInstitution;

import java.util.List;


/**
 * @author CCAFS
 */

public interface IntellectualPropertyRightsInstitutionManager {


  /**
   * This method removes a specific intellectualPropertyRightsInstitution value from the database.
   * 
   * @param intellectualPropertyRightsInstitutionId is the intellectualPropertyRightsInstitution identifier.
   * @return true if the intellectualPropertyRightsInstitution was successfully deleted, false otherwise.
   */
  public void deleteIntellectualPropertyRightsInstitution(long intellectualPropertyRightsInstitutionId);


  /**
   * This method validate if the intellectualPropertyRightsInstitution identify with the given id exists in the system.
   * 
   * @param intellectualPropertyRightsInstitutionID is a intellectualPropertyRightsInstitution identifier.
   * @return true if the intellectualPropertyRightsInstitution exists, false otherwise.
   */
  public boolean existIntellectualPropertyRightsInstitution(long intellectualPropertyRightsInstitutionID);


  /**
   * This method gets a list of intellectualPropertyRightsInstitution that are active
   * 
   * @return a list from IntellectualPropertyRightsInstitution null if no exist records
   */
  public List<IntellectualPropertyRightsInstitution> findAll();


  /**
   * This method gets a intellectualPropertyRightsInstitution object by a given intellectualPropertyRightsInstitution identifier.
   * 
   * @param intellectualPropertyRightsInstitutionID is the intellectualPropertyRightsInstitution identifier.
   * @return a IntellectualPropertyRightsInstitution object.
   */
  public IntellectualPropertyRightsInstitution getIntellectualPropertyRightsInstitutionById(long intellectualPropertyRightsInstitutionID);

  /**
   * This method saves the information of the given intellectualPropertyRightsInstitution
   * 
   * @param intellectualPropertyRightsInstitution - is the intellectualPropertyRightsInstitution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the intellectualPropertyRightsInstitution was
   *         updated
   *         or -1 is some error occurred.
   */
  public IntellectualPropertyRightsInstitution saveIntellectualPropertyRightsInstitution(IntellectualPropertyRightsInstitution intellectualPropertyRightsInstitution);


}
