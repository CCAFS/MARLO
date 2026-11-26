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

package org.cgiar.ccafs.marlo.utils.pdf;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Proves the Handlebars helpers of the local PDF engine (local-pdf-report-engine FN-003, AC-FN-003) against
 * <code>helper-vectors.json</code>. Every <code>expected</code> in that file was produced by Handlebars.js 4.7.8 with
 * the helpers copied from <code>pdf.module.ts</code> (see its <code>_generator</code> field); this test only replays
 * the rows through the Java engine.
 */
public class HandlebarsReportHelpersTest {

  /** A bean the engine must not reach: only maps are resolved. */
  public static class Bean {

    public String getName() {
      return "reached";
    }
  }

  @SuppressWarnings("unchecked")
  private static List<Map<String, Object>> readRows() throws Exception {
    InputStream stream = HandlebarsReportHelpersTest.class.getResourceAsStream("/pdf/helper-vectors.json");
    assertNotNull("helper-vectors.json is missing from the test classpath", stream);
    try {
      Map<String, Object> file = new ObjectMapper().readValue(stream, Map.class);
      assertNotNull(file.get("_generator"));
      return (List<Map<String, Object>>) file.get("rows");
    } finally {
      stream.close();
    }
  }

  /** The messages of an exception and its causes, as Handlebars.java wraps helper failures. */
  private static String messages(Throwable error) {
    StringBuilder text = new StringBuilder();
    for (Throwable cause = error; cause != null; cause = cause.getCause()) {
      text.append(cause.getMessage()).append('\n');
    }
    return text.toString();
  }

  @SuppressWarnings("unchecked")
  private static String render(Handlebars handlebars, Object template, Object context) throws Exception {
    Template compiled = handlebars.compileInline((String) template);
    return HandlebarsReportHelpers.apply(compiled, (Map<String, Object>) context);
  }

  @Test
  public void helpersMatchHandlebarsJsVectors() throws Exception {
    Handlebars handlebars = HandlebarsReportHelpers.newHandlebars();
    List<Map<String, Object>> rows = readRows();
    assertTrue("the vector table is unexpectedly small: " + rows.size(), rows.size() > 1000);
    List<String> failures = new ArrayList<String>();
    for (int i = 0; i < rows.size(); i++) {
      Map<String, Object> row = rows.get(i);
      Object expected = row.get("expected");
      String label = "row " + i + " template=" + row.get("template") + " context=" + row.get("context");
      String actual;
      try {
        actual = render(handlebars, row.get("template"), row.get("context"));
      } catch (Exception e) {
        if (expected instanceof Map) {
          String message = String.valueOf(((Map<?, ?>) expected).get("error"));
          if (!messages(e).contains(message)) {
            failures.add(label + " expected an error containing <" + message + "> but got " + e);
          }
          continue;
        }
        failures.add(label + " expected=<" + expected + "> but threw " + e);
        continue;
      }
      if (expected instanceof Map) {
        failures.add(label + " expected an error (" + expected + ") but rendered <" + actual + ">");
      } else if (!expected.equals(actual)) {
        failures.add(label + " expected=<" + expected + "> actual=<" + actual + ">");
      }
    }
    assertEquals("Java output differs from Handlebars.js on " + failures.size() + " of " + rows.size()
      + " rows:\n" + String.join("\n", failures), 0, failures.size());
  }

