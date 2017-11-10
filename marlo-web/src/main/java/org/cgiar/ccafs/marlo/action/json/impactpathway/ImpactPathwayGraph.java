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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.SrfIdoManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.SrfIdo;
import org.cgiar.ccafs.marlo.data.model.SrfSlo;
import org.cgiar.ccafs.marlo.data.model.SrfSloIdo;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImpactPathwayGraph extends BaseAction {

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ImpactPathwayGraph.class);
  /**
   * 
   */
  private static final long serialVersionUID = 971011588781935964L;
  long crpProgramID;

  private CrpProgramManager crpProgramManager;

  private SrfSubIdoManager srfSubIdoManager;

  private SrfIdoManager srfIdoManager;

  private CrpProgramOutcomeManager crpProgramOutcomeManager;

  private HashMap<String, Object> elements;
  private String sectionName;

  @Inject
  public ImpactPathwayGraph(APConfig config, CrpProgramManager crpProgramManager, SrfSubIdoManager srfSubIdoManager,
    SrfIdoManager srfIdoManager, CrpProgramOutcomeManager crpProgramOutcomeManager) {
    super(config);
    this.crpProgramManager = crpProgramManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.srfIdoManager = srfIdoManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
  }

  @Override
  public String execute() throws Exception {
    CrpProgram crpProgram = crpProgramManager.getCrpProgramById(crpProgramID);
    elements = new HashMap<>();
    Set<SrfSlo> slos = new HashSet<>();
    Set<SrfSubIdo> subIdos = new HashSet<>();
    Set<SrfIdo> srfIdos = new HashSet<>();
    List<HashMap<String, Object>> dataNodes = new ArrayList<HashMap<String, Object>>();
    List<HashMap<String, Object>> dataEdges = new ArrayList<HashMap<String, Object>>();
    HashMap<String, Object> data = new HashMap<>();
    HashMap<String, Object> dataProgram = new HashMap<>();
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
      HashMap<String, Object> dataSubIdos = new HashMap<>();
      HashMap<String, Object> dataIdos = new HashMap<>();
      HashMap<String, Object> dataSlos = new HashMap<>();
      HashMap<String, Object> dataDetailOutcome = new HashMap<>();
      dataDetailOutcome.put("id", "O" + crpProgramOutcome.getId());
      dataDetailOutcome.put("label", "Outcome #" + i);
      dataDetailOutcome.put("description", crpProgramOutcome.getDescription());
      dataDetailOutcome.put("color", "FFF");
      dataDetailOutcome.put("type", "O");
      dataDetailOutcome.put("parent", "F" + crpProgram.getId());
      if (this.hasSpecificities(APConstants.CRP_IP_OUTCOME_INDICATOR)) {
        dataDetailOutcome.put("indicator", crpProgramOutcome.getIndicator());
      }

      dataOutcome.put("data", dataDetailOutcome);


      CrpProgramOutcome crpProgramOutcomeDB =
        crpProgramOutcomeManager.getCrpProgramOutcomeById(crpProgramOutcome.getId());
      for (CrpOutcomeSubIdo crpOutcomeSubIdo : crpProgramOutcomeDB.getCrpOutcomeSubIdos().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList())) {


        if (crpOutcomeSubIdo.getSrfSubIdo() != null && crpOutcomeSubIdo.getSrfSubIdo().isActive()) {
          dataSubIdos = new HashMap<>();
          HashMap<String, Object> dataDetaiSubIDO = new HashMap<>();
          dataDetaiSubIDO.put("id", "SD" + crpOutcomeSubIdo.getSrfSubIdo().getId());
          dataDetaiSubIDO.put("label", "Sub-IDO #" + crpOutcomeSubIdo.getSrfSubIdo().getId());
          dataDetaiSubIDO.put("description", crpOutcomeSubIdo.getSrfSubIdo().getDescription());

          dataDetaiSubIDO.put("type", "SD");

          dataSubIdos.put("data", dataDetaiSubIDO);


          if (!subIdos.contains(crpOutcomeSubIdo.getSrfSubIdo())) {
            dataNodes.add(dataSubIdos);
            subIdos.add(crpOutcomeSubIdo.getSrfSubIdo());
          }
          dataIdos = new HashMap<>();
          HashMap<String, Object> dataDetaiSIDO = new HashMap<>();
          dataDetaiSIDO.put("id", "IDO" + crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId());
          if (crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().isIsCrossCutting()) {
            dataDetaiSIDO.put("color", "#676b6d");
          }
          dataDetaiSIDO.put("label", "IDO #" + crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId());
          dataDetaiSIDO.put("description", crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getDescription());

          dataDetaiSIDO.put("type", "IDO");

          dataIdos.put("data", dataDetaiSIDO);

          SrfIdo srfIDODB = srfIdoManager.getSrfIdoById(crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId());
          if (!srfIdos.contains(srfIDODB)) {
            dataNodes.add(dataIdos);
            srfIdos.add(srfIDODB);
          }

          crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getSrfSloIdos().stream().filter(c -> c.isActive())
            .collect(Collectors.toList()).sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
          // crpOutcomeSubIdo = crpOutcomeSubIdoManager.getCrpOutcomeSubIdoById(crpOutcomeSubIdo.getId());
          for (SrfSloIdo srfSloIdo : crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getSrfSloIdos()) {
            dataSlos = new HashMap<>();
            HashMap<String, Object> dataDetaiSlo = new HashMap<>();
            dataDetaiSlo.put("id", "SLO" + srfSloIdo.getSrfSlo().getId());
            dataDetaiSlo.put("label", "SLO #" + srfSloIdo.getSrfSlo().getId());
            dataDetaiSlo.put("description", srfSloIdo.getSrfSlo().getDescription());
            dataDetaiSlo.put("type", "SLO");

            dataSlos.put("data", dataDetaiSlo);

            if (!slos.contains(srfSloIdo.getSrfSlo())) {
              dataNodes.add(dataSlos);
              slos.add(srfSloIdo.getSrfSlo());
            }


            HashMap<String, Object> dataEdgeDetailIDO = new HashMap<>();
            dataEdgeDetailIDO.put("target", "IDO" + srfSloIdo.getSrfIdo().getId());
            dataEdgeDetailIDO.put("source", "SLO" + srfSloIdo.getSrfSlo().getId());
            HashMap<String, Object> dataEdgeIDO = new HashMap<>();

            dataEdgeIDO.put("data", dataEdgeDetailIDO);
            dataEdges.add(dataEdgeIDO);
          }


          HashMap<String, Object> dataEdgeDetailOutcome = new HashMap<>();
          dataEdgeDetailOutcome.put("target", "O" + crpProgramOutcome.getId());
          dataEdgeDetailOutcome.put("source", "SD" + crpOutcomeSubIdo.getSrfSubIdo().getId());
          HashMap<String, Object> dataEdgeKeyOoutput = new HashMap<>();


          dataEdgeKeyOoutput.put("data", dataEdgeDetailOutcome);
          dataEdges.add(dataEdgeKeyOoutput);


        }


      }


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
      /*
       * if (dataSubIdos.containsKey("data")) {
       * dataNodes.add(dataSubIdos);
       * }
       */
      /*
       * if (dataIdos.containsKey("data")) {
       * dataNodes.add(dataIdos);
       * }
       */ /*
          * if (dataSlos.containsKey("data")) {
          * dataNodes.add(dataSlos);
          * }
          */

      i++;
    }

    int i1 = 1;
    int j = 1;
    for (CrpClusterOfActivity crpClusterOfActivity : crpProgram.getCrpClusterOfActivities().stream()
      .filter(c -> c.isActive()).collect(Collectors.toList())) {
      HashMap<String, Object> dataOutcome = new HashMap<>();
      HashMap<String, Object> dataDetailOutcome = new HashMap<>();
      dataDetailOutcome.put("id", "C" + crpClusterOfActivity.getId());
      dataDetailOutcome.put("label", crpClusterOfActivity.getIdentifier());
      dataDetailOutcome.put("description", crpClusterOfActivity.getComposedName());
      dataDetailOutcome.put("color", "#c0c0c0");
      dataDetailOutcome.put("type", "CoA");
      dataDetailOutcome.put("parent", "F" + crpProgram.getId());
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

    Set<HashMap<String, Object>> foo = new HashSet<HashMap<String, Object>>(dataEdges);

    dataEdges.clear();

    dataEdges.addAll(foo);
    Collections.sort(dataNodes, new Comparator<HashMap<String, Object>>() {

      @Override
      public int compare(HashMap<String, Object> one, HashMap<String, Object> two) {

        HashMap<String, Object> oneObject = (HashMap<String, Object>) one.get("data");
        HashMap<String, Object> twoObject = (HashMap<String, Object>) one.get("data");

        int compareTO = (oneObject.get("id").toString().compareTo(twoObject.get("id").toString()));
        return compareTO;
      }
    });


    for (SrfSubIdo subIdo : subIdos) {
      subIdo = srfSubIdoManager.getSrfSubIdoById(subIdo.getId());
      HashMap<String, Object> dataEdgeDetailIDO = new HashMap<>();
      dataEdgeDetailIDO.put("target", "SD" + subIdo.getId());
      dataEdgeDetailIDO.put("source", "IDO" + subIdo.getSrfIdo().getId());
      HashMap<String, Object> dataEdgeIDO = new HashMap<>();
      dataEdgeIDO.put("data", dataEdgeDetailIDO);
      dataEdges.add(dataEdgeIDO);
    }

    Collections.sort(dataEdges, new Comparator<HashMap<String, Object>>() {

      @Override
      public int compare(HashMap<String, Object> one, HashMap<String, Object> two) {

        HashMap<String, Object> oneObject = (HashMap<String, Object>) one.get("data");
        HashMap<String, Object> twoObject = (HashMap<String, Object>) one.get("data");

        int compareTO = (oneObject.get("source").toString().compareTo(twoObject.get("source").toString()));
        return compareTO;
      }
    });
    elements.put("nodes", dataNodes);
    elements.put("edges", dataEdges);


    return SUCCESS;
  }


  public HashMap<String, Object> getElements() {
    return elements;
  }


  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();

    // Validating parameters.
    // sectionName = StringUtils.trim(((String[]) parameters.get(APConstants.SECTION_NAME))[0]);
    sectionName = StringUtils.trim(parameters.get(APConstants.SECTION_NAME).getMultipleValues()[0]);

    crpProgramID = -1;

    try {
      // crpProgramID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.CRP_PROGRAM_ID))[0]));
      crpProgramID =
        Long.parseLong(StringUtils.trim(parameters.get(APConstants.CRP_PROGRAM_ID).getMultipleValues()[0]));
    } catch (Exception e) {
      LOG.error("There was an exception trying to parse the crp program id = {} ",
        // StringUtils.trim(((String[]) parameters.get(APConstants.CRP_PROGRAM_ID))[0]));
        StringUtils.trim(parameters.get(APConstants.CRP_PROGRAM_ID).getMultipleValues()[0]));

    }
  }

  public void setElements(HashMap<String, Object> elements) {
    this.elements = elements;
  }
}
