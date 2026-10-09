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

import org.cgiar.ccafs.marlo.data.dao.OtpChallengeDAO;
import org.cgiar.ccafs.marlo.data.model.OtpChallenge;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;

/**
 * CHG-OTP-LOGIN-001-T03. Native, conditional statements whose affected-row count is the answer (DD-3), and the
 * uncached allow-list read (DD-12).
 * <p>
 * <b>One time source.</b> Every {@code DATETIME} value, written or compared, is a UTC text bound from the Java
 * {@link Instant} the caller gives, in {@code yyyy-MM-dd HH:mm:ss.SSS}. MySQL converts the text itself, so neither
 * the JVM time zone, the driver nor the database {@code NOW()} takes part, and an application and a database in
 * different time zones cannot shift an expiry.
 * <p>
 * <b>Query spaces.</b> A native statement with no synchronized query space makes Hibernate evict every second-level
 * cache region when the transaction ends. The statements on {@code otp_challenges} declare that space, so a code
 * request never empties the caches of the other specificities.
 */
@Named
public class OtpChallengeMySQLDAO extends AbstractMarloDAO<OtpChallenge, Long> implements OtpChallengeDAO {

  private static final String TABLE = "otp_challenges";

  private static final DateTimeFormatter UTC_DATETIME =
    DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS").withZone(ZoneOffset.UTC);

  // The scripted run on the throwaway database copy executes these statements, read from this file.
  private static final String SUPERSEDE_SQL =
    "UPDATE otp_challenges SET consumed_at = :now WHERE email_hmac = :emailHmac AND consumed_at IS NULL";

  private static final String INSERT_SQL = "INSERT INTO otp_challenges "
    + "(nonce, email_hmac, global_unit_id, code_hmac, expires_at, attempts, consumed_at, created_at) "
    + "VALUES (:nonce, :emailHmac, :globalUnitId, :codeHmac, :expiresAt, 0, NULL, :now)";

  private static final String INCREMENT_ATTEMPTS_SQL = "UPDATE otp_challenges SET attempts = attempts + 1 "
    + "WHERE nonce = :nonce AND consumed_at IS NULL AND attempts < " + OtpChallenge.MAX_ATTEMPTS;

  private static final String FIND_ATTEMPTS_SQL = "SELECT attempts FROM otp_challenges WHERE nonce = :nonce";

  private static final String CONSUME_SQL = "UPDATE otp_challenges SET consumed_at = :now "
    + "WHERE nonce = :nonce AND consumed_at IS NULL AND attempts < " + OtpChallenge.MAX_ATTEMPTS
    + " AND expires_at > :now";

  private static final String DELETE_EXPIRED_SQL = "DELETE FROM otp_challenges WHERE expires_at < :olderThan";

  // An active custom_parameters row wins even when its value is NULL, so the CASE tests the row, not the value.
  private static final String ALLOW_LIST_SQL = "SELECT g.id, "
    + "CASE WHEN cp.id IS NOT NULL THEN cp.value ELSE p.default_value END "
    + "FROM global_units g "
    + "LEFT JOIN parameters p ON p.global_unit_type_id = g.global_unit_type_id AND p.`key` = :key "
    + "LEFT JOIN custom_parameters cp ON cp.parameter_id = p.id AND cp.global_unit_id = g.id AND cp.is_active = 1 "
    + "WHERE g.id IN (:globalUnitIds) ORDER BY g.id, cp.id";

  @Inject
  public OtpChallengeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  private static String utc(Instant instant) {
    return UTC_DATETIME.format(instant);
  }

  /** A native statement on otp_challenges, scoped to its table so no other cache region is evicted. */
  private NativeQuery<?> statement(String sql) {
    NativeQuery<?> query = this.getSessionFactory().getCurrentSession().createNativeQuery(sql);
    query.addSynchronizedQuerySpace(TABLE);
    return query;
  }

  @Override
  public int consume(String nonce, Instant now) {
    return this.statement(CONSUME_SQL).setParameter("nonce", nonce).setParameter("now", utc(now)).executeUpdate();
  }

  @Override
  public int deleteExpiredBefore(Instant olderThan) {
    return this.statement(DELETE_EXPIRED_SQL).setParameter("olderThan", utc(olderThan)).executeUpdate();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<Long, String> findAllowListValues(String key, Collection<Long> globalUnitIds) {
    // Not cacheable and no entity: the read goes straight to the tables, so a change applies on the next request
    // on every instance (DD-12, FN-002).
    NativeQuery<Object[]> query = this.getSessionFactory().getCurrentSession().createNativeQuery(ALLOW_LIST_SQL);
    List<Object[]> rows = query.setParameter("key", key).setParameterList("globalUnitIds", globalUnitIds).list();
    Map<Long, String> values = new HashMap<>();
    for (Object[] row : rows) {
      values.put(((Number) row[0]).longValue(), row[1] == null ? null : row[1].toString());
    }
    return values;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Integer findAttempts(String nonce) {
    NativeQuery<Number> query = this.getSessionFactory().getCurrentSession().createNativeQuery(FIND_ATTEMPTS_SQL);
    query.addSynchronizedQuerySpace(TABLE);
    List<Number> attempts = query.setParameter("nonce", nonce).list();
    return attempts.isEmpty() || attempts.get(0) == null ? null : Integer.valueOf(attempts.get(0).intValue());
  }

  @Override
  public OtpChallenge findByNonce(String nonce) {
    OtpChallenge challenge = this.getSessionFactory().getCurrentSession()
      .createQuery("select c from OtpChallenge c where c.nonce = :nonce", OtpChallenge.class)
      .setParameter("nonce", nonce).uniqueResult();
    if (challenge != null) {
      // Detached, so a later read of the same row in this session reads the database again, never this instance.
      this.getSessionFactory().getCurrentSession().evict(challenge);
    }
    return challenge;
  }

  @Override
  public int incrementAttempts(String nonce) {
    return this.statement(INCREMENT_ATTEMPTS_SQL).setParameter("nonce", nonce).executeUpdate();
  }

  @Override
  public int insert(String emailHmac, long globalUnitId, String nonce, String codeHmac, Instant expiresAt,
    Instant now) {
    return this.statement(INSERT_SQL).setParameter("nonce", nonce).setParameter("emailHmac", emailHmac)
      .setParameter("globalUnitId", globalUnitId).setParameter("codeHmac", codeHmac)
      .setParameter("expiresAt", utc(expiresAt)).setParameter("now", utc(now)).executeUpdate();
  }

  @Override
  public int supersede(String emailHmac, Instant now) {
    return this.statement(SUPERSEDE_SQL).setParameter("emailHmac", emailHmac).setParameter("now", utc(now))
      .executeUpdate();
  }
}
