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

import org.cgiar.ccafs.marlo.data.dao.mysql.IpLiaisonUserMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonUser;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(IpLiaisonUserMySQLDAO.class)
public interface IpLiaisonUserDAO {

  /**
   * This method removes a specific ipLiaisonUser value from the database.
   * 
   * @param ipLiaisonUserId is the ipLiaisonUser identifier.
   * @return true if the ipLiaisonUser was successfully deleted, false otherwise.
   */
  public boolean deleteIpLiaisonUser(long ipLiaisonUserId);

  /**
   * This method validate if the ipLiaisonUser identify with the given id exists in the system.
   * 
   * @param ipLiaisonUserID is a ipLiaisonUser identifier.
   * @return true if the ipLiaisonUser exists, false otherwise.
   */
  public boolean existIpLiaisonUser(long ipLiaisonUserID);

  /**
   * This method gets a ipLiaisonUser object by a given ipLiaisonUser identifier.
   * 
   * @param ipLiaisonUserID is the ipLiaisonUser identifier.
   * @return a IpLiaisonUser object.
   */
  public IpLiaisonUser find(long id);

  /**
   * This method gets a list of ipLiaisonUser that are active
   * 
   * @return a list from IpLiaisonUser null if no exist records
   */
  public List<IpLiaisonUser> findAll();


  /**
   * This method saves the information of the given ipLiaisonUser
   * 
   * @param ipLiaisonUser - is the ipLiaisonUser object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the ipLiaisonUser was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(IpLiaisonUser ipLiaisonUser);
}
