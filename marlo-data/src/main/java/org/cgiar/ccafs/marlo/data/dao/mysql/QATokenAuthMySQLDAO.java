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

import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;

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

  /**
   * A2-2461: the five values are <b>bound</b>, not concatenated into the statement.
   * <p>
   * They used to be interpolated into single-quoted SQL literals and run as a native query, so nothing
   * between this method and the database escaped anything. That was reachable: through
   * {@code POST /api/qatoken/} the {@code name} and {@code username} arrive in the request body and
   * {@code QATokenItem} checks only that they are non-empty -- {@code smocode} and {@code email} are
   * validated properly, those two are not -- so client-controlled text reached the statement intact.
   * <p>
   * The same defect also broke on legitimate data: {@code name} is {@code firstName + " " + lastName}, and
   * a surname carrying an apostrophe closed the literal early, raising {@code SQLGrammarException} out of
   * {@code QAReportsAction.prepare()}, which handles nothing -- so the QA reports page was unreachable for
   * those accounts. <b>Binding closes both at once:</b> the value can no longer alter the statement, and an
   * apostrophe is no longer part of its syntax.
   * <p>
   * {@code executeFunction} is deliberately not reused: it routes through
   * {@code AbstractMarloDAO.findCustomQuery(String)}, which takes a finished SQL string and offers no way to
   * bind anything. Rather than widen that shared method for one caller, the query is issued here with the
   * same {@code FlushMode.COMMIT} it sets, so how this call persists is unchanged -- the INSERT lives inside
   * the {@code getQAToken} function and is performed by the server as part of this statement.
   *
   * @param name the display name, as supplied by the caller
   * @param username the login, as supplied by the caller
   * @param email the caller's email
   * @param smoCode the Global Unit's SMO code
   * @param userId the acting user's id
   * @return the freshly generated token row, or {@code null} when the function returned nothing
   */
  @Override
  public QATokenAuth generate(String name, String username, String email, String smoCode, String userId) {
    NativeQuery<Object> query = this.getSessionFactory().getCurrentSession()
      .createSQLQuery("SELECT getQAToken(:name, :username, :email, :smoCode, :appUser)");
    query.setFlushMode(FlushMode.COMMIT);
    query.setParameter("name", name);
    query.setParameter("username", username);
    query.setParameter("email", email);
    query.setParameter("smoCode", smoCode);
    query.setParameter("appUser", userId);
    Object generatedId = query.uniqueResult();
    if (generatedId == null) {
      return null;
    }
    // The function is declared RETURNS text and returns @@identity, so the driver hands back a String.
    // Number is accepted too rather than assumed away: a driver or column-type change would otherwise turn a
    // working call into a ClassCastException, and the id is the only thing this method needs.
    Long id = generatedId instanceof Number ? Long.valueOf(((Number) generatedId).longValue())
      : Long.valueOf(generatedId.toString().trim());
    return super.find(QATokenAuth.class, id);
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