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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationPartnershipPersonDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationPartnershipPersonManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPartnershipPerson;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationPartnershipPersonManagerImpl implements ProjectInnovationPartnershipPersonManager {


  private ProjectInnovationPartnershipPersonDAO projectInnovationPartnershipPersonDAO;
  // Managers


  @Inject
  public ProjectInnovationPartnershipPersonManagerImpl(ProjectInnovationPartnershipPersonDAO projectInnovationPartnershipPersonDAO) {
    this.projectInnovationPartnershipPersonDAO = projectInnovationPartnershipPersonDAO;


  }

  @Override
  public void deleteProjectInnovationPartnershipPerson(long projectInnovationPartnershipPersonId) {

    projectInnovationPartnershipPersonDAO.deleteProjectInnovationPartnershipPerson(projectInnovationPartnershipPersonId);
  }

  @Override
  public boolean existProjectInnovationPartnershipPerson(long projectInnovationPartnershipPersonID) {

    return projectInnovationPartnershipPersonDAO.existProjectInnovationPartnershipPerson(projectInnovationPartnershipPersonID);
  }

  @Override
  public List<ProjectInnovationPartnershipPerson> findAll() {

    return projectInnovationPartnershipPersonDAO.findAll();

  }

  @Override
  public ProjectInnovationPartnershipPerson getProjectInnovationPartnershipPersonById(long projectInnovationPartnershipPersonID) {

    return projectInnovationPartnershipPersonDAO.find(projectInnovationPartnershipPersonID);
  }

  @Override
  public ProjectInnovationPartnershipPerson saveProjectInnovationPartnershipPerson(ProjectInnovationPartnershipPerson projectInnovationPartnershipPerson) {

    return projectInnovationPartnershipPersonDAO.save(projectInnovationPartnershipPerson);
  }


}
