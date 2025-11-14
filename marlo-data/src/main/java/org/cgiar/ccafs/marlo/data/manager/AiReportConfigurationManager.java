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

import org.cgiar.ccafs.marlo.data.model.AiReportConfiguration;

import java.util.List;


/**
 * @author CCAFS
 */

public interface AiReportConfigurationManager {


  /**
   * This method removes a specific aiReportConfiguration value from the database.
   * 
   * @param aiReportConfigurationId is the aiReportConfiguration identifier.
   * @return true if the aiReportConfiguration was successfully deleted, false otherwise.
   */
  public void deleteAiReportConfiguration(long aiReportConfigurationId);


  /**
   * This method validate if the aiReportConfiguration identify with the given id exists in the system.
   * 
   * @param aiReportConfigurationID is a aiReportConfiguration identifier.
   * @return true if the aiReportConfiguration exists, false otherwise.
   */
  public boolean existAiReportConfiguration(long aiReportConfigurationID);


  /**
   * This method gets a list of aiReportConfiguration that are active
   * 
   * @return a list from AiReportConfiguration null if no exist records
   */
  public List<AiReportConfiguration> findAll();


  /**
   * This method gets a aiReportConfiguration object by a given aiReportConfiguration identifier.
   * 
   * @param aiReportConfigurationID is the aiReportConfiguration identifier.
   * @return a AiReportConfiguration object.
   */
  public AiReportConfiguration getAiReportConfigurationById(long aiReportConfigurationID);

  /**
   * This method saves the information of the given aiReportConfiguration
   * 
   * @param aiReportConfiguration - is the aiReportConfiguration object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the aiReportConfiguration was
   *         updated
   *         or -1 is some error occurred.
   */
  public AiReportConfiguration saveAiReportConfiguration(AiReportConfiguration aiReportConfiguration);


}
