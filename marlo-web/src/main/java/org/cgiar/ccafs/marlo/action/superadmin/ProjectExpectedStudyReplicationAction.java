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
import org.cgiar.ccafs.marlo.data.model.AllianceLever;
import org.cgiar.ccafs.marlo.data.model.AllianceLeverOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Nexus;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
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
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.SdgTargets;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicator;
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
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyCenters(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyCenters replication...");
    List<ProjectExpectedStudyCenter> studyCenters =
      this.projectExpectedStudyCenterManager.getAllStudyCentersByStudy(studyId);
    studyCenters.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getInstitution() == null
      || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studyCenters)) {
      Map<Institution, ProjectExpectedStudyCenter> uniqueInstitutions = new HashMap<>(studyCenters.size());

      for (ProjectExpectedStudyCenter pesc : studyCenters) {
        Institution institution = pesc.getInstitution();

        if (uniqueInstitutions.containsKey(institution)) {
          LOG.info("The institution {} is duplicated", institution.getId());
        } else {
          uniqueInstitutions.put(institution, pesc);
        }
      }

      for (ProjectExpectedStudyCenter pesc : uniqueInstitutions.values()) {
        this.projectExpectedStudyCenterManager.saveProjectExpectedStudyCenter(pesc);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyCountries associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyCountries(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyCountry replication...");
    List<ProjectExpectedStudyCountry> studyCountries =
      this.projectExpectedStudyCountryManager.getAllStudyCountriesByStudy(studyId);
    studyCountries.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getLocElement() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studyCountries)) {
      Map<LocElement, ProjectExpectedStudyCountry> uniqueCountries = new HashMap<>(studyCountries.size());

      for (ProjectExpectedStudyCountry pesc : studyCountries) {
        LocElement locElement = pesc.getLocElement();

        if (uniqueCountries.containsKey(locElement)) {
          LOG.info("The country {} is duplicated", locElement.getId());
        } else {
          uniqueCountries.put(locElement, pesc);
        }
      }

      for (ProjectExpectedStudyCountry pesc : uniqueCountries.values()) {
        this.projectExpectedStudyCountryManager.saveProjectExpectedStudyCountry(pesc);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyCrps associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyCrps(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyCrp replication...");
    List<ProjectExpectedStudyCrp> studyCrps = this.projectExpectedStudyCrpManager.getAllStudyCrpsByStudy(studyId);
    studyCrps.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getGlobalUnit() == null
      || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studyCrps)) {
      Map<GlobalUnit, ProjectExpectedStudyCrp> uniqueCrps = new HashMap<>(studyCrps.size());

      for (ProjectExpectedStudyCrp pesc : studyCrps) {
        GlobalUnit globalUnit = pesc.getGlobalUnit();

        if (uniqueCrps.containsKey(globalUnit)) {
          LOG.info("The CRP/Platform {} is duplicated", globalUnit.getId());
        } else {
          uniqueCrps.put(globalUnit, pesc);
        }
      }

      for (ProjectExpectedStudyCrp pesc : uniqueCrps.values()) {
        this.projectExpectedStudyCrpManager.saveProjectExpectedStudyCrp(pesc);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyCrps associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyFlagships(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyFlagship replication...");
    List<ProjectExpectedStudyFlagship> studyFlagships =
      this.projectExpectedStudyFlagshipManager.getAllStudyFlagshipsByStudy(studyId);
    studyFlagships.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getCrpProgram() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studyFlagships)) {
      Map<CrpProgram, ProjectExpectedStudyFlagship> uniqueCrpPrograms = new HashMap<>(studyFlagships.size());

      for (ProjectExpectedStudyFlagship pesf : studyFlagships) {
        CrpProgram crpProgram = pesf.getCrpProgram();

        if (uniqueCrpPrograms.containsKey(crpProgram)) {
          LOG.info("The flagship {} is duplicated", crpProgram.getId());
        } else {
          uniqueCrpPrograms.put(crpProgram, pesf);
        }
      }

      for (ProjectExpectedStudyFlagship pesf : uniqueCrpPrograms.values()) {
        this.projectExpectedStudyFlagshipManager.saveProjectExpectedStudyFlagship(pesf);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyGeographicScopes associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyGeographicScopes(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyGeographicScope replication...");
    List<ProjectExpectedStudyGeographicScope> studyGeoScopes =
      this.projectExpectedStudyGeographicScopeManager.getAllStudyGeoScopesByStudy(studyId);
    studyGeoScopes.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getRepIndGeographicScope() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studyGeoScopes)) {
      Map<RepIndGeographicScope, ProjectExpectedStudyGeographicScope> uniqueGeoScopes =
        new HashMap<>(studyGeoScopes.size());

      for (ProjectExpectedStudyGeographicScope pesgs : studyGeoScopes) {
        RepIndGeographicScope repIndGeographicScope = pesgs.getRepIndGeographicScope();

        if (uniqueGeoScopes.containsKey(repIndGeographicScope)) {
          LOG.info("The geoscope {} is duplicated", repIndGeographicScope.getId());
        } else {
          uniqueGeoScopes.put(repIndGeographicScope, pesgs);
        }
      }

      for (ProjectExpectedStudyGeographicScope pesgs : uniqueGeoScopes.values()) {
        this.projectExpectedStudyGeographicScopeManager.saveProjectExpectedStudyGeographicScope(pesgs);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyInfos associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyInfos(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyInfo replication...");
    List<ProjectExpectedStudyInfo> studyInfos = this.projectExpectedStudyInfoManager.getAllStudyInfosByStudy(studyId);
    studyInfos.removeIf(
      pc -> pc == null || pc.getId() == null || pc.getPhase() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studyInfos)) {
      ProjectExpectedStudyInfo projectExpectedStudyInfo = studyInfos.get(studyInfos.size() - 1);

      this.projectExpectedStudyInfoManager.saveProjectExpectedStudyInfo(projectExpectedStudyInfo);
    }
  }

  /**
   * Replicates the ProjectExpectedStudyInfos associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyInnovations(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyInnovation replication...");
    List<ProjectExpectedStudyInnovation> studyInnnovations =
      this.projectExpectedStudyInnovationManager.getAllStudyInnovationsByStudy(studyId);
    studyInnnovations.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getProjectInnovation() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studyInnnovations)) {
      Map<ProjectInnovation, ProjectExpectedStudyInnovation> uniqueInnovations =
        new HashMap<>(studyInnnovations.size());

      for (ProjectExpectedStudyInnovation pesi : studyInnnovations) {
        ProjectInnovation projectInnovation = pesi.getProjectInnovation();

        if (uniqueInnovations.containsKey(projectInnovation)) {
          LOG.info("The innovation {} is duplicated", projectInnovation.getId());
        } else {
          uniqueInnovations.put(projectInnovation, pesi);
        }
      }

      for (ProjectExpectedStudyInnovation pesi : uniqueInnovations.values()) {
        this.projectExpectedStudyInnovationManager.saveProjectExpectedStudyInnovation(pesi);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyInstitutions associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyInstitutions(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyInstitution replication...");
    List<ProjectExpectedStudyInstitution> studyInstitutions =
      this.projectExpectedStudyInstitutionManager.getAllStudyInstitutionsByStudy(studyId);
    studyInstitutions.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getInstitution() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studyInstitutions)) {
      Map<Institution, ProjectExpectedStudyInstitution> uniqueInstitutions = new HashMap<>(studyInstitutions.size());

      for (ProjectExpectedStudyInstitution pesi : studyInstitutions) {
        Institution institution = pesi.getInstitution();

        if (uniqueInstitutions.containsKey(institution)) {
          LOG.info("The institution {} is duplicated", institution.getId());
        } else {
          uniqueInstitutions.put(institution, pesi);
        }
      }

      for (ProjectExpectedStudyInstitution pesi : uniqueInstitutions.values()) {
        this.projectExpectedStudyInstitutionManager.saveProjectExpectedStudyInstitution(pesi);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyLeverOutcomes associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyLeverOutcomes(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyLeverOutcome replication...");
    List<ProjectExpectedStudyLeverOutcome> studyLeverOutcomes =
      this.projectExpectedStudyLeverOutcomeManager.getAllStudyLeverOutcomesByStudy(studyId);
    studyLeverOutcomes.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getLeverOutcome() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studyLeverOutcomes)) {
      Map<AllianceLeverOutcome, ProjectExpectedStudyLeverOutcome> uniqueLeverOutcomes =
        new HashMap<>(studyLeverOutcomes.size());

      for (ProjectExpectedStudyLeverOutcome peslo : studyLeverOutcomes) {
        AllianceLeverOutcome allianceLeverOutcome = peslo.getLeverOutcome();

        if (uniqueLeverOutcomes.containsKey(allianceLeverOutcome)) {
          LOG.info("The lever outcome {} is duplicated", allianceLeverOutcome.getId());
        } else {
          uniqueLeverOutcomes.put(allianceLeverOutcome, peslo);
        }
      }

      for (ProjectExpectedStudyLeverOutcome peslo : uniqueLeverOutcomes.values()) {
        peslo = this.projectExpectedStudyLeverOutcomeManager.saveProjectExpectedStudyLeverOutcome(peslo);
        this.projectExpectedStudyLeverOutcomeManager.replicate(peslo, peslo.getPhase().getNext());
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyLevers associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyLevers(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyLever replication...");
    List<ProjectExpectedStudyLever> studyLevers =
      this.projectExpectedStudyLeverManager.getAllStudyLeversByStudy(studyId);
    studyLevers.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getAllianceLever() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studyLevers)) {
      Map<AllianceLever, ProjectExpectedStudyLever> uniqueLevers = new HashMap<>(studyLevers.size());

      for (ProjectExpectedStudyLever pesl : studyLevers) {
        AllianceLever allianceLever = pesl.getAllianceLever();

        if (uniqueLevers.containsKey(allianceLever)) {
          LOG.info("The lever {} is duplicated", allianceLever.getId());
        } else {
          uniqueLevers.put(allianceLever, pesl);
        }
      }

      for (ProjectExpectedStudyLever pesl : uniqueLevers.values()) {
        pesl = this.projectExpectedStudyLeverManager.saveProjectExpectedStudyLever(pesl);
        this.projectExpectedStudyLeverManager.replicate(pesl, pesl.getPhase().getNext());
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyLinks associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyLinks(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyLink replication...");
    List<ProjectExpectedStudyLink> studyLinks = this.projectExpectedStudyLinkManager.getAllStudyLinksByStudy(studyId);
    studyLinks.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || StringUtils.isBlank(pc.getLink()) || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studyLinks)) {
      for (ProjectExpectedStudyLink pesl : studyLinks) {
        this.projectExpectedStudyLinkManager.saveProjectExpectedStudyLink(pesl);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyMilestones associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyMilestones(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyMilestone replication...");
    List<ProjectExpectedStudyMilestone> studyMilestones =
      this.projectExpectedStudyMilestoneManager.getAllStudyMilestonesByStudy(studyId);
    studyMilestones.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getCrpMilestone() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studyMilestones)) {
      Map<CrpMilestone, ProjectExpectedStudyMilestone> uniqueMilestones = new HashMap<>(studyMilestones.size());

      for (ProjectExpectedStudyMilestone pesm : studyMilestones) {
        CrpMilestone crpMilestone = pesm.getCrpMilestone();

        if (uniqueMilestones.containsKey(crpMilestone)) {
          LOG.info("The milestone {} is duplicated", crpMilestone.getId());
        } else {
          uniqueMilestones.put(crpMilestone, pesm);
        }
      }

      for (ProjectExpectedStudyMilestone pesm : uniqueMilestones.values()) {
        this.projectExpectedStudyMilestoneManager.saveProjectExpectedStudyMilestone(pesm);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyNexuses associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyNexuses(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyNexus replication...");
    List<ProjectExpectedStudyNexus> studyNexuses =
      this.projectExpectedStudyNexusManager.getAllStudyNexussByStudy(studyId);
    studyNexuses.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getNexus() == null
      || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studyNexuses)) {
      Map<Nexus, ProjectExpectedStudyNexus> uniqueNexus = new HashMap<>(studyNexuses.size());

      for (ProjectExpectedStudyNexus pesn : studyNexuses) {
        Nexus nexus = pesn.getNexus();

        if (uniqueNexus.containsKey(nexus)) {
          LOG.info("The nexus {} is duplicated", nexus.getId());
        } else {
          uniqueNexus.put(nexus, pesn);
        }
      }

      for (ProjectExpectedStudyNexus pesn : uniqueNexus.values()) {
        pesn = this.projectExpectedStudyNexusManager.saveProjectExpectedStudyNexus(pesn);
        this.projectExpectedStudyNexusManager.replicate(pesn, pesn.getPhase().getNext());
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyPolicy associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyPolicies(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyNexus replication...");
    List<ProjectExpectedStudyPolicy> expectedStudyPolicies =
      this.projectExpectedStudyPolicyManager.getAllStudyPoliciesByStudy(studyId);
    expectedStudyPolicies.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getProjectPolicy() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(expectedStudyPolicies)) {
      Map<ProjectPolicy, ProjectExpectedStudyPolicy> uniquePolicies = new HashMap<>(expectedStudyPolicies.size());

      for (ProjectExpectedStudyPolicy pesp : expectedStudyPolicies) {
        ProjectPolicy projectPolicy = pesp.getProjectPolicy();

        if (uniquePolicies.containsKey(projectPolicy)) {
          LOG.info("The policy {} is duplicated", projectPolicy.getId());
        } else {
          uniquePolicies.put(projectPolicy, pesp);
        }
      }

      for (ProjectExpectedStudyPolicy pesp : uniquePolicies.values()) {
        this.projectExpectedStudyPolicyManager.saveProjectExpectedStudyPolicy(pesp);
      }
    }
  }

  /**
   * Replicates the ExpectedStudyProjects associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyProjects(long studyId, Phase selectedPhase) {
    LOG.info("Starting ExpectedStudyProject replication...");
    List<ExpectedStudyProject> studyProjects =
      this.projectExpectedStudyProjectManager.getAllStudyProjectsByStudy(studyId);
    studyProjects.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getProject() == null
      || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studyProjects)) {
      Map<Project, ExpectedStudyProject> uniquePolicies = new HashMap<>(studyProjects.size());

      for (ExpectedStudyProject esp : studyProjects) {
        Project project = esp.getProject();

        if (uniquePolicies.containsKey(project)) {
          LOG.info("The project {} is duplicated", project.getId());
        } else {
          uniquePolicies.put(project, esp);
        }
      }

      for (ExpectedStudyProject esp : uniquePolicies.values()) {
        this.projectExpectedStudyProjectManager.saveExpectedStudyProject(esp);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyQuantifications associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyQuantifications(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyQuantification replication...");
    List<ProjectExpectedStudyQuantification> studyQuantifications =
      this.projectExpectedStudyQuantificationManager.getAllStudyQuantificationsByStudy(studyId);
    studyQuantifications.removeIf(
      pc -> pc == null || pc.getId() == null || pc.getPhase() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studyQuantifications)) {
      for (ProjectExpectedStudyQuantification pesq : studyQuantifications) {
        this.projectExpectedStudyQuantificationManager.saveProjectExpectedStudyQuantification(pesq);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudyRegions associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudyRegions(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudyRegion replication...");
    List<ProjectExpectedStudyRegion> studyRegions =
      this.projectExpectedStudyRegionManager.getAllStudyRegionsByStudy(studyId);
    studyRegions.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getLocElement() == null
      || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studyRegions)) {
      Map<LocElement, ProjectExpectedStudyRegion> uniqueRegions = new HashMap<>(studyRegions.size());

      for (ProjectExpectedStudyRegion pesr : studyRegions) {
        LocElement locElement = pesr.getLocElement();

        if (uniqueRegions.containsKey(locElement)) {
          LOG.info("The region {} is duplicated", locElement.getId());
        } else {
          uniqueRegions.put(locElement, pesr);
        }
      }

      for (ProjectExpectedStudyRegion pesr : uniqueRegions.values()) {
        this.projectExpectedStudyRegionManager.saveProjectExpectedStudyRegion(pesr);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudySdgTargets associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudySdgTargets(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudySdgTarget replication...");
    List<ProjectExpectedStudySdgTarget> studySdgTargets =
      this.projectExpectedStudySdgTargetManager.getAllStudySdgTargetsByStudy(studyId);
    studySdgTargets.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getSdgTarget() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studySdgTargets)) {
      Map<SdgTargets, ProjectExpectedStudySdgTarget> uniqueSdgTargets = new HashMap<>(studySdgTargets.size());

      for (ProjectExpectedStudySdgTarget pesst : studySdgTargets) {
        SdgTargets sdgTargets = pesst.getSdgTarget();

        if (uniqueSdgTargets.containsKey(sdgTargets)) {
          LOG.info("The sdg target {} is duplicated", sdgTargets.getId());
        } else {
          uniqueSdgTargets.put(sdgTargets, pesst);
        }
      }

      for (ProjectExpectedStudySdgTarget pesst : uniqueSdgTargets.values()) {
        pesst = this.projectExpectedStudySdgTargetManager.saveProjectExpectedStudySdgTarget(pesst);
        this.projectExpectedStudySdgTargetManager.replicate(pesst, pesst.getPhase().getNext());
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudySrfTargets associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudySrfTargets(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudySrfTarget replication...");
    List<ProjectExpectedStudySrfTarget> studySrfTargets =
      this.projectExpectedStudySrfTargetManager.getAllStudySrfTargetsByStudy(studyId);
    studySrfTargets.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null
      || pc.getSrfSloIndicator() == null || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studySrfTargets)) {
      Map<SrfSloIndicator, ProjectExpectedStudySrfTarget> uniqueSrfSloIndicators =
        new HashMap<>(studySrfTargets.size());

      for (ProjectExpectedStudySrfTarget pesst : studySrfTargets) {
        SrfSloIndicator srfSloIndicator = pesst.getSrfSloIndicator();

        if (uniqueSrfSloIndicators.containsKey(srfSloIndicator)) {
          LOG.info("The srf slo indicator {} is duplicated", srfSloIndicator.getId());
        } else {
          uniqueSrfSloIndicators.put(srfSloIndicator, pesst);
        }
      }

      for (ProjectExpectedStudySrfTarget pesst : uniqueSrfSloIndicators.values()) {
        this.projectExpectedStudySrfTargetManager.saveProjectExpectedStudySrfTarget(pesst);
      }
    }
  }

  /**
   * Replicates the ProjectExpectedStudySubIdos associated to the studyId
   * 
   * @param studyId the study identifier
   * @param selectedPhase the CRP phase where the info is going to be replicated
   */
  private void replicateProjectExpectedStudySubIdos(long studyId, Phase selectedPhase) {
    LOG.info("Starting ProjectExpectedStudySubIdo replication...");
    List<ProjectExpectedStudySubIdo> studySubIdos =
      this.projectExpectedStudySubIdoManager.getAllStudySubIdosByStudy(studyId);
    studySubIdos.removeIf(pc -> pc == null || pc.getId() == null || pc.getPhase() == null || pc.getSrfSubIdo() == null
      || !pc.getPhase().equals(selectedPhase));

    if (this.isNotEmpty(studySubIdos)) {
      Map<SrfSubIdo, ProjectExpectedStudySubIdo> uniqueSrfSubIdos = new HashMap<>(studySubIdos.size());

      for (ProjectExpectedStudySubIdo pessi : studySubIdos) {
        SrfSubIdo srfSloIndicator = pessi.getSrfSubIdo();

        if (uniqueSrfSubIdos.containsKey(srfSloIndicator)) {
          LOG.info("The srf sub ido {} is duplicated", srfSloIndicator.getId());
        } else {
          uniqueSrfSubIdos.put(srfSloIndicator, pessi);
        }
      }

      for (ProjectExpectedStudySubIdo pessi : uniqueSrfSubIdos.values()) {
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
        // Phase lastCrpPhase = this.phaseManager.getLastCrpPhase(this.selectedPhase.getCrp().getId());
        ProjectExpectedStudy currentStudy = null;
        for (String id : ids) {
          LOG.debug("Replicating study: " + id);
          long studyIdLong = Long.parseLong(id);
          currentStudy = this.projectExpectedStudyManager.getProjectExpectedStudyById(studyIdLong);
          if (currentStudy != null) {
            this.replicateProjectExpectedStudyInfos(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudyProjects(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudyCenters(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudyGeographicScopes(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudyRegions(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudyCountries(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudyCrps(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudyFlagships(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudyInnovations(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudyInstitutions(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudyLevers(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudyLeverOutcomes(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudyLinks(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudyMilestones(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudyNexuses(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudyPolicies(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudyQuantifications(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudySdgTargets(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudySrfTargets(studyIdLong, selectedPhase);
            this.replicateProjectExpectedStudySubIdos(studyIdLong, selectedPhase);
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
