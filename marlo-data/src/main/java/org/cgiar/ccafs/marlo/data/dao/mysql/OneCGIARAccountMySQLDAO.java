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

package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.OneCGIARAccountDAO;
import org.cgiar.ccafs.marlo.data.model.OneCGIARAccount;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class OneCGIARAccountMySQLDAO extends AbstractMarloDAO<OneCGIARAccount, Long> implements OneCGIARAccountDAO {

  @Inject
  public OneCGIARAccountMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public OneCGIARAccount getAccountByFinancialCode(String financialCode) {
    String query = "select oca from OneCGIARAccount oca where financialCode = :financialCode";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("financialCode", financialCode);

    List<OneCGIARAccount> results = super.findAll(createQuery);

    OneCGIARAccount oneCGIARAccount = (results != null && !results.isEmpty()) ? results.get(0) : null;

    return oneCGIARAccount;
  }

  @Override
  public OneCGIARAccount getAccountById(long id) {
    return super.find(OneCGIARAccount.class, id);
  }

  @Override
  public List<OneCGIARAccount> getAccountsByAccountType(long accountTypeId) {
    String query = "select oca from OneCGIARAccount oca where accountType.id = :accountTypeId";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("accountTypeId", accountTypeId);

    List<OneCGIARAccount> results = super.findAll(createQuery);

    return results;
  }

  @Override
  public List<OneCGIARAccount> getAccountsByParent(long parentId) {
    String query = "select oca from OneCGIARAccount oca where parentAccount.id = :parentId";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("parentId", parentId);

    List<OneCGIARAccount> results = super.findAll(createQuery);

    return results;
  }

  @Override
  public List<OneCGIARAccount> getAll() {
    String query = "from " + OneCGIARAccount.class.getName();
    List<OneCGIARAccount> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

}
