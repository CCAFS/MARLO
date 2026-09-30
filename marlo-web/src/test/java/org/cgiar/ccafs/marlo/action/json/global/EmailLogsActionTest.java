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

import org.cgiar.ccafs.marlo.data.model.EmailLogSearch;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.dispatcher.Parameter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The table and the resend of System Admin -> Emails read the same filters through EmailLogsAction.read, so a filter
 * the table applies is the one the resend applies.
 */
public class EmailLogsActionTest {

  private static Map<String, Parameter> parameters(String... namesAndValues) {
    Map<String, Parameter> parameters = new HashMap<>();
    for (int i = 0; i < namesAndValues.length; i += 2) {
      parameters.put(namesAndValues[i], new Parameter.Request(namesAndValues[i], namesAndValues[i + 1]));
    }
    return parameters;
  }

  @Test
  public void testFiltersAreRead() throws Exception {
    EmailLogSearch search = EmailLogsAction.read(parameters("globalUnit", "45", "source", "/projects/partners", "from",
      "2026-09-01", "to", "2026-09-28", "search", " welcome ", "orderColumn", "SUBJECT", "orderDir", "asc"));

    SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");
    assertEquals(Long.valueOf(45), search.getGlobalUnitId());
    assertEquals("/projects/partners", search.getSourceAction());
    assertEquals(day.parse("2026-09-01"), search.getFrom());
    assertEquals("the to day is included", day.parse("2026-09-29"), search.getUntil());
    assertEquals("welcome", search.getText());
    assertEquals(EmailLogSearch.Order.SUBJECT, search.getOrder());
    assertTrue(search.isAscending());
  }

  @Test
  public void testIdsThatAreNotPositiveLongsAreIgnored() {
    assertEquals(Long.valueOf(45), EmailLogsAction.idValue("45"));
    assertNull("too long for a long", EmailLogsAction.idValue("99999999999999999999"));
    assertNull(EmailLogsAction.idValue("0"));
    assertNull(EmailLogsAction.idValue("-4"));
    assertNull(EmailLogsAction.idValue(""));
    assertNull(EmailLogsAction.idValue(null));
    assertNull(EmailLogsAction.read(parameters("globalUnit", "99999999999999999999")).getGlobalUnitId());
  }

  @Test
  public void testMissingParametersDoNotFilter() {
    EmailLogSearch search = EmailLogsAction.read(parameters());

    assertNull(search.getGlobalUnitId());
    assertFalse(search.isWithoutGlobalUnit());
    assertNull(search.getSourceAction());
    assertFalse(search.isWithoutSourceAction());
    assertNull(search.getFrom());
    assertNull(search.getUntil());
    assertNull(search.getText());
    assertEquals("newest first by default", EmailLogSearch.Order.DATE, search.getOrder());
    assertFalse(search.isAscending());
  }

  @Test
  public void testNotRecordedAsksForTheRowsWithoutOne() {
    EmailLogSearch search = EmailLogsAction.read(parameters("globalUnit", "none", "source", "none"));

    assertTrue(search.isWithoutGlobalUnit());
    assertNull(search.getGlobalUnitId());
    assertTrue(search.isWithoutSourceAction());
    assertNull(search.getSourceAction());
  }

  @Test
  public void testUnreadableValuesAreIgnored() {
    EmailLogSearch search = EmailLogsAction.read(parameters("globalUnit", "45 or 1=1", "from", "2026-02-30", "to",
      "yesterday", "orderColumn", "e.id; drop table", "orderDir", "sideways"));

    assertNull(search.getGlobalUnitId());
    assertNull(search.getFrom());
    assertNull(search.getUntil());
    assertEquals(EmailLogSearch.Order.DATE, search.getOrder());
    assertFalse(search.isAscending());
    assertEquals(7, EmailLogsAction.intValue(parameters("length", "x"), "length", 7));
    assertEquals(7, EmailLogsAction.intValue(parameters(), "length", 7));
  }

  @Test
  public void testYearsOutsideADatetimeAreIgnored() {
    // A date input accepts years of up to six digits; date_email is a DATETIME, which ends at 9999.
    EmailLogSearch search = EmailLogsAction.read(parameters("from", "20260-01-01", "to", "0999-12-31"));

    assertNull(search.getFrom());
    assertNull(search.getUntil());
  }
}
