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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.QATokenAuthDAO;
import org.cgiar.ccafs.marlo.data.model.QATokenAuth;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
@Named
public class QATokenAuthMySQLDAO extends AbstractMarloDAO<QATokenAuth, Long> implements QATokenAuthDAO {


  @Inject
  public QATokenAuthMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteQATokenAuth(long qATokenAuthId) {
    QATokenAuth qATokenAuth = this.find(qATokenAuthId);
    this.update(qATokenAuth);
  }

  @Override
  public boolean existQATokenAuth(long qATokenAuthID) {
    QATokenAuth qATokenAuth = this.find(qATokenAuthID);
    if (qATokenAuth == null) {
      return false;
    }
    return true;

  }

  @Override
  public QATokenAuth find(long id) {
    return super.find(QATokenAuth.class, id);

  }

  @Override
  public List<QATokenAuth> findAll() {
    String query = "from " + QATokenAuth.class.getName();
    List<QATokenAuth> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public QATokenAuth generate(String name, String username, String email, String smoCode, String userId) {
    String query =
      "SELECT getQAToken('" + name + "','" + username + "','" + email + "','" + smoCode + "','" + userId + "')";
    return super.find(QATokenAuth.class, new Long((String) super.executeFunction(query)));
  }


  @Override
  public QATokenAuth save(QATokenAuth qATokenAuth) {
    if (qATokenAuth.getId() == null) {
      super.saveEntity(qATokenAuth);
    } else {
      qATokenAuth = super.update(qATokenAuth);
    }


    return qATokenAuth;
  }


}