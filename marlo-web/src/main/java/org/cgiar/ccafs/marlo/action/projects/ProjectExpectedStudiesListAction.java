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
import org.cgiar.ccafs.marlo.data.manager.ExpectedStudyProjectManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaStudyManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.StudyTypeManager;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressStudy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaStudy;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.StudyType;
import org.cgiar.ccafs.marlo.security.Permission;
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
public class ProjectExpectedStudiesListAction extends BaseAction {


  private static final long serialVersionUID = 5533305942651533875L;
  private static final Logger LOG = LoggerFactory.getLogger(ProjectExpectedStudiesListAction.class);

  // Managers
  private SectionStatusManager sectionStatusManager;
  private ProjectManager projectManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager;
  private ProjectExpectedStudyCrpManager projectExpectedStudyCrpManager;
  private StudyTypeManager studyTypeManager;
  private ExpectedStudyProjectManager expectedStudyProjectManager;

  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisFlagshipProgressStudyManager flagshipProgressStudyManager;
  private ReportSynthesisMeliaStudyManager reportSynthesisMeliaStudyManager;

  // Parameters or Variables
  private List<ProjectExpectedStudy> nonProjectStudies;
  private List<ProjectExpectedStudy> myNonProjectStudies;
  private List<ProjectExpectedStudy> projectStudies;
  private List<ProjectExpectedStudy> projectOldStudies;
  private Project project;
  private List<Integer> allYears;
  private long projectID;
  private long expectedID;
  private GlobalUnit loggedCrp;
  private String justification;


  @Inject
  public ProjectExpectedStudiesListAction(APConfig config, SectionStatusManager sectionStatusManager,
    ProjectManager projectManager, ProjectExpectedStudyManager projectExpectedStudyManager,
    ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager, StudyTypeManager studyTypeManager,
    ExpectedStudyProjectManager expectedStudyProjectManager, LiaisonInstitutionManager liaisonInstitutionManager,
    ProjectExpectedStudyCrpManager projectExpectedStudyCrpManager, ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisFlagshipProgressStudyManager flagshipProgressStudyManager,
    ReportSynthesisMeliaStudyManager reportSynthesisMeliaStudyManager) {
    super(config);
    this.sectionStatusManager = sectionStatusManager;
    this.projectManager = projectManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.projectExpectedStudyInfoManager = projectExpectedStudyInfoManager;
    this.studyTypeManager = studyTypeManager;
    this.expectedStudyProjectManager = expectedStudyProjectManager;
    this.projectExpectedStudyCrpManager = projectExpectedStudyCrpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.flagshipProgressStudyManager = flagshipProgressStudyManager;
    this.reportSynthesisMeliaStudyManager = reportSynthesisMeliaStudyManager;
  }

