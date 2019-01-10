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


import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyCountryDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCountryManager;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCountry;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectPolicyCountryManagerImpl implements ProjectPolicyCountryManager {


  private ProjectPolicyCountryDAO projectPolicyCountryDAO;
  // Managers


  @Inject
  public ProjectPolicyCountryManagerImpl(ProjectPolicyCountryDAO projectPolicyCountryDAO) {
    this.projectPolicyCountryDAO = projectPolicyCountryDAO;


  }

  @Override
  public void deleteProjectPolicyCountry(long projectPolicyCountryId) {

    projectPolicyCountryDAO.deleteProjectPolicyCountry(projectPolicyCountryId);
  }

  @Override
  public boolean existProjectPolicyCountry(long projectPolicyCountryID) {

    return projectPolicyCountryDAO.existProjectPolicyCountry(projectPolicyCountryID);
  }

  @Override
  public List<ProjectPolicyCountry> findAll() {

    return projectPolicyCountryDAO.findAll();

  }

  @Override
  public ProjectPolicyCountry getProjectPolicyCountryById(long projectPolicyCountryID) {

    return projectPolicyCountryDAO.find(projectPolicyCountryID);
  }

  @Override
  public ProjectPolicyCountry saveProjectPolicyCountry(ProjectPolicyCountry projectPolicyCountry) {

    return projectPolicyCountryDAO.save(projectPolicyCountry);
  }


}
