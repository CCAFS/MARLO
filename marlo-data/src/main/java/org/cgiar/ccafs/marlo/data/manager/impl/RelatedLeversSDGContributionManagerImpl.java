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


import org.cgiar.ccafs.marlo.data.dao.RelatedLeversSDGContributionDAO;
import org.cgiar.ccafs.marlo.data.manager.RelatedLeversSDGContributionManager;
import org.cgiar.ccafs.marlo.data.model.RelatedLeversSDGContribution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RelatedLeversSDGContributionManagerImpl implements RelatedLeversSDGContributionManager {


  private RelatedLeversSDGContributionDAO relatedLeversSDGContributionDAO;
  // Managers


  @Inject
  public RelatedLeversSDGContributionManagerImpl(RelatedLeversSDGContributionDAO relatedLeversSDGContributionDAO) {
    this.relatedLeversSDGContributionDAO = relatedLeversSDGContributionDAO;


  }

  @Override
  public void deleteRelatedLeversSDGContribution(long relatedLeversSDGContributionId) {

    relatedLeversSDGContributionDAO.deleteRelatedLeversSDGContribution(relatedLeversSDGContributionId);
  }

  @Override
  public boolean existRelatedLeversSDGContribution(long relatedLeversSDGContributionID) {

    return relatedLeversSDGContributionDAO.existRelatedLeversSDGContribution(relatedLeversSDGContributionID);
  }

  @Override
  public List<RelatedLeversSDGContribution> findAll() {

    return relatedLeversSDGContributionDAO.findAll();

  }

  @Override
  public RelatedLeversSDGContribution getRelatedLeversSDGContributionById(long relatedLeversSDGContributionID) {

    return relatedLeversSDGContributionDAO.find(relatedLeversSDGContributionID);
  }

  @Override
  public RelatedLeversSDGContribution saveRelatedLeversSDGContribution(RelatedLeversSDGContribution relatedLeversSDGContribution) {

    return relatedLeversSDGContributionDAO.save(relatedLeversSDGContribution);
  }


}
