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

import org.cgiar.ccafs.marlo.data.model.PowbMonitoringEvaluationLearning;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface PowbMonitoringEvaluationLearningManager {


  /**
   * This method removes a specific powbMonitoringEvaluationLearning value from the database.
   * 
   * @param powbMonitoringEvaluationLearningId is the powbMonitoringEvaluationLearning identifier.
   * @return true if the powbMonitoringEvaluationLearning was successfully deleted, false otherwise.
   */
  public void deletePowbMonitoringEvaluationLearning(long powbMonitoringEvaluationLearningId);


  /**
   * This method validate if the powbMonitoringEvaluationLearning identify with the given id exists in the system.
   * 
   * @param powbMonitoringEvaluationLearningID is a powbMonitoringEvaluationLearning identifier.
   * @return true if the powbMonitoringEvaluationLearning exists, false otherwise.
   */
  public boolean existPowbMonitoringEvaluationLearning(long powbMonitoringEvaluationLearningID);


  /**
   * This method gets a list of powbMonitoringEvaluationLearning that are active
   * 
   * @return a list from PowbMonitoringEvaluationLearning null if no exist records
   */
  public List<PowbMonitoringEvaluationLearning> findAll();


  /**
   * This method gets a powbMonitoringEvaluationLearning object by a given powbMonitoringEvaluationLearning identifier.
   * 
   * @param powbMonitoringEvaluationLearningID is the powbMonitoringEvaluationLearning identifier.
   * @return a PowbMonitoringEvaluationLearning object.
   */
  public PowbMonitoringEvaluationLearning getPowbMonitoringEvaluationLearningById(long powbMonitoringEvaluationLearningID);

  /**
   * This method saves the information of the given powbMonitoringEvaluationLearning
   * 
   * @param powbMonitoringEvaluationLearning - is the powbMonitoringEvaluationLearning object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbMonitoringEvaluationLearning was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbMonitoringEvaluationLearning savePowbMonitoringEvaluationLearning(PowbMonitoringEvaluationLearning powbMonitoringEvaluationLearning);


}
