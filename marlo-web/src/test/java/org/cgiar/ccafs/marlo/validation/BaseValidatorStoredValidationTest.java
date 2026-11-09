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

package org.cgiar.ccafs.marlo.validation;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * A validation that runs from an action that only validates stores the result of an existing section status on its
 * own; a page save, a new status or a section left out keep the behaviour they had.
 */
public class BaseValidatorStoredValidationTest {

  private static final String SECTION = "description";

  private final BaseValidator validator = new BaseValidator();

  /** Calls to updateMissingFieldsInOwnSession, as "id=missingFields". */
  private final List<String> storedApart = new ArrayList<>();

  private SectionStatus foundStatus;

  private boolean storedOnValidation;

  private boolean failToStore;

  private BaseAction action;

  private static Project project() {
    Project project = new Project();
    project.setId(Long.valueOf(102079L));
    return project;
  }

  private static SectionStatus existingStatus(String missingFields) {
    SectionStatus status = new SectionStatus();
    status.setId(Long.valueOf(113515L));
    status.setMissingFields(missingFields);
    return status;
  }

  @Before
  public void setUp() throws ReflectiveOperationException {
    SectionStatusManager manager = (SectionStatusManager) Proxy.newProxyInstance(
      SectionStatusManager.class.getClassLoader(), new Class<?>[] {SectionStatusManager.class},
      (proxy, method, args) -> {
        switch (method.getName()) {
          case "getSectionStatusByProject":
            return this.foundStatus;
          case "saveSectionStatus":
            return args[0];
          case "updateMissingFieldsInOwnSession":
            if (this.failToStore) {
              throw new IllegalStateException("lock wait timeout");
            }
            this.storedApart.add(args[0] + "=" + args[1]);
            return Integer.valueOf(1);
          default:
            throw new UnsupportedOperationException(method.getName());
        }
      });
    Field field = BaseValidator.class.getDeclaredField("sectionStatusManager");
    field.setAccessible(true);
    field.set(this.validator, manager);

    this.action = new BaseAction() {

      private static final long serialVersionUID = 1L;

      @Override
      public boolean isSectionStatusStoredOnValidation(String sectionName) {
        return BaseValidatorStoredValidationTest.this.storedOnValidation && SECTION.equals(sectionName);
      }
    };
  }

  private void validateWith(String missingFields) {
    this.action.getMissingFields().setLength(0);
    this.action.getMissingFields().append(missingFields);
    this.validator.saveMissingFields(project(), "Planning", 2026, Boolean.FALSE, SECTION, this.action);
  }

  @Test
  public void aFailureToStoreLeavesTheValidationAnswerAlone() {
    this.foundStatus = existingStatus("Lessons Learned");
    this.storedOnValidation = true;
    this.failToStore = true;

    this.validateWith("");

    assertEquals("the answer still carries the computed result", "", this.foundStatus.getMissingFields());
  }

  @Test
  public void aNewStatusIsNotStoredApart() {
    this.foundStatus = null;
    this.storedOnValidation = true;

    this.validateWith("Cluster Summary");

    assertTrue(this.storedApart.isEmpty());
  }

  @Test
  public void aPageSaveDoesNotStoreApart() {
    this.foundStatus = existingStatus("Lessons Learned");
    this.storedOnValidation = false;

    this.validateWith("");

    assertTrue(this.storedApart.isEmpty());
  }

  @Test
  public void theResultOfAnExistingStatusIsStoredWhenTheActionAsksForIt() {
    this.foundStatus = existingStatus("Lessons Learned");
    this.storedOnValidation = true;

    this.validateWith("");

    assertEquals(1, this.storedApart.size());
    assertEquals("113515=", this.storedApart.get(0));
  }

  @Test
  public void aDraftAloneIsStoredAsComplete() {
    // The autosave of the page validates after every draft: the stored status must not turn red for every user
    this.foundStatus = existingStatus("");
    this.storedOnValidation = true;

    this.validateWith("draft");

    assertEquals("113515=", this.storedApart.get(0));
    assertEquals("the answer still tells the editor about the draft", "draft", this.foundStatus.getMissingFields());
  }

  @Test
  public void theDraftMarkerIsRemovedFromTheOtherMissingFields() {
    this.foundStatus = existingStatus("");
    this.storedOnValidation = true;

    this.validateWith("draft;Lessons Learned;Cluster Type");

    assertEquals("113515=Lessons Learned;Cluster Type", this.storedApart.get(0));
  }

  @Test
  public void withoutDraftMarkerKeepsEverythingElse() {
    assertEquals(null, BaseValidator.withoutDraftMarker(null));
    assertEquals("", BaseValidator.withoutDraftMarker(""));
    assertEquals("A;B", BaseValidator.withoutDraftMarker("A;draft;B"));
    assertEquals("draft text;drafts", BaseValidator.withoutDraftMarker("draft text;drafts"));
  }
}
