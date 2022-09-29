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
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentableFieldsManager;
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
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCuttingDimensionInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAComment;
import org.cgiar.ccafs.marlo.data.model.FeedbackQACommentableFields;
import org.cgiar.ccafs.marlo.data.model.FeedbackStatusEnum;
import org.cgiar.ccafs.marlo.data.model.Project;
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
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimensionInnovation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressInnovation;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectInnovationListAction extends BaseAction {


  private static final long serialVersionUID = 3586039079035252726L;


  // Manager
  private ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager;
  private ProjectInnovationCenterManager projectInnovationCenterManager;
  private ProjectInnovationContributingOrganizationManager projectInnovationContributingOrganizationManager;
  private ProjectInnovationCountryManager projectInnovationCountryManager;
  private ProjectInnovationCrpManager projectInnovationCrpManager;
  private ProjectInnovationDeliverableManager projectInnovationDeliverableManager;
  private ProjectInnovationGeographicScopeManager projectInnovationGeographicScopeManager;
  private ProjectInnovationInfoManager projectInnovationInfoManager;
  private ProjectInnovationMilestoneManager projectInnovationMilestoneManager;
  private ProjectInnovationOrganizationManager projectInnovationOrganizationManager;
  private ProjectInnovationRegionManager projectInnovationRegionManager;
  private ProjectInnovationSharedManager projectInnovationSharedManager;
  private ProjectInnovationSubIdoManager projectInnovationSubIdoManager;
  private ProjectInnovationManager projectInnovationManager;
  private ProjectPolicyInnovationManager projectPolicyInnovationManager;
  private ReportSynthesisCrossCuttingDimensionInnovationManager reportSynthesisCrossCuttingDimensionInnovationManager;
  private ReportSynthesisFlagshipProgressInnovationManager reportSynthesisFlagshipProgressInnovationManager;
  private FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager;
  private FeedbackQACommentManager commentManager;

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
  public ProjectInnovationListAction(APConfig config, SectionStatusManager sectionStatusManager,
    ProjectManager projectManager, ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager,
    ProjectInnovationCenterManager projectInnovationCenterManager,
    ProjectInnovationContributingOrganizationManager projectInnovationContributingOrganizationManager,
    ProjectInnovationCountryManager projectInnovationCountryManager,
    ProjectInnovationCrpManager projectInnovationCrpManager,
    ProjectInnovationDeliverableManager projectInnovationDeliverableManager,
    ProjectInnovationGeographicScopeManager projectInnovationGeographicScopeManager,
    ProjectInnovationInfoManager projectInnovationInfoManager,
    ProjectInnovationMilestoneManager projectInnovationMilestoneManager,
    ProjectInnovationOrganizationManager projectInnovationOrganizationManager,
    ProjectInnovationRegionManager projectInnovationRegionManager,
    ProjectInnovationSharedManager projectInnovationSharedManager,
    ProjectInnovationSubIdoManager projectInnovationSubIdoManager, ProjectInnovationManager projectInnovationManager,
    ProjectPolicyInnovationManager projectPolicyInnovationManager,
    ReportSynthesisCrossCuttingDimensionInnovationManager reportSynthesisCrossCuttingDimensionInnovationManager,
    ReportSynthesisFlagshipProgressInnovationManager reportSynthesisFlagshipProgressInnovationManager,
    FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager, FeedbackQACommentManager commentManager) {
    super(config);
    this.sectionStatusManager = sectionStatusManager;
    this.projectManager = projectManager;
    this.projectExpectedStudyInnovationManager = projectExpectedStudyInnovationManager;
    this.projectInnovationCenterManager = projectInnovationCenterManager;
    this.projectInnovationContributingOrganizationManager = projectInnovationContributingOrganizationManager;
    this.projectInnovationCountryManager = projectInnovationCountryManager;
    this.projectInnovationCrpManager = projectInnovationCrpManager;
    this.projectInnovationDeliverableManager = projectInnovationDeliverableManager;
    this.projectInnovationGeographicScopeManager = projectInnovationGeographicScopeManager;
    this.projectInnovationInfoManager = projectInnovationInfoManager;
    this.projectInnovationMilestoneManager = projectInnovationMilestoneManager;
    this.projectInnovationOrganizationManager = projectInnovationOrganizationManager;
    this.projectInnovationRegionManager = projectInnovationRegionManager;
    this.projectInnovationSharedManager = projectInnovationSharedManager;
    this.projectInnovationSubIdoManager = projectInnovationSubIdoManager;
    this.projectInnovationManager = projectInnovationManager;
    this.projectPolicyInnovationManager = projectPolicyInnovationManager;
    this.reportSynthesisCrossCuttingDimensionInnovationManager = reportSynthesisCrossCuttingDimensionInnovationManager;
    this.reportSynthesisFlagshipProgressInnovationManager = reportSynthesisFlagshipProgressInnovationManager;
    this.feedbackQACommentableFieldsManager = feedbackQACommentableFieldsManager;
    this.commentManager = commentManager;
  }

  @Override
  public String add() {
    ProjectInnovation projectInnovation = new ProjectInnovation();

    projectInnovation.setProject(project);

    projectInnovation = projectInnovationManager.saveProjectInnovation(projectInnovation);

    ProjectInnovationInfo projectInnovationInfo = new ProjectInnovationInfo(projectInnovation, this.getActualPhase(),
      "", "", "", "", "", "", new Long(this.getActualPhase().getYear()));

    projectInnovationInfoManager.saveProjectInnovationInfo(projectInnovationInfo);

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

        // project_expected_study_innovations
        for (ProjectExpectedStudyInnovation pesi : projectInnovationBD.getProjectExpectedStudyInnovations().stream()
          .filter(i -> i.getPhase().getId().equals(this.getPhaseID())).collect(Collectors.toList())) {
          projectExpectedStudyInnovationManager.deleteProjectExpectedStudyInnovation(pesi.getId());
        }

        // project_innovation_centers
        for (ProjectInnovationCenter pic : projectInnovationBD.getProjectInnovationCenters().stream()
          .filter(i -> i.getPhase().getId().equals(this.getPhaseID())).collect(Collectors.toList())) {
          projectInnovationCenterManager.deleteProjectInnovationCenter(pic.getId());
        }

        // project_innovation_contributing_organizations
        for (ProjectInnovationContributingOrganization pico : projectInnovationBD
          .getProjectInnovationContributingOrganization().stream()
          .filter(i -> i.getPhase().getId().equals(this.getPhaseID())).collect(Collectors.toList())) {
          projectInnovationContributingOrganizationManager
            .deleteProjectInnovationContributingOrganization(pico.getId());
        }

        // project_innovation_countries
        for (ProjectInnovationCountry pic : projectInnovationBD.getProjectInnovationCountries().stream()
          .filter(i -> i.getPhase().getId().equals(this.getPhaseID())).collect(Collectors.toList())) {
          projectInnovationCountryManager.deleteProjectInnovationCountry(pic.getId());
        }

        // project_innovation_crps
        for (ProjectInnovationCrp pic : projectInnovationBD.getProjectInnovationCrps().stream()
          .filter(i -> i.getPhase().getId().equals(this.getPhaseID())).collect(Collectors.toList())) {
          projectInnovationCrpManager.deleteProjectInnovationCrp(pic.getId());
        }

        // project_innovation_deliverables
        for (ProjectInnovationDeliverable pid : projectInnovationBD.getProjectInnovationDeliverables().stream()
          .filter(i -> i.getPhase().getId().equals(this.getPhaseID())).collect(Collectors.toList())) {
          projectInnovationDeliverableManager.deleteProjectInnovationDeliverable(pid.getId());
        }

        // project_innovation_geographic_scopes
        for (ProjectInnovationGeographicScope pigs : projectInnovationBD.getProjectInnovationGeographicScopes().stream()
          .filter(i -> i.getPhase().getId().equals(this.getPhaseID())).collect(Collectors.toList())) {
          projectInnovationGeographicScopeManager.deleteProjectInnovationGeographicScope(pigs.getId());
        }

        // project_innovation_info (remove from this phase onwards)
        for (ProjectInnovationInfo pii : projectInnovationBD.getProjectInnovationInfos().stream()
          .filter(info -> info.getPhase().getId() >= this.getPhaseID()).collect(Collectors.toList())) {
          projectInnovationInfoManager.deleteProjectInnovationInfo(pii.getId());
        }

        // project_innovation_milestones
        for (ProjectInnovationMilestone pim : projectInnovationBD.getProjectInnovationMilestones().stream()
          .filter(i -> i.getPhase().getId().equals(this.getPhaseID())).collect(Collectors.toList())) {
          projectInnovationMilestoneManager.deleteProjectInnovationMilestone(pim.getId());
        }

        // project_innovation_organizations
        for (ProjectInnovationOrganization pio : projectInnovationBD.getProjectInnovationOrganizations().stream()
          .filter(i -> i.getPhase().getId().equals(this.getPhaseID())).collect(Collectors.toList())) {
          projectInnovationOrganizationManager.deleteProjectInnovationOrganization(pio.getId());
        }
        // project_innovation_regions
        for (ProjectInnovationRegion pir : projectInnovationBD.getProjectInnovationRegions().stream()
          .filter(i -> i.getPhase().getId().equals(this.getPhaseID())).collect(Collectors.toList())) {
          projectInnovationRegionManager.deleteProjectInnovationRegion(pir.getId());
        }

        // project_innovation_shared
        for (ProjectInnovationShared pis : projectInnovationBD.getProjectInnovationShareds().stream()
          .filter(i -> i.getPhase().getId().equals(this.getPhaseID())).collect(Collectors.toList())) {
          projectInnovationSharedManager.deleteProjectInnovationShared(pis.getId());
        }

        // project_innovation_sub_idos
        for (ProjectInnovationSubIdo pisi : projectInnovationBD.getProjectInnovationSubIdos().stream()
          .filter(i -> i.getPhase().getId().equals(this.getPhaseID())).collect(Collectors.toList())) {
          projectInnovationSubIdoManager.deleteProjectInnovationSubIdo(pisi.getId());
        }

        // project_policy_innovations (2020: it is now not being used)
        List<ProjectPolicyInnovation> policyInnovations = projectPolicyInnovationManager.findAll().stream()
          .filter(p -> p.getProjectInnovation() != null
            && p.getProjectInnovation().getId().equals(projectInnovationBD.getId())
            && p.getPhase().getId().equals(this.getPhaseID()))
          .collect(Collectors.toList());
        for (ProjectPolicyInnovation ppi : policyInnovations) {
          projectPolicyInnovationManager.deleteProjectPolicyInnovation(ppi.getId());
        }

        // report_synthesis_cross_cutting_dimension_innovations (2020: it is now not being used)
        List<ReportSynthesisCrossCuttingDimensionInnovation> dimensionInnovations =
          reportSynthesisCrossCuttingDimensionInnovationManager.findAll().stream()
            .filter(di -> di.getReportSynthesisCrossCuttingDimension() != null
              && di.getReportSynthesisCrossCuttingDimension().getReportSynthesis() != null
              // we need to remove the phases from here because it is not done in the manager
              && di.getReportSynthesisCrossCuttingDimension().getReportSynthesis().getPhase() != null
              && (di.getReportSynthesisCrossCuttingDimension().getReportSynthesis().getPhase().getId()
                .equals(this.getActualPhase().getId())
                || di.getReportSynthesisCrossCuttingDimension().getReportSynthesis().getPhase().getId() > this
                  .getActualPhase().getId())
              && di.getProjectInnovation() != null
              && di.getProjectInnovation().getId().equals(projectInnovationBD.getId()))
            .collect(Collectors.toList());
        for (ReportSynthesisCrossCuttingDimensionInnovation rsccdi : dimensionInnovations) {
          reportSynthesisCrossCuttingDimensionInnovationManager
            .deleteReportSynthesisCrossCuttingDimensionInnovation(rsccdi.getId());
        }
        // report_synthesis_flagship_progress_innovations
        List<ReportSynthesisFlagshipProgressInnovation> progressInnovations =
          reportSynthesisFlagshipProgressInnovationManager.findAll().stream()
            .filter(pi -> pi.getReportSynthesisFlagshipProgress() != null
              && pi.getReportSynthesisFlagshipProgress().getReportSynthesis() != null
              // we need to remove the phase entries from here because it is not done in the manager
              && pi.getReportSynthesisFlagshipProgress().getReportSynthesis().getPhase() != null
              && (pi.getReportSynthesisFlagshipProgress().getReportSynthesis().getPhase().getId()
                .equals(this.getActualPhase().getId())
                || pi.getReportSynthesisFlagshipProgress().getReportSynthesis().getPhase().getId() > this
                  .getActualPhase().getId())
              && pi.getProjectInnovation() != null
              && pi.getProjectInnovation().getId().equals(projectInnovationBD.getId()))
            .collect(Collectors.toList());
        for (ReportSynthesisFlagshipProgressInnovation rsfpi : progressInnovations) {
          reportSynthesisFlagshipProgressInnovationManager
            .deleteReportSynthesisFlagshipProgressInnovation(rsfpi.getId());
        }

        // section_status
        for (SectionStatus sectionStatus : projectInnovationBD.getSectionStatuses()) {
          sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
        }

        projectInnovation.setModificationJustification(justification);

        // we need to find the first info in order to know if the innovation has to be "deleted"
        ProjectInnovationInfo initialInfo = projectInnovation.getProjectInnovationInfos().stream()
          .sorted((infoOne, infoTwo) -> infoOne.getPhase().getId().compareTo(infoTwo.getPhase().getId())).findFirst()
          // orElse should NEVER happen, I hope...
          .orElse(null);
        // if the innovation was created in this phase, the ACTUAL innovation should be "deleted"
        if (initialInfo != null && this.getPhaseID().equals(initialInfo.getPhase().getId())) {
          projectInnovationManager.deleteProjectInnovation(projectInnovationBD.getId());
        }
      }
    }
    return SUCCESS;
  }


  @Override
  public List<Integer> getAllYears() {
    return allYears;
  }

  public void getCommentStatuses() {

    try {


      List<FeedbackQACommentableFields> commentableFields = new ArrayList<>();

      // get the commentable fields by sectionName
      if (feedbackQACommentableFieldsManager.findAll() != null) {
        commentableFields = feedbackQACommentableFieldsManager.findAll().stream()
          .filter(f -> f != null && f.getSectionName().equals("innovation")).collect(Collectors.toList());
      }
      if (projectInnovations != null && !projectInnovations.isEmpty() && commentableFields != null
        && !commentableFields.isEmpty()) {


        // Set the comment status in each project outcome

        for (ProjectInnovation study : projectInnovations) {
          int answeredComments = 0, totalComments = 0;
          try {


            for (FeedbackQACommentableFields commentableField : commentableFields) {
              if (commentableField != null && commentableField.getId() != null) {

                if (study != null && study.getId() != null && commentableField != null
                  && commentableField.getId() != null) {
                  List<FeedbackQAComment> comments = commentManager.findAll().stream()
                    .filter(f -> f != null && f.getParentId() == study.getId()

                      && (f.getFeedbackStatus() != null && f.getFeedbackStatus().getId() != null && (!f
                        .getFeedbackStatus().getId().equals(Long.parseLong(FeedbackStatusEnum.Dismissed.getStatusId()))
                      // &&
                      // !f.getFeedbackStatus().getId().equals(Long.parseLong(FeedbackStatusEnum.Draft.getStatusId()))
                      ))

                      && f.getField() != null && f.getField().getId().equals(commentableField.getId()))
                    .collect(Collectors.toList());
                  if (comments != null && !comments.isEmpty()) {
                    totalComments += comments.size();
                    comments = comments.stream()
                      .filter(f -> f != null && ((f.getFeedbackStatus() != null && f.getFeedbackStatus().getId()
                        .equals(Long.parseLong(FeedbackStatusEnum.Agreed.getStatusId())))
                        || (f.getFeedbackStatus() != null && f.getReply() != null)))
                      .collect(Collectors.toList());
                    if (comments != null) {
                      answeredComments += comments.size();
                    }
                  }
                }
              }
            }
            study.setCommentStatus(answeredComments + "/" + totalComments);

            if (study.getCommentStatus() == null
              || (study.getCommentStatus() != null && study.getCommentStatus().isEmpty())) {
              study.setCommentStatus(0 + "/" + 0);
            }
          } catch (Exception e) {
            study.setCommentStatus(0 + "/" + 0);

          }
        }

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
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

    if (this.hasSpecificities(this.feedbackModule())) {
      this.getCommentStatuses();
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

}
