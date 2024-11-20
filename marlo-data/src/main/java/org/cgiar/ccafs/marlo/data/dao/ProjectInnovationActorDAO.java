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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationActor;

import java.util.List;


public interface ProjectInnovationActorDAO {

  /**
   * This method removes a specific projectInnovationActor value from the database.
   * 
   * @param projectInnovationActorId is the projectInnovationActor identifier.
   * @return true if the projectInnovationActor was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationActor(long projectInnovationActorId);

  /**
   * This method validate if the projectInnovationActor identify with the given id exists in the system.
   * 
   * @param projectInnovationActorID is a projectInnovationActor identifier.
   * @return true if the projectInnovationActor exists, false otherwise.
   */
  public boolean existProjectInnovationActor(long projectInnovationActorID);

  /**
   * This method gets a projectInnovationActor object by a given projectInnovationActor identifier.
   * 
   * @param projectInnovationActorID is the projectInnovationActor identifier.
   * @return a ProjectInnovationActor object.
   */
  public ProjectInnovationActor find(long id);

  /**
   * This method gets a list of projectInnovationActor that are active
   * 
   * @return a list from ProjectInnovationActor null if no exist records
   */
  public List<ProjectInnovationActor> findAll();


  /**
   * This method saves the information of the given projectInnovationActor
   * 
   * @param projectInnovationActor - is the projectInnovationActor object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationActor was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationActor save(ProjectInnovationActor projectInnovationActor);
}
