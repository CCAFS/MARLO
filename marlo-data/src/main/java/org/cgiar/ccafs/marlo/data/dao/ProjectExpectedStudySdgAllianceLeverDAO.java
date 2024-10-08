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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySdgAllianceLever;

import java.util.List;


public interface ProjectExpectedStudySdgAllianceLeverDAO {

  /**
   * This method removes a specific projectExpectedStudySdgAllianceLever value from the database.
   * 
   * @param projectExpectedStudySdgAllianceLeverId is the projectExpectedStudySdgAllianceLever identifier.
   * @return true if the projectExpectedStudySdgAllianceLever was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudySdgAllianceLever(long projectExpectedStudySdgAllianceLeverId);

  /**
   * This method validate if the projectExpectedStudySdgAllianceLever identify with the given id exists in the system.
   * 
   * @param projectExpectedStudySdgAllianceLeverID is a projectExpectedStudySdgAllianceLever identifier.
   * @return true if the projectExpectedStudySdgAllianceLever exists, false otherwise.
   */
  public boolean existProjectExpectedStudySdgAllianceLever(long projectExpectedStudySdgAllianceLeverID);

  /**
   * This method gets a projectExpectedStudySdgAllianceLever object by a given projectExpectedStudySdgAllianceLever
   * identifier.
   * 
   * @param projectExpectedStudySdgAllianceLeverID is the projectExpectedStudySdgAllianceLever identifier.
   * @return a ProjectExpectedStudySdgAllianceLever object.
   */
  public ProjectExpectedStudySdgAllianceLever find(long id);

  /**
   * This method gets a list of projectExpectedStudySdgAllianceLever that are active
   * 
   * @return a list from ProjectExpectedStudySdgAllianceLever null if no exist records
   */
  public List<ProjectExpectedStudySdgAllianceLever> findAll();


  ProjectExpectedStudySdgAllianceLever findAllByPhaseExpectedAndLever(long phaseId, long expectedId, long leverId);

  ProjectExpectedStudySdgAllianceLever findByPhaseExpectedAndLever(long phaseId, long expectedId, long leverId,
    long sdg, int isPrimary);

  ProjectExpectedStudySdgAllianceLever findByPhaseExpectedAndLeverSingle(long phaseId, long expectedId, long leverId,
    long sdg, int isPrimary);

  /**
   * This method saves the information of the given projectExpectedStudySdgAllianceLever
   * 
   * @param projectExpectedStudySdgAllianceLever - is the projectExpectedStudySdgAllianceLever object with the new
   *        information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudySdgAllianceLever was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudySdgAllianceLever
    save(ProjectExpectedStudySdgAllianceLever projectExpectedStudySdgAllianceLever);
}