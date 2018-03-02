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

import org.cgiar.ccafs.marlo.data.model.PowbCrpStaffingCategories;

import java.util.List;


public interface PowbCrpStaffingCategoriesDAO {

  /**
   * This method removes a specific powbCrpStaffingCategories value from the database.
   * 
   * @param powbCrpStaffingCategoriesId is the powbCrpStaffingCategories identifier.
   * @return true if the powbCrpStaffingCategories was successfully deleted, false otherwise.
   */
  public void deletePowbCrpStaffingCategories(long powbCrpStaffingCategoriesId);

  /**
   * This method validate if the powbCrpStaffingCategories identify with the given id exists in the system.
   * 
   * @param powbCrpStaffingCategoriesID is a powbCrpStaffingCategories identifier.
   * @return true if the powbCrpStaffingCategories exists, false otherwise.
   */
  public boolean existPowbCrpStaffingCategories(long powbCrpStaffingCategoriesID);

  /**
   * This method gets a powbCrpStaffingCategories object by a given powbCrpStaffingCategories identifier.
   * 
   * @param powbCrpStaffingCategoriesID is the powbCrpStaffingCategories identifier.
   * @return a PowbCrpStaffingCategories object.
   */
  public PowbCrpStaffingCategories find(long id);

  /**
   * This method gets a list of powbCrpStaffingCategories that are active
   * 
   * @return a list from PowbCrpStaffingCategories null if no exist records
   */
  public List<PowbCrpStaffingCategories> findAll();


  /**
   * This method saves the information of the given powbCrpStaffingCategories
   * 
   * @param powbCrpStaffingCategories - is the powbCrpStaffingCategories object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbCrpStaffingCategories was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbCrpStaffingCategories save(PowbCrpStaffingCategories powbCrpStaffingCategories);
}
