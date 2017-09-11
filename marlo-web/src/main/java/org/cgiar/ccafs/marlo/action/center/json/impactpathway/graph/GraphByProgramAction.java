/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &`
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

package org.cgiar.ccafs.marlo.action.center.json.impactpathway.graph;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ICenterObjectiveManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.CenterImpact;
import org.cgiar.ccafs.marlo.data.model.CenterImpactObjective;
import org.cgiar.ccafs.marlo.data.model.CenterObjective;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;
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

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class GraphByProgramAction extends BaseAction {


  // logger
  private static final Logger LOG = LoggerFactory.getLogger(GraphByProgramAction.class);
  private static final long serialVersionUID = -5892467002878824894L;

  // Return values
  private HashMap<String, Object> elements;

  // Parameters
  private long programID;

  // Managers -Services
  private ICenterProgramManager programService;
  private ICenterObjectiveManager objectiveService;

  @Inject
  public GraphByProgramAction(APConfig config, ICenterProgramManager programService,
    ICenterObjectiveManager objectiveService) {
    super(config);
    this.programService = programService;
    this.objectiveService = objectiveService;
  }

  @Override
  public String execute() throws Exception {

    elements = new HashMap<>();
    List<HashMap<String, Object>> dataNodes = new ArrayList<HashMap<String, Object>>();
    List<HashMap<String, Object>> dataEdges = new ArrayList<HashMap<String, Object>>();


    CenterProgram program = programService.getProgramById(programID);
    CenterArea area = program.getResearchArea();

    // Research Area Data
    HashMap<String, Object> dataArea = new HashMap<>();
    HashMap<String, Object> dataAreaDetail = new HashMap<>();
    dataAreaDetail.put("id", "A" + area.getId());
    dataAreaDetail.put("label", area.getAcronym());
    dataAreaDetail.put("title", "Research Area");
    dataAreaDetail.put("description", area.getName());
    dataAreaDetail.put("color", area.getColor());
    dataAreaDetail.put("type", "A");
    dataArea.put("data", dataAreaDetail);

    dataNodes.add(dataArea);
    // Research Program Data
    HashMap<String, Object> dataProgram = new HashMap<>();
    HashMap<String, Object> dataProgramDetail = new HashMap<>();
    dataProgramDetail.put("id", "P" + program.getId());
    dataProgramDetail.put("parent", "A" + program.getResearchArea().getId());
    dataProgramDetail.put("label", program.getAcronym());
    dataProgramDetail.put("title", "Research Program");
    dataProgramDetail.put("description", program.getName());
    dataProgramDetail.put("color", program.getColor());
    dataProgramDetail.put("type", "P");
    dataProgram.put("data", dataProgramDetail);

    dataNodes.add(dataProgram);


    List<CenterObjective> objectives = new ArrayList<>();
    if (objectiveService.findAll() != null) {
      objectives = objectiveService.findAll().stream().filter(o -> o.isActive()).collect(Collectors.toList());
    }

    List<CenterImpact> impacts =
      new ArrayList<>(program.getResearchImpacts().stream().filter(ri -> ri.isActive()).collect(Collectors.toList()));

    List<CenterTopic> topics =
      new ArrayList<>(program.getResearchTopics().stream().filter(rt -> rt.isActive()).collect(Collectors.toList()));

    int i = 1;

    for (CenterImpact impact : impacts) {
      // Research Impact Data
      HashMap<String, Object> dataImpact = new HashMap<>();
      HashMap<String, Object> dataImpactDetail = new HashMap<>();
      dataImpactDetail.put("id", "I" + impact.getId());
      dataImpactDetail.put("parent", "P" + impact.getResearchProgram().getId());
      dataImpactDetail.put("label",
        this.isShortName(impact.getShortName()) ? impact.getShortName() : "Research Impact " + impact.getId());
      dataImpactDetail.put("title", this.isShortName(impact.getShortName())
        ? "Research Impact - " + impact.getShortName() : "Research Impact " + impact.getId());
      dataImpactDetail.put("description", impact.getDescription());
      dataImpactDetail.put("color", impact.getColor());
      dataImpactDetail.put("type", "I");
      dataImpact.put("data", dataImpactDetail);
      dataNodes.add(dataImpact);

      List<CenterImpactObjective> impactObjectives = new ArrayList<>(
        impact.getResearchImpactObjectives().stream().filter(io -> io.isActive()).collect(Collectors.toList()));

      for (CenterImpactObjective impactObjective : impactObjectives) {
        // Relation S Objective - Program Impact
        HashMap<String, Object> dataEdgeImpact = new HashMap<>();
        HashMap<String, Object> dataEdgeImpactDetail = new HashMap<>();
        dataEdgeImpactDetail.put("source", "SO" + impactObjective.getResearchObjective().getId());
        dataEdgeImpactDetail.put("target", "I" + impactObjective.getResearchImpact().getId());
        dataEdgeImpact.put("data", dataEdgeImpactDetail);
        dataEdges.add(dataEdgeImpact);
      }


      i++;
    }

    i = 1;

    for (CenterTopic topic : topics) {
      // Research Topic Data
      HashMap<String, Object> dataTopic = new HashMap<>();
      HashMap<String, Object> dataTopicDetail = new HashMap<>();
      dataTopicDetail.put("id", "T" + topic.getId());
      dataTopicDetail.put("parent", "P" + topic.getResearchProgram().getId());
      dataTopicDetail.put("label",
        this.isShortName(topic.getShortName()) ? topic.getShortName() : "Research Topic - R" + topic.getId());
      dataTopicDetail.put("title", this.isShortName(topic.getShortName()) ? "Research Topic - " + topic.getShortName()
        : "Research Topic - R" + topic.getId());
      dataTopicDetail.put("description", topic.getResearchTopic());
      dataTopicDetail.put("color", topic.getColor());
      dataTopicDetail.put("type", "T");
      dataTopic.put("data", dataTopicDetail);
      dataNodes.add(dataTopic);

      int j = 1;
      List<CenterOutcome> outcomes = new ArrayList<>(topic.getResearchOutcomes().stream()
        .filter(ro -> ro.isActive() && ro.getResearchImpact() != null).collect(Collectors.toList()));

      for (CenterOutcome outcome : outcomes) {
        // Research Outcome Data
        HashMap<String, Object> dataOutcome = new HashMap<>();
        HashMap<String, Object> dataOutcomeDetail = new HashMap<>();
        dataOutcomeDetail.put("id", "OC" + outcome.getId());
        dataOutcomeDetail.put("parent", "T" + outcome.getResearchTopic().getId());
        dataOutcomeDetail.put("label",
          this.isShortName(outcome.getShortName()) ? outcome.getShortName() : "Outcome " + outcome.getId());
        dataOutcomeDetail.put("title", this.isShortName(outcome.getShortName()) ? "Outcome - " + outcome.getShortName()
          : "Outcome - OC" + outcome.getId());
        dataOutcomeDetail.put("description", outcome.getDescription());
        dataOutcomeDetail.put("color", outcome.getResearchImpact().getColor());
        dataOutcomeDetail.put("type", "OC");

        // Relation Program Impact - Outcome
        HashMap<String, Object> dataEdgeOutcome = new HashMap<>();
        HashMap<String, Object> dataEdgeOutcomeDetail = new HashMap<>();
        dataEdgeOutcomeDetail.put("source", "I" + outcome.getResearchImpact().getId());
        dataEdgeOutcomeDetail.put("target", "OC" + outcome.getId());
        dataEdgeOutcome.put("data", dataEdgeOutcomeDetail);
        dataEdges.add(dataEdgeOutcome);

        List<CenterOutput> outputs = new ArrayList<>(
          outcome.getResearchOutputs().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));

        dataOutcomeDetail.put("Nouptus", outputs.size());
        dataOutcome.put("data", dataOutcomeDetail);
        dataNodes.add(dataOutcome);

        int k = 1;
        for (CenterOutput output : outputs) {
          // Research Output Data
          HashMap<String, Object> dataOutput = new HashMap<>();
          HashMap<String, Object> dataOutputDetail = new HashMap<>();
          dataOutputDetail.put("id", "OP" + output.getId());
          dataOutputDetail.put("parent", "T" + output.getResearchOutcome().getResearchTopic().getId());
          dataOutputDetail.put("label",
            this.isShortName(output.getShortName()) ? output.getShortName() : "Output " + output.getId());
          dataOutputDetail.put("title", this.isShortName(output.getShortName()) ? "Output - " + output.getShortName()
            : "Output - O" + output.getId());
          dataOutputDetail.put("description", output.getTitle());
          dataOutputDetail.put("color", output.getResearchOutcome().getResearchImpact().getColor());
          dataOutputDetail.put("type", "OP");
          dataOutput.put("data", dataOutputDetail);
          dataNodes.add(dataOutput);
          // Relation Outcome - Output
          HashMap<String, Object> dataEdgeOutput = new HashMap<>();
          HashMap<String, Object> dataEdgeOutputDetail = new HashMap<>();
          dataEdgeOutputDetail.put("source", "OC" + output.getResearchOutcome().getId());
          dataEdgeOutputDetail.put("target", "OP" + output.getId());
          dataEdgeOutput.put("data", dataEdgeOutputDetail);
          dataEdges.add(dataEdgeOutput);

          k++;
        }

        j++;

      }

      i++;
    }

    i = 1;

    for (CenterObjective researchObjective : objectives) {
      // Strategic Objective Data
      HashMap<String, Object> dataObjective = new HashMap<>();
      HashMap<String, Object> dataObjectiveDetail = new HashMap<>();
      dataObjectiveDetail.put("id", "SO" + researchObjective.getId());
      dataObjectiveDetail.put("label", "Objective " + i);
      dataObjectiveDetail.put("title", "Strategic Objective " + i);
      dataObjectiveDetail.put("description", researchObjective.getObjective());
      dataObjectiveDetail.put("color", "#FFFFFF");
      dataObjectiveDetail.put("type", "SO");
      dataObjective.put("data", dataObjectiveDetail);
      dataNodes.add(dataObjective);


      i++;
    }

    elements.put("nodes", dataNodes);
    elements.put("edges", dataEdges);

    return SUCCESS;
  }

  public HashMap<String, Object> getElements() {
    return elements;
  }

  public boolean isShortName(String shortName) {
    if (shortName == null) {
      return false;
    }

    if (shortName.trim().length() <= 0) {
      return false;
    }

    return true;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();

    try {
      programID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.CENTER_PROGRAM_ID))[0]));
    } catch (Exception e) {
      LOG.error("There was an exception trying to parse the crp program id = {} ",
        StringUtils.trim(((String[]) parameters.get(APConstants.CENTER_PROGRAM_ID))[0]));
    }
  }

  public void setElements(HashMap<String, Object> elements) {
    this.elements = elements;
  }

}
