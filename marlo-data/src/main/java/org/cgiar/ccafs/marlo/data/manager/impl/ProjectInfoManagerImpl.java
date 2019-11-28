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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.DeliverableDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectInfoDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectPhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.ActivityManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableFundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectClusterActivityManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLocationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnership;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectInfoManagerImpl implements ProjectInfoManager {


  private ProjectInfoDAO projectInfoDAO;
  // Managers
  private PhaseDAO phaseMySQLDAO;
  private ProjectPhaseDAO projectPhaseDAO;
  private ProjectFocusManager projectFocusManager;
  private ProjectPartnerManager projectPartnerManager;
  private ProjectLocationManager projectLocationManager;
  private ProjectClusterActivityManager projectClusterActivityManager;
  private ProjectOutcomeManager projectOutcomeManager;
  private DeliverableManager deliverableManager;
  private DeliverableUserPartnershipManager deliverableUserPartnershipManager;
  private DeliverableFundingSourceManager deliverableFundingSourceManager;
  private DeliverableDAO deliverableDAO;
  private ActivityManager activityManager;
  private ProjectBudgetManager projectBudgetManager;


  @Inject
  public ProjectInfoManagerImpl(ProjectInfoDAO projectInfoDAO, PhaseDAO phaseMySQLDAO, ProjectPhaseDAO projectPhaseDAO,
    ProjectFocusManager projectFocusManager, ProjectClusterActivityManager projectClusterActivityManager,
    ProjectPartnerManager projectPartnerManager, ProjectLocationManager projectLocationManager,
    ProjectOutcomeManager projectOutcomeManager, DeliverableUserPartnershipManager deliverableUserPartnershipManager,
    DeliverableFundingSourceManager deliverableFundingSourceManager, DeliverableDAO deliverableDAO,
    DeliverableManager deliverableManager, ActivityManager activityManager, ProjectBudgetManager projectBudgetManager) {
    this.projectInfoDAO = projectInfoDAO;
    this.phaseMySQLDAO = phaseMySQLDAO;
    this.projectPhaseDAO = projectPhaseDAO;
    this.projectClusterActivityManager = projectClusterActivityManager;
    this.projectPartnerManager = projectPartnerManager;
    this.projectLocationManager = projectLocationManager;
    this.projectFocusManager = projectFocusManager;
    this.projectOutcomeManager = projectOutcomeManager;
    this.deliverableManager = deliverableManager;
    this.deliverableDAO = deliverableDAO;
    this.deliverableFundingSourceManager = deliverableFundingSourceManager;
    this.deliverableUserPartnershipManager = deliverableUserPartnershipManager;
    this.activityManager = activityManager;
    this.projectBudgetManager = projectBudgetManager;


  }

  @Override
  public void deleteProjectInfo(long projectInfoId) {

    projectInfoDAO.deleteProjectInfo(projectInfoId);
  }

  @Override
  public boolean existProjectInfo(long projectInfoID) {

    return projectInfoDAO.existProjectInfo(projectInfoID);
  }

  @Override
  public List<ProjectInfo> findAll() {

    return projectInfoDAO.findAll();

  }

  @Override
  public ProjectInfo getProjectInfoById(long projectInfoID) {

    return projectInfoDAO.find(projectInfoID);
  }

  @Override
  public ProjectInfo getProjectInfoByProjectPhase(long projectId, long phase) {
    return projectInfoDAO.getProjectInfoByProjectPhase(projectId, phase);
  }

  public List<DeliverableUserPartnership> otherPartners(Deliverable deliverable, Phase phase) {
    try {
      List<DeliverableUserPartnership> list = deliverable.getDeliverableUserPartnerships().stream()
        .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(phase.getId())
          && dp.getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_OTHER))
        .collect(Collectors.toList());


      return list;
    } catch (Exception e) {
      return null;
    }


  }

  private List<DeliverableUserPartnership> responsiblePartner(Deliverable deliverable, Phase phase) {
    try {
      List<DeliverableUserPartnership> partnership = deliverable.getDeliverableUserPartnerships().stream()
        .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(phase.getId())
          && dp.getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
        .collect(Collectors.toList());
      return partnership;
    } catch (Exception e) {
      return null;
    }
  }

  public void saveInfoPhase(Phase next, long projecID, ProjectInfo projectInfo) {
    Phase phase = phaseMySQLDAO.find(next.getId());

    Calendar cal = Calendar.getInstance();


    if (projectInfo.getEndDate() != null) {
      cal.setTime(projectInfo.getEndDate());
    }

    List<ProjectInfo> projectInfos = phase.getProjectInfos().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID).collect(Collectors.toList());
    if (!projectInfos.isEmpty()) {
      for (ProjectInfo projectInfoPhase : projectInfos) {
        projectInfoPhase.updateProjectInfo(projectInfo);
        projectInfoDAO.save(projectInfoPhase);
      }
    } else {
      if (projectInfo.getEndDate() != null) {


        ProjectInfo projectInfoPhaseAdd = new ProjectInfo();
        projectInfoPhaseAdd.setProject(projectInfo.getProject());
        projectInfoPhaseAdd.setPhase(phase);
        // projectInfoPhaseAdd.setProjectEditLeader(false);
        projectInfoPhaseAdd.setProjectEditLeader(projectInfo.getProjectEditLeader());

        projectInfoPhaseAdd.updateProjectInfo(projectInfo);
        projectInfoDAO.save(projectInfoPhaseAdd);
        ProjectPhase projectPhase = new ProjectPhase();
        projectPhase.setPhase(phase);
        projectPhase.setProject(projectInfo.getProject());
        projectPhaseDAO.save(projectPhase);
        List<ProjectFocus> projectFocus = projectInfo.getProject().getProjectFocuses().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(projectInfo.getPhase())).collect(Collectors.toList());
        for (ProjectFocus projectFocusDB : projectFocus) {
          projectFocusManager.saveProjectFocus(projectFocusDB);
        }

        List<ProjectClusterActivity> projectClusters = projectInfo.getProject().getProjectClusterActivities().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(projectInfo.getPhase())).collect(Collectors.toList());
        for (ProjectClusterActivity projectClusterActivity : projectClusters) {
          projectClusterActivityManager.saveProjectClusterActivity(projectClusterActivity);
        }

        List<ProjectPartner> projectPartners = projectInfo.getProject().getProjectPartners().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(projectInfo.getPhase())).collect(Collectors.toList());
        for (ProjectPartner projectPartner : projectPartners) {
          projectPartner.setPartnerPersons(
            projectPartner.getProjectPartnerPersons().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
          List<ProjectPartnerContribution> contributors = new ArrayList<>();


          List<ProjectPartnerContribution> partnerContributions = projectPartner.getProjectPartnerContributions()
            .stream().filter(c -> c.isActive()).collect(Collectors.toList());
          for (ProjectPartnerContribution projectPartnerContribution : partnerContributions) {
            contributors.add(projectPartnerContribution);
          }
          projectPartner.setSelectedLocations(new ArrayList<>());
          for (ProjectPartnerLocation projectPartnerLocation : projectPartner.getProjectPartnerLocations().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList())) {
            projectPartner.getSelectedLocations().add(projectPartnerLocation.getInstitutionLocation());
          }
          projectPartner.setPartnerContributors(contributors);
          projectPartner.setPartnerPersons(
            projectPartner.getProjectPartnerPersons().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
          projectPartnerManager.copyPartner(projectPartner, phase);
        }
        List<ProjectLocation> projectLocations = projectInfo.getProject().getProjectLocations().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(projectInfo.getPhase())).collect(Collectors.toList());
        for (ProjectLocation projectLocation : projectLocations) {
          projectLocationManager.copyProjectLocation(projectLocation, phase);
        }
        List<Activity> activities = projectInfo.getProject().getActivities().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(projectInfo.getPhase())).collect(Collectors.toList());
        for (Activity activity : activities) {
          activity.setDeliverables(
            activity.getDeliverableActivities().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
          activityManager.copyActivity(activity, phase);
        }
        List<ProjectBudget> budgets = projectInfo.getProject().getProjectBudgets().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(projectInfo.getPhase())).collect(Collectors.toList());
        for (ProjectBudget projectBudget : budgets) {
          projectBudgetManager.copyProjectBudget(projectBudget, phase);
        }

        Phase phaseDb = phaseMySQLDAO.find(projectInfo.getPhase().getId());
        List<Deliverable> deliverableInfos =
          projectInfo.getProject().getDeliverables().stream().filter(c -> c.isActive()).collect(Collectors.toList());
        for (Deliverable deliverableDB : deliverableInfos) {
          DeliverableInfo deliverableInfo = deliverableDB.getDeliverableInfo(phaseDb);

          if (deliverableInfo != null) {
            Deliverable deliverable = new Deliverable();
            deliverable = deliverableDAO.find(deliverableDB.getId());
            deliverableInfo.setDeliverable(deliverable);
            deliverable.getDeliverableInfo(projectInfo.getPhase());
            deliverable.setResponsiblePartnership(this.responsiblePartner(deliverable, phaseDb));
            deliverable.setOtherPartnerships(this.otherPartners(deliverable, phaseDb));
            deliverableInfo.getDeliverable().setDeliverableInfo(deliverableInfo);

            deliverableManager.copyDeliverable(deliverableInfo.getDeliverable(), phase);

            if (deliverable.getResponsiblePartnership() != null) {
              // dperez validate list if has data. 2019-11-19
              if (deliverable.getResponsiblePartnership().size() > 0) {
                deliverableUserPartnershipManager
                  .copyDeliverableUserPartnership(deliverable.getResponsiblePartnership().get(0), phase);
              }

            }


            for (DeliverableUserPartnership deliverablePartnership : deliverable.getOtherPartnerships()) {
              deliverableUserPartnershipManager.copyDeliverableUserPartnership(deliverablePartnership, phase);
            }
            deliverable.setFundingSources(deliverable
              .getDeliverableFundingSources().stream().filter(c -> c.isActive() && c.getPhase() != null
                && c.getPhase().equals(phaseDb) && c.getFundingSource().getFundingSourceInfo(phaseDb) != null)
              .collect(Collectors.toList()));

            for (DeliverableFundingSource deliverableFundingSource : deliverable.getFundingSources()) {
              deliverableFundingSourceManager.copyDeliverableFundingSource(deliverableFundingSource, phase);

            }
          }


        }
        List<ProjectOutcome> outcomes = projectInfo.getProject().getProjectOutcomes().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(projectInfo.getPhase())).collect(Collectors.toList());
        for (ProjectOutcome projectOutcome : outcomes) {
          projectOutcome.setMilestones(
            projectOutcome.getProjectMilestones().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

          projectOutcome.setCommunications(
            projectOutcome.getProjectCommunications().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
          projectOutcome.setNextUsers(
            projectOutcome.getProjectNextusers().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

          projectOutcomeManager.copyProjectOutcome(projectOutcome, phase);
        }
      }
    }

    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), projecID, projectInfo);
    }
  }

  @Override
  public ProjectInfo saveProjectInfo(ProjectInfo projectInfo) {

    ProjectInfo resultProjectInfo = projectInfoDAO.save(projectInfo);
    if (projectInfo.getPhase().getDescription().equals(APConstants.PLANNING)) {
      if (projectInfo.getPhase().getNext() != null) {
        this.saveInfoPhase(projectInfo.getPhase().getNext(), projectInfo.getProject().getId(), projectInfo);
      }
    }
    // Uncomment this line to allow reporting replication to upkeep
    // if (projectInfo.getPhase().getDescription().equals(APConstants.REPORTING)) {
    // if (projectInfo.getPhase().getNext() != null && projectInfo.getPhase().getNext().getNext() != null) {
    // Phase upkeepPhase = projectInfo.getPhase().getNext().getNext();
    // if (upkeepPhase != null) {
    // this.saveInfoPhase(upkeepPhase, projectInfo.getProject().getId(), projectInfo);
    // }
    // }
    // }
    return resultProjectInfo;
  }


}
