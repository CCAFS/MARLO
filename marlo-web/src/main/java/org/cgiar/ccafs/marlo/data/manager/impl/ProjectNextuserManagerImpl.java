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


import org.cgiar.ccafs.marlo.data.dao.ProjectNextuserDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectNextuserManager;
import org.cgiar.ccafs.marlo.data.model.ProjectNextuser;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectNextuserManagerImpl implements ProjectNextuserManager {


  private ProjectNextuserDAO projectNextuserDAO;
  // Managers


  @Inject
  public ProjectNextuserManagerImpl(ProjectNextuserDAO projectNextuserDAO) {
    this.projectNextuserDAO = projectNextuserDAO;


  }

  @Override
  public void deleteProjectNextuser(long projectNextuserId) {

    projectNextuserDAO.deleteProjectNextuser(projectNextuserId);
  }

  @Override
  public boolean existProjectNextuser(long projectNextuserID) {

    return projectNextuserDAO.existProjectNextuser(projectNextuserID);
  }

  @Override
  public List<ProjectNextuser> findAll() {

    return projectNextuserDAO.findAll();

  }

  @Override
  public ProjectNextuser getProjectNextuserById(long projectNextuserID) {

    return projectNextuserDAO.find(projectNextuserID);
  }

  @Override
  public ProjectNextuser saveProjectNextuser(ProjectNextuser projectNextuser) {

    return projectNextuserDAO.save(projectNextuser);
  }


}
