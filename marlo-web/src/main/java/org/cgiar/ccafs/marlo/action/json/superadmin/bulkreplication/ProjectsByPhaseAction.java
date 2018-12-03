package org.cgiar.ccafs.marlo.action.json.superadmin.bulkreplication;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProjectsByPhaseAction extends BaseAction {

  private static final long serialVersionUID = 918183616637573695L;

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(ProjectsByPhaseAction.class);

  // Parameters
  private List<Map<String, Object>> projectsbyPhase;
  private long selectedPhaseID;


  // Managers
  private ProjectManager projectManager;


  private PhaseManager phaseManager;

  @Inject
  public ProjectsByPhaseAction(APConfig config, ProjectManager projectManager, PhaseManager phaseManager) {
    super(config);
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
  }

  @Override
  public String execute() throws Exception {
    projectsbyPhase = new ArrayList<Map<String, Object>>();

    if (selectedPhaseID != -1) {
      Phase phase = phaseManager.getPhaseById(selectedPhaseID);
      // Get projects by Phase
      List<ProjectInfo> projectsbyPhaseList = phase.getProjectInfos().stream().collect(Collectors.toList());

      if (projectsbyPhaseList != null && !projectsbyPhaseList.isEmpty()) {
        projectsbyPhaseList.sort((p1, p2) -> p1.getProject().getId().compareTo(p2.getProject().getId()));
        // Build the list into a Map
        for (ProjectInfo projectInfo : projectsbyPhaseList) {
          try {
            if (projectInfo != null) {
              Map<String, Object> projectMap = new HashMap<String, Object>();
              projectMap.put("id", projectInfo.getProject().getId());
              projectMap.put("title", projectInfo.getTitle());
              projectMap.put("composedName", "P" + projectInfo.getProject().getId() + ": " + projectInfo.getTitle());
              this.projectsbyPhase.add(projectMap);
            }
          } catch (Exception e) {
            logger.error("Unable to add projectInfo to Project list", e);
          }
        }
      }
    }

    return SUCCESS;
  }


  public List<Map<String, Object>> getProjectsbyPhase() {
    return projectsbyPhase;
  }


  public long getSelectedPhaseID() {
    return selectedPhaseID;
  }


  @Override
  public void prepare() throws Exception {

  }


  public void setProjectsbyPhase(List<Map<String, Object>> projectsbyPhase) {
    this.projectsbyPhase = projectsbyPhase;
  }


  public void setSelectedPhaseID(long selectedPhaseID) {
    this.selectedPhaseID = selectedPhaseID;
  }


}