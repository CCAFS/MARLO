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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HelldotsProjectionTest {

  @Test
  public void deletedEventSoftDeletes() {
    assertEquals(HelldotsProjection.Action.SOFT_DELETE, HelldotsProjection.actionFor("comment:deleted"));
  }

  @Test
  public void mutatingEventsUpsert() {
    String[] types = {"comment:created", "comment:edited", "comment:status-changed", "comment:updated",
      "comment:anchor-lost", "reply:added", "reply:deleted", "reply:edited", "reaction:toggled"};
    for (String type : types) {
      assertEquals(type, HelldotsProjection.Action.UPSERT, HelldotsProjection.actionFor(type));
    }
  }

  @Test
  public void unknownEventIsRejected() {
    assertEquals(HelldotsProjection.Action.UNKNOWN, HelldotsProjection.actionFor("comment:exploded"));
    assertEquals(HelldotsProjection.Action.UNKNOWN, HelldotsProjection.actionFor(null));
  }

  @Test
  public void stringFieldReadsAndTolerates() {
    Map<String, Object> comment = new HashMap<>();
    comment.put("status", "open");
    comment.put("type", null);
    assertEquals("open", HelldotsProjection.stringField(comment, "status"));
    assertNull(HelldotsProjection.stringField(comment, "type"));
    assertNull(HelldotsProjection.stringField(comment, "absent"));
  }

  @Test
  public void dateFieldParsesValidIsoTimestamp() {
    Map<String, Object> comment = new HashMap<>();
    comment.put("createdAt", "2026-08-24T10:14:00.000Z");

    Calendar expected = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    expected.clear();
    expected.set(2026, Calendar.AUGUST, 24, 10, 14, 0);
    expected.set(Calendar.MILLISECOND, 0);

    Date parsed = HelldotsProjection.dateField(comment, "createdAt");
    assertEquals(expected.getTimeInMillis(), parsed.getTime());
  }

  @Test
  public void dateFieldReturnsNullForMalformedAbsentOrEmpty() {
    Map<String, Object> comment = new HashMap<>();
    comment.put("createdAt", "not-a-date");
    comment.put("editedAt", "");

    assertNull(HelldotsProjection.dateField(comment, "createdAt"));
    assertNull(HelldotsProjection.dateField(comment, "editedAt"));
    assertNull(HelldotsProjection.dateField(comment, "absent"));
  }

  @Test
  public void intFieldConvertsAnyNumberType() {
    Map<String, Object> comment = new HashMap<>();
    comment.put("schemaVersionInteger", Integer.valueOf(2));
    comment.put("schemaVersionLong", Long.valueOf(2L));
    comment.put("schemaVersionDouble", Double.valueOf(2.9));
    comment.put("schemaVersionString", "2");

    assertEquals(Integer.valueOf(2), HelldotsProjection.intField(comment, "schemaVersionInteger"));
    assertEquals(Integer.valueOf(2), HelldotsProjection.intField(comment, "schemaVersionLong"));
    assertEquals(Integer.valueOf(2), HelldotsProjection.intField(comment, "schemaVersionDouble"));
    assertNull(HelldotsProjection.intField(comment, "schemaVersionString"));
    assertNull(HelldotsProjection.intField(comment, "absent"));
  }

  @Test
  public void fieldAccessorsTolerateANullMap() {
    assertNull(HelldotsProjection.stringField(null, "status"));
    assertNull(HelldotsProjection.intField(null, "schemaVersion"));
    assertNull(HelldotsProjection.dateField(null, "createdAt"));
  }

  @Test
  public void pathOfStripsQueryAndFragment() {
    assertEquals("/dashboard.do", HelldotsProjection.pathOf("/dashboard.do?projectID=123"));
    assertEquals("/dashboard.do", HelldotsProjection.pathOf("/dashboard.do#anchor"));
    assertEquals("/dashboard.do", HelldotsProjection.pathOf("/dashboard.do"));
  }

  @Test
  public void enumerationsMatchTheLibrary() {
    assertTrue(HelldotsProjection.STATUSES.contains("in_review"));
    assertTrue(HelldotsProjection.TYPES.contains("improvement"));
    assertTrue(HelldotsProjection.PRIORITIES.contains("medium"));
    assertEquals(4, HelldotsProjection.STATUSES.size());
    assertEquals(4, HelldotsProjection.TYPES.size());
    assertEquals(3, HelldotsProjection.PRIORITIES.size());
  }
}
