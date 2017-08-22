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

package org.cgiar.ccafs.marlo.action.center.monitoring.outcome;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterImpactManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterMonitoringMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterMonitoringOutcomeEvidenceManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterMonitoringOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterTargetUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterTopicManager;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.CenterImpact;
import org.cgiar.ccafs.marlo.data.model.CenterMilestone;
import org.cgiar.ccafs.marlo.data.model.CenterMonitoringMilestone;
import org.cgiar.ccafs.marlo.data.model.CenterMonitoringOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterMonitoringOutcomeEvidence;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterTargetUnit;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
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
public class MonitoringOutcomeAction extends BaseAction {


  private static final long serialVersionUID = 8641483319512138926L;


  private ICenterManager centerService;

  private ICenterOutcomeManager outcomeService;


  private AuditLogManager auditLogService;
  private ICenterTargetUnitManager targetUnitService;
  private ICenterTopicManager researchTopicService;
  private ICenterProgramManager programService;
  private ICenterImpactManager impactService;
  private ICenterMilestoneManager milestoneService;
  private ICenterMonitoringOutcomeManager monitoringOutcomeService;
  private ICenterMonitoringMilestoneManager monitoringMilestoneService;
  private ICenterMonitoringOutcomeEvidenceManager evidenceService;
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

