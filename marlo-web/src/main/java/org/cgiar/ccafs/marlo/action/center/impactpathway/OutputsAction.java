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
import org.cgiar.ccafs.marlo.data.manager.ICenterNextuserTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutputManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutputsNextUserManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.CenterLeader;
import org.cgiar.ccafs.marlo.data.model.CenterNextuserType;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;
import org.cgiar.ccafs.marlo.data.model.CenterOutputsNextUser;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.center.impactpathway.OutputsValidator;

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
 * The action that is responsible for handling the outputs.
 * Modified by @author nmatovu last on Oct 3, 2016
 */
public class OutputsAction extends BaseAction {


  private static final long serialVersionUID = 4999336618747960173L;


  // Services - Managers
  // GlobalUnit Manager
  private GlobalUnitManager centerService;


  private AuditLogManager auditLogService;


  private ICenterProgramManager programService;


  private ICenterOutputManager outputService;


  private ICenterNextuserTypeManager nextUserService;
  private ICenterOutputsNextUserManager outputNextUserService;
  // Front Variables
  private GlobalUnit loggedCenter;
  private List<CenterArea> researchAreas;
  private CenterArea selectedResearchArea;
  private List<CenterProgram> researchPrograms;

  private CenterProgram selectedProgram;
  private CenterOutcome selectedResearchOutcome;
  private CenterOutput output;
  private CenterTopic selectedResearchTopic;
  private List<CenterLeader> contacPersons;
  private List<CenterNextuserType> nextuserTypes;
  // Parameter Variables
  private long programID;
  private long areaID;
  private long outcomeID;
  private long outputID;
  private String transaction;
  private long nextUserTypeID;
  // Validator
  private OutputsValidator validator;

  /**
   * @param config
   */
  @Inject
  public OutputsAction(APConfig config, GlobalUnitManager centerService, AuditLogManager auditLogService,
    ICenterProgramManager programService, ICenterOutputManager outputService, OutputsValidator validator,
    ICenterNextuserTypeManager nextUserService, ICenterOutputsNextUserManager outputNextUserService) {
    super(config);
    this.centerService = centerService;
    this.auditLogService = auditLogService;
    this.programService = programService;
    this.outputService = outputService;
    this.validator = validator;
    this.nextUserService = nextUserService;
    this.outputNextUserService = outputNextUserService;
  }

