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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ProjectOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectNextuser;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class ProjectOutcomeMySQLDAO implements ProjectOutcomeDAO {

  private StandardDAO dao;

  @Inject
  public ProjectOutcomeMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  /**
   * clone the project milestones
   * 
   * @param projectOutcome projectOutcome original
   * @param projectOutcomeAdd projectOutcome new
   */

  private void addMilestones(ProjectOutcome projectOutcome, ProjectOutcome projectOutcomeAdd) {

    if (projectOutcome.getMilestones() != null) {
      for (ProjectMilestone projectMilestone : projectOutcome.getMilestones()) {
        ProjectMilestone projectMilestoneAdd = new ProjectMilestone();
        projectMilestoneAdd.setActive(true);
        projectMilestoneAdd.setActiveSince(projectOutcome.getActiveSince());
        projectMilestoneAdd.setCreatedBy(projectOutcome.getCreatedBy());
        projectMilestoneAdd.setModificationJustification("");
        projectMilestoneAdd.setModifiedBy(projectOutcome.getCreatedBy());
        projectMilestoneAdd.setCrpMilestone(projectMilestone.getCrpMilestone());
        projectMilestoneAdd.setAchievedValue(projectMilestone.getAchievedValue());
        projectMilestoneAdd.setExpectedUnit(projectMilestone.getExpectedUnit());
        projectMilestoneAdd.setExpectedValue(projectMilestone.getExpectedValue());
        projectMilestoneAdd.setAchievedValue(projectMilestone.getAchievedValue());
        projectMilestoneAdd.setNarrativeAchieved(projectMilestone.getNarrativeAchieved());
        projectMilestoneAdd.setNarrativeTarget(projectMilestone.getNarrativeTarget());
        projectMilestoneAdd.setProjectOutcome(projectOutcomeAdd);
        projectMilestoneAdd.setYear(projectMilestone.getYear());
        dao.save(projectMilestoneAdd);
      }
    }
  }

  /**
   * clone the next users
   * 
   * @param projectOutcome projectOutcome original
   * @param projectOutcomeAdd projectOutcome new
   */


  private void addProjectNextUsers(ProjectOutcome projectOutcome, ProjectOutcome projectOutcomeAdd) {

    if (projectOutcome.getNextUsers() != null) {
      for (ProjectNextuser projectNextuser : projectOutcome.getNextUsers()) {
        ProjectNextuser projectNextuserAdd = new ProjectNextuser();
        projectNextuserAdd.setActive(true);
        projectNextuserAdd.setActiveSince(projectOutcome.getActiveSince());
        projectNextuserAdd.setCreatedBy(projectOutcome.getCreatedBy());
        projectNextuserAdd.setModificationJustification("");
        projectNextuserAdd.setModifiedBy(projectOutcome.getCreatedBy());
        projectNextuserAdd.setKnowledge(projectNextuser.getKnowledge());
        projectNextuserAdd.setNextUser(projectNextuser.getNextUser());
        projectNextuserAdd.setProjectOutcome(projectOutcomeAdd);
        projectNextuserAdd.setStrategies(projectNextuser.getStrategies());
        dao.save(projectNextuserAdd);
        if (projectNextuser.getComposeID() == null) {
          projectNextuser.setComposeID(projectOutcome.getProject().getId() + "-"
            + projectOutcome.getCrpProgramOutcome().getId() + "-" + projectNextuserAdd.getId());
          projectNextuserAdd.setComposeID(projectNextuser.getComposeID());
          dao.update(projectNextuserAdd);
        }
      }
    }
  }

  /**
   * clone or update the project outcome for next phases
   * 
   * @param next the next phase to clone
   * @param projectID the project id we are working
   * @param projectOutcome the projectOutcome to clone
   */
  private void addProjectOutcomePhase(Phase next, long projectID, ProjectOutcome projectOutcome) {
    Phase phase = dao.find(Phase.class, next.getId());
    List<ProjectOutcome> projectOutcomes = phase.getProjectOutcomes().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projectID
        && c.getCrpProgramOutcome().getId().equals(projectOutcome.getCrpProgramOutcome().getId()))
      .collect(Collectors.toList());
    if (phase.getEditable() != null && phase.getEditable() && projectOutcomes.isEmpty()) {
      ProjectOutcome projectOutcomeAdd = new ProjectOutcome();
      projectOutcomeAdd.setActive(true);
      projectOutcomeAdd.setActiveSince(projectOutcome.getActiveSince());
      projectOutcomeAdd.setCreatedBy(projectOutcome.getCreatedBy());
      projectOutcomeAdd.setModificationJustification(projectOutcome.getModificationJustification());
      projectOutcomeAdd.setModifiedBy(projectOutcome.getModifiedBy());
      projectOutcomeAdd.setPhase(phase);
      projectOutcomeAdd.setAchievedUnit(projectOutcome.getAchievedUnit());
      projectOutcomeAdd.setAchievedValue(projectOutcome.getAchievedValue());
      projectOutcomeAdd.setCrpProgramOutcome(projectOutcome.getCrpProgramOutcome());
      projectOutcomeAdd.setExpectedUnit(projectOutcome.getExpectedUnit());
      projectOutcomeAdd.setExpectedValue(projectOutcome.getExpectedValue());
      projectOutcomeAdd.setGenderDimenssion(projectOutcome.getGenderDimenssion());
      projectOutcomeAdd.setNarrativeAchieved(projectOutcome.getNarrativeAchieved());
      projectOutcomeAdd.setNarrativeTarget(projectOutcome.getNarrativeTarget());
      projectOutcomeAdd.setProject(projectOutcome.getProject());
      projectOutcomeAdd.setYouthComponent(projectOutcome.getYouthComponent());
      dao.save(projectOutcomeAdd);
      this.addMilestones(projectOutcome, projectOutcomeAdd);
      this.addProjectNextUsers(projectOutcome, projectOutcomeAdd);
    } else {
      if (phase.getEditable() != null && phase.getEditable()) {
        for (ProjectOutcome projectOutcomeAdd : projectOutcomes) {
          projectOutcomeAdd.setAchievedValue(projectOutcome.getAchievedValue());
          projectOutcomeAdd.setExpectedUnit(projectOutcome.getExpectedUnit());
          projectOutcomeAdd.setExpectedValue(projectOutcome.getExpectedValue());
          projectOutcomeAdd.setGenderDimenssion(projectOutcome.getGenderDimenssion());
          projectOutcomeAdd.setNarrativeAchieved(projectOutcome.getNarrativeAchieved());
          projectOutcomeAdd.setNarrativeTarget(projectOutcome.getNarrativeTarget());
          projectOutcomeAdd.setYouthComponent(projectOutcome.getYouthComponent());
          dao.update(projectOutcomeAdd);
          this.updateProjectMilestones(projectOutcomeAdd, projectOutcome);
          this.updateProjectNextUsers(projectOutcomeAdd, projectOutcome);
        }
      }
    }

    if (phase.getNext() != null) {
      this.addProjectOutcomePhase(phase.getNext(), projectID, projectOutcome);

    }


  }

  @Override
  public boolean deleteProjectOutcome(long projectOutcomeId) {
    ProjectOutcome projectOutcome = this.find(projectOutcomeId);
    projectOutcome.setActive(false);
    return this.save(projectOutcome) > 0;
  }

  @Override
  public boolean existProjectOutcome(long projectOutcomeID) {
    ProjectOutcome projectOutcome = this.find(projectOutcomeID);
    if (projectOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectOutcome find(long id) {
    return dao.find(ProjectOutcome.class, id);

  }

  @Override
  public List<ProjectOutcome> findAll() {
    String query = "from " + ProjectOutcome.class.getName() + " where is_active=1";
    List<ProjectOutcome> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectOutcome projectOutcome) {
    if (projectOutcome.getId() == null) {
      dao.save(projectOutcome);
    } else {
      dao.update(projectOutcome);
    }

    if (projectOutcome.getPhase().getNext() != null) {
      this.addProjectOutcomePhase(projectOutcome.getPhase().getNext(), projectOutcome.getProject().getId(),
        projectOutcome);
    }
    return projectOutcome.getId();
  }

  @Override
  public long save(ProjectOutcome projectOutcome, String section, List<String> relationsName, Phase phase) {
    if (projectOutcome.getId() == null) {
      dao.save(projectOutcome, section, relationsName, phase);
    } else {
      dao.update(projectOutcome, section, relationsName, phase);
    }


    return projectOutcome.getId();
  }


  /**
   * check the milestones and updated
   * 
   * @param projectOutcomePrev project outcome to update
   * @param projectOutcome project outcome modified
   */
  private void updateProjectMilestones(ProjectOutcome projectOutcomePrev, ProjectOutcome projectOutcome) {
    for (ProjectMilestone projectMilestone : projectOutcomePrev.getProjectMilestones().stream()
      .filter(c -> c.isActive()).collect(Collectors.toList())) {
      if (projectOutcome.getMilestones() == null || projectOutcome.getMilestones().stream()
        .filter(c -> c != null && c.getCrpMilestone() != null
          && c.getCrpMilestone().getId().equals(projectMilestone.getCrpMilestone().getId()))
        .collect(Collectors.toList()).isEmpty()) {
        projectMilestone.setActive(false);
        dao.update(projectMilestone);
      }
    }
    if (projectOutcome.getMilestones() != null) {
      for (ProjectMilestone projectMilestone : projectOutcome.getMilestones()) {
        if (projectMilestone != null) {
          if (projectOutcomePrev.getProjectMilestones().stream()
            .filter(c -> c != null && c.isActive() && c.getCrpMilestone() != null
              && projectMilestone.getCrpMilestone() != null
              && c.getCrpMilestone().equals(projectMilestone.getCrpMilestone()))
            .collect(Collectors.toList()).isEmpty()) {

            ProjectMilestone projectMilestoneAdd = new ProjectMilestone();
            projectMilestoneAdd.setActive(true);
            projectMilestoneAdd.setActiveSince(projectOutcome.getActiveSince());
            projectMilestoneAdd.setCreatedBy(projectOutcome.getCreatedBy());
            projectMilestoneAdd.setModificationJustification("");
            projectMilestoneAdd.setModifiedBy(projectOutcome.getCreatedBy());
            projectMilestoneAdd.setCrpMilestone(projectMilestone.getCrpMilestone());
            projectMilestoneAdd.setAchievedValue(projectMilestone.getAchievedValue());
            projectMilestoneAdd.setExpectedUnit(projectMilestone.getExpectedUnit());
            projectMilestoneAdd.setExpectedValue(projectMilestone.getExpectedValue());
            projectMilestoneAdd.setAchievedValue(projectMilestone.getAchievedValue());
            projectMilestoneAdd.setNarrativeAchieved(projectMilestone.getNarrativeAchieved());
            projectMilestoneAdd.setNarrativeTarget(projectMilestone.getNarrativeTarget());
            projectMilestoneAdd.setProjectOutcome(projectOutcomePrev);
            projectMilestoneAdd.setYear(projectMilestone.getYear());
            dao.update(projectMilestoneAdd);

          } else {
            ProjectMilestone milestone = projectOutcomePrev.getProjectMilestones().stream()
              .filter(c -> c.isActive() && c.getCrpMilestone().equals(projectMilestone.getCrpMilestone()))
              .collect(Collectors.toList()).get(0);
            milestone.setAchievedValue(projectMilestone.getAchievedValue());
            milestone.setExpectedUnit(projectMilestone.getExpectedUnit());
            milestone.setExpectedValue(projectMilestone.getExpectedValue());
            milestone.setAchievedValue(projectMilestone.getAchievedValue());
            milestone.setNarrativeAchieved(projectMilestone.getNarrativeAchieved());
            milestone.setNarrativeTarget(projectMilestone.getNarrativeTarget());
            milestone.setYear(projectMilestone.getYear());
            dao.update(milestone);

          }
        }

      }
    }
  }

  /**
   * check the project next users and updated
   * 
   * @param projectOutcomePrev project outcome to update
   * @param ProjectOutcome project outcome modified
   */
  private void updateProjectNextUsers(ProjectOutcome projectOutcomePrev, ProjectOutcome projectOutcome) {
    for (ProjectNextuser projectNextuser : projectOutcomePrev.getProjectNextusers().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {
      if (projectOutcome.getNextUsers() == null || projectOutcome.getNextUsers().stream()
        .filter(c -> c.getComposeID().equals(projectNextuser.getComposeID())).collect(Collectors.toList()).isEmpty()) {
        projectNextuser.setActive(false);
        dao.update(projectNextuser);
      }
    }
    if (projectOutcome.getNextUsers() != null) {
      for (ProjectNextuser projectNextuser : projectOutcome.getNextUsers()) {
        if (projectOutcomePrev.getProjectNextusers().stream()
          .filter(c -> c.isActive() && c.getComposeID().equals(projectNextuser.getComposeID()))
          .collect(Collectors.toList()).isEmpty()) {

          ProjectNextuser projectNextuserAdd = new ProjectNextuser();

          projectNextuserAdd.setActive(true);
          projectNextuserAdd.setActiveSince(projectOutcome.getActiveSince());
          projectNextuserAdd.setCreatedBy(projectOutcome.getCreatedBy());
          projectNextuserAdd.setModificationJustification("");
          projectNextuserAdd.setModifiedBy(projectOutcome.getCreatedBy());
          projectNextuserAdd.setKnowledge(projectNextuser.getKnowledge());
          projectNextuserAdd.setNextUser(projectNextuser.getNextUser());
          projectNextuserAdd.setProjectOutcome(projectOutcomePrev);
          projectNextuserAdd.setStrategies(projectNextuser.getStrategies());
          dao.save(projectNextuserAdd);
          if (projectNextuser.getComposeID() == null) {
            projectNextuser.setComposeID(projectOutcome.getProject().getId() + "-"
              + projectOutcome.getCrpProgramOutcome().getId() + "-" + projectNextuserAdd.getId());
            projectNextuserAdd.setComposeID(projectNextuser.getComposeID());
            dao.update(projectNextuserAdd);
          }

        } else {
          ProjectNextuser projectNextusertoUpdate = projectOutcomePrev.getProjectNextusers().stream()
            .filter(c -> c.isActive() && c.getComposeID().equals(projectNextuser.getComposeID()))
            .collect(Collectors.toList()).get(0);
          projectNextusertoUpdate.setKnowledge(projectNextuser.getKnowledge());
          projectNextusertoUpdate.setNextUser(projectNextuser.getNextUser());
          projectNextusertoUpdate.setProjectOutcome(projectOutcomePrev);
          projectNextusertoUpdate.setStrategies(projectNextuser.getStrategies());
          dao.update(projectNextusertoUpdate);
        }
      }
    }
  }


}