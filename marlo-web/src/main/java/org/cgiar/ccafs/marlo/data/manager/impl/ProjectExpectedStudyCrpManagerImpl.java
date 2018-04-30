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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyCrpDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCrpManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrp;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyCrpManagerImpl implements ProjectExpectedStudyCrpManager {


  private ProjectExpectedStudyCrpDAO projectExpectedStudyCrpDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyCrpManagerImpl(ProjectExpectedStudyCrpDAO projectExpectedStudyCrpDAO) {
    this.projectExpectedStudyCrpDAO = projectExpectedStudyCrpDAO;


  }

  @Override
  public void deleteProjectExpectedStudyCrp(long projectExpectedStudyCrpId) {

    projectExpectedStudyCrpDAO.deleteProjectExpectedStudyCrp(projectExpectedStudyCrpId);
  }

  @Override
  public boolean existProjectExpectedStudyCrp(long projectExpectedStudyCrpID) {

    return projectExpectedStudyCrpDAO.existProjectExpectedStudyCrp(projectExpectedStudyCrpID);
  }

  @Override
  public List<ProjectExpectedStudyCrp> findAll() {

    return projectExpectedStudyCrpDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyCrp getProjectExpectedStudyCrpById(long projectExpectedStudyCrpID) {

    return projectExpectedStudyCrpDAO.find(projectExpectedStudyCrpID);
  }

  @Override
  public ProjectExpectedStudyCrp saveProjectExpectedStudyCrp(ProjectExpectedStudyCrp projectExpectedStudyCrp) {

    return projectExpectedStudyCrpDAO.save(projectExpectedStudyCrp);
  }


}
