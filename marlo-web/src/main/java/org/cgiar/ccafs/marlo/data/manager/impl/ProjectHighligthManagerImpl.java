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


import org.cgiar.ccafs.marlo.data.dao.ProjectHighligthDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectHighligthManager;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectHighligthManagerImpl implements ProjectHighligthManager {


  private ProjectHighligthDAO projectHighligthDAO;
  // Managers


  @Inject
  public ProjectHighligthManagerImpl(ProjectHighligthDAO projectHighligthDAO) {
    this.projectHighligthDAO = projectHighligthDAO;


  }

  @Override
  public void deleteProjectHighligth(long projectHighligthId) {

    projectHighligthDAO.deleteProjectHighligth(projectHighligthId);
  }

  @Override
  public boolean existProjectHighligth(long projectHighligthID) {

    return projectHighligthDAO.existProjectHighligth(projectHighligthID);
  }

  @Override
  public List<ProjectHighlight> findAll() {

    return projectHighligthDAO.findAll();

  }

  @Override
  public ProjectHighlight getProjectHighligthById(long projectHighligthID) {

    return projectHighligthDAO.find(projectHighligthID);
  }

  @Override
  public ProjectHighlight saveProjectHighligth(ProjectHighlight projectHighlight) {

    return projectHighligthDAO.save(projectHighlight);
  }

  @Override
  public ProjectHighlight saveProjectHighligth(ProjectHighlight projectHighlight, String section, List<String> relationsName) {
    return projectHighligthDAO.save(projectHighlight, section, relationsName);
  }

}
