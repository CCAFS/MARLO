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
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.statusPlannedOutcomes.StatusPlannedOutcomesItem;
import org.cgiar.ccafs.marlo.rest.dto.StatusPlannedOutcomesDTO;
import org.cgiar.ccafs.marlo.rest.errors.ErrorConstants;
import org.cgiar.ccafs.marlo.rest.errors.ErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;

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
 * ENH-LOGGING-STANDARDIZATION-001-T17: {@code StatusPlannedOutcomes.findStatusPlannedOutcome} used to log what its
 * item threw and return null (an empty 200), or the item's own NOT_FOUND answer with an empty body.
 */
public class StatusPlannedOutcomesErrorPropagationTest {

  private static final class StubItem extends StatusPlannedOutcomesItem<StatusPlannedOutcomesDTO> {

    private ResponseEntity<StatusPlannedOutcomesDTO> answer;
    private RuntimeException failure;
    private int calls;
    private String receivedOutcome;
    private String receivedEntity;
    private Integer receivedYear;
    private String receivedPhase;
    private User receivedUser;

    StubItem() {
      super(null, null, null, null, null, null, null, null, null, null, null);
    }

    @Override
    public ResponseEntity<StatusPlannedOutcomesDTO> findStatusPlannedOutcome(String outcomeID,
      String CGIARentityAcronym, Integer repoYear, String repoPhase, User user) {
      this.calls++;
      this.receivedOutcome = outcomeID;
      this.receivedEntity = CGIARentityAcronym;
      this.receivedYear = repoYear;
      this.receivedPhase = repoPhase;
      this.receivedUser = user;
      if (this.failure != null) {
        throw this.failure;
      }
      return this.answer;
    }
  }

  @Rule
  public RestErrorHarness harness = new RestErrorHarness();

  private StubItem item;
  private StatusPlannedOutcomes controller;

  @Before
  public void createController() {
    this.item = new StubItem();
    this.controller = new StatusPlannedOutcomes(this.item, null, this.harness.userManager());
    RestErrorHarness.inject(this.controller, "env",
      RestErrorHarness.environment("StatusPlannedOutcomes.outcomes.GET.id.404", "No such planned outcome status"));
  }

  @Test
  public void aKnownOutcomeStatusIsReturnedExactlyAsTheItemAnsweredIt() {
    StatusPlannedOutcomesDTO outcome = new StatusPlannedOutcomesDTO();
    ResponseEntity<StatusPlannedOutcomesDTO> answer =
      new ResponseEntity<StatusPlannedOutcomesDTO>(outcome, HttpStatus.OK);
    this.item.answer = answer;

    ResponseEntity<StatusPlannedOutcomesDTO> response =
      this.controller.findStatusPlannedOutcome("AICCRA", "FP1-O1", 2024, "Reporting");

    assertSame(answer, response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(outcome, response.getBody());
    assertEquals(1, this.item.calls);
    assertEquals("FP1-O1", this.item.receivedOutcome);
    assertEquals("AICCRA", this.item.receivedEntity);
    assertEquals(Integer.valueOf(2024), this.item.receivedYear);
    assertEquals("Reporting", this.item.receivedPhase);
    assertSame(this.harness.user(), this.item.receivedUser);
    this.harness.assertNothingLogged();
  }

  @Test
  public void anInvalidPhaseEntityOrOutcomeIsAnswered400WithTheFieldErrors() {
    MARLOFieldValidationException failure = RestErrorHarness.fieldValidation("findStatusPlannedOutcomeById",
      "Outcome", "XX-O9 is an invalid CRP Outcome code");
    this.item.failure = failure;

    Translated translated =
      this.harness.propagate(() -> this.controller.findStatusPlannedOutcome("AICCRA", "XX-O9", 2024, "Reporting"));

    assertSame(failure, translated.exception);
    assertEquals(400, translated.status);
    assertSame(failure.getErrorDTO(), translated.body);
    assertEquals(Level.WARN, translated.loggedLevel);
  }

  @Test
  public void anOutcomeWithoutAStatusIsAnswered404WithTheConfiguredDescription() {
    this.item.answer = new ResponseEntity<StatusPlannedOutcomesDTO>(HttpStatus.NOT_FOUND);

    Translated translated =
      this.harness.propagate(() -> this.controller.findStatusPlannedOutcome("AICCRA", "FP1-O1", 2024, "Reporting"));

    assertTrue(translated.exception instanceof NotFoundException);
    assertEquals(404, translated.status);
    assertEquals("404 - No such planned outcome status", ((ErrorDTO) translated.body).getMessage());
    assertEquals(Level.INFO, translated.loggedLevel);
  }

  @Test
  public void aMissingAnswerFromTheItemIsAnswered404NotAnEmptyOk() {
    this.item.answer = null;

    Translated translated =
      this.harness.propagate(() -> this.controller.findStatusPlannedOutcome("AICCRA", "FP1-O1", 2024, "Reporting"));

    assertTrue(translated.exception instanceof NotFoundException);
    assertEquals(404, translated.status);
    assertEquals("404 - No such planned outcome status", ((ErrorDTO) translated.body).getMessage());
    assertEquals(Level.INFO, translated.loggedLevel);
  }

  @Test
  public void anUnexpectedFailureIsAnswered500() {
    RuntimeException failure = new RuntimeException("the item failed");
    this.item.failure = failure;

    Translated translated =
      this.harness.propagate(() -> this.controller.findStatusPlannedOutcome("AICCRA", "FP1-O1", 2024, "Reporting"));

    assertSame(failure, translated.exception);
    assertEquals(500, translated.status);
    assertEquals(ErrorConstants.ERR_INTERNAL_SERVER, ((ErrorDTO) translated.body).getMessage());
    assertEquals(Level.ERROR, translated.loggedLevel);
  }
}
