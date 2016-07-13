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

import org.cgiar.ccafs.marlo.data.dao.mysql.SrfSloMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.SrfSlo;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(SrfSloMySQLDAO.class)
public interface SrfSloDAO {

  /**
   * This method removes a specific srfSlo value from the database.
   * 
   * @param srfSloId is the srfSlo identifier.
   * @return true if the srfSlo was successfully deleted, false otherwise.
   */
  public boolean deleteSrfSlo(long srfSloId);

  /**
   * This method validate if the srfSlo identify with the given id exists in the system.
   * 
   * @param srfSloID is a srfSlo identifier.
   * @return true if the srfSlo exists, false otherwise.
   */
  public boolean existSrfSlo(long srfSloID);

  /**
   * This method gets a srfSlo object by a given srfSlo identifier.
   * 
   * @param srfSloID is the srfSlo identifier.
   * @return a SrfSlo object.
   */
  public SrfSlo find(long id);

  /**
   * This method gets a list of srfSlo that are active
   * 
   * @return a list from SrfSlo null if no exist records
   */
  public List<SrfSlo> findAll();


  /**
   * This method saves the information of the given srfSlo
   * 
   * @param srfSlo - is the srfSlo object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the srfSlo was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(SrfSlo srfSlo);
}
