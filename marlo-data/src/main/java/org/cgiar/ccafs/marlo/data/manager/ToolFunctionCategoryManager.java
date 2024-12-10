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
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.model.ToolFunctionCategory;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ToolFunctionCategoryManager {


  /**
   * This method removes a specific toolFunctionCategory value from the database.
   * 
   * @param toolFunctionCategoryId is the toolFunctionCategory identifier.
   * @return true if the toolFunctionCategory was successfully deleted, false otherwise.
   */
  public void deleteToolFunctionCategory(long toolFunctionCategoryId);


  /**
   * This method validate if the toolFunctionCategory identify with the given id exists in the system.
   * 
   * @param toolFunctionCategoryID is a toolFunctionCategory identifier.
   * @return true if the toolFunctionCategory exists, false otherwise.
   */
  public boolean existToolFunctionCategory(long toolFunctionCategoryID);


  /**
   * This method gets a list of toolFunctionCategory that are active
   * 
   * @return a list from ToolFunctionCategory null if no exist records
   */
  public List<ToolFunctionCategory> findAll();


  /**
   * This method gets a toolFunctionCategory object by a given toolFunctionCategory identifier.
   * 
   * @param toolFunctionCategoryID is the toolFunctionCategory identifier.
   * @return a ToolFunctionCategory object.
   */
  public ToolFunctionCategory getToolFunctionCategoryById(long toolFunctionCategoryID);

  /**
   * This method saves the information of the given toolFunctionCategory
   * 
   * @param toolFunctionCategory - is the toolFunctionCategory object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the toolFunctionCategory was
   *         updated
   *         or -1 is some error occurred.
   */
  public ToolFunctionCategory saveToolFunctionCategory(ToolFunctionCategory toolFunctionCategory);


}
