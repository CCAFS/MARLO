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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyMilestone;

import java.util.List;


public interface ProjectExpectedStudyMilestoneDAO {

  /**
   * This method removes a specific projectExpectedStudyMilestone value from the database.
   * 
   * @param projectExpectedStudyMilestoneId is the projectExpectedStudyMilestone identifier.
   * @return true if the projectExpectedStudyMilestone was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyMilestone(long projectExpectedStudyMilestoneId);

  /**
   * This method validate if the projectExpectedStudyMilestone identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyMilestoneID is a projectExpectedStudyMilestone identifier.
   * @return true if the projectExpectedStudyMilestone exists, false otherwise.
   */
  public boolean existProjectExpectedStudyMilestone(long projectExpectedStudyMilestoneID);

  /**
   * This method gets a projectExpectedStudyMilestone object by a given projectExpectedStudyMilestone identifier.
   * 
   * @param projectExpectedStudyMilestoneID is the projectExpectedStudyMilestone identifier.
   * @return a ProjectExpectedStudyMilestone object.
   */
  public ProjectExpectedStudyMilestone find(long id);

  /**
   * This method gets a list of projectExpectedStudyMilestone that are active
   * 
   * @return a list from ProjectExpectedStudyMilestone null if no exist records
   */
  public List<ProjectExpectedStudyMilestone> findAll();


  /**
   * This method saves the information of the given projectExpectedStudyMilestone
   * 
   * @param projectExpectedStudyMilestone - is the projectExpectedStudyMilestone object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyMilestone was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyMilestone save(ProjectExpectedStudyMilestone projectExpectedStudyMilestone);
}
