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

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests what MarloMdcJsonProvider writes for a given MDC: the JSON type of each field (NF-006), the SEC-001
 * restriction on the personal fields, and what happens to a value that is missing or malformed (NF-005).
 */
public class MarloMdcJsonProviderTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static Map<String, String> mdc(String... keysAndValues) {
    Map<String, String> map = new HashMap<>();
    for (int i = 0; i < keysAndValues.length; i += 2) {
      map.put(keysAndValues[i], keysAndValues[i + 1]);
    }
    return map;
  }

  private static JsonNode write(MarloMdcJsonProvider provider, Map<String, String> mdc) throws IOException {
    LoggingEvent event = new LoggingEvent(MarloMdcJsonProviderTest.class.getName(),
      new LoggerContext().getLogger(MarloMdcJsonProviderTest.class), Level.INFO, "a message", null, null);
    event.setMDCPropertyMap(mdc);
    StringWriter out = new StringWriter();
    try (JsonGenerator generator = new JsonFactory().createGenerator(out)) {
      generator.writeStartObject();
      provider.writeTo(generator, event);
      generator.writeEndObject();
    }
    return MAPPER.readTree(out.toString());
  }

  private static JsonNode write(Map<String, String> mdc) throws IOException {
    return write(new MarloMdcJsonProvider(), mdc);
  }

  @Test
  public void anEventWithNoContextWritesNothing() throws IOException {
    assertEquals(0, write(new HashMap<String, String>()).size());
  }

  @Test
  public void aBlankPersonalValueIsLeftOutEvenOnAnError() throws IOException {
    JsonNode json = write(mdc("status_code", "500", "user_name", "  ", "user_email", ""));
    assertEquals(1, json.size());
    assertTrue(json.get("status_code").isNumber());
  }

  @Test
  public void aMalformedNumberIsAbsentAndNeverThrows() throws IOException {
    JsonNode json = write(mdc("status_code", "40x", "user_id", "abc", "user_name", "Ana Perez"));
    assertEquals("a malformed status or id must not be written as a string either", 0, json.size());
    assertEquals(0, write(mdc("status_code", "", "user_id", " ")).size());
  }

  @Test
  public void aMissingStatusIsNotAnError() throws IOException {
    JsonNode json = write(mdc("user_id", "7", "user_name", "Ana Perez", "user_email", "ana@example.org"));
    assertFalse(json.has("user_name"));
    assertFalse(json.has("user_email"));
    assertEquals(7, json.get("user_id").asInt());
  }

  @Test
  public void anErrorStatusPublishesTheNameAndTheEmail() throws IOException {
    JsonNode json = write(mdc("status_code", "404", "user_id", "7", "user_name", "Ana Perez", "user_email",
      "ana@example.org"));
    assertEquals("Ana Perez", json.get("user_name").asText());
    assertEquals("ana@example.org", json.get("user_email").asText());
  }

  @Test
  public void aStatusBelow400KeepsTheNameAndTheEmailOut() throws IOException {
    for (String status : new String[] {"100", "200", "302", "399"}) {
      JsonNode json = write(mdc("status_code", status, "user_name", "Ana Perez", "user_email", "ana@example.org"));
      assertFalse("user_name on status " + status, json.has("user_name"));
      assertFalse("user_email on status " + status, json.has("user_email"));
      assertTrue("the status itself is still written", json.has("status_code"));
    }
  }

  @Test
  public void statusCodeAndUserIdAreJsonNumbers() throws IOException {
    JsonNode json = write(mdc("status_code", "500", "user_id", "1234567890123"));
    assertTrue(json.get("status_code").isNumber());
    assertEquals(500, json.get("status_code").intValue());
    assertTrue(json.get("user_id").isNumber());
    assertEquals(1234567890123L, json.get("user_id").longValue());
  }

  @Test
  public void theFieldNamesCanBeSetFromLogbackXml() throws IOException {
    MarloMdcJsonProvider provider = new MarloMdcJsonProvider();
    provider.setStatusCodeFieldName("http_status");
    provider.setUserIdFieldName("uid");
    provider.setUserNameFieldName("full_name");
    provider.setUserEmailFieldName("mail");
    JsonNode json = write(provider, mdc("status_code", "500", "user_id", "7", "user_name", "Ana Perez", "user_email",
      "ana@example.org"));
    assertEquals(500, json.get("http_status").intValue());
    assertEquals(7, json.get("uid").intValue());
    assertEquals("Ana Perez", json.get("full_name").asText());
    assertEquals("ana@example.org", json.get("mail").asText());
    assertEquals(4, json.size());
  }

  @Test
  public void serviceAffectedIsNeverWritten() throws IOException {
    JsonNode json = write(mdc("service_affected", "", "status_code", "500"));
    assertFalse(json.has("service_affected"));
    assertFalse(write(mdc("service_affected", "marlo")).has("service_affected"));
  }

}
