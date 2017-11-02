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

import org.cgiar.ccafs.marlo.data.dao.mysql.CrpIndicatorReportMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.CrpIndicatorReport;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CrpIndicatorReportMySQLDAO.class)
public interface CrpIndicatorReportDAO {

  /**
   * This method removes a specific crpIndicatorReport value from the database.
   * 
   * @param crpIndicatorReportId is the crpIndicatorReport identifier.
   * @return true if the crpIndicatorReport was successfully deleted, false otherwise.
   */
  public void deleteCrpIndicatorReport(long crpIndicatorReportId);

  /**
   * This method validate if the crpIndicatorReport identify with the given id exists in the system.
   * 
   * @param crpIndicatorReportID is a crpIndicatorReport identifier.
   * @return true if the crpIndicatorReport exists, false otherwise.
   */
  public boolean existCrpIndicatorReport(long crpIndicatorReportID);

  /**
   * This method gets a crpIndicatorReport object by a given crpIndicatorReport identifier.
   * 
   * @param crpIndicatorReportID is the crpIndicatorReport identifier.
   * @return a CrpIndicatorReport object.
   */
  public CrpIndicatorReport find(long id);

  /**
   * This method gets a list of crpIndicatorReport that are active
   * 
   * @return a list from CrpIndicatorReport null if no exist records
   */
  public List<CrpIndicatorReport> findAll();


  /**
   * Get the list of indicator's reports made by the leader and corresponding
   * to the given logframe.
   * 
   * @param leader
   * @return a list of IndicatorReport objects with the information.
   */
  public List<CrpIndicatorReport> getIndicatorReportsList(long leader, int year);

  /**
   * This method saves the information of the given crpIndicatorReport
   * 
   * @param crpIndicatorReport - is the crpIndicatorReport object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpIndicatorReport was
   *         updated
   *         or -1 is some error occurred.
   */
  public CrpIndicatorReport save(CrpIndicatorReport crpIndicatorReport);


}
