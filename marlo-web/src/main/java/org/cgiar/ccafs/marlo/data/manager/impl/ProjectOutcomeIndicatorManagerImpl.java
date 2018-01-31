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


import org.cgiar.ccafs.marlo.data.dao.ProjectOutcomeIndicatorDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeIndicatorManager;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcomeIndicator;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectOutcomeIndicatorManagerImpl implements ProjectOutcomeIndicatorManager {


  private ProjectOutcomeIndicatorDAO projectOutcomeIndicatorDAO;
  // Managers


  @Inject
  public ProjectOutcomeIndicatorManagerImpl(ProjectOutcomeIndicatorDAO projectOutcomeIndicatorDAO) {
    this.projectOutcomeIndicatorDAO = projectOutcomeIndicatorDAO;


  }

  @Override
  public void deleteProjectOutcomeIndicator(long projectOutcomeIndicatorId) {

    projectOutcomeIndicatorDAO.deleteProjectOutcomeIndicator(projectOutcomeIndicatorId);
  }

  @Override
  public boolean existProjectOutcomeIndicator(long projectOutcomeIndicatorID) {

    return projectOutcomeIndicatorDAO.existProjectOutcomeIndicator(projectOutcomeIndicatorID);
  }

  @Override
  public List<ProjectOutcomeIndicator> findAll() {

    return projectOutcomeIndicatorDAO.findAll();

  }

  @Override
  public ProjectOutcomeIndicator getProjectOutcomeIndicatorById(long projectOutcomeIndicatorID) {

    return projectOutcomeIndicatorDAO.find(projectOutcomeIndicatorID);
  }

  @Override
  public ProjectOutcomeIndicator saveProjectOutcomeIndicator(ProjectOutcomeIndicator projectOutcomeIndicator) {

    return projectOutcomeIndicatorDAO.save(projectOutcomeIndicator);
  }


}
