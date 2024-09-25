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


import org.cgiar.ccafs.marlo.data.dao.AllianceLeversSdgContributionDAO;
import org.cgiar.ccafs.marlo.data.manager.AllianceLeversSdgContributionManager;
import org.cgiar.ccafs.marlo.data.model.AllianceLeversSdgContribution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class AllianceLeversSdgContributionManagerImpl implements AllianceLeversSdgContributionManager {


  private AllianceLeversSdgContributionDAO allianceLeversSdgContributionDAO;
  // Managers


  @Inject
  public AllianceLeversSdgContributionManagerImpl(AllianceLeversSdgContributionDAO allianceLeversSdgContributionDAO) {
    this.allianceLeversSdgContributionDAO = allianceLeversSdgContributionDAO;


  }

  @Override
  public void deleteAllianceLeversSdgContribution(long allianceLeversSdgContributionId) {

    allianceLeversSdgContributionDAO.deleteAllianceLeversSdgContribution(allianceLeversSdgContributionId);
  }

  @Override
  public boolean existAllianceLeversSdgContribution(long allianceLeversSdgContributionID) {

    return allianceLeversSdgContributionDAO.existAllianceLeversSdgContribution(allianceLeversSdgContributionID);
  }

  @Override
  public List<AllianceLeversSdgContribution> findAll() {

    return allianceLeversSdgContributionDAO.findAll();

  }


  @Override
  public List<AllianceLeversSdgContribution> findAllByLeverId(long leverId) {

    return allianceLeversSdgContributionDAO.findAllByLeverId(leverId);

  }

  @Override
  public AllianceLeversSdgContribution getAllianceLeversSdgContributionById(long allianceLeversSdgContributionID) {

    return allianceLeversSdgContributionDAO.find(allianceLeversSdgContributionID);
  }

  @Override
  public AllianceLeversSdgContribution
    saveAllianceLeversSdgContribution(AllianceLeversSdgContribution allianceLeversSdgContribution) {

    return allianceLeversSdgContributionDAO.save(allianceLeversSdgContribution);
  }


}
