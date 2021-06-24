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
import org.cgiar.ccafs.marlo.data.manager.ActivityTitleManager;
import org.cgiar.ccafs.marlo.data.model.ActivityTitle;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;


public class CrpActivityAction extends BaseAction {

  private static final long serialVersionUID = 3355662668874414548L;


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
  private List<ActivityTitle> activities;

  @Inject
  public CrpActivityAction(APConfig config, ActivityTitleManager activityTitleManager) {
    super(config);
    this.activityTitleManager = activityTitleManager;
  }

  public List<ActivityTitle> getActivities() {
    return activities;
  }

  @Override
  public void prepare() throws Exception {
    activities = activityTitleManager.findAll();
    if (this.isHttpPost()) {

      if (activities != null) {
        activities.clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {

      this.saveActivities();

      Collection<String> messages = this.getActionMessages();
      if (!this.getInvalidFields().isEmpty()) {

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
    // Delete activity
    List<ActivityTitle> activitiesDB = new ArrayList<>();
    activitiesDB = activityTitleManager.findAll();
    if (activitiesDB != null && !activitiesDB.isEmpty()) {
      for (ActivityTitle activityDB : activitiesDB) {
        if (activities != null && !activities.isEmpty() && !activities.contains(activityDB)) {
          activityTitleManager.deleteActivityTitle(activityDB.getId());
        }
      }
    }

    // Update activity
    if (activities != null && !activities.isEmpty()) {
      for (ActivityTitle activity : activities) {
        if (activity != null) {
          if (activity.getId() != null) {

            ActivityTitle activityDB = new ActivityTitle();
            activityDB = activityTitleManager.getActivityTitleById(activity.getId());
            if (activityDB != null) {
              if (activity.getTitle() != null && !activity.getTitle().isEmpty()) {
                activityTitleManager.saveActivityTitle(activity);
              }
            }

          } else {
            // Create new activity
            /*
             * ActivityTitle activityNew = new ActivityTitle();
             * if (activity.getTitle() != null && activity.getTitle().isEmpty()) {
             * activityNew.setTitle(activity.getTitle());
             * }
             * activityTitleManager.saveActivityTitle(activityNew);
             */
          }
        }
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

      if (activities != null || !activities.isEmpty()) {
        int index = 0;
        for (ActivityTitle activity : activities) {
          if (activity != null && (activity.getTitle() != null && activity.getTitle().isEmpty())) {
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
