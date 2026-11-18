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

import org.cgiar.ccafs.marlo.data.manager.DeliverableDisseminationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataExternalSourcesManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverablePublicationMetadataManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.MetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.MetadataElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Publication;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.RestErrorHarness.Translated;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.Deliverables.DeliverablesItem;
import org.cgiar.ccafs.marlo.rest.dto.NewPublicationDTO;
import org.cgiar.ccafs.marlo.rest.dto.PhaseDTO;
import org.cgiar.ccafs.marlo.rest.dto.PublicationDTO;
import org.cgiar.ccafs.marlo.rest.errors.ErrorDTO;
import org.cgiar.ccafs.marlo.rest.mappers.PublicationsMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.hibernate.LazyInitializationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * ENH-LOGGING-STANDARDIZATION-001-T17 (batch 2): the error handling of {@code DeliverablesItem}. The publication list
 * used to turn every failure, including a database error, into a 400 with an extra "ERROR Creating info" field error,
 * and logged it twice. The creation of a publication keeps its fallback when the optional WOS metadata cannot be
 * stored, because the deliverable is already saved by then.
 */
public class DeliverablesItemErrorHandlingTest {

  /**
   * An item whose call to the WOS service is answered by the test instead of the network.
   */
  private static final class ItemWithFakeWos extends DeliverablesItem<Object> {

    private final JsonElement answer;
    private final IOException failure;

    ItemWithFakeWos(JsonElement answer, IOException failure) {
      super(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
      this.answer = answer;
      this.failure = failure;
    }

    @Override
    public JsonElement getServiceWOS(String url) throws IOException {
      if (this.failure != null) {
        throw this.failure;
      }
      return this.answer;
    }
  }

  private static final String ACRONYM = "AICCRA";
  private static final long DELIVERABLE_ID = 77L;
  private static final String FIXED_MESSAGE = "Could not read the deliverables of the requested phase";

  @Rule
  public RestErrorHarness harness = new RestErrorHarness();

  private GlobalUnit globalUnit;
  private Phase phase;
  private Deliverable deliverable;

  @Before
  public void createFixtures() {
    this.globalUnit = new GlobalUnit();
    this.globalUnit.setId(45L);
    this.globalUnit.setAcronym(ACRONYM);
    this.phase = new Phase();
    this.phase.setId(100L);
    this.phase.setYear(2024);
    this.phase.setName("Reporting");
    this.phase.setCrp(this.globalUnit);
    this.deliverable = new Deliverable();
    this.deliverable.setId(DELIVERABLE_ID);
    this.deliverable.setPhase(this.phase);
  }

  private DeliverablesItem<Object> listingItem(Fakes.Answer deliverablesOfThePhase) {
    DeliverableInfo info = new DeliverableInfo();
    info.setYear(2024);
    info.setTitle("A publication");
    this.deliverable.setDeliverableInfo(info);

    GlobalUnitManager globalUnitManager = Fakes.fake(GlobalUnitManager.class)
      .on("findGlobalUnitByAcronym", args -> ACRONYM.equals(args[0]) ? this.globalUnit : null).build();
    PhaseManager phaseManager = Fakes.fake(PhaseManager.class).returning("findAll", Arrays.asList(this.phase)).build();
    DeliverableManager deliverableManager = Fakes.fake(DeliverableManager.class)
      .on("getDeliverablesByParameters", deliverablesOfThePhase).returning("getDeliverableById", this.deliverable)
      .returning("isDeliverableExcluded", Boolean.TRUE).build();
    PublicationsMapper mapper = Fakes.fake(PublicationsMapper.class).on("publicationToPublicationDTO", args -> {
      PublicationDTO dto = new PublicationDTO();
      dto.setId(((Publication) args[0]).getId());
      return dto;
    }).build();

    DeliverablesItem<Object> item = Fakes.instance(DeliverablesItem.class);
    RestErrorHarness.inject(item, "globalUnitManager", globalUnitManager);
    RestErrorHarness.inject(item, "phaseManager", phaseManager);
    RestErrorHarness.inject(item, "deliverableManager", deliverableManager);
    RestErrorHarness.inject(item, "publicationsMapper", mapper);
    return item;
  }

  @Test
  public void thePublicationsOfAPhaseAreAnsweredAsBefore() {
    DeliverablesItem<Object> item = this.listingItem(args -> Arrays.asList(this.deliverable));

    List<PublicationDTO> publications = item.getAllDeliverables(ACRONYM, 2024, "Reporting", this.harness.user());

    assertEquals(1, publications.size());
    assertEquals(Long.valueOf(DELIVERABLE_ID), publications.get(0).getId());
    this.harness.assertNothingLogged();
  }

