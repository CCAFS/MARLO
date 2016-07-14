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

import org.cgiar.ccafs.marlo.data.manager.impl.SrfIdoManagerImpl;
import org.cgiar.ccafs.marlo.data.model.SrfIdo;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(SrfIdoManagerImpl.class)
public interface SrfIdoManager {


  /**
   * This method removes a specific srfIdo value from the database.
   * 
   * @param srfIdoId is the srfIdo identifier.
   * @return true if the srfIdo was successfully deleted, false otherwise.
   */
  public boolean deleteSrfIdo(long srfIdoId);


  /**
   * This method validate if the srfIdo identify with the given id exists in the system.
   * 
   * @param srfIdoID is a srfIdo identifier.
   * @return true if the srfIdo exists, false otherwise.
   */
  public boolean existSrfIdo(long srfIdoID);


  /**
   * This method gets a list of srfIdo that are active
   * 
   * @return a list from SrfIdo null if no exist records
   */
  public List<SrfIdo> findAll();


  /**
   * This method gets a srfIdo object by a given srfIdo identifier.
   * 
   * @param srfIdoID is the srfIdo identifier.
   * @return a SrfIdo object.
   */
  public SrfIdo getSrfIdoById(long srfIdoID);

  /**
   * This method saves the information of the given srfIdo
   * 
   * @param srfIdo - is the srfIdo object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the srfIdo was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveSrfIdo(SrfIdo srfIdo);


}
