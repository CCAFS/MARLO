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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisNarrative;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisNarrativeManager {


  /**
   * This method removes a specific reportSynthesisNarrative value from the database.
   * 
   * @param reportSynthesisNarrativeId is the reportSynthesisNarrative identifier.
   * @return true if the reportSynthesisNarrative was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisNarrative(long reportSynthesisNarrativeId);


  /**
   * This method validate if the reportSynthesisNarrative identify with the given id exists in the system.
   * 
   * @param reportSynthesisNarrativeID is a reportSynthesisNarrative identifier.
   * @return true if the reportSynthesisNarrative exists, false otherwise.
   */
  public boolean existReportSynthesisNarrative(long reportSynthesisNarrativeID);


  /**
   * This method gets a list of reportSynthesisNarrative that are active
   * 
   * @return a list from ReportSynthesisNarrative null if no exist records
   */
  public List<ReportSynthesisNarrative> findAll();


  /**
   * This method gets a reportSynthesisNarrative object by a given reportSynthesisNarrative identifier.
   * 
   * @param reportSynthesisNarrativeID is the reportSynthesisNarrative identifier.
   * @return a ReportSynthesisNarrative object.
   */
  public ReportSynthesisNarrative getReportSynthesisNarrativeById(long reportSynthesisNarrativeID);

  /**
   * This method saves the information of the given reportSynthesisNarrative
   * 
   * @param reportSynthesisNarrative - is the reportSynthesisNarrative object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisNarrative was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisNarrative saveReportSynthesisNarrative(ReportSynthesisNarrative reportSynthesisNarrative);


}
