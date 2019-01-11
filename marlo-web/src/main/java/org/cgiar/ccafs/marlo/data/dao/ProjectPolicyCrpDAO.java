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

import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrp;

import java.util.List;


public interface ProjectPolicyCrpDAO {

  /**
   * This method removes a specific projectPolicyCrp value from the database.
   * 
   * @param projectPolicyCrpId is the projectPolicyCrp identifier.
   * @return true if the projectPolicyCrp was successfully deleted, false otherwise.
   */
  public void deleteProjectPolicyCrp(long projectPolicyCrpId);

  /**
   * This method validate if the projectPolicyCrp identify with the given id exists in the system.
   * 
   * @param projectPolicyCrpID is a projectPolicyCrp identifier.
   * @return true if the projectPolicyCrp exists, false otherwise.
   */
  public boolean existProjectPolicyCrp(long projectPolicyCrpID);

  /**
   * This method gets a projectPolicyCrp object by a given projectPolicyCrp identifier.
   * 
   * @param projectPolicyCrpID is the projectPolicyCrp identifier.
   * @return a ProjectPolicyCrp object.
   */
  public ProjectPolicyCrp find(long id);

  /**
   * This method gets a list of projectPolicyCrp that are active
   * 
   * @return a list from ProjectPolicyCrp null if no exist records
   */
  public List<ProjectPolicyCrp> findAll();


  /**
   * This method saves the information of the given projectPolicyCrp
   * 
   * @param projectPolicyCrp - is the projectPolicyCrp object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectPolicyCrp was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectPolicyCrp save(ProjectPolicyCrp projectPolicyCrp);
}
