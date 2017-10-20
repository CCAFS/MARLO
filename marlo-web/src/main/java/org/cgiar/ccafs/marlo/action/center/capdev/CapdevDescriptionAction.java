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
import org.cgiar.ccafs.marlo.validation.center.capdev.CapDevDescriptionValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
  private List<Long> capdevDisciplines;
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


  public String deleteDiscipline() {
    final Map<String, Object> parameters = this.getParameters();
    final long capdevDisciplineID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));
    final CapdevDiscipline capdev_discipline = capdevDisciplineService.getCapdevDisciplineById(capdevDisciplineID);
    capdev_discipline.setActive(false);
    capdevDisciplineService.saveCapdevDiscipline(capdev_discipline);
    return SUCCESS;
  }

  public String deleteOutput() {
    final Map<String, Object> parameters = this.getParameters();
    final long capdevoutputID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));
    final CapdevOutputs capdev_output = capdevOutputService.getCapdevOutputsById(capdevoutputID);
    capdev_output.setActive(false);
    capdev_output.setModifiedBy(this.getCurrentUser());
    capdevOutputService.saveCapdevOutputs(capdev_output);
    return SUCCESS;
  }

  public String deletePartnert() {
    final Map<String, Object> parameters = this.getParameters();
    final long capdevpartnerID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));
    final CapdevPartners capdev_partner = capdevPartnerService.getCapdevPartnersById(capdevpartnerID);
    capdev_partner.setActive(false);
    capdevPartnerService.saveCapdevPartners(capdev_partner);
    return SUCCESS;
  }

  public String deleteTargetGroup() {
    final Map<String, Object> parameters = this.getParameters();
    final long capdevtargetgroupID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));
    final CapdevTargetgroup capdev_targetgroup = capdevTargetgroupService.getCapdevTargetgroupById(capdevtargetgroupID);
    capdev_targetgroup.setActive(false);
    capdevTargetgroupService.saveCapdevTargetgroup(capdev_targetgroup);
    return SUCCESS;
  }

  public CapacityDevelopment getCapdev() {
    return capdev;
  }


  public List<Long> getCapdevDisciplines() {
    return capdevDisciplines;
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

    capdevDisciplines = new ArrayList<>();
    capdevTargetGroup = new ArrayList<>();
    capdevPartners = new ArrayList<>();
    capdevOutputs = new ArrayList<>();

    try {
      capdevID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CAPDEV_ID)));
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_ID)));
    } catch (final Exception e) {
      capdevID = -1;
      projectID = 0;
    }


    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      final CapacityDevelopment history = (CapacityDevelopment) auditLogService.getHistory(transaction);

      if (history != null) {
        capdev = history;
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }
    } else {
      capdev = capdevService.getCapacityDevelopmentById(capdevID);
    }


  }


  @Override
  public String save() {


    final CapacityDevelopment capdevDB = capdevService.getCapacityDevelopmentById(capdevID);

    capdevDB.setOtherDiscipline(otherDiscipline);
    capdevDB.setDisciplineSuggested(capdev.getDisciplineSuggested());
    capdevDB.setOtherTargetGroup(otherTargetGroup);
    capdevDB.setTargetGroupSuggested(capdev.getTargetGroupSuggested());
    capdevDB.setOtherPartner(otherPartner);
    capdevDB.setPartnerSuggested(capdev.getPartnerSuggested());


    if (capdev.getResearchArea().getId() > -1) {
      capdevDB.setResearchArea(capdev.getResearchArea());

      if (capdev.getResearchProgram() != null) {
        if (capdev.getResearchProgram().getId() != -1) {
          capdevDB.setResearchProgram(capdev.getResearchProgram());
        }
      }
      if (capdev.getProject() != null) {
        if (capdev.getProject().getId() != -1) {
          capdevDB.setProject(capdev.getProject());
        }
      }
    }

    if (capdev.getCrp().getId() > -1) {
      capdevDB.setCrp(capdev.getCrp());
    }


    this.saveCapDevDisciplines(capdevDisciplines, capdevDB);
    this.saveCapdevTargetGroups(capdevTargetGroup, capdevDB);
    this.saveCapdevPartners(capdevPartners, capdevDB);
    this.saveCapdevOutputs(capdevOutputs, capdevDB);

    final List<String> relationsName = new ArrayList<>();
    relationsName.add(APConstants.CAPDEV_DISCIPLINES_RELATION);
    relationsName.add(APConstants.CAPDEV_TARGETGROUPS_RELATION);
    relationsName.add(APConstants.CAPDEV_PARTNERS_RELATION);
    relationsName.add(APConstants.CAPDEV_OUTPUTS_RELATION);
    capdevDB.setActiveSince(new Date());
    capdevDB.setModifiedBy(this.getCurrentUser());

    capdevService.saveCapacityDevelopment(capdevDB, this.getActionName(), relationsName);

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


  public void saveCapDevDisciplines(List<Long> disciplines, CapacityDevelopment capdev) {
    CapdevDiscipline capdevDiscipline = null;
    final Session session = SecurityUtils.getSubject().getSession();

    final User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
    if (!disciplines.isEmpty()) {
      for (final Long iterator : disciplines) {
        if (iterator != null) {
          final Discipline discipline = disciplineService.getDisciplineById(iterator);
          if (discipline != null) {
            capdevDiscipline = new CapdevDiscipline();
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
  }


  public void saveCapdevOutputs(List<Long> outputs, CapacityDevelopment capdev) {
    CapdevOutputs capdevOutput = null;
    final Session session = SecurityUtils.getSubject().getSession();
    final User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
    if (!outputs.isEmpty()) {
      for (final Long iterator : outputs) {
        final CenterOutput output = researchOutputService.getResearchOutputById(iterator);
        if (output != null) {
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


  public void saveCapdevPartners(List<Long> partners, CapacityDevelopment capdev) {
    CapdevPartners capdevPartner = null;
    final Session session = SecurityUtils.getSubject().getSession();
    final User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
    if (!partners.isEmpty()) {
      for (final Long iterator : partners) {
        final Institution institution = institutionService.getInstitutionById(iterator);
        if (institution != null) {
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


  public void saveCapdevTargetGroups(List<Long> targetGroups, CapacityDevelopment capdev) {
    CapdevTargetgroup capdevTargetgroup = null;
    final Session session = SecurityUtils.getSubject().getSession();

    final User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
    if (!targetGroups.isEmpty()) {
      for (final Long iterator : targetGroups) {
        if (iterator != null) {
          final TargetGroup targetGroup = targetGroupService.getTargetGroupById(iterator);
          if (targetGroup != null) {
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
  }


  public void setCapdev(CapacityDevelopment capdev) {
    this.capdev = capdev;
  }


  public void setCapdevDisciplines(List<Long> capdevDisciplines) {
    this.capdevDisciplines = capdevDisciplines;
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
      validator.validate(this, capdev, capdevDisciplines, capdevTargetGroup, capdevPartners, capdevOutputs);
    }
  }

}
