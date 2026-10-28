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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist;

import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.RestErrorHarness.Translated;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.Deliverables.DeliverablesWOSItem;
import org.cgiar.ccafs.marlo.rest.dto.PublicationsWOSDTO;
import org.cgiar.ccafs.marlo.rest.errors.ErrorDTO;
import org.cgiar.ccafs.marlo.rest.mappers.PublicationWOSMapper;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.PublicationWOS;

import java.io.IOException;
import java.util.Arrays;

import ch.qos.logback.classic.Level;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * ENH-LOGGING-STANDARDIZATION-001-T17 (batch 2): {@code DeliverablesWOSItem.validateDeliverable} answered a failure of
 * the WOS service as a 400 field error that echoed the exception message, which can carry the internal address of the
 * service, and logged it a second time. It is a server failure and answers 500 with a fixed message.
 */
public class DeliverablesWOSItemErrorHandlingTest {

  private static final String UPSTREAM = "http://clarisa.wos.api.mel.cgiar.org/?link=";
  private static final String LINK = "10.1000/xyz123";

  /**
   * An item whose call to the WOS service is answered by the test instead of the network.
   */
  private static final class ItemWithFakeWos extends DeliverablesWOSItem<Object> {

    private final JsonElement answer;
    private final IOException failure;
    private String requestedUrl;

    ItemWithFakeWos(PublicationWOSMapper mapper, JsonElement answer, IOException failure) {
      super(mapper);
      this.answer = answer;
      this.failure = failure;
    }

    @Override
    public JsonElement getServiceWOS(String url) throws IOException {
      this.requestedUrl = url;
      if (this.failure != null) {
        throw this.failure;
      }
      return this.answer;
    }
  }

  @Rule
  public RestErrorHarness harness = new RestErrorHarness();

  private static PublicationWOSMapper mapper() {
    return Fakes.fake(PublicationWOSMapper.class).on("publicationWOSToPublicationWOSDTO", args -> {
      PublicationWOS publication = (PublicationWOS) args[0];
      if (publication == null) {
        return null;
      }
      PublicationsWOSDTO dto = new PublicationsWOSDTO();
      dto.setTitle(publication.getTitle());
      return dto;
    }).build();
  }

  @Test
  public void aValidAnswerIsReturnedAsBefore() {
    ItemWithFakeWos item = new ItemWithFakeWos(mapper(), new JsonParser().parse("{\"title\":\"A title\"}"), null);

    ResponseEntity<PublicationsWOSDTO> response = item.validateDeliverable(LINK);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("A title", response.getBody().getTitle());
    assertEquals(LINK, response.getBody().getUrl());
    assertEquals(UPSTREAM + LINK, item.requestedUrl);
    this.harness.assertNothingLogged();
  }

  @Test
  public void anUnknownLinkIsAnswered404() {
    ItemWithFakeWos item = new ItemWithFakeWos(mapper(), JsonNull.INSTANCE, null);

    ResponseEntity<PublicationsWOSDTO> response = item.validateDeliverable(LINK);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    this.harness.assertNothingLogged();
  }

  @Test
  public void aMissingLinkIsAnswered400() {
    ItemWithFakeWos item = new ItemWithFakeWos(mapper(), null, null);

    Translated translated = this.harness.propagate(() -> item.validateDeliverable(null));

    assertEquals(400, translated.status);
    assertEquals(Arrays.asList("URL: URL is missing"), Fakes.fieldErrors(translated.body));
    assertEquals(Level.WARN, translated.loggedLevel);
    assertNull("the WOS service is not called without a link", item.requestedUrl);
  }

  @Test
  public void aFailureOfTheWosServiceIsAnswered500WithoutItsAddress() {
    IOException failure = new IOException("Connection refused: connect to " + UPSTREAM + LINK);
    ItemWithFakeWos item = new ItemWithFakeWos(mapper(), null, failure);

    Translated translated = this.harness.propagate(() -> item.validateDeliverable(LINK));

    assertEquals(500, translated.status);
    assertEquals(Level.ERROR, translated.loggedLevel);
    assertTrue(translated.exception instanceof IllegalStateException);
    assertEquals(failure, translated.exception.getCause());
    ErrorDTO error = (ErrorDTO) translated.body;
    String body = error.getMessage() + " | " + error.getDescription() + " | " + error.getSeverity();
    assertTrue(body, body.contains("The WOS service did not answer for the requested link"));
    assertFalse("the body must not echo the upstream address: " + body, body.contains("clarisa"));
    assertFalse(body.contains(LINK));
  }
}
