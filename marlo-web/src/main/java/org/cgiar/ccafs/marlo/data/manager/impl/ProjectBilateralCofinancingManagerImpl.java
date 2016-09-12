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


import org.cgiar.ccafs.marlo.data.dao.ProjectBilateralCofinancingDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectBilateralCofinancingManager;
import org.cgiar.ccafs.marlo.data.model.ProjectBilateralCofinancing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectBilateralCofinancingManagerImpl implements ProjectBilateralCofinancingManager {


  private ProjectBilateralCofinancingDAO projectBilateralCofinancingDAO;
  // Managers


  @Inject
  public ProjectBilateralCofinancingManagerImpl(ProjectBilateralCofinancingDAO projectBilateralCofinancingDAO) {
    this.projectBilateralCofinancingDAO = projectBilateralCofinancingDAO;


  }

  @Override
  public boolean deleteProjectBilateralCofinancing(long projectBilateralCofinancingId) {

    return projectBilateralCofinancingDAO.deleteProjectBilateralCofinancing(projectBilateralCofinancingId);
  }

  @Override
  public boolean existProjectBilateralCofinancing(long projectBilateralCofinancingID) {

    return projectBilateralCofinancingDAO.existProjectBilateralCofinancing(projectBilateralCofinancingID);
  }

  @Override
  public List<ProjectBilateralCofinancing> findAll() {

    return projectBilateralCofinancingDAO.findAll();

  }

  @Override
  public ProjectBilateralCofinancing getProjectBilateralCofinancingById(long projectBilateralCofinancingID) {

    return projectBilateralCofinancingDAO.find(projectBilateralCofinancingID);
  }

  @Override
  public List<ProjectBilateralCofinancing> getUserCofundedProjects(long userId, String crp) {
    List<ProjectBilateralCofinancing> projects = new ArrayList<>();

    List<Map<String, Object>> view = projectBilateralCofinancingDAO.getUserCofundedProjects(userId, crp);

    if (view != null) {
      for (Map<String, Object> map : view) {
        projects.add(this.getProjectBilateralCofinancingById((Long.parseLong(map.get("project_id").toString()))));
      }
    }
    return projects;
  }

  @Override
  public long saveProjectBilateralCofinancing(ProjectBilateralCofinancing projectBilateralCofinancing) {

    return projectBilateralCofinancingDAO.save(projectBilateralCofinancing);
  }

  @Override
  public List<ProjectBilateralCofinancing> searchProject(String searchValue, long institutionID, int year) {
    return projectBilateralCofinancingDAO.searchProject(searchValue, institutionID, year);
  }


}
