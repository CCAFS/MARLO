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

import org.cgiar.ccafs.marlo.data.model.User;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.MDC;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Configures the shipped logback.xml, with only its property file line removed so that the test controls the
 * log.* properties, and checks the file the FILE-JSON appender writes: every line must parse as JSON and carry the
 * fields of design section 11 with the right types, and the personal fields must appear on failed requests only.
 * A started application is still the proof that the encoder works against the Jackson of the WAR (design section 16);
 * this test runs against the Jackson of the test classpath, which is the same version.
 */
public class JsonLogAppenderTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static final Pattern PROPERTY_FILE_LINE = Pattern.compile("<property resource=\"[^\"]*\"\\s*/>");

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private LoggerContext context;

  private static String shippedLogbackXml() {
    InputStream stream = JsonLogAppenderTest.class.getClassLoader().getResourceAsStream("logback.xml");
    assertNotNull("logback.xml must be on the classpath", stream);
    try (Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name()).useDelimiter("\\A")) {
      return scanner.hasNext() ? scanner.next() : "";
    }
  }

  private File jsonFile() {
    return new File(folder.getRoot(), "marlo-json-unit.log");
  }

  private Logger configure(String logJson, String logProduction) throws Exception {
    String xml = shippedLogbackXml();
    assertTrue("the property file line must be there to be removed", PROPERTY_FILE_LINE.matcher(xml).find());
    xml = PROPERTY_FILE_LINE.matcher(xml).replaceFirst("");
    context = new LoggerContext();
    context.putProperty("log.folder", folder.getRoot().getAbsolutePath());
    context.putProperty("log.instance", "unit");
    context.putProperty("log.console", "false");
    context.putProperty("log.file", "true");
    context.putProperty("log.production", logProduction);
    context.putProperty("log.test", "true");
    if (logJson != null) {
      context.putProperty("log.json", logJson);
    }
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    configurator.doConfigure(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    return context.getLogger("org.cgiar.ccafs.marlo.logging.JsonLogAppenderTest");
  }

  private List<JsonNode> readLines() throws IOException {
    context.stop();
    List<JsonNode> events = new ArrayList<>();
    for (String line : Files.readAllLines(jsonFile().toPath(), StandardCharsets.UTF_8)) {
      events.add(MAPPER.readTree(line));
    }
    return events;
  }

  private static JsonNode withMessage(List<JsonNode> events, String message) {
    for (JsonNode event : events) {
      if (message.equals(event.path("message").asText())) {
        return event;
      }
    }
    throw new AssertionError("no event with the message " + message + " in " + events);
  }

  @Before
  public void clearTheContext() {
    LogContext.clear();
  }

  @After
  public void cleanUp() {
    LogContext.clear();
    if (context != null) {
      context.stop();
    }
  }

  @Test
  public void everyLineIsOneJsonObjectWithTheSchemaFields() throws Exception {
    Logger logger = configure("true", "false");
    LogContext.putUserId(7L);
    LogContext.putGlobalUnit("CCAFS");
    LogContext.putRoute("/projects/CCAFS/reportingSummary.do");
    // LogContext generates the request id only inside the request listener, so the test sets the key directly
    MDC.put(LogContext.REQUEST_ID, "00000000000000ab");
    logger.info("an ordinary event");
    Exception e = new IllegalStateException("outer", new IllegalArgumentException("inner"));
    logger.error("a failed event", e);
    List<JsonNode> events = readLines();
    assertEquals(2, events.size());
    for (JsonNode event : events) {
      assertTrue(event.isObject());
      for (String field : new String[] {"timestamp", "level", "environment", "tool_name", "module_section",
        "controller_affected", "message", "request_id"}) {
        assertTrue(field + " must be a non-empty string in " + event, event.path(field).isTextual()
          && !event.get(field).asText().isEmpty());
      }
      assertEquals("CCAFS", event.get("tool_name").asText());
      assertEquals("/projects/CCAFS/reportingSummary.do", event.get("controller_affected").asText());
      assertEquals("00000000000000ab", event.get("request_id").asText());
      assertEquals(JsonLogAppenderTest.class.getName(), event.get("module_section").asText());
      assertTrue("timestamp must be ISO 8601 UTC with milliseconds: " + event.get("timestamp"),
        event.get("timestamp").asText().matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z"));
      assertEquals("the number is written without quotes", "number", event.get("user_id").getNodeType().name()
        .toLowerCase());
      assertFalse("service_affected is omitted", event.has("service_affected"));
    }
    assertEquals("INFO", events.get(0).get("level").asText());
    assertEquals("ERROR", events.get(1).get("level").asText());
    assertFalse("no stack trace without an exception", events.get(0).has("stack_trace"));
    assertTrue(events.get(1).get("stack_trace").asText().contains("IllegalArgumentException"));
  }

  @Test
  public void thePersonalFieldsAppearOnlyOnceTheStatusIs400OrMore() throws Exception {
    Logger logger = configure("true", "false");
    User user = new User();
    user.setFirstName("Ana");
    user.setLastName("Perez");
    user.setEmail("ana@example.org");
    LogContext.putUserId(7L);
    LogContext.putUserName(user);
    logger.info("before the status");
    LogContext.putStatusCode(200);
    logger.info("a successful status");
    LogContext.putStatusCode(404);
    logger.warn("a failed status");
    // A value that reaches the MDC by any route other than LogContext is still held back on a successful request
    LogContext.clear();
    MDC.put("user_email", "ana@example.org");
    MDC.put("user_name", "Ana Perez");
    MDC.put("user_id", "7");
    logger.info("personal data placed directly in the MDC");
    List<JsonNode> events = readLines();
    for (String message : new String[] {"before the status", "a successful status",
      "personal data placed directly in the MDC"}) {
      JsonNode event = withMessage(events, message);
      assertFalse(message + " must not carry user_email", event.has("user_email"));
      assertFalse(message + " must not carry user_name", event.has("user_name"));
      assertEquals(7, event.get("user_id").asInt());
    }
    JsonNode failed = withMessage(events, "a failed status");
    assertEquals("Ana Perez", failed.get("user_name").asText());
    assertEquals("ana@example.org", failed.get("user_email").asText());
    assertEquals(404, failed.get("status_code").asInt());
    assertEquals("number", failed.get("status_code").getNodeType().name().toLowerCase());
  }

  @Test
  public void anEventOutsideARequestCarriesNoRequestContext() throws Exception {
    Logger logger = configure("true", "false");
    logger.info("startup");
    JsonNode event = readLines().get(0);
    for (String field : new String[] {"user_id", "user_name", "user_email", "status_code", "request_id",
      "controller_affected", "tool_name"}) {
      assertFalse(field + " must be absent outside a request", event.has(field));
    }
    assertTrue(event.get("environment").asText().length() > 0);
  }

  @Test
  public void aLongStackTraceIsBounded() throws Exception {
    Logger logger = configure("true", "false");
    Throwable e = new RuntimeException("bottom");
    for (int i = 0; i < 200; i++) {
      e = new RuntimeException("level " + i, e);
    }
    logger.error("a deeply nested failure", e);
    JsonNode event = readLines().get(0);
    int length = event.get("stack_trace").asText().length();
    assertTrue("stack_trace is bounded, was " + length, length > 0 && length <= 8192 + 64);
  }

  @Test
  public void theFrameworkLoggerLevelsApplyToTheJsonAppender() throws Exception {
    Logger logger = configure("true", "false");
    context.getLogger("net.sf.ehcache.pool.sizeof.SizeOf").warn("noise that A2-2435 silenced");
    context.getLogger("net.sf.ehcache.pool.sizeof.SizeOf").error("an error of that logger");
    logger.info("an application event");
    List<JsonNode> events = readLines();
    assertEquals(2, events.size());
    assertEquals("an error of that logger", events.get(0).get("message").asText());
  }

  @Test
  public void aProductionServerDropsEventsBelowInfo() throws Exception {
    Logger logger = configure("true", "true");
    logger.debug("a debug event");
    logger.info("an info event");
    List<JsonNode> events = readLines();
    assertEquals(1, events.size());
    assertEquals("an info event", events.get(0).get("message").asText());
  }

  @Test
  public void withLogJsonFalseNoJsonFileIsWrittenAndTheTextLogStillWorks() throws Exception {
    Logger logger = configure("false", "false");
    logger.info("an event");
    context.stop();
    assertFalse(jsonFile().exists());
    for (String name : folder.getRoot().list()) {
      assertFalse("no JSON file expected, found " + name, name.contains("json"));
    }
    File textLog = new File(folder.getRoot(), "marlo-unit.log");
    assertTrue(new String(Files.readAllBytes(textLog.toPath()), StandardCharsets.UTF_8).contains(": an event"));
  }

  @Test
  public void withLogJsonUndefinedNoJsonFileIsWritten() throws Exception {
    Logger logger = configure(null, "false");
    logger.info("an event");
    context.stop();
    assertFalse(jsonFile().exists());
  }

}
