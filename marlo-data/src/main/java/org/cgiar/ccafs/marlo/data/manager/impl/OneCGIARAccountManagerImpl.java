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

package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.data.dao.OneCGIARAccountDAO;
import org.cgiar.ccafs.marlo.data.manager.OneCGIARAccountManager;
import org.cgiar.ccafs.marlo.data.model.OneCGIARAccount;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class OneCGIARAccountManagerImpl implements OneCGIARAccountManager {

  private OneCGIARAccountDAO oneCGIARAccountDAO;

  @Inject
  public OneCGIARAccountManagerImpl(OneCGIARAccountDAO oneCGIARAccountDAO) {
    super();
    this.oneCGIARAccountDAO = oneCGIARAccountDAO;
  }

  @Override
  public void deleteOneCGIARAccount(Long oneCGIARAccountId) {
    this.oneCGIARAccountDAO.deleteOneCGIARAccount(oneCGIARAccountId.longValue());
  }

  @Override
  public boolean existOneCGIARAccount(Long oneCGIARAccountID) {
    return this.oneCGIARAccountDAO.existOneCGIARAccount(oneCGIARAccountID.longValue());
  }

  @Override
  public OneCGIARAccount getAccountByFinancialCode(String financialCode) {
    return this.oneCGIARAccountDAO.getAccountByFinancialCode(financialCode);
  }

  @Override
  public OneCGIARAccount getAccountById(Long id) {
    return this.oneCGIARAccountDAO.getAccountById(id.longValue());
  }

  @Override
  public List<OneCGIARAccount> getAccountsByAccountType(Long accountTypeId) {
    return this.oneCGIARAccountDAO.getAccountsByAccountType(accountTypeId.longValue());
  }

  @Override
  public List<OneCGIARAccount> getAccountsByParent(Long parentId) {
    return this.oneCGIARAccountDAO.getAccountsByParent(parentId.longValue());
  }

  @Override
  public List<OneCGIARAccount> getAll() {
    return this.oneCGIARAccountDAO.getAll();
  }

  @Override
  public OneCGIARAccount save(OneCGIARAccount oneCGIARAccount) {
    return this.oneCGIARAccountDAO.save(oneCGIARAccount);
  }

}
