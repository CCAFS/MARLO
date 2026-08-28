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

package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.data.dao.HomepageBannerDAO;
import org.cgiar.ccafs.marlo.data.manager.HomepageBannerManager;
import org.cgiar.ccafs.marlo.data.model.HomepageBanner;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

@Named
public class HomepageBannerManagerImpl implements HomepageBannerManager {

  private HomepageBannerDAO homepageBannerDAO;

  @Inject
  public HomepageBannerManagerImpl(HomepageBannerDAO homepageBannerDAO) {
    this.homepageBannerDAO = homepageBannerDAO;
  }

  @Override
  public HomepageBanner findByGlobalUnit(long globalUnitID) {
    return homepageBannerDAO.findByGlobalUnit(globalUnitID);
  }

  @Override
  public HomepageBanner getHomepageBannerById(long homepageBannerID) {
    return homepageBannerDAO.find(homepageBannerID);
  }

  @Override
  @Transactional
  public HomepageBanner saveHomepageBanner(HomepageBanner homepageBanner) {
    return homepageBannerDAO.save(homepageBanner);
  }
}
