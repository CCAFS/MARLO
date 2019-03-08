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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExpenditureCategory;

import java.util.List;


public interface ReportSynthesisExpenditureCategoryDAO {

  /**
   * This method removes a specific reportSynthesisExpenditureCategory value from the database.
   * 
   * @param reportSynthesisExpenditureCategoryId is the reportSynthesisExpenditureCategory identifier.
   * @return true if the reportSynthesisExpenditureCategory was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisExpenditureCategory(long reportSynthesisExpenditureCategoryId);

  /**
   * This method validate if the reportSynthesisExpenditureCategory identify with the given id exists in the system.
   * 
   * @param reportSynthesisExpenditureCategoryID is a reportSynthesisExpenditureCategory identifier.
   * @return true if the reportSynthesisExpenditureCategory exists, false otherwise.
   */
  public boolean existReportSynthesisExpenditureCategory(long reportSynthesisExpenditureCategoryID);

  /**
   * This method gets a reportSynthesisExpenditureCategory object by a given reportSynthesisExpenditureCategory identifier.
   * 
   * @param reportSynthesisExpenditureCategoryID is the reportSynthesisExpenditureCategory identifier.
   * @return a ReportSynthesisExpenditureCategory object.
   */
  public ReportSynthesisExpenditureCategory find(long id);

  /**
   * This method gets a list of reportSynthesisExpenditureCategory that are active
   * 
   * @return a list from ReportSynthesisExpenditureCategory null if no exist records
   */
  public List<ReportSynthesisExpenditureCategory> findAll();


  /**
   * This method saves the information of the given reportSynthesisExpenditureCategory
   * 
   * @param reportSynthesisExpenditureCategory - is the reportSynthesisExpenditureCategory object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisExpenditureCategory was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisExpenditureCategory save(ReportSynthesisExpenditureCategory reportSynthesisExpenditureCategory);
}
