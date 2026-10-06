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

import org.cgiar.ccafs.marlo.data.dao.EmailLogDAO;
import org.cgiar.ccafs.marlo.data.model.EmailLog;
import org.cgiar.ccafs.marlo.data.model.EmailLogSearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

@Named
public class EmailLogMySQLDAO extends AbstractMarloDAO<EmailLog, Long> implements EmailLogDAO {


  @Inject
  public EmailLogMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  private static final String SUMMARY_COLUMNS =
    "select e.id, e.date, e.subject, e.to, e.globalUnitId, e.sourceAction, e.tried, e.error, e.succes, e.messageID";

  // Whitelisted so the order requested by the page never reaches the query as text.
  private static final Map<EmailLogSearch.Order, String> ORDER_COLUMNS = new EnumMap<>(EmailLogSearch.Order.class);

  static {
    ORDER_COLUMNS.put(EmailLogSearch.Order.DATE, "e.date");
    ORDER_COLUMNS.put(EmailLogSearch.Order.SUBJECT, "e.subject");
    ORDER_COLUMNS.put(EmailLogSearch.Order.TO, "e.to");
    ORDER_COLUMNS.put(EmailLogSearch.Order.GLOBAL_UNIT, "e.globalUnitId");
    ORDER_COLUMNS.put(EmailLogSearch.Order.SOURCE, "e.sourceAction");
    ORDER_COLUMNS.put(EmailLogSearch.Order.TRIED, "e.tried");
  }

  /**
   * Builds the where clause of a search and collects its parameters.
   */
  private static String where(EmailLogSearch search, Map<String, Object> parameters) {
    StringBuilder where = new StringBuilder(" where 1 = 1");
    if (search.getSent() != null) {
      // A row whose outcome was never recorded counts as not sent, the list the retry works on.
      where.append(search.getSent() ? " and e.succes = true" : " and (e.succes is null or e.succes = false)");
    }
    if (search.isWithoutGlobalUnit()) {
      where.append(" and e.globalUnitId is null");
    } else if (search.getGlobalUnitId() != null) {
      where.append(" and e.globalUnitId = :globalUnitId");
      parameters.put("globalUnitId", search.getGlobalUnitId());
    }
    if (search.isWithoutSourceAction()) {
      where.append(" and e.sourceAction is null");
    } else if (search.getSourceAction() != null) {
      where.append(" and e.sourceAction = :sourceAction");
      parameters.put("sourceAction", search.getSourceAction());
    }
    if (search.getFrom() != null) {
      where.append(" and e.date >= :from");
      parameters.put("from", search.getFrom());
    }
    if (search.getUntil() != null) {
      where.append(" and e.date < :until");
      parameters.put("until", search.getUntil());
    }
    if (search.getText() != null && !search.getText().trim().isEmpty()) {
      where.append(" and (lower(e.subject) like :text escape '!' or lower(e.to) like :text escape '!'"
        + " or lower(e.cc) like :text escape '!' or lower(e.bbc) like :text escape '!')");
      // Locale.ROOT, so a Turkish default locale does not turn "I" into a dotless i that MySQL's lower() never gives.
      String escaped =
        search.getText().trim().toLowerCase(Locale.ROOT).replace("!", "!!").replace("%", "!%").replace("_", "!_");
      parameters.put("text", "%" + escaped + "%");
    }
    return where.toString();
  }

  private static String orderBy(EmailLogSearch search) {
    String direction = search.isAscending() ? " asc" : " desc";
    // The id breaks ties, so a page never repeats or skips a row of the previous one.
    return " order by " + ORDER_COLUMNS.get(search.getOrder()) + direction + ", e.id" + direction;
  }

  @Override
  public long count(EmailLogSearch search) {
    Map<String, Object> parameters = new HashMap<>();
    Query<Long> query = this.getSessionFactory().getCurrentSession()
      .createQuery("select count(e.id) from EmailLog e" + where(search, parameters), Long.class);
    parameters.forEach(query::setParameter);
    return query.uniqueResult();
  }

  @Override
  public List<Long> findGlobalUnitIds() {
    return this.getSessionFactory().getCurrentSession()
      .createQuery("select distinct e.globalUnitId from EmailLog e where e.globalUnitId is not null", Long.class)
      .list();
  }

  @Override
  public List<String> findSourceActions() {
    return this.getSessionFactory().getCurrentSession()
      .createQuery("select distinct e.sourceAction from EmailLog e where e.sourceAction is not null"
        + " order by e.sourceAction", String.class)
      .list();
  }

  @Override
  public List<EmailLog> findSummaries(EmailLogSearch search, int first, int max) {
    Map<String, Object> parameters = new HashMap<>();
    Query<Object[]> query = this.getSessionFactory().getCurrentSession()
      .createQuery(SUMMARY_COLUMNS + " from EmailLog e" + where(search, parameters) + orderBy(search), Object[].class);
    parameters.forEach(query::setParameter);
    query.setFirstResult(first);
    query.setMaxResults(max);
    List<EmailLog> summaries = new ArrayList<>();
    for (Object[] row : query.list()) {
      EmailLog summary = new EmailLog();
      summary.setId((Long) row[0]);
      summary.setDate((Date) row[1]);
      summary.setSubject((String) row[2]);
      summary.setTo((String) row[3]);
      summary.setGlobalUnitId((Long) row[4]);
      summary.setSourceAction((String) row[5]);
      summary.setTried((Integer) row[6]);
      summary.setError((String) row[7]);
      summary.setSucces((Boolean) row[8]);
      summary.setMessageID((String) row[9]);
      summaries.add(summary);
    }
    return summaries;
  }

  @Override
  public List<EmailLog> search(EmailLogSearch search) {
    Map<String, Object> parameters = new HashMap<>();
    Query<EmailLog> query = this.getSessionFactory().getCurrentSession()
      .createQuery("select e from EmailLog e" + where(search, parameters) + orderBy(search), EmailLog.class);
    parameters.forEach(query::setParameter);
    return query.list();
  }

  @Override
  public void deleteEmailLog(long emailLogId) {
    EmailLog emailLog = this.find(emailLogId);

    this.delete(emailLog);
  }

  @Override
  public boolean existEmailLog(long emailLogID) {
    EmailLog emailLog = this.find(emailLogID);
    if (emailLog == null) {
      return false;
    }
    return true;

  }

  @Override
  public EmailLog find(long id) {
    return super.find(EmailLog.class, id);

  }

  @Override
  public List<EmailLog> findAll() {
    String query = "from " + EmailLog.class.getName();
    List<EmailLog> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public EmailLog save(EmailLog emailLog) {
    if (emailLog.getId() == null) {
      super.saveEntity(emailLog);
    } else {
      emailLog = super.update(emailLog);
    }


    return emailLog;
  }


}