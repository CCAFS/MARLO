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

import org.cgiar.ccafs.marlo.data.dao.mysql.CrpPpaPartnerMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@ImplementedBy(CrpPpaPartnerMySQLDAO.class)
public interface CrpPpaPartnerDAO {

  /**
   * This method removes a specific crpPpaPartner value from the database.
   * 
   * @param crpPpaPartnerId is the CrpPpaPartner identifier.
   * @return true if the crpPpaPartner was successfully deleted, false otherwise.
   */
  public boolean deleteCrpPpaPartner(long crpPpaPartnerId);

  /**
   * This method validate if the crpPpaPartner identify with the given id exists in the system.
   * 
   * @param crpPpaPartnerID is a CrpParameter identifier.
   * @return true if the crpPpaPartner exists, false otherwise.
   */
  public boolean existCrpPpaPartner(long crpPpaPartnerId);

  /**
   * This method gets a crpPpaPartner object by a given crpPpaPartner identifier.
   * 
   * @param crpPpaPartnerID is the CrpPpaPartner identifier.
   * @return a crpPpaPartner object.
   */
  public CrpPpaPartner find(long id);

  /**
   * This method gets a list of crpPpaPartner that are active
   * 
   * @return a list from CrpPpaPartner null if no exist records
   */
  public List<CrpPpaPartner> findAll();

  /**
   * This method saves the information of the given crpPpaPartner
   * 
   * @param CrpPpaPartner - is the crpPpaPartner object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpPpaPartner was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CrpPpaPartner crpPpaPartner);

}
