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


import org.cgiar.ccafs.marlo.data.dao.ProjectImpactsDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectImpactsManager;
import org.cgiar.ccafs.marlo.data.model.ProjectImpacts;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectImpactsManagerImpl implements ProjectImpactsManager {


  private ProjectImpactsDAO projectImpactsDAO;
  // Managers


  @Inject
  public ProjectImpactsManagerImpl(ProjectImpactsDAO projectImpactsDAO) {
    this.projectImpactsDAO = projectImpactsDAO;


  }

  @Override
  public void deleteProjectImpacts(long projectImpactsId) {

    projectImpactsDAO.deleteProjectImpacts(projectImpactsId);
  }

  @Override
  public boolean existProjectImpacts(long projectImpactsID) {

    return projectImpactsDAO.existProjectImpacts(projectImpactsID);
  }

  @Override
  public List<ProjectImpacts> findAll() {

    return projectImpactsDAO.findAll();

  }

  @Override
  public ProjectImpacts getProjectImpactsById(long projectImpactsID) {

    return projectImpactsDAO.find(projectImpactsID);
  }

  @Override
  public List<ProjectImpacts> getProjectImpactsByProjectId(long projectId) {
    return projectImpactsDAO.findByProjectId(projectId);
  }

  @Override
  public ProjectImpacts saveProjectImpacts(ProjectImpacts projectImpacts) {

    return projectImpactsDAO.save(projectImpacts);
  }


}
