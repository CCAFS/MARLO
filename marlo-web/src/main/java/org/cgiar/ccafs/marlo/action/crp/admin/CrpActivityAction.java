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


package org.cgiar.ccafs.marlo.action.crp.admin;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.ActivityManager;
import org.cgiar.ccafs.marlo.data.manager.ActivityTitleManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.ActivityTitle;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CrpActivityAction extends BaseAction {

  private static final long serialVersionUID = 3355662668874414548L;

  private static final Logger LOG = LoggerFactory.getLogger(CrpActivityAction.class);


  /**
   * Helper method to read a stream into memory.
   * 
   * @param stream
   * @return
   * @throws IOException
   */
  public static byte[] readFully(InputStream stream) throws IOException {
    byte[] buffer = new byte[8192];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    int bytesRead;
    while ((bytesRead = stream.read(buffer)) != -1) {
      baos.write(buffer, 0, bytesRead);
    }
    return baos.toByteArray();
  }

  // GlobalUnit Manager
  private ActivityTitleManager activityTitleManager;
  private ActivityManager activityManager;
  private List<ActivityTitle> activities;

  @Inject
  public CrpActivityAction(APConfig config, ActivityTitleManager activityTitleManager,
    ActivityManager activityManager) {
    super(config);
    this.activityTitleManager = activityTitleManager;
    this.activityManager = activityManager;
  }

  public List<ActivityTitle> getActivities() {
    return activities;
  }

  /**
   * Gets every activity linked to the given activity title, with no phase, year or status restriction. The
   * activities are returned ordered by cluster(project), and each one carries its own cluster, phase and active
   * flag, so the view can show which clusters are using the activity title.
   * This method is read only: it reports the relation, it does not decide whether the activity title can be
   * deleted. That rule lives in BaseAction.canBeDeleted(long, String).
   * 
   * @param activityTitleID is the activity title identifier.
   * @return a list of Activity, empty when no cluster is using the activity title.
   */
  public List<Activity> getActivityTitleRelations(long activityTitleID) {
    try {
      List<Activity> relations = activityManager.getActivitiesByActivityTitle(activityTitleID);
      if (relations == null) {
        return new ArrayList<>();
      }
      return relations;
    } catch (Exception e) {
      LOG.error("Error getting the clusters related to the activity title " + activityTitleID, e);
      return new ArrayList<>();
    }
  }

  @Override
  public void prepare() throws Exception {
    if (!this.isHttpPost()) {
      GlobalUnit globalUnit = this.getCurrentGlobalUnit();
      activities = activityTitleManager.findByGlobalUnit(globalUnit.getId());
      if (activities == null) {
        activities = new ArrayList<>();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {

      this.saveActivities();

      Collection<String> messages = this.getActionMessages();
      if (this.getInvalidFields()!= null && !this.getInvalidFields().isEmpty()) {

        this.setActionMessages(null);
        // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
        List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());

        for (String key : keys) {

          this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
        }


        // this.addActionWarning(this.getText("saving.saved") + Arrays.toString(this.getInvalidFields().toArray()));
      } else {
        this.addActionMessage("message:" + this.getText("saving.saved"));
      }
      messages = this.getActionMessages();
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }

  }

  private void saveActivities() {
    GlobalUnit globalUnit = this.getCurrentGlobalUnit();
    List<ActivityTitle> activitiesDB = activityTitleManager.findByGlobalUnit(globalUnit.getId());
    if (activitiesDB == null) {
      activitiesDB = new ArrayList<>();
    }

    Set<Long> submittedIds = new HashSet<>();
    if (activities != null) {
      for (ActivityTitle activity : activities) {
        if (activity != null && activity.getId() != null && activity.getId() > 0) {
          submittedIds.add(activity.getId());
        }
      }
    }

    for (ActivityTitle activityDB : activitiesDB) {
      if (activityDB.getId() != null && !submittedIds.contains(activityDB.getId())) {
        activityTitleManager.deleteActivityTitle(activityDB.getId());
      }
    }

    if (activities == null || activities.isEmpty()) {
      return;
    }

    for (ActivityTitle activity : activities) {
      if (activity == null) {
        continue;
      }
      String title = activity.getTitle();
      if (title != null) {
        title = title.trim();
      }
      if (title == null || title.isEmpty()) {
        continue;
      }

      if (activity.getId() != null && activity.getId() > 0) {
        ActivityTitle activityDB = activityTitleManager.getActivityTitleById(activity.getId());
        if (activityDB != null) {
          activityDB.setTitle(title);
          activityDB.setGlobalUnit(this.getCurrentGlobalUnit());
          activityTitleManager.saveActivityTitle(activityDB);
        }
      } else {
        ActivityTitle activityNew = new ActivityTitle();
        activityNew.setTitle(title);
        activityNew.setGlobalUnit(this.getCurrentGlobalUnit());
        activityTitleManager.saveActivityTitle(activityNew);
      }
    }
  }

  public void setActivities(List<ActivityTitle> activities) {
    this.activities = activities;
  }

  @Override
  public void validate() {
    if (save) {
      HashMap<String, String> error = new HashMap<>();

      if (activities != null && !activities.isEmpty()) {
        int index = 0;
        for (ActivityTitle activity : activities) {
          if (activity != null && activity.getTitle() != null && activity.getTitle().trim().isEmpty()) {
            error.put("input-activities[" + index + "].title",
              this.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Activities"}));
          }
          index++;
        }
        this.setInvalidFields(error);
      }
    }
  }
}
