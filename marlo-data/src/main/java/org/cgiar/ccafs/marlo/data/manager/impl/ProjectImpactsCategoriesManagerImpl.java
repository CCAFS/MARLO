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


import org.cgiar.ccafs.marlo.data.dao.ProjectImpactsCategoriesDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectImpactsCategoriesManager;
import org.cgiar.ccafs.marlo.data.model.ProjectImpactsCategories;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectImpactsCategoriesManagerImpl implements ProjectImpactsCategoriesManager {


  private ProjectImpactsCategoriesDAO projectImpactsCategoriesDAO;
  // Managers


  @Inject
  public ProjectImpactsCategoriesManagerImpl(ProjectImpactsCategoriesDAO projectImpactsCategoriesDAO) {
    this.projectImpactsCategoriesDAO = projectImpactsCategoriesDAO;


  }

  @Override
  public void deleteProjectImpactsCategories(long projectImpactsCategoriesId) {

    projectImpactsCategoriesDAO.deleteProjectImpactsCategories(projectImpactsCategoriesId);
  }

  @Override
  public boolean existProjectImpactsCategories(long projectImpactsCategoriesID) {

    return projectImpactsCategoriesDAO.existProjectImpactsCategories(projectImpactsCategoriesID);
  }

  @Override
  public List<ProjectImpactsCategories> findAll() {

    return projectImpactsCategoriesDAO.findAll();

  }

  @Override
  public ProjectImpactsCategories getProjectImpactsCategoriesById(long projectImpactsCategoriesID) {

    return projectImpactsCategoriesDAO.find(projectImpactsCategoriesID);
  }

  @Override
  public ProjectImpactsCategories saveProjectImpactsCategories(ProjectImpactsCategories projectImpactsCategories) {

    return projectImpactsCategoriesDAO.save(projectImpactsCategories);
  }


}
