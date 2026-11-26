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

import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.RestErrorHarness.Translated;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.expectedStudies.ExpectedStudiesOtherItem;
import org.cgiar.ccafs.marlo.rest.dto.MeliaARDTO;
import org.cgiar.ccafs.marlo.rest.dto.MeliaDTO;
import org.cgiar.ccafs.marlo.rest.errors.ErrorConstants;
import org.cgiar.ccafs.marlo.rest.errors.ErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;

import java.util.Arrays;
import java.util.List;

import ch.qos.logback.classic.Level;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * ENH-LOGGING-STANDARDIZATION-001-T17: two endpoints of {@code ExpectedStudiesOther} swallowed what their item threw.
 * The list endpoint answered an empty list, indistinguishable from "no MELIA studies"; the by-id endpoint answered
 * null (an empty 200), or the item's own NOT_FOUND answer with an empty body.
 */
public class ExpectedStudiesOtherErrorPropagationTest {

  private static final class StubItem extends ExpectedStudiesOtherItem<MeliaDTO> {

    private List<MeliaARDTO> list;
    private ResponseEntity<MeliaDTO> answer;
    private RuntimeException failure;
    private int calls;
    private Long receivedId;
    private String receivedEntity;
    private Integer receivedYear;
    private String receivedPhase;
    private User receivedUser;

    StubItem() {
      super(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    private void record(Long id, String entity, Integer year, String phase, User user) {
      this.calls++;
      this.receivedId = id;
      this.receivedEntity = entity;
      this.receivedYear = year;
      this.receivedPhase = phase;
      this.receivedUser = user;
      if (this.failure != null) {
        throw this.failure;
      }
    }

    @Override
    public List<MeliaARDTO> findAllMeliaByGlobalUnit(String CGIARentityAcronym, Integer repoYear, String repoPhase,
      User user) {
      this.record(null, CGIARentityAcronym, repoYear, repoPhase, user);
      return this.list;
    }

    @Override
    public ResponseEntity<MeliaDTO> findExpectedStudyById(Long id, String CGIARentityAcronym, Integer repoYear,
      String repoPhase, User user) {
      this.record(id, CGIARentityAcronym, repoYear, repoPhase, user);
      return this.answer;
    }
  }

  @Rule
  public RestErrorHarness harness = new RestErrorHarness();

  private StubItem item;
  private ExpectedStudiesOther controller;

  @Before
  public void createController() {
    this.item = new StubItem();
    this.controller = new ExpectedStudiesOther(this.item, this.harness.userManager());
    RestErrorHarness.inject(this.controller, "env",
      RestErrorHarness.environment("ExpectedStudies.MELIA.GET.id.404", "No such MELIA study"));
  }

  @Test
  public void theMeliaListIsReturnedExactlyAsTheItemAnsweredIt() {
    List<MeliaARDTO> studies = Arrays.asList(new MeliaARDTO(), new MeliaARDTO());
    this.item.list = studies;

    List<MeliaARDTO> response = this.controller.findAllMeliaByGlobalUnit("AICCRA", 2024, "Reporting");

    assertSame(studies, response);
    assertEquals(1, this.item.calls);
    assertEquals("AICCRA", this.item.receivedEntity);
    assertEquals(Integer.valueOf(2024), this.item.receivedYear);
    assertEquals("Reporting", this.item.receivedPhase);
    assertSame(this.harness.user(), this.item.receivedUser);
    this.harness.assertNothingLogged();
  }

  @Test
  public void anInvalidPhaseOrEntityOnTheListIsAnswered400InsteadOfAnEmptyList() {
    MARLOFieldValidationException failure =
      RestErrorHarness.fieldValidation("allMelia", "phase", "2020 is an invalid year");
    this.item.failure = failure;

    Translated translated =
      this.harness.propagate(() -> this.controller.findAllMeliaByGlobalUnit("AICCRA", 2020, "Reporting"));

    assertSame(failure, translated.exception);
    assertEquals(400, translated.status);
    assertSame(failure.getErrorDTO(), translated.body);
    assertEquals(Level.WARN, translated.loggedLevel);
  }

  @Test
  public void anUnexpectedFailureOnTheListIsAnswered500InsteadOfAnEmptyList() {
    RuntimeException failure = new RuntimeException("the item failed");
    this.item.failure = failure;

    Translated translated =
      this.harness.propagate(() -> this.controller.findAllMeliaByGlobalUnit("AICCRA", 2024, "Reporting"));

    assertSame(failure, translated.exception);
    assertEquals(500, translated.status);
    assertEquals(ErrorConstants.ERR_INTERNAL_SERVER, ((ErrorDTO) translated.body).getMessage());
    assertEquals(Level.ERROR, translated.loggedLevel);
  }

  @Test
  public void aKnownMeliaStudyIsReturnedExactlyAsTheItemAnsweredIt() {
    MeliaDTO study = new MeliaDTO();
    ResponseEntity<MeliaDTO> answer = new ResponseEntity<MeliaDTO>(study, HttpStatus.OK);
    this.item.answer = answer;

    ResponseEntity<MeliaDTO> response = this.controller.findExpectedStudyById("AICCRA", 7L, 2024, "Reporting");

    assertSame(answer, response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(study, response.getBody());
    assertEquals(1, this.item.calls);
    assertEquals(Long.valueOf(7L), this.item.receivedId);
    assertEquals("AICCRA", this.item.receivedEntity);
    assertEquals(Integer.valueOf(2024), this.item.receivedYear);
    assertEquals("Reporting", this.item.receivedPhase);
    assertSame(this.harness.user(), this.item.receivedUser);
    this.harness.assertNothingLogged();
  }

  @Test
  public void anInvalidPhaseOrIdentifierIsAnswered400WithTheFieldErrors() {
    MARLOFieldValidationException failure = RestErrorHarness.fieldValidation("findExpectedStudyOther",
      "ProjectExpectedStudyEntity", "9999 is an invalid Project Expected Study Code");
    this.item.failure = failure;

    Translated translated =
      this.harness.propagate(() -> this.controller.findExpectedStudyById("AICCRA", 9999L, 2024, "Reporting"));

    assertSame(failure, translated.exception);
    assertEquals(400, translated.status);
    assertSame(failure.getErrorDTO(), translated.body);
    assertEquals(Level.WARN, translated.loggedLevel);
  }

  @Test
  public void anUnknownMeliaStudyIsAnswered404WithTheConfiguredDescription() {
    this.item.answer = new ResponseEntity<MeliaDTO>(HttpStatus.NOT_FOUND);

    Translated translated =
      this.harness.propagate(() -> this.controller.findExpectedStudyById("AICCRA", 9999L, 2024, "Reporting"));

    assertTrue(translated.exception instanceof NotFoundException);
    assertEquals(404, translated.status);
    assertEquals("404 - No such MELIA study", ((ErrorDTO) translated.body).getMessage());
    assertEquals(Level.INFO, translated.loggedLevel);
  }

  @Test
  public void anUnexpectedFailureOnTheByIdEndpointIsAnswered500() {
    RuntimeException failure = new RuntimeException("the item failed");
    this.item.failure = failure;

    Translated translated =
      this.harness.propagate(() -> this.controller.findExpectedStudyById("AICCRA", 7L, 2024, "Reporting"));

    assertSame(failure, translated.exception);
    assertEquals(500, translated.status);
    assertEquals(Level.ERROR, translated.loggedLevel);
  }
}
