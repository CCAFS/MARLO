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
import org.cgiar.ccafs.marlo.data.model.ActivityTitle;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
  /** Clusters using each activity title, keyed by activity title id. Loaded once per request, on first use. */
  private Map<Long, List<ActivityTitleRelation>> relationsByTitle;

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
   * Gets the logical activities that are using the given activity title, one per cluster(project) and composedId,
   * with no phase or year restriction. The whole page is resolved with a single query the first time this is called.
   * This method is read only: it reports the relation, it does not decide whether the activity title can be
   * deleted. That rule lives in BaseAction.canBeDeleted(long, String).
   * 
   * @param activityTitleID is the activity title identifier.
   * @return a list of relations ordered by cluster, empty when no cluster is using the activity title.
   */
  public List<ActivityTitleRelation> getActivityTitleRelations(long activityTitleID) {
    if (relationsByTitle == null) {
      relationsByTitle = this.loadActivityTitleRelations();
    }
    List<ActivityTitleRelation> relations = relationsByTitle.get(activityTitleID);
    if (relations == null) {
      return Collections.emptyList();
    }
    return relations;
  }

  /**
   * Collapses the projected relation rows into one entry per cluster and composedId. Activities replicate forward,
   * so the rows of a group only differ by phase: the group keeps every phase it appears in, in chronological order,
   * plus the description and status of the phase the user is looking at.
   * Package private and static so the grouping can be unit tested without a database.
   * 
   * @param rows the projected rows, ordered by cluster, composedId and phase.
   * @param currentPhaseId the phase the user is looking at.
   * @return the relations keyed by activity title id.
   */
  static Map<Long, List<ActivityTitleRelation>> groupRelations(List<Map<String, Object>> rows, long currentPhaseId) {
    Map<Long, List<ActivityTitleRelation>> result = new LinkedHashMap<>();
    if (rows == null) {
      return result;
    }

    Map<String, ActivityTitleRelation> groups = new LinkedHashMap<>();
    for (Map<String, Object> row : rows) {
      if (row == null) {
        continue;
      }
      Long titleId = asLong(row.get("titleId"));
      Long clusterId = asLong(row.get("clusterId"));
      if (titleId == null || clusterId == null) {
        continue;
      }
      String composedId = asString(row.get("composedId"));
      // Legacy records may have no composedId: treat each one as its own activity
      String activityId = asString(row.get("activityId"));
      String groupKey =
        titleId + "|" + clusterId + "|" + (composedId.isEmpty() ? "activity-" + activityId : composedId);

      // Space, not a dash: the label is composed again when the view lists the phases
      String phaseLabel = (asString(row.get("phaseName")) + " " + asString(row.get("phaseYear"))).trim();
      Long rowPhaseId = asLong(row.get("phaseId"));
      boolean isCurrentPhase = rowPhaseId != null && rowPhaseId.longValue() == currentPhaseId;
      String description = asString(row.get("activityDescription"));

      ActivityTitleRelation relation = groups.get(groupKey);
      if (relation == null) {
        relation = new ActivityTitleRelation();
        relation.setClusterId(clusterId.longValue());
        relation.setClusterTitle(asString(row.get("clusterTitle")));
        relation.setComposedId(composedId);
        groups.put(groupKey, relation);
        result.computeIfAbsent(titleId, key -> new ArrayList<>()).add(relation);
      }
      // Rows arrive in chronological order, so the list keeps that order
      relation.addPhaseLabel(phaseLabel);
      // The description of the phase being looked at wins, otherwise the latest one seen
      if (isCurrentPhase || relation.getActivityDescription() == null
        || relation.getActivityDescription().isEmpty()) {
        relation.setActivityDescription(description);
      }
      if (isCurrentPhase) {
        relation.setReportedInCurrentPhase(asBoolean(row.get("activityActive")));
      }
    }
    return result;
  }

  private static boolean asBoolean(Object value) {
    if (value == null) {
      return false;
    }
    if (value instanceof Boolean) {
      return ((Boolean) value).booleanValue();
    }
    if (value instanceof Number) {
      return ((Number) value).intValue() != 0;
    }
    return "1".equals(value.toString()) || "true".equalsIgnoreCase(value.toString());
  }

  private static Long asLong(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Number) {
      return Long.valueOf(((Number) value).longValue());
    }
    try {
      return Long.valueOf(value.toString().trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private static String asString(Object value) {
    if (value == null) {
      return "";
    }
    return value.toString().trim();
  }

  /**
   * Runs the relation query for the current global unit and phase, and groups the result.
   * 
   * @return the relations of every activity title of the current global unit, keyed by activity title id.
   */
  private Map<Long, List<ActivityTitleRelation>> loadActivityTitleRelations() {
    try {
      GlobalUnit globalUnit = this.getCurrentGlobalUnit();
      Phase phase = this.getActualPhase();
      if (globalUnit == null || globalUnit.getId() == null || phase == null) {
        return new LinkedHashMap<>();
      }
      return groupRelations(activityManager.getActivityTitleRelations(globalUnit.getId(), phase.getId()),
        phase.getId().longValue());
    } catch (Exception e) {
      LOG.error("Error getting the clusters related to the activity titles", e);
      return new LinkedHashMap<>();
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
