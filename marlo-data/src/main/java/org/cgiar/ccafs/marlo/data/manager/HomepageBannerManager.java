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

package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.model.HomepageBanner;

public interface HomepageBannerManager {

  /**
   * This method gets the homepage banner of a given Global Unit.
   *
   * @param globalUnitID is the Global Unit identifier.
   * @return the HomepageBanner object, null when that Global Unit has no banner yet.
   */
  public HomepageBanner findByGlobalUnit(long globalUnitID);

  /**
   * This method gets a homepage banner by its identifier.
   *
   * @param homepageBannerID is the banner identifier.
   * @return the HomepageBanner object, null when it does not exist.
   */
  public HomepageBanner getHomepageBannerById(long homepageBannerID);

  /**
   * This method saves the information of the given homepage banner. There is no phase replication: the banner is
   * Global Unit wide.
   *
   * @param homepageBanner is the banner object with the new information to be added/updated.
   * @return the persisted HomepageBanner object.
   */
  public HomepageBanner saveHomepageBanner(HomepageBanner homepageBanner);
}
