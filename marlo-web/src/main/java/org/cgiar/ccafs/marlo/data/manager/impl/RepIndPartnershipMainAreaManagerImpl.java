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


import org.cgiar.ccafs.marlo.data.dao.RepIndPartnershipMainAreaDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndPartnershipMainAreaManager;
import org.cgiar.ccafs.marlo.data.model.RepIndPartnershipMainArea;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RepIndPartnershipMainAreaManagerImpl implements RepIndPartnershipMainAreaManager {


  private RepIndPartnershipMainAreaDAO repIndPartnershipMainAreaDAO;
  // Managers


  @Inject
  public RepIndPartnershipMainAreaManagerImpl(RepIndPartnershipMainAreaDAO repIndPartnershipMainAreaDAO) {
    this.repIndPartnershipMainAreaDAO = repIndPartnershipMainAreaDAO;


  }

  @Override
  public void deleteRepIndPartnershipMainArea(long repIndPartnershipMainAreaId) {

    repIndPartnershipMainAreaDAO.deleteRepIndPartnershipMainArea(repIndPartnershipMainAreaId);
  }

  @Override
  public boolean existRepIndPartnershipMainArea(long repIndPartnershipMainAreaID) {

    return repIndPartnershipMainAreaDAO.existRepIndPartnershipMainArea(repIndPartnershipMainAreaID);
  }

  @Override
  public List<RepIndPartnershipMainArea> findAll() {

    return repIndPartnershipMainAreaDAO.findAll();

  }

  @Override
  public RepIndPartnershipMainArea getRepIndPartnershipMainAreaById(long repIndPartnershipMainAreaID) {

    return repIndPartnershipMainAreaDAO.find(repIndPartnershipMainAreaID);
  }

  @Override
  public RepIndPartnershipMainArea saveRepIndPartnershipMainArea(RepIndPartnershipMainArea repIndPartnershipMainArea) {

    return repIndPartnershipMainAreaDAO.save(repIndPartnershipMainArea);
  }


}
