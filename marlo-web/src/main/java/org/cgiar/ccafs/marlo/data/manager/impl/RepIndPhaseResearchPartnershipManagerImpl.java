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


import org.cgiar.ccafs.marlo.data.dao.RepIndPhaseResearchPartnershipDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndPhaseResearchPartnershipManager;
import org.cgiar.ccafs.marlo.data.model.RepIndPhaseResearchPartnership;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class RepIndPhaseResearchPartnershipManagerImpl implements RepIndPhaseResearchPartnershipManager {


  private RepIndPhaseResearchPartnershipDAO repIndPhaseResearchPartnershipDAO;
  // Managers


  @Inject
  public RepIndPhaseResearchPartnershipManagerImpl(RepIndPhaseResearchPartnershipDAO repIndPhaseResearchPartnershipDAO) {
    this.repIndPhaseResearchPartnershipDAO = repIndPhaseResearchPartnershipDAO;


  }

  @Override
  public void deleteRepIndPhaseResearchPartnership(long repIndPhaseResearchPartnershipId) {

    repIndPhaseResearchPartnershipDAO.deleteRepIndPhaseResearchPartnership(repIndPhaseResearchPartnershipId);
  }

  @Override
  public boolean existRepIndPhaseResearchPartnership(long repIndPhaseResearchPartnershipID) {

    return repIndPhaseResearchPartnershipDAO.existRepIndPhaseResearchPartnership(repIndPhaseResearchPartnershipID);
  }

  @Override
  public List<RepIndPhaseResearchPartnership> findAll() {

    return repIndPhaseResearchPartnershipDAO.findAll();

  }

  @Override
  public RepIndPhaseResearchPartnership getRepIndPhaseResearchPartnershipById(long repIndPhaseResearchPartnershipID) {

    return repIndPhaseResearchPartnershipDAO.find(repIndPhaseResearchPartnershipID);
  }

  @Override
  public RepIndPhaseResearchPartnership saveRepIndPhaseResearchPartnership(RepIndPhaseResearchPartnership repIndPhaseResearchPartnership) {

    return repIndPhaseResearchPartnershipDAO.save(repIndPhaseResearchPartnership);
  }


}
