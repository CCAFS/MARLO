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


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.ProjectImpactsCategories;

import java.util.List;


public interface ProjectImpactsCategoriesDAO {

  /**
   * This method removes a specific projectImpactsCategories value from the database.
   * 
   * @param projectImpactsCategoriesId is the projectImpactsCategories identifier.
   * @return true if the projectImpactsCategories was successfully deleted, false otherwise.
   */
  public void deleteProjectImpactsCategories(long projectImpactsCategoriesId);

  /**
   * This method validate if the projectImpactsCategories identify with the given id exists in the system.
   * 
   * @param projectImpactsCategoriesID is a projectImpactsCategories identifier.
   * @return true if the projectImpactsCategories exists, false otherwise.
   */
  public boolean existProjectImpactsCategories(long projectImpactsCategoriesID);

  /**
   * This method gets a projectImpactsCategories object by a given projectImpactsCategories identifier.
   * 
   * @param projectImpactsCategoriesID is the projectImpactsCategories identifier.
   * @return a ProjectImpactsCategories object.
   */
  public ProjectImpactsCategories find(long id);

  /**
   * This method gets a list of projectImpactsCategories that are active
   * 
   * @return a list from ProjectImpactsCategories null if no exist records
   */
  public List<ProjectImpactsCategories> findAll();


  /**
   * This method saves the information of the given projectImpactsCategories
   * 
   * @param projectImpactsCategories - is the projectImpactsCategories object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectImpactsCategories was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectImpactsCategories save(ProjectImpactsCategories projectImpactsCategories);
}
