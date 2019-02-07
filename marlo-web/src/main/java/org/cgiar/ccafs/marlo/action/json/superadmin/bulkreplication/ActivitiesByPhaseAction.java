package org.cgiar.ccafs.marlo.action.json.superadmin.bulkreplication;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ActivitiesByPhaseAction extends BaseAction {

  private static final long serialVersionUID = 918183616637573695L;

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(ActivitiesByPhaseAction.class);

  // Parameters
  private List<Map<String, Object>> entityByPhaseList;

  private long selectedPhaseID;


  // Managers
  private PhaseManager phaseManager;


  @Inject
  public ActivitiesByPhaseAction(APConfig config, PhaseManager phaseManager) {
    super(config);
    this.phaseManager = phaseManager;
  }

  @Override
  public String execute() throws Exception {
    entityByPhaseList = new ArrayList<Map<String, Object>>();

    if (selectedPhaseID != -1) {
      Phase phase = phaseManager.getPhaseById(selectedPhaseID);
      // Get deliverables by Phase
      List<Activity> activitiesbyPhaseList = phase.getProjectActivites().stream()
        .filter(a -> a.isActive() && a.getPhase().equals(phase)).collect(Collectors.toList());

      if (activitiesbyPhaseList != null && !activitiesbyPhaseList.isEmpty()) {
        activitiesbyPhaseList.sort((d1, d2) -> d1.getId().compareTo(d2.getId()));
        // Build the list into a Map
        for (Activity activity : activitiesbyPhaseList) {
          try {
            if (activity != null) {
              Map<String, Object> activityMap = new HashMap<String, Object>();
              activityMap.put("id", activity.getId());
              activityMap.put("composedName", "A" + activity.getId() + ": " + activity.getTitle());
              activityMap.put("project", activity.getProject().getId());
              this.entityByPhaseList.add(activityMap);
            }
          } catch (Exception e) {
            logger.error("Unable to add Activity to Activity list", e);
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