  @Inject
  public MonitoringOutcomeAction(APConfig config, ICenterManager centerService, ICenterOutcomeManager outcomeService,
    ICenterTargetUnitManager targetUnitService, ICenterTopicManager researchTopicService,
    ICenterProgramManager programService, ICenterImpactManager impactService, ICenterMilestoneManager milestoneService,
    AuditLogManager auditLogService, ICenterMonitoringOutcomeManager monitoringOutcomeService,
    ICenterMonitoringMilestoneManager monitoringMilestoneService,
    ICenterMonitoringOutcomeEvidenceManager evidenceService) {
    super(config);
    this.centerService = centerService;
    this.outcomeService = outcomeService;
    this.targetUnitService = targetUnitService;
    this.researchTopicService = researchTopicService;
    this.programService = programService;
    this.impactService = impactService;
    this.milestoneService = milestoneService;
    this.auditLogService = auditLogService;
    this.evidenceService = evidenceService;
    this.monitoringMilestoneService = monitoringMilestoneService;
    this.monitoringOutcomeService = monitoringOutcomeService;
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

  public void fillFrontValues() {
    programID = outcome.getResearchTopic().getResearchProgram().getId();
    selectedProgram = programService.getProgramById(programID);
    selectedResearchTopic = outcome.getResearchTopic();
    topicID = selectedResearchTopic.getId();
    selectedResearchArea = selectedProgram.getResearchArea();
    areaID = selectedResearchArea.getId();

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

  public void fillOutcomeYear() {
    outcome.setMonitorings(new ArrayList<>());


    Calendar calendarStart = Calendar.getInstance();
    calendarStart.set(Calendar.YEAR, this.getCenterYear());
    Calendar calendarEnd = Calendar.getInstance();
    calendarEnd.set(Calendar.YEAR, outcome.getTargetYear());

    while (calendarStart.get(Calendar.YEAR) <= calendarEnd.get(Calendar.YEAR)) {
      CenterMonitoringOutcome monitoringOutcome = new CenterMonitoringOutcome();

      monitoringOutcome.setActive(true);
      monitoringOutcome.setYear(calendarStart.get(Calendar.YEAR));
      monitoringOutcome.setResearchOutcome(outcome);

      monitoringOutcome.setCreatedBy(this.getCurrentUser());
      monitoringOutcome.setModifiedBy(this.getCurrentUser());
      monitoringOutcome.setActiveSince(new Date());
      monitoringOutcome.setModificationJustification("");


      monitoringOutcome = monitoringOutcomeService.saveMonitoringOutcome(monitoringOutcome);

      List<CenterMilestone> milestones = new ArrayList<>(outcome.getResearchMilestones().stream()
        .filter(rm -> rm.isActive() && rm.getTargetYear() >= calendarStart.get(Calendar.YEAR))
        .collect(Collectors.toList()));
      Collections.sort(milestones, (mi1, mi2) -> mi1.getId().compareTo(mi2.getId()));

      for (CenterMilestone researchMilestone : milestones) {

        CenterMonitoringMilestone monitoringMilestone = new CenterMonitoringMilestone();

        monitoringMilestone.setActive(true);
        monitoringMilestone.setResearchMilestone(researchMilestone);
        monitoringMilestone.setMonitoringOutcome(monitoringOutcome);

        monitoringMilestone.setCreatedBy(this.getCurrentUser());
        monitoringMilestone.setModifiedBy(this.getCurrentUser());
        monitoringMilestone.setActiveSince(new Date());
        monitoringMilestone.setModificationJustification("");

        monitoringMilestoneService.saveMonitoringMilestone(monitoringMilestone);

      }

      monitoringOutcome.setMilestones(new ArrayList<>(
        monitoringOutcome.getMonitoringMilestones().stream().filter(mm -> mm.isActive()).collect(Collectors.toList())));
      monitoringOutcome.setEvidences(new ArrayList<>());


      outcome.getMonitorings().add(monitoringOutcome);

      calendarStart.add(Calendar.YEAR, 1);
    }
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


    if (outcome != null) {

      this.fillFrontValues();

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave() && this.isEditable()) {
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        AutoSaveReader autoSaveReader = new AutoSaveReader();

        outcome = (CenterOutcome) autoSaveReader.readFromJson(jReader);

        if (outcome.getMonitorings() != null || !outcome.getMonitorings().isEmpty()) {

          List<CenterMonitoringOutcome> monitoringOutcomes = new ArrayList<>();

          for (CenterMonitoringOutcome monitoringOutcome : outcome.getMonitorings()) {
            CenterMonitoringOutcome outcome =
              monitoringOutcomeService.getMonitoringOutcomeById(monitoringOutcome.getId());
            monitoringOutcome.setYear(outcome.getYear());

            monitoringOutcomes.add(monitoringOutcome);
          }

          outcome.setMonitorings(new ArrayList<>(monitoringOutcomes));
        }

        reader.close();
        this.setDraft(true);

      } else {
        this.setDraft(false);
        outcome.setMonitorings(new ArrayList<>(
          outcome.getMonitoringOutcomes().stream().filter(mo -> mo.isActive()).collect(Collectors.toList())));

        if (outcome.getMonitorings() == null || outcome.getMonitorings().isEmpty()) {
          this.fillOutcomeYear();
        } else {
          for (CenterMonitoringOutcome monitoringOutcome : outcome.getMonitorings()) {
            monitoringOutcome.setEvidences(new ArrayList<>(monitoringOutcome.getMonitorignOutcomeEvidences().stream()
              .filter(me -> me.isActive()).collect(Collectors.toList())));

            monitoringOutcome.setMilestones(new ArrayList<>(monitoringOutcome.getMonitoringMilestones().stream()
              .filter(mm -> mm.isActive()).collect(Collectors.toList())));

            Collections.sort(monitoringOutcome.getMilestones(), (mil1, mil2) -> mil1.getResearchMilestone()
              .getTargetYear().compareTo(mil2.getResearchMilestone().getTargetYear()));

          }

          Collections.sort(outcome.getMonitorings(),
            (mon1, mon2) -> (new Integer(mon1.getYear())).compareTo(new Integer(mon2.getYear())));
          for (CenterMonitoringOutcome monitoringOutcome : outcome.getMonitorings()) {
            Collections.sort(monitoringOutcome.getMilestones(), (mil1, mil2) -> mil1.getResearchMilestone()
              .getTargetYear().compareTo(mil2.getResearchMilestone().getTargetYear()));
          }
        }
      }


    }


    String params[] = {loggedCenter.getAcronym(), selectedResearchArea.getId() + "", selectedProgram.getId() + ""};
    this.setBasePermission(this.getText(Permission.RESEARCH_PROGRAM_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (targetUnitList != null) {
        targetUnitList.clear();
      }

      if (outcome.getMonitorings() != null) {
        outcome.getMonitorings().clear();
      }
    }


  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {

      this.setInvalidFields(new HashMap<>());

      CenterOutcome outcomeDB = outcomeService.getResearchOutcomeById(outcomeID);
      outcomeDB.setBaseline(outcome.getBaseline());

      outcomeService.saveResearchOutcome(outcomeDB);

      if (outcome.getMonitorings() != null || !outcome.getMonitorings().isEmpty()) {
        for (CenterMonitoringOutcome monitoringOutcome : outcome.getMonitorings()) {

          CenterMonitoringOutcome monitoringOutcomeDB =
            monitoringOutcomeService.getMonitoringOutcomeById(monitoringOutcome.getId());

          List<CenterMonitoringMilestone> monitoringMilestones = monitoringOutcome.getMilestones();

          if (monitoringMilestones != null) {
            for (CenterMonitoringMilestone monitoringMilestone : monitoringMilestones) {

              CenterMonitoringMilestone monitoringMilestoneDB =
                monitoringMilestoneService.getMonitoringMilestoneById(monitoringMilestone.getId());

              monitoringMilestoneDB.setAchievedValue(monitoringMilestone.getAchievedValue());
              monitoringMilestoneDB.setNarrative(monitoringMilestone.getNarrative());

              monitoringMilestoneService.saveMonitoringMilestone(monitoringMilestoneDB);

            }
          }


          monitoringOutcomeDB.setNarrative(monitoringOutcome.getNarrative());
          monitoringOutcomeService.saveMonitoringOutcome(monitoringOutcomeDB);

          this.saveEvidences(monitoringOutcomeDB, monitoringOutcome);


        }
      }

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.OUTCOME_MONITORING_RELATION);
      outcome = outcomeService.getResearchOutcomeById(outcomeID);
      outcome.setActiveSince(new Date());
      outcome.setModifiedBy(this.getCurrentUser());
      outcomeService.saveResearchOutcome(outcome, this.getActionName(), relationsName);

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      if (!this.getInvalidFields().isEmpty()) {
        this.setActionMessages(null);
        List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
        for (String key : keys) {
          this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
        }
      } else {
        this.addActionMessage("message:" + this.getText("saving.saved"));
      }

      return SUCCESS;
    } else {

      return NOT_AUTHORIZED;
    }
  }

  public void saveEvidences(CenterMonitoringOutcome monitoringOutcomeDB, CenterMonitoringOutcome monitoringOutcome) {

    if (monitoringOutcomeDB.getEvidences() != null && monitoringOutcomeDB.getEvidences().size() > 0) {

      List<CenterMonitoringOutcomeEvidence> evidences = new ArrayList<>(monitoringOutcomeDB
        .getMonitorignOutcomeEvidences().stream().filter(ev -> ev.isActive()).collect(Collectors.toList()));

      for (CenterMonitoringOutcomeEvidence monitorignOutcomeEvidence : evidences) {
        if (!monitoringOutcome.getEvidences().contains(monitorignOutcomeEvidence)) {
          evidenceService.deleteMonitorignOutcomeEvidence(monitorignOutcomeEvidence.getId());
        }
      }
    }


    if (monitoringOutcome.getEvidences() != null) {
      for (CenterMonitoringOutcomeEvidence monitorignOutcomeEvidence : monitoringOutcome.getEvidences()) {

        if (monitorignOutcomeEvidence.getId() == null || monitorignOutcomeEvidence.getId() == -1) {


          CenterMonitoringOutcomeEvidence evidenceSave = new CenterMonitoringOutcomeEvidence();

          evidenceSave.setActive(true);
          evidenceSave.setCreatedBy(this.getCurrentUser());
          evidenceSave.setModifiedBy(this.getCurrentUser());
          evidenceSave.setActiveSince(new Date());
          evidenceSave.setModificationJustification("");
          evidenceSave.setEvidenceLink(monitorignOutcomeEvidence.getEvidenceLink());

          evidenceSave.setMonitoringOutcome(monitoringOutcomeDB);


          evidenceService.saveMonitorignOutcomeEvidence(evidenceSave);

        } else {
          boolean hasChanges = false;
          CenterMonitoringOutcomeEvidence evidencePrew =
            evidenceService.getMonitorignOutcomeEvidenceById(monitorignOutcomeEvidence.getId());

          if (!evidencePrew.getEvidenceLink().equals(monitorignOutcomeEvidence.getEvidenceLink())) {
            hasChanges = true;
            evidencePrew.setEvidenceLink(monitorignOutcomeEvidence.getEvidenceLink());
          }

          if (hasChanges) {
            evidencePrew.setModifiedBy(this.getCurrentUser());
            evidencePrew.setActiveSince(new Date());
            evidenceService.saveMonitorignOutcomeEvidence(evidencePrew);
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

}
