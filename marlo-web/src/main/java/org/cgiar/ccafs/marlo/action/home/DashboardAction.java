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

package org.cgiar.ccafs.marlo.action.home;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dto.DeliverableHomeDTO;
import org.cgiar.ccafs.marlo.data.dto.InnovationHomeDTO;
import org.cgiar.ccafs.marlo.data.dto.ProjectHomeDTO;
import org.cgiar.ccafs.marlo.data.dto.StudyHomeDTO;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PolicyHomeDTO;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DashboardAction extends BaseAction {

  private static final long serialVersionUID = 6686785556753962379L;
  private static final Logger LOG = LoggerFactory.getLogger(DashboardAction.class);

  // Managers
  private PhaseManager phaseManager;
  private ProjectManager projectManager;
  private GlobalUnitManager crpManager;
  private DeliverableManager deliverableManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ProjectInnovationManager projectInnovationManager;
  private ProjectPolicyManager projectPolicyManager;
  private CrpProgramManager crpProgramManager;

  // Variables
  private GlobalUnit loggedCrp;

  private List<ProjectHomeDTO> myProjects;
  private List<Project> myFullProjects;
  private List<DeliverableHomeDTO> myDeliverables = new ArrayList<>();
  private List<StudyHomeDTO> myOicrs = new ArrayList<>();
  private List<StudyHomeDTO> myMelias = new ArrayList<>();
  private List<InnovationHomeDTO> myInnovations = new ArrayList<>();
  private List<PolicyHomeDTO> myPolicies = new ArrayList<>();
  private Map<String, String> fpColors = new HashMap<>();

  @Inject
  public DashboardAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    PhaseManager phaseManager, DeliverableManager deliverableManager, ProjectPolicyManager projectPolicyManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, ProjectInnovationManager projectInnovationManager,
    CrpProgramManager crpProgramManager) {
    super(config);
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.phaseManager = phaseManager;
    this.deliverableManager = deliverableManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.projectInnovationManager = projectInnovationManager;
    this.projectPolicyManager = projectPolicyManager;
    this.crpProgramManager = crpProgramManager;
  }

  public Map<String, String> getFpColors() {
    return fpColors;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }


  /**
   * Get the value of myDeliverables
   *
   * @return the value of myDeliverables
   */
  public List<DeliverableHomeDTO> getMyDeliverables() {
    return myDeliverables;
  }


  public List<InnovationHomeDTO> getMyInnovations() {
    return myInnovations;
  }

  public List<StudyHomeDTO> getMyMelias() {
    return myMelias;
  }


  public List<StudyHomeDTO> getMyOicrs() {
    return myOicrs;
  }

  public List<PolicyHomeDTO> getMyPolicies() {
    return myPolicies;
  }

  public List<ProjectHomeDTO> getMyProjects() {
    return myProjects;
  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    Phase phase = phaseManager.getPhaseById(this.getActualPhase().getId());

    if (this.isSwitchSession()) {
      this.clearPermissionsCache();
    }

    // if (projectManager.findAll() != null) {
    myFullProjects = new ArrayList<>();
    myProjects = new ArrayList<>();
    if (this.canAccessSuperAdmin() || this.canAcessCrpAdmin()) {

      for (ProjectPhase projectPhase : phase.getProjectPhases()) {
        projectPhase.getProject().setProjectInfo(projectPhase.getProject().getProjecInfoPhase(this.getActualPhase()));
        myFullProjects.add(projectPhase.getProject());
      }


    } else {

      List<Project> allProjects = new ArrayList<>();
      if (phase != null) {
        /*
         * if (phase.getCrp().getGlobalUnitType().getId().equals(APConstants.CRP_DASHBOARD_CENTER_IDENTIFICATION)) {
         * for (ProjectPhase projectPhase : phase.getProjectPhases()) {
         * Project project = projectManager.getProjectById(projectPhase.getProject().getId());
         * if (this.isProjectSubmitted(project.getId())) {
         * allProjects.add(project);
         * }
         * }
         * }
         */
        for (ProjectPhase projectPhase : phase.getProjectPhases()) {
          allProjects.add(projectManager.getProjectById(projectPhase.getProject().getId()));
        }
      }


      AuthorizationInfo info = ((APCustomRealm) this.securityContext.getRealm())
        .getAuthorizationInfo(this.securityContext.getSubject().getPrincipals());


      for (String permission : info.getStringPermissions()) {
        if (permission.contains("project")) {
          for (int i = 0; i > permission.split(":").length; i++) {
            System.out.println(permission.split(":")[i]);
          }
        }
      }


      myFullProjects = projectManager.getUserProjects(this.getCurrentUser().getId(), loggedCrp.getAcronym()).stream()
        .filter(p -> p.isActive()).collect(Collectors.toList());


      List<Project> mProjects = new ArrayList<>();
      mProjects.addAll(myFullProjects);


      for (Project project : mProjects) {
        project.getProjecInfoPhase(this.getActualPhase());

        if (!allProjects.contains(project)) {
          myFullProjects.remove(project);
        }
      }


    }

    Stream<Project> projectStream = Stream.empty();

    // Skip closed projects for Reporting
    if (this.isPlanningActive()) {
      if (this.getActualPhase() != null && this.getActualPhase().getId() != null) {
        List<Project> closedProjects =
          projectManager.getCompletedProjects(this.getCrpID(), this.getActualPhase().getId());
        if (closedProjects != null) {
          // closedProjects.addAll(projectManager.getNoPhaseProjects(this.getCrpID(), this.getActualPhase()));
          myFullProjects.removeAll(closedProjects);
        }

        projectStream =
          myFullProjects.stream().filter(mp -> mp.isActive() && mp.getProjecInfoPhase(this.getActualPhase()) != null);
      }
    } else {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
      LOG.error(String.valueOf(this.getCurrentCycleYear()));

      projectStream = myFullProjects.stream()
        /*
         * .peek(mp -> LOG.error("P{} ends on {}", mp.getId(),
         * Integer.parseInt(dateFormat.format(mp.getProjecInfoPhase(this.getActualPhase()).getEndDate()))))
         */
        .filter(mp -> mp.isActive() && mp.getProjecInfoPhase(this.getActualPhase()) != null
          && (mp.getProjecInfoPhase(this.getActualPhase()).getEndDate() == null
            || Integer.parseInt(dateFormat.format(mp.getProjecInfoPhase(this.getActualPhase()).getEndDate())) >= this
              .getCurrentCycleYear())
          && StringUtils.isNotEmpty(mp.getProjecInfoPhase(this.getActualPhase()).getStatusName()));
    }

    myProjects = projectStream.map(p -> new ProjectHomeDTO(p.getId(),
      StringUtils.trim(p.getProjecInfoPhase(this.getActualPhase()) != null
        ? p.getProjecInfoPhase(this.getActualPhase()).getTitle() : null),
      StringUtils.defaultIfEmpty(p.getProjecInfoPhase(this.getActualPhase()).getStatusName(), "Not Defined"),
      projectManager.getPrograms(p.getId(), ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue(), this.getActualPhase().getId())
        .stream().filter(cp -> cp != null && cp.getId() != null && StringUtils.isNotEmpty(cp.getAcronym()))
        .map(cp -> cp.getAcronym()).collect(Collectors.toList())))
      .collect(Collectors.toList());

    Collections.sort(myProjects, (p1, p2) -> p1.getProjectId().compareTo(p2.getProjectId()));

    fpColors = crpProgramManager.findAll().stream()
      .filter(cp -> cp != null && cp.getId() != null && cp.getCrp() != null && cp.getCrp().getId() != null
        && cp.getCrp().getId().equals(this.getCrpID())
        && cp.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toMap(CrpProgram::getAcronym, CrpProgram::getColor));

    myDeliverables = myProjects.stream().filter(p -> p != null && p.getProjectId() != null)
      .flatMap(p -> deliverableManager
        .getDeliverablesByProjectAndPhaseHome(this.getActualPhase().getId(), p.getProjectId()).stream())
      .collect(Collectors.toList());

    Map<Boolean, List<StudyHomeDTO>> allStudies = myProjects.stream().filter(p -> p != null && p.getProjectId() != null)
      .flatMap(p -> projectExpectedStudyManager
        .getStudiesByProjectAndPhaseHome(this.getActualPhase().getId(), p.getProjectId()).stream())
      .collect(
        Collectors.partitioningBy(st -> StringUtils.startsWith(StringUtils.trimToNull(st.getStudyType()), "OICR")));

    myMelias.addAll(allStudies.get(false));
    myOicrs.addAll(allStudies.get(true));

    myInnovations = myProjects.stream().filter(p -> p != null && p.getProjectId() != null)
      .flatMap(p -> projectInnovationManager
        .getInnovationsByProjectAndPhaseHome(this.getActualPhase().getId(), p.getProjectId()).stream())
      .collect(Collectors.toList());

    myPolicies = myProjects.stream().filter(p -> p != null && p.getProjectId() != null)
      .flatMap(p -> projectPolicyManager
        .getPoliciesByProjectAndPhaseHome(this.getActualPhase().getId(), p.getProjectId()).stream())
      .collect(Collectors.toList());

    this.getSession().put(APConstants.USER_PROJECTS, myProjects);
    this.getSession().put(APConstants.USER_DELIVERABLES, myDeliverables);
    this.getSession().put(APConstants.USER_MELIAS, myMelias);
    this.getSession().put(APConstants.USER_OICRS, myOicrs);
    this.getSession().put(APConstants.USER_INNOVATIONS, myInnovations);
    this.getSession().put(APConstants.USER_POLICIES, myPolicies);

    this.getSession().put(APConstants.FP_COLORS, fpColors);
  }

  public void setFpColors(Map<String, String> fpColors) {
    this.fpColors = fpColors;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  /**
   * Set the value of myDeliverables
   *
   * @param myDeliverables new value of myDeliverables
   */
  public void setMyDeliverables(List<DeliverableHomeDTO> myDeliverables) {
    this.myDeliverables = myDeliverables;
  }

  public void setMyInnovations(List<InnovationHomeDTO> myInnovations) {
    this.myInnovations = myInnovations;
  }

  public void setMyMelias(List<StudyHomeDTO> myMelias) {
    this.myMelias = myMelias;
  }

  public void setMyOicrs(List<StudyHomeDTO> myStudies) {
    this.myOicrs = myStudies;
  }

  public void setMyPolicies(List<PolicyHomeDTO> myPolicies) {
    this.myPolicies = myPolicies;
  }

  public void setMyProjects(List<ProjectHomeDTO> myProjects) {
    this.myProjects = myProjects;
  }
}
