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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationAllianceLeversDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationAllianceLeversManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationAllianceLevers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationAllianceLeversManagerImpl implements ProjectInnovationAllianceLeversManager {


  private ProjectInnovationAllianceLeversDAO projectInnovationAllianceLeversDAO;
  // Managers


  @Inject
  public ProjectInnovationAllianceLeversManagerImpl(ProjectInnovationAllianceLeversDAO projectInnovationAllianceLeversDAO) {
    this.projectInnovationAllianceLeversDAO = projectInnovationAllianceLeversDAO;


  }

  @Override
  public void deleteProjectInnovationAllianceLevers(long projectInnovationAllianceLeversId) {

    projectInnovationAllianceLeversDAO.deleteProjectInnovationAllianceLevers(projectInnovationAllianceLeversId);
  }

  @Override
  public boolean existProjectInnovationAllianceLevers(long projectInnovationAllianceLeversID) {

    return projectInnovationAllianceLeversDAO.existProjectInnovationAllianceLevers(projectInnovationAllianceLeversID);
  }

  @Override
  public List<ProjectInnovationAllianceLevers> findAll() {

    return projectInnovationAllianceLeversDAO.findAll();

  }

  @Override
  public ProjectInnovationAllianceLevers getProjectInnovationAllianceLeversById(long projectInnovationAllianceLeversID) {

    return projectInnovationAllianceLeversDAO.find(projectInnovationAllianceLeversID);
  }

  @Override
  public ProjectInnovationAllianceLevers saveProjectInnovationAllianceLevers(ProjectInnovationAllianceLevers projectInnovationAllianceLevers) {

    return projectInnovationAllianceLeversDAO.save(projectInnovationAllianceLevers);
  }


}
