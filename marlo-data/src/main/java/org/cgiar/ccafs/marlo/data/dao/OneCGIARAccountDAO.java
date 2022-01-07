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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.OneCGIARAccount;

import java.util.List;

public interface OneCGIARAccountDAO {

  /**
   * This method removes a specific oneCGIARAccount value from the database.
   * 
   * @param oneCGIARAccountId is the oneCGIARAccount identifier.
   * @return true if the oneCGIARAccount was successfully deleted, false otherwise.
   */
  public void deleteOneCGIARAccount(long oneCGIARAccountId);

  /**
   * This method validate if the oneCGIARAccount identify with the given id exists in the system.
   * 
   * @param oneCGIARAccountID is a oneCGIARAccount identifier.
   * @return true if the oneCGIARAccount exists, false otherwise.
   */
  public boolean existOneCGIARAccount(long oneCGIARAccountID);

  /**
   * This method gets a OneCGIARAccount object by a given financialCode identifier.
   * 
   * @param financialCode is the account financialCode identifier.
   * @return a OneCGIARAccount object.
   */
  public OneCGIARAccount getAccountByFinancialCode(String financialCode);

  /**
   * This method gets a OneCGIARAccount object by a given identifier.
   * 
   * @param id is the account identifier.
   * @return a OneCGIARAccount object.
   */
  public OneCGIARAccount getAccountById(long id);

  /**
   * This method gets a list of OneCGIARAccount objects by a given account type identifier.
   * 
   * @param financialCode is the account type identifier.
   * @return a list from OneCGIARAccount; empty list if no records exists.
   */
  public List<OneCGIARAccount> getAccountsByAccountType(long accountTypeId);

  /**
   * This method gets a list of OneCGIARAccount objects by a given account parent identifier.
   * 
   * @param parentId is the account parent identifier.
   * @return a list from OneCGIARAccount; empty list if no records exists.
   */
  public List<OneCGIARAccount> getAccountsByParent(long parentId);

  /**
   * This method gets a list of oneCGIARAccount that are active
   * 
   * @return a list from OneCGIARAccount; null if no records exists
   */
  public List<OneCGIARAccount> getAll();

  /**
   * This method saves the information of the given oneCGIARAccount
   * 
   * @param oneCGIARAccount - is the oneCGIARAccount object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the oneCGIARAccount was
   *         updated
   *         or -1 is some error occurred.
   */
  public OneCGIARAccount save(OneCGIARAccount oneCGIARAccount);

}
