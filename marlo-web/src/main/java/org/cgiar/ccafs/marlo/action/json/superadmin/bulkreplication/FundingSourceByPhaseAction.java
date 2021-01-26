package org.cgiar.ccafs.marlo.action.json.superadmin.bulkreplication;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FundingSourceByPhaseAction extends BaseAction {

  private static final long serialVersionUID = 918183616637573695L;

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(FundingSourceByPhaseAction.class);

  // Parameters
  private List<Map<String, Object>> entityByPhaseList;
  private long selectedPhaseID;

  // Managers
  private FundingSourceManager fundingSourceManager;
  private PhaseManager phaseManager;


  @Inject
  public FundingSourceByPhaseAction(APConfig config, PhaseManager phaseManager,
    FundingSourceManager fundingSourceManager) {
    super(config);
    this.phaseManager = phaseManager;
    this.fundingSourceManager = fundingSourceManager;
  }


  @Override
  public String execute() throws Exception {
    entityByPhaseList = new ArrayList<Map<String, Object>>();

    if (selectedPhaseID != -1) {
      Phase phase = phaseManager.getPhaseById(selectedPhaseID);
      // Get deliverables by Phase
      Set<Integer> statuses = new HashSet<>();
      statuses.add(1);
      statuses.add(2);
      statuses.add(3);
      statuses.add(4);

      List<FundingSource> FundingSourcesbyPhaseList =
        fundingSourceManager.getGlobalUnitFundingSourcesByPhaseAndTypes(this.getCurrentCrp(), phase, statuses);
      // List<FundingSource> FundingSourcesbyPhaseList = fundingSourceManager.findAll().stream().filter(f ->
      // f.getFundingSourceInfo(phase)!= null && f.isActive() &&
      // f.getFundingSourceInfo(phase).isActive()).collect(Collectors.toList());

      if (FundingSourcesbyPhaseList != null && !FundingSourcesbyPhaseList.isEmpty()) {
        FundingSourcesbyPhaseList.sort((d1, d2) -> d1.getId().compareTo(d2.getId()));
        // Build the list into a Map
        for (FundingSource fundingSource : FundingSourcesbyPhaseList) {
          try {
            if (fundingSource != null && fundingSource.getFundingSourceInfo(phase) != null) {
              Map<String, Object> fundingMap = new HashMap<String, Object>();
              fundingMap.put("id", fundingSource.getId());
              fundingMap.put("title", fundingSource.getFundingSourceInfo().getTitle());
              fundingMap.put("composedName",
                "FS" + fundingSource.getId() + ": " + fundingSource.getFundingSourceInfo().getTitle());
              // fundingMap.put("project", fundingSource.getProject().getId());
              this.entityByPhaseList.add(fundingMap);
            }
          } catch (Exception e) {
            logger.error("Unable to add Funding Source to Funding Source list", e);
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