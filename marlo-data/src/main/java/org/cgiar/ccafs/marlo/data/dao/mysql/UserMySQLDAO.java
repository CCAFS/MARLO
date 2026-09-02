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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Christian Garcia - CIAT/CCAFS
 */
@Repository
public class UserMySQLDAO extends AbstractMarloDAO<User, Long> implements UserDAO {

  public static Logger LOG = LoggerFactory.getLogger(UserMySQLDAO.class);

  @Autowired
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
    Query<String> query = this.getSessionFactory().getCurrentSession().createQuery(queryString, String.class);
    String email = query.uniqueResult();
    return email;
  }

  @Override
  public List<Map<String, Object>> getPermission(int userId, String crpId) {
    List<Map<String, Object>> list = new ArrayList<>();
    StringBuilder builder = new StringBuilder();
    builder.append(" select * from user_permission");
    if (super.getTemTableUserId() == userId) {
      list = super.findCustomQuery(builder.toString());
    } else {
      list = super.excuteStoreProcedure("getPermissions", builder.toString(), userId);
    }
    if (crpId == null) {
      list = list.stream().filter(c -> c.get("permission").toString().contains("api:")).collect(Collectors.toList());
    } else {
      list = list.stream().filter(c -> c.get("crp_acronym").equals(crpId)).collect(Collectors.toList());
    }

    return list;
  }


  @Override
  public User getUser(Long id) {
    return super.find(User.class, id);
  }

  @Override
  public User getUser(String email) {
    // CHG-COGNITO-AUTH-001-T07 scope extension (OQ-9): normalize the INPUT with trim() + lowercase before
    // lookup. The previous implementation lowercased but did not trim, so a claim or credential arriving
    // with surrounding whitespace silently failed to resolve a row that exists -- an authentication
    // failure for a valid user. Verified in the dev database: 0 stored emails carry surrounding
    // whitespace, so trimming the input strictly widens matching and cannot change which row matches an
    // already-clean value.
    //
    // CORRECTED after the T07 audit: only the input is normalized -- the predicate below is
    // LOWER(u.email), not LOWER(TRIM(u.email)). users.email is unique on the RAW value (Users.hbm.xml),
    // so a TRIM() on the column could match two distinct rows (e.g. "a@x.org" and " a@x.org") and this
    // method has no ORDER BY / setMaxResults(1) to make choosing between them deterministic. Restricting
    // normalization to the input keeps this predicate's matching behavior byte-identical to the
    // pre-existing LOWER(email) comparison for every row already in the table (0 of which carry
    // whitespace), while still resolving a whitespace-carrying claim against an already-clean row.
    String normalizedEmail = normalizeEmail(email);
    if (normalizedEmail == null) {
      return null;
    }
    // Parameterized HQL replaces the previous concatenated native SQL (PS-17), following the pattern
    // ParameterMySQLDAO.getParameterByKey uses. The interpolated value used to be trusted because it came
    // from a form field; after T07 it can be the value of a signed token claim, and "trusted because the
    // token is signed" is the reasoning that fails the day the pool accepts an identity MARLO did not
    // anticipate.
    String queryString = "SELECT u FROM " + User.class.getName() + " u WHERE LOWER(u.email) = :email";
    Query<User> query = this.getSessionFactory().getCurrentSession().createQuery(queryString, User.class);
    query.setParameter("email", normalizedEmail);
    List<User> users = super.findAll(query);
    // The id is resolved and getUser(Long) -- super.find(User.class, id) -- is called again on purpose:
    // this preserves getUser(String)'s existing two-step return shape rather than returning the entity
    // this query already loaded. getUser(String) has callers outside this spec's scope, and changing its
    // return path was not authorized.
    if (!users.isEmpty()) {
      return this.getUser(users.get(0).getId());
    }
    return null;
  }

  /**
   * Normalizes an email for a case- and whitespace-insensitive lookup: {@code trim()} then lowercase
   * (CHG-COGNITO-AUTH-001-T07, OQ-9). Public and stateless so it can be unit-tested directly with no
   * {@code SessionFactory} needed -- including from {@code CognitoIdentityMappingTest}, whose test double
   * calls this same method so its normalization test reddens if this method regresses, rather than
   * re-deciding for itself what "the same row" means.
   *
   * @param email the raw email to normalize, possibly {@code null} or blank
   * @return the trimmed, lowercased email, or {@code null} when {@code email} is {@code null} or blank --
   *         there is no row a blank value could resolve
   */
  public static String normalizeEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      return null;
    }
    return email.trim().toLowerCase();
  }

  @Override
  public User getActiveSuperAdminUserByUsernameOccurrence() {
    String query = "select id from users where is_active = 1 and username is not null "
      + "and LOWER(username) like '%super%' and LOWER(username) like '%admin%' order by id asc limit 1";
    List<Map<String, Object>> users = super.findCustomQuery(query);
    if (users.isEmpty()) {
      String fallbackQuery = "select id from users where is_active = 1 and username is not null "
        + "and (LOWER(username) like '%super%' or LOWER(username) like '%admin%') order by id asc limit 1";
      users = super.findCustomQuery(fallbackQuery);
    }
    if (!users.isEmpty()) {
      return this.getUser(Long.parseLong(users.get(0).get("id").toString()));
    }
    return null;
  }

  @Override
  @Transactional
  public boolean saveLastLogin(User user) {
    // @Transactional is required so Spring starts a transaction and switches the OSIV session
    // from FlushMode.MANUAL to AUTO, flushing the update. Without it (Java 17 / Spring 5 setup)
    // the merge stays in memory and last_login / agree_terms are never persisted.
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
