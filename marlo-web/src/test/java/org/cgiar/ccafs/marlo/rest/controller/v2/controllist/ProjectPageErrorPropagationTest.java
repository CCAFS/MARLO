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
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.projectPage.ProjectPageItem;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPageDTO;
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
 * ENH-LOGGING-STANDARDIZATION-001-T17: {@code ProjectPage.findProjectById} used to catch everything its item threw,
 * log it without a status and return null, so the client received an empty 200 for an unknown project, an entity
 * that does not publish project pages and a server fault alike.
 */
public class ProjectPageErrorPropagationTest {

  private static final class StubItem extends ProjectPageItem<ProjectPage> {

    private ResponseEntity<ProjectPageDTO> answer;
    private RuntimeException failure;
    private int calls;
    private Long receivedId;
    private String receivedEntity;

    StubItem() {
      super(null, null, null, null, null, null, null, null);
    }

    @Override
    public ResponseEntity<ProjectPageDTO> findProjectPageById(Long id, String globalUnitAcronym) {
      this.calls++;
      this.receivedId = id;
      this.receivedEntity = globalUnitAcronym;
      if (this.failure != null) {
        throw this.failure;
      }
      return this.answer;
    }
  }

  @Rule
  public RestErrorHarness harness = new RestErrorHarness();

  private StubItem item;
  private ProjectPage controller;

  @Before
  public void createController() {
    this.item = new StubItem();
    this.controller = new ProjectPage(this.item);
  }

  @Test
  public void aKnownProjectIsReturnedExactlyAsTheItemAnsweredIt() {
    ProjectPageDTO page = new ProjectPageDTO();
    ResponseEntity<ProjectPageDTO> answer = new ResponseEntity<ProjectPageDTO>(page, HttpStatus.OK);
    this.item.answer = answer;

    ResponseEntity<ProjectPageDTO> response = this.controller.findProjectById("AICCRA", 7L);

    assertSame(answer, response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(page, response.getBody());
    assertEquals(1, this.item.calls);
    assertEquals(Long.valueOf(7L), this.item.receivedId);
    assertEquals("AICCRA", this.item.receivedEntity);
    this.harness.assertNothingLogged();
  }

  @Test
  public void anUnknownProjectOrEntityIsAnswered400WithTheFieldErrors() {
    MARLOFieldValidationException failure =
      RestErrorHarness.fieldValidation("ProjectPageDTO", "Project", "Invalid Project code");
    this.item.failure = failure;

    Translated translated = this.harness.propagate(() -> this.controller.findProjectById("AICCRA", 9999L));

    assertSame(failure, translated.exception);
    assertEquals(400, translated.status);
    assertSame(failure.getErrorDTO(), translated.body);
    assertEquals(Level.WARN, translated.loggedLevel);
  }

  @Test
  public void aProjectPageWithoutDataIsAnswered404() {
    this.item.answer = new ResponseEntity<ProjectPageDTO>(HttpStatus.NOT_FOUND);

    Translated translated = this.harness.propagate(() -> this.controller.findProjectById("AICCRA", 7L));

    assertTrue(translated.exception instanceof NotFoundException);
    assertEquals(404, translated.status);
    assertEquals("404 - Not Found", ((ErrorDTO) translated.body).getMessage());
    assertEquals(Level.INFO, translated.loggedLevel);
  }

  @Test
  public void anUnexpectedFailureIsAnswered500() {
    RuntimeException failure = new RuntimeException("the item failed");
    this.item.failure = failure;

    Translated translated = this.harness.propagate(() -> this.controller.findProjectById("AICCRA", 7L));

    assertSame(failure, translated.exception);
    assertEquals(500, translated.status);
    assertEquals(ErrorConstants.ERR_INTERNAL_SERVER, ((ErrorDTO) translated.body).getMessage());
    assertEquals(Level.ERROR, translated.loggedLevel);
  }
}
