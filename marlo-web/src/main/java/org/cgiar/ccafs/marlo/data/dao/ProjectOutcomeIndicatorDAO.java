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

import org.cgiar.ccafs.marlo.data.model.ProjectOutcomeIndicator;

import java.util.List;


public interface ProjectOutcomeIndicatorDAO {

  /**
   * This method removes a specific projectOutcomeIndicator value from the database.
   * 
   * @param projectOutcomeIndicatorId is the projectOutcomeIndicator identifier.
   * @return true if the projectOutcomeIndicator was successfully deleted, false otherwise.
   */
  public void deleteProjectOutcomeIndicator(long projectOutcomeIndicatorId);

  /**
   * This method validate if the projectOutcomeIndicator identify with the given id exists in the system.
   * 
   * @param projectOutcomeIndicatorID is a projectOutcomeIndicator identifier.
   * @return true if the projectOutcomeIndicator exists, false otherwise.
   */
  public boolean existProjectOutcomeIndicator(long projectOutcomeIndicatorID);

  /**
   * This method gets a projectOutcomeIndicator object by a given projectOutcomeIndicator identifier.
   * 
   * @param projectOutcomeIndicatorID is the projectOutcomeIndicator identifier.
   * @return a ProjectOutcomeIndicator object.
   */
  public ProjectOutcomeIndicator find(long id);

  /**
   * This method gets a list of projectOutcomeIndicator that are active
   * 
   * @return a list from ProjectOutcomeIndicator null if no exist records
   */
  public List<ProjectOutcomeIndicator> findAll();


  /**
   * This method saves the information of the given projectOutcomeIndicator
   * 
   * @param projectOutcomeIndicator - is the projectOutcomeIndicator object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectOutcomeIndicator was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectOutcomeIndicator save(ProjectOutcomeIndicator projectOutcomeIndicator);
}
