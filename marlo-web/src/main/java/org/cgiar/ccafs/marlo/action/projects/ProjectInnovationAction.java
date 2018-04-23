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

package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationOrganizationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndInnovationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPhaseResearchPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndRegionManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageInnovationManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndInnovationType;
import org.cgiar.ccafs.marlo.data.model.RepIndPhaseResearchPartnership;
import org.cgiar.ccafs.marlo.data.model.RepIndRegion;
import org.cgiar.ccafs.marlo.data.model.RepIndStageInnovation;
import org.cgiar.ccafs.marlo.data.model.TypeExpectedStudiesEnum;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectInnovationAction extends BaseAction {

  private static final long serialVersionUID = 2025842196563364380L;

  private long projectID;
  private long innovationID;
  private Project project;
  private ProjectInnovation innovation;
  private GlobalUnit loggedCrp;

  private List<RepIndPhaseResearchPartnership> phaseResearchList;
  private List<RepIndStageInnovation> stageInnovationList;
  private List<RepIndGeographicScope> geographicScopeList;
  private List<RepIndInnovationType> innovationTypeList;
  private List<RepIndRegion> regionList;
  private List<LocElement> countries;
  private List<ProjectExpectedStudy> expectedStudyList;
  private List<Deliverable> deliverableList;
  private List<GlobalUnit> crpList;
  private List<RepIndGenderYouthFocusLevel> focusLevelList;

  private ProjectInnovationManager projectInnovationManager;
  private GlobalUnitManager globalUnitManager;
  private ProjectManager projectManager;
  private PhaseManager phaseManager;
  private RepIndPhaseResearchPartnershipManager repIndPhaseResearchPartnershipManager;
  private RepIndStageInnovationManager repIndStageInnovationManager;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private RepIndInnovationTypeManager repIndInnovationTypeManager;
  private RepIndRegionManager repIndRegionManager;
  private LocElementManager locElementManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private DeliverableManager deriverableManager;
  private RepIndGenderYouthFocusLevelManager focusLevelManager;
  private ProjectInnovationInfoManager projectInnovationInfoManager;
  private ProjectInnovationCrpManager projectInnovationCrpManager;
  private ProjectInnovationOrganizationManager projectInnovationOrganizationManager;
  private ProjectInnovationDeliverableManager projectInnovationDeliverableManager;
  private ProjectInnovationCountryManager projectInnovationCountryManager;


  @Inject
  public ProjectInnovationAction(APConfig config, GlobalUnitManager globalUnitManager,
    ProjectInnovationManager projectInnovationManager, ProjectManager projectManager, PhaseManager phaseManager,
    RepIndPhaseResearchPartnershipManager repIndPhaseResearchPartnershipManager,
    RepIndStageInnovationManager repIndStageInnovationManager,
    RepIndGeographicScopeManager repIndGeographicScopeManager, RepIndInnovationTypeManager repIndInnovationTypeManager,
    RepIndRegionManager repIndRegionManager, LocElementManager locElementManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, DeliverableManager deriverableManager,
    RepIndGenderYouthFocusLevelManager focusLevelManager, ProjectInnovationInfoManager projectInnovationInfoManager,
    ProjectInnovationCrpManager projectInnovationCrpManager,
    ProjectInnovationOrganizationManager projectInnovationOrganizationManager,
    ProjectInnovationDeliverableManager projectInnovationDeliverableManager,
    ProjectInnovationCountryManager projectInnovationCountryManager) {
    super(config);
    this.projectInnovationManager = projectInnovationManager;
    this.globalUnitManager = globalUnitManager;
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
    this.repIndPhaseResearchPartnershipManager = repIndPhaseResearchPartnershipManager;
    this.repIndStageInnovationManager = repIndStageInnovationManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.repIndInnovationTypeManager = repIndInnovationTypeManager;
    this.repIndRegionManager = repIndRegionManager;
    this.locElementManager = locElementManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.deriverableManager = deriverableManager;
    this.focusLevelManager = focusLevelManager;
    this.projectInnovationInfoManager = projectInnovationInfoManager;
    this.projectInnovationCrpManager = projectInnovationCrpManager;
    this.projectInnovationOrganizationManager = projectInnovationOrganizationManager;
    this.projectInnovationDeliverableManager = projectInnovationDeliverableManager;
    this.projectInnovationCountryManager = projectInnovationCountryManager;


  }


  public List<LocElement> getCountries() {
    return countries;
  }

  @Override
  public List<GlobalUnit> getCrpList() {
    return crpList;
  }

  public List<Deliverable> getDeliverableList() {
    return deliverableList;
  }

  public List<ProjectExpectedStudy> getExpectedStudyList() {
    return expectedStudyList;
  }

  public List<RepIndGenderYouthFocusLevel> getFocusLevelList() {
    return focusLevelList;
  }


  public List<RepIndGeographicScope> getGeographicScopeList() {
    return geographicScopeList;
  }

  public ProjectInnovation getInnovation() {
    return innovation;
  }

  public long getInnovationID() {
    return innovationID;
  }

  public List<RepIndInnovationType> getInnovationTypeList() {
    return innovationTypeList;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public List<RepIndPhaseResearchPartnership> getPhaseResearchList() {
    return phaseResearchList;
  }

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  public List<RepIndRegion> getRegionList() {
    return regionList;
  }

  public List<RepIndStageInnovation> getStageInnovationList() {
    return stageInnovationList;
  }


  @Override
  public void prepare() throws Exception {

    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = globalUnitManager.getGlobalUnitById(loggedCrp.getId());

    innovationID =
      Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.INNOVATION_REQUEST_ID)));

    innovation = projectInnovationManager.getProjectInnovationById(innovationID);


    if (innovation != null) {

      project = projectManager.getProjectById(innovation.getProject().getId());
      projectID = project.getId();


      // TODO Autosave
      Phase phase = phaseManager.getPhaseById(this.getActualPhase().getId());


      // Getting The list
      countries = locElementManager.findAll().stream().filter(c -> c.getLocElementType().getId().intValue() == 2)
        .collect(Collectors.toList());


      phaseResearchList = repIndPhaseResearchPartnershipManager.findAll();
      stageInnovationList = repIndStageInnovationManager.findAll();
      geographicScopeList = repIndGeographicScopeManager.findAll();
      innovationTypeList = repIndInnovationTypeManager.findAll();
      regionList = repIndRegionManager.findAll();
      focusLevelList = focusLevelManager.findAll();


      expectedStudyList = projectExpectedStudyManager.findAll().stream()
        .filter(ex -> ex.isActive() && ex.getType() != null
          && ex.getType() == TypeExpectedStudiesEnum.OUTCOMECASESTUDY.getId() && ex.getPhase().getId() == phase.getId())
        .collect(Collectors.toList());

      List<DeliverableInfo> infos = phase
        .getDeliverableInfos().stream().filter(c -> c.getDeliverable().getProject() != null
          && c.getDeliverable().getProject().equals(project) && c.getDeliverable().isActive())
        .collect(Collectors.toList());
      deliverableList = new ArrayList<>();
      for (DeliverableInfo deliverableInfo : infos) {
        Deliverable deliverable = deliverableInfo.getDeliverable();
        deliverable.setDeliverableInfo(deliverableInfo);
        deliverableList.add(deliverable);
      }


      crpList = globalUnitManager.findAll().stream()
        .filter(gu -> gu.isActive() && (gu.getGlobalUnitType().getId() == 1 || gu.getGlobalUnitType().getId() == 3))
        .collect(Collectors.toList());

    }


    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_INNOVATIONS_BASE_PERMISSION, params));
  }

  public void setCountries(List<LocElement> countries) {
    this.countries = countries;
  }

  public void setCrpList(List<GlobalUnit> crpList) {
    this.crpList = crpList;
  }

  public void setDeliverableList(List<Deliverable> deliverableList) {
    this.deliverableList = deliverableList;
  }

  public void setExpectedStudyList(List<ProjectExpectedStudy> expectedStudyList) {
    this.expectedStudyList = expectedStudyList;
  }

  public void setFocusLevelList(List<RepIndGenderYouthFocusLevel> focusLevelList) {
    this.focusLevelList = focusLevelList;
  }

  public void setGeographicScopeList(List<RepIndGeographicScope> geographicScopeList) {
    this.geographicScopeList = geographicScopeList;
  }

  public void setInnovation(ProjectInnovation innovation) {
    this.innovation = innovation;
  }

  public void setInnovationID(long innovationID) {
    this.innovationID = innovationID;
  }

  public void setInnovationTypeList(List<RepIndInnovationType> innovationTypeList) {
    this.innovationTypeList = innovationTypeList;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setPhaseResearchList(List<RepIndPhaseResearchPartnership> phaseResearchList) {
    this.phaseResearchList = phaseResearchList;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setRegionList(List<RepIndRegion> regionList) {
    this.regionList = regionList;
  }

  public void setStageInnovationList(List<RepIndStageInnovation> stageInnovationList) {
    this.stageInnovationList = stageInnovationList;
  }


}
