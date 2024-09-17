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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPartnership;

import java.util.List;


public interface ProjectExpectedStudyPartnershipDAO {

  /**
   * This method removes a specific projectExpectedStudyPartnership value from the database.
   * 
   * @param projectExpectedStudyPartnershipId is the projectExpectedStudyPartnership identifier.
   * @return true if the projectExpectedStudyPartnership was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyPartnership(long projectExpectedStudyPartnershipId);

  /**
   * This method validate if the projectExpectedStudyPartnership identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyPartnershipID is a projectExpectedStudyPartnership identifier.
   * @return true if the projectExpectedStudyPartnership exists, false otherwise.
   */
  public boolean existProjectExpectedStudyPartnership(long projectExpectedStudyPartnershipID);

  /**
   * This method gets a projectExpectedStudyPartnership object by a given projectExpectedStudyPartnership identifier.
   * 
   * @param projectExpectedStudyPartnershipID is the projectExpectedStudyPartnership identifier.
   * @return a ProjectExpectedStudyPartnership object.
   */
  public ProjectExpectedStudyPartnership find(long id);

  /**
   * This method gets a list of projectExpectedStudyPartnership that are active
   * 
   * @return a list from ProjectExpectedStudyPartnership null if no exist records
   */
  public List<ProjectExpectedStudyPartnership> findAll();


  List<ProjectExpectedStudyPartnership> findByExpected(long expectedId);

  List<ProjectExpectedStudyPartnership> findByExpectedAndPhase(long expectedId, long phaseId);

  /**
   * This method saves the information of the given projectExpectedStudyPartnership
   * 
   * @param projectExpectedStudyPartnership - is the projectExpectedStudyPartnership object with the new information to
   *        be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudyPartnership was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyPartnership save(ProjectExpectedStudyPartnership projectExpectedStudyPartnership);
}
