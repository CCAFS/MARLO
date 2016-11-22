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


package org.cgiar.ccafs.marlo.action.json.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImpactPathwayFullGraph extends BaseAction {

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ImpactPathwayFullGraph.class);
  /**
   * 
   */
  private static final long serialVersionUID = 971011588781935964L;
  long crpID;
  @Inject
  private CrpManager crpManager;
  private HashMap<String, Object> elements;


  @Inject
  public ImpactPathwayFullGraph(APConfig config) {
    super(config);

  }

  @Override
  public String execute() throws Exception {
    Crp crp = crpManager.getCrpById(crpID);
    elements = new HashMap<>();

    List<HashMap<String, Object>> dataNodes = new ArrayList<HashMap<String, Object>>();
    List<HashMap<String, Object>> dataEdges = new ArrayList<HashMap<String, Object>>();


    for (CrpProgram crpProgram : crp.getCrpPrograms().stream()
      .filter(c -> c.isActive() && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList())) {
      HashMap<String, Object> data = new HashMap<>();
      HashMap<String, Object> dataProgram = new HashMap<>();
      dataProgram.put("id", crpProgram.getAcronym());
      dataProgram.put("label", crpProgram.getAcronym());
      dataProgram.put("description", crpProgram.getName());
      dataProgram.put("color", crpProgram.getColor());
      dataProgram.put("type", "F");
      data.put("data", dataProgram);
      dataNodes.add(data);

      int i = 1;
      for (CrpProgramOutcome crpProgramOutcome : crpProgram.getCrpProgramOutcomes().stream().filter(c -> c.isActive())
        .collect(Collectors.toList())) {
        HashMap<String, Object> dataOutcome = new HashMap<>();
        HashMap<String, Object> dataDetailOutcome = new HashMap<>();
        HashMap<String, Object> dataEdgeOutcome = new HashMap<>();
        HashMap<String, Object> dataEdgeDetailOutcome = new HashMap<>();
        dataDetailOutcome.put("id", "O" + crpProgramOutcome.getId());
        dataDetailOutcome.put("label", "Outcome #" + i);
        dataDetailOutcome.put("description", crpProgramOutcome.getDescription());
        dataDetailOutcome.put("color", crpProgramOutcome.getCrpProgram().getColor());
        dataDetailOutcome.put("type", "O");
        dataOutcome.put("data", dataDetailOutcome);
        dataEdgeDetailOutcome.put("source", crpProgram.getAcronym());
        dataEdgeDetailOutcome.put("target", "O" + crpProgramOutcome.getId());
        dataEdgeOutcome.put("data", dataEdgeDetailOutcome);
        dataEdges.add(dataEdgeOutcome);


        for (CrpClusterKeyOutputOutcome keyOutputOutcome : crpProgramOutcome.getCrpClusterKeyOutputOutcomes().stream()
          .filter(koo -> koo.isActive()).collect(Collectors.toList())) {
          HashMap<String, Object> dataEdgeKeyOoutput = new HashMap<>();
          HashMap<String, Object> dataDetailKeyOutput = new HashMap<>();
          dataDetailKeyOutput.put("source", "O" + crpProgramOutcome.getId());
          dataDetailKeyOutput.put("target", "KO" + keyOutputOutcome.getCrpClusterKeyOutput().getId());
          dataEdgeKeyOoutput.put("data", dataDetailKeyOutput);
          if (keyOutputOutcome.getCrpClusterKeyOutput().isActive()) {

            dataEdges.add(dataEdgeKeyOoutput);
          }
        }

        dataNodes.add(dataOutcome);


        i++;
      }

      int i1 = 1;
      int j = 1;
      for (CrpClusterOfActivity crpClusterOfActivity : crpProgram.getCrpClusterOfActivities().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList())) {
        HashMap<String, Object> dataOutcome = new HashMap<>();
        HashMap<String, Object> dataDetailOutcome = new HashMap<>();
        dataDetailOutcome.put("id", "C" + crpClusterOfActivity.getId());
        dataDetailOutcome.put("label", "CoA #" + i1);
        dataDetailOutcome.put("description", crpClusterOfActivity.getComposedName());
        dataDetailOutcome.put("color", "#c0c0c0");
        dataDetailOutcome.put("type", "CoA");
        dataOutcome.put("data", dataDetailOutcome);
        dataNodes.add(dataOutcome);

        for (CrpClusterKeyOutput keyOutput : crpClusterOfActivity.getCrpClusterKeyOutputs().stream()
          .filter(ko -> ko.isActive()).collect(Collectors.toList())) {
          HashMap<String, Object> dataKeyOutput = new HashMap<>();
          HashMap<String, Object> dataDetailKeyOutput = new HashMap<>();
          dataDetailKeyOutput.put("id", "KO" + keyOutput.getId());
          dataDetailKeyOutput.put("parent", "C" + crpClusterOfActivity.getId());
          dataDetailKeyOutput.put("label", "KeyOutput #" + j);
          dataDetailKeyOutput.put("description", keyOutput.getKeyOutput());
          dataDetailKeyOutput.put("color", crpClusterOfActivity.getCrpProgram().getColor());
          dataDetailKeyOutput.put("type", "KO");
          j++;
          dataKeyOutput.put("data", dataDetailKeyOutput);
          dataNodes.add(dataKeyOutput);
        }


        i1++;
      }
    }
    elements.put("nodes", dataNodes);
    elements.put("edges", dataEdges);
    return SUCCESS;
  }


  public HashMap<String, Object> getElements() {
    return elements;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    // Validating parameters.

    crpID = -1;

    try {
      crpID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.CRP_ID))[0]));
    } catch (Exception e) {
      LOG.error("There was an exception trying to parse the crp  id = {} ",
        StringUtils.trim(((String[]) parameters.get(APConstants.CRP_ID))[0]));

    }
  }

  public void setElements(HashMap<String, Object> elements) {
    this.elements = elements;
  }
}
