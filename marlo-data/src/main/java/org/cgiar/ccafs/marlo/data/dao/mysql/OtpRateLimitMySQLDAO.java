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

import org.cgiar.ccafs.marlo.data.dao.OtpRateLimitDAO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;

/**
 * CHG-OTP-LOGIN-001-T04. Native statements on {@code otp_rate_limits} and {@code otp_cooldowns}.
 * <p>
 * There is no entity: the tables are throw-away counters that nothing joins, so this DAO talks to the session
 * directly instead of extending {@link AbstractMarloDAO}, whose generic and audit helpers presuppose an entity.
 * <p>
 * <b>One time source.</b> Every {@code DATETIME} value, written or compared, is a UTC text bound from the Java
 * {@link Instant} the caller gives, in {@code yyyy-MM-dd HH:mm:ss.SSS}. MySQL converts the text itself, so neither
 * the JVM time zone, the driver nor the database {@code NOW()} takes part. The cooldown value is read back as the
 * text the column stores and parsed as UTC, so the comparison is made in Java on the same wall clock.
 * <p>
 * <b>Query spaces.</b> A native statement with no synchronized query space makes Hibernate evict every second-level
 * cache region when the transaction ends. Each statement declares the table it touches, so a rate-limit hit never
 * empties the caches of the rest of the application.
 */
@Named
public class OtpRateLimitMySQLDAO implements OtpRateLimitDAO {

  private static final String WINDOWS_TABLE = "otp_rate_limits";
  private static final String COOLDOWNS_TABLE = "otp_cooldowns";

  private static final DateTimeFormatter UTC_DATETIME =
    DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS").withZone(ZoneOffset.UTC);

  // The text of a stored DATETIME: whole seconds and an optional fraction, whatever precision the column has.
  private static final DateTimeFormatter STORED_DATETIME = new DateTimeFormatterBuilder()
    .append(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")).optionalStart()
    .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).optionalEnd().toFormatter();

  // The scripted run on the throwaway database copy executes these statements, read from this file.
  private static final String INCREMENT_WINDOW_SQL = "INSERT INTO otp_rate_limits (bucket, window_start, hits) "
    + "VALUES (:bucket, :windowStart, 1) ON DUPLICATE KEY UPDATE hits = hits + 1";

  private static final String FIND_HITS_SQL =
    "SELECT hits FROM otp_rate_limits WHERE bucket = :bucket AND window_start = :windowStart";

  // An exclusive no-op upsert: a new row is written with the sentinel, an existing row is locked exclusively and left
  // as it is. Never INSERT IGNORE: on a duplicate it takes only a shared lock, and two sessions that then both ask
  // for the exclusive one deadlock on each other.
  private static final String LOCK_COOLDOWN_SQL = "INSERT INTO otp_cooldowns (bucket, last_request_at) "
    + "VALUES (:bucket, :sentinel) ON DUPLICATE KEY UPDATE last_request_at = last_request_at";

  // The value is read as the text the column stores, so the Java side never converts a DATETIME through a time zone.
  private static final String FIND_COOLDOWN_FOR_UPDATE_SQL =
    "SELECT CAST(last_request_at AS CHAR) FROM otp_cooldowns WHERE bucket = :bucket FOR UPDATE";

  private static final String UPDATE_COOLDOWN_SQL =
    "UPDATE otp_cooldowns SET last_request_at = :now WHERE bucket = :bucket";

  private static final String DELETE_WINDOWS_SQL = "DELETE FROM otp_rate_limits WHERE window_start < :olderThan";

  private static final String DELETE_COOLDOWNS_SQL = "DELETE FROM otp_cooldowns WHERE last_request_at < :olderThan";

  private final SessionFactory sessionFactory;

  @Inject
  public OtpRateLimitMySQLDAO(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private static String utc(Instant instant) {
    return UTC_DATETIME.format(instant);
  }

  /** A native statement on one table, scoped to it so no other cache region is evicted. */
  private NativeQuery<?> statement(String sql, String table) {
    NativeQuery<?> query = this.sessionFactory.getCurrentSession().createNativeQuery(sql);
    query.addSynchronizedQuerySpace(table);
    return query;
  }

  @Override
  public int deleteCooldownsBefore(Instant olderThan) {
    return this.statement(DELETE_COOLDOWNS_SQL, COOLDOWNS_TABLE).setParameter("olderThan", utc(olderThan))
      .executeUpdate();
  }

  @Override
  public int deleteWindowsBefore(Instant olderThan) {
    return this.statement(DELETE_WINDOWS_SQL, WINDOWS_TABLE).setParameter("olderThan", utc(olderThan))
      .executeUpdate();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Integer findHits(String bucket, Instant windowStart) {
    NativeQuery<Number> query = this.sessionFactory.getCurrentSession().createNativeQuery(FIND_HITS_SQL);
    query.addSynchronizedQuerySpace(WINDOWS_TABLE);
    List<Number> hits = query.setParameter("bucket", bucket).setParameter("windowStart", utc(windowStart)).list();
    return hits.isEmpty() || hits.get(0) == null ? null : Integer.valueOf(hits.get(0).intValue());
  }

  @Override
  public int incrementWindow(String bucket, Instant windowStart) {
    return this.statement(INCREMENT_WINDOW_SQL, WINDOWS_TABLE).setParameter("bucket", bucket)
      .setParameter("windowStart", utc(windowStart)).executeUpdate();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Instant findLastRequestAtForUpdate(String bucket) {
    NativeQuery<Object> query = this.sessionFactory.getCurrentSession().createNativeQuery(FIND_COOLDOWN_FOR_UPDATE_SQL);
    query.addSynchronizedQuerySpace(COOLDOWNS_TABLE);
    List<Object> stored = query.setParameter("bucket", bucket).list();
    if (stored.isEmpty() || stored.get(0) == null) {
      return null;
    }
    return LocalDateTime.parse(stored.get(0).toString(), STORED_DATETIME).toInstant(ZoneOffset.UTC);
  }

  @Override
  public int lockCooldown(String bucket, Instant sentinel) {
    return this.statement(LOCK_COOLDOWN_SQL, COOLDOWNS_TABLE).setParameter("bucket", bucket)
      .setParameter("sentinel", utc(sentinel)).executeUpdate();
  }

  @Override
  public int updateCooldown(String bucket, Instant now) {
    return this.statement(UPDATE_COOLDOWN_SQL, COOLDOWNS_TABLE).setParameter("now", utc(now))
      .setParameter("bucket", bucket).executeUpdate();
  }
}
