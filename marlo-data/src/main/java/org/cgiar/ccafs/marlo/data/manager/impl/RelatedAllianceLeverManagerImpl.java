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


import org.cgiar.ccafs.marlo.data.dao.RelatedAllianceLeverDAO;
import org.cgiar.ccafs.marlo.data.manager.RelatedAllianceLeverManager;
import org.cgiar.ccafs.marlo.data.model.RelatedAllianceLever;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RelatedAllianceLeverManagerImpl implements RelatedAllianceLeverManager {


  private RelatedAllianceLeverDAO relatedAllianceLeverDAO;
  // Managers


  @Inject
  public RelatedAllianceLeverManagerImpl(RelatedAllianceLeverDAO relatedAllianceLeverDAO) {
    this.relatedAllianceLeverDAO = relatedAllianceLeverDAO;


  }

  @Override
  public void deleteRelatedAllianceLever(long relatedAllianceLeverId) {

    relatedAllianceLeverDAO.deleteRelatedAllianceLever(relatedAllianceLeverId);
  }

  @Override
  public boolean existRelatedAllianceLever(long relatedAllianceLeverID) {

    return relatedAllianceLeverDAO.existRelatedAllianceLever(relatedAllianceLeverID);
  }

  @Override
  public List<RelatedAllianceLever> findAll() {

    return relatedAllianceLeverDAO.findAll();

  }

  @Override
  public List<RelatedAllianceLever> findAllByPhase(long phaseId) {

    return relatedAllianceLeverDAO.findAllByPhase(phaseId);

  }

  @Override
  public RelatedAllianceLever getRelatedAllianceLeverById(long relatedAllianceLeverID) {

    return relatedAllianceLeverDAO.find(relatedAllianceLeverID);
  }

  @Override
  public RelatedAllianceLever saveRelatedAllianceLever(RelatedAllianceLever relatedAllianceLever) {

    return relatedAllianceLeverDAO.save(relatedAllianceLever);
  }


}
