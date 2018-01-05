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

import org.cgiar.ccafs.marlo.data.model.Parameter;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ParameterManager {


  /**
   * This method removes a specific parameter value from the database.
   * 
   * @param parameterId is the parameter identifier.
   * @return true if the parameter was successfully deleted, false otherwise.
   */
  public void deleteParameter(long parameterId);


  /**
   * This method validate if the parameter identify with the given id exists in the system.
   * 
   * @param parameterID is a parameter identifier.
   * @return true if the parameter exists, false otherwise.
   */
  public boolean existParameter(long parameterID);


  /**
   * This method gets a list of parameter that are active
   * 
   * @return a list from Parameter null if no exist records
   */
  public List<Parameter> findAll();


  /**
   * This method gets a parameter object by a given parameter identifier.
   * 
   * @param parameterID is the parameter identifier.
   * @return a Parameter object.
   */
  public Parameter getParameterById(long parameterID);

  public Parameter getParameterByKey(String key, long globalUnitId);

  /**
   * This method saves the information of the given parameter
   * 
   * @param parameter - is the parameter object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the parameter was
   *         updated
   *         or -1 is some error occurred.
   */
  public Parameter saveParameter(Parameter parameter);


}
