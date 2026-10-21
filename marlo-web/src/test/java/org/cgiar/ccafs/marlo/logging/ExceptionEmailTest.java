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
package org.cgiar.ccafs.marlo.logging;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.slf4j.MDC;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ExceptionEmailTest {

  @After
  public void clearContext() {
    LogContext.clear();
  }

  @Test
  public void credentialsAreMaskedAndOtherParametersKept() {
    Map<String, String[]> parameters = new HashMap<>();
    parameters.put("login.password", new String[] {"s3cret"});
    parameters.put("tipParameter.tokenValue", new String[] {"abc"});
    parameters.put("code", new String[] {"123456"});
    parameters.put("outcomesForm[0].milestones[0].code", new String[] {"1.0"});
    parameters.put("phaseID", new String[] {"444", "444"});

    String html = new ExceptionEmail("t").parameters(parameters).toHtml(null);

    assertFalse(html.contains("s3cret"));
    assertFalse(html.contains("abc"));
    assertFalse(html.contains("123456"));
    assertTrue(html.contains("outcomesForm[0].milestones[0].code: 1.0\n"));
    // A repeated value is written once
    assertTrue(html.contains("phaseID: 444\n"));
  }

  @Test
  public void everyValueIsEscaped() {
    Map<String, String[]> parameters = new HashMap<>();
    parameters.put("title", new String[] {"<script>x</script>"});
    String html = new ExceptionEmail("<b>t</b>").intro("<i>who</i>").parameters(parameters)
      .exception(new IllegalStateException("<img src=x>")).toHtml("https://m.org");

    assertFalse(html.contains("<script>"));
    assertFalse(html.contains("<img src=x>"));
    assertFalse(html.contains("<i>who"));
    assertTrue(html.contains("&lt;script&gt;"));
    assertTrue(html.contains("&lt;b&gt;t&lt;/b&gt;"));
  }

  @Test
  public void rootCauseIsShownFirst() {
    Exception root = new IllegalArgumentException("foreign key fails");
    Exception top = new RuntimeException("could not execute statement", new IllegalStateException("mid", root));

    assertSame(root, ExceptionEmail.rootCause(top));
    String html = new ExceptionEmail("t").exception(top).toHtml(null);
    assertTrue(html.indexOf("Root cause") < html.indexOf("Stack trace"));
    assertTrue(html.contains("java.lang.IllegalArgumentException: foreign key fails"));
  }

  @Test
  public void longValuesAreCut() {
    String value = new String(new char[ExceptionEmail.MAX_VALUE_LENGTH + 20]).replace('\0', 'a');
    String shortened = ExceptionEmail.shorten(value, ExceptionEmail.MAX_VALUE_LENGTH);

    assertEquals(value.substring(0, ExceptionEmail.MAX_VALUE_LENGTH) + " [... 20 more characters]", shortened);
    assertSame(value, ExceptionEmail.shorten(value, value.length()));
  }

  @Test
  public void correlationReadsTheLogContextAndSkipsMissingValues() {
    MDC.put(LogContext.REQUEST_ID, "b815cdbc1db20885");
    String html = new ExceptionEmail("t").correlation(3).detail("Phase", null).toHtml(null);

    assertTrue(html.contains("b815cdbc1db20885"));
    assertTrue(html.contains("Repeats not emailed in the previous hour"));
    assertFalse(html.contains("Status code"));
    assertFalse(html.contains("Phase"));
  }

  @Test
  public void manyParametersStayCompact() {
    Map<String, String[]> parameters = new HashMap<>();
    for (int i = 0; i < 300; i++) {
      parameters.put("deliverable.fundingSources[" + i + "].fundingSource.id", new String[] {"12345"});
    }
    int empty = new ExceptionEmail("t").toHtml(null).length();
    int full = new ExceptionEmail("t").parameters(parameters).toHtml(null).length();

    // Each parameter is one line of text, not a styled table row
    assertTrue((full - empty) / 300 < 70);
  }

  @Test
  public void imagesAreLeftOutWithoutAnAssetsUrl() {
    assertFalse(new ExceptionEmail("t").toHtml(null).contains("<img"));
    assertTrue(new ExceptionEmail("t").toHtml("https://m.org").contains("https://m.org/global/images/email/"));
  }
}
