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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCenterManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationContributingOrganizationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationOrganizationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationSharedManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInnovationManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCenter;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationContributingOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationDeliverable;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationShared;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInnovation;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class ProjectInnovationReplicationAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -6222691323576108517L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ProjectExpectedStudyReplicationAction.class);

  // Managers
  private GlobalUnitManager globalUnitManager;
  private PhaseManager phaseManager;

  private ProjectInnovationManager projectInnovationManager;
  private ProjectInnovationInfoManager projectInnovationInfoManager;
  private ProjectInnovationCenterManager projectInnovationCenterManager;
  private ProjectInnovationContributingOrganizationManager projectInnovationContributingOrganizationManager;
  private ProjectInnovationCountryManager projectInnovationCountryManager;
  private ProjectInnovationCrpManager projectInnovationCrpManager;
  private ProjectInnovationDeliverableManager projectInnovationDeliverableManager;
  private ProjectInnovationGeographicScopeManager projectInnovationGeographicScopeManager;
  private ProjectInnovationMilestoneManager projectInnovationMilestoneManager;
  private ProjectInnovationOrganizationManager projectInnovationOrganizationManager;
  private ProjectInnovationRegionManager projectInnovationRegionManager;
  private ProjectInnovationSharedManager projectInnovationSharedManager;
  private ProjectInnovationSubIdoManager projectInnovationSubIdoManager;

  private ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager;
  private ProjectPolicyInnovationManager projectPolicyInnovationManager;

  // Variables
  private String entityByPhaseList;
  private List<GlobalUnit> crps;
  private long selectedPhaseID;
  private Phase selectedPhase;

  @Inject
  public ProjectInnovationReplicationAction(APConfig config, PhaseManager phaseManager,
    GlobalUnitManager globalUnitManager, ProjectInnovationManager projectInnovationManager,
    ProjectInnovationInfoManager projectInnovationInfoManager,
    ProjectInnovationCenterManager projectInnovationCenterManager,
    ProjectInnovationContributingOrganizationManager projectInnovationContributingOrganizationManager,
    ProjectInnovationCountryManager projectInnovationCountryManager,
    ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager,
    ProjectInnovationDeliverableManager projectInnovationDeliverableManager,
    ProjectInnovationGeographicScopeManager projectInnovationGeographicScopeManager,
    ProjectInnovationMilestoneManager projectInnovationMilestoneManager,
    ProjectInnovationOrganizationManager projectInnovationOrganizationManager,
    ProjectInnovationRegionManager projectInnovationRegionManager,
    ProjectInnovationSharedManager projectInnovationSharedManager,
    ProjectInnovationSubIdoManager projectInnovationSubIdoManager,
    ProjectInnovationCrpManager projectInnovationCrpManager,
    ProjectPolicyInnovationManager projectPolicyInnovationManager) {
    super(config);
    this.globalUnitManager = globalUnitManager;
    this.phaseManager = phaseManager;

    this.projectInnovationManager = projectInnovationManager;
    this.projectInnovationInfoManager = projectInnovationInfoManager;
    this.projectInnovationCenterManager = projectInnovationCenterManager;
    this.projectInnovationContributingOrganizationManager = projectInnovationContributingOrganizationManager;
    this.projectInnovationCountryManager = projectInnovationCountryManager;
    this.projectInnovationCrpManager = projectInnovationCrpManager;
    this.projectInnovationDeliverableManager = projectInnovationDeliverableManager;
    this.projectInnovationGeographicScopeManager = projectInnovationGeographicScopeManager;
    this.projectInnovationMilestoneManager = projectInnovationMilestoneManager;
    this.projectInnovationOrganizationManager = projectInnovationOrganizationManager;
    this.projectInnovationRegionManager = projectInnovationRegionManager;
    this.projectInnovationSharedManager = projectInnovationSharedManager;
    this.projectInnovationSubIdoManager = projectInnovationSubIdoManager;

    this.projectExpectedStudyInnovationManager = projectExpectedStudyInnovationManager;
    this.projectPolicyInnovationManager = projectPolicyInnovationManager;
  }

  public List<GlobalUnit> getCrps() {
    return crps;
  }

  public String getEntityByPhaseList() {
    return entityByPhaseList;
  }

  public long getSelectedPhaseID() {
    return selectedPhaseID;
  }

  @Override
  public void prepare() throws Exception {
    super.prepare();
    crps = globalUnitManager.findAll().stream().filter(c -> c.isMarlo() && c.isActive()).collect(Collectors.toList());
  }

  /**
   * Replicates the ProjectExpectedStudyInnovations associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyInnovations(long innovationId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyInnovation replication...");
    List<ProjectExpectedStudyInnovation> studyInnovations =
      this.projectExpectedStudyInnovationManager.getAllStudyInnovationsByInnovation(innovationId);
    studyInnovations.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getProjectExpectedStudy() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studyInnovations)) {
      Map<ProjectExpectedStudy, ProjectExpectedStudyInnovation> uniqueStudies = new HashMap<>(studyInnovations.size());

      for (ProjectExpectedStudyInnovation pesi : studyInnovations) {
        ProjectExpectedStudy projectExpectedStudy = pesi.getProjectExpectedStudy();

        if (uniqueStudies.containsKey(projectExpectedStudy)) {
          LOG.info("The study {} is duplicated", projectExpectedStudy.getId());
        } else {
          uniqueStudies.put(projectExpectedStudy, pesi);
        }
      }

      for (ProjectExpectedStudyInnovation pesi : uniqueStudies.values()) {
        this.projectExpectedStudyInnovationManager.saveProjectExpectedStudyInnovation(pesi);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationCenters associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectInnovationCenters(long innovationId, Phase selectedPhase) {
    LOG.info("Starting ProjectInnovationCenter replication...");
    List<ProjectInnovationCenter> innovationCenters =
      this.projectInnovationCenterManager.getAllInnovationCentersByInnovation(innovationId);
    innovationCenters.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getInstitution() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(innovationCenters)) {
      Map<Institution, ProjectInnovationCenter> uniqueCenters = new HashMap<>(innovationCenters.size());

      for (ProjectInnovationCenter pic : innovationCenters) {
        Institution institution = pic.getInstitution();

        if (uniqueCenters.containsKey(institution)) {
          LOG.info("The institution {} is duplicated", institution.getId());
        } else {
          uniqueCenters.put(institution, pic);
        }
      }

      for (ProjectInnovationCenter pic : uniqueCenters.values()) {
        this.projectInnovationCenterManager.saveProjectInnovationCenter(pic);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationContributingOrganizations associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectInnovationContributingOrganizations(long innovationId, Phase selectedPhase) {
    LOG.info("Starting ProjectInnovationContributingOrganization replication...");
    List<ProjectInnovationContributingOrganization> innovationContributingOrganizations =
      this.projectInnovationContributingOrganizationManager
        .getAllInnovationContributingOrganizationsByInnovation(innovationId);
    innovationContributingOrganizations.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getInstitution() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(innovationContributingOrganizations)) {
      Map<Institution, ProjectInnovationContributingOrganization> uniqueContributingOrganizations =
        new HashMap<>(innovationContributingOrganizations.size());

      for (ProjectInnovationContributingOrganization pico : innovationContributingOrganizations) {
        Institution institution = pico.getInstitution();

        if (uniqueContributingOrganizations.containsKey(institution)) {
          LOG.info("The institution {} is duplicated", institution.getId());
        } else {
          uniqueContributingOrganizations.put(institution, pico);
        }
      }

      for (ProjectInnovationContributingOrganization pico : uniqueContributingOrganizations.values()) {
        this.projectInnovationContributingOrganizationManager.saveProjectInnovationContributingOrganization(pico);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationCountries associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectInnovationCountries(long innovationId, Phase selectedPhase) {
    LOG.info("Starting ProjectInnovationCountry replication...");
    List<ProjectInnovationCountry> innovationCountries =
      this.projectInnovationCountryManager.getAllInnovationCountriesByInnovation(innovationId);
    innovationCountries.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getLocElement() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(innovationCountries)) {
      Map<LocElement, ProjectInnovationCountry> uniqueCountries = new HashMap<>(innovationCountries.size());

      for (ProjectInnovationCountry pic : innovationCountries) {
        LocElement locElement = pic.getLocElement();

        if (uniqueCountries.containsKey(locElement)) {
          LOG.info("The country {} is duplicated", locElement.getId());
        } else {
          uniqueCountries.put(locElement, pic);
        }
      }

      for (ProjectInnovationCountry pic : uniqueCountries.values()) {
        this.projectInnovationCountryManager.saveProjectInnovationCountry(pic);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationCrps associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectInnovationCrps(long innovationId, Phase selectedPhase) {
    LOG.info("Starting ProjectInnovationCrp replication...");
    List<ProjectInnovationCrp> innovationCrps =
      this.projectInnovationCrpManager.getAllInnovationCrpsByInnovation(innovationId);
    innovationCrps.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getGlobalUnit() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(innovationCrps)) {
      Map<GlobalUnit, ProjectInnovationCrp> uniqueCrps = new HashMap<>(innovationCrps.size());

      for (ProjectInnovationCrp pic : innovationCrps) {
        GlobalUnit globalUnit = pic.getGlobalUnit();

        if (uniqueCrps.containsKey(globalUnit)) {
          LOG.info("The CRP/Platform {} is duplicated", globalUnit.getId());
        } else {
          uniqueCrps.put(globalUnit, pic);
        }
      }

      for (ProjectInnovationCrp pic : uniqueCrps.values()) {
        this.projectInnovationCrpManager.saveProjectInnovationCrp(pic);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationDeliverables associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectInnovationDeliverables(long innovationId, Phase selectedPhase) {
    LOG.info("Starting ProjectInnovationDeliverable replication...");
    List<ProjectInnovationDeliverable> innovationDeliverables =
      this.projectInnovationDeliverableManager.getAllInnovationDeliverablesByInnovation(innovationId);
    innovationDeliverables.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getDeliverable() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(innovationDeliverables)) {
      Map<Deliverable, ProjectInnovationDeliverable> uniqueDeliverables = new HashMap<>(innovationDeliverables.size());

      for (ProjectInnovationDeliverable pid : innovationDeliverables) {
        Deliverable deliverable = pid.getDeliverable();

        if (uniqueDeliverables.containsKey(deliverable)) {
          LOG.info("The deliverable {} is duplicated", deliverable.getId());
        } else {
          uniqueDeliverables.put(deliverable, pid);
        }
      }

      for (ProjectInnovationDeliverable pid : uniqueDeliverables.values()) {
        this.projectInnovationDeliverableManager.saveProjectInnovationDeliverable(pid);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationGeographicScopes associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectInnovationGeographicScopes(long innovationId, Phase selectedPhase) {
    LOG.info("Starting ProjectInnovationGeographicScope replication...");
    List<ProjectInnovationGeographicScope> innovationGeoScopes =
      this.projectInnovationGeographicScopeManager.getAllInnovationGeographicScopesByInnovation(innovationId);
    innovationGeoScopes.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getRepIndGeographicScope() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(innovationGeoScopes)) {
      Map<RepIndGeographicScope, ProjectInnovationGeographicScope> uniqueGeoScopes =
        new HashMap<>(innovationGeoScopes.size());

      for (ProjectInnovationGeographicScope pigs : innovationGeoScopes) {
        RepIndGeographicScope repIndGeographicScope = pigs.getRepIndGeographicScope();

        if (uniqueGeoScopes.containsKey(repIndGeographicScope)) {
          LOG.info("The geoscope {} is duplicated", repIndGeographicScope.getId());
        } else {
          uniqueGeoScopes.put(repIndGeographicScope, pigs);
        }
      }

      for (ProjectInnovationGeographicScope pigs : uniqueGeoScopes.values()) {
        this.projectInnovationGeographicScopeManager.saveProjectInnovationGeographicScope(pigs);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationInfos associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectInnovationInfos(long innovationId, Phase selectedPhase) {
    LOG.info("Starting ProjectInnovationInfo replication...");
    List<ProjectInnovationInfo> innovationInfos =
      this.projectInnovationInfoManager.getAllInnovationInfosByInnovation(innovationId);
    innovationInfos.removeIf(
      pc -> pc == null || pc.getId() == null || pc.getPhase() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(innovationInfos)) {
      ProjectInnovationInfo projectInnovationInfo = innovationInfos.get(innovationInfos.size() - 1);

      this.projectInnovationInfoManager.saveProjectInnovationInfo(projectInnovationInfo);
    }
  }

  /**
   * Replicates the ProjectInnovationMilestones associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectInnovationMilestones(long innovationId, Phase selectedPhase) {
    LOG.info("Starting ProjectInnovationMilestone replication...");
    List<ProjectInnovationMilestone> innovationMilestones =
      this.projectInnovationMilestoneManager.getAllInnovationMilestonesByInnovation(innovationId);
    innovationMilestones.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getCrpMilestone() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(innovationMilestones)) {
      Map<CrpMilestone, ProjectInnovationMilestone> uniqueMilestones = new HashMap<>(innovationMilestones.size());

      for (ProjectInnovationMilestone pim : innovationMilestones) {
        CrpMilestone crpMilestone = pim.getCrpMilestone();

        if (uniqueMilestones.containsKey(crpMilestone)) {
          LOG.info("The milestone {} is duplicated", crpMilestone.getId());
        } else {
          uniqueMilestones.put(crpMilestone, pim);
        }
      }

      for (ProjectInnovationMilestone pim : uniqueMilestones.values()) {
        this.projectInnovationMilestoneManager.saveProjectInnovationMilestone(pim);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationOrganizations associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectInnovationOrganizations(long innovationId, Phase selectedPhase) {
    LOG.info("Starting ProjectInnovationOrganization replication...");
    List<ProjectInnovationOrganization> innovationOrganizations =
      this.projectInnovationOrganizationManager.getAllInnovationOrganizationsByInnovation(innovationId);
    innovationOrganizations.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getRelationship() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(innovationOrganizations)) {
      Map<RepIndOrganizationType, ProjectInnovationOrganization> uniqueRelations =
        new HashMap<>(innovationOrganizations.size());

      for (ProjectInnovationOrganization pio : innovationOrganizations) {
        RepIndOrganizationType repIndOrganizationType = pio.getRelationship();

        if (uniqueRelations.containsKey(repIndOrganizationType)) {
          LOG.info("The organization type {} is duplicated", repIndOrganizationType.getId());
        } else {
          uniqueRelations.put(repIndOrganizationType, pio);
        }
      }

      for (ProjectInnovationOrganization pio : uniqueRelations.values()) {
        this.projectInnovationOrganizationManager.saveProjectInnovationOrganization(pio);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationRegions associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectInnovationRegions(long innovationId, Phase selectedPhase) {
    LOG.info("Starting ProjectInnovationRegion replication...");
    List<ProjectInnovationRegion> innovationRegions =
      this.projectInnovationRegionManager.getAllInnovationRegionsByInnovation(innovationId);
    innovationRegions.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getLocElement() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(innovationRegions)) {
      Map<LocElement, ProjectInnovationRegion> uniqueRegions = new HashMap<>(innovationRegions.size());

      for (ProjectInnovationRegion pir : innovationRegions) {
        LocElement locElement = pir.getLocElement();

        if (uniqueRegions.containsKey(locElement)) {
          LOG.info("The region {} is duplicated", locElement.getId());
        } else {
          uniqueRegions.put(locElement, pir);
        }
      }

      for (ProjectInnovationRegion pir : uniqueRegions.values()) {
        this.projectInnovationRegionManager.saveProjectInnovationRegion(pir);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationShared associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectInnovationShared(long innovationId, Phase selectedPhase) {
    LOG.info("Starting ProjectInnovationShared replication...");
    List<ProjectInnovationShared> innovationSharedList =
      this.projectInnovationSharedManager.getAllInnovationSharedByInnovation(innovationId);
    innovationSharedList.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getProject() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(innovationSharedList)) {
      Map<Project, ProjectInnovationShared> uniqueSharedProjects = new HashMap<>(innovationSharedList.size());

      for (ProjectInnovationShared pis : innovationSharedList) {
        Project project = pis.getProject();

        if (uniqueSharedProjects.containsKey(project)) {
          LOG.info("The project {} is duplicated", project.getId());
        } else {
          uniqueSharedProjects.put(project, pis);
        }
      }

      for (ProjectInnovationShared pis : uniqueSharedProjects.values()) {
        this.projectInnovationSharedManager.saveProjectInnovationShared(pis);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationSubIdos associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectInnovationSubIdos(long innovationId, Phase selectedPhase) {
    LOG.info("Starting ProjectInnovationSubIdo replication...");
    List<ProjectInnovationSubIdo> innovationSubIdos =
      this.projectInnovationSubIdoManager.getAllInnovationSubIdosByInnovation(innovationId);
    innovationSubIdos.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getSrfSubIdo() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(innovationSubIdos)) {
      Map<SrfSubIdo, ProjectInnovationSubIdo> uniqueSubIdos = new HashMap<>(innovationSubIdos.size());

      for (ProjectInnovationSubIdo pisi : innovationSubIdos) {
        SrfSubIdo srfSubIdo = pisi.getSrfSubIdo();

        if (uniqueSubIdos.containsKey(srfSubIdo)) {
          LOG.info("The srf sub ido {} is duplicated", srfSubIdo.getId());
        } else {
          uniqueSubIdos.put(srfSubIdo, pisi);
        }
      }

      for (ProjectInnovationSubIdo pisi : uniqueSubIdos.values()) {
        this.projectInnovationSubIdoManager.saveProjectInnovationSubIdo(pisi);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyInnovations associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectPolicyInnovations(long innovationId, Phase selectedPhase) {
    LOG.info("Starting ProjectPolicyInnovation replication...");
    List<ProjectPolicyInnovation> innovationPolicies =
      this.projectPolicyInnovationManager.getAllPolicyInnovationsByInnovation(innovationId);
    innovationPolicies.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getProjectPolicy() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(innovationPolicies)) {
      Map<ProjectPolicy, ProjectPolicyInnovation> uniquePolicies = new HashMap<>(innovationPolicies.size());

      for (ProjectPolicyInnovation ppi : innovationPolicies) {
        ProjectPolicy projectPolicy = ppi.getProjectPolicy();

        if (uniquePolicies.containsKey(projectPolicy)) {
          LOG.info("The policy {} is duplicated", projectPolicy.getId());
        } else {
          uniquePolicies.put(projectPolicy, ppi);
        }
      }

      for (ProjectPolicyInnovation ppi : uniquePolicies.values()) {
        this.projectPolicyInnovationManager.saveProjectPolicyInnovation(ppi);
      }
    }
  }

  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      List<String> ids = Arrays.asList(StringUtils.split(StringUtils.trimToEmpty(entityByPhaseList), ",")).stream()
        .map(id -> StringUtils.trimToNull(id)).filter(id -> id != null).collect(Collectors.toList());

      if (this.isNotEmpty(ids)) {
        LOG.debug("Start replication for phase: " + selectedPhaseID);
        selectedPhase = this.phaseManager.getPhaseById(selectedPhaseID);
        // Phase lastCrpPhase = this.phaseManager.getLastCrpPhase(this.selectedPhase.getCrp().getId());
        ProjectInnovation currentInnovation = null;
        for (String id : ids) {
          LOG.debug("Replicating innovation: " + id);
          long innovationIdLong = Long.parseLong(id);
          currentInnovation = this.projectInnovationManager.getProjectInnovationById(innovationIdLong);
          if (currentInnovation != null) {
            this.replicateProjectInnovationInfos(innovationIdLong, selectedPhase);
            this.replicateProjectInnovationCenters(innovationIdLong, selectedPhase);
            this.replicateProjectInnovationContributingOrganizations(innovationIdLong, selectedPhase);
            this.replicateProjectInnovationCountries(innovationIdLong, selectedPhase);
            this.replicateProjectInnovationCrps(innovationIdLong, selectedPhase);
            this.replicateProjectInnovationDeliverables(innovationIdLong, selectedPhase);
            this.replicateProjectInnovationGeographicScopes(innovationIdLong, selectedPhase);
            this.replicateProjectInnovationMilestones(innovationIdLong, selectedPhase);
            this.replicateProjectInnovationOrganizations(innovationIdLong, selectedPhase);
            this.replicateProjectInnovationRegions(innovationIdLong, selectedPhase);
            this.replicateProjectInnovationShared(innovationIdLong, selectedPhase);
            this.replicateProjectInnovationSubIdos(innovationIdLong, selectedPhase);

            this.replicateProjectExpectedStudyInnovations(innovationIdLong, selectedPhase);
            this.replicateProjectPolicyInnovations(innovationIdLong, selectedPhase);
          }
        }
      } else {
        LOG.debug("No innovations selected");
      }

      LOG.debug("Finished replication succesfully");

      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setEntityByPhaseList(String entityByPhaseList) {
    this.entityByPhaseList = entityByPhaseList;
  }

  public void setSelectedPhaseID(long selectedPhaseID) {
    this.selectedPhaseID = selectedPhaseID;
  }
}
