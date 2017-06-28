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

package org.cgiar.ccafs.marlo.data.dao.impl;

import org.cgiar.ccafs.marlo.data.dao.IUserDAO;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Christian Garcia - CIAT/CCAFS
 */
public class UserDAO implements IUserDAO {

  private StandardDAO dao;

  @Inject
  public UserDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public String getEmailByUsername(String username) {
    String query = "select email from " + User.class.getName() + " where username = '" + username + "'";
    String email = (String) dao.findSingleResult(String.class, query);
    return email;
  }

  @Override
  public List<Map<String, Object>> getPermission(int userId, String crpId) {
    String query = "select * from center_user_permissions where id=" + userId + " and center_acronym='" + crpId + "'";
    return dao.findCustomQuery(query);
  }

  @Override
  public User getUser(Long id) {
    return dao.find(User.class, id);
  }

  @Override
  public User getUser(String email) {
    String query = "from " + User.class.getName() + " where email= '" + email + "'";
    List<User> users = dao.findAll(query);
    if (users.size() > 0) {
      return users.get(0);
    }
    return null;
  }

  @Override
  public boolean saveLastLogin(User user) {
    if (user.getId() == null) {
      dao.save(user);
    } else {
      dao.update(user);
    }
    return true;
  }

  @Override
  public Long saveUser(User user) {
    if (user.getId() == null) {
      dao.save(user);
    } else {
      dao.update(user);
    }
    return user.getId();
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

    return dao.findAll(query.toString());
  }

}
