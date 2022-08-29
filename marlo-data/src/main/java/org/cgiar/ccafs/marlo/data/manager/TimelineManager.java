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

import org.cgiar.ccafs.marlo.data.model.Timeline;

import java.util.List;


/**
 * @author CCAFS
 */

public interface TimelineManager {


  /**
   * This method removes a specific timeline value from the database.
   * 
   * @param timelineId is the timeline identifier.
   * @return true if the timeline was successfully deleted, false otherwise.
   */
  public void deleteTimeline(long timelineId);


  /**
   * This method validate if the timeline identify with the given id exists in the system.
   * 
   * @param timelineID is a timeline identifier.
   * @return true if the timeline exists, false otherwise.
   */
  public boolean existTimeline(long timelineID);


  /**
   * This method gets a list of timeline that are active
   * 
   * @return a list from Timeline null if no exist records
   */
  public List<Timeline> findAll();


  /**
   * This method gets a timeline object by a given timeline identifier.
   * 
   * @param timelineID is the timeline identifier.
   * @return a Timeline object.
   */
  public Timeline getTimelineById(long timelineID);

  /**
   * This method saves the information of the given timeline
   * 
   * @param timeline - is the timeline object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the timeline was
   *         updated
   *         or -1 is some error occurred.
   */
  public Timeline saveTimeline(Timeline timeline);


}
