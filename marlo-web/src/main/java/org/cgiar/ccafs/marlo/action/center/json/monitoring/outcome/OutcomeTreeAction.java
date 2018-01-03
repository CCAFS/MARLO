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

package org.cgiar.ccafs.marlo.action.center.json.monitoring.outcome;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterProjectOutput;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class OutcomeTreeAction extends BaseAction {


  private static final long serialVersionUID = 4433721734538258925L;


  private long outcomeID;


  private ICenterOutcomeManager outcomeService;


  private List<Map<String, Object>> dataProjects;

  @Inject
  public OutcomeTreeAction(APConfig config, ICenterOutcomeManager outcomeService) {
    super(config);
    this.outcomeService = outcomeService;
  }

  @Override
  public String execute() throws Exception {

    List<CenterProject> projects = new ArrayList<>();

    CenterOutcome outcome = outcomeService.getResearchOutcomeById(outcomeID);

    List<CenterOutput> outputs =
      new ArrayList<>(outcome.getResearchOutputs().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));

    if (!outputs.isEmpty()) {
      for (CenterOutput researchOutput : outputs) {
        List<CenterProjectOutput> projectOutputs = new ArrayList<>(
          researchOutput.getProjectOutputs().stream().filter(po -> po.isActive()).collect(Collectors.toList()));
        if (!projectOutputs.isEmpty()) {

          for (CenterProjectOutput projectOutput : projectOutputs) {
            if (projectOutput.getProject().isActive()) {
              projects.add(projectOutput.getProject());
            }
          }

        }
      }
    }

    HashSet<CenterProject> hashProjects = new HashSet<>();
    hashProjects.addAll(projects);
    projects = new ArrayList<>(hashProjects);


    this.dataProjects = new ArrayList<>();
    for (CenterProject project : hashProjects) {
      Map<String, Object> dataProject = new HashMap<>();
      dataProject.put("id", project.getId());
      dataProject.put("name", project.getName());

      List<CenterProjectOutput> projectOutputs =
        new ArrayList<>(project.getProjectOutputs().stream().filter(po -> po.isActive()).collect(Collectors.toList()));
      List<Map<String, Object>> dataOutputs = new ArrayList<>();
      if (!projectOutputs.isEmpty()) {
        for (CenterProjectOutput projectOutput : projectOutputs) {
          Map<String, Object> dataOutput = new HashMap<>();
          dataOutput.put("id", projectOutput.getResearchOutput().getId());
          dataOutput.put("name", projectOutput.getResearchOutput().getTitle());

          dataOutputs.add(dataOutput);

        }

      }
      dataProject.put("outputs", dataOutputs);

      List<CenterDeliverable> deliverables =
        new ArrayList<>(project.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList()));
      List<Map<String, Object>> dataDeliverables = new ArrayList<>();
      if (!deliverables.isEmpty()) {
        for (CenterDeliverable deliverable : deliverables) {
          Map<String, Object> dataDeliverable = new HashMap<>();
          dataDeliverable.put("id", deliverable.getId());
          dataDeliverable.put("name", deliverable.getName());

          dataDeliverables.add(dataDeliverable);
        }

      }
      dataProject.put("deliverables", dataDeliverables);
      this.dataProjects.add(dataProject);
    }


    return SUCCESS;
  }

  public List<Map<String, Object>> getDataProjects() {
    return dataProjects;
  }

  public long getOutcomeID() {
    return outcomeID;
  }


  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();
    /// outcomeID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.OUTCOME_ID))[0]));
    outcomeID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.OUTCOME_ID).getMultipleValues()[0]));
  }

  public void setDataProjects(List<Map<String, Object>> dataProjects) {
    this.dataProjects = dataProjects;
  }

  public void setOutcomeID(long outcomeID) {
    this.outcomeID = outcomeID;
  }

}
