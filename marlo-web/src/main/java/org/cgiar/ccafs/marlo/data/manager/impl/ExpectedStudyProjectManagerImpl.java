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
import org.cgiar.ccafs.marlo.data.manager.ExpectedStudyProjectManager;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ExpectedStudyProjectManagerImpl implements ExpectedStudyProjectManager {


  private ExpectedStudyProjectDAO expectedStudyProjectDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ExpectedStudyProjectManagerImpl(ExpectedStudyProjectDAO expectedStudyProjectDAO, PhaseDAO phaseDAO) {
    this.expectedStudyProjectDAO = expectedStudyProjectDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteExpectedStudyProject(long expectedStudyProjectId) {

    ExpectedStudyProject expectedStudyProject = this.getExpectedStudyProjectById(expectedStudyProjectId);
    Phase currentPhase = expectedStudyProject.getPhase();

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteExpectedStudyProjectPhase(upkeepPhase, expectedStudyProject.getProjectExpectedStudy().getId(),
            expectedStudyProject);
        }
      }
    } else {
      if (currentPhase.getNext() != null) {
        this.deleteExpectedStudyProjectPhase(currentPhase.getNext(),
          expectedStudyProject.getProjectExpectedStudy().getId(), expectedStudyProject);
      }
    }

    expectedStudyProjectDAO.deleteExpectedStudyProject(expectedStudyProjectId);
  }


  public void deleteExpectedStudyProjectPhase(Phase next, long expectedID, ExpectedStudyProject pxpectedStudyProject) {
    Phase phase = phaseDAO.find(next.getId());

    List<ExpectedStudyProject> pxpectedStudyProjects = phase.getExpectedStudyProjects().stream()
      .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getProject().getId().equals(pxpectedStudyProject.getProject().getId()))
      .collect(Collectors.toList());
    for (ExpectedStudyProject pxpectedStudyProjectDB : pxpectedStudyProjects) {
      expectedStudyProjectDAO.deleteExpectedStudyProject(pxpectedStudyProjectDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteExpectedStudyProjectPhase(phase.getNext(), expectedID, pxpectedStudyProject);
    }
  }

  @Override
  public boolean existExpectedStudyProject(long expectedStudyProjectID) {

    return expectedStudyProjectDAO.existExpectedStudyProject(expectedStudyProjectID);
  }

  @Override
  public List<ExpectedStudyProject> findAll() {

    return expectedStudyProjectDAO.findAll();

  }

  @Override
  public ExpectedStudyProject getExpectedStudyProjectById(long expectedStudyProjectID) {

    return expectedStudyProjectDAO.find(expectedStudyProjectID);
  }

  @Override
  public ExpectedStudyProject saveExpectedStudyProject(ExpectedStudyProject expectedStudyProject) {

    ExpectedStudyProject expectedStudy = expectedStudyProjectDAO.save(expectedStudyProject);
    Phase currentPhase = expectedStudy.getPhase();

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveExpectedStudyProjectPhase(upkeepPhase, expectedStudy.getProjectExpectedStudy().getId(),
            expectedStudyProject);
        }
      }
    } else {
      if (currentPhase.getNext() != null) {
        this.saveExpectedStudyProjectPhase(currentPhase.getNext(), expectedStudy.getProjectExpectedStudy().getId(),
          expectedStudyProject);
      }
    }


    return expectedStudy;
  }

  public void saveExpectedStudyProjectPhase(Phase next, long expectedID, ExpectedStudyProject expectedStudyProject) {
    Phase phase = phaseDAO.find(next.getId());

    List<ExpectedStudyProject> expectedStudyProjects = phase.getExpectedStudyProjects().stream()
      .filter(c -> c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getProject().getId().equals(expectedStudyProject.getProject().getId()))
      .collect(Collectors.toList());

    if (expectedStudyProjects.isEmpty()) {
      ExpectedStudyProject expectedStudyProjectAdd = new ExpectedStudyProject();
      expectedStudyProjectAdd.setProjectExpectedStudy(expectedStudyProject.getProjectExpectedStudy());
      expectedStudyProjectAdd.setPhase(phase);
      expectedStudyProjectAdd.setProject(expectedStudyProject.getProject());
      expectedStudyProjectDAO.save(expectedStudyProjectAdd);
    }


    if (phase.getNext() != null) {
      this.saveExpectedStudyProjectPhase(phase.getNext(), expectedID, expectedStudyProject);
    }
  }


}
