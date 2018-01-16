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


import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerOverallDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerOverallManager;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerOverall;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectPartnerOverallManagerImpl implements ProjectPartnerOverallManager {


  private ProjectPartnerOverallDAO projectPartnerOverallDAO;
  // Managers


  @Inject
  public ProjectPartnerOverallManagerImpl(ProjectPartnerOverallDAO projectPartnerOverallDAO) {
    this.projectPartnerOverallDAO = projectPartnerOverallDAO;


  }

  @Override
  public void deleteProjectPartnerOverall(long projectPartnerOverallId) {

    projectPartnerOverallDAO.deleteProjectPartnerOverall(projectPartnerOverallId);
  }

  @Override
  public boolean existProjectPartnerOverall(long projectPartnerOverallID) {

    return projectPartnerOverallDAO.existProjectPartnerOverall(projectPartnerOverallID);
  }

  @Override
  public List<ProjectPartnerOverall> findAll() {

    return projectPartnerOverallDAO.findAll();

  }

  @Override
  public ProjectPartnerOverall getProjectPartnerOverallById(long projectPartnerOverallID) {

    return projectPartnerOverallDAO.find(projectPartnerOverallID);
  }

  @Override
  public ProjectPartnerOverall saveProjectPartnerOverall(ProjectPartnerOverall projectPartnerOverall) {

    return projectPartnerOverallDAO.save(projectPartnerOverall);
  }


}
