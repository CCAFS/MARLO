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

import org.cgiar.ccafs.marlo.data.dao.mysql.ProjectFurtherContributionMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectFurtherContribution;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectFurtherContributionMySQLDAO.class)
public interface ProjectFurtherContributionDAO {

  /**
   * This method removes a specific projectFurtherContribution value from the database.
   * 
   * @param projectFurtherContributionId is the projectFurtherContribution identifier.
   * @return true if the projectFurtherContribution was successfully deleted, false otherwise.
   */
  public boolean deleteProjectFurtherContribution(long projectFurtherContributionId);

  /**
   * This method validate if the projectFurtherContribution identify with the given id exists in the system.
   * 
   * @param projectFurtherContributionID is a projectFurtherContribution identifier.
   * @return true if the projectFurtherContribution exists, false otherwise.
   */
  public boolean existProjectFurtherContribution(long projectFurtherContributionID);

  /**
   * This method gets a projectFurtherContribution object by a given projectFurtherContribution identifier.
   * 
   * @param projectFurtherContributionID is the projectFurtherContribution identifier.
   * @return a ProjectFurtherContribution object.
   */
  public ProjectFurtherContribution find(long id);

  /**
   * This method gets a list of projectFurtherContribution that are active
   * 
   * @return a list from ProjectFurtherContribution null if no exist records
   */
  public List<ProjectFurtherContribution> findAll();


  /**
   * This method saves the information of the given projectFurtherContribution
   * 
   * @param projectFurtherContribution - is the projectFurtherContribution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectFurtherContribution was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(ProjectFurtherContribution projectFurtherContribution);
}
