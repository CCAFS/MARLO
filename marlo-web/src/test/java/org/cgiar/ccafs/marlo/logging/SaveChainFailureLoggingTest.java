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

import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.impl.ProjectInnovationPartnershipManagerImpl;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * A failure inside a save-chain manager must reach the JSON log with its stack trace and must not print on
 * System.err. The manager's logger is pointed at a LoggerContext built from the shipped logback.xml with log.json on.
 */
public class SaveChainFailureLoggingTest {

  private static final Pattern PROPERTY_FILE_LINE = Pattern.compile("<property resource=\"[^\"]*\"\\s*/>");

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void partnerLookupFailureIsLoggedWithItsStackTraceAndNotOnSystemErr() throws Exception {
    InputStream stream = SaveChainFailureLoggingTest.class.getClassLoader().getResourceAsStream("logback.xml");
    String xml;
    try (Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name()).useDelimiter("\\A")) {
      xml = PROPERTY_FILE_LINE.matcher(scanner.next()).replaceFirst("");
    }
    LoggerContext context = new LoggerContext();
    context.putProperty("log.folder", folder.getRoot().getAbsolutePath());
    context.putProperty("log.instance", "unit");
    context.putProperty("log.console", "false");
    context.putProperty("log.file", "true");
    context.putProperty("log.production", "false");
    context.putProperty("log.test", "true");
    context.putProperty("log.json", "true");
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    configurator.doConfigure(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

    ProjectPartnerManager failing = (ProjectPartnerManager) Proxy.newProxyInstance(
      ProjectPartnerManager.class.getClassLoader(), new Class<?>[] {ProjectPartnerManager.class},
      (proxy, method, args) -> {
        throw new IllegalStateException("partner lookup failed");
      });
    ProjectInnovationPartnershipManagerImpl manager =
      new ProjectInnovationPartnershipManagerImpl(null, null, null, failing);
    Field logger = ProjectInnovationPartnershipManagerImpl.class.getDeclaredField("logger");
    logger.setAccessible(true);
    logger.set(manager, context.getLogger(ProjectInnovationPartnershipManagerImpl.class));

    Phase phase = new Phase();
    phase.setId(5L);
    PrintStream originalErr = System.err;
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    System.setErr(new PrintStream(err, true));
    try {
      assertTrue(manager.getUserList(11L, 22L, phase).isEmpty());
    } finally {
      System.setErr(originalErr);
    }
    context.stop();

    List<String> lines = Files.readAllLines(new java.io.File(folder.getRoot(), "marlo-json-unit.log").toPath(),
      StandardCharsets.UTF_8);
    JsonNode event = null;
    for (String line : lines) {
      JsonNode node = new ObjectMapper().readTree(line);
      if (node.path("message").asText().startsWith("Unable to get the partners")) {
        event = node;
      }
    }
    assertTrue("the failure must be in the JSON log: " + lines, event != null);
    assertEquals("Unable to get the partners of project 22 and institution 11", event.path("message").asText());
    assertEquals("ERROR", event.path("level").asText());
    String stackTrace = event.path("stack_trace").asText();
    assertTrue(stackTrace, stackTrace.contains("java.lang.IllegalStateException: partner lookup failed"));
    assertEquals("", err.toString());
  }
}
