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

import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.data.model.StudyHomeDTO;

import java.util.List;
import java.util.Map;


public interface ProjectExpectedStudyDAO {

  /**
   * This method removes a specific projectExpectedStudy value from the database.
   * 
   * @param projectExpectedStudyId is the projectExpectedStudy identifier.
   * @return true if the projectExpectedStudy was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudy(long projectExpectedStudyId);

  /**
   * This method validate if the projectExpectedStudy identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyID is a projectExpectedStudy identifier.
   * @return true if the projectExpectedStudy exists, false otherwise.
   */
  public boolean existProjectExpectedStudy(long projectExpectedStudyID);

  /**
   * This method gets a projectExpectedStudy object by a given projectExpectedStudy identifier.
   * 
   * @param projectExpectedStudyID is the projectExpectedStudy identifier.
   * @return a ProjectExpectedStudy object.
   */
  public ProjectExpectedStudy find(long id);

  /**
   * This method gets a list of projectExpectedStudy that are active
   * 
   * @return a list from ProjectExpectedStudy null if no exist records
   */
  public List<ProjectExpectedStudy> findAll();


  /**
   * This method gets a list of ALL projectExpectedStudy that are active up to a given phase
   * 
   * @return a list of ProjectExpectedStudy null if no exist records
   */
  public List<ProjectExpectedStudy> getAllStudiesByPhase(long phaseId);

  /**
   * This method gets a list of projectExpectedStudy that are active by a given organizationType and phase
   * 
   * @return a list from ProjectExpectedStudy null if no exist records
   */
  public List<ProjectExpectedStudy> getStudiesByOrganizationType(RepIndOrganizationType repIndOrganizationType,
    Phase phase);

  /**
   * This method gets a list of projectExpectedStudy that are active by a given phase
   * 
   * @return a list from ProjectExpectedStudy null if no exist records
   */
  public List<ProjectExpectedStudy> getStudiesByPhase(Phase phase);

  /**
   * Gets a list of all the studies from a project planned for the phase's year
   * NOTE: this method is meant to be used by the Home Dashboard table
   * 
   * @param phaseId the Phase identifier
   * @param projectId the Project identifier
   * @return a list of StudyHomeDTO or empty
   */
  public List<StudyHomeDTO> getStudiesByProjectAndPhaseHome(long phaseId, long projectId);

  /**
   * This method search the expected Studies that the user can be edit.
   * 
   * @param userId the user id
   * @param crp the crp acronym
   * @return The expected Studies that can edit the user
   */
  public List<Map<String, Object>> getUserStudies(long userId, String crp);

  /**
   * This method gets the information if a study was unchecked to exclude on reporting for the given
   * projectExpectedStudy and phase
   * 
   * @param projectExpectedStudyId - is study identifier.
   * @param phaseId - is phase identifier
   * @param typeStudy - is study type 1-OICR, 2-MELIA
   * @return a boolean representing if study was excluded from Reporting false-Included, true-Excluded
   */
  public Boolean isStudyExcluded(Long projectExpectedStudyId, Long phaseId, Long typeStudy);

  /**
   * This method saves the information of the given projectExpectedStudy
   * 
   * @param projectExpectedStudy - is the projectExpectedStudy object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudy was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudy save(ProjectExpectedStudy projectExpectedStudy);

  /**
   * This method saves the information of the given projectExpectedStudy
   * 
   * @param projectExpectedStudy - is the projectExpectedStudy object with the new information to be added/updated.
   * @param section - the name of the map section.
   * @param relationsName - a List of set relations that the object have it.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudy was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudy save(ProjectExpectedStudy projectExpectedStudy, String section,
    List<String> relationsName, Phase phase);
}
