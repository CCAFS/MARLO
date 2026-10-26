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

package org.cgiar.ccafs.marlo.action.projects;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * The partner request page is opened with the id of the item it was requested from. Some links build that id with a
 * FreeMarker default, so it can arrive blank, and a crawler sends anything; neither may fail the page.
 */
public class PartnersSaveActionRequestIdTest {

  @Test
  public void aNumberIsTheId() {
    assertEquals(102082, PartnersSaveAction.readRequestId("102082"));
    assertEquals(102082, PartnersSaveAction.readRequestId(" 102082 "));
  }

  @Test
  public void aMissingOrBlankIdIsZero() {
    assertEquals(0, PartnersSaveAction.readRequestId(null));
    assertEquals(0, PartnersSaveAction.readRequestId(""));
    assertEquals(0, PartnersSaveAction.readRequestId("   "));
  }

  @Test
  public void aValueThatIsNotAnIdIsZero() {
    assertEquals(0, PartnersSaveAction.readRequestId("abc"));
    assertEquals(0, PartnersSaveAction.readRequestId("${900964452+919539869}"));
    assertEquals(0, PartnersSaveAction.readRequestId("99999999999"));
  }
}
