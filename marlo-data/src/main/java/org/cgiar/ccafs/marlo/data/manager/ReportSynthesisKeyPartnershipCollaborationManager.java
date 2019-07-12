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
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaboration;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisKeyPartnershipCollaborationManager {


  /**
   * This method removes a specific reportSynthesisKeyPartnershipCollaboration value from the database.
   * 
   * @param reportSynthesisKeyPartnershipCollaborationId is the reportSynthesisKeyPartnershipCollaboration identifier.
   * @return true if the reportSynthesisKeyPartnershipCollaboration was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisKeyPartnershipCollaboration(long reportSynthesisKeyPartnershipCollaborationId);


  /**
   * This method validate if the reportSynthesisKeyPartnershipCollaboration identify with the given id exists in the
   * system.
   * 
   * @param reportSynthesisKeyPartnershipCollaborationID is a reportSynthesisKeyPartnershipCollaboration identifier.
   * @return true if the reportSynthesisKeyPartnershipCollaboration exists, false otherwise.
   */
  public boolean existReportSynthesisKeyPartnershipCollaboration(long reportSynthesisKeyPartnershipCollaborationID);


  /**
   * This method gets a list of reportSynthesisKeyPartnershipCollaboration that are active
   * 
   * @return a list from ReportSynthesisKeyPartnershipCollaboration null if no exist records
   */
  public List<ReportSynthesisKeyPartnershipCollaboration> findAll();


  /**
   * This method gets a reportSynthesisKeyPartnershipCollaboration object by a given
   * reportSynthesisKeyPartnershipCollaboration identifier.
   * 
   * @param reportSynthesisKeyPartnershipCollaborationID is the reportSynthesisKeyPartnershipCollaboration identifier.
   * @return a ReportSynthesisKeyPartnershipCollaboration object.
   */
  public ReportSynthesisKeyPartnershipCollaboration
    getReportSynthesisKeyPartnershipCollaborationById(long reportSynthesisKeyPartnershipCollaborationID);

  /**
   * Get the table 9 Information to load in the word document.
   * 
   * @param flagships - The list of the liaison institution of the flagships
   * @param pmu - The liaison institution of the PMU
   * @param phase - The phase for get the information
   * @return - A list of the CGIAR collaborations that include in the current AR synthesis
   */
  public List<ReportSynthesisKeyPartnershipCollaboration> getTable9(List<LiaisonInstitution> flagships,
    LiaisonInstitution pmu, Phase phase);

  /**
   * This method saves the information of the given reportSynthesisKeyPartnershipCollaboration
   * 
   * @param reportSynthesisKeyPartnershipCollaboration - is the reportSynthesisKeyPartnershipCollaboration object with
   *        the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         reportSynthesisKeyPartnershipCollaboration was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisKeyPartnershipCollaboration saveReportSynthesisKeyPartnershipCollaboration(
    ReportSynthesisKeyPartnershipCollaboration reportSynthesisKeyPartnershipCollaboration);


}
