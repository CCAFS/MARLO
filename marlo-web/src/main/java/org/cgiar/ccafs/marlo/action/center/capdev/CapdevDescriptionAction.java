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

package org.cgiar.ccafs.marlo.action.center.capdev;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.ICenterProgramDAO;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentService;
import org.cgiar.ccafs.marlo.data.manager.ICapdevDisciplineService;
import org.cgiar.ccafs.marlo.data.manager.ICapdevOutputsService;
import org.cgiar.ccafs.marlo.data.manager.ICapdevPartnersService;
import org.cgiar.ccafs.marlo.data.manager.ICapdevTargetgroupService;
import org.cgiar.ccafs.marlo.data.manager.ICenterAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutputManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectManager;
import org.cgiar.ccafs.marlo.data.manager.IDisciplineService;
import org.cgiar.ccafs.marlo.data.manager.ITargetGroupService;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.data.model.CapdevDiscipline;
import org.cgiar.ccafs.marlo.data.model.CapdevOutputs;
import org.cgiar.ccafs.marlo.data.model.CapdevPartners;
import org.cgiar.ccafs.marlo.data.model.CapdevTargetgroup;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Discipline;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.TargetGroup;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.center.capdev.CapDevDescriptionValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

public class CapdevDescriptionAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private CapacityDevelopment capdev;
  private final CapDevDescriptionValidator validator;
  private long capdevID;
  private long projectID;
  private List<Discipline> disciplines;
  private List<TargetGroup> targetGroups;
  private List<CenterArea> researchAreas;
  private List<CenterProgram> researchPrograms;
  private List<CenterProject> projects;
  private List<Map<String, Object>> jsonProjects;
  private List<Map<String, Object>> json;
  private List<Crp> crps;
  private List<Institution> partners;
  private List<CenterOutput> outputs;
  private List<Long> capdevdisciplines;
  private String otherDiscipline;
  private String otherTargetGroup;
  private String otherPartner;
  private List<Long> capdevTargetGroup;
  private List<Long> capdevPartners;
  private List<Long> capdevOutputs;
  private final ICapacityDevelopmentService capdevService;
  private final ICenterAreaManager researchAreaService;
  private final ICenterProgramDAO researchProgramSercive;
  private final ICenterProjectManager projectService;
  private final CrpManager crpService;
  private final InstitutionManager institutionService;
  private final ICenterOutputManager researchOutputService;
  private final IDisciplineService disciplineService;
  private final ITargetGroupService targetGroupService;
  private final ICapdevDisciplineService capdevDisciplineService;
  private final ICapdevTargetgroupService capdevTargetgroupService;
  private final ICapdevPartnersService capdevPartnerService;
  private final ICapdevOutputsService capdevOutputService;

  private String transaction;
  private final AuditLogManager auditLogService;

  @Inject
  public CapdevDescriptionAction(APConfig config, ICenterAreaManager researchAreaService,
    ICenterProgramDAO researchProgramSercive, ICenterProjectManager projectService, CrpManager crpService,
    IDisciplineService disciplineService, ITargetGroupService targetGroupService,
    ICapacityDevelopmentService capdevService, ICapdevDisciplineService capdevDisciplineService,
    ICapdevTargetgroupService capdevTargetgroupService, InstitutionManager institutionService,
    ICenterOutputManager researchOutputService, ICapdevPartnersService capdevPartnerService,
    ICapdevOutputsService capdevOutputService, CapDevDescriptionValidator validator, AuditLogManager auditLogService) {
    super(config);
    this.researchAreaService = researchAreaService;
    this.researchProgramSercive = researchProgramSercive;
    this.projectService = projectService;
    this.crpService = crpService;
    this.disciplineService = disciplineService;
    this.targetGroupService = targetGroupService;
    this.capdevService = capdevService;
    this.capdevDisciplineService = capdevDisciplineService;
    this.capdevTargetgroupService = capdevTargetgroupService;
    this.institutionService = institutionService;
    this.researchOutputService = researchOutputService;
    this.capdevPartnerService = capdevPartnerService;
    this.capdevOutputService = capdevOutputService;
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

  public String deleteDiscipline() {
    // final long capdevDisciplineID =
    // Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));
    long capdevDisciplineID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter("capdevDiscipline")));
    CapdevDiscipline capdev_discipline = capdevDisciplineService.getCapdevDisciplineById(capdevDisciplineID);
    capdev_discipline.setActive(false);
    capdev_discipline.setModifiedBy(this.getCurrentUser());
    capdevDisciplineService.saveCapdevDiscipline(capdev_discipline);
    return SUCCESS;
  }

  public String deleteOutput() {
    // final long capdevoutputID =
    // Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));
    long capdevoutputID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter("capdevOutput")));
    CapdevOutputs capdev_output = capdevOutputService.getCapdevOutputsById(capdevoutputID);
    capdev_output.setActive(false);
    capdev_output.setModifiedBy(this.getCurrentUser());
    capdevOutputService.saveCapdevOutputs(capdev_output);
    return SUCCESS;
  }

  public String deletePartnert() {
    // final long capdevpartnerID =
    // Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));
    long capdevpartnerID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter("capdevPartner")));
    CapdevPartners capdev_partner = capdevPartnerService.getCapdevPartnersById(capdevpartnerID);
    capdev_partner.setActive(false);
    capdevPartnerService.saveCapdevPartners(capdev_partner);
    return SUCCESS;
  }

  public String deleteTargetGroup() {
    // final long capdevtargetgroupID =
    // Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));
    long capdevtargetgroupID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter("capdevTargetGroup")));
    CapdevTargetgroup capdev_targetgroup = capdevTargetgroupService.getCapdevTargetgroupById(capdevtargetgroupID);
    capdev_targetgroup.setActive(false);
    capdevTargetgroupService.saveCapdevTargetgroup(capdev_targetgroup);
    return SUCCESS;
  }


  private Path getAutoSaveFilePath() {
    String composedClassName = capdev.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = capdev.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public CapacityDevelopment getCapdev() {
    return capdev;
  }


  public List<Long> getCapdevdisciplines() {
    return capdevdisciplines;
  }


  public long getCapdevID() {
    return capdevID;
  }


  public List<Long> getCapdevOutputs() {
    return capdevOutputs;
  }


  public List<Long> getCapdevPartners() {
    return capdevPartners;
  }


  public List<Long> getCapdevTargetGroup() {
    return capdevTargetGroup;
  }


  public List<Crp> getCrps() {
    return crps;
  }

  public List<Discipline> getDisciplines() {
    return disciplines;
  }


  public List<Map<String, Object>> getJson() {
    return json;
  }


  public List<Map<String, Object>> getJsonProjects() {
    return jsonProjects;
  }

  public String getOtherDiscipline() {
    return otherDiscipline;
  }


  public String getOtherPartner() {
    return otherPartner;
  }


  public String getOtherTargetGroup() {
    return otherTargetGroup;
  }


  public List<CenterOutput> getOutputs() {
    return outputs;
  }

  public List<Institution> getPartners() {
    return partners;
  }

  public long getProjectID() {
    return projectID;
  }


  public List<CenterProject> getProjects() {
    return projects;
  }

  public List<CenterArea> getResearchAreas() {
    return researchAreas;
  }

  public List<CenterProgram> getResearchPrograms() {
    return researchPrograms;
  }


  public List<TargetGroup> getTargetGroups() {
    return targetGroups;
  }


  public String getTransaction() {
    return transaction;
  }


  @Override
  public void prepare() throws Exception {

    researchAreas = researchAreaService.findAll().stream().filter(ra -> ra.isActive()).collect(Collectors.toList());
    Collections.sort(researchAreas, (r1, r2) -> r1.getName().compareTo(r2.getName()));

    researchPrograms =
      researchProgramSercive.findAll().stream().filter(rp -> rp.isActive()).collect(Collectors.toList());
    Collections.sort(researchPrograms, (r1, r2) -> r1.getName().compareTo(r2.getName()));

    projects =
      projectService.findAll().stream().filter(p -> p.isActive() && (p.getName() != null)).collect(Collectors.toList());
    Collections.sort(projects, (r1, r2) -> r1.getName().compareTo(r2.getName()));

    crps = crpService.findAll().stream().filter(out -> out.isActive()).collect(Collectors.toList());
    Collections.sort(crps, (r1, r2) -> r1.getName().compareTo(r2.getName()));

    partners = institutionService.findAll().stream().filter(pt -> pt.isActive()).collect(Collectors.toList());
    Collections.sort(partners, (r1, r2) -> r1.getName().compareTo(r2.getName()));

    outputs = researchOutputService.findAll().stream().filter(out -> out.isActive() && (out.getTitle() != null))
      .collect(Collectors.toList());
    Collections.sort(outputs, (r1, r2) -> r1.getTitle().compareTo(r2.getTitle()));

    // Disciplines List
    disciplines = disciplineService.findAll();
    Collections.sort(disciplines, (r1, r2) -> r1.getName().compareTo(r2.getName()));

    // Target groups List
    targetGroups = targetGroupService.findAll();
    Collections.sort(targetGroups, (r1, r2) -> r1.getName().compareTo(r2.getName()));

    capdevdisciplines = new ArrayList<>();
    capdevTargetGroup = new ArrayList<>();
    capdevPartners = new ArrayList<>();
    capdevOutputs = new ArrayList<>();

    try {

      capdevID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CAPDEV_ID)));
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_ID)));
    } catch (final Exception e) {
      capdevID = -1;
      projectID = -1;
    }


    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      CapacityDevelopment history = (CapacityDevelopment) auditLogService.getHistory(transaction);

      if (history != null) {
        capdev = history;
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }
    } else {
      capdev = capdevService.getCapacityDevelopmentById(capdevID);
    }

    if (capdev != null) {
      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave() && this.isEditable()) {
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();

        AutoSaveReader autoSaveReader = new AutoSaveReader();
        capdev = (CapacityDevelopment) autoSaveReader.readFromJson(jReader);

        if (capdev.getCapdevDisciplineList() != null) {
          for (CapdevDiscipline capdevDiscipline : capdev.getCapdevDisciplineList()) {
            if (capdevDiscipline != null) {
              capdevDiscipline
                .setDiscipline(disciplineService.getDisciplineById(capdevDiscipline.getDiscipline().getId()));
            }
          }
        }

        if (capdev.getCapdevTargetGroupList() != null) {
          for (CapdevTargetgroup capdevtgroups : capdev.getCapdevTargetGroupList()) {
            if (capdevtgroups != null) {
              capdevtgroups
                .setTargetGroups(targetGroupService.getTargetGroupById(capdevtgroups.getTargetGroups().getId()));
            }
          }
        }

        if (capdev.getCapdevPartnersList() != null) {
          for (CapdevPartners capdevPartner : capdev.getCapdevPartnersList()) {
            if (capdevPartner != null) {
              capdevPartner
                .setInstitution(institutionService.getInstitutionById(capdevPartner.getInstitution().getId()));
            }
          }
        }

        if (capdev.getCapdevOutputsList() != null) {
          for (CapdevOutputs capdevOutputs : capdev.getCapdevOutputsList()) {
            if (capdevOutputs != null) {
              capdevOutputs.setResearchOutputs(
                researchOutputService.getResearchOutputById(capdevOutputs.getResearchOutputs().getId()));
            }
          }
        }


        this.setDraft(true);

      } else {
        this.setDraft(false);
        if (capdev.getCapdevDiscipline() != null) {
          List<CapdevDiscipline> capdevDisciplines = new ArrayList<CapdevDiscipline>(
            capdev.getCapdevDiscipline().stream().filter(d -> d.isActive()).collect(Collectors.toList()));
          capdev.setCapdevDisciplineList(capdevDisciplines);
        }


        if (capdev.getCapdevTargetgroup() != null) {
          List<CapdevTargetgroup> capdevTargetGroups = new ArrayList<CapdevTargetgroup>(
            capdev.getCapdevTargetgroup().stream().filter(t -> t.isActive()).collect(Collectors.toList()));
          capdev.setCapdevTargetGroupList(capdevTargetGroups);
        }

        if (capdev.getCapdevPartners() != null) {
          List<CapdevPartners> capdevPartners = new ArrayList<CapdevPartners>(
            capdev.getCapdevPartners().stream().filter(p -> p.isActive()).collect(Collectors.toList()));
          capdev.setCapdevPartnersList(capdevPartners);
        }

        if (capdev.getCapdevOutputs() != null) {
          List<CapdevOutputs> capdevOuputs = new ArrayList<CapdevOutputs>(
            capdev.getCapdevOutputs().stream().filter(o -> o.isActive()).collect(Collectors.toList()));
          capdev.setCapdevOutputsList(capdevOuputs);
        }
      }


    }

    if (this.isHttpPost()) {
      capdev.setResearchArea(null);
      capdev.setResearchProgram(null);
      capdev.setCrp(null);
      capdev.setProject(null);
      capdev.setCapdevDiscipline(null);
      capdev.setCapdevDisciplineList(null);
      capdev.setCapdevTargetgroup(null);
      capdev.setCapdevTargetGroupList(null);
      capdev.setCapdevPartners(null);
      capdev.setCapdevPartnersList(null);
      capdev.setCapdevOutputs(null);
      capdev.setCapdevOutputsList(null);
    }


  }


  @Override
  public String save() {


    CapacityDevelopment capdevDB = capdevService.getCapacityDevelopmentById(capdevID);

    capdevDB.setOtherDiscipline(capdev.getOtherDiscipline());
    capdevDB.setOtherTargetGroup(capdev.getOtherTargetGroup());
    capdevDB.setOtherPartner(capdev.getOtherPartner());
    capdevDB.setDisciplineSuggested(capdev.getDisciplineSuggested());
    capdevDB.setTargetGroupSuggested(capdev.getTargetGroupSuggested());
    capdevDB.setPartnerSuggested(capdev.getPartnerSuggested());

    if (capdev.getResearchArea() != null) {
      if (capdev.getResearchArea().getId() != -1) {
        capdevDB.setResearchArea(capdev.getResearchArea());

        if (capdev.getResearchProgram() != null) {
          if (capdev.getResearchProgram().getId() != -1) {
            capdevDB.setResearchProgram(capdev.getResearchProgram());
          } else {
            capdevDB.setResearchProgram(null);
          }
        }
        if (capdev.getProject() != null) {
          if (capdev.getProject().getId() != -1) {
            capdevDB.setProject(capdev.getProject());
          } else {
            capdevDB.setProject(null);
          }
        }
      } else {
        capdevDB.setResearchArea(null);
      }
    }


    if (capdev.getCrp().getId() > -1) {
      capdevDB.setCrp(capdev.getCrp());
    } else {
      capdevDB.setCrp(null);
    }


    this.saveCapDevDisciplines(capdev.getCapdevDisciplineList(), capdevDB);
    this.saveCapdevTargetGroups(capdev.getCapdevTargetGroupList(), capdevDB);
    this.saveCapdevPartners(capdev.getCapdevPartnersList(), capdevDB);
    this.saveCapdevOutputs(capdev.getCapdevOutputsList(), capdevDB);

    final List<String> relationsName = new ArrayList<>();
    relationsName.add(APConstants.CAPDEV_DISCIPLINES_RELATION);
    relationsName.add(APConstants.CAPDEV_TARGETGROUPS_RELATION);
    relationsName.add(APConstants.CAPDEV_PARTNERS_RELATION);
    relationsName.add(APConstants.CAPDEV_OUTPUTS_RELATION);
    capdevDB.setActiveSince(new Date());
    capdevDB.setModifiedBy(this.getCurrentUser());

    capdevService.saveCapacityDevelopment(capdevDB, this.getActionName(), relationsName);

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {
      path.toFile().delete();
    }

    if (!this.getInvalidFields().isEmpty()) {
      this.setActionMessages(null);
      final List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
      for (final String key : keys) {
        this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
      }
    } else {
      this.addActionMessage("message:" + this.getText("saving.saved"));
    }

    return SUCCESS;
  }


  public void saveCapDevDisciplines(List<CapdevDiscipline> disciplines, CapacityDevelopment capdev) {
    CapdevDiscipline capdevDiscipline = null;
    Session session = SecurityUtils.getSubject().getSession();

    User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
    if (disciplines != null) {
      for (CapdevDiscipline iterator : disciplines) {
        if (iterator.getId() == null) {
          capdevDiscipline = new CapdevDiscipline();
          Discipline discipline = disciplineService.getDisciplineById(iterator.getDiscipline().getId());
          capdevDiscipline.setCapacityDevelopment(capdev);
          capdevDiscipline.setDiscipline(discipline);
          capdevDiscipline.setActive(true);
          capdevDiscipline.setActiveSince(new Date());
          capdevDiscipline.setCreatedBy(currentUser);
          capdevDiscipline.setModifiedBy(currentUser);
          capdevDisciplineService.saveCapdevDiscipline(capdevDiscipline);

        }


      }
    }
  }


  public void saveCapdevOutputs(List<CapdevOutputs> outputs, CapacityDevelopment capdev) {
    CapdevOutputs capdevOutput = null;
    Session session = SecurityUtils.getSubject().getSession();
    User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
    if (outputs != null) {
      for (CapdevOutputs iterator : outputs) {
        if (iterator.getId() == null) {
          CenterOutput output = researchOutputService.getResearchOutputById(iterator.getResearchOutputs().getId());
          capdevOutput = new CapdevOutputs();
          capdevOutput.setCapacityDevelopment(capdev);
          capdevOutput.setResearchOutputs(output);
          capdevOutput.setActive(true);
          capdevOutput.setActiveSince(new Date());
          capdevOutput.setCreatedBy(currentUser);
          capdevOutput.setModifiedBy(currentUser);
          capdevOutputService.saveCapdevOutputs(capdevOutput);
        }
      }
    }
  }


  public void saveCapdevPartners(List<CapdevPartners> partners, CapacityDevelopment capdev) {
    CapdevPartners capdevPartner = null;
    Session session = SecurityUtils.getSubject().getSession();
    User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
    if (partners != null) {
      for (CapdevPartners iterator : partners) {
        if (iterator.getId() == null) {
          Institution institution = institutionService.getInstitutionById(iterator.getInstitution().getId());
          capdevPartner = new CapdevPartners();
          capdevPartner.setCapacityDevelopment(capdev);
          capdevPartner.setInstitution(institution);;
          capdevPartner.setActive(true);
          capdevPartner.setActiveSince(new Date());
          capdevPartner.setCreatedBy(currentUser);
          capdevPartner.setModifiedBy(currentUser);
          capdevPartnerService.saveCapdevPartners(capdevPartner);
        }
      }
    }
  }


  public void saveCapdevTargetGroups(List<CapdevTargetgroup> targetGroups, CapacityDevelopment capdev) {
    CapdevTargetgroup capdevTargetgroup = null;
    Session session = SecurityUtils.getSubject().getSession();

    User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
    if (targetGroups != null) {
      for (CapdevTargetgroup iterator : targetGroups) {
        if (iterator.getId() == null) {
          TargetGroup targetGroup = targetGroupService.getTargetGroupById(iterator.getTargetGroups().getId());
          capdevTargetgroup = new CapdevTargetgroup();
          capdevTargetgroup.setCapacityDevelopment(capdev);
          capdevTargetgroup.setTargetGroups(targetGroup);
          capdevTargetgroup.setActive(true);
          capdevTargetgroup.setActiveSince(new Date());
          capdevTargetgroup.setCreatedBy(currentUser);
          capdevTargetgroup.setModifiedBy(currentUser);
          capdevTargetgroupService.saveCapdevTargetgroup(capdevTargetgroup);

        }


      }
    }
  }


  public void setCapdev(CapacityDevelopment capdev) {
    this.capdev = capdev;
  }


  public void setCapdevdisciplines(List<Long> capdevdisciplines) {
    this.capdevdisciplines = capdevdisciplines;
  }


  public void setCapdevID(long capdevID) {
    this.capdevID = capdevID;
  }


  public void setCapdevOutputs(List<Long> capdevOutputs) {
    this.capdevOutputs = capdevOutputs;
  }


  public void setCapdevPartners(List<Long> capdevPartners) {
    this.capdevPartners = capdevPartners;
  }


  public void setCapdevTargetGroup(List<Long> capdevTargetGroup) {
    this.capdevTargetGroup = capdevTargetGroup;
  }


  public void setCrps(List<Crp> crps) {
    this.crps = crps;
  }


  public void setDisciplines(List<Discipline> disciplines) {
    this.disciplines = disciplines;
  }


  public void setJson(List<Map<String, Object>> json) {
    this.json = json;
  }

  public void setJsonProjects(List<Map<String, Object>> jsonProjects) {
    this.jsonProjects = jsonProjects;
  }


  public void setOtherDiscipline(String otherDiscipline) {
    this.otherDiscipline = otherDiscipline;
  }


  public void setOtherPartner(String otherPartner) {
    this.otherPartner = otherPartner;
  }


  public void setOtherTargetGroup(String otherTargetGroup) {
    this.otherTargetGroup = otherTargetGroup;
  }


  public void setOutputs(List<CenterOutput> outputs) {
    this.outputs = outputs;
  }


  public void setPartners(List<Institution> partners) {
    this.partners = partners;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setProjects(List<CenterProject> projects) {
    this.projects = projects;
  }


  public void setResearchAreas(List<CenterArea> researchAreas) {
    this.researchAreas = researchAreas;
  }


  public void setResearchPrograms(List<CenterProgram> researchPrograms) {
    this.researchPrograms = researchPrograms;
  }


  public void setTargetGroups(List<TargetGroup> targetGroups) {
    this.targetGroups = targetGroups;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, capdev);
    }
  }

}
