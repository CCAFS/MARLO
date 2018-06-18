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

import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnership;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ProjectPartnerPartnershipManager {


  /**
   * This method removes a specific projectPartnerPartnership value from the database.
   * 
   * @param projectPartnerPartnershipId is the projectPartnerPartnership identifier.
   * @return true if the projectPartnerPartnership was successfully deleted, false otherwise.
   */
  public void deleteProjectPartnerPartnership(long projectPartnerPartnershipId);


  /**
   * This method validate if the projectPartnerPartnership identify with the given id exists in the system.
   * 
   * @param projectPartnerPartnershipID is a projectPartnerPartnership identifier.
   * @return true if the projectPartnerPartnership exists, false otherwise.
   */
  public boolean existProjectPartnerPartnership(long projectPartnerPartnershipID);


  /**
   * This method gets a list of projectPartnerPartnership that are active
   * 
   * @return a list from ProjectPartnerPartnership null if no exist records
   */
  public List<ProjectPartnerPartnership> findAll();


  /**
   * This method gets a projectPartnerPartnership object by a given projectPartnerPartnership identifier.
   * 
   * @param projectPartnerPartnershipID is the projectPartnerPartnership identifier.
   * @return a ProjectPartnerPartnership object.
   */
  public ProjectPartnerPartnership getProjectPartnerPartnershipById(long projectPartnerPartnershipID);

  /**
   * This method gets a list of ProjectPartnerPartnership that are active by a given phase
   * 
   * @return a list from ProjectPartnerPartnership null if no exist records
   */
  public List<ProjectPartnerPartnership> getProjectPartnerPartnershipByPhase(Phase phase);

  /**
   * This method saves the information of the given projectPartnerPartnership
   * 
   * @param projectPartnerPartnership - is the projectPartnerPartnership object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectPartnerPartnership was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectPartnerPartnership saveProjectPartnerPartnership(ProjectPartnerPartnership projectPartnerPartnership);
}
