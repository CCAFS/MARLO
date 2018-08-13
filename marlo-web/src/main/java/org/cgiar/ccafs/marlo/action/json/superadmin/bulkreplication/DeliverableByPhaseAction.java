package org.cgiar.ccafs.marlo.action.json.superadmin.bulkreplication;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DeliverableByPhaseAction extends BaseAction {

  private static final long serialVersionUID = 918183616637573695L;

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(DeliverableByPhaseAction.class);

  // Parameters
  private List<Map<String, Object>> deliverablesbyPhase;
  private long phaseID;

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
    deliverablesbyPhase = new ArrayList<Map<String, Object>>();

    if (phaseID != -1) {
      Phase phase = phaseManager.getPhaseById(phaseID);
      // Get deliverables by Phase
      List<Deliverable> deliverablesbyPhaseList = deliverableManager.getDeliverablesByPhase(phaseID);

      if (deliverablesbyPhaseList != null && !deliverablesbyPhaseList.isEmpty()) {
        deliverablesbyPhaseList.sort((d1, d2) -> d1.getId().compareTo(d2.getId()));
        // Build the list into a Map
        for (Deliverable deliverable : deliverablesbyPhaseList) {
          try {
            if (deliverable.getDeliverableInfo(phase) != null) {
              Map<String, Object> deliverableMap = new HashMap<String, Object>();
              deliverableMap.put("id", deliverable.getId());
              deliverableMap.put("description", deliverable.getDeliverableInfo().getDescription());
              deliverableMap.put("project", deliverable.getProject().getId());
              this.deliverablesbyPhase.add(deliverableMap);
            }
          } catch (Exception e) {
            logger.error("Unable to add Deliverable to Deliverable list", e);
          }
        }
      }
    }

    return SUCCESS;
  }

  public List<Map<String, Object>> getDeliverablesbyPhase() {
    return deliverablesbyPhase;
  }


  @Override
  public void prepare() throws Exception {
    // get global Unit id from the parameters
    String _phaseID = StringUtils.trim(this.getRequest().getParameter(APConstants.PHASE_ID));

    try {
      phaseID = (_phaseID != null) ? Integer.parseInt(_phaseID) : -1;
    } catch (NumberFormatException e) {
      logger.warn("There was an exception trying to convert to int the parameter {}", _phaseID);
      phaseID = -1;
    }
  }


  public void setDeliverablesbyPhase(List<Map<String, Object>> deliverablesbyPhase) {
    this.deliverablesbyPhase = deliverablesbyPhase;
  }


}