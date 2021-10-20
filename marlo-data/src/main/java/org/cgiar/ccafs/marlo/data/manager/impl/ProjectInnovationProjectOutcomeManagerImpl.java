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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationProjectOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationProjectOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationProjectOutcomeManagerImpl implements ProjectInnovationProjectOutcomeManager {


  private ProjectInnovationProjectOutcomeDAO projectInnovationProjectOutcomeDAO;
  // Managers


  @Inject
  public ProjectInnovationProjectOutcomeManagerImpl(ProjectInnovationProjectOutcomeDAO projectInnovationProjectOutcomeDAO) {
    this.projectInnovationProjectOutcomeDAO = projectInnovationProjectOutcomeDAO;


  }

  @Override
  public void deleteProjectInnovationProjectOutcome(long projectInnovationProjectOutcomeId) {

    projectInnovationProjectOutcomeDAO.deleteProjectInnovationProjectOutcome(projectInnovationProjectOutcomeId);
  }

  @Override
  public boolean existProjectInnovationProjectOutcome(long projectInnovationProjectOutcomeID) {

    return projectInnovationProjectOutcomeDAO.existProjectInnovationProjectOutcome(projectInnovationProjectOutcomeID);
  }

  @Override
  public List<ProjectInnovationProjectOutcome> findAll() {

    return projectInnovationProjectOutcomeDAO.findAll();

  }

  @Override
  public ProjectInnovationProjectOutcome getProjectInnovationProjectOutcomeById(long projectInnovationProjectOutcomeID) {

    return projectInnovationProjectOutcomeDAO.find(projectInnovationProjectOutcomeID);
  }

  @Override
  public ProjectInnovationProjectOutcome saveProjectInnovationProjectOutcome(ProjectInnovationProjectOutcome projectInnovationProjectOutcome) {

    return projectInnovationProjectOutcomeDAO.save(projectInnovationProjectOutcome);
  }


}
