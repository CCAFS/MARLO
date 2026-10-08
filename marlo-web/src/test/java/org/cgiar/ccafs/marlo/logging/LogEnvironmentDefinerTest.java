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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LogEnvironmentDefinerTest {

  private static final String OVERRIDE = "marlo.environment";

  private static final String PATTERN_FIELD = " environment=${environment:-UNKNOWN}";

  private static final String PROFILES = "spring.profiles.active";

  private static void restore(String key, String value) {
    if (value == null) {
      System.clearProperty(key);
    } else {
      System.setProperty(key, value);
    }
  }

  private static String configure(String xml) throws Exception {
    LoggerContext context = new LoggerContext();
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    configurator.doConfigure(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    return context.getProperty("environment");
  }

  private static String configure(String profiles, String override) throws Exception {
    return configure("<configuration><define name=\"environment\" scope=\"context\" class=\""
      + LogEnvironmentDefiner.class.getName() + "\"><profiles>" + profiles + "</profiles><override>" + override
      + "</override></define></configuration>");
  }

  private static String shippedLogbackXml() {
    InputStream stream = LogEnvironmentDefinerTest.class.getClassLoader().getResourceAsStream("logback.xml");
    assertNotNull("logback.xml must be on the classpath", stream);
    try (Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name()).useDelimiter("\\A")) {
      return scanner.hasNext() ? scanner.next() : "";
    }
  }

  @Test
  public void anOverrideWinsOverTheProfile() {
    assertEquals("STAGING", LogEnvironmentDefiner.resolve(" staging ", "pro"));
  }

  @Test
  public void anUnknownProfileIsUpperCasedNotGuessed() {
    assertEquals("API", LogEnvironmentDefiner.resolve(null, "api"));
  }

  @Test
  public void anUnsafeValueIsUnknown() {
    assertEquals(LogEnvironmentDefiner.UNKNOWN, LogEnvironmentDefiner.resolve("PROD request_id=1", "pro"));
    assertEquals(LogEnvironmentDefiner.UNKNOWN, LogEnvironmentDefiner.resolve(null, "a\nb"));
  }

  @Test
  public void eachProfileMapsToItsEnvironment() {
    assertEquals(LogEnvironmentDefiner.PROD, LogEnvironmentDefiner.resolve(null, "pro"));
    assertEquals(LogEnvironmentDefiner.PROD, LogEnvironmentDefiner.resolve("", "api, pro"));
    assertEquals(LogEnvironmentDefiner.TEST, LogEnvironmentDefiner.resolve(null, "test"));
    assertEquals(LogEnvironmentDefiner.DEV, LogEnvironmentDefiner.resolve(null, "dev"));
    assertEquals(LogEnvironmentDefiner.DEV, LogEnvironmentDefiner.resolve(null, "FAST"));
  }

  @Test
  public void logbackDefinesThePropertyFromTheNestedElements() throws Exception {
    assertEquals(LogEnvironmentDefiner.TEST, configure("test", ""));
    // The form logback.xml uses: an unset marlo.environment must not override the profile
    assertEquals(LogEnvironmentDefiner.PROD, configure("pro", "${marlo.environment.unset.in.tests:-}"));
    assertEquals("UAT", configure("pro", "uat"));
  }

  /**
   * Configures only the define element of the shipped logback.xml, so that a misspelt class or element name fails
   * here instead of printing environment=UNKNOWN on every line. The rest of the file is left out because its appenders
   * would create log files.
   */
  @Test
  public void theShippedLogbackXmlDefinesTheEnvironment() throws Exception {
    Matcher define =
      Pattern.compile("<define name=\"environment\".*?</define>", Pattern.DOTALL).matcher(shippedLogbackXml());
    assertTrue("logback.xml must declare the environment property", define.find());
    String xml = "<configuration>" + define.group() + "</configuration>";
    String previousProfiles = System.getProperty(PROFILES);
    String previousOverride = System.getProperty(OVERRIDE);
    try {
      // pro, not dev: a misspelt <profiles> element leaves the profile null, which is DEV, the no-profile value
      System.setProperty(PROFILES, "pro");
      System.clearProperty(OVERRIDE);
      assertEquals(LogEnvironmentDefiner.PROD, configure(xml));
      System.setProperty(OVERRIDE, "uat");
      assertEquals("UAT", configure(xml));
    } finally {
      restore(PROFILES, previousProfiles);
      restore(OVERRIDE, previousOverride);
    }
  }

  @Test
  public void everyShippedPatternPrintsTheEnvironment() {
    Matcher pattern = Pattern.compile("<pattern>(.*?)</pattern>", Pattern.DOTALL).matcher(shippedLogbackXml());
    int patterns = 0;
    while (pattern.find()) {
      patterns++;
      assertTrue("pattern without the environment field: " + pattern.group(1),
        pattern.group(1).contains(PATTERN_FIELD));
    }
    assertTrue("logback.xml must declare at least one pattern", patterns > 0);
  }

  @Test
  public void noProfileIsDevLikeApplicationContextConfig() {
    assertEquals(LogEnvironmentDefiner.DEV, LogEnvironmentDefiner.resolve(null, null));
    assertEquals(LogEnvironmentDefiner.DEV, LogEnvironmentDefiner.resolve(null, " "));
  }
}
