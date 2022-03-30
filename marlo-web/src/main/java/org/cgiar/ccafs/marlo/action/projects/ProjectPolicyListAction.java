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
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInfo;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressPolicy;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.PhaseComparator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectPolicyListAction extends BaseAction {


  private static final long serialVersionUID = 3586039079035252726L;
  private static final Logger LOG = LoggerFactory.getLogger(ProjectPolicyListAction.class);

  // Manager
  private ProjectPolicyManager projectPolicyManager;
  private ProjectPolicyInfoManager projectPolicyInfoManager;
  private ProjectPolicyCrpManager projectPolicyCrpManager;
  private SectionStatusManager sectionStatusManager;
  private ProjectManager projectManager;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;

  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisFlagshipProgressPolicyManager flagshipProgressPolicyManager;

  // Variables
  // Model for the back-end
  private Project project;
  // Model for the front-end
  private long projectID;
  private long policyID;
  private List<Integer> allYears;
  private List<ProjectPolicy> projectOldPolicies;
  private String justification;


  @Inject
  public ProjectPolicyListAction(APConfig config, ProjectPolicyManager projectPolicyManager,
    ProjectPolicyInfoManager projectPolicyInfoManager, SectionStatusManager sectionStatusManager,
    ProjectManager projectManager, ProjectPolicyCrpManager projectPolicyCrpManager,
    RepIndGeographicScopeManager repIndGeographicScopeManager, ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisFlagshipProgressPolicyManager flagshipProgressPolicyManager,
    LiaisonInstitutionManager liaisonInstitutionManager) {
    super(config);
    this.projectPolicyManager = projectPolicyManager;
    this.projectPolicyInfoManager = projectPolicyInfoManager;
    this.sectionStatusManager = sectionStatusManager;
    this.projectManager = projectManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.projectPolicyCrpManager = projectPolicyCrpManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.flagshipProgressPolicyManager = flagshipProgressPolicyManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
  }

  @Override
  public String add() {
    ProjectPolicy projectPolicy = new ProjectPolicy();

    projectPolicy.setProject(project);

    projectPolicy = projectPolicyManager.saveProjectPolicy(projectPolicy);

    ProjectPolicyInfo projectPolicyInfo =
      new ProjectPolicyInfo(this.getActualPhase(), projectPolicy, new Long(this.getCurrentCycleYear()));

    projectPolicyInfo = projectPolicyInfoManager.saveProjectPolicyInfo(projectPolicyInfo);

    if (!this.hasSpecificities(APConstants.CRP_ENABLE_NEXUS_LEVER_SDG_FIELDS)) {
      ProjectPolicyCrp projectPolicyThisCrp = new ProjectPolicyCrp();
      projectPolicyThisCrp.setGlobalUnit(this.getCurrentGlobalUnit());
      projectPolicyThisCrp.setPhase(this.getActualPhase());
      projectPolicyThisCrp.setProjectPolicy(projectPolicy);

      projectPolicyThisCrp = this.projectPolicyCrpManager.saveProjectPolicyCrp(projectPolicyThisCrp);
    }

    policyID = projectPolicy.getId();

    if (policyID > 0) {
      this.createSynthesisAssociation(projectPolicy);
      return SUCCESS;
    }

    return INPUT;
  }

  private void createSynthesisAssociation(ProjectPolicy policy) {
    LiaisonInstitution liaisonInstitution = this.liaisonInstitutionManager.findByAcronymAndCrp("PMU", this.getCrpID());
    if (liaisonInstitution != null) {
      ReportSynthesis reportSynthesis =
        this.reportSynthesisManager.findSynthesis(this.getActualPhase().getId(), liaisonInstitution.getId());
      if (reportSynthesis != null) {
        ReportSynthesisFlagshipProgressPolicy flagshipProgressPolicy = new ReportSynthesisFlagshipProgressPolicy();
        // isActive means excluded
        flagshipProgressPolicy.setActive(true);
        flagshipProgressPolicy.setCreatedBy(this.getCurrentUser());
        flagshipProgressPolicy.setProjectPolicy(policy);
        flagshipProgressPolicy.setReportSynthesisFlagshipProgress(reportSynthesis.getReportSynthesisFlagshipProgress());

        flagshipProgressPolicy =
          this.flagshipProgressPolicyManager.saveReportSynthesisFlagshipProgressPolicy(flagshipProgressPolicy);
      }
    }
  }

  @Override
  public String delete() {
    for (ProjectPolicy projectPolicy : project.getPolicies()) {
      if (projectPolicy.getId().longValue() == policyID) {
        ProjectPolicy projectPolicyBD = projectPolicyManager.getProjectPolicyById(policyID);
        for (SectionStatus sectionStatus : projectPolicyBD.getSectionStatuses()) {
          sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
        }
        projectPolicy.setModificationJustification(justification);
        projectPolicyManager.deleteProjectPolicy(projectPolicy.getId());
      }
    }
    return SUCCESS;
  }


  @Override
  public List<Integer> getAllYears() {
    return allYears;
  }

  @Override
  public String getJustification() {
    return justification;
  }

  public long getPolicyID() {
    return policyID;
  }


  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }

  public List<ProjectPolicy> getProjectOldPolicies() {
    return projectOldPolicies;
  }

  @Override
  public void prepare() throws Exception {
    projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    project = projectManager.getProjectById(projectID);

    // this.updateCrpAffiliation();

    allYears = project.getProjecInfoPhase(this.getActualPhase()).getAllYears();
    projectOldPolicies = new ArrayList<>();
    List<ProjectPolicy> policies =
      project.getProjectPolicies().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    project.setPolicies(new ArrayList<ProjectPolicy>());
    for (ProjectPolicy projectPolicy : policies) {
      if (projectPolicy.getProjectPolicyInfo(this.getActualPhase()) != null) {
        // SubIdos List
        if (projectPolicy.getProjectPolicySubIdos() != null) {
          projectPolicy.setSubIdos(new ArrayList<>(projectPolicy.getProjectPolicySubIdos().stream()
            .filter(o -> o.isActive() && o.getPhase().getId() == this.getActualPhase().getId())
            .collect(Collectors.toList())));
        }

        // SubIdos List
        if (projectPolicy.getProjectPolicySubIdos() != null) {
          projectPolicy.setSubIdos(new ArrayList<>(projectPolicy.getProjectPolicySubIdos().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getActualPhase().getId()))
            .collect(Collectors.toList())));
        }

        // Geographic Scope List
        if (projectPolicy.getProjectPolicyGeographicScopes() != null) {
          projectPolicy.setGeographicScopes(new ArrayList<>(projectPolicy.getProjectPolicyGeographicScopes().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getActualPhase().getId()))
            .collect(Collectors.toList())));
        }

        if (projectPolicy.getGeographicScopes() != null) {
          for (ProjectPolicyGeographicScope projectPolicyGeographicScope : projectPolicy.getGeographicScopes()) {
            projectPolicyGeographicScope.setRepIndGeographicScope(repIndGeographicScopeManager
              .getRepIndGeographicScopeById(projectPolicyGeographicScope.getRepIndGeographicScope().getId()));
          }
        }
        if (projectPolicy.getProjectPolicyInfo(this.getActualPhase()).isPrevious()) {
          projectOldPolicies.add(projectPolicy);
        } else {
          project.getPolicies().add(projectPolicy);
        }
      }
    }
  }

  public void setAllYears(List<Integer> allYears) {
    this.allYears = allYears;
  }

  @Override
  public void setJustification(String justification) {
    this.justification = justification;
  }

  public void setPolicyID(long policyID) {
    this.policyID = policyID;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setProjectOldPolicies(List<ProjectPolicy> projectOldPolicies) {
    this.projectOldPolicies = projectOldPolicies;
  }

  private void updateCrpAffiliation() {
    Comparator<Phase> phaseComparator = PhaseComparator.getInstance();

    Map<ProjectPolicy, SortedSet<Phase>> ar2021AndBeyond = this.projectPolicyInfoManager.findAll().stream()
      .filter(pii -> pii != null && pii.getId() != null && pii.getPhase() != null && pii.getPhase().getId() != null
        && pii.getPhase().getCrp() != null && pii.getPhase().getCrp().getId() != null && pii.getProjectPolicy() != null
        && pii.getProjectPolicy().getId() != null)
      .collect(Collectors.groupingBy(ProjectPolicyInfo::getProjectPolicy, Collectors
        .mapping(ProjectPolicyInfo::getPhase, Collectors.toCollection(() -> new TreeSet<Phase>(phaseComparator)))));

    Map<GlobalUnit, Set<ProjectPolicy>> policysPerCrp = this.projectPolicyInfoManager.findAll().stream()
      .filter(pii -> pii != null && pii.getId() != null && pii.getPhase() != null && pii.getPhase().getId() != null
        && pii.getPhase().getCrp() != null && pii.getPhase().getCrp().getId() != null && pii.getProjectPolicy() != null
        && pii.getProjectPolicy().getId() != null)
      .collect(Collectors.groupingBy(pii -> ar2021AndBeyond.get(pii.getProjectPolicy()).first().getCrp(),
        Collectors.mapping(ProjectPolicyInfo::getProjectPolicy,
          Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(ProjectPolicy::getId))))));

    List<String> inserts = new ArrayList<>();
    for (Entry<GlobalUnit, Set<ProjectPolicy>> entry : policysPerCrp.entrySet()) {
      GlobalUnit crp = entry.getKey();
      Long crpId = crp.getId();
      for (ProjectPolicy policy : entry.getValue()) {
        Long projectPolicyId = policy.getId();
        Set<Phase> allPhasesWithRows = ar2021AndBeyond.get(policy);
        Map<Phase, Set<GlobalUnit>> policyLinkedCrpsPerPhase = policy.getProjectPolicyCrps().stream()
          .filter(pic -> pic != null && pic.getId() != null && pic.getGlobalUnit() != null
            && pic.getGlobalUnit().getId() != null && pic.getPhase() != null && pic.getPhase().getId() != null
            && pic.getPhase().getCrp() != null && pic.getPhase().getCrp().getId() != null)
          .collect(Collectors.groupingBy(pic -> pic.getPhase(), () -> new TreeMap<>(phaseComparator),
            Collectors.mapping(ProjectPolicyCrp::getGlobalUnit, Collectors.toSet())));
        for (Phase phase : allPhasesWithRows) {
          Long phaseId = phase.getId();
          if (!policyLinkedCrpsPerPhase.getOrDefault(phase, Collections.emptySet()).contains(crp)) {
            StringBuilder insert = new StringBuilder(
              "INSERT INTO project_policy_crps(project_policy_id, global_unit_id, id_phase) VALUES (");
            insert = insert.append(projectPolicyId).append(",").append(crpId).append(",").append(phaseId).append(");");
            inserts.add(insert.toString());
          }
        }
      }
    }

    LOG.info("test");


    Path fileSuccess = Paths.get("D:\\misc\\insert-pcrps.txt");
    try {
      Files.write(fileSuccess, inserts, StandardCharsets.UTF_8);
    } catch (IOException e) {
      LOG.error("rip");
      e.printStackTrace();
    }

  }

}
