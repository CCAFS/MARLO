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

import org.cgiar.ccafs.marlo.data.model.ActivityTitle;

import java.util.List;


public interface ActivityTitleDAO {

  /**
   * This method removes a specific activityTitle value from the database.
   * 
   * @param activityTitleId is the activityTitle identifier.
   * @return true if the activityTitle was successfully deleted, false otherwise.
   */
  public void deleteActivityTitle(long activityTitleId);

  /**
   * This method validate if the activityTitle identify with the given id exists in the system.
   * 
   * @param activityTitleID is a activityTitle identifier.
   * @return true if the activityTitle exists, false otherwise.
   */
  public boolean existActivityTitle(long activityTitleID);

  /**
   * This method gets a activityTitle object by a given activityTitle identifier.
   * 
   * @param activityTitleID is the activityTitle identifier.
   * @return a ActivityTitle object.
   */
  public ActivityTitle find(long id);

  /**
   * This method gets a list of activityTitle that are active
   * 
   * @return a list from ActivityTitle null if no exist records
   */
  public List<ActivityTitle> findAll();

  /**
   * This method gets a list of activityTitle that are active
   * 
   * @param year is a int object.
   * @return a list from ActivityTitle null if no exist records
   */
  public List<ActivityTitle> findByCurrentYear(int year);


  /**
   * This method saves the information of the given activityTitle
   * 
   * @param activityTitle - is the activityTitle object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the activityTitle was
   *         updated
   *         or -1 is some error occurred.
   */
  public ActivityTitle save(ActivityTitle activityTitle);
}
