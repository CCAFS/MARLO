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

package org.cgiar.ccafs.marlo.action.center.monitoring.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterProjectPartner;
import org.cgiar.ccafs.marlo.data.model.CenterProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.center.monitoring.project.ProjectPartnerValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
public class ProjectPartnersAction extends BaseAction {


  private static final long serialVersionUID = -6355291584207636077L;


  private static final String INTERNAL = "Internal";

  private static final String EXTERNAL = "External";


  // Services - Managers
  private ICenterManager centerService;


  private ICenterProjectManager projectService;
  private AuditLogManager auditLogService;
  private ICenterProjectPartnerManager partnerService;
  private ICenterProjectPartnerPersonManager partnerPersonService;
  private InstitutionManager institutionService;
  private UserManager userService;

  // Front Variables
  private Center loggedCenter;
  private CenterArea selectedResearchArea;
  private CenterProgram selectedProgram;
  private List<CenterArea> researchAreas;
  private List<CenterProgram> researchPrograms;
  private List<Institution> institutions;
  private List<CenterProjectPartner> projectPartners;
  private HashMap<Boolean, String> partnerModes;
  private CenterProject project;

  // Parameter Variables
  private long programID;
  private long areaID;
  private long projectID;
  private String transaction;

  // Validator
  private ProjectPartnerValidator validator;

