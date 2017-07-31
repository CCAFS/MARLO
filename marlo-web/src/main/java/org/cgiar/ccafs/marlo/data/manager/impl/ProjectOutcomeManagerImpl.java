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


import org.cgiar.ccafs.marlo.data.dao.ProjectOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectOutcomeManagerImpl implements ProjectOutcomeManager {


  private ProjectOutcomeDAO projectOutcomeDAO;
  // Managers


  @Inject
  public ProjectOutcomeManagerImpl(ProjectOutcomeDAO projectOutcomeDAO) {
    this.projectOutcomeDAO = projectOutcomeDAO;


  }

  @Override
  public boolean deleteProjectOutcome(long projectOutcomeId) {

    return projectOutcomeDAO.deleteProjectOutcome(projectOutcomeId);
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
  public long saveProjectOutcome(ProjectOutcome projectOutcome) {

    return projectOutcomeDAO.save(projectOutcome);
  }


  @Override
  public long saveProjectOutcome(ProjectOutcome projectOutcome, String section, List<String> relationsName,
    Phase phase) {

    return projectOutcomeDAO.save(projectOutcome, section, relationsName, phase);
  }


}
