package org.cgiar.ccafs.marlo.action.json.superadmin.bulkreplication;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProjectOutcomesByPhaseAction extends BaseAction {

  private static final long serialVersionUID = 918183616637573695L;

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(ProjectOutcomesByPhaseAction.class);

  // Parameters
  private List<Map<String, Object>> entityByPhaseList;

  private long selectedPhaseID;


  // Managers
  private PhaseManager phaseManager;


  @Inject
  public ProjectOutcomesByPhaseAction(APConfig config, PhaseManager phaseManager) {
    super(config);
    this.phaseManager = phaseManager;
  }

  @Override
  public String execute() throws Exception {
    entityByPhaseList = new ArrayList<Map<String, Object>>();

    if (selectedPhaseID != -1) {
      Phase phase = phaseManager.getPhaseById(selectedPhaseID);
      // Get deliverables by Phase

      List<ProjectOutcome> projectOutcomesbyPhaseList = phase.getProjectOutcomes().stream()
        .filter(po -> po.isActive() && po.getProject() != null && po.getProject().isActive())
        .collect(Collectors.toList());


      if (projectOutcomesbyPhaseList != null && !projectOutcomesbyPhaseList.isEmpty()) {
        projectOutcomesbyPhaseList.sort((po1, po2) -> po1.getId().compareTo(po2.getId()));
        // Build the list into a Map
        for (ProjectOutcome projectOutcome : projectOutcomesbyPhaseList) {
          try {
            if (projectOutcome != null) {
              Map<String, Object> outcomeMap = new HashMap<String, Object>();
              outcomeMap.put("id", projectOutcome.getId());
              outcomeMap.put("composedName", projectOutcome.getId());
              outcomeMap.put("project", projectOutcome.getProject().getId());
              this.entityByPhaseList.add(outcomeMap);
            }
          } catch (Exception e) {
            logger.error("Unable to add ProjectOutcome to ProjectOutcome list", e);
          }
        }
      }
    }

    return SUCCESS;
  }

  public List<Map<String, Object>> getEntityByPhaseList() {
    return entityByPhaseList;
  }

  public long getSelectedPhaseID() {
    return selectedPhaseID;
  }


  @Override
  public void prepare() throws Exception {

  }


  public void setEntityByPhaseList(List<Map<String, Object>> entityByPhaseList) {
    this.entityByPhaseList = entityByPhaseList;
  }


  public void setSelectedPhaseID(long selectedPhaseID) {
    this.selectedPhaseID = selectedPhaseID;
  }


}