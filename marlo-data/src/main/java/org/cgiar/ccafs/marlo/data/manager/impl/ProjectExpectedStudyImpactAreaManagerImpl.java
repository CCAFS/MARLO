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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyImpactAreaDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyImpactAreaManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyImpactArea;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyImpactAreaManagerImpl implements ProjectExpectedStudyImpactAreaManager {


  private ProjectExpectedStudyImpactAreaDAO projectExpectedStudyImpactAreaDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyImpactAreaManagerImpl(ProjectExpectedStudyImpactAreaDAO projectExpectedStudyImpactAreaDAO) {
    this.projectExpectedStudyImpactAreaDAO = projectExpectedStudyImpactAreaDAO;


  }

  @Override
  public void deleteProjectExpectedStudyImpactArea(long projectExpectedStudyImpactAreaId) {

    projectExpectedStudyImpactAreaDAO.deleteProjectExpectedStudyImpactArea(projectExpectedStudyImpactAreaId);
  }

  @Override
  public boolean existProjectExpectedStudyImpactArea(long projectExpectedStudyImpactAreaID) {

    return projectExpectedStudyImpactAreaDAO.existProjectExpectedStudyImpactArea(projectExpectedStudyImpactAreaID);
  }

  @Override
  public List<ProjectExpectedStudyImpactArea> findAll() {

    return projectExpectedStudyImpactAreaDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyImpactArea getProjectExpectedStudyImpactAreaById(long projectExpectedStudyImpactAreaID) {

    return projectExpectedStudyImpactAreaDAO.find(projectExpectedStudyImpactAreaID);
  }

  @Override
  public ProjectExpectedStudyImpactArea saveProjectExpectedStudyImpactArea(ProjectExpectedStudyImpactArea projectExpectedStudyImpactArea) {

    return projectExpectedStudyImpactAreaDAO.save(projectExpectedStudyImpactArea);
  }


}