  @Test
  public void aPhaseWithoutPublicationsIsAnswered400WithOnlyNoDataFound() {
    DeliverablesItem<Object> item = this.listingItem(args -> new ArrayList<Deliverable>());

    Translated translated =
      this.harness.propagate(() -> item.getAllDeliverables(ACRONYM, 2024, "Reporting", this.harness.user()));

    assertEquals(400, translated.status);
    assertEquals(Arrays.asList("Deliverable: No data found"), Fakes.fieldErrors(translated.body));
    assertEquals(Level.WARN, translated.loggedLevel);
  }

  /**
   * What a client can read in the response body of a 500: every text the handler put in it.
   */
  private static String bodyText(Translated translated) {
    ErrorDTO error = (ErrorDTO) translated.body;
    return error.getMessage() + " | " + error.getDescription() + " | " + error.getSeverity();
  }

  /**
   * The failure is a 500 that carries a fixed message only: the original exception stays as the cause, for the log and
   * the support email, and nothing of it reaches the client.
   */
  private void assertAnswered500WithOnlyTheFixedMessage(Translated translated, Throwable cause, String... internal) {
    assertEquals(500, translated.status);
    assertEquals(Level.ERROR, translated.loggedLevel);
    String body = bodyText(translated);
    assertTrue(body, body.contains(FIXED_MESSAGE));
    if (cause.getMessage() != null) {
      assertFalse("the body must not echo the cause: " + body, body.contains(cause.getMessage()));
    }
    for (String detail : internal) {
      assertFalse("the body must not carry " + detail + ": " + body, body.contains(detail));
    }
    assertEquals(IllegalStateException.class, translated.exception.getClass());
    assertEquals(FIXED_MESSAGE, translated.exception.getMessage());
    assertSame("the original failure is kept as the cause, so it is logged with its stack trace", cause,
      translated.exception.getCause());
  }

  @Test
  public void aFailingManagerIsAnswered500WithOnlyAFixedMessage() {
    RuntimeException failure = new LazyInitializationException("failed to lazily initialize a collection of role: "
      + "org.cgiar.ccafs.marlo.data.model.Deliverable.deliverableInfos, could not initialize proxy - no Session");
    DeliverablesItem<Object> item = this.listingItem(args -> {
      throw failure;
    });

    Translated translated =
      this.harness.propagate(() -> item.getAllDeliverables(ACRONYM, 2024, "Reporting", this.harness.user()));

    this.assertAnswered500WithOnlyTheFixedMessage(translated, failure, "org.cgiar", "deliverableInfos", "Session");
  }

  @Test
  public void aFailingManagerWithAPlainMessageIsAnswered500WithOnlyAFixedMessage() {
    RuntimeException failure = new IllegalStateException("the database is down at jdbc:mysql://10.1.2.3/marlo");
    DeliverablesItem<Object> item = this.listingItem(args -> {
      throw failure;
    });

    Translated translated =
      this.harness.propagate(() -> item.getAllDeliverables(ACRONYM, 2024, "Reporting", this.harness.user()));

    this.assertAnswered500WithOnlyTheFixedMessage(translated, failure, "jdbc:mysql", "10.1.2.3");
  }

  @Test
  public void aRowWithAMissingValueIsAnswered500WithoutTheNullPointerDetail() {
    DeliverableDissemination withoutPhase = new DeliverableDissemination();
    this.deliverable.setDeliverableDisseminations(new HashSet<DeliverableDissemination>(Arrays.asList(withoutPhase)));
    DeliverablesItem<Object> item = this.listingItem(args -> Arrays.asList(this.deliverable));

    Translated translated =
      this.harness.propagate(() -> item.getAllDeliverables(ACRONYM, 2024, "Reporting", this.harness.user()));

    // The cause is the exception the item received; when the item lets it through unwrapped, it is the exception itself
    Throwable original =
      translated.exception.getCause() != null ? translated.exception.getCause() : translated.exception;
    assertTrue("the row without a phase fails with a null pointer, whose text names internal methods",
      original instanceof NullPointerException);
    this.assertAnswered500WithOnlyTheFixedMessage(translated, original, "getPhase", "getId", "Cannot invoke",
      "org.cgiar");
  }

  private NewPublicationDTO newPublication() {
    PhaseDTO phaseDTO = new PhaseDTO();
    phaseDTO.setYear(2024);
    phaseDTO.setName("Reporting");
    NewPublicationDTO dto = new NewPublicationDTO();
    dto.setPhase(phaseDTO);
    dto.setTitle("A publication");
    dto.setDoi("10.1000/xyz123");
    dto.setAuthorList(Collections.emptyList());
    return dto;
  }

  private MetadataElement element(String encodedName) {
    MetadataElement element = new MetadataElement();
    element.setEcondedName(encodedName);
    return element;
  }

