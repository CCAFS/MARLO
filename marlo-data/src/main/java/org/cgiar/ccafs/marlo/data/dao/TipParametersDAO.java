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

import org.cgiar.ccafs.marlo.data.model.TipParameters;

import java.util.List;


public interface TipParametersDAO {

  /**
   * This method removes a specific tipParameters value from the database.
   * 
   * @param tipParametersId is the tipParameters identifier.
   * @return true if the tipParameters was successfully deleted, false otherwise.
   */
  public void deleteTipParameters(long tipParametersId);

  /**
   * This method validate if the tipParameters identify with the given id exists in the system.
   * 
   * @param tipParametersID is a tipParameters identifier.
   * @return true if the tipParameters exists, false otherwise.
   */
  public boolean existTipParameters(long tipParametersID);

  /**
   * This method gets a tipParameters object by a given tipParameters identifier.
   * 
   * @param tipParametersID is the tipParameters identifier.
   * @return a TipParameters object.
   */
  public TipParameters find(long id);

  /**
   * This method gets a list of tipParameters that are active
   * 
   * @return a list from TipParameters null if no exist records
   */
  public List<TipParameters> findAll();


  /**
   * This method saves the information of the given tipParameters
   * 
   * @param tipParameters - is the tipParameters object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the tipParameters was
   *         updated
   *         or -1 is some error occurred.
   */
  public TipParameters save(TipParameters tipParameters);
}
