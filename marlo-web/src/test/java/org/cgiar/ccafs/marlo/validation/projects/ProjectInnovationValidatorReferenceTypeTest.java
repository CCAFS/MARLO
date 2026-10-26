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

package org.cgiar.ccafs.marlo.validation.projects;

import org.cgiar.ccafs.marlo.data.model.DeliverableType;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The reference category check of an innovation: the same answer as the expression it replaces wherever that one did
 * not throw, and no exception for a reference without a type.
 */
public class ProjectInnovationValidatorReferenceTypeTest {

  private static DeliverableType type(DeliverableType category) {
    DeliverableType type = new DeliverableType();
    type.setId(Long.valueOf(10L));
    type.setDeliverableCategory(category);
    return type;
  }

  private static DeliverableType category(Long id) {
    DeliverableType category = new DeliverableType();
    category.setId(id);
    return category;
  }

  /** The expression used before, evaluated only for a reference that has a type. */
  private static boolean previousAnswer(DeliverableType type) {
    return type != null && type.getDeliverableCategory() == null
      || (type.getDeliverableCategory() != null && type.getDeliverableCategory().getId() == null
        || type.getDeliverableCategory().getId() == -1);
  }

  @Test
  public void aReferenceWithoutATypeIsLeftToTheTypeCheck() {
    assertFalse(ProjectInnovationValidator.isTypeWithoutCategory(null));
  }

  @Test
  public void aTypeWithACategoryIsComplete() {
    DeliverableType type = type(category(Long.valueOf(3L)));
    assertFalse(ProjectInnovationValidator.isTypeWithoutCategory(type));
    assertEquals(previousAnswer(type), ProjectInnovationValidator.isTypeWithoutCategory(type));
  }

  @Test
  public void aTypeWithoutACategoryIsReported() {
    DeliverableType type = type(null);
    assertTrue(ProjectInnovationValidator.isTypeWithoutCategory(type));
    assertEquals(previousAnswer(type), ProjectInnovationValidator.isTypeWithoutCategory(type));
  }

  @Test
  public void aCategoryWithoutAnIdIsReported() {
    DeliverableType type = type(category(null));
    assertTrue(ProjectInnovationValidator.isTypeWithoutCategory(type));
    assertEquals(previousAnswer(type), ProjectInnovationValidator.isTypeWithoutCategory(type));
  }

  @Test
  public void theUnselectedCategoryIsReported() {
    DeliverableType type = type(category(Long.valueOf(-1L)));
    assertTrue(ProjectInnovationValidator.isTypeWithoutCategory(type));
    assertEquals(previousAnswer(type), ProjectInnovationValidator.isTypeWithoutCategory(type));
  }
}
