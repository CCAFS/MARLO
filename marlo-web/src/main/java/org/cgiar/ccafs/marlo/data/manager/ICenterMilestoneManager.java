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

import org.cgiar.ccafs.marlo.data.manager.impl.CenterMilestoneManager;
import org.cgiar.ccafs.marlo.data.model.CenterMilestone;

import java.util.List;
import java.util.Map;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CenterMilestoneManager.class)
public interface ICenterMilestoneManager {


  /**
   * This method removes a specific centerMilestone value from the database.
   * 
   * @param centerMilestoneId is the centerMilestone identifier.
   * @return true if the centerMilestone was successfully deleted, false otherwise.
   */
  public boolean deleteCenterMilestone(long centerMilestoneId);


  /**
   * This method validate if the centerMilestone identify with the given id exists in the system.
   * 
   * @param centerMilestoneID is a centerMilestone identifier.
   * @return true if the centerMilestone exists, false otherwise.
   */
  public boolean existCenterMilestone(long centerMilestoneID);


  /**
   * This method gets a list of centerMilestone that are active
   * 
   * @return a list from CenterMilestone null if no exist records
   */
  public List<CenterMilestone> findAll();


  /**
   * This method gets a centerMilestone object by a given centerMilestone identifier.
   * 
   * @param centerMilestoneID is the centerMilestone identifier.
   * @return a CenterMilestone object.
   */
  public CenterMilestone getCenterMilestoneById(long centerMilestoneID);

  /**
   * This method gets a list of centerMilestones belongs of the user
   * 
   * @param userId - the user id
   * @return List of Projects or null if the user is invalid or not have roles.
   */
  public List<CenterMilestone> getCenterMilestonesByUserId(Long userId);

  /**
   * This method gets a report of Impact Pathway Outcomes Target Unit count by program
   * 
   * @return a list of report of Impact Pathway Outcomes
   */
  public List<Map<String, Object>> getCountTargetUnit(long programID);

  /**
   * This method saves the information of the given centerMilestone
   * 
   * @param centerMilestone - is the centerMilestone object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the centerMilestone was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveCenterMilestone(CenterMilestone centerMilestone);

  /**
   * This method saves the information of the given centerMilestone
   * 
   * @param centerMilestone - is the centerMilestone object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the centerMilestone was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveCenterMilestone(CenterMilestone centerMilestone, String actionName, List<String> relationsName);

}
