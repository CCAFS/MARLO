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

import org.cgiar.ccafs.marlo.data.manager.impl.MogSynthesyManagerImpl;
import org.cgiar.ccafs.marlo.data.model.MogSynthesy;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(MogSynthesyManagerImpl.class)
public interface MogSynthesyManager {


  /**
   * This method removes a specific mogSynthesy value from the database.
   * 
   * @param mogSynthesyId is the mogSynthesy identifier.
   * @return true if the mogSynthesy was successfully deleted, false otherwise.
   */
  public boolean deleteMogSynthesy(long mogSynthesyId);


  /**
   * This method validate if the mogSynthesy identify with the given id exists in the system.
   * 
   * @param mogSynthesyID is a mogSynthesy identifier.
   * @return true if the mogSynthesy exists, false otherwise.
   */
  public boolean existMogSynthesy(long mogSynthesyID);


  /**
   * This method gets a list of mogSynthesy that are active
   * 
   * @return a list from MogSynthesy null if no exist records
   */
  public List<MogSynthesy> findAll();


  /**
   * This method gets all the OutcomeSynthesis information by a given program id
   * 
   * @param projectID - is the Id of the project
   * @return a List of OutcomeSynthesis with the Information related with the programId
   */
  public List<MogSynthesy> getMogSynthesis(long programId);

  /**
   * This method gets all the MogSynthesis information by a given program id
   * 
   * @param projectID - is the Id of the project
   * @return a List of MogSynthesis with the Information related with the programId
   */
  public List<MogSynthesy> getMogSynthesisRegions(long midoutcome, int year);

  /**
   * This method gets a mogSynthesy object by a given mogSynthesy identifier.
   * 
   * @param mogSynthesyID is the mogSynthesy identifier.
   * @return a MogSynthesy object.
   */
  public MogSynthesy getMogSynthesyById(long mogSynthesyID);

  /**
   * This method saves the information of the given mogSynthesy
   * 
   * @param mogSynthesy - is the mogSynthesy object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the mogSynthesy was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveMogSynthesy(MogSynthesy mogSynthesy);


}
