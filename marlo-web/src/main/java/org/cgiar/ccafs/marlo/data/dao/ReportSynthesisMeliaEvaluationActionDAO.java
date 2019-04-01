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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluationAction;

import java.util.List;


public interface ReportSynthesisMeliaEvaluationActionDAO {

  /**
   * This method removes a specific reportSynthesisMeliaEvaluationAction value from the database.
   * 
   * @param reportSynthesisMeliaEvaluationActionId is the reportSynthesisMeliaEvaluationAction identifier.
   * @return true if the reportSynthesisMeliaEvaluationAction was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisMeliaEvaluationAction(long reportSynthesisMeliaEvaluationActionId);

  /**
   * This method validate if the reportSynthesisMeliaEvaluationAction identify with the given id exists in the system.
   * 
   * @param reportSynthesisMeliaEvaluationActionID is a reportSynthesisMeliaEvaluationAction identifier.
   * @return true if the reportSynthesisMeliaEvaluationAction exists, false otherwise.
   */
  public boolean existReportSynthesisMeliaEvaluationAction(long reportSynthesisMeliaEvaluationActionID);

  /**
   * This method gets a reportSynthesisMeliaEvaluationAction object by a given reportSynthesisMeliaEvaluationAction identifier.
   * 
   * @param reportSynthesisMeliaEvaluationActionID is the reportSynthesisMeliaEvaluationAction identifier.
   * @return a ReportSynthesisMeliaEvaluationAction object.
   */
  public ReportSynthesisMeliaEvaluationAction find(long id);

  /**
   * This method gets a list of reportSynthesisMeliaEvaluationAction that are active
   * 
   * @return a list from ReportSynthesisMeliaEvaluationAction null if no exist records
   */
  public List<ReportSynthesisMeliaEvaluationAction> findAll();


  /**
   * This method saves the information of the given reportSynthesisMeliaEvaluationAction
   * 
   * @param reportSynthesisMeliaEvaluationAction - is the reportSynthesisMeliaEvaluationAction object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisMeliaEvaluationAction was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisMeliaEvaluationAction save(ReportSynthesisMeliaEvaluationAction reportSynthesisMeliaEvaluationAction);
}
