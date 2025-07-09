package org.cgiar.ccafs.marlo.action.json.summary;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CrpProgramOutcomesByGlobalUnit extends BaseAction {

  private static final long serialVersionUID = 918183616637573695L;

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(CrpProgramOutcomesByGlobalUnit.class);

  // Parameters
  private List<Map<String, Object>> entityByPhaseList;
  private GlobalUnit globalUnit;

  // Managers
  private CrpProgramOutcomeManager crpProgramOutcomeManager;

  @Inject
  public CrpProgramOutcomesByGlobalUnit(APConfig config, CrpProgramOutcomeManager crpProgramOutcomeManager) {
    super(config);
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
  }

  @Override
  public String execute() throws Exception {
    entityByPhaseList = new ArrayList<Map<String, Object>>();

    try {
      globalUnit = this.getCurrentCrp();

      List<CrpProgramOutcome> crpOutcomesList = new ArrayList<>();

      if (this.getCurrentGlobalUnit() != null) {
        crpOutcomesList =
          crpProgramOutcomeManager.getAllCrpProgramOutcomesByPhase(this.getActualPhase().getId()).stream()
            .filter(c -> c != null && c.getDescription() != null
              && !c.getDescription().contains(APConstants.DELIVERABLE_CRP_PROGRAM_OUTCOME_DEPRECATED))
            .collect(Collectors.toList());
      }

      if (crpOutcomesList != null && !crpOutcomesList.isEmpty()) {
        crpOutcomesList.sort((po1, po2) -> po1.getId().compareTo(po2.getId()));
        // Build the list into a Map
        for (CrpProgramOutcome crpOutcome : crpOutcomesList) {
          try {
            if (crpOutcome != null) {
              Map<String, Object> outcomeMap = new HashMap<String, Object>();
              outcomeMap.put("id", crpOutcome.getId());
              outcomeMap.put("acronym", crpOutcome.getAcronym());
              outcomeMap.put("description", crpOutcome.getDescription());
              this.entityByPhaseList.add(outcomeMap);
            }
          } catch (Exception e) {
            logger.error("Unable to add ProjectOutcome to ProjectOutcome list", e);
          }
        }
      }
    } catch (Exception e) {
      logger.error("Error retrieving project outcomes by phase", e);
      return ERROR;
    }
    return SUCCESS;
  }

  public List<Map<String, Object>> getEntityByPhaseList() {
    return entityByPhaseList;
  }

  @Override
  public void prepare() throws Exception {

  }

  public void setEntityByPhaseList(List<Map<String, Object>> entityByPhaseList) {
    this.entityByPhaseList = entityByPhaseList;
  }
}