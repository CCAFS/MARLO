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
import org.cgiar.ccafs.marlo.data.dao.ExpectedStudyProjectDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectExpectedStudyManagerImpl implements ProjectExpectedStudyManager {

  private PhaseDAO phaseDAO;
  private ProjectDAO projectDAO;
  private ExpectedStudyProjectDAO expectedStudyProjectDAO;

  private ProjectExpectedStudyDAO projectExpectedStudyDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyManagerImpl(ProjectExpectedStudyDAO projectExpectedStudyDAO, PhaseDAO phaseDAO,
    ExpectedStudyProjectDAO expectedStudyProjectDAO, ProjectDAO projectDAO) {
    this.projectExpectedStudyDAO = projectExpectedStudyDAO;
    this.phaseDAO = phaseDAO;
    this.projectDAO = projectDAO;
    this.expectedStudyProjectDAO = expectedStudyProjectDAO;
  }

  public void cloneExpectedStudyProject(ExpectedStudyProject expectedStudyProjectAdd,
    ExpectedStudyProject expectedStudyProject, ProjectExpectedStudy projectExpectedStudy,
    ProjectExpectedStudy projectExpectedStudyAdd, Phase phase) {
    expectedStudyProjectAdd.setActive(true);
    expectedStudyProjectAdd.setActiveSince(projectExpectedStudy.getActiveSince());
    expectedStudyProjectAdd.setProjectExpectedStudy(projectExpectedStudyAdd);
    expectedStudyProjectAdd.setCreatedBy(projectExpectedStudy.getCreatedBy());
    expectedStudyProjectAdd.setMyProject(expectedStudyProject.getMyProject());
    expectedStudyProjectAdd.setModificationJustification(projectExpectedStudy.getModificationJustification());
    expectedStudyProjectAdd.setModifiedBy(projectExpectedStudy.getModifiedBy());

  }

  /**
   * clone the activity info
   * 
   * @param activityAdd activity to clone
   * @param activity base
   * @param phase the current phase
   */
  public void cloneProjectExpectedStudy(ProjectExpectedStudy projectExpectedStudyAdd,
    ProjectExpectedStudy projectExpectedStudy, Phase phase) {
    projectExpectedStudyAdd.setActive(true);
    projectExpectedStudyAdd.setActiveSince(new Date());
    projectExpectedStudyAdd.setCreatedBy(projectExpectedStudy.getCreatedBy());
    projectExpectedStudyAdd.setComments(projectExpectedStudy.getComments());
    projectExpectedStudyAdd.setComposedId(projectExpectedStudy.getComposedId());
    projectExpectedStudyAdd.setOtherType(projectExpectedStudy.getOtherType());
    projectExpectedStudyAdd.setScope(projectExpectedStudy.getScope());
    projectExpectedStudyAdd.setSrfSloIndicator(projectExpectedStudy.getSrfSloIndicator());
    projectExpectedStudyAdd.setSrfSubIdo(projectExpectedStudy.getSrfSubIdo());
    projectExpectedStudyAdd.setTopicStudy(projectExpectedStudy.getTopicStudy());
    projectExpectedStudyAdd.setModificationJustification(projectExpectedStudy.getModificationJustification());
    projectExpectedStudyAdd.setModifiedBy(projectExpectedStudy.getCreatedBy());
    projectExpectedStudyAdd.setPhase(phase);
    projectExpectedStudyAdd.setProject(projectDAO.find(projectExpectedStudy.getProject().getId()));

  }

  @Override
  public void deleteProjectExpectedStudy(long projectExpectedStudyId) {

    projectExpectedStudyDAO.deleteProjectExpectedStudy(projectExpectedStudyId);
  }

  @Override
  public boolean existProjectExpectedStudy(long projectExpectedStudyID) {

    return projectExpectedStudyDAO.existProjectExpectedStudy(projectExpectedStudyID);
  }

  @Override
  public List<ProjectExpectedStudy> findAll() {

    return projectExpectedStudyDAO.findAll();

  }


  @Override
  public ProjectExpectedStudy getProjectExpectedStudyById(long projectExpectedStudyID) {

    return projectExpectedStudyDAO.find(projectExpectedStudyID);
  }

  @Override
  public ProjectExpectedStudy saveProjectExpectedStudy(ProjectExpectedStudy projectExpectedStudy) {
    ProjectExpectedStudy projectExpectedStudyResult = projectExpectedStudyDAO.save(projectExpectedStudy);
    Phase currentPhase = phaseDAO.find(projectExpectedStudy.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (projectExpectedStudy.getPhase().getNext() != null) {
        this.saveProjectExpectedStudyPhase(projectExpectedStudy.getPhase().getNext(),
          projectExpectedStudy.getProject().getId(), projectExpectedStudy);
      }
    }
    return projectExpectedStudyResult;
  }

  public void saveProjectExpectedStudyPhase(Phase next, long projecID, ProjectExpectedStudy projectExpectedStudy) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudy> expectedStudies = phase.getProjectExpectedStudies().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
        && c.getComposedId().equals(projectExpectedStudy.getComposedId()))
      .collect(Collectors.toList());
    if (expectedStudies.isEmpty()) {
      ProjectExpectedStudy projectExpectedStudyAdd = new ProjectExpectedStudy();
      this.cloneProjectExpectedStudy(projectExpectedStudyAdd, projectExpectedStudy, phase);
      projectExpectedStudyAdd = projectExpectedStudyDAO.save(projectExpectedStudyAdd);
      if (projectExpectedStudyAdd.getComposedId() == null) {
        projectExpectedStudy
          .setComposedId(projectExpectedStudy.getProject().getId() + "-" + projectExpectedStudyAdd.getId());
        projectExpectedStudyAdd.setComposedId(projectExpectedStudy.getComposedId());
        projectExpectedStudyDAO.save(projectExpectedStudyAdd);
      }
      if (projectExpectedStudy.getProjects() != null) {
        for (ExpectedStudyProject expectedStudyProject : projectExpectedStudy.getProjects()) {
          ExpectedStudyProject expectedStudyProjectAdd = new ExpectedStudyProject();

          this.cloneExpectedStudyProject(expectedStudyProjectAdd, expectedStudyProject, projectExpectedStudy,
            projectExpectedStudyAdd, phase);

          expectedStudyProjectDAO.save(expectedStudyProjectAdd);
        }
      }
    } else {
      ProjectExpectedStudy projectExpectedStudyAdd = expectedStudies.get(0);
      this.cloneProjectExpectedStudy(projectExpectedStudyAdd, projectExpectedStudy, phase);
      projectExpectedStudyAdd = projectExpectedStudyDAO.save(projectExpectedStudyAdd);
      if (projectExpectedStudy.getProjects() == null) {
        projectExpectedStudy.setProjects(new ArrayList<>());
      }

      for (ExpectedStudyProject expectedStudyProject : projectExpectedStudy.getProjects()) {
        if (projectExpectedStudyAdd.getExpectedStudyProjects().stream()
          .filter(c -> c.isActive() && c.getMyProject().getId().equals(expectedStudyProject.getMyProject().getId()))
          .collect(Collectors.toList()).isEmpty()) {
          ExpectedStudyProject expectedStudyProjectAdd = new ExpectedStudyProject();
          this.cloneExpectedStudyProject(expectedStudyProjectAdd, expectedStudyProject, projectExpectedStudy,
            projectExpectedStudyAdd, phase);
          expectedStudyProjectDAO.save(expectedStudyProjectAdd);
        }
      }
      for (ExpectedStudyProject expectedStudyProject : projectExpectedStudyAdd.getExpectedStudyProjects().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList())) {
        if (projectExpectedStudy.getProjects().stream()
          .filter(c -> c.getMyProject().getId().equals(expectedStudyProject.getMyProject().getId()))
          .collect(Collectors.toList()).isEmpty()) {
          expectedStudyProject.setActive(false);
          expectedStudyProjectDAO.save(expectedStudyProject);
        }

      }

    }

    if (phase.getNext() != null)

    {
      this.saveProjectExpectedStudyPhase(phase.getNext(), projecID, projectExpectedStudy);
    }


  }


}
