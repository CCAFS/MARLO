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


import org.cgiar.ccafs.marlo.data.dao.OtherContributionDAO;
import org.cgiar.ccafs.marlo.data.manager.OtherContributionManager;
import org.cgiar.ccafs.marlo.data.model.OtherContribution;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class OtherContributionManagerImpl implements OtherContributionManager {


  private OtherContributionDAO otherContributionDAO;
  // Managers


  @Inject
  public OtherContributionManagerImpl(OtherContributionDAO otherContributionDAO) {
    this.otherContributionDAO = otherContributionDAO;


  }

  @Override
  public boolean deleteOtherContribution(long otherContributionId) {

    return otherContributionDAO.deleteOtherContribution(otherContributionId);
  }

  @Override
  public boolean existOtherContribution(long otherContributionID) {

    return otherContributionDAO.existOtherContribution(otherContributionID);
  }

  @Override
  public List<OtherContribution> findAll() {

    return otherContributionDAO.findAll();

  }

  @Override
  public OtherContribution getOtherContributionById(long otherContributionID) {

    return otherContributionDAO.find(otherContributionID);
  }

  @Override
  public long saveOtherContribution(OtherContribution otherContribution) {

    return otherContributionDAO.save(otherContribution);
  }


}
