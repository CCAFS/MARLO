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


import org.cgiar.ccafs.marlo.data.dao.RelatedAllianceLeverSdgContributionDAO;
import org.cgiar.ccafs.marlo.data.manager.RelatedAllianceLeverSdgContributionManager;
import org.cgiar.ccafs.marlo.data.model.RelatedAllianceLeverSdgContribution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RelatedAllianceLeverSdgContributionManagerImpl implements RelatedAllianceLeverSdgContributionManager {


  private RelatedAllianceLeverSdgContributionDAO relatedAllianceLeverSdgContributionDAO;
  // Managers


  @Inject
  public RelatedAllianceLeverSdgContributionManagerImpl(RelatedAllianceLeverSdgContributionDAO relatedAllianceLeverSdgContributionDAO) {
    this.relatedAllianceLeverSdgContributionDAO = relatedAllianceLeverSdgContributionDAO;


  }

  @Override
  public void deleteRelatedAllianceLeverSdgContribution(long relatedAllianceLeverSdgContributionId) {

    relatedAllianceLeverSdgContributionDAO.deleteRelatedAllianceLeverSdgContribution(relatedAllianceLeverSdgContributionId);
  }

  @Override
  public boolean existRelatedAllianceLeverSdgContribution(long relatedAllianceLeverSdgContributionID) {

    return relatedAllianceLeverSdgContributionDAO.existRelatedAllianceLeverSdgContribution(relatedAllianceLeverSdgContributionID);
  }

  @Override
  public List<RelatedAllianceLeverSdgContribution> findAll() {

    return relatedAllianceLeverSdgContributionDAO.findAll();

  }

  @Override
  public RelatedAllianceLeverSdgContribution getRelatedAllianceLeverSdgContributionById(long relatedAllianceLeverSdgContributionID) {

    return relatedAllianceLeverSdgContributionDAO.find(relatedAllianceLeverSdgContributionID);
  }

  @Override
  public RelatedAllianceLeverSdgContribution saveRelatedAllianceLeverSdgContribution(RelatedAllianceLeverSdgContribution relatedAllianceLeverSdgContribution) {

    return relatedAllianceLeverSdgContributionDAO.save(relatedAllianceLeverSdgContribution);
  }


}
