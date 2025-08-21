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
import org.cgiar.ccafs.marlo.action.summaries.BaseSummariesAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PRMSInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.PRMSInnovation;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.struts2.dispatcher.Parameter;

public class PRMSInnovationService extends BaseAction {

  private static final long serialVersionUID = -4335064142194555431L;
  private List<Map<String, String>> innovations;

  private List<Project> allInnovations;
  private PRMSInnovationManager prmsInnovationManager;


  @Inject
  public PRMSInnovationService(APConfig config, PRMSInnovationManager prmsInnovationManager) {
    super(config);
    this.prmsInnovationManager = prmsInnovationManager;
  }

  @Override
  public String execute() throws Exception {
    innovations = new ArrayList<>();
    try {
      List<PRMSInnovation> allPRMSInnovations = prmsInnovationManager.findAll();

      for (PRMSInnovation innovation : allPRMSInnovations) {
        Map<String, String> innovationMap = new HashMap<>();

        innovationMap.put("id", innovation.getId() != null ? innovation.getId().toString() : "");
        innovationMap.put("title", innovation.getTitle() != null ? innovation.getTitle() : "");
        innovationMap.put("description", innovation.getDescription() != null ? innovation.getDescription() : "");
        innovationMap.put("typeId", String.valueOf(innovation.getTypeId()));
        innovationMap.put("typeName", innovation.getTypeName() != null ? innovation.getTypeName() : "");
        innovationMap.put("year", String.valueOf(innovation.getYear()));
        innovationMap.put("pdfLink", innovation.getPdfLink() != null ? innovation.getPdfLink() : "");
        innovationMap.put("readinessLevelId", String.valueOf(innovation.getReadinessLevelId()));
        innovationMap.put("readinessLevelName",
          innovation.getReadinessLevelName() != null ? innovation.getReadinessLevelName() : "");

        innovations.add(innovationMap);
      }

      return SUCCESS;
    } catch (Exception e) {
      e.printStackTrace();
      return ERROR;
    }
  }

  public List<Project> getAllInnovations() {
    return allInnovations;
  }

  public List<Map<String, String>> getInnovations() {
    return innovations;
  }

  public void setInnovations(List<Map<String, String>> innovations) {
    this.innovations = innovations;
  }

  public void setAllInnovations(List<Project> allInnovations) {
    this.allInnovations = allInnovations;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
  }
}