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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationReferenceUrlDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationReferenceUrlManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReferenceUrl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationReferenceUrlManagerImpl implements ProjectInnovationReferenceUrlManager {


  private ProjectInnovationReferenceUrlDAO projectInnovationReferenceUrlDAO;
  // Managers


  @Inject
  public ProjectInnovationReferenceUrlManagerImpl(ProjectInnovationReferenceUrlDAO projectInnovationReferenceUrlDAO) {
    this.projectInnovationReferenceUrlDAO = projectInnovationReferenceUrlDAO;


  }

  @Override
  public void deleteProjectInnovationReferenceUrl(long projectInnovationReferenceUrlId) {

    projectInnovationReferenceUrlDAO.deleteProjectInnovationReferenceUrl(projectInnovationReferenceUrlId);
  }

  @Override
  public boolean existProjectInnovationReferenceUrl(long projectInnovationReferenceUrlID) {

    return projectInnovationReferenceUrlDAO.existProjectInnovationReferenceUrl(projectInnovationReferenceUrlID);
  }

  @Override
  public List<ProjectInnovationReferenceUrl> findAll() {

    return projectInnovationReferenceUrlDAO.findAll();

  }

  @Override
  public ProjectInnovationReferenceUrl getProjectInnovationReferenceUrlById(long projectInnovationReferenceUrlID) {

    return projectInnovationReferenceUrlDAO.find(projectInnovationReferenceUrlID);
  }

  @Override
  public ProjectInnovationReferenceUrl saveProjectInnovationReferenceUrl(ProjectInnovationReferenceUrl projectInnovationReferenceUrl) {

    return projectInnovationReferenceUrlDAO.save(projectInnovationReferenceUrl);
  }


}
