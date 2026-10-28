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
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.policies.PolicyItem;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPolicyDTO;
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
 * ENH-LOGGING-STANDARDIZATION-001-T17: {@code Policies.createPolicy} used to log what its item threw and answer 200
 * with no identifier, which a client reads as a policy that was created.
 */
public class PoliciesErrorPropagationTest {

  private static final class StubItem extends PolicyItem<ProjectPolicyDTO> {

    private Long answer;
    private RuntimeException failure;
    private int calls;
    private NewProjectPolicyDTO receivedPolicy;
    private String receivedEntity;
    private User receivedUser;

    StubItem() {
      super(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null);
    }

    @Override
    public Long createPolicy(NewProjectPolicyDTO newPolicyDTO, String entityAcronym, User user) {
      this.calls++;
      this.receivedPolicy = newPolicyDTO;
      this.receivedEntity = entityAcronym;
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
  private Policies controller;

  @Before
  public void createController() {
    this.item = new StubItem();
    this.controller = new Policies(this.item, this.harness.userManager());
  }

  @Test
  public void aCreatedPolicyIsAnswered200WithTheIdentifierTheItemReturned() {
    NewProjectPolicyDTO policy = new NewProjectPolicyDTO();
    this.item.answer = 31L;

    ResponseEntity<Long> response = this.controller.createPolicy("AICCRA", policy);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Long.valueOf(31L), response.getBody());
    assertEquals(1, this.item.calls);
    assertSame(policy, this.item.receivedPolicy);
    assertEquals("AICCRA", this.item.receivedEntity);
    assertSame(this.harness.user(), this.item.receivedUser);
    this.harness.assertNothingLogged();
  }

  @Test
  public void anInvalidPolicyIsAnswered400WithTheFieldErrorsInsteadOfA200() {
    MARLOFieldValidationException failure =
      RestErrorHarness.fieldValidation("createPolicy", "phase", "2020 is an invalid year");
    this.item.failure = failure;

    Translated translated =
      this.harness.propagate(() -> this.controller.createPolicy("AICCRA", new NewProjectPolicyDTO()));

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
      this.harness.propagate(() -> this.controller.createPolicy("AICCRA", new NewProjectPolicyDTO()));

    assertSame(failure, translated.exception);
    assertEquals(500, translated.status);
    assertEquals(ErrorConstants.ERR_INTERNAL_SERVER, ((ErrorDTO) translated.body).getMessage());
    assertEquals(Level.ERROR, translated.loggedLevel);
  }

  @Test
  public void aFailureToResolveTheCurrentUserIsAnswered500AndNothingIsCreated() {
    RuntimeException failure = new RuntimeException("the user lookup failed");
    this.controller = new Policies(this.item, this.harness.userManagerFailingWith(failure));

    Translated translated =
      this.harness.propagate(() -> this.controller.createPolicy("AICCRA", new NewProjectPolicyDTO()));

    assertSame(failure, translated.exception);
    assertEquals(500, translated.status);
    assertEquals(0, this.item.calls);
  }
}
