package org.cgiar.ccafs.marlo.action.json.superadmin.bulkreplication;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DeliverableByPhaseAction extends BaseAction {

  private static final long serialVersionUID = 918183616637573695L;

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(DeliverableByPhaseAction.class);

  // Parameters
  private List<Map<String, Object>> entityByPhaseList;
  private long selectedPhaseID;

  // Managers
  private DeliverableManager deliverableManager;

  private PhaseManager phaseManager;


  @Inject
  public DeliverableByPhaseAction(APConfig config, DeliverableManager deliverableManager, PhaseManager phaseManager) {
    super(config);
    this.deliverableManager = deliverableManager;
    this.phaseManager = phaseManager;
  }


  @Override
  public String execute() throws Exception {
    entityByPhaseList = new ArrayList<Map<String, Object>>();

    if (selectedPhaseID != -1) {
      Phase phase = phaseManager.getPhaseById(selectedPhaseID);
      // Get deliverables by Phase
      List<Deliverable> deliverablesbyPhaseList = deliverableManager.getDeliverablesByPhase(selectedPhaseID);

      if (deliverablesbyPhaseList != null && !deliverablesbyPhaseList.isEmpty()) {
        deliverablesbyPhaseList.sort((d1, d2) -> d1.getId().compareTo(d2.getId()));
        // Build the list into a Map
        for (Deliverable deliverable : deliverablesbyPhaseList) {
          try {
            if (deliverable != null && deliverable.getDeliverableInfo(phase) != null) {
              Map<String, Object> deliverableMap = new HashMap<String, Object>();
              deliverableMap.put("id", deliverable.getId());
              deliverableMap.put("title", deliverable.getDeliverableInfo().getTitle());
              deliverableMap.put("composedName",
                "D" + deliverable.getId() + ": " + deliverable.getDeliverableInfo().getTitle());
              deliverableMap.put("project", deliverable.getProject().getId());
              this.entityByPhaseList.add(deliverableMap);
            }
          } catch (Exception e) {
            logger.error("Unable to add Deliverable to Deliverable list", e);
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