  @Override
  public String add() {
    ProjectExpectedStudy projectExpectedStudy = new ProjectExpectedStudy();

    if (project != null) {
      projectExpectedStudy.setProject(project);
    }

    projectExpectedStudy.setModificationJustification("");
    projectExpectedStudy.setYear(this.getActualPhase().getYear());

    projectExpectedStudy = projectExpectedStudyManager.saveProjectExpectedStudy(projectExpectedStudy);

    /*
     * ProjectExpectedStudyInfo projectExpectedStudyInfo = new ProjectExpectedStudyInfo(this.getActualPhase(),
     * projectExpectedStudy, "", "", "", "", "", "", "", "", "", "", "", "", "", this.getActualPhase().getYear());
     */

    ProjectExpectedStudyInfo projectExpectedStudyInfo = new ProjectExpectedStudyInfo();
    projectExpectedStudyInfo.setPhase(this.getActualPhase());
    projectExpectedStudyInfo.setProjectExpectedStudy(projectExpectedStudy);
    projectExpectedStudyInfo.setYear(this.getActualPhase().getYear());
    projectExpectedStudyInfo.setIsContribution(true);

    // when a project expected study is created, it is assigned by default status 2 = On going
    GeneralStatus status = new GeneralStatus();
    status.setId(new Long(2));
    projectExpectedStudyInfo.setStatus(status);

    long studyTypeID = -1;
    try {
      studyTypeID =
        Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.STUDY_TYPE_REQUEST_ID)));
      StudyType studyType = studyTypeManager.getStudyTypeById(studyTypeID);
      projectExpectedStudyInfo.setStudyType(studyType);
    } catch (Exception e) {
      studyTypeID = -1;
    }

    projectExpectedStudyInfo = projectExpectedStudyInfoManager.saveProjectExpectedStudyInfo(projectExpectedStudyInfo);


    expectedID = projectExpectedStudy.getId();

    if (expectedID > 0) {
      if (studyTypeID == 1L && !this.hasSpecificities(APConstants.CRP_ENABLE_NEXUS_LEVER_SDG_FIELDS)) {
        ProjectExpectedStudyCrp studyCrp = new ProjectExpectedStudyCrp();

        studyCrp.setGlobalUnit(this.loggedCrp);
        studyCrp.setProjectExpectedStudy(projectExpectedStudy);
        studyCrp.setPhase(this.getActualPhase());

        studyCrp = projectExpectedStudyCrpManager.saveProjectExpectedStudyCrp(studyCrp);
      }

      this.createSynthesisAssociation(projectExpectedStudy, studyTypeID);

      return SUCCESS;
    }

    return INPUT;
  }

  private void createSynthesisAssociation(ProjectExpectedStudy study, Long studyTypeId) {
    if (studyTypeId > 0) {
      LiaisonInstitution liaisonInstitution =
        this.liaisonInstitutionManager.findByAcronymAndCrp("PMU", this.getCrpID());
      if (liaisonInstitution != null) {
        ReportSynthesis reportSynthesis =
          this.reportSynthesisManager.findSynthesis(this.getActualPhase().getId(), liaisonInstitution.getId());
        if (reportSynthesis != null) {
          if (studyTypeId == 1L) {
            ReportSynthesisFlagshipProgressStudy flagshipProgressStudy = new ReportSynthesisFlagshipProgressStudy();
            // isActive means excluded
            flagshipProgressStudy.setActive(true);
            flagshipProgressStudy.setCreatedBy(this.getCurrentUser());
            flagshipProgressStudy.setProjectExpectedStudy(study);
            flagshipProgressStudy
              .setReportSynthesisFlagshipProgress(reportSynthesis.getReportSynthesisFlagshipProgress());

            flagshipProgressStudy =
              this.flagshipProgressStudyManager.saveReportSynthesisFlagshipProgressStudy(flagshipProgressStudy);
          } else {
            ReportSynthesisMeliaStudy reportSynthesisMeliaStudy = new ReportSynthesisMeliaStudy();
            // isActive means excluded
            reportSynthesisMeliaStudy.setActive(true);
            reportSynthesisMeliaStudy.setCreatedBy(this.getCurrentUser());
            reportSynthesisMeliaStudy.setProjectExpectedStudy(study);
            reportSynthesisMeliaStudy.setReportSynthesisMelia(reportSynthesis.getReportSynthesisMelia());

            reportSynthesisMeliaStudy =
              this.reportSynthesisMeliaStudyManager.saveReportSynthesisMeliaStudy(reportSynthesisMeliaStudy);
          }
        }
      }
    }
  }

  /*
   * @Override
   * public String delete() {
   * ProjectExpectedStudy projectExpectedStudyBD = projectExpectedStudyManager.getProjectExpectedStudyById(expectedID);
   * if (projectExpectedStudyBD.getSectionStatuses() != null) {
   * for (SectionStatus sectionStatus : projectExpectedStudyBD.getSectionStatuses()) {
   * sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
   * }
   * }
   * projectExpectedStudyBD.setModificationJustification(justification);
   * projectExpectedStudyManager.deleteProjectExpectedStudy(projectExpectedStudyBD.getId());
   * return SUCCESS;
   * }
   */

  @Override
  public String delete() {
    // if (project.getExpectedStudies() != null) {
    if (project != null) {
      if (projectStudies != null) {
        // for (ProjectExpectedStudy projectStudy : project.getExpectedStudies()) {
        for (ProjectExpectedStudy projectStudy : projectStudies) {
          if (projectStudy.getId().longValue() == expectedID) {
            ProjectExpectedStudy projectExpectedBD =
              projectExpectedStudyManager.getProjectExpectedStudyById(expectedID);
            for (SectionStatus sectionStatus : projectExpectedBD.getSectionStatuses()) {
              sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
            }

            projectStudy.setModificationJustification(justification);
            projectExpectedStudyManager.deleteProjectExpectedStudy(projectStudy.getId());
          }
        }
      }
    } else {
      for (ProjectExpectedStudy projectStudy : myNonProjectStudies) {
        if (projectStudy.getId().longValue() == expectedID) {
          ProjectExpectedStudy projectExpectedBD = projectExpectedStudyManager.getProjectExpectedStudyById(expectedID);
          for (SectionStatus sectionStatus : projectExpectedBD.getSectionStatuses()) {
            sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
          }

          projectStudy.setModificationJustification(justification);
          projectExpectedStudyManager.deleteProjectExpectedStudy(projectStudy.getId());
        }
      }
    }

    return SUCCESS;
  }

  @Override
  public List<Integer> getAllYears() {
    return allYears;
  }

  public long getExpectedID() {
    return expectedID;
  }


  @Override
  public String getJustification() {
    return justification;
  }


  public List<ProjectExpectedStudy> getMyNonProjectStudies() {
    return myNonProjectStudies;
  }

  public List<ProjectExpectedStudy> getNonProjectStudies() {
    return nonProjectStudies;
  }


  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }


  public List<ProjectExpectedStudy> getProjectOldStudies() {
    return projectOldStudies;
  }

  public List<ProjectExpectedStudy> getProjectStudies() {
    return projectStudies;
  }

  @Override
  public void prepare() throws Exception {

    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    this.setPhaseID(this.getActualPhase().getId());

    // this.updateCrpAffiliation();

    try {
      projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
      project = projectManager.getProjectById(projectID);
      allYears = project.getProjecInfoPhase(this.getActualPhase()).getAllYears();
    } catch (Exception e) {
      project = null;
    }


    if (project != null) {

      List<ProjectExpectedStudy> allProjectStudies = new ArrayList<ProjectExpectedStudy>();

      // Load Studies
      List<ProjectExpectedStudy> studies = project.getProjectExpectedStudies().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudyInfo(this.getActualPhase()) != null)
        .collect(Collectors.toList());
      if (studies != null && studies.size() > 0) {
        allProjectStudies.addAll(studies);
      }

      // Load Shared studies
      List<ExpectedStudyProject> expectedStudyProject =
        this.expectedStudyProjectManager.getByProjectAndPhase(project.getId(), this.getPhaseID()) != null
          ? this.expectedStudyProjectManager.getByProjectAndPhase(project.getId(), this.getPhaseID()).stream()
            .filter(px -> px.isActive() && px.getProjectExpectedStudy().isActive()
              && px.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getActualPhase()) != null)
            .collect(Collectors.toList())
          : Collections.emptyList();
      if (expectedStudyProject != null && expectedStudyProject.size() > 0) {
        for (ExpectedStudyProject expectedStudy : expectedStudyProject) {
          if (!allProjectStudies.contains(expectedStudy.getProjectExpectedStudy())) {
            allProjectStudies.add(expectedStudy.getProjectExpectedStudy());
          }
        }
      }

      if (allProjectStudies != null && allProjectStudies.size() > 0) {
        // Editable project studies: Current cycle year-1 will be editable except Complete and Cancelled.
        // Every study of the current cycle year will be editable
        projectStudies = new ArrayList<ProjectExpectedStudy>();
        projectStudies = allProjectStudies.stream()
          .filter(ps -> ps.getProjectExpectedStudyInfo().getYear() != null
            && ps.getProjectExpectedStudyInfo().getStatus() != null
            && ps.getProjectExpectedStudyInfo().getYear() >= this.getCurrentCycleYear())
          .collect(Collectors.toList());

        // Non Editable project studies
        projectOldStudies = new ArrayList<ProjectExpectedStudy>();
        if (projectStudies == null || projectStudies.isEmpty()) {
          projectOldStudies.addAll(allProjectStudies);
        } else {
          for (ProjectExpectedStudy projectExpectedStudy : allProjectStudies) {
            List<ProjectExpectedStudy> studiesFiltered = projectStudies.stream()
              .filter(ps -> ps.getId().equals(projectExpectedStudy.getId())).collect(Collectors.toList());
            if (studiesFiltered == null || studiesFiltered.isEmpty()) {
              projectOldStudies.add(projectExpectedStudy);
            }
          }
        }
      }
    } else {
      nonProjectStudies = new ArrayList<>();
      List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
        .filter(s -> s.isActive() && s.getProject() == null).collect(Collectors.toList()));
      for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
        if (projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()) != null) {
          nonProjectStudies.add(projectExpectedStudy);
        }
      }
      myNonProjectStudies = new ArrayList<>();

      if (!nonProjectStudies.isEmpty()) {
        if (this.canAccessSuperAdmin() || this
          .hasPermission(this.generatePermission(Permission.STUDIES_FULL_EDIT_PERMISSION, loggedCrp.getAcronym()))) {

          for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
            if (projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()) != null) {
              nonProjectStudies.remove(projectExpectedStudy);
              myNonProjectStudies.add(projectExpectedStudy);
            }
          }

        } else {

          for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
            if (projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()) != null) {
              if (projectExpectedStudy.getCreatedBy().getId().equals(this.getCurrentUser().getId())) {
                nonProjectStudies.remove(projectExpectedStudy);
                myNonProjectStudies.add(projectExpectedStudy);
              }
            }
          }

          expectedStudies = new ArrayList<>(
            projectExpectedStudyManager.getUserStudies(this.getCurrentUser().getId(), loggedCrp.getAcronym()).stream()
              .filter(e -> e.isActive()).collect(Collectors.toList()));
          for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
            if (nonProjectStudies.contains(projectExpectedStudy)) {
              nonProjectStudies.remove(projectExpectedStudy);
              myNonProjectStudies.add(projectExpectedStudy);
            }
          }
        }
      }
    }
  }


  public void setAllYears(List<Integer> allYears) {
    this.allYears = allYears;
  }


  public void setExpectedID(long expectedID) {
    this.expectedID = expectedID;
  }

  @Override
  public void setJustification(String justification) {
    this.justification = justification;
  }

  public void setMyNonProjectStudies(List<ProjectExpectedStudy> myNonProjectStudies) {
    this.myNonProjectStudies = myNonProjectStudies;
  }

  public void setNonProjectStudies(List<ProjectExpectedStudy> nonProjectStudies) {
    this.nonProjectStudies = nonProjectStudies;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setProjectOldStudies(List<ProjectExpectedStudy> projectOldStudies) {
    this.projectOldStudies = projectOldStudies;
  }

  public void setProjectStudies(List<ProjectExpectedStudy> projectStudies) {
    this.projectStudies = projectStudies;
  }

  private void updateCrpAffiliation() {
    Comparator<Phase> phaseComparator = PhaseComparator.getInstance();

    Map<ProjectExpectedStudy, SortedSet<Phase>> ar2021AndBeyond =
      this.projectExpectedStudyInfoManager.findAll().stream()
        .filter(pii -> pii != null && pii.getId() != null && pii.getPhase() != null && pii.getPhase().getId() != null
          && pii.getPhase().getCrp() != null && pii.getPhase().getCrp().getId() != null && pii.getStudyType() != null
          && pii.getStudyType().getId() != null && pii.getStudyType().getId() == 1L
          && pii.getProjectExpectedStudy() != null && pii.getProjectExpectedStudy().getId() != null)
        .collect(Collectors.groupingBy(ProjectExpectedStudyInfo::getProjectExpectedStudy, Collectors.mapping(
          ProjectExpectedStudyInfo::getPhase, Collectors.toCollection(() -> new TreeSet<Phase>(phaseComparator)))));

    Map<GlobalUnit, Set<ProjectExpectedStudy>> studysPerCrp = this.projectExpectedStudyInfoManager.findAll().stream()
      .filter(pii -> pii != null && pii.getId() != null && pii.getPhase() != null && pii.getPhase().getId() != null
        && pii.getPhase().getCrp() != null && pii.getPhase().getCrp().getId() != null && pii.getStudyType() != null
        && pii.getStudyType().getId() != null && pii.getStudyType().getId() == 1L
        && pii.getProjectExpectedStudy() != null && pii.getProjectExpectedStudy().getId() != null)
      .collect(Collectors.groupingBy(pii -> ar2021AndBeyond.get(pii.getProjectExpectedStudy()).first().getCrp(),
        Collectors.mapping(ProjectExpectedStudyInfo::getProjectExpectedStudy,
          Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(ProjectExpectedStudy::getId))))));

    List<String> inserts = new ArrayList<>();
    for (Entry<GlobalUnit, Set<ProjectExpectedStudy>> entry : studysPerCrp.entrySet()) {
      GlobalUnit crp = entry.getKey();
      Long crpId = crp.getId();
      for (ProjectExpectedStudy study : entry.getValue()) {
        Long projectStudyId = study.getId();
        Set<Phase> allPhasesWithRows = ar2021AndBeyond.get(study);
        Map<Phase, Set<GlobalUnit>> studyLinkedCrpsPerPhase = study.getProjectExpectedStudyCrps().stream()
          .filter(pic -> pic != null && pic.getId() != null && pic.getGlobalUnit() != null
            && pic.getGlobalUnit().getId() != null && pic.getPhase() != null && pic.getPhase().getId() != null
            && pic.getPhase().getCrp() != null && pic.getPhase().getCrp().getId() != null)
          .collect(Collectors.groupingBy(pic -> pic.getPhase(), () -> new TreeMap<>(phaseComparator),
            Collectors.mapping(ProjectExpectedStudyCrp::getGlobalUnit, Collectors.toSet())));
        for (Phase phase : allPhasesWithRows) {
          Long phaseId = phase.getId();
          if (!studyLinkedCrpsPerPhase.getOrDefault(phase, Collections.emptySet()).contains(crp)) {
            StringBuilder insert = new StringBuilder(
              "INSERT INTO project_expected_study_crp(expected_id, global_unit_id, id_phase) VALUES (");
            insert = insert.append(projectStudyId).append(",").append(crpId).append(",").append(phaseId).append(");");
            inserts.add(insert.toString());
          }
        }
      }
    }

    LOG.info("test");


    Path fileSuccess = Paths.get("D:\\misc\\insert-ecrps.txt");
    try {
      Files.write(fileSuccess, inserts, StandardCharsets.UTF_8);
    } catch (IOException e) {
      LOG.error("rip");
      e.printStackTrace();
    }

  }

}
