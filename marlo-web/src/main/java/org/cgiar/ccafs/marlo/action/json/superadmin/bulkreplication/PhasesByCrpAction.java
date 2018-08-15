package org.cgiar.ccafs.marlo.action.json.superadmin.bulkreplication;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
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


public class PhasesByCrpAction extends BaseAction {

  private static final long serialVersionUID = -4101892827812684836L;

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(PhasesByCrpAction.class);

  // Parameters
  private List<Map<String, Object>> phasesbyGlobalUnit;

  private long globalUnitID;


  // Managers
  private PhaseManager phaseManager;


  @Inject
  public PhasesByCrpAction(APConfig config, PhaseManager phaseManager) {
    super(config);
    this.phaseManager = phaseManager;
  }


  @Override
  public String execute() throws Exception {
    phasesbyGlobalUnit = new ArrayList<Map<String, Object>>();
    if (globalUnitID != -1) {

      // Get phases by Global Unit
      List<Phase> phasesbyGlobalUnitList = phaseManager.findAll().stream()
        .filter(p -> p.getCrp().getId().longValue() == globalUnitID && p.isActive()).collect(Collectors.toList());

      if (phasesbyGlobalUnitList != null && !phasesbyGlobalUnitList.isEmpty()) {
        phasesbyGlobalUnitList.sort((p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate()));
        // Build the list into a Map
        for (Phase phase : phasesbyGlobalUnitList) {
          try {
            Map<String, Object> phasestMap = new HashMap<String, Object>();
            phasestMap.put("id", phase.getId());
            phasestMap.put("name", phase.getName());
            phasestMap.put("description", phase.getDescription());
            phasestMap.put("year", phase.getYear());
            this.phasesbyGlobalUnit.add(phasestMap);
          } catch (Exception e) {
            logger.error("Unable to add Phase to Phase list", e);
          }
        }
      }
    }

    return SUCCESS;
  }


  public long getGlobalUnitID() {
    return globalUnitID;
  }

  public List<Map<String, Object>> getPhasesbyGlobalUnit() {
    return phasesbyGlobalUnit;
  }


  @Override
  public void prepare() throws Exception {


  }

  public void setGlobalUnitID(long globalUnitID) {
    this.globalUnitID = globalUnitID;
  }


  public void setPhasesbyGlobalUnit(List<Map<String, Object>> phasesbyGlobalUnit) {
    this.phasesbyGlobalUnit = phasesbyGlobalUnit;
  }


}