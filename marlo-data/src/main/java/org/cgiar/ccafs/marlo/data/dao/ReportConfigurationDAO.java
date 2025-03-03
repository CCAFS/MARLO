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

import org.cgiar.ccafs.marlo.data.model.ReportConfiguration;

import java.util.List;


public interface ReportConfigurationDAO {

  /**
   * This method removes a specific reportConfiguration value from the database.
   * 
   * @param reportConfigurationId is the reportConfiguration identifier.
   * @return true if the reportConfiguration was successfully deleted, false otherwise.
   */
  public void deleteReportConfiguration(long reportConfigurationId);

  /**
   * This method validate if the reportConfiguration identify with the given id exists in the system.
   * 
   * @param reportConfigurationID is a reportConfiguration identifier.
   * @return true if the reportConfiguration exists, false otherwise.
   */
  public boolean existReportConfiguration(long reportConfigurationID);

  /**
   * This method gets a reportConfiguration object by a given reportConfiguration identifier.
   * 
   * @param reportConfigurationID is the reportConfiguration identifier.
   * @return a ReportConfiguration object.
   */
  public ReportConfiguration find(long id);

  /**
   * This method gets a list of reportConfiguration that are active
   * 
   * @return a list from ReportConfiguration null if no exist records
   */
  public List<ReportConfiguration> findAll();


  /**
   * This method saves the information of the given reportConfiguration
   * 
   * @param reportConfiguration - is the reportConfiguration object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportConfiguration was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportConfiguration save(ReportConfiguration reportConfiguration);
}
