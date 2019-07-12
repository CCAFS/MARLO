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

import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternal;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisKeyPartnershipExternalManager {


  /**
   * This method removes a specific reportSynthesisKeyPartnershipExternal value from the database.
   * 
   * @param reportSynthesisKeyPartnershipExternalId is the reportSynthesisKeyPartnershipExternal identifier.
   * @return true if the reportSynthesisKeyPartnershipExternal was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisKeyPartnershipExternal(long reportSynthesisKeyPartnershipExternalId);


  /**
   * This method validate if the reportSynthesisKeyPartnershipExternal identify with the given id exists in the system.
   * 
   * @param reportSynthesisKeyPartnershipExternalID is a reportSynthesisKeyPartnershipExternal identifier.
   * @return true if the reportSynthesisKeyPartnershipExternal exists, false otherwise.
   */
  public boolean existReportSynthesisKeyPartnershipExternal(long reportSynthesisKeyPartnershipExternalID);


  /**
   * This method gets a list of reportSynthesisKeyPartnershipExternal that are active
   * 
   * @return a list from ReportSynthesisKeyPartnershipExternal null if no exist records
   */
  public List<ReportSynthesisKeyPartnershipExternal> findAll();


  /**
   * This method gets a reportSynthesisKeyPartnershipExternal object by a given reportSynthesisKeyPartnershipExternal
   * identifier.
   * 
   * @param reportSynthesisKeyPartnershipExternalID is the reportSynthesisKeyPartnershipExternal identifier.
   * @return a ReportSynthesisKeyPartnershipExternal object.
   */
  public ReportSynthesisKeyPartnershipExternal
    getReportSynthesisKeyPartnershipExternalById(long reportSynthesisKeyPartnershipExternalID);

  /**
   * Get the table 8 Information to load in the word document.
   * 
   * @param flagships - The list of the liaison institution of the flagships
   * @param pmu - The liaison institution of the PMU
   * @param phase - The phase for get the information
   * @return - A list of the key external partnerships that include in the current AR synthesis
   */
  public List<ReportSynthesisKeyPartnershipExternal> getTable8(List<LiaisonInstitution> flagships,
    LiaisonInstitution pmu, Phase phase);

  /**
   * This method saves the information of the given reportSynthesisKeyPartnershipExternal
   * 
   * @param reportSynthesisKeyPartnershipExternal - is the reportSynthesisKeyPartnershipExternal object with the new
   *        information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         reportSynthesisKeyPartnershipExternal was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisKeyPartnershipExternal saveReportSynthesisKeyPartnershipExternal(
    ReportSynthesisKeyPartnershipExternal reportSynthesisKeyPartnershipExternal);


}
