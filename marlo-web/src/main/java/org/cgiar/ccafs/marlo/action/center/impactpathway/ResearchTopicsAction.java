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

/**
 * 
 */
package org.cgiar.ccafs.marlo.action.center.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterTopicManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.CenterLeader;
import org.cgiar.ccafs.marlo.data.model.CenterLeaderTypeEnum;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.center.impactpathway.ResearchTopicsValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;


/**
 * This action handles the research topics
 * Modified by @author nmatovu last on Oct 3, 2016
 */
public class ResearchTopicsAction extends BaseAction {


  private static final long serialVersionUID = -3428452128710971531L;


  // Services (Managers)
  // GlobalUnit Manager
  private GlobalUnitManager centerService;

  private ICenterProgramManager programService;

  private ICenterAreaManager researchAreaService;
  private ICenterTopicManager researchTopicService;
  private AuditLogManager auditLogService;
  private UserManager userService;
  private ResearchTopicsValidator validator;
  // Local Variables
  private GlobalUnit loggedCenter;

  private List<CenterArea> researchAreas;
  private List<CenterTopic> topics;
  private List<CenterProgram> researchPrograms;
  private CenterArea selectedResearchArea;
  private CenterProgram selectedProgram;
  private long programID;
  private long areaID;
  private String transaction;


