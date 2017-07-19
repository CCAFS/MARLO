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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
  private long capdevID;
  private List<Discipline> disciplines;
  private List<TargetGroup> targetGroups;
  private List<CenterArea> researchAreas;
  private List<CenterProgram> researchPrograms;
  private List<CenterProject> projects;
  private List<Map<String, Object>> jsonProjects;
  private List<Crp> crps;
  private List<Institution> partners;
  private List<CenterOutput> outputs;
  private List<Long> capdevDisciplines;
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

  @Inject
  public CapdevDescriptionAction(APConfig config, ICenterAreaManager researchAreaService,
    ICenterProgramDAO researchProgramSercive, ICenterProjectManager projectService, CrpManager crpService,
    IDisciplineService disciplineService, ITargetGroupService targetGroupService,
    ICapacityDevelopmentService capdevService, ICapdevDisciplineService capdevDisciplineService,
    ICapdevTargetgroupService capdevTargetgroupService, InstitutionManager institutionService,
    ICenterOutputManager researchOutputService, ICapdevPartnersService capdevPartnerService,
    ICapdevOutputsService capdevOutputService) {
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


  public List<Map<String, Object>> getJsonProjects() {
    return jsonProjects;
  }


  public List<CenterOutput> getOutputs() {
    return outputs;
  }

  public List<Institution> getPartners() {
    return partners;
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


  @Override
  public void prepare() throws Exception {

    researchAreas = researchAreaService.findAll();
    researchPrograms = researchProgramSercive.findAll();
    projects = projectService.findAll();
    crps = crpService.findAll();
    partners = institutionService.findAll();
    outputs = researchOutputService.findAll();

    // Disciplines List
    disciplines = disciplineService.findAll();

    // Target groups List
    targetGroups = targetGroupService.findAll();

    capdevDisciplines = new ArrayList<>();
    capdevTargetGroup = new ArrayList<>();
    capdevPartners = new ArrayList<>();
    capdevOutputs = new ArrayList<>();

    try {
      capdevID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CAPDEV_ID)));
    } catch (final Exception e) {
      capdevID = -1;
    }
    capdev = capdevService.getCapacityDevelopmentById(capdevID);


  }


  @Override
  public String save() {

    System.out.println("capdev.getResearchArea() " + capdev.getResearchArea().getId());
    final CapacityDevelopment capdevDB = capdevService.getCapacityDevelopmentById(capdevID);

    if (capdev.getResearchArea().getId() > -1) {
      capdevDB.setResearchArea(capdev.getResearchArea());
    }
    if (capdev.getResearchProgram().getId() > -1) {
      capdevDB.setResearchProgram(capdev.getResearchProgram());
    }
    if (capdev.getProject().getId() > -1) {
      capdevDB.setProject(capdev.getProject());
    }
    if (capdev.getCrp().getId() > -1) {
      capdevDB.setCrp(capdev.getCrp());
    }


    capdevService.saveCapacityDevelopment(capdevDB);
    this.saveCapDevDisciplines(capdevDisciplines, capdevDB);
    this.saveCapdevTargetGroups(capdevTargetGroup, capdevDB);
    System.out.println("capdevPartners.size() " + capdevPartners.size());
    System.out.println("capdevOutputs.size() " + capdevOutputs.size());
    this.saveCapdevPartners(capdevPartners, capdevDB);
    this.saveCapdevOutputs(capdevOutputs, capdevDB);


    this.addActionMessage("message: Information was correctly saved.</br>");


    return SUCCESS;
  }

  public void saveCapDevDisciplines(List<Long> disciplines, CapacityDevelopment capdev) {
    CapdevDiscipline capdevDiscipline = null;
    final Session session = SecurityUtils.getSubject().getSession();

    final User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
    if (!disciplines.isEmpty()) {
      for (final Long iterator : disciplines) {
        final Discipline discipline = disciplineService.getDisciplineById(iterator);
        if (discipline != null) {
          capdevDiscipline = new CapdevDiscipline();
          capdevDiscipline.setCapacityDevelopment(capdev);
          capdevDiscipline.setDisciplines(discipline);
          capdevDiscipline.setActive(true);
          capdevDiscipline.setActiveSince(new Date());
          capdevDiscipline.setUsersByCreatedBy(currentUser);
          capdevDisciplineService.saveCapdevDiscipline(capdevDiscipline);
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
          capdevOutput.setUsersByCreatedBy(currentUser);
          capdevOutputService.saveCapdevOutputs(capdevOutput);
        }
      }
    }
  }


  public void saveCapdevPartners(List<Long> partners, CapacityDevelopment capdev) {
    CapdevPartners capdevPartner = null;
    System.out.println(capdev);
    final Session session = SecurityUtils.getSubject().getSession();
    final User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
    if (!partners.isEmpty()) {
      for (final Long iterator : partners) {
        final Institution institution = institutionService.getInstitutionById(iterator);
        if (institution != null) {
          capdevPartner = new CapdevPartners();
          capdevPartner.setCapacityDevelopment(capdev);
          capdevPartner.setInstitutions(institution);;
          capdevPartner.setActive(true);
          capdevPartner.setActiveSince(new Date());
          capdevPartner.setUsersByCreatedBy(currentUser);
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
        final TargetGroup targetGroup = targetGroupService.getTargetGroupById(iterator);
        if (targetGroup != null) {
          capdevTargetgroup = new CapdevTargetgroup();
          capdevTargetgroup.setCapacityDevelopment(capdev);
          capdevTargetgroup.setTargetGroups(targetGroup);
          capdevTargetgroup.setActive(true);
          capdevTargetgroup.setActiveSince(new Date());
          capdevTargetgroup.setUsersByCreatedBy(currentUser);
          capdevTargetgroupService.saveCapdevTargetgroup(capdevTargetgroup);
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


  public void setJsonProjects(List<Map<String, Object>> jsonProjects) {
    this.jsonProjects = jsonProjects;
  }


  public void setOutputs(List<CenterOutput> outputs) {
    this.outputs = outputs;
  }


  public void setPartners(List<Institution> partners) {
    this.partners = partners;
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


}
