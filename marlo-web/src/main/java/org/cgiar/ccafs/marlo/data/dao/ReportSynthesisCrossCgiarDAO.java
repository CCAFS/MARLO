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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCgiar;

import java.util.List;


public interface ReportSynthesisCrossCgiarDAO {

  /**
   * This method removes a specific reportSynthesisCrossCgiar value from the database.
   * 
   * @param reportSynthesisCrossCgiarId is the reportSynthesisCrossCgiar identifier.
   * @return true if the reportSynthesisCrossCgiar was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisCrossCgiar(long reportSynthesisCrossCgiarId);

  /**
   * This method validate if the reportSynthesisCrossCgiar identify with the given id exists in the system.
   * 
   * @param reportSynthesisCrossCgiarID is a reportSynthesisCrossCgiar identifier.
   * @return true if the reportSynthesisCrossCgiar exists, false otherwise.
   */
  public boolean existReportSynthesisCrossCgiar(long reportSynthesisCrossCgiarID);

  /**
   * This method gets a reportSynthesisCrossCgiar object by a given reportSynthesisCrossCgiar identifier.
   * 
   * @param reportSynthesisCrossCgiarID is the reportSynthesisCrossCgiar identifier.
   * @return a ReportSynthesisCrossCgiar object.
   */
  public ReportSynthesisCrossCgiar find(long id);

  /**
   * This method gets a list of reportSynthesisCrossCgiar that are active
   * 
   * @return a list from ReportSynthesisCrossCgiar null if no exist records
   */
  public List<ReportSynthesisCrossCgiar> findAll();


  /**
   * This method saves the information of the given reportSynthesisCrossCgiar
   * 
   * @param reportSynthesisCrossCgiar - is the reportSynthesisCrossCgiar object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisCrossCgiar was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisCrossCgiar save(ReportSynthesisCrossCgiar reportSynthesisCrossCgiar);
}
