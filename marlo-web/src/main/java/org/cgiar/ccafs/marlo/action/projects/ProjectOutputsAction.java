/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
package org.cgiar.ccafs.marlo.action.projects;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.IpElementManager;
import org.cgiar.ccafs.marlo.data.manager.IpProjectContributionOverviewManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.IpElement;
import org.cgiar.ccafs.marlo.data.model.IpProjectContribution;
import org.cgiar.ccafs.marlo.data.model.IpProjectContributionOverview;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.utils.HistoryDifference;
import org.cgiar.ccafs.marlo.validation.projects.ProjectOutputsValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Christian David Garcia Oviedo- CIAT/CCAFS
 */
public class ProjectOutputsAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 5027543384132820515L;
  // Manager
  private final ProjectManager projectManager;
  private final IpProjectContributionOverviewManager ipProjectContributionOverviewManager;
  private final IpElementManager ipElementManager;
  private final ProjectOutputsValidator projectOutputsValidator;
  private final HistoryComparator historyComparator;

  private List<Integer> allYears;


  private long projectID;


  private Project project;

  private final CrpManager crpManager;
  private Crp loggedCrp;

  private String transaction;

  private final AuditLogManager auditLogManager;


  @Inject
  public ProjectOutputsAction(APConfig config, ProjectManager projectManager, AuditLogManager auditLogManager,
    CrpManager crpManager, IpProjectContributionOverviewManager ipProjectContributionOverviewManager,
    IpElementManager ipElementManager, ProjectOutputsValidator projectOutputsValidator,
    HistoryComparator historyComparator) {
    super(config);
    this.projectManager = projectManager;
    this.ipProjectContributionOverviewManager = ipProjectContributionOverviewManager;
    this.ipElementManager = ipElementManager;
    this.historyComparator = historyComparator;
    this.projectOutputsValidator = projectOutputsValidator;
    this.crpManager = crpManager;
    this.auditLogManager = auditLogManager;

  }

  @Override
  public String cancel() {

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {

      boolean fileDeleted = path.toFile().delete();
    }

    this.setDraft(false);
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      String validationMessage = messages.iterator().next();
      this.setActionMessages(null);
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    } else {
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    }
    messages = this.getActionMessages();

    return SUCCESS;
  }

  @Override
  public List<Integer> getAllYears() {
    return allYears;
  }


  private Path getAutoSaveFilePath() {
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public int getIndex(int year, long mogID) {
    if (project.getOverviews() != null) {
      int i = 0;
      for (IpProjectContributionOverview overview : project.getOverviews()) {
        if (overview.getYear() == year && overview.getIpElement().getId().longValue() == mogID) {
          return i;
        }
        i++;
      }
    }
    IpProjectContributionOverview ipProjectContributionOverview = new IpProjectContributionOverview();
    ipProjectContributionOverview.setYear(year);
    ipProjectContributionOverview.setIpElement(ipElementManager.getIpElementById(mogID));
    if (project.getOverviews() == null) {
      project.setOverviews(new ArrayList<>());
    }
    project.getOverviews().add(ipProjectContributionOverview);
    return project.getOverviews().size() - 1;
  }


  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  public IpProjectContributionOverview getOverview(int year, long mogID) {
    int index = this.getIndex(year, mogID);
    if (index >= 0) {
      return project.getOverviews().get(index);

    }
    return null;
  }

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  public ProjectManager getProjectManager() {
    return projectManager;
  }

  public String getProjectRequest() {
    return APConstants.PROJECT_REQUEST_ID;
  }


  public String getTransaction() {
    return transaction;
  }

  @Override
  public String next() {
    String result = this.save();
    if (result.equals(BaseAction.SUCCESS)) {
      return BaseAction.NEXT;
    } else {
      return result;
    }
  }


  public void overViewsNewData(List<IpProjectContributionOverview> overviews) {

    for (IpProjectContributionOverview overview : overviews) {
      if (overview != null) {
        if (overview.getId() == null || overview.getId() == -1) {
          overview.setActive(true);
          overview.setCreatedBy(this.getCurrentUser());
          overview.setModifiedBy(this.getCurrentUser());
          overview.setModificationJustification(this.getJustification());
          overview.setActiveSince(new Date());

          overview.setId(null);
          overview.setProject(project);

        } else {
          IpProjectContributionOverview overviewDB =
            ipProjectContributionOverviewManager.getIpProjectContributionOverviewById(overview.getId());
          overview.setActive(true);
          overview.setCreatedBy(overviewDB.getCreatedBy());
          overview.setModifiedBy(this.getCurrentUser());
          overview.setModificationJustification(this.getJustification());
          overview.setYear(overviewDB.getYear());
          overview.setProject(project);
          overview.setActiveSince(overviewDB.getActiveSince());

        }
        ipProjectContributionOverviewManager.saveIpProjectContributionOverview(overview);
      }


    }

  }

  @Override
  public void prepare() throws Exception {
    super.prepare();

    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());


    projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {


      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Project history = (Project) auditLogManager.getHistory(transaction);

      if (history != null) {
        project = history;

        List<HistoryDifference> differences = new ArrayList<>();
        Map<String, String> specialList = new HashMap<>();
        int i = 0;
        project.setOverviews(
          project.getIpProjectContributionOverviews().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        for (IpProjectContributionOverview projectOutcomePandr : project.getOverviews()) {
          int[] index = new int[1];
          index[0] = i;
          differences.addAll(historyComparator.getDifferencesList(projectOutcomePandr, transaction, specialList,
            "project.overviews[" + i + "]", "project", 1));
          i++;
        }

        this.setDifferences(differences);

      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }

    } else {
      project = projectManager.getProjectById(projectID);
    }

    if (project != null) {


      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();


        AutoSaveReader autoSaveReader = new AutoSaveReader();

        project = (Project) autoSaveReader.readFromJson(jReader);


        if (project.getOverviews() == null) {

          project.setOverviews(new ArrayList<IpProjectContributionOverview>());
        } else {
          for (IpProjectContributionOverview overview : project.getOverviews()) {

            if (overview != null) {
              if (overview.getIpElement() != null) {
                overview.setIpElement(ipElementManager.getIpElementById(overview.getIpElement().getId()));
              }

            }


          }
        }
        this.setDraft(true);
      } else {
        project.setOverviews(
          project.getIpProjectContributionOverviews().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

        this.setDraft(false);
      }
    }


    Project projectDB = projectManager.getProjectById(projectID);
    project.setAdministrative(projectDB.getAdministrative());
    project.setProjectEditLeader(projectDB.isProjectEditLeader());
    allYears = projectDB.getAllYears();
    project.setMogs(new ArrayList<>());
    List<IpProjectContribution> ipProjectContributions =
      projectDB.getIpProjectContributions().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    for (IpProjectContribution ipProjectContribution : ipProjectContributions) {
      project.getMogs().add(ipProjectContribution.getIpElementByMogId());
    }
    Set<IpElement> elementsMogs = new HashSet<>();
    elementsMogs.addAll(project.getMogs());

    project.getMogs().clear();
    project.getMogs().addAll(elementsMogs);
    // Getting the list of all institutions

    if (this.isHttpPost()) {

      if (project.getOverviews() != null) {
        project.getOverviews().clear();
      }


    }

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_OVERVIEWS_BASE_PERMISSION, params));


  }


  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      Project projectDB = projectManager.getProjectById(project.getId());
      project.setActive(true);
      project.setCreatedBy(projectDB.getCreatedBy());
      project.setModifiedBy(this.getCurrentUser());
      project.setModificationJustification(this.getJustification());
      project.setActiveSince(projectDB.getActiveSince());


      this.overViewsNewData(project.getOverviews());
      /*
       * this.activitiesPreviousData(project.getClosedProjectActivities(), false);
       * this.activitiesNewData(project.getClosedProjectActivities());
       */
      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_OVERVIEWS_RELATION);
      project = projectManager.getProjectById(projectID);
      project.setActiveSince(new Date());
      project.setModifiedBy(this.getCurrentUser());
      project.setModificationJustification(this.getJustification());
      projectManager.saveProject(project, this.getActionName(), relationsName);
      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists()) {
        path.toFile().delete();
      }


      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();

        if (!this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }

        } else {
          this.addActionMessage("message:" + this.getText("saving.saved"));
        }
        return SUCCESS;
      } else {
        this.addActionMessage("");
        this.setActionMessages(null);
        return REDIRECT;
      }
    }
    return NOT_AUTHORIZED;
  }

  public void setAllYears(List<Integer> allYears) {
    this.allYears = allYears;
  }


  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      projectOutputsValidator.validate(this, project, true);

    }
  }
}