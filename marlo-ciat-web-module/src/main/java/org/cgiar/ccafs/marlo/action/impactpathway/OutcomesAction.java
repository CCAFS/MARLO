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

package org.cgiar.ccafs.marlo.action.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConfig;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterImpactManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterTargetUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterTopicManager;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.CenterImpact;
import org.cgiar.ccafs.marlo.data.model.CenterMilestone;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterTargetUnit;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConstants;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.impactpathway.OutcomesValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class OutcomesAction extends BaseAction {


  private static final long serialVersionUID = 286014380343262534L;


  // Services - Managers
  private ICenterManager centerService;
  private ICenterOutcomeManager outcomeService;
  private AuditLogManager auditLogService;
  private ICenterTargetUnitManager targetUnitService;
  private ICenterTopicManager researchTopicService;
  private ICenterProgramManager programService;
  private ICenterImpactManager impactService;
  private ICenterMilestoneManager milestoneService;

  // Front Variables
  private Center loggedCenter;
  private List<CenterArea> researchAreas;

  private CenterArea selectedResearchArea;
  private List<CenterProgram> researchPrograms;
  private CenterProgram selectedProgram;
  private CenterOutcome outcome;
  private List<CenterTopic> researchTopics;
  private CenterTopic selectedResearchTopic;
  private List<CenterImpact> researchImpacts;
  private HashMap<Long, String> targetUnitList;
  // Parameter Variables
  private long programID;
  private long areaID;
  private long topicID;
  private long outcomeID;
  private String transaction;

  // Validator
  private OutcomesValidator validator;

  @Inject
  public OutcomesAction(APConfig config, ICenterManager centerService, ICenterOutcomeManager outcomeService,
    ICenterTargetUnitManager targetUnitService, ICenterTopicManager researchTopicService,
    ICenterProgramManager programService, ICenterImpactManager impactService, ICenterMilestoneManager milestoneService,
    OutcomesValidator validator, AuditLogManager auditLogService) {
    super(config);
    this.centerService = centerService;
    this.outcomeService = outcomeService;
    this.targetUnitService = targetUnitService;
    this.researchTopicService = researchTopicService;
    this.programService = programService;
    this.impactService = impactService;
    this.milestoneService = milestoneService;
    this.validator = validator;
    this.auditLogService = auditLogService;
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

  public long getAreaID() {
    return areaID;
  }

  private Path getAutoSaveFilePath() {
    String composedClassName = outcome.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = outcome.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public Center getLoggedCenter() {
    return loggedCenter;
  }

  public CenterOutcome getOutcome() {
    return outcome;
  }


  public long getOutcomeID() {
    return outcomeID;
  }

  public long getProgramID() {
    return programID;
  }

  public List<CenterArea> getResearchAreas() {
    return researchAreas;
  }

  public List<CenterImpact> getResearchImpacts() {
    return researchImpacts;
  }

  public List<CenterProgram> getResearchPrograms() {
    return researchPrograms;
  }

  public List<CenterTopic> getResearchTopics() {
    return researchTopics;
  }


  public CenterProgram getSelectedProgram() {
    return selectedProgram;
  }


  public CenterArea getSelectedResearchArea() {
    return selectedResearchArea;
  }


  public CenterTopic getSelectedResearchTopic() {
    return selectedResearchTopic;
  }


  public HashMap<Long, String> getTargetUnitList() {
    return targetUnitList;
  }


  public long getTopicID() {
    return topicID;
  }


  public String getTransaction() {
    return transaction;
  }


  @Override
  public void prepare() throws Exception {
    areaID = -1;
    programID = -1;
    topicID = -1;

    loggedCenter = (Center) this.getSession().get(APConstants.SESSION_CENTER);
    loggedCenter = centerService.getCrpById(loggedCenter.getId());

    try {
      outcomeID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.OUTCOME_ID)));
    } catch (Exception e) {
      e.printStackTrace();
    }


    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      CenterOutcome history = (CenterOutcome) auditLogService.getHistory(transaction);

      if (history != null) {
        outcome = history;
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }

    } else {
      outcome = outcomeService.getResearchOutcomeById(outcomeID);
    }

    researchAreas = new ArrayList<>(
      loggedCenter.getResearchAreas().stream().filter(ra -> ra.isActive()).collect(Collectors.toList()));

    Collections.sort(researchAreas, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));

    if (researchAreas != null && outcome != null) {

      programID = outcome.getResearchTopic().getResearchProgram().getId();
      selectedProgram = programService.getProgramById(programID);
      selectedResearchTopic = outcome.getResearchTopic();
      topicID = selectedResearchTopic.getId();
      selectedResearchArea = selectedProgram.getResearchArea();
      areaID = selectedResearchArea.getId();

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        AutoSaveReader autoSaveReader = new AutoSaveReader();

        outcome = (CenterOutcome) autoSaveReader.readFromJson(jReader);

        reader.close();
        this.setDraft(true);
      } else {
        this.setDraft(false);

        outcome.setMilestones(new ArrayList<>(
          outcome.getResearchMilestones().stream().filter(rm -> rm.isActive()).collect(Collectors.toList())));
      }


      if (selectedProgram.getResearchTopics() != null) {
        researchTopics = new ArrayList<>(selectedProgram.getResearchTopics().stream()
          .filter(rt -> rt.isActive() && rt.getResearchTopic().trim().length() > 0).collect(Collectors.toList()));
      }

      researchPrograms = new ArrayList<>(
        selectedResearchArea.getResearchPrograms().stream().filter(rp -> rp.isActive()).collect(Collectors.toList()));

      researchImpacts = new ArrayList<>(selectedProgram.getResearchImpacts().stream()
        .filter(ri -> ri.isActive() && ri.getDescription().trim().length() > 0).collect(Collectors.toList()));

      targetUnitList = new HashMap<>();
      if (targetUnitService.findAll() != null) {

        List<CenterTargetUnit> targetUnits =
          targetUnitService.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());

        Collections.sort(targetUnits,
          (tu1, tu2) -> tu1.getName().toLowerCase().trim().compareTo(tu2.getName().toLowerCase().trim()));

        for (CenterTargetUnit srfTargetUnit : targetUnits) {
          targetUnitList.put(srfTargetUnit.getId(), srfTargetUnit.getName());
        }

        targetUnitList = this.sortByComparator(targetUnitList);
      }


    }

    String params[] = {loggedCenter.getAcronym(), selectedResearchArea.getId() + "", selectedProgram.getId() + ""};
    this.setBasePermission(this.getText(Permission.RESEARCH_PROGRAM_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (targetUnitList != null) {
        targetUnitList.clear();
      }

      if (researchImpacts != null) {
        researchImpacts.clear();
      }

      if (researchTopics != null) {
        researchTopics.clear();
      }

      if (outcome.getMilestones() != null) {
        outcome.getMilestones().clear();
      }
    }

  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {

      CenterOutcome outcomeDb = outcomeService.getResearchOutcomeById(outcomeID);

      CenterImpact impact = impactService.getResearchImpactById(outcome.getResearchImpact().getId());

      CenterTargetUnit targetUnit = targetUnitService.getTargetUnitById(outcome.getTargetUnit().getId());

      outcomeDb.setDescription(outcome.getDescription());
      outcomeDb.setShortName(outcome.getShortName());
      outcomeDb.setTargetYear(outcome.getTargetYear());

      outcomeDb.setTargetUnit(targetUnit);
      if (targetUnit.getId() != -1) {
        outcomeDb.setValue(outcome.getValue());
      } else {
        outcomeDb.setValue(null);
      }

      outcomeDb.setResearchImpact(impact);

      outcomeDb.setModifiedBy(this.getCurrentUser());
      Long outcomeSaveId = outcomeService.saveResearchOutcome(outcomeDb);

      CenterOutcome outcomeSave = outcomeService.getResearchOutcomeById(outcomeSaveId);

      this.saveMilestones(outcomeSave);

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.RESEARCH_OUTCOME_MILESTONE_RELATION);
      outcome = outcomeService.getResearchOutcomeById(outcomeID);
      outcome.setActiveSince(new Date());
      outcome.setModifiedBy(this.getCurrentUser());
      outcomeService.saveResearchOutcome(outcome, this.getActionName(), relationsName);

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      Collection<String> messages = this.getActionMessages();

      if (!this.getInvalidFields().isEmpty()) {
        this.setActionMessages(null);

        List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
        for (String key : keys) {
          this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
        }

      } else {
        this.addActionMessage("message:" + this.getText("saving.saved"));
      }

      messages = this.getActionMessages();

      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void saveMilestones(CenterOutcome outcomeSave) {
    if (outcomeSave.getResearchMilestones() != null && outcomeSave.getResearchMilestones().size() > 0) {
      List<CenterMilestone> milestonesPrew = new ArrayList<>(
        outcomeSave.getResearchMilestones().stream().filter(rm -> rm.isActive()).collect(Collectors.toList()));

      for (CenterMilestone researchMilestone : milestonesPrew) {
        if (!outcome.getMilestones().contains(researchMilestone)) {
          milestoneService.deleteCenterMilestone(researchMilestone.getId());
        }
      }
    }

    if (outcome.getMilestones() != null) {
      for (CenterMilestone researchMilestone : outcome.getMilestones()) {
        if (researchMilestone.getId() == null) {
          CenterMilestone milestone = new CenterMilestone();
          milestone.setResearchOutcome(outcomeSave);
          milestone.setActive(true);
          milestone.setActiveSince(new Date());
          milestone.setCreatedBy(this.getCurrentUser());
          milestone.setModifiedBy(this.getCurrentUser());
          milestone.setImpactPathway(true);

          CenterTargetUnit targetUnit = targetUnitService.getTargetUnitById(researchMilestone.getTargetUnit().getId());
          milestone.setTargetUnit(targetUnit);
          if (targetUnit.getId() != -1) {
            milestone.setValue(researchMilestone.getValue());
          } else {
            milestone.setValue(null);
          }
          milestone.setTargetYear(researchMilestone.getTargetYear());
          milestone.setTitle(researchMilestone.getTitle());

          milestoneService.saveCenterMilestone(milestone);
        } else {
          boolean hasChanges = false;
          CenterMilestone milestonePrew = milestoneService.getCenterMilestoneById(researchMilestone.getId());

          if (!milestonePrew.getTitle().equals(researchMilestone.getTitle())) {
            hasChanges = true;
            milestonePrew.setTitle(researchMilestone.getTitle());
          }

          CenterTargetUnit targetUnit = targetUnitService.getTargetUnitById(researchMilestone.getTargetUnit().getId());
          if (!milestonePrew.getTargetUnit().equals(targetUnit)) {
            hasChanges = true;
            milestonePrew.setTargetUnit(targetUnit);
            if (targetUnit.getId() == -1) {
              milestonePrew.setValue(null);
            }
          }

          if (targetUnit.getId() != -1) {
            if (milestonePrew.getValue() != null) {
              if (!milestonePrew.getValue().equals(researchMilestone.getValue())) {
                hasChanges = true;
                milestonePrew.setValue(researchMilestone.getValue());
              }
            } else {
              hasChanges = true;
              milestonePrew.setValue(researchMilestone.getValue());
            }
          }

          if (milestonePrew.getTargetYear() != null) {
            if (!milestonePrew.getTargetYear().equals(researchMilestone.getTargetYear())) {
              hasChanges = true;
              milestonePrew.setTargetYear(researchMilestone.getTargetYear());
            }
          } else {
            hasChanges = true;
            milestonePrew.setTargetYear(researchMilestone.getTargetYear());
          }


          if (hasChanges) {
            milestonePrew.setModifiedBy(this.getCurrentUser());
            milestoneService.saveCenterMilestone(milestonePrew);
          }
        }
      }
    }

  }

  public void setAreaID(long areaID) {
    this.areaID = areaID;
  }

  public void setLoggedCenter(Center loggedCenter) {
    this.loggedCenter = loggedCenter;
  }

  public void setOutcome(CenterOutcome outcome) {
    this.outcome = outcome;
  }

  public void setOutcomeID(long outcomeID) {
    this.outcomeID = outcomeID;
  }

  public void setProgramID(long programID) {
    this.programID = programID;
  }

  public void setResearchAreas(List<CenterArea> researchAreas) {
    this.researchAreas = researchAreas;
  }

  public void setResearchImpacts(List<CenterImpact> researchImpacts) {
    this.researchImpacts = researchImpacts;
  }

  public void setResearchPrograms(List<CenterProgram> researchPrograms) {
    this.researchPrograms = researchPrograms;
  }

  public void setResearchTopics(List<CenterTopic> researchTopics) {
    this.researchTopics = researchTopics;
  }

  public void setSelectedProgram(CenterProgram selectedProgram) {
    this.selectedProgram = selectedProgram;
  }

  public void setSelectedResearchArea(CenterArea selectedResearchArea) {
    this.selectedResearchArea = selectedResearchArea;
  }

  public void setSelectedResearchTopic(CenterTopic selectedResearchTopic) {
    this.selectedResearchTopic = selectedResearchTopic;
  }

  public void setTargetUnitList(HashMap<Long, String> targetUnitList) {
    this.targetUnitList = targetUnitList;
  }

  public void setTopicID(long topicID) {
    this.topicID = topicID;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  /**
   * method that sort a map list alphabetical
   * 
   * @param unsortMap - map to sort
   * @return
   */
  private HashMap<Long, String> sortByComparator(HashMap<Long, String> unsortMap) {

    // Convert Map to List
    List<HashMap.Entry<Long, String>> list = new LinkedList<HashMap.Entry<Long, String>>(unsortMap.entrySet());

    // Sort list with comparator, to compare the Map values
    Collections.sort(list, new Comparator<HashMap.Entry<Long, String>>() {

      @Override
      public int compare(HashMap.Entry<Long, String> o1, HashMap.Entry<Long, String> o2) {
        return (o1.getValue().toLowerCase().trim()).compareTo(o2.getValue().toLowerCase().trim());
      }
    });

    // Convert sorted map back to a Map
    HashMap<Long, String> sortedMap = new LinkedHashMap<Long, String>();
    for (Iterator<HashMap.Entry<Long, String>> it = list.iterator(); it.hasNext();) {
      HashMap.Entry<Long, String> entry = it.next();
      sortedMap.put(entry.getKey(), entry.getValue());
    }
    return sortedMap;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, outcome, selectedProgram, true);
    }
  }

}
