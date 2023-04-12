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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyReference;

import java.util.List;


public interface ProjectExpectedStudyReferenceDAO {

  /**
   * This method removes a specific projectExpectedStudyReference value from the database.
   * 
   * @param projectExpectedStudyReferenceId is the projectExpectedStudyReference identifier.
   * @return true if the projectExpectedStudyReference was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyReference(long projectExpectedStudyReferenceId);

  /**
   * This method validate if the projectExpectedStudyReference identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyReferenceID is a projectExpectedStudyReference identifier.
   * @return true if the projectExpectedStudyReference exists, false otherwise.
   */
  public boolean existProjectExpectedStudyReference(long projectExpectedStudyReferenceID);

  /**
   * This method gets a projectExpectedStudyReference object by a given projectExpectedStudyReference identifier.
   * 
   * @param projectExpectedStudyReferenceID is the projectExpectedStudyReference identifier.
   * @return a ProjectExpectedStudyReference object.
   */
  public ProjectExpectedStudyReference find(long id);

  /**
   * This method gets a list of projectExpectedStudyReference that are active
   * 
   * @return a list from ProjectExpectedStudyReference null if no exist records
   */
  public List<ProjectExpectedStudyReference> findAll();

  /**
   * This method gets a list of projectExpectedStudyReference by a given projectExpectedStudy identifier.
   * 
   * @param studyId is the projectExpectedStudy identifier.
   * @return a list of projectExpectedStudyReference objects.
   */
  public List<ProjectExpectedStudyReference> getAllStudyReferencesByStudy(long studyId);

  /**
   * This method gets a projectExpectedStudyReference object by a given projectExpectedStudy, phase identifier and link
   * 
   * @param expectedID is the projectExpectedStudy identifier.
   * @param link is the url link
   * @param phaseID is the phase identifier
   * @return a ProjectExpectedStudyReference object.
   */
  public ProjectExpectedStudyReference getProjectExpectedStudyReferenceByPhase(long expectedID, String link,
    long phaseID);

  /**
   * This method saves the information of the given projectExpectedStudyReference
   * 
   * @param projectExpectedStudyReference - is the projectExpectedStudyReference object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudyReference
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyReference save(ProjectExpectedStudyReference projectExpectedStudyReference);
}