  private static String resourceText(String path) throws Exception {
    try (InputStream in = HandlebarsReportHelpersTest.class.getResourceAsStream(path)) {
      assertNotNull("missing test resource " + path, in);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[8192];
      for (int read = in.read(buffer); read >= 0; read = in.read(buffer)) {
        out.write(buffer, 0, read);
      }
      return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
  }

  /**
   * The HTML the local engine builds from each production fixture template is byte-identical to what Handlebars.js
   * 4.7.8 builds (stored in <code>expected-html/</code>, produced by <code>generate-expected-html.js</code>): the
   * PhantomJS renderer must see the same bytes production sees, standalone-line stripping included.
   */
  @Test
  @SuppressWarnings("unchecked")
  public void fixtureHtmlIsByteIdenticalToHandlebarsJs() throws Exception {
    Handlebars handlebars = HandlebarsReportHelpers.newHandlebars();
    for (String name : new String[] {"oicr", "innovation", "project"}) {
      Map<String, Object> payload = new ObjectMapper().readValue(resourceText("/pdf/payloads/" + name
        + "-payload.json"), Map.class);
      Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) payload.get("data")).get("data");
      String actual = HandlebarsReportHelpers.apply(handlebars.compileInline(resourceText("/pdf/templates/" + name
        + ".html")), data);
      String expected = resourceText("/pdf/expected-html/" + name + ".html");
      if (!expected.equals(actual)) {
        int at = 0;
        while (at < expected.length() && at < actual.length() && expected.charAt(at) == actual.charAt(at)) {
          at++;
        }
        int from = Math.max(0, at - 60);
        throw new AssertionError(name + " differs from Handlebars.js at char " + at + " (lengths expected "
          + expected.length() + ", actual " + actual.length() + ")\nexpected: <"
          + expected.substring(from, Math.min(expected.length(), at + 60)) + ">\nactual:   <"
          + actual.substring(from, Math.min(actual.length(), at + 60)) + ">");
      }
    }
  }

  /** The log events of {@link HandlebarsReportHelpers} while <code>body</code> runs. */
  private static List<String> warningsDuring(Runnable body) {
    ch.qos.logback.classic.Logger logger =
      (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(HandlebarsReportHelpers.class);
    ch.qos.logback.core.read.ListAppender<ch.qos.logback.classic.spi.ILoggingEvent> appender =
      new ch.qos.logback.core.read.ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>();
    appender.start();
    logger.addAppender(appender);
    try {
      body.run();
    } finally {
      logger.detachAppender(appender);
    }
    List<String> warnings = new ArrayList<String>();
    for (ch.qos.logback.classic.spi.ILoggingEvent event : appender.list) {
      if (event.getLevel() == ch.qos.logback.classic.Level.WARN) {
        warnings.add(event.getFormattedMessage());
      }
    }
    return warnings;
  }

  private static void compileQuietly(final String template) {
    try {
      HandlebarsReportHelpers.newHandlebars().compileInline(template);
    } catch (Exception expected) {
      // Handlebars.java may reject the template afterwards; the test is about the warning
    }
  }

  @Test
  public void aTemplateLeftUnstrippedIsWarnedOnceWithTheReasonAndNeverItsText() {
    String[][] cases = {{"a\n{{{{raw}}}}\nSECRET-TEXT\n{{{{/raw}}}}\nb", "raw block"},
      {"a\n{{#if x}}\nSECRET-TEXT\n", "unbalanced block tags"},
      {"a\n{{/if}}\nSECRET-TEXT\n", "unbalanced block tags"},
      {"a\n{{^x}}\nSECRET-TEXT\n{{else}}\nb\n{{/x}}", "inverse block with else"},
      {"a\n{{x SECRET-TEXT", "unterminated tag"}};
    for (final String[] test : cases) {
      List<String> warnings = warningsDuring(new Runnable() {

        @Override
        public void run() {
          compileQuietly(test[0]);
        }
      });
      assertEquals(test[0], 1, warnings.size());
      assertTrue(warnings.get(0), warnings.get(0).contains(test[1]));
      assertTrue(warnings.get(0), !warnings.get(0).contains("SECRET-TEXT"));
    }
  }

  @Test
  public void aStrippedTemplateIsNotWarnedAbout() {
    List<String> warnings = warningsDuring(new Runnable() {

      @Override
      public void run() {
        compileQuietly("a\n{{#if x}}\nb\n{{/if}}\nc");
        compileQuietly("plain text");
      }
    });
    assertEquals(warnings.toString(), 0, warnings.size());
  }

  @Test
  public void registersExactlyTheSeventeenHelpersOfThePdfModule() {
    Set<String> defaults = new HashSet<String>();
    for (Map.Entry<String, ?> entry : new Handlebars().helpers()) {
      defaults.add(entry.getKey());
    }
    Set<String> registered = new HashSet<String>();
    for (Map.Entry<String, ?> entry : HandlebarsReportHelpers.newHandlebars().helpers()) {
      if (!defaults.contains(entry.getKey())) {
        registered.add(entry.getKey());
      }
    }
    Set<String> expected = new HashSet<String>();
    expected.add("safeEmpty");
    expected.add("percentage");
    expected.add("mathOperator");
    expected.add("stringContentCompare");
    expected.add("inList");
    expected.add("yesNoHelper");
    expected.add("conditionalAttribiute");
    expected.add("safeString");
    expected.add("ifEquals");
    expected.add("ifNotEquals");
    expected.add("ifCond");
    expected.add("or");
    expected.add("typeof");
    expected.add("json");
    expected.add("parseJSON");
    expected.add("ifAnd");
    expected.add("ifOr");
    assertEquals(17, expected.size());
    assertEquals(expected, registered);
  }

  @Test
  public void stringContentCompareRejectsNonStringsAsTheMicroserviceDoes() throws Exception {
    Handlebars handlebars = HandlebarsReportHelpers.newHandlebars();
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("n", 1);
    String[] templates = {"{{#stringContentCompare n \"x\"}}T{{else}}F{{/stringContentCompare}}",
      "{{#stringContentCompare \"x\"}}T{{else}}F{{/stringContentCompare}}"};
    for (String template : templates) {
      try {
        HandlebarsReportHelpers.apply(handlebars.compileInline(template), data);
        throw new AssertionError("expected an exception for " + template);
      } catch (AssertionError e) {
        throw e;
      } catch (Exception expected) {
        assertTrue(String.valueOf(expected.getMessage()).contains("stringContentCompare requires"));
      }
    }
  }

  @Test
  public void resolvesMapsOnlyNeverJavaBeans() throws Exception {
    Handlebars handlebars = HandlebarsReportHelpers.newHandlebars();
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("bean", new Bean());
    data.put("map", java.util.Collections.singletonMap("name", "from-map"));
    assertEquals("[][from-map]",
      HandlebarsReportHelpers.apply(handlebars.compileInline("[{{bean.name}}][{{map.name}}]"), data));
  }

  @Test
  public void printsNumbersTheJavaScriptWay() throws Exception {
    Handlebars handlebars = HandlebarsReportHelpers.newHandlebars();
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("whole", 1.0d);
    data.put("fraction", 2.5d);
    data.put("integer", 10);
    Template template = handlebars.compileInline("{{whole}} {{fraction}} {{integer}}");
    assertEquals("1 2.5 10", HandlebarsReportHelpers.apply(template, data));
  }
}
