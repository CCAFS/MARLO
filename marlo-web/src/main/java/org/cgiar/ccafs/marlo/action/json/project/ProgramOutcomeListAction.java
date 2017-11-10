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

package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProgramOutcomeListAction extends BaseAction {


  private static final long serialVersionUID = -5595055892247130791L;


  private CrpProgramManager crpProgramManager;

  private List<Map<String, Object>> programOutcomes;


  private List<Map<String, Object>> clusterOfActivities;

  private Long crpProgramId;

  @Inject
  public ProgramOutcomeListAction(APConfig config, CrpProgramManager crpProgramManager) {
    super(config);
    this.crpProgramManager = crpProgramManager;
  }

  @Override
  public String execute() throws Exception {
    programOutcomes = new ArrayList<Map<String, Object>>();
    clusterOfActivities = new ArrayList<Map<String, Object>>();
    Map<String, Object> programOutcome;
    Map<String, Object> clusterOfActivity;

    CrpProgram program = crpProgramManager.getCrpProgramById(crpProgramId);

    if (program != null) {
      if (program.getCrpProgramOutcomes().stream().filter(po -> po.isActive()).collect(Collectors.toList()) != null) {
        for (CrpProgramOutcome outcome : program.getCrpProgramOutcomes().stream().filter(po -> po.isActive())
          .collect(Collectors.toList())) {
          if (outcome != null) {
            programOutcome = new HashMap<String, Object>();
            programOutcome.put("id", outcome.getId());
            programOutcome.put("description", outcome.getDescription());
            programOutcomes.add(programOutcome);
          }
        }
      }

      if (program.getCrpClusterOfActivities().stream().filter(ca -> ca.isActive())
        .collect(Collectors.toList()) != null) {
        for (CrpClusterOfActivity activity : program.getCrpClusterOfActivities().stream().filter(ca -> ca.isActive())
          .collect(Collectors.toList())) {
          if (activity != null) {
            clusterOfActivity = new HashMap<String, Object>();
            clusterOfActivity.put("id", activity.getId());
            clusterOfActivity.put("description", activity.getComposedName());
            clusterOfActivities.add(clusterOfActivity);
          }

        }
      }
    }

    return SUCCESS;
  }

  public List<Map<String, Object>> getClusterOfActivities() {
    return clusterOfActivities;
  }

  public List<Map<String, Object>> getProgramOutcomes() {
    return programOutcomes;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    // crpProgramId = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.CRP_PROGRAM_ID))[0]));

    Map<String, Parameter> parameters = this.getParameters();
    crpProgramId = Long.parseLong(StringUtils.trim(parameters.get(APConstants.CRP_PROGRAM_ID).getMultipleValues()[0]));
  }

  public void setClusterOfActivities(List<Map<String, Object>> clusterOfActivities) {
    this.clusterOfActivities = clusterOfActivities;
  }

  public void setProgramOutcomes(List<Map<String, Object>> programOutcomes) {
    this.programOutcomes = programOutcomes;
  }

}
