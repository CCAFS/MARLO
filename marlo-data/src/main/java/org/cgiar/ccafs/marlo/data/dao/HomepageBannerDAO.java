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

package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.HomepageBanner;

public interface HomepageBannerDAO {

  /**
   * Finds the banner of a Global Unit.
   *
   * @param globalUnitID the Global Unit identifier.
   * @return the banner, or null when that Global Unit has none.
   */
  public HomepageBanner findByGlobalUnit(long globalUnitID);

  /**
   * Finds a banner by its own identifier.
   *
   * @param homepageBannerID the banner identifier.
   * @return the banner, or null when it does not exist.
   */
  public HomepageBanner find(long homepageBannerID);

  /**
   * Inserts the banner when it has no identifier yet, updates it otherwise.
   *
   * @param homepageBanner the banner to persist.
   * @return the persisted banner.
   */
  public HomepageBanner save(HomepageBanner homepageBanner);
}