  @Inject
  public ProjectPartnersAction(APConfig config, ICenterManager centerService, ICenterProjectManager projectService,
    ICenterProjectPartnerManager partnerService, ICenterProjectPartnerPersonManager partnerPersonService,
    InstitutionManager institutionService, UserManager userService, ProjectPartnerValidator validator,
    AuditLogManager auditLogService) {
    super(config);
    this.centerService = centerService;
    this.projectService = projectService;
    this.partnerService = partnerService;
    this.partnerPersonService = partnerPersonService;
    this.institutionService = institutionService;
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
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public List<Institution> getInstitutions() {
    return institutions;
  }

  public Center getLoggedCenter() {
    return loggedCenter;
  }

  public HashMap<Boolean, String> getPartnerModes() {
    return partnerModes;
  }


  public long getProgramID() {
    return programID;
  }

  public CenterProject getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  public List<CenterProjectPartner> getProjectPartners() {
    return projectPartners;
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

  public String getTransaction() {
    return transaction;
  }

  @Override
  public void prepare() throws Exception {
    loggedCenter = (Center) this.getSession().get(APConstants.SESSION_CENTER);
    loggedCenter = centerService.getCrpById(loggedCenter.getId());

    researchAreas = new ArrayList<>(
      loggedCenter.getResearchAreas().stream().filter(ra -> ra.isActive()).collect(Collectors.toList()));

    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_ID)));
    } catch (Exception e) {
      projectID = -1;
    }

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      CenterProject history = (CenterProject) auditLogService.getHistory(transaction);

      if (history != null) {
        project = history;
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }

    } else {
      project = projectService.getCenterProjectById(projectID);
    }

    partnerModes = new HashMap<Boolean, String>();
    partnerModes.put(true, INTERNAL);
    partnerModes.put(false, EXTERNAL);

    if (project != null) {

      CenterProject ProjectDB = projectService.getCenterProjectById(projectID);
      selectedProgram = ProjectDB.getResearchProgram();
      programID = selectedProgram.getId();
      selectedResearchArea = selectedProgram.getResearchArea();
      areaID = selectedResearchArea.getId();
      researchPrograms = new ArrayList<>(
        selectedResearchArea.getResearchPrograms().stream().filter(rp -> rp.isActive()).collect(Collectors.toList()));

      if (institutionService.findAll() != null) {
        institutions = new ArrayList<>(institutionService.findAll());
      }

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave() && this.isEditable()) {
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        AutoSaveReader autoSaveReader = new AutoSaveReader();

        project = (CenterProject) autoSaveReader.readFromJson(jReader);
        CenterProject projectDB = projectService.getCenterProjectById(project.getId());

        if (project.getPartners() != null) {

          List<CenterProjectPartner> partners = new ArrayList<>();

          for (CenterProjectPartner partner : project.getPartners()) {
            Institution institution = institutionService.getInstitutionById(partner.getInstitution().getId());
            partner.setInstitution(institution);
            if (partner.getUsers() != null) {
              for (CenterProjectPartnerPerson person : partner.getUsers()) {
                User user = userService.getUser(person.getUser().getId());
                person.setUser(user);
              }
            }

            partners.add(partner);
          }

          project.setPartners(new ArrayList<>(partners));
        }


        reader.close();
        this.setDraft(true);
      } else {

        this.setDraft(false);
        project.setPartners(new ArrayList<>(
          project.getProjectPartners().stream().filter(pp -> pp.isActive()).collect(Collectors.toList())));

        if (project.getPartners() != null || !project.getPartners().isEmpty()) {
          for (CenterProjectPartner partner : project.getPartners()) {
            partner.setUsers(new ArrayList<>(
              partner.getProjectPartnerPersons().stream().filter(ppp -> ppp.isActive()).collect(Collectors.toList())));
          }
        }
      }

      String params[] =
        {loggedCenter.getAcronym(), selectedResearchArea.getId() + "", selectedProgram.getId() + "", projectID + ""};
      this.setBasePermission(this.getText(Permission.CENTER_PROJECT_PARTNERS_BASE_PERMISSION, params));


      if (this.isHttpPost()) {

        if (project.getPartners() != null) {
          project.getPartners().clear();
        }

        if (institutions != null) {
          institutions.clear();
        }
      }

    }
  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {
      CenterProject projectDB = projectService.getCenterProjectById(projectID);

      this.savePartners(projectDB);

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_PARTNERS_RELATION);
      projectDB.setActiveSince(new Date());
      projectDB.setModifiedBy(this.getCurrentUser());
      projectDB = projectService.saveCenterProject(projectDB, this.getActionName(), relationsName);

      project = projectDB;

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

  public void savePartners(CenterProject projectSave) {

    if (projectSave.getProjectPartners() != null && projectSave.getProjectPartners().size() > 0) {

      List<CenterProjectPartner> partnersPrew =
        projectSave.getProjectPartners().stream().filter(pp -> pp.isActive()).collect(Collectors.toList());

      for (CenterProjectPartner projectPartner : partnersPrew) {
        if (!project.getPartners().contains(projectPartner)) {
          for (CenterProjectPartnerPerson partnerPerson : projectPartner.getProjectPartnerPersons().stream()
            .filter(ppp -> ppp.isActive()).collect(Collectors.toList())) {
            partnerPersonService.deleteProjectPartnerPerson(partnerPerson.getId());
          }
          partnerService.deleteProjectPartner(projectPartner.getId());
        } else {
          for (CenterProjectPartner projectPartnerPrew : project.getPartners()) {
            if (projectPartnerPrew.equals(projectPartner)) {
              if (projectPartner.getProjectPartnerPersons() != null
                && projectPartner.getProjectPartnerPersons().size() > 0) {

                List<CenterProjectPartnerPerson> personsPrew = projectPartner.getProjectPartnerPersons().stream()
                  .filter(pp -> pp.isActive()).collect(Collectors.toList());

                for (CenterProjectPartnerPerson projectPartnerPerson : personsPrew) {
                  if (projectPartnerPrew.getUsers() != null) {
                    if (!projectPartnerPrew.getUsers().contains(projectPartnerPerson)) {
                      partnerPersonService.deleteProjectPartnerPerson(projectPartnerPerson.getId());
                    }
                  }
                }
              }
            }
          }
        }
      }
    }


    if (project.getPartners() != null) {
      for (CenterProjectPartner projectPartner : project.getPartners()) {
        if (projectPartner.getId() == null) {

          CenterProjectPartner partnerNew = new CenterProjectPartner();
          partnerNew.setActive(true);
          partnerNew.setActiveSince(new Date());
          partnerNew.setCreatedBy(this.getCurrentUser());
          partnerNew.setModifiedBy(this.getCurrentUser());
          partnerNew.setModificationJustification("");
          partnerNew.setProject(projectSave);

          Institution institution = institutionService.getInstitutionById(projectPartner.getInstitution().getId());
          partnerNew.setInstitution(institution);

          partnerNew = partnerService.saveProjectPartner(partnerNew);

          if (projectPartner.getUsers() != null) {
            for (CenterProjectPartnerPerson partnerPerson : projectPartner.getUsers()) {

              CenterProjectPartnerPerson partnerPersonNew = new CenterProjectPartnerPerson();
              partnerPersonNew.setActive(true);
              partnerPersonNew.setActiveSince(new Date());
              partnerPersonNew.setCreatedBy(this.getCurrentUser());
              partnerPersonNew.setModifiedBy(this.getCurrentUser());
              partnerPersonNew.setModificationJustification("");

              partnerPersonNew.setProjectPartner(partnerNew);

              User user = userService.getUser(partnerPerson.getUser().getId());
              partnerPersonNew.setUser(user);

              partnerPersonNew = partnerPersonService.saveProjectPartnerPerson(partnerPersonNew);

            }
          }
        } else {

          CenterProjectPartner partnerNew = partnerService.getProjectPartnerById(projectPartner.getId());

          if (projectPartner.getUsers() != null) {
            for (CenterProjectPartnerPerson partnerPerson : projectPartner.getUsers()) {
              if (partnerPerson.getId() == null) {

                CenterProjectPartnerPerson partnerPersonNew = new CenterProjectPartnerPerson();
                partnerPersonNew.setActive(true);
                partnerPersonNew.setActiveSince(new Date());
                partnerPersonNew.setCreatedBy(this.getCurrentUser());
                partnerPersonNew.setModifiedBy(this.getCurrentUser());
                partnerPersonNew.setModificationJustification("");


                partnerPersonNew.setProjectPartner(partnerNew);

                User user = userService.getUser(partnerPerson.getUser().getId());
                partnerPersonNew.setUser(user);

                partnerPersonNew = partnerPersonService.saveProjectPartnerPerson(partnerPersonNew);
              }
            }
          }

        }
      }
    }

  }


  public void setAreaID(long areaID) {
    this.areaID = areaID;
  }

  public void setInstitutions(List<Institution> institutions) {
    this.institutions = institutions;
  }

  public void setLoggedCenter(Center loggedCenter) {
    this.loggedCenter = loggedCenter;
  }

  public void setPartnerModes(HashMap<Boolean, String> partnerModes) {
    this.partnerModes = partnerModes;
  }

  public void setProgramID(long programID) {
    this.programID = programID;
  }

  public void setProject(CenterProject project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setProjectPartners(List<CenterProjectPartner> projectPartners) {
    this.projectPartners = projectPartners;
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


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, project, selectedProgram, true);
    }
  }

}
