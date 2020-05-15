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

import org.cgiar.ccafs.marlo.data.model.BiParameters;

import java.util.List;


/**
 * @author Luis Benavides - CIAT/CCAFS
 */
public interface BiParametersManager {


  /**
   * This method removes a specific biParameters value from the database.
   * 
   * @param biParametersId is the biParameters identifier.
   * @return true if the biParameters was successfully deleted, false otherwise.
   */
  public void deleteBiParameters(long biParametersId);


  /**
   * This method validate if the biParameters identify with the given id exists in the system.
   * 
   * @param biParametersID is a biParameters identifier.
   * @return true if the biParameters exists, false otherwise.
   */
  public boolean existBiParameters(long biParametersID);


  /**
   * This method gets a list of biParameters that are active
   * 
   * @return a list from BiParameters null if no exist records
   */
  public List<BiParameters> findAll();


  /**
   * This method gets a biParameters object by a given biParameters identifier.
   * 
   * @param biParametersID is the biParameters identifier.
   * @return a BiParameters object.
   */
  public BiParameters getBiParametersById(long biParametersID);

  /**
   * This method saves the information of the given biParameters
   * 
   * @param biParameters - is the biParameters object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the biParameters was
   *         updated
   *         or -1 is some error occurred.
   */
  public BiParameters saveBiParameters(BiParameters biParameters);


}
