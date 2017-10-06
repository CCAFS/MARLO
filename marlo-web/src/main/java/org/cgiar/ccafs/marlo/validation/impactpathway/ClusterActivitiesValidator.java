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
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.SectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.inject.Inject;

public class ClusterActivitiesValidator extends BaseValidator {

  @Inject
  public ClusterActivitiesValidator() {
  }

  private Path getAutoSaveFilePath(CrpProgram program) {
    String composedClassName = program.getClass().getSimpleName();
    String actionFile = SectionStatusEnum.CLUSTERACTIVITES.getStatus().replace("/", "_");
    String autoSaveFile = program.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public void validate(BaseAction action, List<CrpClusterOfActivity> activities, CrpProgram program, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    if (!saving) {
      Path path = this.getAutoSaveFilePath(program);

      if (path.toFile().exists()) {
        this.addMissingField("program.activites.draft");
      }
    }


    if (activities.size() == 0) {
      this.addMissingField("program.activites");
      action.getInvalidFields().put("list-clusterofActivities",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Cluster of Activities"}));
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
    this.saveMissingFieldsImpactPathway(program, "clusterActivities", action.getActualPhase().getYear(),
      action.getActualPhase().getDescription());
  }

  public void validateClusterOfActivity(BaseAction action, CrpClusterOfActivity activity, int i) {
    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));
    if (!(this.isValidString(activity.getDescription()) && this.wordCount(activity.getDescription()) <= 20)) {
      this.addMessage(action.getText("outcome.action.cluster.descritpion.required", params));
      action.getInvalidFields().put("input-clusterofActivities[" + i + "].description",
        InvalidFieldsMessages.EMPTYFIELD);

    }

    if (!(this.isValidString(activity.getIdentifier()) && this.wordCount(activity.getIdentifier()) <= 20)) {
      this.addMessage(action.getText("outcome.action.cluster.descritpion.required", params));
      action.getInvalidFields().put("input-clusterofActivities[" + i + "].identifier",
        InvalidFieldsMessages.EMPTYFIELD);

    }

    if (!action.hasSpecificities(APConstants.CRP_CLUSTER_LEADER)) {
      if (activity.getLeaders() == null || activity.getLeaders().isEmpty()) {
        this.addMessage(action.getText("outcome.action.cluster.leader.required", params));
        action.getInvalidFields().put("list-clusterofActivities[" + i + "].leaders",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Cluster of Activities Leaders"}));
      }
    }

    if (activity.getKeyOutputs() == null || activity.getKeyOutputs().isEmpty()) {
      this.addMessage(action.getText("outcome.action.cluster.key.required", params));

      action.getInvalidFields().put("list-clusterofActivities[" + i + "].keyOutputs",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Key Outputs"}));
    } else {
      int j = 0;
      for (CrpClusterKeyOutput crpClusterKeyOutput : activity.getKeyOutputs()) {
        List<String> paramsOutcomes = new ArrayList<String>();
        paramsOutcomes.add(String.valueOf(i + 1));
        paramsOutcomes.add(String.valueOf(j));

        if (!this.isValidString(crpClusterKeyOutput.getKeyOutput())) {
          this.addMessage(action.getText("outcome.action.cluster.key.statment", params));
          action.getInvalidFields().put("input-clusterofActivities[" + i + "].keyOutputs[" + j + "].keyOutput",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        if (crpClusterKeyOutput.getKeyOutputOutcomes() == null
          || crpClusterKeyOutput.getKeyOutputOutcomes().isEmpty()) {
          this.addMessage(action.getText("outcome.action.cluster.key.outcomes.required", paramsOutcomes));
          action.getInvalidFields().put("list-clusterofActivities[" + i + "].keyOutputs[" + j + "].keyOutputOutcomes",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Key Output Outcomes"}));

        }

        j++;
      }
    }
  }

}