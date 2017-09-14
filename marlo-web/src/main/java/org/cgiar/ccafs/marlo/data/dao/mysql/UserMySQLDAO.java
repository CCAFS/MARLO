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

import org.cgiar.ccafs.marlo.data.dao.UserDAO;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Christian Garcia - CIAT/CCAFS
 */
public class UserMySQLDAO extends AbstractMarloDAO<User, Long> implements UserDAO {

  public static Logger LOG = LoggerFactory.getLogger(UserMySQLDAO.class);

  @Inject
  public UserMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public List<Map<String, Object>> getCenterPermission(int userId, String centerId) {
    String query =
      "select * from center_user_permissions where id=" + userId + " and center_acronym='" + centerId + "'";
    return super.findCustomQuery(query);
  }

  @Override
  public String getEmailByUsername(String username) {
    String queryString = "select email from " + User.class.getName() + " where username = '" + username + "'";
    Query query = this.getSessionFactory().getCurrentSession().createQuery(queryString);
    String email = (String) query.uniqueResult();
    return email;
  }

  @Override
  public List<Map<String, Object>> getPermission(int userId, String crpId) {
    String query = "select * from user_permissions where id=" + userId + " and crp_acronym='" + crpId + "'";
    return super.findCustomQuery(query);
  }


  @Override
  public User getUser(Long id) {
    return super.find(User.class, id);
  }

  @Override
  public User getUser(String email) {
    // validate the email on lower charters
    String query = "select * from users where LOWER(email)= '" + email.toLowerCase() + "'";
    List<Map<String, Object>> users = super.findCustomQuery(query);
    if (users.size() > 0) {
      return this.getUser(Long.parseLong(users.get(0).get("id").toString()));
    }
    return null;
  }

  @Override
  public boolean saveLastLogin(User user) {
    if (user.getId() == null) {
      super.saveEntity(user);
    } else {
      user = super.update(user);
    }
    return true;
  }

  @Override
  public User saveUser(User user) {
    if (user.getId() == null) {
      super.saveEntity(user);
    } else {
      user = super.update(user);
    }
    return user;
  }

  @Override
  public List<User> searchUser(String searchValue) {

    StringBuilder query = new StringBuilder();
    query.append("from " + User.class.getName());
    query.append(" WHERE ");
    query.append("first_name like '%" + searchValue + "%' ");
    query.append("OR last_name like '%" + searchValue + "%' ");
    query.append("OR email like '%" + searchValue + "%' ");
    query.append("GROUP BY email ");
    query.append("ORDER BY CASE ");
    query.append("WHEN email like '" + searchValue + "%' THEN 0 ");
    query.append("WHEN email like '% %" + searchValue + "% %' THEN 1 ");
    query.append("WHEN email like '%" + searchValue + "' THEN 2 ");
    query.append("WHEN last_name like '" + searchValue + "%' THEN 3 ");
    query.append("WHEN last_name like '% %" + searchValue + "% %' THEN 4 ");
    query.append("WHEN last_name like '%" + searchValue + "' THEN 5 ");
    query.append("WHEN first_name like '" + searchValue + "%' THEN 6 ");
    query.append("WHEN first_name like '% %" + searchValue + "% %' THEN 7 ");
    query.append("WHEN first_name like '%" + searchValue + "' THEN 8 ");
    query.append("ELSE 9 ");
    query.append("END, email, last_name, first_name ");

    return super.findAll(query.toString());
  }

  @Override
  public boolean verifiyCredentials(String email, String password) {
    String query = "from " + User.class.getName() + " where email= '" + email + "' and password= '" + password
      + "' and is_active = 1";
    List<User> users = super.findAll(query);
    if (users.size() > 0) {
      return true;
    }
    LOG.error("verifiyCredentials() > There was an error verifiying the credentials", email);
    return false;
  }

}
