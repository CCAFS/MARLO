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

import org.cgiar.ccafs.marlo.data.model.PowbIndFollowingMilestone;

import java.util.List;


public interface PowbIndFollowingMilestoneDAO {

  /**
   * This method removes a specific powbIndFollowingMilestone value from the database.
   * 
   * @param powbIndFollowingMilestoneId is the powbIndFollowingMilestone identifier.
   * @return true if the powbIndFollowingMilestone was successfully deleted, false otherwise.
   */
  public void deletePowbIndFollowingMilestone(long powbIndFollowingMilestoneId);

  /**
   * This method validate if the powbIndFollowingMilestone identify with the given id exists in the system.
   * 
   * @param powbIndFollowingMilestoneID is a powbIndFollowingMilestone identifier.
   * @return true if the powbIndFollowingMilestone exists, false otherwise.
   */
  public boolean existPowbIndFollowingMilestone(long powbIndFollowingMilestoneID);

  /**
   * This method gets a powbIndFollowingMilestone object by a given powbIndFollowingMilestone identifier.
   * 
   * @param powbIndFollowingMilestoneID is the powbIndFollowingMilestone identifier.
   * @return a PowbIndFollowingMilestone object.
   */
  public PowbIndFollowingMilestone find(long id);

  /**
   * This method gets a list of powbIndFollowingMilestone that are active
   * 
   * @return a list from PowbIndFollowingMilestone null if no exist records
   */
  public List<PowbIndFollowingMilestone> findAll();


  /**
   * This method saves the information of the given powbIndFollowingMilestone
   * 
   * @param powbIndFollowingMilestone - is the powbIndFollowingMilestone object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbIndFollowingMilestone was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbIndFollowingMilestone save(PowbIndFollowingMilestone powbIndFollowingMilestone);
}
