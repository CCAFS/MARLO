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

import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.Map;


/**
 * @author Christian Garcia
 */

public interface ActivityManager {


  public Activity copyActivity(Activity activity, Phase phase);


  /**
   * This method removes a specific activity value from the database.
   * 
   * @param activityId is the activity identifier.
   * @return true if the activity was successfully deleted, false otherwise.
   */
  public void deleteActivity(long activityId);


  /**
   * This method validate if the activity identify with the given id exists in the system.
   * 
   * @param activityID is a activity identifier.
   * @return true if the activity exists, false otherwise.
   */
  public boolean existActivity(long activityID);


  /**
   * This method gets a list of activity that are active
   * 
   * @return a list from Activity null if no exist records
   */
  public List<Activity> findAll();


  /**
   * This method gets actives activities by a given activity identifier and project.
   * 
   * @param projectID is the project/cluster identifier.
   * @param phaseId is the phase identifier.
   * @return a list from Activity null if no exist records
   */
  public List<Activity> getActiveActivitiesByProject(long projectId, long phaseId);

  /**
   * This method gets, for every activity title of a global unit, the activities that are using it. One row per
   * activities record is returned, that is one row per phase, because activities replicate forward; the caller is
   * expected to collapse them by composedId. Only scalar columns are projected, so rendering the report does not
   * hydrate entities nor trigger lazy loads.
   * Columns: titleId, clusterId, composedId, activityId, activityActive, activityDescription, phaseId, phaseName,
   * phaseYear, clusterTitle. Rows come ordered by cluster, composedId and phase.
   * 
   * @param globalUnitId is the global unit that owns the activity titles.
   * @param currentPhaseId is the phase used to resolve the cluster title, falling back to its latest phase.
   * @return a list of rows, empty if no exist records
   */
  public List<Map<String, Object>> getActivityTitleRelations(long globalUnitId, long currentPhaseId);

  /**
   * This method gets activities by a given activity composedID and phase.
   * 
   * @param composedID is the composed identifier.
   * @param phaseId is the phase identifier.
   * @return a list from Activity null if no exist records
   */
  public List<Activity> getActivitiesByComposedID(String composedID, long phaseId);

  /**
   * This method gets activities by a given activity composedID and phase.
   * 
   * @param composedID is the composed identifier.
   * @param phaseId is the phase identifier.
   * @param projectID is the project identifier.
   * @return a list from Activity null if no exist records
   */
  List<Activity> getActivitiesByComposedIDPhaseIDProjectID(String composedID, long phaseId, long projectId);

  /**
   * This method obtains the number of existing activities, by deliverable and phase
   * 
   * @param deliverableId deliverable identifier.
   * @param phaseId phase identifier.
   * @return number of existing activities
   */
  int getActivitiesByDeliverableAndPhaseQuantity(long deliverableId, long phaseId);


  /**
   * This method gets a activity object by a given activity identifier.
   * 
   * @param projectID is the project/cluster identifier.
   * @return a list from Activity null if no exist records
   */
  public List<Activity> getActivitiesByProject(long projectId, long phaseId);


  /**
   * get quantity of activities by project and user
   * 
   * @author IBD
   * @param phase phase of the project
   * @param projectId project id
   * @param projectPersonId projectPerson id
   * @return quantity of activities
   */

  int getActivitiesByProjectAndUserQuantity(long projectId, long phaseId, long projectPersonId);

  /**
   * This method gets a activity object by a given activity identifier.
   * 
   * @param activityID is the activity identifier.
   * @return a Activity object.
   */
  public Activity getActivityById(long activityID);

  /**
   * This method saves the information of the given activity
   * 
   * @param activity - is the activity object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the activity was
   *         updated
   *         or -1 is some error occurred.
   */
  public Activity saveActivity(Activity activity);

}
