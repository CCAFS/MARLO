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
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationSharedManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationShared;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.Patterns;
import org.cgiar.ccafs.marlo.utils.PhaseComparator;

import java.util.ArrayList;
import java.util.Arrays;
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

import org.apache.commons.collections4.comparators.ComparatorChain;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectInnovationListAction extends BaseAction {


  private static final long serialVersionUID = 3586039079035252726L;
  private static final Logger LOG = LoggerFactory.getLogger(ProjectInnovationListAction.class);

  // Manager
  private ProjectInnovationSharedManager projectInnovationSharedManager;
  private ProjectInnovationInfoManager projectInnovationInfoManager;
  private ProjectInnovationManager projectInnovationManager;
  private ProjectInnovationCrpManager projectInnovationCrpManager;
  private PhaseManager phaseManager;

  private SectionStatusManager sectionStatusManager;
  private ProjectManager projectManager;

  // Variables
  // Model for the back-end
  private Project project;
  // Model for the front-end
  private long projectID;
  private long innovationID;
  private List<Integer> allYears;
  private List<ProjectInnovation> projectOldInnovations;
  private List<ProjectInnovation> projectInnovations;
  private String justification;


  @Inject
  public ProjectInnovationListAction(APConfig config, ProjectInnovationInfoManager projectInnovationInfoManager,
    SectionStatusManager sectionStatusManager, ProjectManager projectManager, PhaseManager phaseManager,
    ProjectInnovationManager projectInnovationManager, ProjectInnovationSharedManager projectInnovationSharedManager,
    ProjectInnovationCrpManager projectInnovationCrpManager) {
    super(config);
    this.sectionStatusManager = sectionStatusManager;
    this.projectManager = projectManager;
    this.projectInnovationInfoManager = projectInnovationInfoManager;
    this.projectInnovationManager = projectInnovationManager;
    this.projectInnovationSharedManager = projectInnovationSharedManager;
    this.projectInnovationCrpManager = projectInnovationCrpManager;
    this.phaseManager = phaseManager;
  }

  @Override
  public String add() {
    ProjectInnovation projectInnovation = new ProjectInnovation();

    projectInnovation.setProject(project);

    projectInnovation = projectInnovationManager.saveProjectInnovation(projectInnovation);

    ProjectInnovationInfo projectInnovationInfo = new ProjectInnovationInfo(projectInnovation, this.getActualPhase(),
      "", "", "", "", "", "", new Long(this.getActualPhase().getYear()));

    projectInnovationInfo = projectInnovationInfoManager.saveProjectInnovationInfo(projectInnovationInfo);

    ProjectInnovationCrp projectInnovationThisCrp = new ProjectInnovationCrp();
    projectInnovationThisCrp.setGlobalUnit(this.getCurrentGlobalUnit());
    projectInnovationThisCrp.setPhase(this.getActualPhase());
    projectInnovationThisCrp.setProjectInnovation(projectInnovation);

    projectInnovationThisCrp = this.projectInnovationCrpManager.saveProjectInnovationCrp(projectInnovationThisCrp);

    innovationID = projectInnovation.getId();

    if (innovationID > 0) {

      return SUCCESS;
    }

    return INPUT;
  }

  @Override
  public String delete() {
    for (ProjectInnovation projectInnovation : projectInnovations) {
      if (projectInnovation.getId().longValue() == this.innovationID) {
        ProjectInnovation projectInnovationBD = projectInnovationManager.getProjectInnovationById(this.innovationID);

        // section_status
        for (SectionStatus sectionStatus : projectInnovationBD.getSectionStatuses()) {
          sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
        }

        projectInnovation.setModificationJustification(justification);

        projectInnovationManager.deleteProjectInnovation(projectInnovationBD.getId());
      }
    }
    return SUCCESS;
  }


  @Override
  public List<Integer> getAllYears() {
    return allYears;
  }

  public long getInnovationID() {
    return innovationID;
  }

  @Override
  public String getJustification() {
    return justification;
  }

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }


  public List<ProjectInnovation> getProjectInnovations() {
    return projectInnovations;
  }

  public List<ProjectInnovation> getProjectOldInnovations() {
    return projectOldInnovations;
  }

  @Override
  public void prepare() throws Exception {
    projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));

    project = projectManager.getProjectById(projectID);

    allYears = project.getProjecInfoPhase(this.getActualPhase()).getAllYears();
    projectOldInnovations = new ArrayList<ProjectInnovation>();
    List<ProjectInnovation> innovations =
      project.getProjectInnovations().stream().filter(c -> c.isActive()).collect(Collectors.toList());

    projectInnovations = new ArrayList<ProjectInnovation>();


    for (ProjectInnovation projectInnovation : innovations) {

      if (projectInnovation.getProjectInnovationInfo(this.getActualPhase()) != null
        && projectInnovation.getProjectInnovationInfo().getYear() >= this.getActualPhase().getYear()) {

        // Geographic Scope List
        if (projectInnovation.getProjectInnovationGeographicScopes() != null) {
          projectInnovation.setGeographicScopes(new ArrayList<>(projectInnovation.getProjectInnovationGeographicScopes()
            .stream().filter(o -> o.isActive() && o.getPhase().getId() == this.getActualPhase().getId())
            .collect(Collectors.toList())));
        }

        projectInnovations.add(projectInnovation);
      } else {
        projectOldInnovations.add(projectInnovation);
      }
    }

    /*
     * Update 4/25/2019 Adding Shared Project Innovation in the lists.
     */
    List<ProjectInnovationShared> innovationShareds = projectInnovationSharedManager.findAll().stream()
      .filter(px -> px.getProject() != null && px.getProject().getId().equals(this.getProjectID()) && px.isActive()
        && px.getPhase().getId().equals(this.getActualPhase().getId()) && px.getProjectInnovation().isActive()
        && px.getProjectInnovation().getProjectInnovationInfo(this.getActualPhase()) != null)
      .collect(Collectors.toList());

    if (innovationShareds != null && innovationShareds.size() > 0) {
      for (ProjectInnovationShared innovationShared : innovationShareds) {
        if (!projectInnovations.contains(innovationShared.getProjectInnovation())
          && !projectOldInnovations.contains(innovationShared.getProjectInnovation())) {
          if (innovationShared.getProjectInnovation().getProjectInnovationInfo(this.getActualPhase()) != null
            && innovationShared.getProjectInnovation().getProjectInnovationInfo().getYear() >= this.getActualPhase()
              .getYear()) {

            // Geographic Scope List
            if (innovationShared.getProjectInnovation().getProjectInnovationGeographicScopes() != null) {
              innovationShared.getProjectInnovation().setGeographicScopes(
                new ArrayList<>(innovationShared.getProjectInnovation().getProjectInnovationGeographicScopes().stream()
                  .filter(o -> o.isActive() && o.getPhase().getId() == this.getActualPhase().getId())
                  .collect(Collectors.toList())));
            }

            projectInnovations.add(innovationShared.getProjectInnovation());
          } else {
            projectOldInnovations.add(innovationShared.getProjectInnovation());
          }
        }
      }
    }


  }

  public void setAllYears(List<Integer> allYears) {
    this.allYears = allYears;
  }

  public void setInnovationID(long innovationID) {
    this.innovationID = innovationID;
  }

  @Override
  public void setJustification(String justification) {
    this.justification = justification;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setProjectInnovations(List<ProjectInnovation> projectInnovations) {
    this.projectInnovations = projectInnovations;
  }

  public void setProjectOldInnovations(List<ProjectInnovation> projectOldInnovations) {
    this.projectOldInnovations = projectOldInnovations;
  }

  private void updateCrpAffiliation() {
    Comparator<Phase> phaseComparator = PhaseComparator.getInstance();

    Map<ProjectInnovation, SortedSet<Phase>> ar2021AndBeyond = this.projectInnovationInfoManager.findAll().stream()
      .filter(pii -> pii != null && pii.getId() != null && pii.getPhase() != null && pii.getPhase().getId() != null
        && pii.getPhase().getCrp() != null && pii.getPhase().getCrp().getId() != null
        && pii.getProjectInnovation() != null && pii.getProjectInnovation().getId() != null)
      .collect(Collectors.groupingBy(ProjectInnovationInfo::getProjectInnovation, Collectors
        .mapping(ProjectInnovationInfo::getPhase, Collectors.toCollection(() -> new TreeSet<Phase>(phaseComparator)))));

    Map<GlobalUnit, Set<ProjectInnovation>> innovationsPerCrp = this.projectInnovationInfoManager.findAll().stream()
      .filter(pii -> pii != null && pii.getId() != null && pii.getPhase() != null && pii.getPhase().getId() != null
        && pii.getPhase().getCrp() != null && pii.getPhase().getCrp().getId() != null
        && pii.getProjectInnovation() != null && pii.getProjectInnovation().getId() != null)
      .collect(Collectors.groupingBy(pii -> ar2021AndBeyond.get(pii.getProjectInnovation()).first().getCrp(),
        Collectors.mapping(ProjectInnovationInfo::getProjectInnovation,
          Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(ProjectInnovation::getId))))));

    List<String> inserts = new ArrayList<>();
    for (Entry<GlobalUnit, Set<ProjectInnovation>> entry : innovationsPerCrp.entrySet()) {
      GlobalUnit crp = entry.getKey();
      Long crpId = crp.getId();
      for (ProjectInnovation innovation : entry.getValue()) {
        Long projectInnovationId = innovation.getId();
        Set<Phase> allPhasesWithRows = ar2021AndBeyond.get(innovation);
        Map<Phase, Set<GlobalUnit>> innovationLinkedCrpsPerPhase = innovation.getProjectInnovationCrps().stream()
          .filter(pic -> pic != null && pic.getId() != null && pic.getGlobalUnit() != null
            && pic.getGlobalUnit().getId() != null && pic.getPhase() != null && pic.getPhase().getId() != null
            && pic.getPhase().getCrp() != null && pic.getPhase().getCrp().getId() != null)
          .collect(Collectors.groupingBy(pic -> pic.getPhase(), () -> new TreeMap<>(phaseComparator),
            Collectors.mapping(ProjectInnovationCrp::getGlobalUnit, Collectors.toSet())));
        for (Phase phase : allPhasesWithRows) {
          Long phaseId = phase.getId();
          if (!innovationLinkedCrpsPerPhase.getOrDefault(phase, Collections.emptySet()).contains(crp)) {
            StringBuilder insert = new StringBuilder(
              "INSERT INTO project_innovation_crps(project_innovation_id, global_unit_id, id_phase) VALUES (");
            insert =
              insert.append(projectInnovationId).append(",").append(crpId).append(",").append(phaseId).append(");");
            inserts.add(insert.toString());
          }
        }
      }
    }

    LOG.info("test");

    /*
     * Path fileSuccess = Paths.get("D:\\misc\\insert-icrps.txt");
     * try {
     * Files.write(fileSuccess, inserts, StandardCharsets.UTF_8);
     * } catch (IOException e) {
     * LOG.error("rip");
     * e.printStackTrace();
     * }
     */
  }

  private void updateLinks() {
    List<ProjectInnovationInfo> innovationInfos = this.projectInnovationManager.findAll().stream()
      .filter(pi -> pi != null && pi.getId() != null).flatMap(pi -> pi.getProjectInnovationInfos().stream())
      .filter(pii -> pii != null && pii.getId() != null && pii.getPhase() != null && pii.getPhase().getId() != null
        && ((pii.getPhase().getYear() == 2021 && pii.getPhase().getName().equalsIgnoreCase("AR"))
          || (pii.getPhase().getYear() > 2021))
        && StringUtils.isNotEmpty(pii.getEvidenceLink()))
      .collect(Collectors.toList());

    LOG.info("infos size is {} and should be 6700; previously it had 6264", innovationInfos.size());

    ComparatorChain<ProjectInnovationInfo> chain = new ComparatorChain<>();
    chain.addComparator((i1, i2) -> i1.getProjectInnovation().getId().compareTo(i2.getProjectInnovation().getId()));
    chain.addComparator((i1, i2) -> i1.getPhase().getId().compareTo(i2.getPhase().getId()));

    Map<ProjectInnovationInfo, List<String>> infoToLinks = new TreeMap<>(chain);
    Map<ProjectInnovationInfo, List<String>> failedInfos = new TreeMap<>(chain);
    for (ProjectInnovationInfo info : innovationInfos) {
      String evidenceLinks = info.getEvidenceLink();
      String[] links = StringUtils.split(StringUtils.trimToEmpty(evidenceLinks), "\n");
      List<String> cleanLinks = new ArrayList<>();
      List<String> failedLinks = new ArrayList<>();
      for (String individualLink : links) {
        String link = StringUtils.trimToEmpty(individualLink);
        cleanLinks.addAll(Arrays.asList(StringUtils.split(link, ";")).stream().map(l -> StringUtils.trimToEmpty(l))
          .collect(Collectors.toList()));
        failedLinks.addAll(cleanLinks.stream()
          .filter(l -> StringUtils.isEmpty(l) || !Patterns.WEB_URL.matcher(l).find()).collect(Collectors.toList()));
        cleanLinks.removeAll(failedLinks);
      }

      infoToLinks.put(info, cleanLinks);
      failedInfos.put(info, failedLinks);
    }

    List<String> inserts = new ArrayList<>();
    List<String> failed = new ArrayList<>();

    for (Entry<ProjectInnovationInfo, List<String>> entry : infoToLinks.entrySet()) {
      if (this.isNotEmpty(entry.getValue())) {
        Long projectInnovationId = entry.getKey().getProjectInnovation().getId();
        Long phaseId = entry.getKey().getPhase().getId();
        for (String link : entry.getValue()) {
          StringBuilder insert = new StringBuilder(
            "INSERT INTO project_innovation_evidence_links(project_innovation_id, link, id_phase) VALUES (");
          insert = insert.append(projectInnovationId).append(",'").append(link.replaceAll("'", "''")).append("',")
            .append(phaseId).append(");");
          inserts.add(insert.toString());
        }
      }
    }

    for (Entry<ProjectInnovationInfo, List<String>> entry : failedInfos.entrySet()) {
      Long projectInnovationId = entry.getKey().getProjectInnovation().getId();
      Long phaseId = entry.getKey().getPhase().getId();
      for (String link : entry.getValue()) {
        failed.add(String.format("project_innovation_info_id = %s; id_phase = %s; evidenceLink = '%s'",
          projectInnovationId, phaseId, link));
      }
    }

    LOG.info("test");

    /*
     * Path fileSuccess = Paths.get("D:\\misc\\insert-iel.txt");
     * Path fileFail = Paths.get("D:\\misc\\fail-iel.txt");
     * try {
     * Files.write(fileSuccess, inserts, StandardCharsets.UTF_8);
     * Files.write(fileFail, failed, StandardCharsets.UTF_8);
     * } catch (IOException e) {
     * LOG.error("rip");
     * e.printStackTrace();
     * }
     */
  }

}
