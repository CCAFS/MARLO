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
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
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
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInnovation;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
   * Replicates the ProjectInnovationInfos associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyInnovations(long innovationId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyInnovation> studyInnovations =
      this.projectExpectedStudyInnovationManager.getAllStudyInnovationsByInnovation(innovationId);
    studyInnovations.removeIf(
      pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getProjectExpectedStudy() == null);
    if (this.isNotEmpty(studyInnovations)) {
      ProjectExpectedStudyInnovation lastStudyInnovation = studyInnovations.get(studyInnovations.size() - 1);

      if (lastStudyInnovation.getPhase() == lastCrpPhase) {
        // the study innovations got replicated all the way to the last phase, we do not have to do anything else
        studyInnovations = Collections.emptyList();
      } else {
        // the study innovation replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        studyInnovations.removeIf(pc -> !pc.getPhase().equals(lastStudyInnovation.getPhase()));
      }

      for (ProjectExpectedStudyInnovation pesi : studyInnovations) {
        this.projectExpectedStudyInnovationManager.saveProjectExpectedStudyInnovation(pesi);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationCenters associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectInnovationCenters(long innovationId, Phase lastCrpPhase) {
    List<ProjectInnovationCenter> innovationCenters =
      this.projectInnovationCenterManager.getAllInnovationCentersByInnovation(innovationId);
    innovationCenters
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getInstitution() == null);
    if (this.isNotEmpty(innovationCenters)) {
      ProjectInnovationCenter lastInnovationCenter = innovationCenters.get(innovationCenters.size() - 1);

      if (lastInnovationCenter.getPhase() == lastCrpPhase) {
        // the innovation centers got replicated all the way to the last phase, we do not have to do anything else
        innovationCenters = Collections.emptyList();
      } else {
        // the innovation center replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        innovationCenters.removeIf(pc -> !pc.getPhase().equals(lastInnovationCenter.getPhase()));
      }

      for (ProjectInnovationCenter pic : innovationCenters) {
        this.projectInnovationCenterManager.saveProjectInnovationCenter(pic);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationContributingOrganizations associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectInnovationContributingOrganizations(long innovationId, Phase lastCrpPhase) {
    List<ProjectInnovationContributingOrganization> innovationContributingOrganizations =
      this.projectInnovationContributingOrganizationManager
        .getAllInnovationContributingOrganizationsByInnovation(innovationId);
    innovationContributingOrganizations
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getInstitution() == null);
    if (this.isNotEmpty(innovationContributingOrganizations)) {
      ProjectInnovationContributingOrganization lastInnovationContributingOrganization =
        innovationContributingOrganizations.get(innovationContributingOrganizations.size() - 1);

      if (lastInnovationContributingOrganization.getPhase() == lastCrpPhase) {
        // the innovation contributing organizations got replicated all the way to the last phase, we do not have to do
        // anything else
        innovationContributingOrganizations = Collections.emptyList();
      } else {
        // the innovation contributing organization replication stopped some place before the last phase, let's
        // replicate everything from that point onwards
        innovationContributingOrganizations
          .removeIf(pc -> !pc.getPhase().equals(lastInnovationContributingOrganization.getPhase()));
      }

      for (ProjectInnovationContributingOrganization pico : innovationContributingOrganizations) {
        this.projectInnovationContributingOrganizationManager.saveProjectInnovationContributingOrganization(pico);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationCountries associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectInnovationCountries(long innovationId, Phase lastCrpPhase) {
    List<ProjectInnovationCountry> innovationCountries =
      this.projectInnovationCountryManager.getAllInnovationCountriesByInnovation(innovationId);
    innovationCountries
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getLocElement() == null);
    if (this.isNotEmpty(innovationCountries)) {
      ProjectInnovationCountry lastInnovationCountry = innovationCountries.get(innovationCountries.size() - 1);

      if (lastInnovationCountry.getPhase() == lastCrpPhase) {
        // the innovation countries got replicated all the way to the last phase, we do not have to do anything else
        innovationCountries = Collections.emptyList();
      } else {
        // the innovation country replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        innovationCountries.removeIf(pc -> !pc.getPhase().equals(lastInnovationCountry.getPhase()));
      }

      for (ProjectInnovationCountry pic : innovationCountries) {
        this.projectInnovationCountryManager.saveProjectInnovationCountry(pic);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationCrps associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectInnovationCrps(long innovationId, Phase lastCrpPhase) {
    List<ProjectInnovationCrp> innovationCrps =
      this.projectInnovationCrpManager.getAllInnovationCrpsByInnovation(innovationId);
    innovationCrps
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getGlobalUnit() == null);
    if (this.isNotEmpty(innovationCrps)) {
      ProjectInnovationCrp lastInnovationCrp = innovationCrps.get(innovationCrps.size() - 1);

      if (lastInnovationCrp.getPhase() == lastCrpPhase) {
        // the innovation crps got replicated all the way to the last phase, we do not have to do anything else
        innovationCrps = Collections.emptyList();
      } else {
        // the innovation crp replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        innovationCrps.removeIf(pc -> !pc.getPhase().equals(lastInnovationCrp.getPhase()));
      }

      for (ProjectInnovationCrp pic : innovationCrps) {
        this.projectInnovationCrpManager.saveProjectInnovationCrp(pic);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationDeliverables associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectInnovationDeliverables(long innovationId, Phase lastCrpPhase) {
    List<ProjectInnovationDeliverable> innovationDeliverables =
      this.projectInnovationDeliverableManager.getAllInnovationDeliverablesByInnovation(innovationId);
    innovationDeliverables
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getDeliverable() == null);
    if (this.isNotEmpty(innovationDeliverables)) {
      ProjectInnovationDeliverable lastInnovationDeliverable =
        innovationDeliverables.get(innovationDeliverables.size() - 1);

      if (lastInnovationDeliverable.getPhase() == lastCrpPhase) {
        // the innovation deliverables got replicated all the way to the last phase, we do not have to do anything else
        innovationDeliverables = Collections.emptyList();
      } else {
        // the innovation deliverable replication stopped some place before the last phase, let's replicate everything
        // from that point onwards
        innovationDeliverables.removeIf(pc -> !pc.getPhase().equals(lastInnovationDeliverable.getPhase()));
      }

      for (ProjectInnovationDeliverable pid : innovationDeliverables) {
        this.projectInnovationDeliverableManager.saveProjectInnovationDeliverable(pid);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationGeographicScopes associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectInnovationGeographicScopes(long innovationId, Phase lastCrpPhase) {
    List<ProjectInnovationGeographicScope> innovationGeoScopes =
      this.projectInnovationGeographicScopeManager.getAllInnovationGeographicScopesByInnovation(innovationId);
    innovationGeoScopes.removeIf(
      pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getRepIndGeographicScope() == null);
    if (this.isNotEmpty(innovationGeoScopes)) {
      ProjectInnovationGeographicScope lastInnovationGeoScope = innovationGeoScopes.get(innovationGeoScopes.size() - 1);

      if (lastInnovationGeoScope.getPhase() == lastCrpPhase) {
        // the innovation geoscopes got replicated all the way to the last phase, we do not have to do anything else
        innovationGeoScopes = Collections.emptyList();
      } else {
        // the innovation geoscope replication stopped some place before the last phase, let's replicate everything
        // from that point onwards
        innovationGeoScopes.removeIf(pc -> !pc.getPhase().equals(lastInnovationGeoScope.getPhase()));
      }

      for (ProjectInnovationGeographicScope pigs : innovationGeoScopes) {
        this.projectInnovationGeographicScopeManager.saveProjectInnovationGeographicScope(pigs);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationInfos associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectInnovationInfos(long innovationId, Phase lastCrpPhase) {
    List<ProjectInnovationInfo> innovationInfos =
      this.projectInnovationInfoManager.getAllInnovationInfosByInnovation(innovationId);
    innovationInfos.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null);
    if (this.isNotEmpty(innovationInfos)) {
      ProjectInnovationInfo lastInnovationInfo = innovationInfos.get(innovationInfos.size() - 1);

      if (lastInnovationInfo.getPhase() == lastCrpPhase) {
        // the innovation infos got replicated all the way to the last phase, we do not have to do anything else
        innovationInfos = Collections.emptyList();
      } else {
        // the innovation info replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        innovationInfos.removeIf(pc -> !pc.getPhase().equals(lastInnovationInfo.getPhase()));
      }

      for (ProjectInnovationInfo pii : innovationInfos) {
        this.projectInnovationInfoManager.saveProjectInnovationInfo(pii);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationMilestones associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectInnovationMilestones(long innovationId, Phase lastCrpPhase) {
    List<ProjectInnovationMilestone> innovationMilestones =
      this.projectInnovationMilestoneManager.getAllInnovationMilestonesByInnovation(innovationId);
    innovationMilestones
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getCrpMilestone() == null);
    if (this.isNotEmpty(innovationMilestones)) {
      ProjectInnovationMilestone lastInnovationMilestone = innovationMilestones.get(innovationMilestones.size() - 1);

      if (lastInnovationMilestone.getPhase() == lastCrpPhase) {
        // the innovation milestones got replicated all the way to the last phase, we do not have to do anything else
        innovationMilestones = Collections.emptyList();
      } else {
        // the innovation milestone replication stopped some place before the last phase, let's replicate everything
        // from that point onwards
        innovationMilestones.removeIf(pc -> !pc.getPhase().equals(lastInnovationMilestone.getPhase()));
      }

      for (ProjectInnovationMilestone pim : innovationMilestones) {
        this.projectInnovationMilestoneManager.saveProjectInnovationMilestone(pim);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationOrganizations associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectInnovationOrganizations(long innovationId, Phase lastCrpPhase) {
    List<ProjectInnovationOrganization> innovationOrganizations =
      this.projectInnovationOrganizationManager.getAllInnovationOrganizationsByInnovation(innovationId);
    innovationOrganizations
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getRelationship() == null);
    if (this.isNotEmpty(innovationOrganizations)) {
      ProjectInnovationOrganization lastInnovationOrganization =
        innovationOrganizations.get(innovationOrganizations.size() - 1);

      if (lastInnovationOrganization.getPhase() == lastCrpPhase) {
        // the innovation organizations got replicated all the way to the last phase, we do not have to do anything else
        innovationOrganizations = Collections.emptyList();
      } else {
        // the innovation organization replication stopped some place before the last phase, let's replicate everything
        // from that point onwards
        innovationOrganizations.removeIf(pc -> !pc.getPhase().equals(lastInnovationOrganization.getPhase()));
      }

      for (ProjectInnovationOrganization pio : innovationOrganizations) {
        this.projectInnovationOrganizationManager.saveProjectInnovationOrganization(pio);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationRegions associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectInnovationRegions(long innovationId, Phase lastCrpPhase) {
    List<ProjectInnovationRegion> innovationRegions =
      this.projectInnovationRegionManager.getAllInnovationRegionsByInnovation(innovationId);
    innovationRegions
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getLocElement() == null);
    if (this.isNotEmpty(innovationRegions)) {
      ProjectInnovationRegion lastInnovationRegion = innovationRegions.get(innovationRegions.size() - 1);

      if (lastInnovationRegion.getPhase() == lastCrpPhase) {
        // the innovation regions got replicated all the way to the last phase, we do not have to do anything else
        innovationRegions = Collections.emptyList();
      } else {
        // the innovation region replication stopped some place before the last phase, let's replicate everything
        // from that point onwards
        innovationRegions.removeIf(pc -> !pc.getPhase().equals(lastInnovationRegion.getPhase()));
      }

      for (ProjectInnovationRegion pir : innovationRegions) {
        this.projectInnovationRegionManager.saveProjectInnovationRegion(pir);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationShared associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectInnovationShared(long innovationId, Phase lastCrpPhase) {
    List<ProjectInnovationShared> innovationSharedList =
      this.projectInnovationSharedManager.getAllInnovationSharedByInnovation(innovationId);
    innovationSharedList
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getProject() == null);
    if (this.isNotEmpty(innovationSharedList)) {
      ProjectInnovationShared lastInnovationShared = innovationSharedList.get(innovationSharedList.size() - 1);

      if (lastInnovationShared.getPhase() == lastCrpPhase) {
        // the innovation shared got replicated all the way to the last phase, we do not have to do anything else
        innovationSharedList = Collections.emptyList();
      } else {
        // the innovation shared replication stopped some place before the last phase, let's replicate everything
        // from that point onwards
        innovationSharedList.removeIf(pc -> !pc.getPhase().equals(lastInnovationShared.getPhase()));
      }

      for (ProjectInnovationShared pis : innovationSharedList) {
        this.projectInnovationSharedManager.saveProjectInnovationShared(pis);
      }
    }
  }

  /**
   * Replicates the ProjectInnovationSubIdos associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectInnovationSubIdos(long innovationId, Phase lastCrpPhase) {
    List<ProjectInnovationSubIdo> innovationSubIdos =
      this.projectInnovationSubIdoManager.getAllInnovationSubIdosByInnovation(innovationId);
    innovationSubIdos
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getSrfSubIdo() == null);
    if (this.isNotEmpty(innovationSubIdos)) {
      ProjectInnovationSubIdo lastInnovationSubIdo = innovationSubIdos.get(innovationSubIdos.size() - 1);

      if (lastInnovationSubIdo.getPhase() == lastCrpPhase) {
        // the innovation sub idos got replicated all the way to the last phase, we do not have to do anything else
        innovationSubIdos = Collections.emptyList();
      } else {
        // the innovation sub ido replication stopped some place before the last phase, let's replicate everything
        // from that point onwards
        innovationSubIdos.removeIf(pc -> !pc.getPhase().equals(lastInnovationSubIdo.getPhase()));
      }

      for (ProjectInnovationSubIdo pisi : innovationSubIdos) {
        this.projectInnovationSubIdoManager.saveProjectInnovationSubIdo(pisi);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyInnovations associated to the innovationId
   * 
   * @param innovationId the innovation identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectPolicyInnovations(long innovationId, Phase lastCrpPhase) {
    List<ProjectPolicyInnovation> innovationSubIdos =
      this.projectPolicyInnovationManager.getAllPolicyInnovationsByInnovation(innovationId);
    innovationSubIdos
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getProjectPolicy() == null);
    if (this.isNotEmpty(innovationSubIdos)) {
      ProjectPolicyInnovation lastInnovationSubIdo = innovationSubIdos.get(innovationSubIdos.size() - 1);

      if (lastInnovationSubIdo.getPhase() == lastCrpPhase) {
        // the policy innovations got replicated all the way to the last phase, we do not have to do anything else
        innovationSubIdos = Collections.emptyList();
      } else {
        // the policy innovation replication stopped some place before the last phase, let's replicate everything
        // from that point onwards
        innovationSubIdos.removeIf(pc -> !pc.getPhase().equals(lastInnovationSubIdo.getPhase()));
      }

      for (ProjectPolicyInnovation ppi : innovationSubIdos) {
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
        Phase lastCrpPhase = this.phaseManager.getLastCrpPhase(this.selectedPhase.getCrp().getId());
        ProjectInnovation currentInnovation = null;
        for (String id : ids) {
          LOG.debug("Replicating innovation: " + id);
          long innovationIdLong = Long.parseLong(id);
          currentInnovation = this.projectInnovationManager.getProjectInnovationById(innovationIdLong);
          if (currentInnovation != null) {
            this.replicateProjectInnovationInfos(innovationIdLong, lastCrpPhase);
            this.replicateProjectInnovationCenters(innovationIdLong, lastCrpPhase);
            this.replicateProjectInnovationContributingOrganizations(innovationIdLong, lastCrpPhase);
            this.replicateProjectInnovationCountries(innovationIdLong, lastCrpPhase);
            this.replicateProjectInnovationCrps(innovationIdLong, lastCrpPhase);
            this.replicateProjectInnovationDeliverables(innovationIdLong, lastCrpPhase);
            this.replicateProjectInnovationGeographicScopes(innovationIdLong, lastCrpPhase);
            this.replicateProjectInnovationMilestones(innovationIdLong, lastCrpPhase);
            this.replicateProjectInnovationOrganizations(innovationIdLong, lastCrpPhase);
            this.replicateProjectInnovationRegions(innovationIdLong, lastCrpPhase);
            this.replicateProjectInnovationShared(innovationIdLong, lastCrpPhase);
            this.replicateProjectInnovationSubIdos(innovationIdLong, lastCrpPhase);

            this.replicateProjectExpectedStudyInnovations(innovationIdLong, lastCrpPhase);
            this.replicateProjectPolicyInnovations(innovationIdLong, lastCrpPhase);
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
