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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.SrfSloIdo;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
  private CrpProgramOutcomeManager crpProgramOutcomeManager;

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
      HashMap<String, Object> dataSubIdos = new HashMap<>();
      HashMap<String, Object> dataIdos = new HashMap<>();
      HashMap<String, Object> dataSlos = new HashMap<>();

      dataProgram.put("id", "F" + crpProgram.getId());
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
        dataDetailOutcome.put("parent", crpProgram.getAcronym());
        dataDetailOutcome.put("type", "O");
        dataOutcome.put("data", dataDetailOutcome);
        // dataEdgeDetailOutcome.put("source", crpProgram.getAcronym());
        // dataEdgeDetailOutcome.put("target", "O" + crpProgramOutcome.getId());

        dataEdgeOutcome.put("data", dataEdgeDetailOutcome);


        CrpProgramOutcome crpProgramOutcomeDB =
          crpProgramOutcomeManager.getCrpProgramOutcomeById(crpProgramOutcome.getId());
        for (CrpOutcomeSubIdo crpOutcomeSubIdo : crpProgramOutcomeDB.getCrpOutcomeSubIdos().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {


          if (crpOutcomeSubIdo.getSrfSubIdo() != null && crpOutcomeSubIdo.getSrfSubIdo().isActive()) {
            HashMap<String, Object> dataDetaiSubIDO = new HashMap<>();
            dataDetaiSubIDO.put("id", "SD" + crpOutcomeSubIdo.getSrfSubIdo().getId());
            dataDetaiSubIDO.put("label", "SubIDO #" + crpOutcomeSubIdo.getSrfSubIdo().getId());
            dataDetaiSubIDO.put("description", crpOutcomeSubIdo.getSrfSubIdo().getDescription());

            dataDetaiSubIDO.put("type", "SD");

            dataSubIdos.put("data", dataDetaiSubIDO);


            HashMap<String, Object> dataDetaiSIDO = new HashMap<>();
            dataDetaiSIDO.put("id", "IDO" + crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId());
            dataDetaiSIDO.put("label", "IDO #" + crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId());
            dataDetaiSIDO.put("description", crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getDescription());

            dataDetaiSIDO.put("type", "IDO");

            dataIdos.put("data", dataDetaiSIDO);


            for (SrfSloIdo srfSloIdo : crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getSrfSloIdos()) {

              HashMap<String, Object> dataDetaiSlo = new HashMap<>();
              dataDetaiSlo.put("id", "SLO" + srfSloIdo.getSrfSlo().getId());
              dataDetaiSlo.put("label", "SLO #" + srfSloIdo.getSrfSlo().getId());
              dataDetaiSlo.put("description", srfSloIdo.getSrfSlo().getDescription());

              dataDetaiSlo.put("type", "SLO");

              dataSlos.put("data", dataDetaiSlo);

              HashMap<String, Object> dataEdgeDetailIDO = new HashMap<>();
              dataEdgeDetailIDO.put("target", "IDO" + srfSloIdo.getSrfIdo().getId());
              dataEdgeDetailIDO.put("source", "SLO" + srfSloIdo.getSrfSlo().getId());
              HashMap<String, Object> dataEdgeIDO = new HashMap<>();

              dataEdgeIDO.put("data", dataEdgeDetailIDO);
              dataEdges.add(dataEdgeIDO);
            }


            HashMap<String, Object> dataEdgeDetailOutcomeAux = new HashMap<>();
            dataEdgeDetailOutcomeAux.put("target", "O" + crpProgramOutcome.getId());
            dataEdgeDetailOutcomeAux.put("source", "SD" + crpOutcomeSubIdo.getSrfSubIdo().getId());
            HashMap<String, Object> dataEdgeKeyOoutput = new HashMap<>();


            HashMap<String, Object> dataEdgeDetailIDO = new HashMap<>();
            dataEdgeDetailIDO.put("target", "SD" + crpOutcomeSubIdo.getSrfSubIdo().getId());
            dataEdgeDetailIDO.put("source", "IDO" + crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId());
            HashMap<String, Object> dataEdgeIDO = new HashMap<>();

            dataEdgeKeyOoutput.put("data", dataEdgeDetailOutcomeAux);
            dataEdges.add(dataEdgeKeyOoutput);

            dataEdgeIDO.put("data", dataEdgeDetailIDO);
            dataEdges.add(dataEdgeIDO);
          }


        }
        // dataEdges.add(dataEdgeOutcome);


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

        if (dataOutcome.containsKey("data")) {
          dataNodes.add(dataOutcome);
        }
        if (dataSubIdos.containsKey("data")) {
          dataNodes.add(dataSubIdos);
        }

        if (dataIdos.containsKey("data")) {
          dataNodes.add(dataIdos);
        }

        if (dataSlos.containsKey("data")) {
          dataNodes.add(dataSlos);
        }


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
        dataDetailOutcome.put("parent", crpProgram.getAcronym());
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

    Set<HashMap<String, Object>> foo = new HashSet<HashMap<String, Object>>(dataEdges);

    dataEdges.clear();

    dataEdges.addAll(foo);
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
