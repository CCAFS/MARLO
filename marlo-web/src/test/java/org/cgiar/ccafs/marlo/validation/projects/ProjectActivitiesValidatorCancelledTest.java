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

package org.cgiar.ccafs.marlo.validation.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.model.Activity;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * A cancelled activity only owes the justification of its cancellation, and only in the reporting cycle.
 */
public class ProjectActivitiesValidatorCancelledTest {

  private final ProjectActivitiesValidator validator = new ProjectActivitiesValidator(null);

  private static BaseAction action(boolean reporting) {
    BaseAction action = new BaseAction() {

      private static final long serialVersionUID = 1L;

      @Override
      public String getText(String aTextName, List<?> args) {
        return aTextName.replace("activity.", "Activity " + args.get(0) + " ");
      }

      @Override
      public boolean isReportingActive() {
        return reporting;
      }
    };
    action.setInvalidFields(new HashMap<>());
    return action;
  }

  private static Activity emptyActivity(String progress) {
    Activity activity = new Activity();
    activity.setId(Long.valueOf(25819L));
    activity.setActivityStatus(Integer.valueOf(5));
    activity.setActivityProgress(progress);
    return activity;
  }

  private String missingFields(Activity activity, boolean reporting) {
    BaseAction action = action(reporting);
    this.validator.validateCancelledActivity(activity, 0, "projectActivities", action);
    return action.getMissingFields().toString();
  }

  @Test
  public void aCancelledActivityWithItsJustificationIsComplete() {
    assertEquals("", this.missingFields(emptyActivity("The partner left the country."), true));
  }

  @Test
  public void aCancelledActivityWithoutJustificationMissesOnlyThatInReporting() {
    assertEquals("Activity 25819 statusProgress", this.missingFields(emptyActivity(""), true));
  }

  @Test
  public void aCancelledActivityOwesNothingInPlanning() {
    assertEquals("", this.missingFields(emptyActivity(null), false));
  }

  @Test
  public void aJustificationOverTheWordLimitIsMissing() {
    StringBuilder text = new StringBuilder();
    for (int i = 0; i < 151; i++) {
      text.append("word ");
    }
    assertEquals("Activity 25819 statusProgress", this.missingFields(emptyActivity(text.toString()), true));
  }
}
