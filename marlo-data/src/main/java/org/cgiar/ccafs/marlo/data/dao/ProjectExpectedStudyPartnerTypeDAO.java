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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPartnerType;

import java.util.List;


public interface ProjectExpectedStudyPartnerTypeDAO {

  /**
   * This method removes a specific projectExpectedStudyPartnerType value from the database.
   * 
   * @param projectExpectedStudyPartnerTypeId is the projectExpectedStudyPartnerType identifier.
   * @return true if the projectExpectedStudyPartnerType was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyPartnerType(long projectExpectedStudyPartnerTypeId);

  /**
   * This method validate if the projectExpectedStudyPartnerType identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyPartnerTypeID is a projectExpectedStudyPartnerType identifier.
   * @return true if the projectExpectedStudyPartnerType exists, false otherwise.
   */
  public boolean existProjectExpectedStudyPartnerType(long projectExpectedStudyPartnerTypeID);

  /**
   * This method gets a projectExpectedStudyPartnerType object by a given projectExpectedStudyPartnerType identifier.
   * 
   * @param projectExpectedStudyPartnerTypeID is the projectExpectedStudyPartnerType identifier.
   * @return a ProjectExpectedStudyPartnerType object.
   */
  public ProjectExpectedStudyPartnerType find(long id);

  /**
   * This method gets a list of projectExpectedStudyPartnerType that are active
   * 
   * @return a list from ProjectExpectedStudyPartnerType null if no exist records
   */
  public List<ProjectExpectedStudyPartnerType> findAll();


  /**
   * This method saves the information of the given projectExpectedStudyPartnerType
   * 
   * @param projectExpectedStudyPartnerType - is the projectExpectedStudyPartnerType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyPartnerType was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyPartnerType save(ProjectExpectedStudyPartnerType projectExpectedStudyPartnerType);
}
