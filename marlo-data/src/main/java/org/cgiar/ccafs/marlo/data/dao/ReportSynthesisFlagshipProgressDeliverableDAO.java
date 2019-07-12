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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressDeliverable;

import java.util.List;


public interface ReportSynthesisFlagshipProgressDeliverableDAO {

  /**
   * This method removes a specific reportSynthesisFlagshipProgressDeliverable value from the database.
   * 
   * @param reportSynthesisFlagshipProgressDeliverableId is the reportSynthesisFlagshipProgressDeliverable identifier.
   * @return true if the reportSynthesisFlagshipProgressDeliverable was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisFlagshipProgressDeliverable(long reportSynthesisFlagshipProgressDeliverableId);

  /**
   * This method validate if the reportSynthesisFlagshipProgressDeliverable identify with the given id exists in the system.
   * 
   * @param reportSynthesisFlagshipProgressDeliverableID is a reportSynthesisFlagshipProgressDeliverable identifier.
   * @return true if the reportSynthesisFlagshipProgressDeliverable exists, false otherwise.
   */
  public boolean existReportSynthesisFlagshipProgressDeliverable(long reportSynthesisFlagshipProgressDeliverableID);

  /**
   * This method gets a reportSynthesisFlagshipProgressDeliverable object by a given reportSynthesisFlagshipProgressDeliverable identifier.
   * 
   * @param reportSynthesisFlagshipProgressDeliverableID is the reportSynthesisFlagshipProgressDeliverable identifier.
   * @return a ReportSynthesisFlagshipProgressDeliverable object.
   */
  public ReportSynthesisFlagshipProgressDeliverable find(long id);

  /**
   * This method gets a list of reportSynthesisFlagshipProgressDeliverable that are active
   * 
   * @return a list from ReportSynthesisFlagshipProgressDeliverable null if no exist records
   */
  public List<ReportSynthesisFlagshipProgressDeliverable> findAll();


  /**
   * This method saves the information of the given reportSynthesisFlagshipProgressDeliverable
   * 
   * @param reportSynthesisFlagshipProgressDeliverable - is the reportSynthesisFlagshipProgressDeliverable object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisFlagshipProgressDeliverable was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisFlagshipProgressDeliverable save(ReportSynthesisFlagshipProgressDeliverable reportSynthesisFlagshipProgressDeliverable);
}
