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
import org.cgiar.ccafs.marlo.data.manager.PolicyMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCenterManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyOwnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicySubIdoManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PolicyMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCenter;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyOwner;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicySubIdo;
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

public class ProjectPolicyReplication extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -2571425391459366525L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ProjectPolicyReplication.class);

  // Managers
  private GlobalUnitManager globalUnitManager;
  private PhaseManager phaseManager;

  private ProjectPolicyManager projectPolicyManager;
  private ProjectPolicyCenterManager projectPolicyCenterManager;
  private ProjectPolicyCountryManager projectPolicyCountryManager;
  private ProjectPolicyCrossCuttingMarkerManager projectPolicyCrossCuttingMarkerManager;
  private ProjectPolicyCrpManager projectPolicyCrpManager;
  private ProjectPolicyGeographicScopeManager projectPolicyGeographicScopeManager;
  private ProjectPolicyInfoManager projectPolicyInfoManager;
  private ProjectPolicyInnovationManager projectPolicyInnovationManager;
  private PolicyMilestoneManager policyMilestoneManager;
  private ProjectPolicyOwnerManager projectPolicyOwnerManager;
  private ProjectPolicyRegionManager projectPolicyRegionManager;
  private ProjectPolicySubIdoManager projectPolicySubIdoManager;

  private ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager;

  // Variables
  private String entityByPhaseList;
  private List<GlobalUnit> crps;
  private long selectedPhaseID;
  private Phase selectedPhase;

  @Inject
  public ProjectPolicyReplication(APConfig config, GlobalUnitManager globalUnitManager,
    ProjectPolicyManager projectPolicyManager, ProjectPolicyCenterManager projectPolicyCenterManager,
    PhaseManager phaseManager, ProjectPolicyCountryManager projectPolicyCountryManager,
    ProjectPolicyCrossCuttingMarkerManager projectPolicyCrossCuttingMarkerManager,
    ProjectPolicyCrpManager projectPolicyCrpManager,
    ProjectPolicyGeographicScopeManager projectPolicyGeographicScopeManager,
    ProjectPolicyInfoManager projectPolicyInfoManager, ProjectPolicyInnovationManager projectPolicyInnovationManager,
    ProjectPolicyOwnerManager projectPolicyOwnerManager, ProjectPolicyRegionManager projectPolicyRegionManager,
    ProjectPolicySubIdoManager projectPolicySubIdoManager,
    ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager,
    PolicyMilestoneManager policyMilestoneManager) {
    super(config);
    this.globalUnitManager = globalUnitManager;
    this.projectPolicyManager = projectPolicyManager;
    this.projectPolicyCenterManager = projectPolicyCenterManager;
    this.phaseManager = phaseManager;
    this.projectPolicyCountryManager = projectPolicyCountryManager;
    this.projectPolicyCrossCuttingMarkerManager = projectPolicyCrossCuttingMarkerManager;
    this.projectPolicyCrpManager = projectPolicyCrpManager;
    this.projectPolicyGeographicScopeManager = projectPolicyGeographicScopeManager;
    this.projectPolicyInfoManager = projectPolicyInfoManager;
    this.projectPolicyInnovationManager = projectPolicyInnovationManager;
    this.projectPolicyOwnerManager = projectPolicyOwnerManager;
    this.projectPolicyRegionManager = projectPolicyRegionManager;
    this.projectPolicySubIdoManager = projectPolicySubIdoManager;
    this.projectExpectedStudyPolicyManager = projectExpectedStudyPolicyManager;
    this.policyMilestoneManager = policyMilestoneManager;
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
   * Replicates the ProjectExpectedStudyPolicy associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyPolicies(long policyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyPolicy> expectedStudyPolicies =
      this.projectExpectedStudyPolicyManager.getAllExpectedStudyPoliciesByPolicy(policyId);
    expectedStudyPolicies.removeIf(
      pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getProjectExpectedStudy() == null);
    if (this.isNotEmpty(expectedStudyPolicies)) {
      ProjectExpectedStudyPolicy lastExpectedStudyPolicy = expectedStudyPolicies.get(expectedStudyPolicies.size() - 1);

      if (lastExpectedStudyPolicy.getPhase() == lastCrpPhase) {
        // the expected policies got replicated all the way to the last phase, let's replicate everything from the
        // selected
        // phase on front
        // expectedStudyPolicies.removeIf(pc -> !pc.getPhase().equals(this.selectedPhase));
        expectedStudyPolicies = Collections.emptyList();
      } else {
        // the expected policy replication stopped some place before the last phase, let's replicate everything from
        // that
        // point onwards
        expectedStudyPolicies.removeIf(pc -> !pc.getPhase().equals(lastExpectedStudyPolicy.getPhase()));
      }

      for (ProjectExpectedStudyPolicy pesp : expectedStudyPolicies) {
        this.projectExpectedStudyPolicyManager.saveProjectExpectedStudyPolicy(pesp);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyCenters associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectPolicyCenters(long policyId, Phase lastCrpPhase) {
    List<ProjectPolicyCenter> policyCenters = this.projectPolicyCenterManager.getAllPolicyCentersByPolicy(policyId);
    policyCenters
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getInstitution() == null);
    if (this.isNotEmpty(policyCenters)) {
      ProjectPolicyCenter lastPolicyCenter = policyCenters.get(policyCenters.size() - 1);

      if (lastPolicyCenter.getPhase() == lastCrpPhase) {
        // the policy centers got replicated all the way to the last phase, let's replicate everything from the selected
        // phase on front
        // policyCenters.removeIf(pc -> !pc.getPhase().equals(this.selectedPhase));
        policyCenters = Collections.emptyList();
      } else {
        // the policy center replication stopped some place before the last phase, let's replicate everything from that
        // point onwards
        policyCenters.removeIf(pc -> !pc.getPhase().equals(lastPolicyCenter.getPhase()));
      }

      for (ProjectPolicyCenter pc : policyCenters) {
        this.projectPolicyCenterManager.saveProjectPolicyCenter(pc);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyCountries associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectPolicyCountries(long policyId, Phase lastCrpPhase) {
    List<ProjectPolicyCountry> policyCountries =
      this.projectPolicyCountryManager.getAllPolicyCountriesByPolicy(policyId);
    policyCountries
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getLocElement() == null);
    if (this.isNotEmpty(policyCountries)) {
      ProjectPolicyCountry lastPolicyCountry = policyCountries.get(policyCountries.size() - 1);

      if (lastPolicyCountry.getPhase() == lastCrpPhase) {
        // the policy countries got replicated all the way to the last phase, let's replicate everything from the
        // selected
        // phase on front
        // policyCountries.removeIf(pc -> !pc.getPhase().equals(this.selectedPhase));
        policyCountries = Collections.emptyList();
      } else {
        // the policy country replication stopped some place before the last phase, let's replicate everything from that
        // point onwards
        policyCountries.removeIf(pc -> !pc.getPhase().equals(lastPolicyCountry.getPhase()));
      }

      for (ProjectPolicyCountry pc : policyCountries) {
        this.projectPolicyCountryManager.saveProjectPolicyCountry(pc);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyCrossCuttingMarker associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectPolicyCrossCuttingMarkers(long policyId, Phase lastCrpPhase) {
    List<ProjectPolicyCrossCuttingMarker> policyCrossCuttingMarkers =
      this.projectPolicyCrossCuttingMarkerManager.getAllPolicyCrossCuttingMarkersByPolicy(policyId);
    policyCrossCuttingMarkers.removeIf(
      pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getCgiarCrossCuttingMarker() == null);
    if (this.isNotEmpty(policyCrossCuttingMarkers)) {
      ProjectPolicyCrossCuttingMarker lastPolicyCrossCuttingMarker =
        policyCrossCuttingMarkers.get(policyCrossCuttingMarkers.size() - 1);

      if (lastPolicyCrossCuttingMarker.getPhase() == lastCrpPhase) {
        // the policy markers got replicated all the way to the last phase, let's replicate everything from the selected
        // phase on front
        // policyCrossCuttingMarkers.removeIf(pc -> !pc.getPhase().equals(this.selectedPhase));
        policyCrossCuttingMarkers = Collections.emptyList();
      } else {
        // the policy marker replication stopped some place before the last phase, let's replicate everything from that
        // point onwards
        policyCrossCuttingMarkers.removeIf(pc -> !pc.getPhase().equals(lastPolicyCrossCuttingMarker.getPhase()));
      }

      for (ProjectPolicyCrossCuttingMarker pccm : policyCrossCuttingMarkers) {
        this.projectPolicyCrossCuttingMarkerManager.saveProjectPolicyCrossCuttingMarker(pccm);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyCrp associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectPolicyCrps(long policyId, Phase lastCrpPhase) {
    List<ProjectPolicyCrp> policyCrps = this.projectPolicyCrpManager.getAllPolicyCrpsByPolicy(policyId);
    policyCrps.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getGlobalUnit() == null);
    if (this.isNotEmpty(policyCrps)) {
      ProjectPolicyCrp lastPolicyCrp = policyCrps.get(policyCrps.size() - 1);

      if (lastPolicyCrp.getPhase() == lastCrpPhase) {
        // the policy crps got replicated all the way to the last phase, let's replicate everything from the selected
        // phase on front
        // policyCrps.removeIf(pc -> !pc.getPhase().equals(this.selectedPhase));
        policyCrps = Collections.emptyList();
      } else {
        // the policy crp replication stopped some place before the last phase, let's replicate everything from that
        // point onwards
        policyCrps.removeIf(pc -> !pc.getPhase().equals(lastPolicyCrp.getPhase()));
      }

      for (ProjectPolicyCrp pc : policyCrps) {
        this.projectPolicyCrpManager.saveProjectPolicyCrp(pc);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyGeographicScope associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectPolicyGeoScopes(long policyId, Phase lastCrpPhase) {
    List<ProjectPolicyGeographicScope> policyGeoScopes =
      this.projectPolicyGeographicScopeManager.getAllPolicyGeographicScopesByPolicy(policyId);
    policyGeoScopes.removeIf(
      pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getRepIndGeographicScope() == null);
    if (this.isNotEmpty(policyGeoScopes)) {
      ProjectPolicyGeographicScope lastPolicyGeoScope = policyGeoScopes.get(policyGeoScopes.size() - 1);

      if (lastPolicyGeoScope.getPhase() == lastCrpPhase) {
        // the policy geoscopes got replicated all the way to the last phase, let's replicate everything from the
        // selected
        // phase on front
        // policyGeoScopes.removeIf(pc -> !pc.getPhase().equals(this.selectedPhase));
        policyGeoScopes = Collections.emptyList();
      } else {
        // the policy geoscope replication stopped some place before the last phase, let's replicate everything from
        // that
        // point onwards
        policyGeoScopes.removeIf(pc -> !pc.getPhase().equals(lastPolicyGeoScope.getPhase()));
      }

      for (ProjectPolicyGeographicScope pgs : policyGeoScopes) {
        this.projectPolicyGeographicScopeManager.saveProjectPolicyGeographicScope(pgs);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyInfo associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectPolicyInfos(long policyId, Phase lastCrpPhase) {
    List<ProjectPolicyInfo> policyInfos = this.projectPolicyInfoManager.getAllPolicyInfosByPolicy(policyId);
    policyInfos.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null);
    if (this.isNotEmpty(policyInfos)) {
      ProjectPolicyInfo lastPolicyInfo = policyInfos.get(policyInfos.size() - 1);

      if (lastPolicyInfo.getPhase() == lastCrpPhase) {
        // the policy infos got replicated all the way to the last phase, let's replicate everything from the selected
        // phase on front
        // policyInfos.removeIf(pc -> !pc.getPhase().equals(this.selectedPhase));
        policyInfos = Collections.emptyList();
      } else {
        // the policy info replication stopped some place before the last phase, let's replicate everything from that
        // point onwards
        policyInfos.removeIf(pc -> !pc.getPhase().equals(lastPolicyInfo.getPhase()));
      }

      for (ProjectPolicyInfo pi : policyInfos) {
        this.projectPolicyInfoManager.saveProjectPolicyInfo(pi);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyInnovation associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectPolicyInnovations(long policyId, Phase lastCrpPhase) {
    List<ProjectPolicyInnovation> policyInnovations =
      this.projectPolicyInnovationManager.getAllPolicyInnovationsByPolicy(policyId);
    policyInnovations
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getProjectInnovation() == null);
    if (this.isNotEmpty(policyInnovations)) {
      ProjectPolicyInnovation lastPolicyInnovation = policyInnovations.get(policyInnovations.size() - 1);

      if (lastPolicyInnovation.getPhase() == lastCrpPhase) {
        // the policy innovations got replicated all the way to the last phase, let's replicate everything from the
        // selected phase on front
        // policyInnovations.removeIf(pc -> !pc.getPhase().equals(this.selectedPhase));
        policyInnovations = Collections.emptyList();
      } else {
        // the policy innovation replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        policyInnovations.removeIf(pc -> !pc.getPhase().equals(lastPolicyInnovation.getPhase()));
      }

      for (ProjectPolicyInnovation pi : policyInnovations) {
        this.projectPolicyInnovationManager.saveProjectPolicyInnovation(pi);
      }
    }
  }

  /**
   * Replicates the PolicyMilestone associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectPolicyMilestones(long policyId, Phase lastCrpPhase) {
    List<PolicyMilestone> policyMilestones = this.policyMilestoneManager.getAllPolicyMilestonesByPolicy(policyId);
    policyMilestones
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getCrpMilestone() == null);
    if (this.isNotEmpty(policyMilestones)) {
      PolicyMilestone lastPolicyMilestone = policyMilestones.get(policyMilestones.size() - 1);

      if (lastPolicyMilestone.getPhase() == lastCrpPhase) {
        // the policy milestones got replicated all the way to the last phase, let's replicate everything from the
        // selected
        // phase on front
        // policyMilestones.removeIf(pc -> !pc.getPhase().equals(this.selectedPhase));
        policyMilestones = Collections.emptyList();
      } else {
        // the policy milestone replication stopped some place before the last phase, let's replicate everything from
        // that
        // point onwards
        policyMilestones.removeIf(pc -> !pc.getPhase().equals(lastPolicyMilestone.getPhase()));
      }

      for (PolicyMilestone pm : policyMilestones) {
        this.policyMilestoneManager.savePolicyMilestone(pm);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyOwner associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectPolicyOwners(long policyId, Phase lastCrpPhase) {
    List<ProjectPolicyOwner> policyOwners = this.projectPolicyOwnerManager.getAllPolicyOwnersByPolicy(policyId);
    policyOwners
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getRepIndPolicyType() == null);
    if (this.isNotEmpty(policyOwners)) {
      ProjectPolicyOwner lastPolicyOwner = policyOwners.get(policyOwners.size() - 1);

      if (lastPolicyOwner.getPhase() == lastCrpPhase) {
        // the policy owners got replicated all the way to the last phase, let's replicate everything from the selected
        // phase on front
        // policyOwners.removeIf(pc -> !pc.getPhase().equals(this.selectedPhase));
        policyOwners = Collections.emptyList();
      } else {
        // the policy owner replication stopped some place before the last phase, let's replicate everything from that
        // point onwards
        policyOwners.removeIf(pc -> !pc.getPhase().equals(lastPolicyOwner.getPhase()));
      }

      for (ProjectPolicyOwner po : policyOwners) {
        this.projectPolicyOwnerManager.saveProjectPolicyOwner(po);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyRegion associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectPolicyRegions(long policyId, Phase lastCrpPhase) {
    List<ProjectPolicyRegion> policyRegions = this.projectPolicyRegionManager.getAllPolicyRegionsByPolicy(policyId);
    policyRegions
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getLocElement() == null);
    if (this.isNotEmpty(policyRegions)) {
      ProjectPolicyRegion lastPolicyRegion = policyRegions.get(policyRegions.size() - 1);

      if (lastPolicyRegion.getPhase() == lastCrpPhase) {
        // the policy regions got replicated all the way to the last phase, let's replicate everything from the selected
        // phase on front
        // policyRegions.removeIf(pc -> !pc.getPhase().equals(this.selectedPhase));
        policyRegions = Collections.emptyList();
      } else {
        // the policy region replication stopped some place before the last phase, let's replicate everything from that
        // point onwards
        policyRegions.removeIf(pc -> !pc.getPhase().equals(lastPolicyRegion.getPhase()));
      }

      for (ProjectPolicyRegion pr : policyRegions) {
        this.projectPolicyRegionManager.saveProjectPolicyRegion(pr);
      }
    }
  }

  /**
   * Replicates the ProjectPolicySubIdo associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectPolicySubIdos(long policyId, Phase lastCrpPhase) {
    List<ProjectPolicySubIdo> policySubIdos = this.projectPolicySubIdoManager.getAllPolicySubIdosByPolicy(policyId);
    policySubIdos
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getSrfSubIdo() == null);
    if (this.isNotEmpty(policySubIdos)) {
      ProjectPolicySubIdo lastPolicySubIdo = policySubIdos.get(policySubIdos.size() - 1);

      if (lastPolicySubIdo.getPhase() == lastCrpPhase) {
        // the policy subidos got replicated all the way to the last phase, let's replicate everything from the selected
        // phase on front
        // policySubIdos.removeIf(pc -> !pc.getPhase().equals(this.selectedPhase));
        policySubIdos = Collections.emptyList();
      } else {
        // the policy subido replication stopped some place before the last phase, let's replicate everything from that
        // point onwards
        policySubIdos.removeIf(pc -> !pc.getPhase().equals(lastPolicySubIdo.getPhase()));
      }

      for (ProjectPolicySubIdo psi : policySubIdos) {
        this.projectPolicySubIdoManager.saveProjectPolicySubIdo(psi);
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
        ProjectPolicy currentPolicy = null;
        for (String id : ids) {
          LOG.debug("Replicating policy: " + id);
          long policyIdLong = Long.parseLong(id);
          currentPolicy = this.projectPolicyManager.getProjectPolicyById(policyIdLong);
          if (currentPolicy != null) {
            this.replicateProjectPolicyInfos(policyIdLong, lastCrpPhase);
            this.replicateProjectPolicyCenters(policyIdLong, lastCrpPhase);
            this.replicateProjectPolicyCrossCuttingMarkers(policyIdLong, lastCrpPhase);
            this.replicateProjectPolicyCrps(policyIdLong, lastCrpPhase);
            this.replicateProjectPolicyGeoScopes(policyIdLong, lastCrpPhase);
            this.replicateProjectPolicyRegions(policyIdLong, lastCrpPhase);
            this.replicateProjectPolicyCountries(policyIdLong, lastCrpPhase);
            this.replicateProjectPolicyInnovations(policyIdLong, lastCrpPhase);
            this.replicateProjectPolicyMilestones(policyIdLong, lastCrpPhase);
            this.replicateProjectPolicyOwners(policyIdLong, lastCrpPhase);
            this.replicateProjectPolicySubIdos(policyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudyPolicies(policyIdLong, lastCrpPhase);
          }
        }
      } else {
        LOG.debug("No policies selected");
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
