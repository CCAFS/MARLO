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

import org.cgiar.ccafs.marlo.data.model.PowbSynthesisCrpStaffingCategory;

import java.util.List;


public interface PowbSynthesisCrpStaffingCategoryDAO {

  /**
   * This method removes a specific powbSynthesisCrpStaffingCategory value from the database.
   * 
   * @param powbSynthesisCrpStaffingCategoryId is the powbSynthesisCrpStaffingCategory identifier.
   * @return true if the powbSynthesisCrpStaffingCategory was successfully deleted, false otherwise.
   */
  public void deletePowbSynthesisCrpStaffingCategory(long powbSynthesisCrpStaffingCategoryId);

  /**
   * This method validate if the powbSynthesisCrpStaffingCategory identify with the given id exists in the system.
   * 
   * @param powbSynthesisCrpStaffingCategoryID is a powbSynthesisCrpStaffingCategory identifier.
   * @return true if the powbSynthesisCrpStaffingCategory exists, false otherwise.
   */
  public boolean existPowbSynthesisCrpStaffingCategory(long powbSynthesisCrpStaffingCategoryID);

  /**
   * This method gets a powbSynthesisCrpStaffingCategory object by a given powbSynthesisCrpStaffingCategory identifier.
   * 
   * @param powbSynthesisCrpStaffingCategoryID is the powbSynthesisCrpStaffingCategory identifier.
   * @return a PowbSynthesisCrpStaffingCategory object.
   */
  public PowbSynthesisCrpStaffingCategory find(long id);

  /**
   * This method gets a list of powbSynthesisCrpStaffingCategory that are active
   * 
   * @return a list from PowbSynthesisCrpStaffingCategory null if no exist records
   */
  public List<PowbSynthesisCrpStaffingCategory> findAll();


  /**
   * This method saves the information of the given powbSynthesisCrpStaffingCategory
   * 
   * @param powbSynthesisCrpStaffingCategory - is the powbSynthesisCrpStaffingCategory object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbSynthesisCrpStaffingCategory was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbSynthesisCrpStaffingCategory save(PowbSynthesisCrpStaffingCategory powbSynthesisCrpStaffingCategory);
}