  @Inject
  public ResearchTopicsAction(APConfig config, GlobalUnitManager centerService, ICenterProgramManager programService,
    ICenterAreaManager researchAreaService, ICenterTopicManager researchTopicService, UserManager userService,
    ResearchTopicsValidator validator, AuditLogManager auditLogService) {
    super(config);
    this.centerService = centerService;
    this.programService = programService;
    this.researchAreaService = researchAreaService;
    this.researchTopicService = researchTopicService;
    this.userService = userService;
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
    String composedClassName = selectedProgram.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = selectedProgram.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public long getProgramID() {
    return programID;
  }


  public List<CenterArea> getResearchAreas() {
    return researchAreas;
  }


  public List<CenterProgram> getResearchPrograms() {
    return researchPrograms;
  }


  public CenterProgram getSelectedProgram() {
    return selectedProgram;
  }


  public CenterArea getSelectedResearchArea() {
    return selectedResearchArea;
  }


  public List<CenterTopic> getTopics() {
    return topics;
  }

  public String getTransaction() {
    return transaction;
  }

  @Override
  public void prepare() throws Exception {
    areaID = -1;
    programID = -1;

    loggedCenter = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCenter = centerService.getGlobalUnitById(loggedCenter.getId());

    researchAreas =
      new ArrayList<>(loggedCenter.getCenterAreas().stream().filter(ra -> ra.isActive()).collect(Collectors.toList()));

    Collections.sort(researchAreas, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));

    if (researchAreas != null) {

      try {
        areaID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CENTER_AREA_ID)));
      } catch (Exception e) {
        try {
          programID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CENTER_PROGRAM_ID)));
        } catch (Exception ex) {
          User user = userService.getUser(this.getCurrentUser().getId());

          // Check if the User is an Area Leader
          List<CenterLeader> userAreaLeads =
            new ArrayList<>(user.getResearchLeaders().stream()
              .filter(rl -> rl.isActive()
                && rl.getType().getId() == CenterLeaderTypeEnum.RESEARCH_AREA_LEADER_TYPE.getValue())
              .collect(Collectors.toList()));
          if (!userAreaLeads.isEmpty()) {
            areaID = userAreaLeads.get(0).getResearchArea().getId();
          } else {
            // Check if the User is a Program Leader
            List<CenterLeader> userProgramLeads = new ArrayList<>(user.getResearchLeaders().stream()
              .filter(rl -> rl.isActive()
                && rl.getType().getId() == CenterLeaderTypeEnum.RESEARCH_PROGRAM_LEADER_TYPE.getValue())
              .collect(Collectors.toList()));
            if (!userProgramLeads.isEmpty()) {
              programID = userProgramLeads.get(0).getResearchProgram().getId();
            } else {
              // Check if the User is a Scientist Leader
              List<CenterLeader> userScientistLeader = new ArrayList<>(user.getResearchLeaders().stream()
                .filter(rl -> rl.isActive()
                  && rl.getType().getId() == CenterLeaderTypeEnum.PROGRAM_SCIENTIST_LEADER_TYPE.getValue())
                .collect(Collectors.toList()));
              if (!userScientistLeader.isEmpty()) {
                programID = userScientistLeader.get(0).getResearchProgram().getId();
              } else {
                List<CenterProgram> rps = researchAreas.get(0).getResearchPrograms().stream().filter(r -> r.isActive())
                  .collect(Collectors.toList());
                Collections.sort(rps, (rp1, rp2) -> rp1.getId().compareTo(rp2.getId()));
                CenterProgram rp = rps.get(0);
                programID = rp.getId();
                areaID = rp.getResearchArea().getId();
              }
            }
          }
        }
      }

      if (areaID != -1 && programID == -1) {
        selectedResearchArea = researchAreaService.find(areaID);
        researchPrograms = new ArrayList<>(
          selectedResearchArea.getResearchPrograms().stream().filter(rp -> rp.isActive()).collect(Collectors.toList()));
        Collections.sort(researchPrograms, (rp1, rp2) -> rp1.getId().compareTo(rp2.getId()));
        if (researchPrograms != null) {
          try {
            programID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CENTER_PROGRAM_ID)));
          } catch (Exception e) {
            User user = userService.getUser(this.getCurrentUser().getId());

            List<CenterLeader> userLeads = new ArrayList<>(user.getResearchLeaders().stream()
              .filter(rl -> rl.isActive()
                && rl.getType().getId() == CenterLeaderTypeEnum.RESEARCH_PROGRAM_LEADER_TYPE.getValue())
              .collect(Collectors.toList()));

            if (!userLeads.isEmpty()) {
              programID = userLeads.get(0).getResearchProgram().getId();
            } else {
              if (!researchPrograms.isEmpty()) {
                programID = researchPrograms.get(0).getId();
              }
            }
          }
        }
        if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

          transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
          CenterProgram history = (CenterProgram) auditLogService.getHistory(transaction);

          if (history != null) {
            selectedProgram = history;
            areaID = selectedProgram.getResearchArea().getId();
            selectedResearchArea = researchAreaService.find(areaID);
          } else {
            this.transaction = null;
            this.setTransaction("-1");
          }

        } else {
          if (programID != -1) {
            selectedProgram = programService.getProgramById(programID);
          }
        }

      } else {

        if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

          transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
          CenterProgram history = (CenterProgram) auditLogService.getHistory(transaction);

          if (history != null) {
            selectedProgram = history;
            areaID = selectedProgram.getResearchArea().getId();
            selectedResearchArea = researchAreaService.find(areaID);
          } else {
            this.transaction = null;
            this.setTransaction("-1");
          }

        } else {

          if (programID != -1) {

            selectedProgram = programService.getProgramById(programID);
            areaID = selectedProgram.getResearchArea().getId();
            selectedResearchArea = researchAreaService.find(areaID);


          }
        }


      }

      if (selectedProgram != null) {
        Path path = this.getAutoSaveFilePath();

        if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
          BufferedReader reader = null;
          reader = new BufferedReader(new FileReader(path.toFile()));
          Gson gson = new GsonBuilder().create();
          JsonObject jReader = gson.fromJson(reader, JsonObject.class);
          reader.close();

          AutoSaveReader autoSaveReader = new AutoSaveReader();

          selectedProgram = (CenterProgram) autoSaveReader.readFromJson(jReader);

          topics = new ArrayList<>(selectedProgram.getTopics());


          this.setDraft(true);
        } else {
          this.setDraft(false);
          researchPrograms = new ArrayList<>(selectedResearchArea.getResearchPrograms().stream()
            .filter(rp -> rp.isActive()).collect(Collectors.toList()));
          if (selectedProgram != null) {
            if (selectedProgram.getResearchTopics() != null) {
              topics = new ArrayList<>(
                selectedProgram.getResearchTopics().stream().filter(rt -> rt.isActive()).collect(Collectors.toList()));
            }
          }
        }
      }

      Collections.sort(topics, (ra1, ra2) -> ra1.getOrder().compareTo(ra2.getOrder()));
    }


    String params[] = {loggedCenter.getAcronym(), selectedResearchArea.getId() + "", selectedProgram.getId() + ""};
    this.setBasePermission(this.getText(Permission.RESEARCH_PROGRAM_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (researchAreas != null) {
        researchAreas.clear();
      }
      if (researchPrograms != null) {
        researchPrograms.clear();
      }
      if (topics != null) {
        topics.clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {

      List<CenterTopic> researchTopicsPrew;

      selectedProgram = programService.getProgramById(programID);

      if (selectedProgram.getResearchTopics() != null) {
        researchTopicsPrew =
          selectedProgram.getResearchTopics().stream().filter(rt -> rt.isActive()).collect(Collectors.toList());

        for (CenterTopic researchTopic : researchTopicsPrew) {
          if (!topics.contains(researchTopic)) {
            researchTopicService.deleteResearchTopic(researchTopic.getId());
          }
        }
      }


      for (CenterTopic researchTopic : topics) {
        if (researchTopic.getId() == null || researchTopic.getId() == -1) {
          CenterTopic newResearchTopic = new CenterTopic();
          newResearchTopic.setActive(true);
          newResearchTopic.setActiveSince(new Date());
          newResearchTopic.setCreatedBy(this.getCurrentUser());
          newResearchTopic.setModifiedBy(this.getCurrentUser());
          newResearchTopic.setResearchTopic(researchTopic.getResearchTopic().trim());
          newResearchTopic.setColor("#ecf0f1");
          newResearchTopic.setResearchProgram(selectedProgram);
          newResearchTopic.setShortName(researchTopic.getShortName().trim());
          newResearchTopic.setOrder(researchTopic.getOrder());

          researchTopicService.saveResearchTopic(newResearchTopic);
        } else {
          boolean hasChanges = false;

          CenterTopic researchTopicPrew = researchTopicService.getResearchTopicById(researchTopic.getId());

          if (!researchTopicPrew.getResearchTopic().equals(researchTopic.getResearchTopic().trim())) {
            hasChanges = true;
            researchTopicPrew.setResearchTopic(researchTopic.getResearchTopic().trim());
          }

          if (researchTopicPrew.getShortName() == null
            || !researchTopicPrew.getShortName().equals(researchTopic.getShortName().trim())) {
            hasChanges = true;
            researchTopicPrew.setShortName(researchTopic.getShortName().trim());
          }

          if (researchTopicPrew.getOrder() == null || researchTopicPrew.getOrder() != researchTopic.getOrder()) {
            hasChanges = true;
            researchTopicPrew.setOrder(researchTopic.getOrder());
          }

          if (hasChanges) {
            researchTopicPrew.setModifiedBy(this.getCurrentUser());
            researchTopicPrew.setModificationJustification("Modified on " + new Date().toString());
            researchTopicService.saveResearchTopic(researchTopicPrew);
          }
        }

      }

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.RESEARCH_PROGRAM_TOPIC_RELATION);
      selectedProgram = programService.getProgramById(programID);
      selectedProgram.setActiveSince(new Date());
      selectedProgram.setModifiedBy(this.getCurrentUser());
      programService.saveProgram(selectedProgram, this.getActionName(), relationsName);

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      // check if there is a url to redirect
      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        // check if there are missing field
        if (!this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }
        } else {
          this.addActionMessage("message:" + this.getText("saving.saved"));
        }
        return SUCCESS;
      } else {
        // No messages to next page
        this.addActionMessage("");
        this.setActionMessages(null);
        // redirect the url select by user
        return REDIRECT;
      }
    } else {
      this.setActionMessages(null);
      return NOT_AUTHORIZED;
    }

  }

  public void setAreaID(long areaID) {
    this.areaID = areaID;
  }


  public void setProgramID(long programID) {
    this.programID = programID;
  }

  public void setResearchAreas(List<CenterArea> researchAreas) {
    this.researchAreas = researchAreas;
  }

  public void setResearchPrograms(List<CenterProgram> researchPrograms) {
    this.researchPrograms = researchPrograms;
  }

  public void setSelectedProgram(CenterProgram selectedProgram) {
    this.selectedProgram = selectedProgram;
  }

  public void setSelectedResearchArea(CenterArea selectedResearchArea) {
    this.selectedResearchArea = selectedResearchArea;
  }

  public void setTopics(List<CenterTopic> topics) {
    this.topics = topics;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, topics, selectedProgram, true);
    }
  }
}
