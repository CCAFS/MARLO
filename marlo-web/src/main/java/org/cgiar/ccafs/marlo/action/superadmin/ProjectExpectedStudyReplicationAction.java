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
import org.cgiar.ccafs.marlo.data.manager.ExpectedStudyProjectManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCenterManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyFlagshipManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyLeverManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyLeverOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyLinkManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyNexusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyQuantificationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySdgTargetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySrfTargetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySubIdoManager;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCenter;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLever;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLeverOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLink;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyNexus;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyQuantification;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySdgTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;
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

public class ProjectExpectedStudyReplicationAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -1478381892525972297L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ProjectExpectedStudyReplicationAction.class);

  // Managers
  private GlobalUnitManager globalUnitManager;
  private PhaseManager phaseManager;

  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager;
  private ExpectedStudyProjectManager projectExpectedStudyProjectManager;
  private ProjectExpectedStudyCenterManager projectExpectedStudyCenterManager;
  private ProjectExpectedStudyGeographicScopeManager projectExpectedStudyGeographicScopeManager;
  private ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager;
  private ProjectExpectedStudyRegionManager projectExpectedStudyRegionManager;
  private ProjectExpectedStudyCrpManager projectExpectedStudyCrpManager;
  private ProjectExpectedStudyFlagshipManager projectExpectedStudyFlagshipManager;
  private ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager;
  private ProjectExpectedStudyInstitutionManager projectExpectedStudyInstitutionManager;
  private ProjectExpectedStudyLeverManager projectExpectedStudyLeverManager;
  private ProjectExpectedStudyLeverOutcomeManager projectExpectedStudyLeverOutcomeManager;
  private ProjectExpectedStudyLinkManager projectExpectedStudyLinkManager;
  private ProjectExpectedStudyMilestoneManager projectExpectedStudyMilestoneManager;
  private ProjectExpectedStudyNexusManager projectExpectedStudyNexusManager;
  private ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager;
  private ProjectExpectedStudyQuantificationManager projectExpectedStudyQuantificationManager;
  private ProjectExpectedStudySdgTargetManager projectExpectedStudySdgTargetManager;
  private ProjectExpectedStudySrfTargetManager projectExpectedStudySrfTargetManager;
  private ProjectExpectedStudySubIdoManager projectExpectedStudySubIdoManager;

  // Variables
  private String entityByPhaseList;
  private List<GlobalUnit> crps;
  private long selectedPhaseID;
  private Phase selectedPhase;

  @Inject
  public ProjectExpectedStudyReplicationAction(APConfig config, PhaseManager phaseManager,
    GlobalUnitManager globalUnitManager, ProjectExpectedStudyManager projectExpectedStudyManager,
    ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager,
    ExpectedStudyProjectManager projectExpectedStudyProjectManager,
    ProjectExpectedStudyCenterManager projectExpectedStudyCenterManager,
    ProjectExpectedStudyGeographicScopeManager projectExpectedStudyGeographicScopeManager,
    ProjectExpectedStudyRegionManager projectExpectedStudyRegionManager,
    ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager,
    ProjectExpectedStudyCrpManager projectExpectedStudyCrpManager,
    ProjectExpectedStudyFlagshipManager projectExpectedStudyFlagshipManager,
    ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager,
    ProjectExpectedStudyInstitutionManager projectExpectedStudyInstitutionManager,
    ProjectExpectedStudyLeverManager projectExpectedStudyLeverManager,
    ProjectExpectedStudyLeverOutcomeManager projectExpectedStudyLeverOutcomeManager,
    ProjectExpectedStudyLinkManager projectExpectedStudyLinkManager,
    ProjectExpectedStudyMilestoneManager projectExpectedStudyMilestoneManager,
    ProjectExpectedStudyNexusManager projectExpectedStudyNexusManager,
    ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager,
    ProjectExpectedStudyQuantificationManager projectExpectedStudyQuantificationManager,
    ProjectExpectedStudySdgTargetManager projectExpectedStudySdgTargetManager,
    ProjectExpectedStudySrfTargetManager projectExpectedStudySrfTargetManager,
    ProjectExpectedStudySubIdoManager projectExpectedStudySubIdoManager) {
    super(config);
    this.globalUnitManager = globalUnitManager;
    this.phaseManager = phaseManager;

    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.projectExpectedStudyInfoManager = projectExpectedStudyInfoManager;
    this.projectExpectedStudyProjectManager = projectExpectedStudyProjectManager;
    this.projectExpectedStudyCenterManager = projectExpectedStudyCenterManager;
    this.projectExpectedStudyGeographicScopeManager = projectExpectedStudyGeographicScopeManager;
    this.projectExpectedStudyCountryManager = projectExpectedStudyCountryManager;
    this.projectExpectedStudyRegionManager = projectExpectedStudyRegionManager;
    this.projectExpectedStudyCrpManager = projectExpectedStudyCrpManager;
    this.projectExpectedStudyFlagshipManager = projectExpectedStudyFlagshipManager;
    this.projectExpectedStudyInnovationManager = projectExpectedStudyInnovationManager;
    this.projectExpectedStudyInstitutionManager = projectExpectedStudyInstitutionManager;
    this.projectExpectedStudyLeverManager = projectExpectedStudyLeverManager;
    this.projectExpectedStudyLeverOutcomeManager = projectExpectedStudyLeverOutcomeManager;
    this.projectExpectedStudyLinkManager = projectExpectedStudyLinkManager;
    this.projectExpectedStudyMilestoneManager = projectExpectedStudyMilestoneManager;
    this.projectExpectedStudyNexusManager = projectExpectedStudyNexusManager;
    this.projectExpectedStudyPolicyManager = projectExpectedStudyPolicyManager;
    this.projectExpectedStudyQuantificationManager = projectExpectedStudyQuantificationManager;
    this.projectExpectedStudySdgTargetManager = projectExpectedStudySdgTargetManager;
    this.projectExpectedStudySrfTargetManager = projectExpectedStudySrfTargetManager;
    this.projectExpectedStudySubIdoManager = projectExpectedStudySubIdoManager;
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
   * Replicates the ProjectExpectedStudyCenters associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyCenters(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyCenter> studyCenters =
      this.projectExpectedStudyCenterManager.getAllStudyCentersByStudy(studyId);
    studyCenters
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getInstitution() == null);
    if (this.isNotEmpty(studyCenters)) {
      ProjectExpectedStudyCenter lastStudyCenter = studyCenters.get(studyCenters.size() - 1);

      if (lastStudyCenter.getPhase() == lastCrpPhase) {
        // the study centers got replicated all the way to the last phase, we do not have to do anything else
        studyCenters = Collections.emptyList();
      } else {
        // the study center replication stopped some place before the last phase, let's replicate everything from that
        // point onwards
        studyCenters.removeIf(pc -> !pc.getPhase().equals(lastStudyCenter.getPhase()));
      }

      for (ProjectExpectedStudyCenter pesc : studyCenters) {
        this.projectExpectedStudyCenterManager.saveProjectExpectedStudyCenter(pesc);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyCountries associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyCountries(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyCountry> studyCountries =
      this.projectExpectedStudyCountryManager.getAllStudyCountriesByStudy(studyId);
    studyCountries
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getLocElement() == null);
    if (this.isNotEmpty(studyCountries)) {
      ProjectExpectedStudyCountry lastStudyCountry = studyCountries.get(studyCountries.size() - 1);

      if (lastStudyCountry.getPhase() == lastCrpPhase) {
        // the study countries got replicated all the way to the last phase, we do not have to do anything else
        studyCountries = Collections.emptyList();
      } else {
        // the study country replication stopped some place before the last phase, let's replicate everything from that
        // point onwards
        studyCountries.removeIf(pc -> !pc.getPhase().equals(lastStudyCountry.getPhase()));
      }

      for (ProjectExpectedStudyCountry pesc : studyCountries) {
        this.projectExpectedStudyCountryManager.saveProjectExpectedStudyCountry(pesc);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyCrps associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyCrps(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyCrp> studyCrps = this.projectExpectedStudyCrpManager.getAllStudyCrpsByStudy(studyId);
    studyCrps.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getGlobalUnit() == null);
    if (this.isNotEmpty(studyCrps)) {
      ProjectExpectedStudyCrp lastStudyCrp = studyCrps.get(studyCrps.size() - 1);

      if (lastStudyCrp.getPhase() == lastCrpPhase) {
        // the study crps got replicated all the way to the last phase, we do not have to do anything else
        studyCrps = Collections.emptyList();
      } else {
        // the study crp replication stopped some place before the last phase, let's replicate everything from that
        // point onwards
        studyCrps.removeIf(pc -> !pc.getPhase().equals(lastStudyCrp.getPhase()));
      }

      for (ProjectExpectedStudyCrp pesc : studyCrps) {
        this.projectExpectedStudyCrpManager.saveProjectExpectedStudyCrp(pesc);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyCrps associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyFlagships(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyFlagship> studyFlagships =
      this.projectExpectedStudyFlagshipManager.getAllStudyFlagshipsByStudy(studyId);
    studyFlagships
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getCrpProgram() == null);
    if (this.isNotEmpty(studyFlagships)) {
      ProjectExpectedStudyFlagship lastStudyFlagship = studyFlagships.get(studyFlagships.size() - 1);

      if (lastStudyFlagship.getPhase() == lastCrpPhase) {
        // the study flagships got replicated all the way to the last phase, we do not have to do anything else
        studyFlagships = Collections.emptyList();
      } else {
        // the study flagship replication stopped some place before the last phase, let's replicate everything from that
        // point onwards
        studyFlagships.removeIf(pc -> !pc.getPhase().equals(lastStudyFlagship.getPhase()));
      }

      for (ProjectExpectedStudyFlagship pesf : studyFlagships) {
        this.projectExpectedStudyFlagshipManager.saveProjectExpectedStudyFlagship(pesf);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyGeographicScopes associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyGeographicScopes(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyGeographicScope> studyGeoScopes =
      this.projectExpectedStudyGeographicScopeManager.getAllStudyGeoScopesByStudy(studyId);
    studyGeoScopes.removeIf(
      pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getRepIndGeographicScope() == null);
    if (this.isNotEmpty(studyGeoScopes)) {
      ProjectExpectedStudyGeographicScope lastStudyGeoScope = studyGeoScopes.get(studyGeoScopes.size() - 1);

      if (lastStudyGeoScope.getPhase() == lastCrpPhase) {
        // the study geoscopes got replicated all the way to the last phase, we do not have to do anything else
        studyGeoScopes = Collections.emptyList();
      } else {
        // the study geoscope replication stopped some place before the last phase, let's replicate everything from that
        // point onwards
        studyGeoScopes.removeIf(pc -> !pc.getPhase().equals(lastStudyGeoScope.getPhase()));
      }

      for (ProjectExpectedStudyGeographicScope pesgs : studyGeoScopes) {
        this.projectExpectedStudyGeographicScopeManager.saveProjectExpectedStudyGeographicScope(pesgs);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyInfos associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyInfos(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyInfo> studyInfos = this.projectExpectedStudyInfoManager.getAllStudyInfosByStudy(studyId);
    studyInfos.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null);
    if (this.isNotEmpty(studyInfos)) {
      ProjectExpectedStudyInfo lastStudyInfo = studyInfos.get(studyInfos.size() - 1);

      if (lastStudyInfo.getPhase() == lastCrpPhase) {
        // the study infos got replicated all the way to the last phase, we do not have to do anything else
        studyInfos = Collections.emptyList();
      } else {
        // the study info replication stopped some place before the last phase, let's replicate everything from that
        // point onwards
        studyInfos.removeIf(pc -> !pc.getPhase().equals(lastStudyInfo.getPhase()));
      }

      for (ProjectExpectedStudyInfo pesi : studyInfos) {
        this.projectExpectedStudyInfoManager.saveProjectExpectedStudyInfo(pesi);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyInfos associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyInnovations(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyInnovation> studyInnnovations =
      this.projectExpectedStudyInnovationManager.getAllStudyInnovationsByStudy(studyId);
    studyInnnovations
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getProjectInnovation() == null);
    if (this.isNotEmpty(studyInnnovations)) {
      ProjectExpectedStudyInnovation lastStudyInnovation = studyInnnovations.get(studyInnnovations.size() - 1);

      if (lastStudyInnovation.getPhase() == lastCrpPhase) {
        // the study innovations got replicated all the way to the last phase, we do not have to do anything else
        studyInnnovations = Collections.emptyList();
      } else {
        // the study innovation replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        studyInnnovations.removeIf(pc -> !pc.getPhase().equals(lastStudyInnovation.getPhase()));
      }

      for (ProjectExpectedStudyInnovation pesi : studyInnnovations) {
        this.projectExpectedStudyInnovationManager.saveProjectExpectedStudyInnovation(pesi);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyInstitutions associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyInstitutions(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyInstitution> studyInstitutions =
      this.projectExpectedStudyInstitutionManager.getAllStudyInstitutionsByStudy(studyId);
    studyInstitutions
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getInstitution() == null);
    if (this.isNotEmpty(studyInstitutions)) {
      ProjectExpectedStudyInstitution lastStudyInstitution = studyInstitutions.get(studyInstitutions.size() - 1);

      if (lastStudyInstitution.getPhase() == lastCrpPhase) {
        // the study institutions got replicated all the way to the last phase, we do not have to do anything else
        studyInstitutions = Collections.emptyList();
      } else {
        // the study institution replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        studyInstitutions.removeIf(pc -> !pc.getPhase().equals(lastStudyInstitution.getPhase()));
      }

      for (ProjectExpectedStudyInstitution pesi : studyInstitutions) {
        this.projectExpectedStudyInstitutionManager.saveProjectExpectedStudyInstitution(pesi);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyLeverOutcomes associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyLeverOutcomes(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyLeverOutcome> studyLeverOutcomes =
      this.projectExpectedStudyLeverOutcomeManager.getAllStudyLeverOutcomesByStudy(studyId);
    studyLeverOutcomes
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getLeverOutcome() == null);
    if (this.isNotEmpty(studyLeverOutcomes)) {
      ProjectExpectedStudyLeverOutcome lastStudyLeverOutcome = studyLeverOutcomes.get(studyLeverOutcomes.size() - 1);

      if (lastStudyLeverOutcome.getPhase() == lastCrpPhase) {
        // the study lever outcomes got replicated all the way to the last phase, we do not have to do anything else
        studyLeverOutcomes = Collections.emptyList();
      } else {
        // the study lever outcome replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        studyLeverOutcomes.removeIf(pc -> !pc.getPhase().equals(lastStudyLeverOutcome.getPhase()));
      }

      for (ProjectExpectedStudyLeverOutcome peslo : studyLeverOutcomes) {
        this.projectExpectedStudyLeverOutcomeManager.saveProjectExpectedStudyLeverOutcome(peslo);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyLevers associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyLevers(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyLever> studyLevers =
      this.projectExpectedStudyLeverManager.getAllStudyLeversByStudy(studyId);
    studyLevers
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getAllianceLever() == null);
    if (this.isNotEmpty(studyLevers)) {
      ProjectExpectedStudyLever lastStudyLever = studyLevers.get(studyLevers.size() - 1);

      if (lastStudyLever.getPhase() == lastCrpPhase) {
        // the study levers got replicated all the way to the last phase, we do not have to do anything else
        studyLevers = Collections.emptyList();
      } else {
        // the study lever replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        studyLevers.removeIf(pc -> !pc.getPhase().equals(lastStudyLever.getPhase()));
      }

      for (ProjectExpectedStudyLever pesl : studyLevers) {
        this.projectExpectedStudyLeverManager.saveProjectExpectedStudyLever(pesl);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyLinks associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyLinks(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyLink> studyLinks = this.projectExpectedStudyLinkManager.getAllStudyLinksByStudy(studyId);
    studyLinks
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || StringUtils.isBlank(pc.getLink()));
    if (this.isNotEmpty(studyLinks)) {
      ProjectExpectedStudyLink lastStudyLink = studyLinks.get(studyLinks.size() - 1);

      if (lastStudyLink.getPhase() == lastCrpPhase) {
        // the study links got replicated all the way to the last phase, we do not have to do anything else
        studyLinks = Collections.emptyList();
      } else {
        // the study link replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        studyLinks.removeIf(pc -> !pc.getPhase().equals(lastStudyLink.getPhase()));
      }

      for (ProjectExpectedStudyLink pesl : studyLinks) {
        this.projectExpectedStudyLinkManager.saveProjectExpectedStudyLink(pesl);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyMilestones associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyMilestones(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyMilestone> studyMilestones =
      this.projectExpectedStudyMilestoneManager.getAllStudyMilestonesByStudy(studyId);
    studyMilestones
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getCrpMilestone() == null);
    if (this.isNotEmpty(studyMilestones)) {
      ProjectExpectedStudyMilestone lastStudyMilestone = studyMilestones.get(studyMilestones.size() - 1);

      if (lastStudyMilestone.getPhase() == lastCrpPhase) {
        // the study milestones got replicated all the way to the last phase, we do not have to do anything else
        studyMilestones = Collections.emptyList();
      } else {
        // the study milestone replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        studyMilestones.removeIf(pc -> !pc.getPhase().equals(lastStudyMilestone.getPhase()));
      }

      for (ProjectExpectedStudyMilestone pesm : studyMilestones) {
        this.projectExpectedStudyMilestoneManager.saveProjectExpectedStudyMilestone(pesm);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyNexuses associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyNexuses(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyNexus> studyNexuses =
      this.projectExpectedStudyNexusManager.getAllStudyNexussByStudy(studyId);
    studyNexuses.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getNexus() == null);
    if (this.isNotEmpty(studyNexuses)) {
      ProjectExpectedStudyNexus lastStudyNexus = studyNexuses.get(studyNexuses.size() - 1);

      if (lastStudyNexus.getPhase() == lastCrpPhase) {
        // the study nexuses got replicated all the way to the last phase, we do not have to do anything else
        studyNexuses = Collections.emptyList();
      } else {
        // the study nexus replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        studyNexuses.removeIf(pc -> !pc.getPhase().equals(lastStudyNexus.getPhase()));
      }

      for (ProjectExpectedStudyNexus pesn : studyNexuses) {
        this.projectExpectedStudyNexusManager.saveProjectExpectedStudyNexus(pesn);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyPolicy associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyPolicies(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyPolicy> expectedStudyPolicies =
      this.projectExpectedStudyPolicyManager.getAllStudyPoliciesByStudy(studyId);
    expectedStudyPolicies
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getProjectPolicy() == null);
    if (this.isNotEmpty(expectedStudyPolicies)) {
      ProjectExpectedStudyPolicy lastExpectedStudyPolicy = expectedStudyPolicies.get(expectedStudyPolicies.size() - 1);

      if (lastExpectedStudyPolicy.getPhase() == lastCrpPhase) {
        // the expected policies got replicated all the way to the last phase, let's replicate everything from the
        // selected phase on front
        expectedStudyPolicies = Collections.emptyList();
      } else {
        // the expected policy replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        expectedStudyPolicies.removeIf(pc -> !pc.getPhase().equals(lastExpectedStudyPolicy.getPhase()));
      }

      for (ProjectExpectedStudyPolicy pesp : expectedStudyPolicies) {
        this.projectExpectedStudyPolicyManager.saveProjectExpectedStudyPolicy(pesp);
      }
    }
  }

  /**
   * Replicates the ExpectedStudyProjects associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyProjects(long studyId, Phase lastCrpPhase) {
    List<ExpectedStudyProject> studyProjects =
      this.projectExpectedStudyProjectManager.getAllStudyProjectsByStudy(studyId);
    studyProjects.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getProject() == null);
    if (this.isNotEmpty(studyProjects)) {
      ExpectedStudyProject lastStudyProject = studyProjects.get(studyProjects.size() - 1);

      if (lastStudyProject.getPhase() == lastCrpPhase) {
        // the study projects got replicated all the way to the last phase, we do not have to do anything else
        studyProjects = Collections.emptyList();
      } else {
        // the study project replication stopped some place before the last phase, let's replicate everything from that
        // point onwards
        studyProjects.removeIf(pc -> !pc.getPhase().equals(lastStudyProject.getPhase()));
      }

      for (ExpectedStudyProject esp : studyProjects) {
        this.projectExpectedStudyProjectManager.saveExpectedStudyProject(esp);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyQuantifications associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyQuantifications(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyQuantification> studyQuantifications =
      this.projectExpectedStudyQuantificationManager.getAllStudyQuantificationsByStudy(studyId);
    studyQuantifications.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null);
    if (this.isNotEmpty(studyQuantifications)) {
      ProjectExpectedStudyQuantification lastStudyQuantification =
        studyQuantifications.get(studyQuantifications.size() - 1);

      if (lastStudyQuantification.getPhase() == lastCrpPhase) {
        // the expected policies got replicated all the way to the last phase, let's replicate everything from the
        // selected phase on front
        studyQuantifications = Collections.emptyList();
      } else {
        // the expected policy replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        studyQuantifications.removeIf(pc -> !pc.getPhase().equals(lastStudyQuantification.getPhase()));
      }

      for (ProjectExpectedStudyQuantification pesq : studyQuantifications) {
        this.projectExpectedStudyQuantificationManager.saveProjectExpectedStudyQuantification(pesq);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyRegions associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudyRegions(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudyRegion> studyRegions =
      this.projectExpectedStudyRegionManager.getAllStudyRegionsByStudy(studyId);
    studyRegions
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getLocElement() == null);
    if (this.isNotEmpty(studyRegions)) {
      ProjectExpectedStudyRegion lastStudyRegion = studyRegions.get(studyRegions.size() - 1);

      if (lastStudyRegion.getPhase() == lastCrpPhase) {
        // the study projects got replicated all the way to the last phase, we do not have to do anything else
        studyRegions = Collections.emptyList();
      } else {
        // the study project replication stopped some place before the last phase, let's replicate everything from that
        // point onwards
        studyRegions.removeIf(pc -> !pc.getPhase().equals(lastStudyRegion.getPhase()));
      }

      for (ProjectExpectedStudyRegion pesr : studyRegions) {
        this.projectExpectedStudyRegionManager.saveProjectExpectedStudyRegion(pesr);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudySdgTargets associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudySdgTargets(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudySdgTarget> studySdgTargets =
      this.projectExpectedStudySdgTargetManager.getAllStudySdgTargetsByStudy(studyId);
    studySdgTargets
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getSdgTarget() == null);
    if (this.isNotEmpty(studySdgTargets)) {
      ProjectExpectedStudySdgTarget lastStudySdgTarget = studySdgTargets.get(studySdgTargets.size() - 1);

      if (lastStudySdgTarget.getPhase() == lastCrpPhase) {
        // the study sdg targets got replicated all the way to the last phase, we do not have to do anything else
        studySdgTargets = Collections.emptyList();
      } else {
        // the study sdg target replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        studySdgTargets.removeIf(pc -> !pc.getPhase().equals(lastStudySdgTarget.getPhase()));
      }

      for (ProjectExpectedStudySdgTarget pesst : studySdgTargets) {
        this.projectExpectedStudySdgTargetManager.saveProjectExpectedStudySdgTarget(pesst);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudySrfTargets associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudySrfTargets(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudySrfTarget> studySrfTargets =
      this.projectExpectedStudySrfTargetManager.getAllStudySrfTargetsByStudy(studyId);
    studySrfTargets
      .removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getSrfSloIndicator() == null);
    if (this.isNotEmpty(studySrfTargets)) {
      ProjectExpectedStudySrfTarget lastStudySrfTarget = studySrfTargets.get(studySrfTargets.size() - 1);

      if (lastStudySrfTarget.getPhase() == lastCrpPhase) {
        // the study srf targets got replicated all the way to the last phase, we do not have to do anything else
        studySrfTargets = Collections.emptyList();
      } else {
        // the study srf target replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        studySrfTargets.removeIf(pc -> !pc.getPhase().equals(lastStudySrfTarget.getPhase()));
      }

      for (ProjectExpectedStudySrfTarget pesst : studySrfTargets) {
        this.projectExpectedStudySrfTargetManager.saveProjectExpectedStudySrfTarget(pesst);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudySubIdos associated to the studyId
   * 
   * @param studyId the study identifier
   * @param lastCrpPhase the last CRP phase
   */
  private void replicateProjectExpectedStudySubIdos(long studyId, Phase lastCrpPhase) {
    List<ProjectExpectedStudySubIdo> studySubIdos =
      this.projectExpectedStudySubIdoManager.getAllStudySubIdosByStudy(studyId);
    studySubIdos.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getSrfSubIdo() == null);
    if (this.isNotEmpty(studySubIdos)) {
      ProjectExpectedStudySubIdo lastStudySubIdo = studySubIdos.get(studySubIdos.size() - 1);

      if (lastStudySubIdo.getPhase() == lastCrpPhase) {
        // the study sub idos got replicated all the way to the last phase, we do not have to do anything else
        studySubIdos = Collections.emptyList();
      } else {
        // the study sub ido replication stopped some place before the last phase, let's replicate everything from
        // that point onwards
        studySubIdos.removeIf(pc -> !pc.getPhase().equals(lastStudySubIdo.getPhase()));
      }

      for (ProjectExpectedStudySubIdo pessi : studySubIdos) {
        this.projectExpectedStudySubIdoManager.saveProjectExpectedStudySubIdo(pessi);
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
        ProjectExpectedStudy currentStudy = null;
        for (String id : ids) {
          LOG.debug("Replicating study: " + id);
          long studyIdLong = Long.parseLong(id);
          currentStudy = this.projectExpectedStudyManager.getProjectExpectedStudyById(studyIdLong);
          if (currentStudy != null) {
            this.replicateProjectExpectedStudyInfos(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudyProjects(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudyCenters(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudyGeographicScopes(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudyRegions(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudyCountries(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudyCrps(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudyFlagships(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudyInnovations(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudyInstitutions(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudyLevers(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudyLeverOutcomes(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudyLinks(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudyMilestones(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudyNexuses(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudyPolicies(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudyQuantifications(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudySdgTargets(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudySrfTargets(studyIdLong, lastCrpPhase);
            this.replicateProjectExpectedStudySubIdos(studyIdLong, lastCrpPhase);
          }
        }
      } else {
        LOG.debug("No studies selected");
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
