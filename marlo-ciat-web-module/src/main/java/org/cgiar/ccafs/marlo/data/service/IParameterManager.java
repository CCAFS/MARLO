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
package org.cgiar.ccafs.marlo.data.service;

import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.service.impl.ParameterManager;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(ParameterManager.class)
public interface IParameterManager {


  /**
   * This method removes a specific crpParameter value from the database.
   * 
   * @param crpParameterId is the crpParameter identifier.
   * @return true if the crpParameter was successfully deleted, false otherwise.
   */
  public boolean deleteCrpParameter(long crpParameterId);


  /**
   * This method validate if the crpParameter identify with the given id exists in the system.
   * 
   * @param crpParameterID is a crpParameter identifier.
   * @return true if the crpParameter exists, false otherwise.
   */
  public boolean existCrpParameter(long crpParameterID);


  /**
   * This method gets a list of crpParameter that are active
   * 
   * @return a list from CrpParameter null if no exist records
   */
  public List<Parameter> findAll();


  /**
   * This method gets a crpParameter object by a given crpParameter identifier.
   * 
   * @param crpParameterID is the crpParameter identifier.
   * @return a CrpParameter object.
   */
  public Parameter getCrpParameterById(long crpParameterID);

  /**
   * This method saves the information of the given crpParameter
   * 
   * @param crpParameter - is the crpParameter object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpParameter was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveCrpParameter(Parameter crpParameter);


}
