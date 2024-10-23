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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationSDGDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationSDGManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSDG;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationSDGManagerImpl implements ProjectInnovationSDGManager {


  private ProjectInnovationSDGDAO projectInnovationSDGDAO;
  // Managers


  @Inject
  public ProjectInnovationSDGManagerImpl(ProjectInnovationSDGDAO projectInnovationSDGDAO) {
    this.projectInnovationSDGDAO = projectInnovationSDGDAO;


  }

  @Override
  public void deleteProjectInnovationSDG(long projectInnovationSDGId) {

    projectInnovationSDGDAO.deleteProjectInnovationSDG(projectInnovationSDGId);
  }

  @Override
  public boolean existProjectInnovationSDG(long projectInnovationSDGID) {

    return projectInnovationSDGDAO.existProjectInnovationSDG(projectInnovationSDGID);
  }

  @Override
  public List<ProjectInnovationSDG> findAll() {

    return projectInnovationSDGDAO.findAll();

  }

  @Override
  public ProjectInnovationSDG getProjectInnovationSDGById(long projectInnovationSDGID) {

    return projectInnovationSDGDAO.find(projectInnovationSDGID);
  }

  @Override
  public ProjectInnovationSDG saveProjectInnovationSDG(ProjectInnovationSDG projectInnovationSDG) {

    return projectInnovationSDGDAO.save(projectInnovationSDG);
  }


}
