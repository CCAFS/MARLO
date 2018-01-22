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

import org.cgiar.ccafs.marlo.data.model.SrfSloIdo;

import java.util.List;


public interface SrfSloIdoDAO {

  /**
   * This method removes a specific srfSloIdo value from the database.
   * 
   * @param srfSloIdoId is the srfSloIdo identifier.
   * @return true if the srfSloIdo was successfully deleted, false otherwise.
   */
  public void deleteSrfSloIdo(long srfSloIdoId);

  /**
   * This method validate if the srfSloIdo identify with the given id exists in the system.
   * 
   * @param srfSloIdoID is a srfSloIdo identifier.
   * @return true if the srfSloIdo exists, false otherwise.
   */
  public boolean existSrfSloIdo(long srfSloIdoID);

  /**
   * This method gets a srfSloIdo object by a given srfSloIdo identifier.
   * 
   * @param srfSloIdoID is the srfSloIdo identifier.
   * @return a SrfSloIdo object.
   */
  public SrfSloIdo find(long id);

  /**
   * This method gets a list of srfSloIdo that are active
   * 
   * @return a list from SrfSloIdo null if no exist records
   */
  public List<SrfSloIdo> findAll();


  /**
   * This method saves the information of the given srfSloIdo
   * 
   * @param srfSloIdo - is the srfSloIdo object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the srfSloIdo was
   *         updated
   *         or -1 is some error occurred.
   */
  public SrfSloIdo save(SrfSloIdo srfSloIdo);
}
