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


package org.cgiar.ccafs.marlo.validation.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

public class ClusterActivitiesValidator extends BaseValidator {

  @Inject
  public ClusterActivitiesValidator() {
  }

  public void validate(BaseAction action, List<CrpClusterOfActivity> activities, CrpProgram program) {
    if (activities.size() == 0) {
      this.addMissingField("program.activites");
    }

    for (int i = 0; i < activities.size(); i++) {
      CrpClusterOfActivity outcome = activities.get(i);
      this.validateClusterOfActivity(action, outcome, i);
    }
    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (validationMessage.length() > 0) {
      action
        .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
    }
    this.saveMissingFieldsImpactPathway(program, "clusterActivities");
  }

  public void validateClusterOfActivity(BaseAction action, CrpClusterOfActivity activity, int i) {
    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));
    if (!(this.isValidString(activity.getDescription()) && this.wordCount(activity.getDescription()) <= 100)) {
      this.addMessage(action.getText("outcome.action.cluster.descritpion.required", params));
    }
  }

}