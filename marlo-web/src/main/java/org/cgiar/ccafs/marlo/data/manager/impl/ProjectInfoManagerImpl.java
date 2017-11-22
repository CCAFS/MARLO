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
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectInfoDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectPhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectClusterActivityManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLocationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.Phase;
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

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
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
  private DeliverableInfoManager deliverableInfoManager;
  private DeliverableManager deliverableManager;

  @Inject
  public ProjectInfoManagerImpl(ProjectInfoDAO projectInfoDAO, PhaseDAO phaseMySQLDAO, ProjectPhaseDAO projectPhaseDAO,
    ProjectFocusManager projectFocusManager, ProjectClusterActivityManager projectClusterActivityManager,
    ProjectPartnerManager projectPartnerManager, ProjectLocationManager projectLocationManager,
    ProjectOutcomeManager projectOutcomeManager, DeliverableInfoManager deliverableInfoManager,
    DeliverableManager deliverableManager) {
    this.projectInfoDAO = projectInfoDAO;
    this.phaseMySQLDAO = phaseMySQLDAO;
    this.projectPhaseDAO = projectPhaseDAO;
    this.projectClusterActivityManager = projectClusterActivityManager;
    this.projectPartnerManager = projectPartnerManager;
    this.projectLocationManager = projectLocationManager;
    this.projectFocusManager = projectFocusManager;
    this.projectOutcomeManager = projectOutcomeManager;
    this.deliverableInfoManager = deliverableInfoManager;
    this.deliverableManager = deliverableManager;


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

  public void saveInfoPhase(Phase next, long projecID, ProjectInfo projectInfo) {
    Phase phase = phaseMySQLDAO.find(next.getId());

    Calendar cal = Calendar.getInstance();


    if (projectInfo.getEndDate() != null) {
      cal.setTime(projectInfo.getEndDate());
    }

    if (phase.getEditable() != null && phase.getEditable()
      && projectInfo.getPhase().getDescription().equals(APConstants.PLANNING)) {
      List<ProjectInfo> projectInfos = phase.getProjectInfos().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID).collect(Collectors.toList());
      if (!projectInfos.isEmpty()) {
        for (ProjectInfo projectInfoPhase : projectInfos) {
          projectInfoPhase.updateProjectInfo(projectInfo);


          if (cal.get(Calendar.YEAR) < phase.getYear()) {

            projectInfoDAO.deleteProjectInfo(projectInfoPhase.getId());
            List<ProjectPhase> projectPhases = phase.getProjectPhases().stream()
              .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID).collect(Collectors.toList());
            if (!projectPhases.isEmpty()) {
              projectPhaseDAO.deleteProjectPhase(projectPhases.get(0).getId());
            }
            List<ProjectFocus> projectFocus = projectInfoPhase.getProject().getProjectFocuses().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(projectInfoPhase.getPhase()))
              .collect(Collectors.toList());
            for (ProjectFocus projectFocusDB : projectFocus) {
              projectFocusManager.deleteProjectFocus(projectFocusDB.getId());
            }
            List<ProjectClusterActivity> projectClusterActivities =
              projectInfoPhase.getProject().getProjectClusterActivities().stream()
                .filter(c -> c.isActive() && c.getPhase().equals(projectInfoPhase.getPhase()))
                .collect(Collectors.toList());
            for (ProjectClusterActivity clusterActivity : projectClusterActivities) {
              projectClusterActivityManager.deleteProjectClusterActivity(clusterActivity.getId());
            }
            List<ProjectPartner> projectPartners = projectInfoPhase.getProject().getProjectPartners().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(projectInfoPhase.getPhase()))
              .collect(Collectors.toList());
            for (ProjectPartner projectPartner : projectPartners) {
              projectPartnerManager.deleteProjectPartner(projectPartner.getId());
            }
            List<ProjectLocation> locations = projectInfoPhase.getProject().getProjectLocations().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(projectInfoPhase.getPhase()))
              .collect(Collectors.toList());
            for (ProjectLocation projectLocation : locations) {
              projectLocationManager.deleteProjectLocation(projectLocation.getId());
            }
            List<ProjectOutcome> outcomes = projectInfoPhase.getProject().getProjectOutcomes().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(projectInfoPhase.getPhase()))
              .collect(Collectors.toList());
            for (ProjectOutcome outcome : outcomes) {
              projectOutcomeManager.deleteProjectOutcome(outcome.getId());
            }

            List<DeliverableInfo> deliverableInfos = projectInfoPhase.getPhase().getDeliverableInfos().stream()
              .filter(c -> c.getDeliverable().getProject() != null
                && c.getDeliverable().getProject().equals(projectInfoPhase.getProject()) && c.isActive()
                && c.getPhase().equals(projectInfoPhase.getPhase()))
              .collect(Collectors.toList());
            for (DeliverableInfo deliverableInfo : deliverableInfos) {
              deliverableInfoManager.deleteDeliverableInfo(deliverableInfo.getId());
            }
          } else {
            projectInfoDAO.save(projectInfoPhase);
          }

        }
      } else {
        if (projectInfo.getEndDate() != null) {

          if (cal.get(Calendar.YEAR) >= phase.getYear()) {
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

            List<ProjectClusterActivity> projectClusters =
              projectInfo.getProject().getProjectClusterActivities().stream()
                .filter(c -> c.isActive() && c.getPhase().equals(projectInfo.getPhase())).collect(Collectors.toList());
            for (ProjectClusterActivity projectClusterActivity : projectClusters) {
              projectClusterActivityManager.saveProjectClusterActivity(projectClusterActivity);
            }

            List<ProjectPartner> projectPartners = projectInfo.getProject().getProjectPartners().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(projectInfo.getPhase())).collect(Collectors.toList());
            for (ProjectPartner projectPartner : projectPartners) {
              projectPartner.setPartnerPersons(projectPartner.getProjectPartnerPersons().stream()
                .filter(c -> c.isActive()).collect(Collectors.toList()));
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
              projectPartner.setPartnerPersons(projectPartner.getProjectPartnerPersons().stream()
                .filter(c -> c.isActive()).collect(Collectors.toList()));
              projectPartnerManager.copyPartner(projectPartner, phase);
            }
            List<ProjectLocation> projectLocations = projectInfo.getProject().getProjectLocations().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(projectInfo.getPhase())).collect(Collectors.toList());
            for (ProjectLocation projectLocation : projectLocations) {
              projectLocationManager.copyProjectLocation(projectLocation, phase);
            }
            Phase phaseDb = phaseMySQLDAO.find(projectInfo.getPhase().getId());
            List<DeliverableInfo> deliverableInfos = phaseDb.getDeliverableInfos().stream()
              .filter(c -> c.getDeliverable().getProject() != null
                && c.getDeliverable().getProject().equals(projectInfo.getProject()) && c.isActive()
                && c.getPhase().equals(projectInfo.getPhase()))
              .collect(Collectors.toList());
            for (DeliverableInfo deliverableInfo : deliverableInfos) {
              deliverableInfo.getDeliverable().setDeliverableInfo(deliverableInfo);
              deliverableManager.copyDeliverable(deliverableInfo.getDeliverable(), phase);
            }
            List<ProjectOutcome> outcomes = projectInfo.getProject().getProjectOutcomes().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(projectInfo.getPhase())).collect(Collectors.toList());
            for (ProjectOutcome projectOutcome : outcomes) {
              projectOutcome.setMilestones(
                projectOutcome.getProjectMilestones().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

              projectOutcome.setCommunications(projectOutcome.getProjectCommunications().stream()
                .filter(c -> c.isActive()).collect(Collectors.toList()));
              projectOutcome.setNextUsers(
                projectOutcome.getProjectNextusers().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

              projectOutcomeManager.copyProjectOutcome(projectOutcome, phase);
            }
          }
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
    return resultProjectInfo;
  }


}