  public String addNextUsers() {

    return SUCCESS;
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
    String composedClassName = output.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = output.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<CenterLeader> getContacPersons() {
    return contacPersons;
  }

  public GlobalUnit getLoggedCenter() {
    return loggedCenter;
  }

  public long getNextUserID() {
    return nextUserTypeID;
  }


  public List<CenterNextuserType> getNextuserTypes() {
    return nextuserTypes;
  }

  public long getOutcomeID() {
    return outcomeID;
  }

  public CenterOutput getOutput() {
    return output;
  }

  public long getOutputID() {
    return outputID;
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


  public CenterOutcome getSelectedResearchOutcome() {
    return selectedResearchOutcome;
  }

  public CenterTopic getSelectedResearchTopic() {
    return selectedResearchTopic;
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

    try {
      outputID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.OUTPUT_ID)));
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      CenterOutput history = (CenterOutput) auditLogService.getHistory(transaction);

      if (history != null) {
        output = history;
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }

    } else {
      output = outputService.getResearchOutputById(outputID);
    }
    researchAreas =
      new ArrayList<>(loggedCenter.getCenterAreas().stream().filter(ra -> ra.isActive()).collect(Collectors.toList()));
    Collections.sort(researchAreas, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));

    if (researchAreas != null && output != null) {

      selectedResearchOutcome = output.getResearchOutcome();
      outcomeID = selectedResearchOutcome.getId();
      programID = selectedResearchOutcome.getResearchTopic().getResearchProgram().getId();
      selectedProgram = programService.getProgramById(programID);
      selectedResearchTopic = selectedResearchOutcome.getResearchTopic();
      selectedResearchArea = selectedProgram.getResearchArea();
      areaID = selectedResearchArea.getId();

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();

        AutoSaveReader autoSaveReader = new AutoSaveReader();

        output = (CenterOutput) autoSaveReader.readFromJson(jReader);

        if (output != null) {
          if (output.getNextUsers() != null) {
            List<CenterOutputsNextUser> ouputNextUsers = new ArrayList<>(output.getNextUsers());
            List<CenterOutputsNextUser> autoSaveOutputNextrUsers = new ArrayList<>();
            for (CenterOutputsNextUser outputNextUser : ouputNextUsers) {
              CenterNextuserType nextuserType = null;
              if (outputNextUser.getNextuserType().getId() != null) {
                nextuserType = nextUserService.getNextuserTypeById(outputNextUser.getNextuserType().getId());
              }

              CenterOutputsNextUser autoSaveOutputNextUser = new CenterOutputsNextUser();

              autoSaveOutputNextUser.setNextuserType(nextuserType);

              if (outputNextUser.getId() != null) {
                autoSaveOutputNextUser.setId(outputNextUser.getId());
              }

              autoSaveOutputNextrUsers.add(autoSaveOutputNextUser);

            }

            output.setNextUsers(new ArrayList<>(autoSaveOutputNextrUsers));
          }
        }


        this.setDraft(true);
      } else {
        this.setDraft(false);

        output.setNextUsers(new ArrayList<>(
          output.getResearchOutputsNextUsers().stream().filter(nu -> nu.isActive()).collect(Collectors.toList())));
      }

      contacPersons = new ArrayList<>(
        selectedProgram.getResearchLeaders().stream().filter(rl -> rl.isActive()).collect(Collectors.toList()));

      if (nextUserService.findAll() != null) {
        nextuserTypes = new ArrayList<>(nextUserService.findAll().stream()
          .filter(nu -> nu.isActive() && nu.getNextuserType() == null).collect(Collectors.toList()));
      }

      String params[] = {loggedCenter.getAcronym(), selectedResearchArea.getId() + "", selectedProgram.getId() + ""};
      this.setBasePermission(this.getText(Permission.RESEARCH_PROGRAM_BASE_PERMISSION, params));

      if (this.isHttpPost()) {
        if (contacPersons != null) {
          contacPersons.clear();
        }

        if (nextuserTypes != null) {
          nextuserTypes.clear();
        }

        if (output.getNextUsers() != null) {
          output.getNextUsers().clear();
        }
      }
    }

  }


  @Override
  public String save() {
    if (this.hasPermission("*")) {

      CenterOutput outputDb = outputService.getResearchOutputById(outputID);

      outputDb.setTitle(output.getTitle());

      outputDb.setShortName(output.getShortName());

      outputDb = outputService.saveResearchOutput(outputDb);

      this.saveNextUser(outputDb);

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.RESEARCH_OUTPUT_NEXTUSER_RELATION);
      output = outputService.getResearchOutputById(outputID);
      output.setActiveSince(new Date());
      output.setModifiedBy(this.getCurrentUser());
      outputService.saveResearchOutput(output, this.getActionName(), relationsName);

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
      return NOT_AUTHORIZED;
    }

  }

  public void saveNextUser(CenterOutput outputSave) {

    if (outputSave.getResearchOutputsNextUsers() != null && outputSave.getResearchOutputsNextUsers().size() > 0) {

      List<CenterOutputsNextUser> nextUsersPrev = new ArrayList<>(
        outputSave.getResearchOutputsNextUsers().stream().filter(nu -> nu.isActive()).collect(Collectors.toList()));

      for (CenterOutputsNextUser researchOutputsNextUser : nextUsersPrev) {
        if (!output.getNextUsers().contains(researchOutputsNextUser)) {
          outputNextUserService.deleteResearchOutputsNextUser(researchOutputsNextUser.getId());
        }
      }
    }

    if (output.getNextUsers() != null) {
      for (CenterOutputsNextUser outputNextUser : output.getNextUsers()) {
        if (outputNextUser.getId() == null) {
          CenterOutputsNextUser nextUserNew = new CenterOutputsNextUser();
          nextUserNew.setActive(true);
          nextUserNew.setActiveSince(new Date());
          nextUserNew.setCreatedBy(this.getCurrentUser());
          nextUserNew.setModifiedBy(this.getCurrentUser());
          nextUserNew.setModificationJustification("");

          nextUserNew.setResearchOutput(outputSave);
          CenterNextuserType nextuserType = null;
          if (outputNextUser.getNextuserType().getId() != -1) {
            nextuserType = nextUserService.getNextuserTypeById(outputNextUser.getNextuserType().getId());
          }
          nextUserNew.setNextuserType(nextuserType);
          outputNextUserService.saveResearchOutputsNextUser(nextUserNew);

        } else {
          boolean hasChanges = false;

          CenterOutputsNextUser nextUserPrev =
            outputNextUserService.getResearchOutputsNextUserById(outputNextUser.getId());

          CenterNextuserType nextuserType = null;
          if (outputNextUser.getNextuserType().getId() != -1) {
            nextuserType = nextUserService.getNextuserTypeById(outputNextUser.getNextuserType().getId());
          }

          if (nextUserPrev.getNextuserType() != null) {
            if (!nextUserPrev.getNextuserType().equals(nextuserType)) {
              nextUserPrev.setNextuserType(nextuserType);
              hasChanges = true;
            }
          } else {
            nextUserPrev.setNextuserType(nextuserType);
            hasChanges = true;
          }

          if (hasChanges) {
            nextUserPrev.setModifiedBy(this.getCurrentUser());
            nextUserPrev.setActiveSince(new Date());
            outputNextUserService.saveResearchOutputsNextUser(nextUserPrev);
          }

        }

      }
    }

  }

  public void setAreaID(long areaID) {
    this.areaID = areaID;
  }


  public void setContacPersons(List<CenterLeader> contacPersons) {
    this.contacPersons = contacPersons;
  }

  public void setLoggedCenter(GlobalUnit loggedCenter) {
    this.loggedCenter = loggedCenter;
  }


  public void setNextUserID(long nextUserID) {
    this.nextUserTypeID = nextUserID;
  }

  public void setNextuserTypes(List<CenterNextuserType> nextuserTypes) {
    this.nextuserTypes = nextuserTypes;
  }


  public void setOutcomeID(long outcomeID) {
    this.outcomeID = outcomeID;
  }

  public void setOutput(CenterOutput output) {
    this.output = output;
  }

  public void setOutputID(long outputID) {
    this.outputID = outputID;
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


  public void setSelectedResearchOutcome(CenterOutcome selectedResearchOutcome) {
    this.selectedResearchOutcome = selectedResearchOutcome;
  }


  public void setSelectedResearchTopic(CenterTopic selectedResearchTopic) {
    this.selectedResearchTopic = selectedResearchTopic;
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }


  @Override
  public void validate() {
    if (save) {
      validator.validate(this, output, selectedProgram, true);
    }
  }
}
