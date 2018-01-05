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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ActivityDAO;
import org.cgiar.ccafs.marlo.data.manager.ActivityManager;
import org.cgiar.ccafs.marlo.data.model.Activity;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class ActivityManagerImpl implements ActivityManager {


  private ActivityDAO activityDAO;
  // Managers


  @Inject
  public ActivityManagerImpl(ActivityDAO activityDAO) {
    this.activityDAO = activityDAO;


  }

  @Override
  public void deleteActivity(long activityId) {

    activityDAO.deleteActivity(activityId);
  }

  @Override
  public boolean existActivity(long activityID) {

    return activityDAO.existActivity(activityID);
  }

  @Override
  public List<Activity> findAll() {

    return activityDAO.findAll();

  }

  @Override
  public Activity getActivityById(long activityID) {

    return activityDAO.find(activityID);
  }

  @Override
  public Activity saveActivity(Activity activity) {

    return activityDAO.save(activity);
  }


}
