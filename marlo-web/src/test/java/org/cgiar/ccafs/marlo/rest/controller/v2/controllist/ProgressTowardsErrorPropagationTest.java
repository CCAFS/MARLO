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
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.progressTowards.ProgressTowardsItem;
import org.cgiar.ccafs.marlo.rest.dto.NewSrfProgressTowardsTargetDTO;
import org.cgiar.ccafs.marlo.rest.dto.SrfProgressTowardsTargetDTO;
import org.cgiar.ccafs.marlo.rest.errors.ErrorConstants;
import org.cgiar.ccafs.marlo.rest.errors.ErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;

import ch.qos.logback.classic.Level;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * ENH-LOGGING-STANDARDIZATION-001-T17: {@code ProgressTowards.createProgressTowards} used to log what its item threw
 * and answer 200 with no identifier, which a client reads as a progress record that was created.
 */
public class ProgressTowardsErrorPropagationTest {

  private static final class StubItem extends ProgressTowardsItem<SrfProgressTowardsTargetDTO> {

    private Long answer;
    private RuntimeException failure;
    private int calls;
    private NewSrfProgressTowardsTargetDTO receivedTarget;
    private String receivedEntity;
    private User receivedUser;

    StubItem() {
      super(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Override
    public Long createProgressTowards(NewSrfProgressTowardsTargetDTO newSrfProgressTowardsTargetDTO,
      String CGIARentityAcronym, User user) {
      this.calls++;
      this.receivedTarget = newSrfProgressTowardsTargetDTO;
      this.receivedEntity = CGIARentityAcronym;
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
  private ProgressTowards controller;

  @Before
  public void createController() {
    this.item = new StubItem();
    this.controller = new ProgressTowards(this.item, this.harness.userManager());
  }

  @Test
  public void aCreatedRecordIsAnswered200WithTheIdentifierTheItemReturned() {
    NewSrfProgressTowardsTargetDTO target = new NewSrfProgressTowardsTargetDTO();
    this.item.answer = 31L;

    ResponseEntity<Long> response = this.controller.createProgressTowards("AICCRA", target);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Long.valueOf(31L), response.getBody());
    assertEquals(1, this.item.calls);
    assertSame(target, this.item.receivedTarget);
    assertEquals("AICCRA", this.item.receivedEntity);
    assertSame(this.harness.user(), this.item.receivedUser);
    this.harness.assertNothingLogged();
  }

  @Test
  public void anInvalidRecordIsAnswered400WithTheFieldErrorsInsteadOfA200() {
    MARLOFieldValidationException failure =
      RestErrorHarness.fieldValidation("createProgressTowards", "phase", "2020 is an invalid year");
    this.item.failure = failure;

    Translated translated =
      this.harness
        .propagate(() -> this.controller.createProgressTowards("AICCRA", new NewSrfProgressTowardsTargetDTO()));

    assertSame(failure, translated.exception);
    assertEquals(400, translated.status);
    assertSame(failure.getErrorDTO(), translated.body);
    assertEquals(Level.WARN, translated.loggedLevel);
  }

  @Test
  public void anUnexpectedFailureIsAnswered500InsteadOfA200() {
    RuntimeException failure = new RuntimeException("the item failed");
    this.item.failure = failure;

    Translated translated =
      this.harness
        .propagate(() -> this.controller.createProgressTowards("AICCRA", new NewSrfProgressTowardsTargetDTO()));

    assertSame(failure, translated.exception);
    assertEquals(500, translated.status);
    assertEquals(ErrorConstants.ERR_INTERNAL_SERVER, ((ErrorDTO) translated.body).getMessage());
    assertEquals(Level.ERROR, translated.loggedLevel);
  }
}
