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
import org.cgiar.ccafs.marlo.data.dao.CrpMilestoneDAO;
import org.cgiar.ccafs.marlo.data.dao.CrpProgramOutcomeDAO;
import org.cgiar.ccafs.marlo.data.dao.CrpProgramOutcomeIndicatorDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectMilestoneDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectNextuserDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectOutcomeDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectOutcomeIndicatorDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectNextuser;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcomeIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectOutcomeManagerImpl implements ProjectOutcomeManager {


  private ProjectOutcomeDAO projectOutcomeDAO;
  // Managers
  private PhaseDAO phaseMySQLDAO;
  private ProjectNextuserDAO projectNextuserDAO;
  private ProjectMilestoneDAO projectMilestoneDAO;
  private ProjectOutcomeIndicatorDAO projectOutcomeIndicatorDAO;

  private CrpProgramOutcomeDAO crpProgramOutcomeDAO;
  private CrpProgramOutcomeIndicatorDAO crpProgramOutcomeIndicatorDAO;

  private CrpMilestoneDAO crpMilestoneDAO;

  @Inject
  public ProjectOutcomeManagerImpl(ProjectOutcomeDAO projectOutcomeDAO, PhaseDAO phaseMySQLDAO,
    ProjectMilestoneDAO projectMilestoneDAO, ProjectNextuserDAO projectNextuserDAO, CrpMilestoneDAO crpMilestoneDAO,
    CrpProgramOutcomeDAO crpProgramOutcomeDAO, ProjectOutcomeIndicatorDAO projectOutcomeIndicatorDAO,
    CrpProgramOutcomeIndicatorDAO crpProgramOutcomeIndicatorDAO) {
    this.projectOutcomeDAO = projectOutcomeDAO;
    this.crpProgramOutcomeDAO = crpProgramOutcomeDAO;
    this.phaseMySQLDAO = phaseMySQLDAO;
    this.projectMilestoneDAO = projectMilestoneDAO;
    this.projectNextuserDAO = projectNextuserDAO;
    this.projectOutcomeIndicatorDAO = projectOutcomeIndicatorDAO;
    this.crpProgramOutcomeIndicatorDAO = crpProgramOutcomeIndicatorDAO;
    this.crpMilestoneDAO = crpMilestoneDAO;
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
        if (projectMilestone != null) {
          ProjectMilestone projectMilestoneAdd = new ProjectMilestone();
          projectMilestoneAdd.setActive(true);
          projectMilestoneAdd.setActiveSince(projectOutcome.getActiveSince());
          projectMilestoneAdd.setCreatedBy(projectOutcome.getCreatedBy());
          projectMilestoneAdd.setModificationJustification("");
          projectMilestoneAdd.setModifiedBy(projectOutcome.getCreatedBy());
          projectMilestoneAdd.setCrpMilestone(crpMilestoneDAO.getCrpMilestone(
            projectMilestone.getCrpMilestone().getComposeID(), projectOutcomeAdd.getCrpProgramOutcome()));

          projectMilestoneAdd.setAchievedValue(projectMilestone.getAchievedValue());
          if (projectMilestone.getExpectedUnit() != null) {
            if (projectMilestone.getExpectedUnit().getId() == null
              || projectMilestone.getExpectedUnit().getId().longValue() == -1) {
              projectMilestoneAdd.setExpectedUnit(null);
            }
          }
          // projectMilestoneAdd.setExpectedUnit(projectMilestone.getExpectedUnit());
          projectMilestoneAdd.setExpectedValue(projectMilestone.getExpectedValue());
          projectMilestoneAdd.setAchievedValue(projectMilestone.getAchievedValue());
          projectMilestoneAdd.setNarrativeAchieved(projectMilestone.getNarrativeAchieved());
          projectMilestoneAdd.setNarrativeTarget(projectMilestone.getNarrativeTarget());
          projectMilestoneAdd.setProjectOutcome(projectOutcomeAdd);
          projectMilestoneAdd.setYear(projectMilestone.getYear());
          if (projectMilestoneAdd.getCrpMilestone() != null) {
            if (projectMilestoneAdd.getCrpMilestone().getYear() >= projectOutcome.getPhase().getYear()) {
              projectMilestoneDAO.save(projectMilestoneAdd);
            }

          }

        }

      }
    }
  }


  /**
   * clone the outcome indicators
   * 
   * @param projectOutcome projectOutcome original
   * @param projectOutcomeAdd projectOutcome new
   */


  private void addProjectIndicators(ProjectOutcome projectOutcome, ProjectOutcome projectOutcomeAdd) {

    if (projectOutcome.getIndicators() != null) {
      for (ProjectOutcomeIndicator projectIndicator : projectOutcome.getIndicators()) {
        projectIndicator.setCrpProgramOutcomeIndicator(
          crpProgramOutcomeIndicatorDAO.find((projectIndicator.getCrpProgramOutcomeIndicator().getId())));
        ProjectOutcomeIndicator projectIndicatorAdd = new ProjectOutcomeIndicator();
        projectIndicatorAdd.setActive(true);
        projectIndicatorAdd.setActiveSince(projectOutcome.getActiveSince());
        projectIndicatorAdd.setCreatedBy(projectOutcome.getCreatedBy());
        projectIndicatorAdd.setModificationJustification("");
        projectIndicatorAdd.setModifiedBy(projectOutcome.getCreatedBy());
        projectIndicatorAdd.setCrpProgramOutcomeIndicator(crpProgramOutcomeIndicatorDAO.getCrpProgramOutcomeIndicator(
          projectIndicator.getCrpProgramOutcomeIndicator().getComposeID(), projectOutcomeAdd.getCrpProgramOutcome()));
        projectIndicatorAdd.setValue(projectIndicator.getValue());
        projectIndicatorAdd.setProjectOutcome(projectOutcomeAdd);
        projectIndicatorAdd.setNarrative(projectIndicator.getNarrative());
        projectOutcomeIndicatorDAO.save(projectIndicatorAdd);

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
        projectNextuserDAO.save(projectNextuserAdd);
        if (projectNextuser.getComposeID() == null) {
          projectNextuser.setComposeID(projectOutcome.getProject().getId() + "-"
            + projectOutcome.getCrpProgramOutcome().getId() + "-" + projectNextuserAdd.getId());
          projectNextuserAdd.setComposeID(projectNextuser.getComposeID());
          projectNextuserDAO.save(projectNextuserAdd);
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
    Phase phase = phaseMySQLDAO.find(next.getId());
    List<ProjectOutcome> projectOutcomes = phase.getProjectOutcomes().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projectID
        && c.getCrpProgramOutcome().getComposeID().equals(projectOutcome.getCrpProgramOutcome().getComposeID()))
      .collect(Collectors.toList());
    if (projectOutcomes.isEmpty()) {
      ProjectOutcome projectOutcomeAdd = new ProjectOutcome();
      projectOutcomeAdd.setActive(true);
      projectOutcomeAdd.setActiveSince(projectOutcome.getActiveSince());
      projectOutcomeAdd.setCreatedBy(projectOutcome.getCreatedBy());
      projectOutcomeAdd.setModificationJustification(projectOutcome.getModificationJustification());
      projectOutcomeAdd.setModifiedBy(projectOutcome.getModifiedBy());
      projectOutcomeAdd.setPhase(phase);
      projectOutcomeAdd.setAchievedUnit(projectOutcome.getAchievedUnit());
      projectOutcomeAdd.setAchievedValue(projectOutcome.getAchievedValue());
      projectOutcomeAdd.setCrpProgramOutcome(
        crpProgramOutcomeDAO.getCrpProgramOutcome(projectOutcome.getCrpProgramOutcome().getComposeID(), next));

      projectOutcomeAdd.setExpectedUnit(projectOutcome.getExpectedUnit());
      projectOutcomeAdd.setExpectedValue(projectOutcome.getExpectedValue());
      projectOutcomeAdd.setGenderDimenssion(projectOutcome.getGenderDimenssion());
      projectOutcomeAdd.setNarrativeAchieved(projectOutcome.getNarrativeAchieved());
      projectOutcomeAdd.setNarrativeTarget(projectOutcome.getNarrativeTarget());
      projectOutcomeAdd.setProject(projectOutcome.getProject());
      projectOutcomeAdd.setYouthComponent(projectOutcome.getYouthComponent());
      if (projectOutcomeAdd.getCrpProgramOutcome() != null) {
        projectOutcomeDAO.save(projectOutcomeAdd);
        this.addMilestones(projectOutcome, projectOutcomeAdd);
        this.addProjectIndicators(projectOutcome, projectOutcomeAdd);
        this.addProjectNextUsers(projectOutcome, projectOutcomeAdd);
      }

    } else {

      for (ProjectOutcome projectOutcomeAdd : projectOutcomes) {
        projectOutcomeAdd.setAchievedValue(projectOutcome.getAchievedValue());
        projectOutcomeAdd.setExpectedUnit(projectOutcome.getExpectedUnit());
        projectOutcomeAdd.setExpectedValue(projectOutcome.getExpectedValue());
        projectOutcomeAdd.setGenderDimenssion(projectOutcome.getGenderDimenssion());
        projectOutcomeAdd.setNarrativeAchieved(projectOutcome.getNarrativeAchieved());
        projectOutcomeAdd.setNarrativeTarget(projectOutcome.getNarrativeTarget());
        projectOutcomeAdd.setYouthComponent(projectOutcome.getYouthComponent());
        projectOutcomeAdd.setActive(projectOutcome.isActive());
        projectOutcomeAdd = projectOutcomeDAO.save(projectOutcomeAdd);
        this.updateProjectMilestones(projectOutcomeAdd, projectOutcome);
        this.updateProjectIndicators(projectOutcomeAdd, projectOutcome);
        this.updateProjectNextUsers(projectOutcomeAdd, projectOutcome);
      }

    }

    if (phase.getNext() != null) {
      this.addProjectOutcomePhase(phase.getNext(), projectID, projectOutcome);

    }


  }

  @Override
  public ProjectOutcome copyProjectOutcome(ProjectOutcome projectOutcome, Phase phase) {

    return this.copyProjectOutcomePhase(phase, projectOutcome.getProject().getId(), projectOutcome);

  }

  private ProjectOutcome copyProjectOutcomePhase(Phase next, long projectID, ProjectOutcome projectOutcome) {
    Phase phase = phaseMySQLDAO.find(next.getId());
    List<ProjectOutcome> projectOutcomes = phase.getProjectOutcomes().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projectID
        && c.getCrpProgramOutcome().getComposeID().equals(projectOutcome.getCrpProgramOutcome().getComposeID()))
      .collect(Collectors.toList());
    if (projectOutcomes.isEmpty()) {
      ProjectOutcome projectOutcomeAdd = new ProjectOutcome();
      projectOutcomeAdd.setActive(true);
      projectOutcomeAdd.setActiveSince(projectOutcome.getActiveSince());
      projectOutcomeAdd.setCreatedBy(projectOutcome.getCreatedBy());
      projectOutcomeAdd.setModificationJustification(projectOutcome.getModificationJustification());
      projectOutcomeAdd.setModifiedBy(projectOutcome.getModifiedBy());
      projectOutcomeAdd.setPhase(phase);
      projectOutcomeAdd.setAchievedUnit(projectOutcome.getAchievedUnit());
      projectOutcomeAdd.setAchievedValue(projectOutcome.getAchievedValue());
      projectOutcomeAdd.setCrpProgramOutcome(
        crpProgramOutcomeDAO.getCrpProgramOutcome(projectOutcome.getCrpProgramOutcome().getComposeID(), next));
      projectOutcomeAdd.setExpectedUnit(projectOutcome.getExpectedUnit());
      projectOutcomeAdd.setExpectedValue(projectOutcome.getExpectedValue());
      projectOutcomeAdd.setGenderDimenssion(projectOutcome.getGenderDimenssion());
      projectOutcomeAdd.setNarrativeAchieved(projectOutcome.getNarrativeAchieved());
      projectOutcomeAdd.setNarrativeTarget(projectOutcome.getNarrativeTarget());
      projectOutcomeAdd.setProject(projectOutcome.getProject());
      projectOutcomeAdd.setYouthComponent(projectOutcome.getYouthComponent());
      if (projectOutcomeAdd.getCrpProgramOutcome() != null) {
        projectOutcomeDAO.save(projectOutcomeAdd);
        this.addMilestones(projectOutcome, projectOutcomeAdd);
        this.addProjectNextUsers(projectOutcome, projectOutcomeAdd);
        return projectOutcomeAdd;
      }

    }

    return null;


  }

  @Override
  public void deleteProjectOutcome(long projectOutcomeId) {

    projectOutcomeDAO.deleteProjectOutcome(projectOutcomeId);
    ProjectOutcome projectOutcome = this.getProjectOutcomeById(projectOutcomeId);
    Phase currentPhase = phaseMySQLDAO.find(projectOutcome.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (projectOutcome.getPhase().getNext() != null) {
        this.deletProjectOutcomePhase(projectOutcome.getPhase().getNext(), projectOutcome.getProject().getId(),
          projectOutcome);
      }
    }

  }

  public void deletProjectOutcomePhase(Phase next, long projecID, ProjectOutcome projectOutcome) {
    Phase phase = phaseMySQLDAO.find(next.getId());

    List<ProjectOutcome> outcomes = phase.getProjectOutcomes().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
        && projectOutcome.getCrpProgramOutcome().getComposeID().equals(c.getCrpProgramOutcome().getComposeID()))
      .collect(Collectors.toList());
    for (ProjectOutcome outcome : outcomes) {
      outcome.setActive(false);
      projectOutcomeDAO.save(outcome);
    }

    if (phase.getNext() != null) {
      this.deletProjectOutcomePhase(phase.getNext(), projecID, projectOutcome);

    }


  }

  @Override
  public boolean existProjectOutcome(long projectOutcomeID) {

    return projectOutcomeDAO.existProjectOutcome(projectOutcomeID);
  }

  @Override
  public List<ProjectOutcome> findAll() {

    return projectOutcomeDAO.findAll();

  }

  @Override
  public ProjectOutcome getProjectOutcomeById(long projectOutcomeID) {

    return projectOutcomeDAO.find(projectOutcomeID);
  }

  @Override
  public ProjectOutcome saveProjectOutcome(ProjectOutcome projectOutcome) {

    ProjectOutcome resultProjectOutcome = projectOutcomeDAO.save(projectOutcome);

    Phase currentPhase = phaseMySQLDAO.find(projectOutcome.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (projectOutcome.getPhase().getNext() != null) {
        this.addProjectOutcomePhase(projectOutcome.getPhase().getNext(), projectOutcome.getProject().getId(),
          projectOutcome);
      }
    }
    return resultProjectOutcome;
  }

  @Override
  public ProjectOutcome saveProjectOutcome(ProjectOutcome projectOutcome, String section, List<String> relationsName,
    Phase phase) {

    return projectOutcomeDAO.save(projectOutcome, section, relationsName, phase);
  }

  /**
   * check the indicators and updated
   * 
   * @param projectOutcomePrev project outcome to update
   * @param projectOutcome project outcome modified
   */
  private void updateProjectIndicators(ProjectOutcome projectOutcomePrev, ProjectOutcome projectOutcome) {
    if (projectOutcome.getIndicators() == null) {
      projectOutcome.setIndicators(new ArrayList<ProjectOutcomeIndicator>());
    }
    for (ProjectOutcomeIndicator projectOutcomeIndicator : projectOutcome.getIndicators()) {
      projectOutcomeIndicator.setCrpProgramOutcomeIndicator(
        crpProgramOutcomeIndicatorDAO.find(projectOutcomeIndicator.getCrpProgramOutcomeIndicator().getId()));
    }
    for (ProjectOutcomeIndicator projectOutcomeIndicator : projectOutcomePrev.getProjectOutcomeIndicators().stream()
      .filter(c -> c.isActive()).collect(Collectors.toList())) {
      if (projectOutcome.getIndicators() == null || projectOutcome.getIndicators().stream()
        .filter(c -> c != null && c.getCrpProgramOutcomeIndicator() != null
          && c.getCrpProgramOutcomeIndicator().getComposeID()
            .equals(projectOutcomeIndicator.getCrpProgramOutcomeIndicator().getComposeID()))
        .collect(Collectors.toList()).isEmpty()) {
        projectOutcomeIndicator.setActive(false);
        projectOutcomeIndicatorDAO.save(projectOutcomeIndicator);
      }
    }
    if (projectOutcome.getIndicators() != null) {
      for (ProjectOutcomeIndicator projectOutcomeIndicator : projectOutcome.getIndicators()) {
        if (projectOutcomeIndicator != null) {
          projectOutcomeIndicator.setCrpProgramOutcomeIndicator(
            crpProgramOutcomeIndicatorDAO.find((projectOutcomeIndicator.getCrpProgramOutcomeIndicator().getId())));
          if (projectOutcomePrev.getProjectOutcomeIndicators().stream()
            .filter(c -> c != null && c.isActive() && c.getCrpProgramOutcomeIndicator() != null
              && projectOutcomeIndicator.getCrpProgramOutcomeIndicator() != null
              && c.getCrpProgramOutcomeIndicator().getComposeID()
                .equals(projectOutcomeIndicator.getCrpProgramOutcomeIndicator().getComposeID()))
            .collect(Collectors.toList()).isEmpty()) {

            ProjectOutcomeIndicator projectOutcomeIndicatorAdd = new ProjectOutcomeIndicator();
            projectOutcomeIndicatorAdd.setActive(true);
            projectOutcomeIndicatorAdd.setActiveSince(projectOutcome.getActiveSince());
            projectOutcomeIndicatorAdd.setCreatedBy(projectOutcome.getCreatedBy());
            projectOutcomeIndicatorAdd.setModificationJustification("");
            projectOutcomeIndicatorAdd.setModifiedBy(projectOutcome.getCreatedBy());
            projectOutcomeIndicatorAdd.setCrpProgramOutcomeIndicator(crpProgramOutcomeIndicatorDAO
              .getCrpProgramOutcomeIndicator(projectOutcomeIndicator.getCrpProgramOutcomeIndicator().getComposeID(),
                this.getProjectOutcomeById(projectOutcomePrev.getId()).getCrpProgramOutcome()));

            projectOutcomeIndicatorAdd.setValue(projectOutcomeIndicator.getValue());
            projectOutcomeIndicatorAdd.setNarrative(projectOutcomeIndicator.getNarrative());
            projectOutcomeIndicatorAdd.setProjectOutcome(projectOutcomePrev);

            projectOutcomeIndicatorDAO.save(projectOutcomeIndicatorAdd);

          } else {
            ProjectOutcomeIndicator indicator = projectOutcomePrev.getProjectOutcomeIndicators().stream()
              .filter(c -> c.isActive() && c.getCrpProgramOutcomeIndicator().getComposeID()
                .equals(projectOutcomeIndicator.getCrpProgramOutcomeIndicator().getComposeID()))
              .collect(Collectors.toList()).get(0);


            indicator.setValue(projectOutcomeIndicator.getValue());
            indicator.setNarrative(projectOutcomeIndicator.getNarrative());


            projectOutcomeIndicatorDAO.save(indicator);

          }
        }

      }
    }
  }

  /**
   * check the milestones and updated
   * 
   * @param projectOutcomePrev project outcome to update
   * @param projectOutcome project outcome modified
   */
  private void updateProjectMilestones(ProjectOutcome projectOutcomePrev, ProjectOutcome projectOutcome) {
    if (projectOutcome.getMilestones() == null) {
      projectOutcome.setMilestones(new ArrayList<ProjectMilestone>());
    }
    for (ProjectMilestone projectMilestone : projectOutcome.getMilestones()) {
      projectMilestone.setCrpMilestone(crpMilestoneDAO.find(projectMilestone.getCrpMilestone().getId()));
    }
    for (ProjectMilestone projectMilestone : projectOutcomePrev.getProjectMilestones().stream()
      .filter(c -> c.isActive()).collect(Collectors.toList())) {
      if (projectOutcome.getMilestones() == null || projectOutcome.getMilestones().stream()
        .filter(c -> c != null && c.getCrpMilestone() != null
          && c.getCrpMilestone().getComposeID().equals(projectMilestone.getCrpMilestone().getComposeID()))
        .collect(Collectors.toList()).isEmpty()) {
        projectMilestone.setActive(false);
        projectMilestoneDAO.save(projectMilestone);
      }
    }
    if (projectOutcome.getMilestones() != null) {
      for (ProjectMilestone projectMilestone : projectOutcome.getMilestones()) {
        if (projectMilestone != null) {
          if (projectOutcomePrev.getProjectMilestones().stream()
            .filter(c -> c != null && c.isActive() && c.getCrpMilestone() != null
              && projectMilestone.getCrpMilestone() != null
              && c.getCrpMilestone().getComposeID().equals(projectMilestone.getCrpMilestone().getComposeID()))
            .collect(Collectors.toList()).isEmpty()) {

            ProjectMilestone projectMilestoneAdd = new ProjectMilestone();
            projectMilestoneAdd.setActive(true);
            projectMilestoneAdd.setActiveSince(projectOutcome.getActiveSince());
            projectMilestoneAdd.setCreatedBy(projectOutcome.getCreatedBy());
            projectMilestoneAdd.setModificationJustification("");
            projectMilestoneAdd.setModifiedBy(projectOutcome.getCreatedBy());
            projectMilestoneAdd
              .setCrpMilestone(crpMilestoneDAO.getCrpMilestone(projectMilestone.getCrpMilestone().getComposeID(),
                this.getProjectOutcomeById(projectOutcomePrev.getId()).getCrpProgramOutcome()));

            projectMilestoneAdd.setAchievedValue(projectMilestone.getAchievedValue());

            if (projectMilestone.getExpectedUnit() != null) {
              if (projectMilestone.getExpectedUnit().getId() == null
                || projectMilestone.getExpectedUnit().getId().longValue() == -1) {
                projectMilestoneAdd.setExpectedUnit(null);
              }
            }

            projectMilestoneAdd.setExpectedValue(projectMilestone.getExpectedValue());
            projectMilestoneAdd.setAchievedValue(projectMilestone.getAchievedValue());
            projectMilestoneAdd.setNarrativeAchieved(projectMilestone.getNarrativeAchieved());
            projectMilestoneAdd.setNarrativeTarget(projectMilestone.getNarrativeTarget());
            projectMilestoneAdd.setProjectOutcome(projectOutcomePrev);
            projectMilestoneAdd.setYear(projectMilestone.getYear());
            projectMilestoneDAO.save(projectMilestoneAdd);

          } else {
            ProjectMilestone milestone = projectOutcomePrev.getProjectMilestones().stream()
              .filter(c -> c.isActive()
                && c.getCrpMilestone().getComposeID().equals(projectMilestone.getCrpMilestone().getComposeID()))
              .collect(Collectors.toList()).get(0);
            milestone.setAchievedValue(projectMilestone.getAchievedValue());
            if (projectMilestone.getExpectedUnit() != null) {
              if (projectMilestone.getExpectedUnit().getId() == null
                || projectMilestone.getExpectedUnit().getId().longValue() == -1) {
                milestone.setExpectedUnit(null);
              }
            }

            milestone.setExpectedValue(projectMilestone.getExpectedValue());
            milestone.setAchievedValue(projectMilestone.getAchievedValue());
            milestone.setNarrativeAchieved(projectMilestone.getNarrativeAchieved());
            milestone.setNarrativeTarget(projectMilestone.getNarrativeTarget());
            milestone.setYear(projectMilestone.getYear());

            projectMilestoneDAO.save(milestone);

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
        .filter(c -> c.getComposeID() != null && c.getComposeID().equals(projectNextuser.getComposeID()))
        .collect(Collectors.toList()).isEmpty()) {
        projectNextuser.setActive(false);
        projectNextuserDAO.save(projectNextuser);
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
          projectNextuserDAO.save(projectNextuserAdd);
          if (projectNextuser.getComposeID() == null) {
            projectNextuser.setComposeID(projectOutcome.getProject().getId() + "-"
              + projectOutcome.getCrpProgramOutcome().getId() + "-" + projectNextuserAdd.getId());
            projectNextuserAdd.setComposeID(projectNextuser.getComposeID());
            projectNextuserDAO.save(projectNextuserAdd);
          }

        } else {
          ProjectNextuser projectNextusertoUpdate = projectOutcomePrev.getProjectNextusers().stream()
            .filter(c -> c.isActive() && c.getComposeID().equals(projectNextuser.getComposeID()))
            .collect(Collectors.toList()).get(0);
          projectNextusertoUpdate.setKnowledge(projectNextuser.getKnowledge());
          projectNextusertoUpdate.setNextUser(projectNextuser.getNextUser());
          projectNextusertoUpdate.setProjectOutcome(projectOutcomePrev);
          projectNextusertoUpdate.setStrategies(projectNextuser.getStrategies());

          projectNextuserDAO.save(projectNextusertoUpdate);
        }
      }
    }
  }


}
