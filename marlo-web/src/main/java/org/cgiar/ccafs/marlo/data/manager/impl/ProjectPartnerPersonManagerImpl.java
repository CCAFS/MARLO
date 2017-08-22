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


import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerPersonDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectPartnerPersonManagerImpl implements ProjectPartnerPersonManager {


  private ProjectPartnerPersonDAO projectPartnerPersonDAO;
  // Managers


  @Inject
  public ProjectPartnerPersonManagerImpl(ProjectPartnerPersonDAO projectPartnerPersonDAO) {
    this.projectPartnerPersonDAO = projectPartnerPersonDAO;


  }

  @Override
  public void deleteProjectPartnerPerson(long projectPartnerPersonId) {

    projectPartnerPersonDAO.deleteProjectPartnerPerson(projectPartnerPersonId);
  }

  @Override
  public boolean existProjectPartnerPerson(long projectPartnerPersonID) {

    return projectPartnerPersonDAO.existProjectPartnerPerson(projectPartnerPersonID);
  }

  @Override
  public List<ProjectPartnerPerson> findAll() {

    return projectPartnerPersonDAO.findAll();

  }

  @Override
  public ProjectPartnerPerson getProjectPartnerPersonById(long projectPartnerPersonID) {

    return projectPartnerPersonDAO.find(projectPartnerPersonID);
  }

  @Override
  public ProjectPartnerPerson saveProjectPartnerPerson(ProjectPartnerPerson projectPartnerPerson) {

    return projectPartnerPersonDAO.save(projectPartnerPerson);
  }


}
