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

package org.cgiar.ccafs.marlo.action.center.json.monitoring.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CenterFundingSyncTypeManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.ocs.model.AgreementOCS;
import org.cgiar.ccafs.marlo.ocs.ws.MarloOcsClient;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * Search the possibles OCS or Project codes to being a funding source in a center project
 * 
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class SuggestedProjectsAction extends BaseAction {


  private static final long serialVersionUID = -2854839401979167421L;


  // Sync Variables
  private long syncTypeID;
  private String syncCode;

  private FundingSourceManager fundingSourceManager;
  private CenterFundingSyncTypeManager fundingSyncTypeManager;
  private ProjectManager projectManager;

  // OCS Agreement Servcie Class
  private MarloOcsClient ocsClient;
  private AgreementOCS agreement;

  // return values List<Map>
  private List<Map<String, Object>> suggestedProjects;

  @Inject
  public SuggestedProjectsAction(APConfig config, CenterFundingSyncTypeManager fundingSyncTypeManager,
    ProjectManager projectManager, MarloOcsClient ocsClient, FundingSourceManager fundingSourceManager) {
    super(config);
    this.fundingSyncTypeManager = fundingSyncTypeManager;
    this.projectManager = projectManager;
    this.ocsClient = ocsClient;
    this.fundingSourceManager = fundingSourceManager;
  }

  /**
   * Check the Project ID suggestions according OCS agreements.
   */
  public void crpSuggestions() {
    agreement = ocsClient.getagreement(syncCode);

    Map<String, Object> dataProject = new HashMap<>();
    dataProject.put("id", syncCode);
    dataProject.put("name", agreement.getShortTitle());

    if (fundingSourceManager.searchFundingSourcesByFinanceCode(syncCode) != null) {
      List<FundingSource> fundingSources = fundingSourceManager.searchFundingSourcesByFinanceCode(syncCode).stream()
        .filter(fs -> fs.isActive()).collect(Collectors.toList());
      List<Map<String, Object>> dataSuggestions = new ArrayList<>();
      for (FundingSource fundingSource : fundingSources) {
        List<ProjectBudget> projectBudgets = fundingSource.getProjectBudgets().stream()
          .filter(pb -> pb.isActive() && pb.getYear() == this.getCenterYear()).collect(Collectors.toList());
        if (projectBudgets != null) {
          for (ProjectBudget projectBudget : projectBudgets) {
            Map<String, Object> dataSuggestion = new HashMap<>();
            if (projectBudget.getProject().isActive()) {
              Project project = projectBudget.getProject();
              dataSuggestion.put("code", project.getId());
              dataSuggestion.put("title", project.getTitle());
              dataSuggestions.add(dataSuggestion);
            }
          }
        }
      }
      dataProject.put("suggestions", dataSuggestions);
    }
    suggestedProjects.add(dataProject);
  }

  @Override
  public String execute() throws Exception {

    this.suggestedProjects = new ArrayList<>();

    switch (Math.toIntExact(syncTypeID)) {

      case 1:
        this.crpSuggestions();
        break;

      case 2:
        this.ocsSuggestions();
        break;

    }
    return SUCCESS;
  }

  public List<Map<String, Object>> getSuggestedProjects() {
    return suggestedProjects;
  }

  public String getSyncCode() {
    return syncCode;
  }

  public long getSyncTypeID() {
    return syncTypeID;
  }

  /**
   * Check the OSC codes suggestions according MARLO CRP Projects.
   */
  public void ocsSuggestions() {

    long projectID = Long.parseLong(syncCode);
    Project project = projectManager.getProjectById(projectID);

    Map<String, Object> dataProject = new HashMap<>();
    dataProject.put("id", project.getId());
    dataProject.put("name", project.getTitle());

    List<ProjectBudget> projectBudgets = new ArrayList<>(project.getProjectBudgets().stream()
      .filter(pb -> pb.isActive() && pb.getYear() == this.getCenterYear()).collect(Collectors.toList()));

    List<FundingSource> fundingSources = new ArrayList<>();

    for (ProjectBudget projectBudget : projectBudgets) {
      FundingSource fundingSource = projectBudget.getFundingSource();
      fundingSources.add(fundingSource);
    }

    if (!fundingSources.isEmpty()) {

      HashSet<FundingSource> hashFundignSources = new HashSet<>();
      hashFundignSources.addAll(fundingSources);
      fundingSources = new ArrayList<>(hashFundignSources);

      List<Map<String, Object>> dataSuggestions = new ArrayList<>();
      for (FundingSource fundingSource : fundingSources) {
        if (fundingSource.getFinanceCode() != null) {
          agreement = ocsClient.getagreement(fundingSource.getFinanceCode());
          Map<String, Object> dataSuggestion = new HashMap<>();
          if (agreement != null) {
            dataSuggestion.put("code", fundingSource.getFinanceCode());
            dataSuggestion.put("title", agreement.getShortTitle());
            dataSuggestions.add(dataSuggestion);
          }

        }
      }

      dataProject.put("suggestions", dataSuggestions);

    }

    suggestedProjects.add(dataProject);
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    syncTypeID =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.CENTER_PROJECT_SYNC_TYPE).getMultipleValues()[0]));
    syncCode = StringUtils.trim(parameters.get(APConstants.CENTER_PROJECT_SYNC_CODE).getMultipleValues()[0]);

    if (syncTypeID == 2) {
      if (syncCode.toUpperCase().contains("P")) {
        syncCode = syncCode.toUpperCase().replaceFirst("P", "");
      }
    }

  }

  public void setSuggestedProjects(List<Map<String, Object>> suggestedProjects) {
    this.suggestedProjects = suggestedProjects;
  }

  public void setSyncCode(String syncCode) {
    this.syncCode = syncCode;
  }

  public void setSyncTypeID(long syncTypeID) {
    this.syncTypeID = syncTypeID;
  }

}
