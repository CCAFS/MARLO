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
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.ExpectedStudyProjectManager;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyFlagshipManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySrfTargetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyInvestimentTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndRegionManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageProcessManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageStudyManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.StudyTypeManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyInvestimentType;
import org.cgiar.ccafs.marlo.data.model.RepIndStageProcess;
import org.cgiar.ccafs.marlo.data.model.RepIndStageStudy;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicator;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.data.model.StudyType;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.projects.ProjectExpectedStudiesValidator;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 */
public class ProjectPolicyAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 597647662288518417L;


  // Managers
  private GlobalUnitManager crpManager;

  // Variables
  private ProjectExpectedStudiesValidator projectExpectedStudiesValidator;
  private GlobalUnit loggedCrp;

  private Project project;
  private long projectID;
  private long expectedID;
  private ProjectExpectedStudy expectedStudy;
  private Map<Integer, String> statuses;
  private List<RepIndGeographicScope> geographicScopes;
  private List<LocElement> regions;
  private List<RepIndOrganizationType> organizationTypes;
  private List<RepIndGenderYouthFocusLevel> focusLevels;
  private List<RepIndPolicyInvestimentType> policyInvestimentTypes;
  private List<RepIndStageProcess> stageProcesses;
  private List<RepIndStageStudy> stageStudies;
  private List<StudyType> studyTypes;
  private List<LocElement> countries;
  private List<SrfSubIdo> subIdos;
  private List<SrfSloIndicator> targets;
  private List<GlobalUnit> crps;
  private List<CrpProgram> flagshipList;
  private List<CrpProgram> regionList;
  private List<Institution> institutions;
  private List<Project> myProjects;
  private String transaction;

  @Inject
  public ProjectPolicyAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, SrfSloIndicatorManager srfSloIndicatorManager,
    SrfSubIdoManager srfSubIdoManager, AuditLogManager auditLogManager,
    ExpectedStudyProjectManager expectedStudyProjectManager,
    ProjectExpectedStudiesValidator projectExpectedStudiesValidator, PhaseManager phaseManager,
    CrpProgramManager crpProgramManager, InstitutionManager institutionManager, LocElementManager locElementManager,
    StudyTypeManager studyTypeManager, FileDBManager fileDBManager, RepIndGeographicScopeManager geographicScopeManager,
    RepIndRegionManager repIndRegionManager, RepIndOrganizationTypeManager organizationTypeManager,
    RepIndGenderYouthFocusLevelManager focusLevelManager, RepIndPolicyInvestimentTypeManager investimentTypeManager,
    RepIndStageProcessManager stageProcessManager, RepIndStageStudyManager stageStudyManager,
    ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager,
    ProjectExpectedStudySubIdoManager projectExpectedStudySubIdoManager,
    ProjectExpectedStudyFlagshipManager projectExpectedStudyFlagshipManager,
    ProjectExpectedStudyCrpManager projectExpectedStudyCrpManager,
    ProjectExpectedStudyInstitutionManager projectExpectedStudyInstitutionManager,
    ProjectExpectedStudySrfTargetManager projectExpectedStudySrfTargetManager,
    ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager,
    ProjectExpectedStudyRegionManager projectExpectedStudyRegionManager) {
    super(config);
    this.crpManager = crpManager;
    this.projectExpectedStudiesValidator = projectExpectedStudiesValidator;
  }

  public List<LocElement> getCountries() {
    return countries;
  }

  public List<GlobalUnit> getCrps() {
    return crps;
  }

  public long getExpectedID() {
    return expectedID;
  }

  public ProjectExpectedStudy getExpectedStudy() {
    return expectedStudy;
  }

  public List<CrpProgram> getFlagshipList() {
    return flagshipList;
  }

  public List<RepIndGenderYouthFocusLevel> getFocusLevels() {
    return focusLevels;
  }

  public List<RepIndGeographicScope> getGeographicScopes() {
    return geographicScopes;
  }

  public List<Institution> getInstitutions() {
    return institutions;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public List<Project> getMyProjects() {
    return myProjects;
  }

  public List<RepIndOrganizationType> getOrganizationTypes() {
    return organizationTypes;
  }

  public String getPath() {
    return config.getDownloadURL() + "/" + this.getStudiesSourceFolder().replace('\\', '/');
  }


  public List<RepIndPolicyInvestimentType> getPolicyInvestimentTypes() {
    return policyInvestimentTypes;
  }

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  public List<CrpProgram> getRegionList() {
    return regionList;
  }

  public List<LocElement> getRegions() {
    return regions;
  }

  public List<RepIndStageProcess> getStageProcesses() {
    return stageProcesses;
  }


  public List<RepIndStageStudy> getStageStudies() {
    return stageStudies;
  }

  public Map<Integer, String> getStatuses() {
    return statuses;
  }

  private String getStudiesSourceFolder() {
    return APConstants.STUDIES_FOLDER.concat(File.separator).concat(this.getCrpSession()).concat(File.separator)
      .concat(File.separator).concat(this.getCrpSession() + "_")
      .concat(ProjectSectionStatusEnum.EXPECTEDSTUDY.getStatus()).concat(File.separator);
  }


  public List<StudyType> getStudyTypes() {
    return studyTypes;
  }

  public List<SrfSubIdo> getSubIdos() {
    return subIdos;
  }

  public List<SrfSloIndicator> getTargets() {
    return targets;
  }

  public String getTransaction() {
    return transaction;
  }

  @Override
  public void prepare() throws Exception {

    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());


  }

  @Override
  public String save() {

    return NOT_AUTHORIZED;
  }

  /**
   * Save Expected Studies Crps Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveCrps(ProjectExpectedStudy projectExpectedStudy, Phase phase) {
  }

  /**
   * Save Expected Studies Flagships Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveFlagships(ProjectExpectedStudy projectExpectedStudy, Phase phase) {
  }

  /**
   * Save Expected Studies Institutions Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveInstitutions(ProjectExpectedStudy projectExpectedStudy, Phase phase) {
  }

  /**
   * Save Expected Studies Projects Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveProjects(ProjectExpectedStudy projectExpectedStudy, Phase phase) {
  }

  /**
   * Save Expected Studies Regions Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveRegions(ProjectExpectedStudy projectExpectedStudy, Phase phase) {
  }

  /**
   * Save Expected Studies Srf Targets Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveSrfTargets(ProjectExpectedStudy projectExpectedStudy, Phase phase) {
  }

  /**
   * Save Expected Studies Geographic Regions Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveStudyRegions(ProjectExpectedStudy projectExpectedStudy, Phase phase) {
  }

  /**
   * Save Expected Studies SubIdos Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveSubIdos(ProjectExpectedStudy projectExpectedStudy, Phase phase) {
  }

  public void setCountries(List<LocElement> countries) {
    this.countries = countries;
  }


  public void setCrps(List<GlobalUnit> crps) {
    this.crps = crps;
  }


  public void setExpectedID(long expectedID) {
    this.expectedID = expectedID;
  }


  public void setExpectedStudy(ProjectExpectedStudy expectedStudy) {
    this.expectedStudy = expectedStudy;
  }

  public void setFlagshipList(List<CrpProgram> flagshipList) {
    this.flagshipList = flagshipList;
  }

  public void setFocusLevels(List<RepIndGenderYouthFocusLevel> focusLevels) {
    this.focusLevels = focusLevels;
  }

  public void setGeographicScopes(List<RepIndGeographicScope> geographicScopes) {
    this.geographicScopes = geographicScopes;
  }


  public void setInstitutions(List<Institution> institutions) {
    this.institutions = institutions;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setMyProjects(List<Project> myProjects) {
    this.myProjects = myProjects;
  }

  public void setOrganizationTypes(List<RepIndOrganizationType> organizationTypes) {
    this.organizationTypes = organizationTypes;
  }

  public void setPolicyInvestimentTypes(List<RepIndPolicyInvestimentType> policyInvestimentTypes) {
    this.policyInvestimentTypes = policyInvestimentTypes;
  }

  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setRegionList(List<CrpProgram> regionList) {
    this.regionList = regionList;
  }


  public void setRegions(List<LocElement> regions) {
    this.regions = regions;
  }


  public void setStageProcesses(List<RepIndStageProcess> stageProcesses) {
    this.stageProcesses = stageProcesses;
  }


  public void setStageStudies(List<RepIndStageStudy> stageStudies) {
    this.stageStudies = stageStudies;
  }


  public void setStatuses(Map<Integer, String> statuses) {
    this.statuses = statuses;
  }


  public void setStudyTypes(List<StudyType> studyTypes) {
    this.studyTypes = studyTypes;
  }


  public void setSubIdos(List<SrfSubIdo> subIdos) {
    this.subIdos = subIdos;
  }

  public void setTargets(List<SrfSloIndicator> targets) {
    this.targets = targets;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      projectExpectedStudiesValidator.validate(this, project, expectedStudy, true);
    }
  }

}