  private DeliverablesItem<Object> creatingItem(JsonElement wosAnswer, IOException wosFailure,
    DeliverableMetadataExternalSourcesManager externalSourcesManager) {
    GlobalUnitManager globalUnitManager =
      Fakes.fake(GlobalUnitManager.class).returning("findGlobalUnitByAcronym", this.globalUnit).build();
    PhaseManager phaseManager = Fakes.fake(PhaseManager.class).returning("findAll", Arrays.asList(this.phase)).build();
    DeliverableManager deliverableManager = Fakes.fake(DeliverableManager.class).on("saveDeliverable", args -> {
      this.deliverable.setId(DELIVERABLE_ID);
      return this.deliverable;
    }).returning("getDeliverableById", this.deliverable).build();
    DeliverableTypeManager typeManager =
      Fakes.fake(DeliverableTypeManager.class).returning("getDeliverableTypeById", new DeliverableType()).build();
    DeliverableInfoManager infoManager =
      Fakes.fake(DeliverableInfoManager.class).on("saveDeliverableInfo", args -> args[0]).build();
    DeliverableDisseminationManager disseminationManager =
      Fakes.fake(DeliverableDisseminationManager.class).on("saveDeliverableDissemination", args -> args[0]).build();
    DeliverablePublicationMetadataManager publicationMetadataManager = Fakes
      .fake(DeliverablePublicationMetadataManager.class).on("saveDeliverablePublicationMetadata", args -> args[0])
      .build();
    MetadataElementManager metadataElementManager = Fakes.fake(MetadataElementManager.class)
      .returning("findAll",
        Arrays.asList(this.element("marlo.handle"), this.element("marlo.doi"), this.element("dc.title"),
          this.element("dc.identifier.citation"), this.element("dc.date"), this.element("marlo.authors")))
      .build();
    DeliverableMetadataElementManager metadataElementValuesManager = Fakes
      .fake(DeliverableMetadataElementManager.class).on("saveDeliverableMetadataElement", args -> args[0]).build();
    DeliverableUserManager userManager = Fakes.fake(DeliverableUserManager.class).build();

    ItemWithFakeWos item = new ItemWithFakeWos(wosAnswer, wosFailure);
    RestErrorHarness.inject(item, "globalUnitManager", globalUnitManager);
    RestErrorHarness.inject(item, "phaseManager", phaseManager);
    RestErrorHarness.inject(item, "deliverableManager", deliverableManager);
    RestErrorHarness.inject(item, "deliverableTypeManager", typeManager);
    RestErrorHarness.inject(item, "deliverableInfoManager", infoManager);
    RestErrorHarness.inject(item, "deliverableDisseminationManager", disseminationManager);
    RestErrorHarness.inject(item, "deliverablePubMetadataManager", publicationMetadataManager);
    RestErrorHarness.inject(item, "metadataElementManager", metadataElementManager);
    RestErrorHarness.inject(item, "deliverableMetadataElementManager", metadataElementValuesManager);
    RestErrorHarness.inject(item, "deliverableUserManager", userManager);
    RestErrorHarness.inject(item, "deliverableMetadataExternalSourcesManager", externalSourcesManager);
    return item;
  }

  private void assertWosFallbackLogged() {
    List<ILoggingEvent> events = this.harness.loggedEvents();
    assertEquals("exactly one event: " + events, 1, events.size());
    ILoggingEvent event = events.get(0);
    assertEquals(Level.ERROR, event.getLevel());
    assertTrue(event.getFormattedMessage(), event.getFormattedMessage().contains(String.valueOf(DELIVERABLE_ID)));
    assertNotNull("the cause is kept in the log event", event.getThrowableProxy());
  }

  @Test
  public void aPublicationIsCreatedEvenWhenTheWosServiceFails() {
    DeliverablesItem<Object> item =
      this.creatingItem(null, new IOException("connection refused to http://clarisa.wos.api.mel.cgiar.org"), null);

    Long id = item.createDeliverable(this.newPublication(), ACRONYM, this.harness.user());

    assertEquals(Long.valueOf(DELIVERABLE_ID), id);
    this.assertWosFallbackLogged();
  }

  @Test
  public void aPublicationIsCreatedEvenWhenTheWosMetadataCannotBeStored() {
    DeliverableMetadataExternalSourcesManager failingStore =
      Fakes.fake(DeliverableMetadataExternalSourcesManager.class).on("saveDeliverableMetadataExternalSources", args -> {
        throw new IllegalStateException("the database is down");
      }).build();
    DeliverablesItem<Object> item =
      this.creatingItem(new JsonParser().parse("{\"doi\":\"10.1000/xyz123\"}"), null, failingStore);

    Long id = item.createDeliverable(this.newPublication(), ACRONYM, this.harness.user());

    assertEquals(Long.valueOf(DELIVERABLE_ID), id);
    this.assertWosFallbackLogged();
  }

  @Test
  public void aPublicationWithoutDoiDoesNotCallTheWosService() {
    NewPublicationDTO dto = this.newPublication();
    dto.setDoi(null);
    DeliverablesItem<Object> item = this.creatingItem(null, new IOException("must not be called"), null);

    Long id = item.createDeliverable(dto, ACRONYM, this.harness.user());

    assertEquals(Long.valueOf(DELIVERABLE_ID), id);
    this.harness.assertNothingLogged();
  }
}
