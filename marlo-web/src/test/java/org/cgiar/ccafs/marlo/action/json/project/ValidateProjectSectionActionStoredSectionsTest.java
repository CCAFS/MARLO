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

package org.cgiar.ccafs.marlo.action.json.project;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The validation result is stored only for the requested section, and only for the project sections whose answer is
 * computed like their page save.
 */
public class ValidateProjectSectionActionStoredSectionsTest {

  private static ValidateProjectSectionAction validating(String sectionName) {
    ValidateProjectSectionAction action =
      new ValidateProjectSectionAction(null, null, null, null, null, null, null, null, null);
    action.setSectionName(sectionName);
    return action;
  }

  @Test
  public void anotherSectionThanTheRequestedOneIsNotStored() {
    assertFalse(validating("partners").isSectionStatusStoredOnValidation("budgetByPartners"));
  }

  @Test
  public void sectionsOutsideTheListAreNotStored() {
    for (String section : new String[] {"deliverableList", "studies", "innovationsList", "budgetByPartners",
      "contributionsCrpList", "safeguards"}) {
      assertFalse(section, validating(section).isSectionStatusStoredOnValidation(section));
    }
  }

  @Test
  public void theListedProjectSectionsAreStored() {
    for (String section : new String[] {"description", "partners", "locations", "activities"}) {
      assertTrue(section, validating(section).isSectionStatusStoredOnValidation(section));
    }
  }

  @Test
  public void withoutASectionNothingIsStored() {
    assertFalse(validating(null).isSectionStatusStoredOnValidation(null));
  }
}
