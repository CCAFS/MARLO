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

package org.cgiar.ccafs.marlo.rest.helldots;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * Pure translation between a HellDots serialized comment and the columns projected out of it.
 * Deliberately free of Spring, Hibernate and session state so it can be unit tested directly.
 */
public final class HelldotsProjection {

  public enum Action {
    UPSERT, SOFT_DELETE, UNKNOWN
  }

  public static final Set<String> STATUSES =
    Collections.unmodifiableSet(new HashSet<>(Arrays.asList("open", "in_progress", "in_review", "resolved")));

  public static final Set<String> TYPES =
    Collections.unmodifiableSet(new HashSet<>(Arrays.asList("bug", "suggestion", "question", "improvement")));

  public static final Set<String> PRIORITIES =
    Collections.unmodifiableSet(new HashSet<>(Arrays.asList("high", "medium", "low")));

  private static final Set<String> UPSERT_EVENTS = Collections.unmodifiableSet(new HashSet<>(
    Arrays.asList("comment:created", "comment:edited", "comment:status-changed", "comment:updated",
      "comment:anchor-lost", "reply:added", "reply:deleted", "reply:edited", "reaction:toggled")));

  private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

  public static Action actionFor(String eventType) {
    if (eventType == null) {
      return Action.UNKNOWN;
    }
    if ("comment:deleted".equals(eventType)) {
      return Action.SOFT_DELETE;
    }
    if (UPSERT_EVENTS.contains(eventType)) {
      return Action.UPSERT;
    }
    return Action.UNKNOWN;
  }

  public static String commentIdOf(Map<String, Object> comment) {
    return stringField(comment, "id");
  }

  /**
   * Parses an ISO-8601 timestamp as written by the widget. Returns null for an absent or unparseable value:
   * a clock the server does not control is not worth failing a write over.
   */
  public static Date dateField(Map<String, Object> comment, String key) {
    String raw = stringField(comment, key);
    if (raw == null || raw.isEmpty()) {
      return null;
    }
    SimpleDateFormat format = new SimpleDateFormat(ISO_FORMAT);
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    try {
      return format.parse(raw);
    } catch (ParseException e) {
      return null;
    }
  }

  public static Integer intField(Map<String, Object> comment, String key) {
    if (comment == null) {
      return null;
    }
    Object value = comment.get(key);
    if (value instanceof Number) {
      return Integer.valueOf(((Number) value).intValue());
    }
    return null;
  }

  /**
   * The widget records location.pathname, but a defensive strip keeps a query or fragment out of the
   * indexed column if a host ever passes a fuller URL.
   */
  public static String pathOf(String page) {
    if (page == null) {
      return null;
    }
    String path = page;
    int query = path.indexOf('?');
    if (query >= 0) {
      path = path.substring(0, query);
    }
    int fragment = path.indexOf('#');
    if (fragment >= 0) {
      path = path.substring(0, fragment);
    }
    return path;
  }

  public static String stringField(Map<String, Object> comment, String key) {
    if (comment == null) {
      return null;
    }
    Object value = comment.get(key);
    if (value == null) {
      return null;
    }
    return String.valueOf(value);
  }

  private HelldotsProjection() {
  }
}
