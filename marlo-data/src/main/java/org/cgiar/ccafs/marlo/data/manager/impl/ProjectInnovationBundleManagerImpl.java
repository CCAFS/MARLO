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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationBundleDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationBundleManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationBundle;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationBundleManagerImpl implements ProjectInnovationBundleManager {


  private ProjectInnovationBundleDAO projectInnovationBundleDAO;
  // Managers


  @Inject
  public ProjectInnovationBundleManagerImpl(ProjectInnovationBundleDAO projectInnovationBundleDAO) {
    this.projectInnovationBundleDAO = projectInnovationBundleDAO;


  }

  @Override
  public void deleteProjectInnovationBundle(long projectInnovationBundleId) {

    projectInnovationBundleDAO.deleteProjectInnovationBundle(projectInnovationBundleId);
  }

  @Override
  public boolean existProjectInnovationBundle(long projectInnovationBundleID) {

    return projectInnovationBundleDAO.existProjectInnovationBundle(projectInnovationBundleID);
  }

  @Override
  public List<ProjectInnovationBundle> findAll() {

    return projectInnovationBundleDAO.findAll();

  }

  @Override
  public ProjectInnovationBundle getProjectInnovationBundleById(long projectInnovationBundleID) {

    return projectInnovationBundleDAO.find(projectInnovationBundleID);
  }

  @Override
  public List<ProjectInnovationBundle> getProjectInnovationBundleByInnovationAndPhase(long innovationID, long phaseID) {
    return projectInnovationBundleDAO.getProjectInnovationBundleByInnovationAndPhase(innovationID, phaseID);
  }

  @Override
  public ProjectInnovationBundle saveProjectInnovationBundle(ProjectInnovationBundle projectInnovationBundle) {

    return projectInnovationBundleDAO.save(projectInnovationBundle);
  }


}
