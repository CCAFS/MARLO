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
import org.cgiar.ccafs.marlo.data.model.CgiarCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PolicyMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
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
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyType;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Arrays;
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
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyPolicies(long policyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyPolicy replication...");
    List<ProjectExpectedStudyPolicy> expectedStudyPolicies =
      this.projectExpectedStudyPolicyManager.getAllExpectedStudyPoliciesByPolicy(policyId);
    expectedStudyPolicies.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getProjectExpectedStudy() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(expectedStudyPolicies)) {
      Map<ProjectExpectedStudy, ProjectExpectedStudyPolicy> uniqueStudies = new HashMap<>(expectedStudyPolicies.size());

      for (ProjectExpectedStudyPolicy pesp : expectedStudyPolicies) {
        ProjectExpectedStudy projectExpectedStudy = pesp.getProjectExpectedStudy();

        if (uniqueStudies.containsKey(projectExpectedStudy)) {
          LOG.info("The study {} is duplicated", projectExpectedStudy.getId());
        } else {
          uniqueStudies.put(projectExpectedStudy, pesp);
        }
      }

      for (ProjectExpectedStudyPolicy pesp : uniqueStudies.values()) {
        this.projectExpectedStudyPolicyManager.saveProjectExpectedStudyPolicy(pesp);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyCenters associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectPolicyCenters(long policyId, Phase selectedPhase) {
    LOG.info("Starting ProjectPolicyCenter replication...");
    List<ProjectPolicyCenter> policyCenters = this.projectPolicyCenterManager.getAllPolicyCentersByPolicy(policyId);
    policyCenters.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getInstitution() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(policyCenters)) {
      Map<Institution, ProjectPolicyCenter> uniqueCenters = new HashMap<>(policyCenters.size());

      for (ProjectPolicyCenter ppc : policyCenters) {
        Institution institution = ppc.getInstitution();

        if (uniqueCenters.containsKey(institution)) {
          LOG.info("The institution {} is duplicated", institution.getId());
        } else {
          uniqueCenters.put(institution, ppc);
        }
      }

      for (ProjectPolicyCenter pc : uniqueCenters.values()) {
        this.projectPolicyCenterManager.saveProjectPolicyCenter(pc);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyCountries associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectPolicyCountries(long policyId, Phase selectedPhase) {
    LOG.info("Starting ProjectPolicyCountry replication...");
    List<ProjectPolicyCountry> policyCountries =
      this.projectPolicyCountryManager.getAllPolicyCountriesByPolicy(policyId);
    policyCountries.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getLocElement() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(policyCountries)) {
      Map<LocElement, ProjectPolicyCountry> uniqueCountries = new HashMap<>(policyCountries.size());

      for (ProjectPolicyCountry ppc : policyCountries) {
        LocElement locElement = ppc.getLocElement();

        if (uniqueCountries.containsKey(locElement)) {
          LOG.info("The country {} is duplicated", locElement.getId());
        } else {
          uniqueCountries.put(locElement, ppc);
        }
      }

      for (ProjectPolicyCountry ppc : uniqueCountries.values()) {
        this.projectPolicyCountryManager.saveProjectPolicyCountry(ppc);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyCrossCuttingMarker associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectPolicyCrossCuttingMarkers(long policyId, Phase selectedPhase) {
    LOG.info("Starting ProjectPolicyCrossCuttingMarker replication...");
    List<ProjectPolicyCrossCuttingMarker> policyCrossCuttingMarkers =
      this.projectPolicyCrossCuttingMarkerManager.getAllPolicyCrossCuttingMarkersByPolicy(policyId);
    policyCrossCuttingMarkers.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getCgiarCrossCuttingMarker() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(policyCrossCuttingMarkers)) {
      Map<CgiarCrossCuttingMarker, ProjectPolicyCrossCuttingMarker> uniqueMarkers =
        new HashMap<>(policyCrossCuttingMarkers.size());

      for (ProjectPolicyCrossCuttingMarker ppccm : policyCrossCuttingMarkers) {
        CgiarCrossCuttingMarker cgiarCrossCuttingMarker = ppccm.getCgiarCrossCuttingMarker();

        if (uniqueMarkers.containsKey(cgiarCrossCuttingMarker)) {
          LOG.info("The cross-cutting marker {} is duplicated", cgiarCrossCuttingMarker.getId());
        } else {
          uniqueMarkers.put(cgiarCrossCuttingMarker, ppccm);
        }
      }

      for (ProjectPolicyCrossCuttingMarker ppccm : uniqueMarkers.values()) {
        this.projectPolicyCrossCuttingMarkerManager.saveProjectPolicyCrossCuttingMarker(ppccm);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyCrp associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectPolicyCrps(long policyId, Phase selectedPhase) {
    LOG.info("Starting ProjectPolicyCrp replication...");
    List<ProjectPolicyCrp> policyCrps = this.projectPolicyCrpManager.getAllPolicyCrpsByPolicy(policyId);
    policyCrps.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getGlobalUnit() == null
      || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(policyCrps)) {
      Map<GlobalUnit, ProjectPolicyCrp> uniqueCrps = new HashMap<>(policyCrps.size());

      for (ProjectPolicyCrp ppc : policyCrps) {
        GlobalUnit globalUnit = ppc.getGlobalUnit();

        if (uniqueCrps.containsKey(globalUnit)) {
          LOG.info("The CRP/Platform {} is duplicated", globalUnit.getId());
        } else {
          uniqueCrps.put(globalUnit, ppc);
        }
      }

      for (ProjectPolicyCrp ppc : uniqueCrps.values()) {
        this.projectPolicyCrpManager.saveProjectPolicyCrp(ppc);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyGeographicScope associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectPolicyGeoScopes(long policyId, Phase selectedPhase) {
    LOG.info("Starting ProjectPolicyGeographicScope replication...");
    List<ProjectPolicyGeographicScope> policyGeoScopes =
      this.projectPolicyGeographicScopeManager.getAllPolicyGeographicScopesByPolicy(policyId);
    policyGeoScopes.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getRepIndGeographicScope() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(policyGeoScopes)) {
      Map<RepIndGeographicScope, ProjectPolicyGeographicScope> uniqueGeoScopes = new HashMap<>(policyGeoScopes.size());

      for (ProjectPolicyGeographicScope ppgs : policyGeoScopes) {
        RepIndGeographicScope repIndGeographicScope = ppgs.getRepIndGeographicScope();

        if (uniqueGeoScopes.containsKey(repIndGeographicScope)) {
          LOG.info("The geoscope {} is duplicated", repIndGeographicScope.getId());
        } else {
          uniqueGeoScopes.put(repIndGeographicScope, ppgs);
        }
      }

      for (ProjectPolicyGeographicScope ppgs : uniqueGeoScopes.values()) {
        this.projectPolicyGeographicScopeManager.saveProjectPolicyGeographicScope(ppgs);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyInfo associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectPolicyInfos(long policyId, Phase selectedPhase) {
    LOG.info("Starting ProjectPolicyInfo replication...");
    List<ProjectPolicyInfo> policyInfos = this.projectPolicyInfoManager.getAllPolicyInfosByPolicy(policyId);
    policyInfos.removeIf(
      pc -> pc == null || pc.getId() == null || pc.getPhase() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(policyInfos)) {
      ProjectPolicyInfo projectPolicyInfo = policyInfos.get(policyInfos.size() - 1);

      this.projectPolicyInfoManager.saveProjectPolicyInfo(projectPolicyInfo);
    }
  }

  /**
   * Replicates the ProjectPolicyInnovation associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectPolicyInnovations(long policyId, Phase selectedPhase) {
    LOG.info("Starting ProjectPolicyInnovation replication...");
    List<ProjectPolicyInnovation> policyInnovations =
      this.projectPolicyInnovationManager.getAllPolicyInnovationsByPolicy(policyId);
    policyInnovations.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getProjectInnovation() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(policyInnovations)) {
      Map<ProjectInnovation, ProjectPolicyInnovation> uniqueInnovations = new HashMap<>(policyInnovations.size());

      for (ProjectPolicyInnovation ppi : policyInnovations) {
        ProjectInnovation projectInnovation = ppi.getProjectInnovation();

        if (uniqueInnovations.containsKey(projectInnovation)) {
          LOG.info("The innovation {} is duplicated", projectInnovation.getId());
        } else {
          uniqueInnovations.put(projectInnovation, ppi);
        }
      }

      for (ProjectPolicyInnovation pi : uniqueInnovations.values()) {
        this.projectPolicyInnovationManager.saveProjectPolicyInnovation(pi);
      }
    }
  }

  /**
   * Replicates the PolicyMilestone associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectPolicyMilestones(long policyId, Phase selectedPhase) {
    LOG.info("Starting PolicyMilestone replication...");
    List<PolicyMilestone> policyMilestones = this.policyMilestoneManager.getAllPolicyMilestonesByPolicy(policyId);
    policyMilestones.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getCrpMilestone() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(policyMilestones)) {
      Map<CrpMilestone, PolicyMilestone> uniqueMilestones = new HashMap<>(policyMilestones.size());

      for (PolicyMilestone pm : policyMilestones) {
        CrpMilestone crpMilestone = pm.getCrpMilestone();

        if (uniqueMilestones.containsKey(crpMilestone)) {
          LOG.info("The milestone {} is duplicated", crpMilestone.getId());
        } else {
          uniqueMilestones.put(crpMilestone, pm);
        }
      }

      for (PolicyMilestone pm : uniqueMilestones.values()) {
        this.policyMilestoneManager.savePolicyMilestone(pm);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyOwner associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectPolicyOwners(long policyId, Phase selectedPhase) {
    LOG.info("Starting ProjectPolicyOwner replication...");
    List<ProjectPolicyOwner> policyOwners = this.projectPolicyOwnerManager.getAllPolicyOwnersByPolicy(policyId);
    policyOwners.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getRepIndPolicyType() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(policyOwners)) {
      Map<RepIndPolicyType, ProjectPolicyOwner> uniquePolicyOwners = new HashMap<>(policyOwners.size());

      for (ProjectPolicyOwner ppo : policyOwners) {
        RepIndPolicyType repIndPolicyType = ppo.getRepIndPolicyType();

        if (uniquePolicyOwners.containsKey(repIndPolicyType)) {
          LOG.info("The policy type {} is duplicated", repIndPolicyType.getId());
        } else {
          uniquePolicyOwners.put(repIndPolicyType, ppo);
        }
      }

      for (ProjectPolicyOwner ppo : uniquePolicyOwners.values()) {
        this.projectPolicyOwnerManager.saveProjectPolicyOwner(ppo);
      }
    }
  }

  /**
   * Replicates the ProjectPolicyRegion associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectPolicyRegions(long policyId, Phase selectedPhase) {
    LOG.info("Starting ProjectPolicyRegion replication...");
    List<ProjectPolicyRegion> policyRegions = this.projectPolicyRegionManager.getAllPolicyRegionsByPolicy(policyId);
    policyRegions.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getLocElement() == null
      || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(policyRegions)) {
      Map<LocElement, ProjectPolicyRegion> uniqueRegions = new HashMap<>(policyRegions.size());

      for (ProjectPolicyRegion ppr : policyRegions) {
        LocElement locElement = ppr.getLocElement();

        if (uniqueRegions.containsKey(locElement)) {
          LOG.info("The region {} is duplicated", locElement.getId());
        } else {
          uniqueRegions.put(locElement, ppr);
        }
      }

      for (ProjectPolicyRegion ppr : uniqueRegions.values()) {
        this.projectPolicyRegionManager.saveProjectPolicyRegion(ppr);
      }
    }
  }

  /**
   * Replicates the ProjectPolicySubIdo associated to the policyId
   * 
   * @param policyId the policy identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectPolicySubIdos(long policyId, Phase selectedPhase) {
    LOG.info("Starting ProjectPolicySubIdo replication...");
    List<ProjectPolicySubIdo> policySubIdos = this.projectPolicySubIdoManager.getAllPolicySubIdosByPolicy(policyId);
    policySubIdos.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getSrfSubIdo() == null
      || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(policySubIdos)) {
      Map<SrfSubIdo, ProjectPolicySubIdo> uniqueSubIdos = new HashMap<>(policySubIdos.size());

      for (ProjectPolicySubIdo ppsi : policySubIdos) {
        SrfSubIdo srfSubIdo = ppsi.getSrfSubIdo();

        if (uniqueSubIdos.containsKey(srfSubIdo)) {
          LOG.info("The srf sub ido {} is duplicated", srfSubIdo.getId());
        } else {
          uniqueSubIdos.put(srfSubIdo, ppsi);
        }
      }

      for (ProjectPolicySubIdo ppsi : uniqueSubIdos.values()) {
        this.projectPolicySubIdoManager.saveProjectPolicySubIdo(ppsi);
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
        ProjectPolicy currentPolicy = null;
        for (String id : ids) {
          LOG.debug("Replicating policy: " + id);
          long policyIdLong = Long.parseLong(id);
          currentPolicy = this.projectPolicyManager.getProjectPolicyById(policyIdLong);
          if (currentPolicy != null) {
            this.replicateProjectPolicyInfos(policyIdLong, selectedPhase);
            this.replicateProjectPolicyCenters(policyIdLong, selectedPhase);
            this.replicateProjectPolicyCrossCuttingMarkers(policyIdLong, selectedPhase);
            this.replicateProjectPolicyCrps(policyIdLong, selectedPhase);
            this.replicateProjectPolicyGeoScopes(policyIdLong, selectedPhase);
            this.replicateProjectPolicyRegions(policyIdLong, selectedPhase);
            this.replicateProjectPolicyCountries(policyIdLong, selectedPhase);
            this.replicateProjectPolicyInnovations(policyIdLong, selectedPhase);
            this.replicateProjectPolicyMilestones(policyIdLong, selectedPhase);
            this.replicateProjectPolicyOwners(policyIdLong, selectedPhase);
            this.replicateProjectPolicySubIdos(policyIdLong, selectedPhase);

            this.replicateProjectExpectedStudyPolicies(policyIdLong, selectedPhase);
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
