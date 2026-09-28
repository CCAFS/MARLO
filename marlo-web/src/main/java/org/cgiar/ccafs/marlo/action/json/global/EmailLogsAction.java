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

package org.cgiar.ccafs.marlo.action.json.global;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.EmailLogManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.EmailLog;
import org.cgiar.ccafs.marlo.data.model.EmailLogSearch;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;

/**
 * One page of the table of System Admin -> Emails, in the shape DataTables expects from a server-side source. Only
 * the columns of the table are read: the message and the attachment come one email at a time from
 * EmailLogDetailAction.
 * It also reads the filters of the page from a request, so the table and the resend of SendFailEmailAction work on
 * the same rows. An unreadable value is ignored rather than rejected: the filters come from the page's own controls.
 */
public class EmailLogsAction extends BaseAction {

  private static final long serialVersionUID = -2981735400116219342L;

  private static final int DEFAULT_PAGE = 20;

  private static final int MAX_PAGE = 100;

  /** The value of the global unit and section filters that asks for the rows without one. */
  static final String NOT_RECORDED = "none";

  /**
   * @return the day, or null when the value is not a real yyyy-MM-dd date. A date input accepts years of up to six
   *         digits, and date_email is a DATETIME, which ends at 9999.
   */
  private static Date day(String value) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    format.setLenient(false);
    try {
      Date date = format.parse(value);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      int year = calendar.get(Calendar.YEAR);
      return year >= 1000 && year <= 9999 ? date : null;
    } catch (ParseException e) {
      return null;
    }
  }

  /**
   * @return the value as a positive id, or null when it is not one. StringUtils.isNumeric alone is not enough: it
   *         accepts digit strings too long for a long, and Long.parseLong would then throw.
   */
  static Long idValue(String value) {
    if (!StringUtils.isNumeric(value)) {
      return null;
    }
    try {
      long id = Long.parseLong(value);
      return id > 0 ? id : null;
    } catch (NumberFormatException e) {
      return null;
    }
  }

  static int intValue(Map<String, Parameter> parameters, String name, int fallback) {
    try {
      return Integer.parseInt(value(parameters, name));
    } catch (NumberFormatException e) {
      return fallback;
    }
  }

  /**
   * @param parameters the request parameters.
   * @return the filters and the order they describe; the caller sets whether the sent or the not sent emails apply.
   */
  static EmailLogSearch read(Map<String, Parameter> parameters) {
    EmailLogSearch search = new EmailLogSearch();

    String globalUnit = value(parameters, "globalUnit");
    if (NOT_RECORDED.equals(globalUnit)) {
      search.setWithoutGlobalUnit(true);
    } else {
      search.setGlobalUnitId(idValue(globalUnit));
    }

    String source = value(parameters, "source");
    if (NOT_RECORDED.equals(source)) {
      search.setWithoutSourceAction(true);
    } else if (!source.isEmpty()) {
      search.setSourceAction(source);
    }

    Date from = day(value(parameters, "from"));
    search.setFrom(from);
    Date to = day(value(parameters, "to"));
    if (to != null) {
      // The "to" day is included, so the bound is the start of the next one.
      Calendar until = Calendar.getInstance();
      until.setTime(to);
      until.add(Calendar.DAY_OF_MONTH, 1);
      search.setUntil(until.getTime());
    }

    String text = value(parameters, "search");
    search.setText(text.isEmpty() ? null : text);

    try {
      search.setOrder(EmailLogSearch.Order.valueOf(value(parameters, "orderColumn")));
    } catch (IllegalArgumentException e) {
      search.setOrder(EmailLogSearch.Order.DATE);
    }
    search.setAscending("asc".equals(value(parameters, "orderDir")));
    return search;
  }

  /**
   * @return the trimmed value of the parameter, or an empty string when it is missing.
   */
  static String value(Map<String, Parameter> parameters, String name) {
    Parameter parameter = parameters.get(name);
    if (parameter == null || !parameter.isDefined() || parameter.getValue() == null) {
      return "";
    }
    return parameter.getValue().trim();
  }

  static Map<Long, String> acronyms(GlobalUnitManager globalUnitManager) {
    Map<Long, String> acronyms = new HashMap<>();
    List<GlobalUnit> globalUnits = globalUnitManager.findAll();
    if (globalUnits != null) {
      for (GlobalUnit globalUnit : globalUnits) {
        acronyms.put(globalUnit.getId(), globalUnit.getAcronym());
      }
    }
    return acronyms;
  }

  private final EmailLogManager emailLogManager;

  private final GlobalUnitManager globalUnitManager;

  private Map<String, Object> response;

  @Inject
  public EmailLogsAction(APConfig config, EmailLogManager emailLogManager, GlobalUnitManager globalUnitManager) {
    super(config);
    this.emailLogManager = emailLogManager;
    this.globalUnitManager = globalUnitManager;
  }

  private long countSent(boolean sent) {
    EmailLogSearch search = new EmailLogSearch();
    search.setSent(sent);
    return emailLogManager.count(search);
  }

  @Override
  public String execute() throws Exception {
    // The json package cannot use superAdminStack, which lives in marlo-default.
    if (!this.canAccessSuperAdmin()) {
      ServletActionContext.getResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
      return NONE;
    }
    Map<String, Parameter> parameters = this.getParameters();
    boolean sent = "sent".equals(value(parameters, "status"));
    EmailLogSearch search = read(parameters);
    search.setSent(sent);

    int start = Math.max(0, intValue(parameters, "start", 0));
    int length = intValue(parameters, "length", DEFAULT_PAGE);
    if (length <= 0 || length > MAX_PAGE) {
      length = DEFAULT_PAGE;
    }

    long sentCount = this.countSent(true);
    long notSentCount = this.countSent(false);
    Map<Long, String> acronyms = acronyms(globalUnitManager);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    List<Map<String, Object>> data = new ArrayList<>();
    for (EmailLog emailLog : emailLogManager.findSummaries(search, start, length)) {
      Map<String, Object> row = new HashMap<>();
      row.put("id", emailLog.getId());
      row.put("date", emailLog.getDate() == null ? null : dateFormat.format(emailLog.getDate()));
      row.put("subject", emailLog.getSubject());
      row.put("to", emailLog.getTo());
      row.put("globalUnit", emailLog.getGlobalUnitId() == null ? null : acronyms.get(emailLog.getGlobalUnitId()));
      row.put("source", emailLog.getSourceAction());
      row.put("tried", emailLog.getTried());
      row.put("error", emailLog.getError());
      data.add(row);
    }

    Map<String, Object> counts = new HashMap<>();
    counts.put("sent", sentCount);
    counts.put("notSent", notSentCount);

    response = new HashMap<>();
    response.put("draw", intValue(parameters, "draw", 0));
    response.put("recordsTotal", sent ? sentCount : notSentCount);
    response.put("recordsFiltered", emailLogManager.count(search));
    response.put("data", data);
    response.put("counts", counts);
    return SUCCESS;
  }

  public Map<String, Object> getResponse() {
    return response;
  }
}
