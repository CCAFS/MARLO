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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;

import java.util.List;


public interface ProjectExpectedStudyFlagshipDAO {

  /**
   * This method removes a specific projectExpectedStudyFlagship value from the database.
   * 
   * @param projectExpectedStudyFlagshipId is the projectExpectedStudyFlagship identifier.
   * @return true if the projectExpectedStudyFlagship was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyFlagship(long projectExpectedStudyFlagshipId);

  /**
   * This method validate if the projectExpectedStudyFlagship identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyFlagshipID is a projectExpectedStudyFlagship identifier.
   * @return true if the projectExpectedStudyFlagship exists, false otherwise.
   */
  public boolean existProjectExpectedStudyFlagship(long projectExpectedStudyFlagshipID);

  /**
   * This method gets a projectExpectedStudyFlagship object by a given projectExpectedStudyFlagship identifier.
   * 
   * @param projectExpectedStudyFlagshipID is the projectExpectedStudyFlagship identifier.
   * @return a ProjectExpectedStudyFlagship object.
   */
  public ProjectExpectedStudyFlagship find(long id);

  /**
   * This method gets a list of projectExpectedStudyFlagship that are active
   * 
   * @return a list from ProjectExpectedStudyFlagship null if no exist records
   */
  public List<ProjectExpectedStudyFlagship> findAll();


  /**
   * This method gets a projectExpectedStudyFlagship object by a given expectedStudy, Flagship and Phase identifier.
   * 
   * @param expectedStudyId is the projectExpectedStudy identifier.
   * @param flagshipId is the flagship/Module identifier.
   * @param phaseId is the Phase identifier.
   * @return a ProjectExpectedStudyFlagship object.
   */
  public ProjectExpectedStudyFlagship findProjectExpectedStudyFlagshipbyPhase(Long expectedStudyId, Long flagshipId,
    Long phaseId);

  /**
   * This method gets a list of projectExpectedStudyFlagship by a given projectExpectedStudy identifier.
   * 
   * @param studyId is the projectExpectedStudy identifier.
   * @return a list of projectExpectedStudyFlagship objects.
   */
  public List<ProjectExpectedStudyFlagship> getAllStudyFlagshipsByStudy(long studyId);

  /**
   * This method saves the information of the given projectExpectedStudyFlagship
   * 
   * @param projectExpectedStudyFlagship - is the projectExpectedStudyFlagship object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudyFlagship was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyFlagship save(ProjectExpectedStudyFlagship projectExpectedStudyFlagship);
}
