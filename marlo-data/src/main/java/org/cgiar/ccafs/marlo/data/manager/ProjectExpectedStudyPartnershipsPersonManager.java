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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPartnershipsPerson;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyPartnershipsPersonManager {


  /**
   * This method removes a specific projectExpectedStudyPartnershipsPerson value from the database.
   * 
   * @param projectExpectedStudyPartnershipsPersonId is the projectExpectedStudyPartnershipsPerson identifier.
   * @return true if the projectExpectedStudyPartnershipsPerson was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyPartnershipsPerson(long projectExpectedStudyPartnershipsPersonId);


  /**
   * This method validate if the projectExpectedStudyPartnershipsPerson identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyPartnershipsPersonID is a projectExpectedStudyPartnershipsPerson identifier.
   * @return true if the projectExpectedStudyPartnershipsPerson exists, false otherwise.
   */
  public boolean existProjectExpectedStudyPartnershipsPerson(long projectExpectedStudyPartnershipsPersonID);


  /**
   * This method gets a list of projectExpectedStudyPartnershipsPerson that are active
   * 
   * @return a list from ProjectExpectedStudyPartnershipsPerson null if no exist records
   */
  public List<ProjectExpectedStudyPartnershipsPerson> findAll();


  /**
   * This method gets a projectExpectedStudyPartnershipsPerson object by a given projectExpectedStudyPartnershipsPerson identifier.
   * 
   * @param projectExpectedStudyPartnershipsPersonID is the projectExpectedStudyPartnershipsPerson identifier.
   * @return a ProjectExpectedStudyPartnershipsPerson object.
   */
  public ProjectExpectedStudyPartnershipsPerson getProjectExpectedStudyPartnershipsPersonById(long projectExpectedStudyPartnershipsPersonID);

  /**
   * This method saves the information of the given projectExpectedStudyPartnershipsPerson
   * 
   * @param projectExpectedStudyPartnershipsPerson - is the projectExpectedStudyPartnershipsPerson object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyPartnershipsPerson was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyPartnershipsPerson saveProjectExpectedStudyPartnershipsPerson(ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPerson);


}
