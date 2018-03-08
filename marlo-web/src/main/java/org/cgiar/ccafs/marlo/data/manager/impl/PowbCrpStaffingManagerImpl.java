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


import org.cgiar.ccafs.marlo.data.dao.PowbCrpStaffingDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbCrpStaffingManager;
import org.cgiar.ccafs.marlo.data.model.PowbCrpStaffing;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbCrpStaffingManagerImpl implements PowbCrpStaffingManager {


  private PowbCrpStaffingDAO powbCrpStaffingDAO;
  // Managers


  @Inject
  public PowbCrpStaffingManagerImpl(PowbCrpStaffingDAO powbCrpStaffingDAO) {
    this.powbCrpStaffingDAO = powbCrpStaffingDAO;


  }

  @Override
  public void deletePowbCrpStaffing(long powbCrpStaffingId) {

    powbCrpStaffingDAO.deletePowbCrpStaffing(powbCrpStaffingId);
  }

  @Override
  public boolean existPowbCrpStaffing(long powbCrpStaffingID) {

    return powbCrpStaffingDAO.existPowbCrpStaffing(powbCrpStaffingID);
  }

  @Override
  public List<PowbCrpStaffing> findAll() {

    return powbCrpStaffingDAO.findAll();

  }

  @Override
  public PowbCrpStaffing getPowbCrpStaffingById(long powbCrpStaffingID) {

    return powbCrpStaffingDAO.find(powbCrpStaffingID);
  }

  @Override
  public PowbCrpStaffing savePowbCrpStaffing(PowbCrpStaffing powbCrpStaffing) {

    return powbCrpStaffingDAO.save(powbCrpStaffing);
  }


}
