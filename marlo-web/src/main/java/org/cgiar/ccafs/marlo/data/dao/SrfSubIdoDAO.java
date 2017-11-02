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

import org.cgiar.ccafs.marlo.data.dao.mysql.SrfSubIdoMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(SrfSubIdoMySQLDAO.class)
public interface SrfSubIdoDAO {

  /**
   * This method removes a specific srfSubIdo value from the database.
   * 
   * @param srfSubIdoId is the srfSubIdo identifier.
   * @return true if the srfSubIdo was successfully deleted, false otherwise.
   */
  public void deleteSrfSubIdo(long srfSubIdoId);

  /**
   * This method validate if the srfSubIdo identify with the given id exists in the system.
   * 
   * @param srfSubIdoID is a srfSubIdo identifier.
   * @return true if the srfSubIdo exists, false otherwise.
   */
  public boolean existSrfSubIdo(long srfSubIdoID);

  /**
   * This method gets a srfSubIdo object by a given srfSubIdo identifier.
   * 
   * @param srfSubIdoID is the srfSubIdo identifier.
   * @return a SrfSubIdo object.
   */
  public SrfSubIdo find(long id);

  /**
   * This method gets a list of srfSubIdo that are active
   * 
   * @return a list from SrfSubIdo null if no exist records
   */
  public List<SrfSubIdo> findAll();


  /**
   * This method saves the information of the given srfSubIdo
   * 
   * @param srfSubIdo - is the srfSubIdo object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the srfSubIdo was
   *         updated
   *         or -1 is some error occurred.
   */
  public SrfSubIdo save(SrfSubIdo srfSubIdo);
}
