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


import org.cgiar.ccafs.marlo.data.dao.RepIndContributionOfCrpDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndContributionOfCrpManager;
import org.cgiar.ccafs.marlo.data.model.RepIndContributionOfCrp;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RepIndContributionOfCrpManagerImpl implements RepIndContributionOfCrpManager {


  private RepIndContributionOfCrpDAO repIndContributionOfCrpDAO;
  // Managers


  @Inject
  public RepIndContributionOfCrpManagerImpl(RepIndContributionOfCrpDAO repIndContributionOfCrpDAO) {
    this.repIndContributionOfCrpDAO = repIndContributionOfCrpDAO;


  }

  @Override
  public void deleteRepIndContributionOfCrp(long repIndContributionOfCrpId) {

    repIndContributionOfCrpDAO.deleteRepIndContributionOfCrp(repIndContributionOfCrpId);
  }

  @Override
  public boolean existRepIndContributionOfCrp(long repIndContributionOfCrpID) {

    return repIndContributionOfCrpDAO.existRepIndContributionOfCrp(repIndContributionOfCrpID);
  }

  @Override
  public List<RepIndContributionOfCrp> findAll() {

    return repIndContributionOfCrpDAO.findAll();

  }

  @Override
  public RepIndContributionOfCrp getRepIndContributionOfCrpById(long repIndContributionOfCrpID) {

    return repIndContributionOfCrpDAO.find(repIndContributionOfCrpID);
  }

  @Override
  public RepIndContributionOfCrp saveRepIndContributionOfCrp(RepIndContributionOfCrp repIndContributionOfCrp) {

    return repIndContributionOfCrpDAO.save(repIndContributionOfCrp);
  }


}
