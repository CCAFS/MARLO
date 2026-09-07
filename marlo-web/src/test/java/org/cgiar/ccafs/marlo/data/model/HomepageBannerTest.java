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

package org.cgiar.ccafs.marlo.data.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * `isEmpty` is the rule two separate decisions lean on: the homepage renders no banner markup when it holds, and the
 * admin action declines to insert a row for a Global Unit whose first save carries nothing. Both of those depend on
 * whitespace-only input counting as empty, which is the part worth pinning.
 */
public class HomepageBannerTest {

  private HomepageBanner banner(String title, String description, String imageFileName) {
    HomepageBanner banner = new HomepageBanner();
    banner.setTitle(title);
    banner.setDescription(description);
    banner.setImageFileName(imageFileName);
    return banner;
  }

  @Test
  public void aFreshBannerIsEmpty() {
    assertTrue(new HomepageBanner().isEmpty());
  }

  @Test
  public void allThreeFieldsNullIsEmpty() {
    assertTrue(banner(null, null, null).isEmpty());
  }

  @Test
  public void whitespaceOnlyFieldsCountAsEmpty() {
    assertTrue(banner("   ", "\t\n ", "  ").isEmpty());
  }

  @Test
  public void aTitleAloneIsEnoughToBeNonEmpty() {
    assertFalse(banner("What is a Cluster?", null, null).isEmpty());
  }

  @Test
  public void aDescriptionAloneIsEnoughToBeNonEmpty() {
    assertFalse(banner(null, "A cluster is defined as...", null).isEmpty());
  }

  @Test
  public void anImageAloneIsEnoughToBeNonEmpty() {
    assertFalse(banner(null, null, "AICCRA.svg").isEmpty());
  }
}
